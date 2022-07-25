package org.codelab.batch.job.alphabet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class AlphabetReader implements ItemReader<String> {

	private static final Logger log = LoggerFactory.getLogger(AlphabetReader.class);

	private int idx;
	private List<String> alphabets;

	public AlphabetReader() {
		idx = 0;
		init();
	}

	private void init() {
		alphabets = new ArrayList<String>();
		alphabets.add("a");
		alphabets.add("b");
		alphabets.add("c");
		alphabets.add("d");
		alphabets.add("e");
		alphabets.add("f");
		alphabets.add("g");
		alphabets.add("h");
		alphabets.add("i");
		alphabets.add("j");
	}

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		String alphabet = null;
		if (idx < alphabets.size()) {
			alphabet = alphabets.get(idx);
			idx++;
		}
		log.info("Read {} - {}", idx, alphabet);
		return alphabet;
	}
}
