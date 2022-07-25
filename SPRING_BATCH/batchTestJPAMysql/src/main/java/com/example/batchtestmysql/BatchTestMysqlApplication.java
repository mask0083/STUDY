package com.example.batchtestmysql;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class BatchTestMysqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchTestMysqlApplication.class, args);
    }

}
