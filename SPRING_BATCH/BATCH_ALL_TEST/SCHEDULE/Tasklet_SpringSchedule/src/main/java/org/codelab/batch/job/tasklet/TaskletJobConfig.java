package org.codelab.batch.job.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.codelab.batch.common.Const;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Configuration
public class TaskletJobConfig {

	@Autowired
	TaskletStep taskletStep;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;


	@Bean(name = Const.JOB_TASKLET)
	public Job job() {
		return jobBuilderFactory.get(Const.JOB_TASKLET)
				.incrementer(new RunIdIncrementer())	// RunIdIncrementer : 동일파라미터라도 재실행이 가능
				.start(taskletStepCall())
				.build();
	}


	/**
	 * job -> step1, step2, step3.....
	 * 각 step은 다음 둘중 하나를 선택해야 함
	 * 1.reader(), processor(), write()
	 * 2.tasklet((contribution, chunkContext) -> { }으로 소단위 작업 수행
	 */
	/**
	 * 2.tasklet((contribution, chunkContext) -> { }을 이용한 step 처리
	 * contribution : 현재 step의 상세 상태(read,write 갯수, 현재 상태등)
	 * chunkContext : 현재 job이나 step의 상태를 Map형태로 반환.
	 */
	@Bean
	public Step taskletStepCall() {

		/** 방법 1. TaskletStep.java -> Tasklet 인터페이스의 execute()메소드 오버라이딩 후 호출
		 * tasklet(taskletStep)으로 호출시
		 */
//		return stepBuilderFactory.get("taskletStep")
//				.tasklet(taskletStep)
//				.build();

		/**방법 2. : 아래 둘중 선택 가능.
		 *  단 반드시 return RepeatStatus.FINISHED; 필요. return안하면 오류남.
		 * 1.tasklet((contribution, chunkContext)
		 * 2.tasklet((a, b)
		 */
//		return stepBuilderFactory.get("taskletStepCall").tasklet((contribution, chunkContext) -> {
//			log.info(" taskletStepCall.. taksklet...");
//			return RepeatStatus.FINISHED;
//		}).build();

		return stepBuilderFactory.get("taskletStepCall").tasklet((A, B) -> {
			log.info("A.toString() = " + A.toString() + " B.toString() = " + B.toString());
			log.info("A.getExitStatus() = " + A.getExitStatus() );
			log.info("A.getReadCount() = " + A.getReadCount() );

			log.info("B.getStepContext() = "+B.getStepContext());
			log.info("B.getClass() = "+B.getClass());

			log.info(" taskletStepCall.. FINISHED");
			return RepeatStatus.FINISHED;
		}).build();

	}
}
