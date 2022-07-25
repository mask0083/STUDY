# Spring 스케줄러 

###

- 사용방법

1.Application에 어노테이션 설정
  
   #### :@EnableScheduling  : 스프링 스케줄링 활성화

2. 원래 TaskletJobConfig에 등록한 @Component를 없애고 대신 JobScheduler.java에 등록
   #### : 이제 JobScheduler가 TaskletJobConfig를 스케줄링에 따라 동작시킬거임

3. JobScheduler.java에 @Scheduled(fixedDelay = 30000) 설정


- 문제점

  1. job이나 step에서 사용자가 던진 parameter를 제대로 받을수 있는가?
  2. job이 늘어나면 스케줄도 job마다 다를텐데 그때마다 매번 메소드( JobScheduler->runJob()같은) 를 생성해야 하는가?
   



