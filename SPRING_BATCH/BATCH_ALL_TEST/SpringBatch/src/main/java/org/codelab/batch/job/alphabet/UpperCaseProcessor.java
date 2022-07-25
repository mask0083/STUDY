package org.codelab.batch.job.alphabet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class UpperCaseProcessor implements ItemProcessor<String, String> {

	private static final Logger log = LoggerFactory.getLogger(UpperCaseProcessor.class);

	@Override
	public String process(String item) throws Exception {
		log.info("Process - {}", item.toUpperCase());
		return item.toUpperCase();
	}
}
