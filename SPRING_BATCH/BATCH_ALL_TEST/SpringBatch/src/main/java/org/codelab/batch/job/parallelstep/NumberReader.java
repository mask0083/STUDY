package org.codelab.batch.job.parallelstep;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class NumberReader implements ItemReader<Integer> {

	private static final Logger log = LoggerFactory.getLogger(NumberReader.class);

	private int idx;
	private List<Integer> numbers;

	public NumberReader() {
		idx = 0;
		init();
	}

	private void init() {
		numbers = new ArrayList<Integer>();
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);
		numbers.add(4);
		numbers.add(5);
		numbers.add(6);
		numbers.add(7);
		numbers.add(8);
		numbers.add(9);
		numbers.add(10);
	}

	@Override
	public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Integer number = null;
		if (idx < numbers.size()) {
			number = numbers.get(idx);
			idx++;
		}
		log.info("Read {} - {}", idx, number);
		return number;
	}
}
