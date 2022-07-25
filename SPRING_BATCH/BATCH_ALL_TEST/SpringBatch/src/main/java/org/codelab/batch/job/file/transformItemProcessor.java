package org.codelab.batch.job.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import org.codelab.batch.dto.Person;

public class transformItemProcessor implements ItemProcessor<Person, Person>{
	private static final Logger log = LoggerFactory.getLogger(transformItemProcessor.class);

	@Override
	public Person process(Person person) throws Exception {
		final String firstName = person.getFirstName().toUpperCase();
		final String lastName = person.getLastName().toUpperCase();
		
		final Person transformedPerson = new Person(firstName, lastName);
		log.info("Converting ( {} into {}", firstName, lastName);
		return transformedPerson;
	}
}
