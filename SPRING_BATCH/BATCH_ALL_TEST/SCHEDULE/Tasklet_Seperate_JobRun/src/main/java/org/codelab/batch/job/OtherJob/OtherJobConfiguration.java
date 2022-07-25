package org.codelab.batch.job.OtherJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OtherJobConfiguration {

    @Autowired
    private  JobBuilderFactory jobBuilderFactory;
    @Autowired
    private  StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job otherJob() {
        return jobBuilderFactory.get("otherJob")
                .incrementer(new RunIdIncrementer())	// RunIdIncrementer : 동일파라미터라도 재실행이 가능
                .start(otherStep())
                .build();
    }

    @Bean
    public Step otherStep() {
        return stepBuilderFactory.get("otherStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> other job started >>>>>");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}