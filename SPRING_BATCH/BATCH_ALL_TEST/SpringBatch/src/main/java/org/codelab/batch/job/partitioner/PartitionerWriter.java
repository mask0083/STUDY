package org.codelab.batch.job.partitioner;


import java.util.List;

import org.codelab.batch.dto.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class PartitionerWriter implements ItemWriter<Person> {

	private static final Logger log = LoggerFactory.getLogger(PartitionerWriter.class);
	
	private String threadName;

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	@Override
	public void write(List<? extends Person> persons) throws Exception {
		//log.info("writer "+persons.size());
	}

}
