package org.codelab.batch.job.partitioner;

import org.apache.ibatis.session.SqlSessionFactory;
import org.codelab.batch.common.Const;
import org.codelab.batch.dto.Person;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.step.builder.PartitionStepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class PartitionerJobConfig {

    //private static final Logger log = LoggerFactory.getLogger(PartitionerJobConfig.class);
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    /**
     *
     */
    private static final int poolSize = 2;   // 쓰레드 갯수
    private static final int gridSize = 2;   // grid 갯수 : page 사이즈를 정함 partition() : int pageSize = (total / gridSize);

    /**
     * Chunk란 데이터 덩어리로 작업 할 때 각 커밋 사이에 처리되는 row 수
     * Chunk 지향 처리란 한 번에 하나씩 데이터를 읽어 Chunk라는 덩어리를 만든 뒤, Chunk 단위로 트랜잭션을 다루는 것을 의미
     *
     * 1.Reader에서 데이터를 하나 읽어옵니다
     * 2.읽어온 데이터를 Processor에서 가공합니다
     * 3.가공된 데이터들을 별도의 공간에 모은 뒤, Chunk 단위만큼 쌓이게 되면 Writer에 전달하고 Writer는 일괄 저장합니다.
     * => Reader와 Processor에서는 1건씩 다뤄지고, Writer에선 Chunk 단위로 처리된다는 것만 기억하시면 됩니다.
     *
     * => 단, 실제로는 그럴일 없겠지만 ...
     *   한번에 가져오는 row수인 pagesize가 5이고 chunksize가 10이면 pagesize의 단위로 write를 함.( chunksize > pagesize  = pagesize를 기준으로 write)
     *
     * page size : 한번에 조회할 데이터 row 수
     * page size는 girdsize의 값으로 자동 세팅됨.
     * 예를 들어 총 데이터가 1000건, gridsize = 10이면 pagesize = 1000/10 = 100. 즉 한번에 100건의 row씩 가져옴.
     *  => 일종의 fetch개념인듯.
     *  : partition() : int pageSize = (total / gridSize);
     */
    private static final int chunkSize = 10;


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Bean(name = Const.JOB_PARTITIONER)
    public Job partitionJob(){

        logger.info("---------- start partitionJob ---------- ");

        return jobBuilderFactory.get(Const.JOB_PARTITIONER)
                .incrementer(new RunIdIncrementer())
                .start(masterStep()).build();
    }


    /**
     * 파티셔닝 (Partitioning) (Single or Multi process / Local or Remote)
     * 매니저 (마스터)를 이용해 데이터를 더 작은 Chunk (파티션이라고 함)로 나눈 다음 파티션에서 슬레이브가 독립적으로 작동하는 방식
     * 슬레이브가 로컬과 원격을 모두 지원하여 확장된 JVM 환경에서의 실행을 해볼 수 있음
     * 원격 슬레이브와 통신하기 위해 다양한 통신 메커니즘을 지원
     *
     * 저희가 멀티쓰레드 Step 나 파티셔닝 (Partitioning)와 같은 Spring Batch의 Scalling 기능을 사용하는 이유는, 기존의 코드 변경 없이 성능을 향상 시키기 위함입니다.
     * 위에서 언급한대로 completablefuture 나 @Async 를 기존 Spring Batch에 사용하기 위해서는 일정 부분 혹은 아주 많은 부분의 코드 변경이 필수인 반면, Spring Batch의 Scalling 기능들은 기존 코드 변경이 거의 없습니다.
     * 다양한 Scalling 기능을 기존의 스프링 배치 코드 변경 없이, 그리고 많은 레퍼런스로 인해 안정적으로 구현이 가능한 기능들이기 때문에 대량의 데이터 처리가 필요한 상황을 대비하여 숙지하고 있어야 한다고 봅니다.
     * 
     * https://jojoldu.tistory.com/550 참고
     */
    @Bean
    Step masterStep(){

        logger.info("---------- start masterStep ---------- ");

        /**
         * 1. partitioner 에서 데이터를 나누고 각 스텝에 나눈 데이터를 분배(partitioner 라는 스레드가 데이터를 나눠서 각 스텝에 나눠줌.)
         * 2. 스텝(여기서 slaveStep) 에서는 분배받은 데이터를 가지고
         *  -- reader()
         *  -- processor()
         *  -- writer()
         *   프로그램을 수행
         *
         * rangePartitioner() : select 쿼리 id 추출
         */
        return stepBuilderFactory.get("masterStep")
                .partitioner(slaveStep().getName(), rangePartitioner())
                .partitionHandler(masterSlaveHandler()).build();

    }



    @Bean
    public Step slaveStep() {
        logger.info("---------- start slaveStep ---------- ");

        /**
         * Chunk란 데이터 덩어리로 작업 할 때 각 커밋 사이에 처리되는 row 수
         * Chunk 지향 처리란 한 번에 하나씩 데이터를 읽어 Chunk라는 덩어리를 만든 뒤, Chunk 단위로 트랜잭션을 다루는 것을 의미
         *
         * 1.Reader에서 데이터를 하나 읽어옵니다
         * 2.읽어온 데이터를 Processor에서 가공합니다
         * 3.가공된 데이터들을 별도의 공간에 모은 뒤, Chunk 단위만큼 쌓이게 되면 Writer에 전달하고 Writer는 일괄 저장합니다.
         * => Reader와 Processor에서는 1건씩 다뤄지고, Writer에선 Chunk 단위로 처리된다는 것만 기억하시면 됩니다.
         *
         * page size : 한번에 조회할 데이터 row 수
         */
        return stepBuilderFactory.get("slaveStep").<Person,Person>chunk(chunkSize)
                .reader(slaveReader(0,0,null))
                .processor(slaveProcessor(null))
                .writer(slaveWriter(null))
                .build();
    }


    @Bean
    public PartitionHandler masterSlaveHandler() {
        logger.info("---------- start masterSlaveHandler ---------- ");

        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(gridSize);
        handler.setTaskExecutor(taskExecutor());
        handler.setStep(slaveStep());
        try {
            handler.afterPropertiesSet();
        } catch(Exception e) {
            e.printStackTrace();
        }

        logger.info("---------- end masterSlaveHandler ---------- ");
        return handler;
    }


    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        /**
         * SimpleAsyncTaskExecutor()는 쓰레드를 무한대로 생성하므로 절대 사용하지 말 것(실제 운영 환경에서는 대형 장애를 발생)
         * 쓰레드풀 내에서 지정된 갯수만큼의 쓰레드만 생성할 수 있도록 ThreadPoolTaskExecutor를 사용
         */
        //return new SimpleAsyncTaskExecutor();

        logger.info("---------- start taskExecutor ---------- ");



        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("my-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE); // true면 진행 중이던 작업이 완료된 후 Thread를 종료한다.
        executor.initialize();

        logger.info("---------- end taskExecutor ---------- ");

        return executor;


    }


    @Bean
    @StepScope
    public MyBatisPagingItemReader<Person> slaveReader(
            @Value("#{stepExecutionContext[skipRows]}") final int skipRows,
            @Value("#{stepExecutionContext[pageSize]}") final int pageSize,
            @Value("#{stepExecutionContext[name]}") final String name) {


        logger.info("--------- start slaveReader -------------{}, {}, {}", name, skipRows, pageSize);
        MyBatisPagingItemReader<Person> reader = new MyBatisPagingItemReader<>();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("getPersonPaging");
        reader.setCurrentItemCount(skipRows);
        reader.setMaxItemCount(skipRows+pageSize);
        reader.setPageSize(pageSize);

        logger.info("---------- end slaveReader ---------- ");

        return reader;
    }

    @Bean
    @StepScope
    public PartitionerProcessor slaveProcessor(@Value("#{stepExecutionContext[name]}") String name) {

        logger.info("--------- start slaveProcessor ------------- {}", name);
        PartitionerProcessor processor = new PartitionerProcessor();
        processor.setThreadName(name);

        logger.info("---------- end slaveProcessor ---------- ");

        return processor;
    }

    @Bean
    @StepScope
    public PartitionerWriter slaveWriter(@Value("#{stepExecutionContext[name]}") String name) {
        logger.info("--------- start slaveWriter ------------- {}", name);
        PartitionerWriter writer = new PartitionerWriter();
        writer.setThreadName(name);
        logger.info("--------- end slaveWriter ------------- {}", name);

        // FILE WRITE 추가





        return writer;
    }


    @Bean
    public RangePartitioner rangePartitioner() {
        logger.info("--------- start rangePartitioner ------------- {}");
        return new RangePartitioner(sqlSessionTemplate, "countPerson");
    }


}
