package org.codelab.batch.job.tasklet;

import org.codelab.batch.common.Const;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskletJobConfig {

	@Autowired
	TaskletStep taskletStep;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean(name = Const.JOB_TASKLET)
	public Job job() {
		return jobBuilderFactory.get(Const.JOB_TASKLET)
				.incrementer(new RunIdIncrementer())
				.start(normalStep1())
				.next(taskletStep2()).build();
	}

	@Bean
	public Step normalStep1() {
		return stepBuilderFactory.get("normalStep1").<String, String>chunk(1)
				.reader(new Reader())
				.writer(new Writer())
				.build();
	}

	@Bean
	public Step taskletStep2() {
		return stepBuilderFactory.get("taskletStep2")
				.tasklet(taskletStep)
				.build();
	}
}
