package org.codelab.batch.job.simple;

import org.codelab.batch.common.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleJobConfig {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleJobConfig.class);

	@Autowired
    private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean(name=Const.JOB_SIMPLE)
    public Job simpleJob() {
        return jobBuilderFactory.get(Const.JOB_SIMPLE)
                .start(simpleStep1(null))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step1");
                    log.info(">>>>> requestDate {}",requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
