package com.example.allinonebatch.job.Jobtasklet1;

import com.example.allinonebatch.common.Const;
import com.example.allinonebatch.dto.Person;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;

import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * process() :  javax.batch.api.chunk.ItemProcessor 이거 import하면 망함. ItemProcessor 인터페이스 정의부가 다름
 * import org.springframework.batch.item.ItemProcessor; <- 이거 사용할것
 */
//import javax.batch.api.chunk.ItemProcessor;


/**
 * 실행방법 :
 *      Edit Configuration -> program argument : --job.name=jobchunk1 ver=1 date=20220723
 *
 *  문제점 :
 *     테스트 소스이므로 person 테이블을 읽어 person 테이블에 write하기 때문에 같은 트랜잭션으로 묶여 reader가 계속 호출됨.
 *     processor()에서 firstname을 가공하므로 insert시 firstname(20) 사이즈 초과로 오류남. 걍 테스트니 넘어갈 것
 *
 *
 *
 *   !!중요!!
 *   jobchunk1_Step() 밑의 하위 메서드들의 이름을 다른 잡에서 중복해서 사용하려면 @Bean으로 등록하면 안됨.
 *   @Bean으로 등록시 defined in class path resource could not be registered 오류 발생함.
 *   private으로 선언하면 private or final 은 선언불가 라고 오류 떨어짐.
 *
 *   같은 메소드 이름을 다른 잡에서 중복해서 사용하고 싶으면 @Bean 삭제.(실행하는데 문제없음)
 */
@Slf4j
@Configuration
public class Jobchunk1Config {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    private final int SKIP_CHUNK_PAGE_SIZE = 3;



    /**
     * Job에서 @StepScope로 설정하면 Scope 'step' is not active for the current thread; 오류발생함.
     * @JobScope는 각 step에서 설정
     * @StepScope는 각 step에서 호출하는 하위메소드에서 설정해서 파라미터를 받아야 함.
     */
    @Bean(name = Const.JOBCHUNK1)
    public Job Jobchunk1_job() throws Exception{

        log.info("Jobchunk1_job start!!");

        return jobBuilderFactory.get(Const.JOBCHUNK1)
                .incrementer(new RunIdIncrementer())	// RunIdIncrementer : 동일파라미터라도 재실행이 가능
                .start(jobchunk1_Step(null,null))// jobParameter을 받기 위해 null로 던짐. 파라미터 갯수만큼 던지면 됨.
                .build();
    }

    @Bean
    @JobScope   // job parameter 획득하기 위해 설정
    public Step jobchunk1_Step(@Value("#{jobParameters[ver]}") String ver, @Value("#{jobParameters[date]}") String date) throws Exception {

        log.info("jobchunk1_Step start!!");
        log.info("ver = " + ver);
        log.info("date = " + date);

        return stepBuilderFactory.get("jobchunk1_Step")
                .<Person,Person>chunk(SKIP_CHUNK_PAGE_SIZE)
                //.faultTolerant().noRollback(this.getClass()) // 실패해도 롤백처리하지 않음. 사용할 일 별로 없을듯
                .reader(reader_page(null,null))  // paging 기반 read : MyBatisPagingItemReaderBuilder 사용
                //.reader(reader_cursor())  // cursor 기반 read : MyBatisCursorItemReaderBuilder 사용
                .processor(processor())
                //.writer(writer_print()) // 로그출력용
                .writer(writerToDB())             // MyBatisBatchItemWriterBuilder사용
                //.writer(writerToDbByItemWriter())   //  MyBatisBatchItemWriter 사용
                //.writer(writerTOFlatFile())

                //.transactionManager(new DataSourceTransactionManager()) // 호출안해도 됨. 트랜잭션 관리 확인용
                .build();
    }

    /**
     * paging 기반 reader.
     * page 단위로 처리하므로 커넥션을 독점하지 않고 사용가능( 대용량 처리에 유리 )
     */

    //@Bean
    @StepScope // job parameter를 얻기 위해 설정(step의 하위 메소드에서 설정해야함. 스텝에 설정하면 안됨)
    public MyBatisPagingItemReader<Person> reader_page( @Value("#{jobParameters[ver]}") String ver, @Value("#{jobParameters[date]}") String date) throws Exception{

        log.info("func reader start!!");
        log.info("ver = " + ver);
        log.info("date = " + date);

        Map<String,Object> parameterValues = new HashMap<>();
        parameterValues.put("Id",10);

        String s_qryId = Const.MAPPER_PATH + "PersonMapperChunk1.getPersonPaging";

        return new MyBatisPagingItemReaderBuilder<Person>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId(s_qryId)
                .parameterValues(parameterValues)
                .pageSize(SKIP_CHUNK_PAGE_SIZE)
                // .maxItemCount(10) // 이건 설정하지 않음. 설정하면 데이터가 천만건이라도 딱 설정된 값까지만 가져옴. 특수한 상황에서 사용.
                .build();
    }

    /**
     * cursor 기반 reader
     * 별도로 사이즈나 페이지 처리하는 세팅이 없는걸로 보임.
     * 커서가 커넥션 독점할 가능성 높음. 대용량처리시 배제할 것
     */
    //@Bean
    @StepScope // job parameter를 얻기 위해 설정(step의 하위 메소드에서 설정해야함. 스텝에 설정하면 안됨)
    public MyBatisCursorItemReader<Person> reader_cursor() {

        String s_qryId = Const.MAPPER_PATH + "PersonMapperChunk1.getPersonCursor";
        Map<String ,Object> param = new HashMap<>();
        param.put("Id" ,10);

        return new MyBatisCursorItemReaderBuilder<Person>()
                .sqlSessionFactory(sqlSessionFactory)
                //.maxItemCount(3)
                .queryId(s_qryId)
                .parameterValues(param)
                .build();
    }



    //@Bean
    @StepScope
    public ItemProcessor<Person, Person> processor() {

        log.info(" process func start!!");

        /**
         * 1.람다로 로그
         */
//        return person -> {
//            log.info("안녕하세요. " + person.getLastName() + "입니다.");
//            return person;
//        };

        /**
         * 2. 메소드오버라이딩 로그
         */
        return new ItemProcessor<Person, Person>() {
            @Override
            public Person process(Person person)  throws Exception{

                log.info(" before person = " + person.toString());

                person.setPersonId(person.getPersonId()+20);
                person.setFirstName(person.getFirstName() + "_" + Integer.toString(person.getPersonId()));
                person.setLastName(person.getLastName() + "_" + Integer.toString(person.getPersonId()));

                //SelectOneByOpenSession();


                //log.info(" after person = " + person.toString());
                return person;
            }
        };
    }

    /**
     * Cannot change the ExecutorType when there is an existing transaction 오류
     *
     * 현상은 "Transaction내에서 ExecutorType이 변경될 수 없다” 이다.
     * 위 현상은 Batch 처리를 할 때 발생할 수 있다. 그 이유는 아래와 같다.
     *
     * MybatisPagingItemReader를 사용하면 내부적으로 SqlSessionTemplate이 생성될 때 ExecutorType.BATCH로 지정된다.
     * 만약 MybatisPagingItemReader에서 다른 쿼리를 날려보고자 한다면 기본적으로 ExecutorType.SIMPLE이기 때문에 문제가 될 수 있다.
     *
     *
     * SpringBatch에서 ExecutorType에는 3가지가 있다.
     *
     * SIMPLE: executor가 별도의 작업을 하지 않음.
     * REUSE: prepared statement 재사용
     * BATCH: prepared statement 재사용 & 묶음 갱신
     *
     *
     * Mybatis 기본 설정에서는 ExecutorType.SIMPLE이 기본이다. 따라서 CommonRepository.now()에서는 SIMPLE로 동작한다.
     * 하지만 MybatisPagingItemReader의 경우는 ExecutorType.BATCH로 SqlSessionTemplate을 생성하고 있다.
     *
     *
     * Transaction 설정이 있다면 ExecutorType이 SIMPLE에서 BATCH로 변경되기 때문에 오류가 발생할 수 있다.
     *
     *
     *
     * 해결방법 1 : application.yml에 다음과 같이 설정했으나 같은 오류발생
     *            mybatis:
     *            executor-type=BATCH:
     *
     *            mybatis:
     *           configuration:
     *           default-executor-type=BATCH:
     *
     *        2 :sqlSessionFactory.openSession().selectOne() , selectList()사용해서 해결
     */
    public void SelectOneByOpenSession(){


        /**
         * 매퍼+인터페이스 방식은 트랜잭션 오류로 사용 불가
         */
//        Person resultOne = new Person();
//        Integer Id = 1;
//
//        resultOne = personMapperDirect.getPersonbyIdOne(Id);
////
////        log.info("resultOne = " + resultOne.toString());
////
////        //0-1. 단건 count 조회
////        Integer personCnt = 0;
////        personCnt = personMapperDirect.getPersonCnt();
////
////        log.info("personCnt = " + personCnt);

        Integer cnt1=0;
        //cnt1 = sqlSessionTemplate.selectOne("com.example.allinonebatch.mapper.PersonMapperChunk1.getPersonCnt");
        cnt1 = sqlSessionFactory.openSession().selectOne("com.example.allinonebatch.mapper.PersonMapperChunk1.getPersonCnt");



        List<Person> list = new ArrayList<>();
        Map<String,Object> param = new HashMap<>();
        param.put("Id",0);
        param.put("lastname","KIM");

        list = sqlSessionFactory.openSession().selectList("com.example.allinonebatch.mapper.PersonMapperDirect.getPersonbyHash", param);

        for(Person pp:list){
            log.info("pp = " + pp.toString());
        }

    }




    //@Bean
    @StepScope
    public MyBatisBatchItemWriter<Person> writerToDB(){

        String i_qryId = Const.MAPPER_PATH + "PersonMapperChunk1.insertPerson";

        return new MyBatisBatchItemWriterBuilder<Person>()
                .sqlSessionFactory(sqlSessionFactory)
                //.statementId("com.example.allinonebatch.mapper.PersonMapperChunk1.insertPerson")
                .statementId(i_qryId)
                .build();

    }

    //@Bean
    @StepScope
    public ItemWriter<Person> writerToDbByItemWriter() {

        MyBatisBatchItemWriter<Person> writer = new MyBatisBatchItemWriter<>();
        String i_qryId = Const.MAPPER_PATH + "PersonMapperChunk1.insertPerson";
        writer.setSqlSessionFactory(sqlSessionFactory);
        writer.setStatementId(i_qryId);
        return writer;

    }




    //@Bean
    @StepScope
    public ItemWriter<Person> writer_print() {

        log.info("func write_print start!!");

        /**
         * 1. 람다로 로그
         */
//        return list -> {
//            for (Person person : list) {
//                log.info("Current person={}", person);
//            }
//        };

        /**
         * 메소드 오버라이딩해서 로그
         */
        return new ItemWriter<Person>() {
            @Override
            public void write(List<? extends Person> items) throws Exception {
                for (Person person:items ){
                    log.info(" person.getPersonId() = " + person.getPersonId());
                }
            }
        };
    }


    /**
     * 오류메세지 : org.springframework.batch.item.WriterNotOpenException: Writer must be open before it can be written to
     * @Bean 과 @StepScope으로 ItemWriter<>을 구현하고 FlatFileItemWriter로 리턴하는게 원인
     * 원래라면 ItemWriter는 ItemStream으로 리턴되어도 상관없지만, 위에 @Bean과 @StepScope를 같이 사용해서 Bean이 proxy로 설정되어 FlatFileItemWriter에서 지원하는 open() 메서드가 동작을 안하게 되어 발생되는 문제.
     * ItemWriter로 반환하려면 @Bean이나 @StepScope 둘 중 하나를 삭제한다. (proxy 로 지정되는 부분을 삭제)
     *
     * @StepScope를 삭제하니  writer(writerTOFlatFile()) 호출부를 주석으로 막아놔도 잡시작시에 강제로 호출이 됨.
     * @Bean을 삭제하니 잡시작시에 자동으로 돌고 + processor()다음에 writerTOFlatFile()로 넘어가지를 않음.
     * 근데 골때리는건 @Bean을 삭제하던 @StepScope를 삭제하던 ... 파일에 write는 정상적으로 함......
     * 골패네....
     *
     *
     * 오류해결 : 리턴값이 ItemWriter라서 FlatFileItemWriter에서 지원하는 open() 메서드가 동작을 안하는게 원인이므로
     * 리턴을 FlatFileItemWriter로 바꿔주면 됨.
     * 잡시작시에 자동으로 먼저 호출되지도 않고 reader-processor-writer순으로 동작도 정상적임. @Bean이나 @StepScope도 그대로 선언 가능.
     */
    @Bean
    @StepScope // 요거 삭제해야 Writer must be open before it can be written to 오류 안남
    //public ItemWriter<Person> writerTOFlatFile(){
    public FlatFileItemWriter<Person> writerTOFlatFile(){
        Path outputFilePath = Paths.get("src\\main\\resources\\Output", "PersonFlatFile.txt");
        Resource outputFile = new FileSystemResource(outputFilePath.toFile());

        log.info( "writerTOFlatFile >> outputFile = " + outputFile);

        return new FlatFileItemWriterBuilder<Person>()
                .name("writerTOFlatFile")
                .resource(outputFile)
                .formatted()
                .format("%10s%20s%30s") // text 포맷팅 fixed length
                .names(new String[] {"personId", "firstName","lastName"}) // 순서대로
                .build();

    }


}
