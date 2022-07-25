package com.example.batchtestmysql.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TaskletJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * taskletJob이 job name임. 두번실행할경우 rerun 오류 생기므로 다음과 같이 파라미터를 줘서 실행할 것
     * @return
     */

    @Bean

    /**
     * Job에서 @StepScope로 설정하면 Scope 'step' is not active for the current thread; 오류발생함.
     * @JobScope는 각 step에서 설정
     * @StepScope는 각 step에서 호출하는 하위메소드에서 설정해서 파라미터를 받아야 함.
     */
    //@StepScope
    //public Job taskletJob_batchBuild(@Value("#{jobParameters[ver]}") String ver,  @Value("#{jobParameters[date]}") String date){
    public Job taskletJob_batchBuild(){
//        log.info("<<<<<< ------------ taskletJob_batchBuild----------->>>>>>");
//        log.info(">>>>jobversion = " + ver);
        return jobBuilderFactory.get("taskletJob").start(taskletJob_step1())
                .next(taskletJob_step2(null,null)).build();
    }

    @Bean
    public Step taskletJob_step1(){
    return stepBuilderFactory.get("taskletJob_step1").tasklet((contribution, chunkContext) -> {
        log.info("...........taskletJob_step1.........");
        return RepeatStatus.FINISHED;
    }).build();

    }


    @Bean
    @JobScope   // Step에서 jobParameters를 가져오고 싶으면 Step 정의 메소드 위에 @JobScope를 붙여준다.
    public Step taskletJob_step2( @Value("#{jobParameters[ver]}") String ver,  @Value("#{jobParameters[date]}") String date) {
        log.info("<<<<<< ------------ taskletJob_step2----------->>>>>>");

        // next(taskletJob_step2(null,null))에서 던진 null이 아닌 job을 실행시킬때 파라미터로 던진 값을 받음.
        log.info(" ver = " + ver);
        log.info(" date = " + date);
        return stepBuilderFactory.get("taskletJob_step2").tasklet((a,b) -> {
            log.info("...........taskletJob_step1 -> step2().........");
            return RepeatStatus.FINISHED;
        }).build();

    }




}
