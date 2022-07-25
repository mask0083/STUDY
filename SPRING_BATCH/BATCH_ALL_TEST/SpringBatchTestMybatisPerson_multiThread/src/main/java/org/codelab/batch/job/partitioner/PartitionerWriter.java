package org.codelab.batch.job.partitioner;


import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.codelab.batch.dto.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class PartitionerWriter implements ItemWriter<Person> {

    //private static final Logger log = LoggerFactory.getLogger(PartitionerWriter.class);
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private String threadName;
    private static int cnt_write;



    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void write(List<? extends Person> persons) throws Exception {
        logger.info("writer "+persons.size());

        // 1. flat file write
        try {
            flatFileWrite(persons);
        }catch(Exception e){
            e.printStackTrace();
        }

        // 2. db write

    }


    public void flatFileWrite( List<? extends Person> persons) throws Exception{

        /**
         * FlleWrite 사용시 new FileWrite(파일명); => 추가로 이어쓰기 안됨
         *                 new FileWrite(파일명, true)  => 추가로 이어쓰기 가능
         */

        String filename = "D:\\INFOPLUS\\STUDY\\SPRING_BATCH\\BATCH_ALL_TEST\\SpringBatchTestMybatisPerson_multiThread\\src\\main\\resources\\Output\\person.txt";
        File file = new File(filename);

        String temp_str = null;

        /** 파일 존재시 삭제
         * 하나의 스레드가 돌때 : tatic final int gridSize = 2 이므로 전체row수 / 2 의 단위로 file write를 함.
         * 예를들어 전체 row가 10 / gridSize(2) = 5 이므로 5개 단위로 write를 하는데 체크를 안하고 file 삭제를 하게 되면
         * 5개 write후 삭제해버림 cnt_write로 체크할 것
         */
        if( file.exists() && this.cnt_write == 0){
            file.delete();
        }

        FileWriter file_w = new FileWriter(filename, true);

        /**
         * person객체에 대한 가공이 필요할땐 PartitonerProcessor.java -> process()에서 가공할 것
         * 여기선 file wirte를 위한 delimeter만 집어넣기때문에 (객체의 멤버가 아닌 문자열 추가이기때문에) 여기서 가공
         */
        for(Person person : persons ){
            if(person!=null){
            temp_str = null;
            temp_str = Integer.toString(person.getPersonId()) + "|" + person.getFirstName() + "|" + person.getLastName();
            file_w.write(temp_str);
            file_w.append("\n");
            this.cnt_write++;
            }
        }

        file_w.flush();   // buffer 비우기
        file_w.close();   // file descriptor clos
    }

}
