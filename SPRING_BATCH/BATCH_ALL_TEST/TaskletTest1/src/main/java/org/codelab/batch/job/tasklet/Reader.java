package org.codelab.batch.job.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Arrays;

public class Reader implements ItemReader<String> {
	
	private String[] files = {"D:\\INFOPLUS\\STUDY\\BATCH_ALL_TEST\\TaskletTest\\ipaddr.txt"};

	/**
	 * 위처럼 배열로 선언후 count 체크해서 return null로 반환하지 않고 밑에처럼 그냥 String files로 해버려서 return null을 하지 않으면 무한루프에 빠짐.
	 * 이유는 모르겠음. read()를 계속 반복적으로 호출함.
	 *
	 * 아마도 여기서 ItemReader의 read를 재정의 하고 return null을 안하면 무한반복 호출을 하는 듯함
	 * SimpleChunkProvider.java : provide()->doInIteration()
	 * if (item == null) {
	 * 					inputs.setEnd();
	 * 					return RepeatStatus.FINISHED;
	 *                   }
	 *
	 *
	 * 여기서 null에 걸리지 않기때문에 무한루프를 도는걸로 보임.
	 *
	 *
	 */
//	private String files = "D:\\INFOPLUS\\STUDY\\BATCH_ALL_TEST\\TaskletTest\\ipaddr.txt";
	public int count =0;
	public static int cnt_read = 0;
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		logger.error("read()) : files = " + files);
		logger.error("files.length = " + files.length);

		cnt_read++;
		logger.error("cnt_read = " + cnt_read);
		if(count < files.length) {
			return files[count++];
		} else {
			count=0;
		}

//		if(null!= files && files.length() > 0 )
//			return files;

		return null;
	}
}
