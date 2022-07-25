package org.codelab.batch.job.ipfilter;

import org.codelab.batch.common.Const;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IpFilterJobConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	private Step stepIpfilter;

	@Bean(name = Const.JOB_IPFILTER)
	public Job job() {
		return jobBuilderFactory.get(Const.JOB_IPFILTER)
				.incrementer(new RunIdIncrementer())
				.start(stepIpfilter)
				.build();
	}
}
