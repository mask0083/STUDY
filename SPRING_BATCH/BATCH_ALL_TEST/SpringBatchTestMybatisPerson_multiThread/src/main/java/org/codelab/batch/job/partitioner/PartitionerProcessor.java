package org.codelab.batch.job.partitioner;

import org.codelab.batch.dto.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PartitionerProcessor implements ItemProcessor<Person, Person> {

    // LoggerFactory.getLogger(PartitionerProcessor.class) 로 호출하면 내부적으로 클래스를 두번 호출해서 로그가 두번찍힘.
    // 여기 코드에서는 그럴일 없으나 (void main()부가 아니므로) 되도록 this.getClass().getSimpleName() 사용해볼 것.
    //private static final Logger log = LoggerFactory.getLogger(PartitionerProcessor.class);
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private String threadName;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Person process(Person item) throws Exception {
        logger.info("ThreadName :{}, id:{}, lastName:{}",threadName, item.getPersonId(), item.getLastName());

        //Person 객체의 가공이 필요할때는 여기서 할 것

        return item;
    }
}
