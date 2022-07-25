package org.codelab.batch.job.SimpleJob;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SimpleJobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    /**
     * Job에서 @StepScope로 설정하면 Scope 'step' is not active for the current thread; 오류발생함.
     * @JobScope는 각 step에서 설정
     * @StepScope는 각 step에서 호출하는 하위메소드에서 설정해서 파라미터를 받아야 함.
     */
    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .incrementer(new RunIdIncrementer())	// RunIdIncrementer : 동일파라미터라도 재실행이 가능
                .start(simpleStep(null,null))   // @JobScope로 job parameter을 가져올때는 메소드의 인자를 맞춰줘야 하므로 null로 던져줌
                .build();
    }

    @Bean
    @JobScope   // Step에서 jobParameters를 가져오고 싶으면 Step 정의 메소드 위에 @JobScope를 붙여준다.
    public Step simpleStep( @Value("#{jobParameters[ver]}") String ver, @Value("#{jobParameters[date]}") String date) {
//        return stepBuilderFactory.get("simpleStep")
//                .tasklet((contribution, chunkContext) -> {
//                    System.out.println("sssssssss");
//                    log.info(">>>>> simple job started >>>>>");
//                    return RepeatStatus.FINISHED;
//                })
//                .build();

        log.info("simple step start");
        log.info(" ver = " + ver);
        log.info(" date = " + date);

        return stepBuilderFactory.get("simpleStep").tasklet((A, B) -> {
            log.info("A.toString() = " + A.toString() + " B.toString() = " + B.toString());
            log.info("A.getExitStatus() = " + A.getExitStatus());
            log.info("A.getReadCount() = " + A.getReadCount());

            log.info("B.getStepContext() = " + B.getStepContext());
            log.info("B.getClass() = " + B.getClass());

            log.info(" simpleStep.. FINISHED");
            return RepeatStatus.FINISHED;
        }).build();
    }


}
