# 여러개의 job을 독립적으로 실행하는 방법 

###

- 사용방법

## 1.applicatio.yml에 설정
###  spring.batch.job.names: ${job.name:NONE}

    :Spring Batch가 실행될때, Program arguments로 job.name 값이 넘어오면 해당 값과 일치하는 Job만 실행하겠다는 것입니다.
여기서 ${job.name:NONE}을 보면 :를 사이에 두고 좌측에 job.name이, 우측에 NONE이 있는데요.
이 코드의 의미는 job.name이 있으면 job.name값을 할당하고, 없으면 NONE을 할당하겠다는 의미입니다.
중요한 것은! spring.batch.job.names에 NONE이 할당되면 어떤 배치도 실행하지 않겠다는 의미입니다.
즉, 혹시라도 값이 없을때 모든 배치가 실행되지 않도록 막는 역할입니다.


## 2. Edit Configuration 팝업에서 Program arguments에 설정
  --job.name=SimpleJob   
  
: jobBuilderFactory.get("simpleJob")의 이름과 동일해야 함 