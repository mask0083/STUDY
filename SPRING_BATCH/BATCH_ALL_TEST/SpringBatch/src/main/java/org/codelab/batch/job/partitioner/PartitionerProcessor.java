package org.codelab.batch.job.partitioner;

import org.codelab.batch.dto.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PartitionerProcessor implements ItemProcessor<Person, Person> {
	
	private static final Logger log = LoggerFactory.getLogger(PartitionerProcessor.class);

	private String threadName;

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public Person process(Person item) throws Exception {
		//log.info("ThreadName :{}, id:{}, lastName:{}",threadName, item.getPersonId(), item.getLastName());
		return item;
	}
}
