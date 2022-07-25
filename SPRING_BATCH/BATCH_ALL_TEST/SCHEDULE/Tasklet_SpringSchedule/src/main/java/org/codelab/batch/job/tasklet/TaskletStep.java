package org.codelab.batch.job.tasklet;

import java.io.File;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskletStep implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		log.info("contribution.toString() = " + contribution.toString() + " chunkContext.toString() = " + chunkContext.toString());
		log.info("contributionA.getExitStatus() = " + contribution.getExitStatus() );
		log.info("contribution.getReadCount() = " + contribution.getReadCount() );

		log.info("chunkContext.getStepContext() = "+chunkContext.getStepContext());
		log.info("chunkContext.getClass() = "+chunkContext.getClass());

		return RepeatStatus.FINISHED;
	}

}
