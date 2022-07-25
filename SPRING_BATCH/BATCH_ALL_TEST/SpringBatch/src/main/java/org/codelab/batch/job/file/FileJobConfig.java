package org.codelab.batch.job.file;

import org.apache.ibatis.session.SqlSessionFactory;
import org.codelab.batch.common.Const;
import org.codelab.batch.dto.Person;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FileJobConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Bean(name = Const.JOB_READFILE)
	public Job job() {
		return jobBuilderFactory.get(Const.JOB_READFILE)
				.incrementer(new RunIdIncrementer())
				.start(stepReadfile()).build();
	}
	
	@Bean
    public Step stepReadfile() {
        return stepBuilderFactory.get("stepReadfile")
            .<Person, Person>chunk(1)
            .reader(personItemReader())
            .processor(processor())
            .writer(writer())
            .build();
    }
	
	@Bean
	public FlatFileItemReader<Person> personItemReader() {
		return new FlatFileItemReaderBuilder<Person>()
				.name("personItemReader")
				.resource(new ClassPathResource("sample-data.csv"))
				.delimited()
				.names(new String[] {"firstName","lastName"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
					setTargetType(Person.class);
				}}).build();
	}
	
	@Bean
	public transformItemProcessor processor() {
		return new transformItemProcessor();
	}
	
	@Bean
	public ItemWriter<Person> writer() {
		MyBatisBatchItemWriter<Person> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(sqlSessionFactory);
		writer.setStatementId("org.codelab.batch.mapper.PersonMapper.insertPerson");
		return writer;
	}
}
