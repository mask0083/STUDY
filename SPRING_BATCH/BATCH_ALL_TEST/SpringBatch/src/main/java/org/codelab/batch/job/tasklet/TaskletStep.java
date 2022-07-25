package org.codelab.batch.job.tasklet;

import java.io.File;

import org.codelab.batch.job.alphabet.AlphabetReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskletStep implements Tasklet {

	private static final Logger log = LoggerFactory.getLogger(AlphabetReader.class);

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		try {
			File file = new File("D:\\files\\ipaddr_filtered.txt");
			if (file.delete())
				log.info("############# TASKLET STEP : {} is deleted", file.getName());
			else
				log.info("Delete operation is failed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
