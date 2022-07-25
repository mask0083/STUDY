package org.codelab.batch.job.tasklet;

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
				.start(normalStep1())
				.next(taskletStep2()).build();
	}


	/**
	 * job -> step1, step2, step3.....
	 * 각 step은 다음 둘중 하나를 선택해야 함
	 * 1.reader(), processor(), write()
	 * 2.tasklet((contribution, chunkContext) -> { }으로 소단위 작업 수행

	 * @return
	 */


	/**
	 * 1.reader(), write()을 이용한 step 처리
	 * @return
	 */
	@Bean
	public Step normalStep1() {
		return stepBuilderFactory.get("normalStep1").<String, String>chunk(1)
				.reader(new Reader())
				.writer(new Writer())
				.build();
	}


	/**
	 * 2.tasklet((contribution, chunkContext) -> { }을 이용한 step 처리
	 * contribution : 현재 step의 상세 상태(read,write 갯수, 현재 상태등)
	 * chunkContext : 현재 job이나 step의 상태를 Map형태로 반환.
	 * @return
	 */
	@Bean
	public Step taskletStep2() {

		/**
		 * tasklet(taskletStep)으로 호출시
		 * return RepeatStatus.FINISHED; 필요없음.
		 */
//		return stepBuilderFactory.get("taskletStep2")
//				.tasklet(taskletStep)
//				.build();

		/**
		 * 아래 둘중 선택 가능. 단 반드시 return RepeatStatus.FINISHED; 필요. return안하면 오류남.
		 * 1.tasklet((contribution, chunkContext)
		 * 2.tasklet((a, b)
		 */
//		return stepBuilderFactory.get("taskletStep2").tasklet((contribution, chunkContext) -> {
//			System.out.println(" taskletStep2.. taksklet...");
//			return RepeatStatus.FINISHED;
//		}).build();

		return stepBuilderFactory.get("taskletStep2").tasklet((A, B) -> {
			System.out.println("A.toString() = " + A.toString() + " B.toString() = " + B.toString());
			System.out.println("A.getExitStatus() = " + A.getExitStatus() );
			System.out.println("A.getReadCount() = " + A.getReadCount() );

			System.out.println("B.getStepContext() = "+B.getStepContext());
			System.out.println("B.getClass() = "+B.getClass());

			System.out.println(" taskletStep2.. FINISHED");
			return RepeatStatus.FINISHED;
		}).build();

	}
}
