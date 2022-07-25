package org.codelab.batch.job.ipfilter;

import java.util.HashMap;
import java.util.Map;

import org.codelab.batch.dto.IpAddr;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class IpFilterStepConfig {
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Step stepIpfilter() {
		return stepBuilderFactory.get("stepIpfilter")
				.<IpAddr,IpAddr> chunk(1)
				.reader(ipReader())
				.processor(ipFilterProcessor())
				.writer(ipClassifierFileWriter())
				.stream(filteredIpWriter())
				.stream(unfilteredIpWriter())
				.build();
	}
	
	@Bean
	public FlatFileItemReader<IpAddr> ipReader() {
		FlatFileItemReaderBuilder<IpAddr> builder = new FlatFileItemReaderBuilder<>();
		builder.name("ipReader");
		builder.resource(new ClassPathResource("ipaddr.txt"));
		builder.delimited()
		.names(new String[] {"ip"})
		.fieldSetMapper(new BeanWrapperFieldSetMapper<IpAddr>() {{
			setTargetType(IpAddr.class);
		}}).build();
		return builder.build();
	}
	
	@Bean
	public IpFilterProcessor ipFilterProcessor() {
		return new IpFilterProcessor();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	@Bean
	public ClassifierCompositeItemWriter<IpAddr> ipClassifierFileWriter() {
		Map<Boolean, ItemWriter<IpAddr>> writerMap = new HashMap<>();
		writerMap.put(true, filteredIpWriter());
		writerMap.put(false, unfilteredIpWriter());
		ClassifierCompositeItemWriter writer = new ClassifierCompositeItemWriter();
		writer.setClassifier(new IpClassfier() {{
			setWriterMap(writerMap);
		}});
		return writer;
	}
	
	@Bean
	public FlatFileItemWriter<IpAddr> filteredIpWriter() {
		FlatFileItemWriterBuilder<IpAddr> builder = new FlatFileItemWriterBuilder<>();
		builder.name("filteredIpWriter");
		builder.resource(new FileSystemResource("D:\\files\\ipaddr_filtered.txt"));
		builder.lineAggregator(new PassThroughLineAggregator<>());
		return builder.build();
	}

	@Bean
	public FlatFileItemWriter<IpAddr> unfilteredIpWriter() {
		FlatFileItemWriterBuilder<IpAddr> builder = new FlatFileItemWriterBuilder<>();
		builder.name("unfilteredIpWriter");
		builder.resource(new FileSystemResource("D:\\files\\ipaddr_unfiltered.txt"));
		builder.lineAggregator(new PassThroughLineAggregator<>());
		return builder.build();
	}
}
