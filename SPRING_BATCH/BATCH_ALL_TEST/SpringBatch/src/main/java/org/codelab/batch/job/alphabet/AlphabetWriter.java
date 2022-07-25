package org.codelab.batch.job.alphabet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class AlphabetWriter implements ItemWriter<String> {

	private static final Logger log = LoggerFactory.getLogger(AlphabetWriter.class);

	@Override
	public void write(List<? extends String> items) throws Exception {
		for (String item : items) {
			log.info("write - {}", item);
		}
	}
}
