package com.example.allinonebatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * @EnableBatchProcessing  : 배치활성화
 * 선언안하면 아래와 같은 오류남
 * The injection point has the following annotations:
 * 	- @org.springframework.beans.factory.annotation.Autowired(required=true)
 */
@EnableBatchProcessing
public class AllinonebatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllinonebatchApplication.class, args);
	}

}
