package org.codelab.batch.job.tasklet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class Writer implements ItemWriter<String> {

	private static final Logger log = LoggerFactory.getLogger(Writer.class);
	
	@Override
	public void write(List<? extends String> paths) throws Exception {
		for (String filePath : paths) {
			log.info("filePath = {}", filePath);
			try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
				stream.forEach(System.out::println);
			} catch (IOException e) {
				throw(e);
			}
		}
	}
}
