package com.example.allinonebatch.job.jobchunk2;

import com.example.allinonebatch.common.Const;
import com.example.allinonebatch.dto.Person;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * tasklet + chunk 혼합해서 사용
 *
 * jobchunk2_Step1  :tasklet방식
 * jobchunk2_Step2 :chunk 방식
 *
 *
 *  !!중요!!
 *  동작은 문제없으나 jobchunk1Config에서 선언한 메소드 이름을 그대로 사용못함. ( 오류발생 )
 *  public ->private 으로 변경해주면 private or final은 선언할수 없다고 오류.
 *
 *
 *   jobchunk1_Step() 밑의 하위 메서드들의 이름을 다른 잡에서 중복해서 사용하려면 @Bean으로 등록하면 안됨.
 *   @Bean으로 등록시 defined in class path resource could not be registered 오류 발생함.
 *   private으로 선언하면 private or final 은 선언불가 라고 오류 떨어짐.
 *   같은 메소드 이름을 다른 잡에서 중복해서 사용하고 싶으면 @Bean 삭제.(실행하는데 문제없음)
 *
 */

@Slf4j
@Configuration
public class jobchunk2Config {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    private final int SKIP_CHUNK_PAGE_SIZE = 3;


    /**
     * jobchunk2_Step1 : 기존 데이터 모두 삭제
     *  return RepeatStatus.FINISHED; 이후에 바로 commit되서 데이터 삭제됨.
     *  스프링배치가 실패시점의 데이터를 스텝에 저장해서 재실행시 실패한 데이터부터 다시 읽어온다고 하나 동작을 확실할수 없어
     *  그냥 기존 데이터 다 지우고 재실행시킴.
     * jobchunk2_Step2 : 새로 insert
     *
     */

    @Bean(name = Const.JOBCHUNK2)
    public Job Jobchunk2_job() throws Exception{

        log.info("Jobchunk2_job start!!");

        return jobBuilderFactory.get(Const.JOBCHUNK2)
                .incrementer(new RunIdIncrementer())	// RunIdIncrementer : 동일파라미터라도 재실행이 가능
                .start(jobchunk2_Step1(null,null))
                .next(jobchunk2_Step2(null,null))// jobParameter을 받기 위해 null로 던짐. 파라미터 갯수만큼 던지면 됨.
                .build();
    }



    @Bean
    @JobScope   // job parameter 획득하기 위해 설정
    public Step jobchunk2_Step1(@Value("#{jobParameters[ver]}") String ver, @Value("#{jobParameters[date]}") String date) throws Exception {

        log.info("jobchunk2_Step1 start!!");
        log.info("ver = " + ver);
        log.info("date = " + date);

        return stepBuilderFactory.get("jobchunk2_Step1").tasklet((A, B) -> {

            sqlSessionFactory.openSession().delete("com.example.allinonebatch.mapper.PersonMapperChunk1.deletePerson1All");
            log.info("delete complete!!");

            return RepeatStatus.FINISHED;
        }).build();
    }


    @Bean
    @JobScope   // job parameter 획득하기 위해 설정
    public Step jobchunk2_Step2(@Value("#{jobParameters[ver]}") String ver, @Value("#{jobParameters[date]}") String date) throws Exception {

        log.info("jobchunk2_Step2 start!!");
        log.info("ver = " + ver);
        log.info("date = " + date);

//        return stepBuilderFactory.get("jobchunk2_Step2").tasklet((A, B) -> {
//            return RepeatStatus.FINISHED;
//        }).build();



        return stepBuilderFactory.get("jobchunk2_Step2")
                .<Person,Person>chunk(SKIP_CHUNK_PAGE_SIZE)
                .reader(reader_page(null,null))  // paging 기반 read : MyBatisPagingItemReaderBuilder 사용
                //.processor(processor())
                .writer(writerToDB())             // MyBatisBatchItemWriterBuilder사용
                .build();
    }

    //@Bean
    @StepScope // job parameter를 얻기 위해 설정(step의 하위 메소드에서 설정해야함. 스텝에 설정하면 안됨)
    public MyBatisPagingItemReader<Person> reader_page(@Value("#{jobParameters[ver]}") String ver, @Value("#{jobParameters[date]}") String date) throws Exception{

        log.info("func reader start!!");
        log.info("ver = " + ver);
        log.info("date = " + date);

        Map<String,Object> parameterValues = new HashMap<>();
        parameterValues.put("Id",0);

        String s_qryId = Const.MAPPER_PATH + "PersonMapperChunk1.getPersonPaging";

        return new MyBatisPagingItemReaderBuilder<Person>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId(s_qryId)
                .parameterValues(parameterValues)
                .pageSize(SKIP_CHUNK_PAGE_SIZE)
                // .maxItemCount(10) // 이건 설정하지 않음. 설정하면 데이터가 천만건이라도 딱 설정된 값까지만 가져옴. 특수한 상황에서 사용.
                .build();
    }


    //@Bean
    @StepScope
    public MyBatisBatchItemWriter<Person> writerToDB(){

        String i_qryId = Const.MAPPER_PATH + "PersonMapperChunk1.insertPerson1";

        return new MyBatisBatchItemWriterBuilder<Person>()
                .sqlSessionFactory(sqlSessionFactory)
                //.statementId("com.example.allinonebatch.mapper.PersonMapperChunk1.insertPerson")
                .statementId(i_qryId)
                .build();

    }



}
