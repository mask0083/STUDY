package org.codelab.batch.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.codelab.batch.job.tasklet.TaskletJobConfig;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dalgun
 * @since 2019-10-30
 */
@Slf4j
@Component
public class JobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private TaskletJobConfig taskletJobConfig;

    @Scheduled(fixedDelay = 30000)
    public void runJob() {

        log.info(" run job 스케줄링");
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try {

            jobLauncher.run(taskletJobConfig.job(), jobParameters);

        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException | org.springframework.batch.core.repository.JobRestartException e) {

            log.error(e.getMessage());
        }
    }

}
