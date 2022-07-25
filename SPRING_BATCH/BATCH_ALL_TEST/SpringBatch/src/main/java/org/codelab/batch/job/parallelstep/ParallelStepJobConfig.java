package org.codelab.batch.job.parallelstep;

import org.codelab.batch.common.Const;
import org.codelab.batch.job.alphabet.AlphabetReader;
import org.codelab.batch.job.alphabet.UpperCaseProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class ParallelStepJobConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Step stepReadfile;

	@Bean(name = Const.JOB_PARALLEL)
	public Job job() {
		return jobBuilderFactory.get(Const.JOB_PARALLEL)
				.incrementer(new RunIdIncrementer())
				.start(splitFlow())
				.next(stepReadfile)
				.build()
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("parallelStepExecutor");
	}
	
	@Bean
	public Flow splitFlow() {
		return new FlowBuilder<SimpleFlow>("splitFlow")
				.split(taskExecutor())
				.add(flow1(),flow2())
				.build();
	}
	
	@Bean
	public Flow flow1() {
		return new FlowBuilder<SimpleFlow>("flow1")
				.start(step1())
				.build();
	}
	
	@Bean
	public Flow flow2() {
		return new FlowBuilder<SimpleFlow>("flow2")
				.start(step2())
				.build();
	}
	
	@Bean
	public Flow flow3() {
	    return new FlowBuilder<SimpleFlow>("flow2")
	        .start(stepReadfile)
	        .build();
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("stepParallel1")
				.<Integer,Integer>chunk(1)
				.reader(new NumberReader())
				.processor(new NumberProcessor())
				.build();
	}
	
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("stepParallel2")
				.<String,String>chunk(1)
				.reader(new AlphabetReader())
				.processor(new UpperCaseProcessor())
				.build();
	}
}
