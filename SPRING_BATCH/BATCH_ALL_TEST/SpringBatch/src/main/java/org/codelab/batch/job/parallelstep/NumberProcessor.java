package org.codelab.batch.job.parallelstep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class NumberProcessor implements ItemProcessor<Integer, Integer> {

	private static final Logger log = LoggerFactory.getLogger(NumberProcessor.class);

	@Override
	public Integer process(Integer item) throws Exception {
		log.info("Process - {}", item+10);
		return item+10;
	}
}
