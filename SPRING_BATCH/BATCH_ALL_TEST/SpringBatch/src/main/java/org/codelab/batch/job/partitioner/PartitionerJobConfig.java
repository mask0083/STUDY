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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class PartitionerJobConfig {

	private static final Logger log = LoggerFactory.getLogger(PartitionerJobConfig.class);

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	@Bean(name = Const.JOB_PARTITIONER)
	public Job partitionJob() {
		return jobBuilderFactory.get(Const.JOB_PARTITIONER)
				.incrementer(new RunIdIncrementer())
				.start(masterStep()).build();
	}
	
	@Bean 
	Step masterStep() {
		return stepBuilderFactory.get("masterStep")
				.partitioner(slaveStep().getName(), rangePartitioner())
				.partitionHandler(masterSlaveHandler()).build();
	}
	
	@Bean
	public PartitionHandler masterSlaveHandler() {
		TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
		handler.setGridSize(10);
		handler.setTaskExecutor(taskExecutor());
		handler.setStep(slaveStep());
		try {
			handler.afterPropertiesSet();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return handler;
	}
	
	@Bean
	public Step slaveStep() {
		return stepBuilderFactory.get("slaveStep").<Person,Person>chunk(10)
				.reader(slaveReader(0,0,null))
				.processor(slaveProcessor(null))
				.writer(slaveWriter(null))
				.build();
	}
	
	@Bean
	@StepScope
	public MyBatisPagingItemReader<Person> slaveReader(
			@Value("#{stepExecutionContext[skipRows]}") final int skipRows,
			@Value("#{stepExecutionContext[pageSize]}") final int pageSize,
			@Value("#{stepExecutionContext[name]}") final String name) {
		log.info("--------- called slaveReader -------------{}, {}, {}", name, skipRows, pageSize);
		MyBatisPagingItemReader<Person> reader = new MyBatisPagingItemReader<>();
		reader.setSqlSessionFactory(sqlSessionFactory);
		reader.setQueryId("getPersonPaging");
		reader.setCurrentItemCount(skipRows);
		reader.setMaxItemCount(skipRows+pageSize);
		reader.setPageSize(pageSize);
		return reader;
	}
	
	@Bean
	@StepScope
	public PartitionerProcessor slaveProcessor(@Value("#{stepExecutionContext[name]}") String name) {
		log.info("--------- called slaveProcessor ------------- {}", name);
		PartitionerProcessor processor = new PartitionerProcessor();
		processor.setThreadName(name);
		return processor;
	}
	
	@Bean
	@StepScope
	public PartitionerWriter slaveWriter(@Value("#{stepExecutionContext[name]}") String name) {
		log.info("--------- called slave Writer ------------- {}", name);
		PartitionerWriter writer = new PartitionerWriter();
		writer.setThreadName(name);
		return writer;
	}
	
	@Bean 
	public SimpleAsyncTaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}
	
	@Bean
	public RangePartitioner rangePartitioner() {
		return new RangePartitioner(sqlSessionTemplate, "countPerson");
	}
}
