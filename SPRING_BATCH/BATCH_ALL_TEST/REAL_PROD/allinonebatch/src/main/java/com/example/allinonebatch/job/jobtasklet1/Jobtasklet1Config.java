package com.example.allinonebatch.job.Jobtasklet1;

import com.example.allinonebatch.common.Const;
import com.example.allinonebatch.dto.Person;
import com.example.allinonebatch.mapper.PersonMapperDirect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 실행방법 :
 *      Edit Configuration -> program argument : --job.name=jobtasklet1
 */

@Slf4j
@Configuration
public class Jobtasklet1Config {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PersonMapperDirect personMapperDirect;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;



    @Bean(name = Const.JOBTASKLET1)
    public Job jobtasklet1_job() {

        log.info("jobtasklet1_job start!!");


        return jobBuilderFactory.get(Const.JOBTASKLET1)
                .incrementer(new RunIdIncrementer())	// RunIdIncrementer : 동일파라미터라도 재실행이 가능
                .start(jobtasklet1_Step())
                .build();
    }

    @Bean
    public Step jobtasklet1_Step() {

        /** 방법 1. TaskletStep.java -> Tasklet 인터페이스의 execute()메소드 오버라이딩 후 호출
         * tasklet(taskletStep)으로 호출시
         */
//		return stepBuilderFactory.get("taskletStep")
//				.tasklet(taskletStep)
//				.build();

        /**방법 2. : 기본 tasklet()메소드 사용
         *  단 반드시 return RepeatStatus.FINISHED; 필요. return안하면 오류남.
         *  contribution: A, chunkContext B
         */
        return stepBuilderFactory.get("jobtasklet1_Step").tasklet((A, B) -> {
//            log.info("A.toString() = " + A.toString() + " B.toString() = " + B.toString());
//            log.info("A.getExitStatus() = " + A.getExitStatus() );
//            log.info("A.getReadCount() = " + A.getReadCount() );
//            log.info("B.getStepContext() = "+B.getStepContext());
//            log.info("B.getClass() = "+B.getClass());

            log.info("jobtasklet1_Step start!!");


            /**
             * 1. mybatis 제공 라이브러리 이용하지 않고 직접 접근.
             */
            DirectReadAndWrite();

            /**
             * 2. SqlSessionFactory 사용
             * SqlSessionFactory 단독 사용이 아니라 MyBatisPagingItemReader 와 MyBatisCursorItemReader 와 MyBatisBatchItemWriter와 혼합해서 사용함.
             * : 이 방식은 tasklet()에서는 안먹는 듯함.
             */
            //InDirectReadAndWrite();


            return RepeatStatus.FINISHED;
        }).build();

    }

    private void InDirectReadAndWrite() {
//
//        int skipRows = 0;
//        int pageSize = 10;
//
//        MyBatisPagingItemReader<Person> reader = new MyBatisPagingItemReader<>();
//        reader.setSqlSessionFactory(sqlSessionFactory);
//        reader.setQueryId("getPersonPaging");
//        reader.setCurrentItemCount(skipRows);   // 읽기를 시작할 카운트. 초기는 0
//        reader.setMaxItemCount(skipRows+pageSize);
//        reader.setPageSize(pageSize);
//
//        Person person = new Person();
//
//        for(int i=0;i<reader.read().)
//
//        for(Person p:reader){
//            log.info("p.getPersonId = " + p.getPersonId());
//            log.info("p.getFirstName = " + p.getFirstName());
//            log.info("p.getLastName = " + p.getLastName());
//        }

//        DataSource dataSource = new DataSource();
//        ApplicationContext applicationContext = new ApplicationContext();
//
//        sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mappers/*.xml"));
//        SqlSessionFactory sqlSessionFactory;
//        sqlSessionFactory =  sqlSessionFactoryBean.getObject();




//        Map<String, Object> parameterValues = new HashMap<>();
//        parameterValues.put("skiprows", 0);
//        parameterValues.put("pagesize", 10);
//        new MyBatisPagingItemReaderBuilder<Person>()
//                .pageSize(10)
//                .sqlSessionFactory(sqlSessionFactory)
//                .queryId("getPersonPaging")
//                .parameterValues(parameterValues)
//                .build();



    }

    public void SelectOne(){

        //0. 단건 PERSON 객체로 받아옴
        Person resultOne = new Person();
        Integer Id = 1;

        resultOne = personMapperDirect.getPersonbyIdOne(Id);

        //0-1. 단건 count 조회
        Integer personCnt = 0;
        personCnt = personMapperDirect.getPersonCnt();

    }

    /**
     * sqlSessionTemplate의 selectOne selectList 사용시에는 인터페이스 매퍼가 아닌 xml을 직접 호출하여 가져옴.
     */
    public void SelectOne_List(){
        Integer cnt1=0;
        cnt1 = sqlSessionTemplate.selectOne("com.example.allinonebatch.mapper.PersonMapperDirect.getPersonCnt");


        Person p = new Person();
        Integer Id = 1;
        p = sqlSessionTemplate.selectOne("com.example.allinonebatch.mapper.PersonMapperDirect.getPersonbyIdOne", Id);


        /**
         * selectList 사용시 파라미터를 두개 이상 던질수가 없으므로 getPersonbyParam 같은 형식은 사용불가
         * hashmap 형태로 파라미터를 던져야함.
         */
        List<Person> list = new ArrayList<>();
        Map<String,Object> param = new HashMap<>();
        param.put("Id",0);
        param.put("lastname","KIM");

        list = sqlSessionTemplate.selectList("com.example.allinonebatch.mapper.PersonMapperDirect.getPersonbyHash", param);

        for(Person pp:list){
            log.info("pp = " + pp.toString());
        }
    }



    /**
     * 1. mybatis 제공 라이브러리 이용하지 않고 직접 접근.
     *  : 트랜잭션 관리됨.
     *  :
     *  : paging 처리 : mysql로 테스트하느라 rownum이 안먹어서 귀찮아서 안함. 대충 객체에 pagecount 변수 하나 선언해서 넘기면 될듯/
     */
    private void DirectReadAndWrite() {

        // 1. 단건 테스트
        SelectOne();


        // 2. sqlSessionTemplate : selectOne, selectList 사용해서 가져오기
        SelectOne_List();


        //1.person객체를 인자로 던져서 select
        List<Person> result = new ArrayList<>();
        Person person = new Person();
        person.setPersonId(0);
        result = personMapperDirect.getPerson(person);


        int cnt = 0;
        for(Person p:result){
            log.info("id = "+ p.getPersonId());
            log.info("firstname = "+ p.getFirstName());
            log.info("lastname = "+ p.getLastName());

            /**
             *  트랜잭션 롤백되는지 테스트. : 6번째부터 익셉션 발생후 롤백됨.
             */
//
//            if( cnt < 5) {
//                p.setPersonId(cnt+11);
//                p.setFirstName(p.getFirstName() + "_1");
//                p.setLastName(p.getLastName() + "_2");
//
//                log.info("id = "+ p.getPersonId());
//                log.info("firstname = "+ p.getFirstName());
//                log.info("lastname = "+ p.getLastName());
//
//                cnt++;
//            }

            p.setPersonId(cnt+11);
            p.setFirstName(p.getFirstName() + "_1");
            p.setLastName(p.getLastName() + "_2");
            cnt++;

            //insert
           // personMapperDirect.insertPerson(p);
    }


        //2.변수를 인자로 던져서 select
        Integer Id = 10;
        List <Person> result1 = personMapperDirect.getPersonbyId(Id);

        log.info("getPersonbyId start");

        for(Person p1:result1){

            log.info("id = "+ p1.getPersonId());
            log.info("firstname = "+ p1.getFirstName());
            log.info("lastname = "+ p1.getLastName());
        }


        /**
         * 3. hashmap을 이용해서 다중 파라미터 던져서 select
         * lastname을 like로 검색해서 가져올때 xml에
         * CDATA 로 sql문장을 감쌀것. 안그러면 특수문자때문에 like검색이 안먹음.... mybatis 문법.. 잣같은....
         */

        HashMap<String,Object> map = new HashMap<>();
        map.put("Id", 0);
        map.put("lastname","KIM");
        //map.put("lastname","'%KIM%'");

        List<Person> result_hash = personMapperDirect.getPersonbyHash(map);

        log.info("-------- result hash ----------");
        for(Person p2:result_hash){
            log.info("id = "+ p2.getPersonId());
            log.info("firstname = "+ p2.getFirstName());
            log.info("lastname = "+ p2.getLastName());
        }

        // 4. @Param 어노테이션 이용해서 인자 전달
        log.info("-------- result PARAM ----------");
        List<Person> result_param = personMapperDirect.getPersonbyParam(0,"KIM");

        for(Person p3:result_param){
            log.info("id = "+ p3.getPersonId());
            log.info("firstname = "+ p3.getFirstName());
            log.info("lastname = "+ p3.getLastName());
        }



    }

}
