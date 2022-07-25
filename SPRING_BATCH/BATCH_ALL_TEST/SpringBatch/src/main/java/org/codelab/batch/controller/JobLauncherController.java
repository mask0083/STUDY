package org.codelab.batch.controller;

import org.codelab.batch.common.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
public class JobLauncherController {

	@Autowired
    JobLauncher jobLauncher;

	@Qualifier(Const.JOB_ALPHABET)
	@Autowired
    Job job;
	
	@RequestMapping("/launchJob")
	public String handle() throws Exception {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		try {
			//JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
			JobParameters jobParameters = new JobParametersBuilder().addLong("uniqueness", System.nanoTime()).toJobParameters();
			jobLauncher.run(job, jobParameters);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return "Done!";
	}
}
