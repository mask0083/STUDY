# Spring Batch + mybatis + mysql + multithread


### 0. 개요
- 멀티스레드를 이용해 병렬처리 
- person 테이블을 읽어와 file write 
    
### 1. 문제점 1
 - 병렬처리시 여러개의 스레드가 동시에 write시 순서가 뒤죽박죽 섞이는 문제
   두개의 스레드를 실행 + 총데이터 10건 + pagesize 5일때
 - 스레드2(6~10번 데이터)가 먼저 write를 해버려서 file write된 순서가 6~10 + 1~5 순으로 되어버림. 
 - 해결점 못찾음. 찾아볼 것 

### 2. 문제점 2
- RangePartitioner.java : int total = sqlSessionTemplate.<Integer> selectOne(queryId, param);
- 여기서 selectOne()으로 단건씩 가져오므로 db를 계속 접근. 비효율적 