# Spring Batch ALL IN ONE (mybatis + mysql) 

### db 준비 

create table person (
personID int(10) not null primary key,
firstname varchar(20),
lastname varchar(30)
);

create table person1 (
personID int(10) not null primary key,
firstname varchar(20),
lastname varchar(30)
)

insert into person (personID, firstname, lastname) values (1, 'BUMSU','KIM' );
insert into person (personID, firstname, lastname) values (2, 'HEEJAE','KANG' );
insert into person (personID, firstname, lastname) values (3, 'JIYONG','GO' );
insert into person (personID, firstname, lastname) values (4, 'JAHYUN','GU' );
insert into person (personID, firstname, lastname) values (5, 'ADAMS','MAC' );
insert into person (personID, firstname, lastname) values (6, 'DOHEE','NA' );
insert into person (personID, firstname, lastname) values (7, 'MIHO','GOO' );
insert into person (personID, firstname, lastname) values (8, 'HEEJUNG','MOO' );
insert into person (personID, firstname, lastname) values (9, 'HYUNSOO','GANG' );
insert into person (personID, firstname, lastname) values (10,'GUIVER','MAC' );
 
    
### 1. 실행방법
Edit Configuration 팝업창에서 program argument에 실행하고자 하는  jobname을 넣어서 기동

--job.name=jobtasklet1
--job.name=jobchunk1 ver=1 date=20220723

jobname은 Cont.java에 정의되어 있음. 

### 문제점 :
*     테스트 소스이므로 person 테이블을 읽어 person 테이블에 write하기 때문에 같은 트랜잭션으로 묶여 reader가 계속 호출됨.
*     processor()에서 firstname을 가공하므로 insert시 firstname(20) 사이즈 초과로 오류남. 걍 테스트니 넘어갈 것

### 2. jobs
2-1. jobtasklet1 : tasklet기반 배치. reader-processor-writer 없이 동작
                 : 반드시 mapper interface와 xml을 등록해야 함. 
                   interface : src\main\java\com\example\allinonebatch\mapper\PersonMapperDirect.java
                   xml : resources/mapper/PersonMapperDirect.xml

2-2. jobchunk1 : chunk 기반 배치. reader-processor-writer 사용
                 xml만 필요하고 interface는 필요없음. xml의 query ID를 직접 찾아가서 sql실행함.
                 xml : resources/mapper/PersonMapperChunk1.xml
2-3. jobchunk2 :  tasklet + chunk 혼합해서 사용
* 
  * jobchunk2_Step1  :tasklet방식
  * jobchunk2_Step2 :chunk 방식
  * 
  * 
  * !!중요!!
  * 동작은 문제없으나 jobchunk1Config에서 선언한 메소드 이름을 그대로 사용못함. ( 오류발생 )
  * public ->private 으로 변경해주면 private or final은 선언할수 없다고 오류.
  * 
  * 
  * ## jobchunk1_Step() 밑의 하위 메서드들의 이름을 다른 잡에서 중복해서 사용하려면 @Bean으로 등록하면 안됨.
* 
  * @Bean으로 등록시 defined in class path resource could not be registered 오류 발생함.
  * private으로 선언하면 private or final 은 선언불가 라고 오류 떨어짐.
  * 같은 메소드 이름을 다른 잡에서 중복해서 사용하고 싶으면 @Bean 삭제.(실행하는데 문제없음)
  * 
  * jobchunk2_Step1 : 기존 데이터 모두 삭제
  * 스프링배치가 실패시점의 데이터를 스텝에 저장해서 재실행시 실패한 데이터부터 다시 읽어온다고 하나 동작을 확실할수 없어
  * 그냥 기존 데이터 다 지우고 재실행시킴.
  * 
  * return RepeatStatus.FINISHED; 이후에 바로 commit되서 데이터 삭제됨.
  * jobchunk2_Step2 : 새로 insert



### 오류내역들 정리
1. jobchunk1을 실행시켰는데 (--job.name=jobchunk1) 다 돌고나서 갑자기 호출하지도 않은 jobtasklet1 -> step을 실행시킴.
   알고보니 application.yml에 잡설정이 잘못되어있었는데 그걸 모르고 break찍어가면서 죄다 뒤짐. 뒤지겠네..
   로그를 찍어보니 ( log.info("Jobchunk1_job start!!");, log.info("jobtasklet1_job start!!");)
   내부적으로 등록된 잡을 최초에 모두 실행시킨 후 +  파라미터로 던지지 않은 잡의 스텝만 실행을 안시키는 거였음.
   잣같이 만들어놨네... 진짜..........

2. LIMIT #{_skiprows}, #{_pagesize}

    pagesize : 한번의 조회로 가져올수 있는 data row 설정
    chunksize : 한번에 write할 수 있는 row 설정
    skiprows : 조회시 시작row 설정. default : 0
    pagesize와 chunksize는 동일하게 설정할 것.

  기본적으로 설정하는 사이즈들은 위와 같은데......

  return new MyBatisPagingItemReaderBuilder<Person>()
  .sqlSessionFactory(sqlSessionFactory)
  .queryId(s_qryId)
  .parameterValues(parameterValues)
  .pageSize(SKIP_CHUNK_PAGE_SIZE)
  .maxItemCount(3)
  .build();


  
여기서 사이즈 설정하는건...
.pageSize(SKIP_CHUNK_PAGE_SIZE)
.maxItemCount(3)
: 실제 요 두개고 이 값을 가지고 sql에 설정한 LIMIT #{_skiprows}, #{_pagesize}
  값을 셋팅함. 

  문제는... 
  데이터가 총 100개고
  pageSize(3)
  maxItemCount(3)
  으로 줘버리면 3개만 가져오고 배치가 종료됨. 데이터가 더 있지만 읽지를 않음.
  maxItemCount > pageSize 보다 크게 주면 해결되나 예를들어 10을 설정하면 딱 10개까지만 가져옴.(가져와야 되는 최대갯수 설정인듯함.)  
  maxItemCount : 이건 설정하지 말것. 설정하면 데이터가 천만건이라도 딱 설정된 값까지만 가져옴. 특수한 상황에서 사용. 

3. MyBatisCursorItemReader vs MyBatisPagingItemReader

   MyBatisCursorItemReader : cursor 기반 reader
   : 별도로 사이즈나 페이지 처리하는 세팅이 없는걸로 보임.
     커서가 커넥션 독점할 가능성 높음. 대용량처리시 배제할 것

   MyBatisPagingItemReader : paging 기반 reader.
   : page 단위로 처리하므로 커넥션을 독점하지 않고 사용가능( 대용량 처리에 유리 )


4. Cannot change the ExecutorType when there is an existing transaction 오류

   현상은 "Transaction내에서 ExecutorType이 변경될 수 없다” 이다.
   위 현상은 Batch 처리를 할 때 발생할 수 있다. 그 이유는 아래와 같다.

   MybatisPagingItemReader를 사용하면 내부적으로 SqlSessionTemplate이 생성될 때 ExecutorType.BATCH로 지정된다.
   만약 MybatisPagingItemReader에서 다른 쿼리를 날려보고자 한다면 기본적으로 ExecutorType.SIMPLE이기 때문에 문제가 될 수 있다.

     SpringBatch에서 ExecutorType에는 3가지가 있다.
    
     SIMPLE: executor가 별도의 작업을 하지 않음.
     REUSE: prepared statement 재사용
     BATCH: prepared statement 재사용 & 묶음 갱신
   
     Mybatis 기본 설정에서는 ExecutorType.SIMPLE이 기본이다. 따라서 CommonRepository.now()에서는 SIMPLE로 동작한다.
     하지만 MybatisPagingItemReader의 경우는 ExecutorType.BATCH로 SqlSessionTemplate을 생성하고 있다.
    
     Transaction 설정이 있다면 ExecutorType이 SIMPLE에서 BATCH로 변경되기 때문에 오류가 발생할 수 있다.
    
     해결방법 1 : application.yml에 다음과 같이 설정했으나 같은 오류발생
                mybatis:
                executor-type=BATCH:
                mybatis:
               configuration:
               default-executor-type=BATCH:
            ## 2 :sqlSessionFactory.openSession().selectOne() , selectList()사용해서 해결

5. FlatFileItemWriterBuilder 사용시 
     오류메세지 : org.springframework.batch.item.WriterNotOpenException: Writer must be open before it can be written to
     @Bean 과 @StepScope으로 ItemWriter<>을 구현하고 FlatFileItemWriter로 리턴하는게 원인
     원래라면 ItemWriter는 ItemStream으로 리턴되어도 상관없지만, 위에 @Bean과 @StepScope를 같이 사용해서 Bean이 proxy로 설정되어 FlatFileItemWriter에서 지원하는 open() 메서드가 
     동작을 안하게 되어 발생되는문제.
     ItemWriter로 반환하려면 @Bean이나 @StepScope 둘 중 하나를 삭제한다. (proxy 로 지정되는 부분을 삭제)

     @StepScope를 삭제하니  writer(writerTOFlatFile()) 호출부를 주석으로 막아놔도 잡시작시에 강제로 호출이 됨.
     @Bean을 삭제하니 잡시작시에 자동으로 돌고 + processor()다음에 writerTOFlatFile()로 넘어가지를 않음.
     근데 골때리는건 @Bean을 삭제하던 @StepScope를 삭제하던 ... 파일에 write는 정상적으로 함......
     골패네....
    
     오류해결 : 리턴값이 ItemWriter라서 FlatFileItemWriter에서 지원하는 open() 메서드가 동작을 안하는게 원인이므로
     리턴을 FlatFileItemWriter로 바꿔주면 됨.
     잡시작시에 자동으로 먼저 호출되지도 않고 reader-processor-writer순으로 동작도 정상적임. @Bean이나 @StepScope도 그대로 선언 가능.