package org.codelab.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing // 배치 활성화
public class Application {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
		System.out.println("finished");
	}
}