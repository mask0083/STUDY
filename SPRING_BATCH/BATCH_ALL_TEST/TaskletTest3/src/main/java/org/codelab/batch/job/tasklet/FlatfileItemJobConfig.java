package org.codelab.batch.job.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codelab.batch.dto.Customer;
import org.codelab.batch.dto.CustomerFieldSetMapper;
import org.codelab.batch.dto.CustomerFileLineTokenizer;
import org.codelab.batch.dto.CustomerJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;


/**
 * 스프링에서는 일반적으로 컴포넌트 스캔을 사용해 자동으로 빈을 등록하는 방법을 이용한다.
 * 하지만 @Bean 어노테이션을 사용해 수동으로 빈을 등록해야 하는 경우도 있다.
 * 대표적으로 다음과 같은 경우에 @Bean으로 직접 빈을 등록해준다.
 * 개발자가 직접 제어가 불가능한 라이브러리를 활용할 때
 * 애플리케이션 전범위적으로 사용되는 클래스를 등록할 때
 * 다형성을 활용하여 여러 구현체를 등록해주어야 할 때
 *
 *
 * 간단하게 @Component를 이용해 스프링 컨테이너에 빈을 자동등록
 * 또는 아래처럼 @Configuration + @Bean 을 통해 수동으로 빈을 등록
 * 반드시 @Configuration + @Bean 둘다 있어야 함.
 */

@Slf4j
@Configuration
@RequiredArgsConstructor

public class FlatfileItemJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Bean
    public Job flatFileItemJob(){
        //RunIdIncrementer : 같은 파라미터의 배치 재실행 가능
        return jobBuilderFactory.get("flatFileItemJob")
                .incrementer(new RunIdIncrementer())
                .start(flatFileItemStep()).build();


    }


    @Bean
    public Step flatFileItemStep(){

        /**
         * Q> reader()에서 읽은 파일을 별도 작업없이 writer()를 호출하기만해도 읽은 파일의 내용이 자동으로 writer()로 전달되는듯함.
         *    신기하긴 한데.. 여러개의 파일을 읽어서 하나의 파일로 합치는 경우는 어떻게 해야되나?????
         *
         */



        /**
         * 1. flatFileItemReaderStep() : FlatFileItemReaderBuilder 사용
         * 2. flatFileItemReaderStep2() : delimited 사용
         * 3. flatFileItemReaderStep3() : FieldSetMapper 클래스의 mapFieldSet()메서드를 overriding해서 수동 매핑
         *                                CustomerFieldSetMapper 클래스 선언 + mapFieldSet
         *                                결과물 파일로 출력.
         * 4. flatFileItemReaderStep4() : 커스텀레코드 파싱 : LineTokenizer 구현체를 직접 만들어 원하는 대로 각 레코드를 파싱할 수 있다.
         *  => 오류남... 알길이 없네...
         *  오류: No LineTokenizer implementation was provided.
         *
         *  5. flatFileItemReaderStep5() : json 파일 read : 단순 json 파일 (sub 배열없음)
         *  6. flatFileItemReaderStepJson() : json 파일 read : 다중 json 파일 (sub 배열있음)
         *
         *
         *  flatFileItemWriter_FileWrite : 실제 flat file write
         */


        // 1~5번까지는 요걸로 테스트
        return stepBuilderFactory.get("flatFileItemStep").<Customer,Customer>chunk(1)
                .reader(flatFileItemReaderStep3())
                //.write(flatFileItemWriterStep()) // print만 함.실제 file 생성 안함.
                .writer(flatFileItemWriter_FileWrite())
                .build();

        // 6번 다중 json 파일 실행시
//        return stepBuilderFactory.get("flatFileItemStep").<CustomerJson,CustomerJson>chunk(1)
//                .reader(flatFileItemReaderStepJson())
//                .writer(flatFileItemWriterStepJson())
//                .build();


    }

    @Bean
    @StepScope  // step에서 호출하는 하위메서드에서 Step의 파라미터를 받아오고 싶을때
    public FlatFileItemReader<Customer> flatFileItemReaderStep(){

        /**
         * ClassPathResource()는 클래스파일이 위치한 상대경로임. 띠바.. 이거때메 몇시간을 날렸네...
         *
         */
        //Resource inputFile = new ClassPathResource("Input/customerFixedWidth.txt");
         Resource inputFile = new ClassPathResource("Input\\customerFixedWidth.txt"); // 이것도 가능

        String []arrtemp = {"firstName", "middleInitial", "lastName",
                "addressNumber", "street", "city", "state", "zipCode"};

        logger.info( "inputFile = " + inputFile);


        /**
         * 구분자가 없는 flat 파일을 읽을때는 fixedLength() + columns() 조합으로 읽어와야 함.
         */
        return new FlatFileItemReaderBuilder<Customer>()
                .name("FlatFileItemReaderStep")
                .resource(inputFile)
                //.strict(false)
                .fixedLength()
                /* Range 객체 (파싱해야할 칼럼의 시작 위치와 종료 위치를 나타낸다) 의 배열 지정 */
                .columns(new Range[]{new Range(1, 11), new Range(12, 12), new Range(13, 22),
                        new Range(23, 26), new Range(27, 46), new Range(47, 62),
                        new Range(63, 64), new Range(65, 69)})
//                .columns(new Range(1,11), new Range(12,12), new Range(13,22), new Range(23,26),new Range(27,46), new Range(47,62)
//                        ,new Range(63,64),new Range(65,69))

                /**
                 * .names(String[] names) 이므로 아래처럼 문자열 그룹으로 넣게 되면 오류임.  arrtemp 배열로 선언해서 날리거나 new String[] 배열로 선언후 날릴 것
                 */
//                .names("firstName", "middleInitial", "lastName",
//                        "addressNumber", "street", "city", "state", "zipCode")
                //.names(arrtemp)
                .names(new String[] { "firstName", "middleInitial", "lastName",
                        "addressNumber", "street", "city", "state", "zipCode"} )
                .targetType(Customer.class)
                .build();
    }


    /**
     * delimited() 사용 : customerFixedWidthDelimited.txt로 테스트해야 함.
     * delimited() 사용시는 fixedLength(), columns() 필요없음.
     */

    @Bean
    @StepScope  // step에서 호출하는 하위메서드에서 Step의 파라미터를 받아오고 싶을때
    public FlatFileItemReader<Customer> flatFileItemReaderStep2(){

        /**
         * ClassPathResource()는 클래스파일이 위치한 상대경로임. 띠바.. 이거때메 몇시간을 날렸네...
         *
         */
        //Resource inputFile = new ClassPathResource("Input/customerFixedWidth.txt");
        Resource inputFile = new ClassPathResource("Input\\customerFixedWidthDelimited.txt"); // 이것도 가능

        logger.info( "flatFileItemReaderStep2 >> inputFile = " + inputFile);

        String deliStr = "|";

        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFileItemReaderStep2")
                .resource(inputFile)
                .delimited().delimiter(deliStr)    // default : ','로 구분함.
                /* Range 객체 (파싱해야할 칼럼의 시작 위치와 종료 위치를 나타낸다) 의 배열 지정 */
                /**
                 * .names(String[] names) 이므로 아래처럼 문자열 그룹으로 넣게 되면 오류임.  arrtemp 배열로 선언해서 날릴 것.
                 */
//                .names("firstName", "middleInitial", "lastName",
//                        "addressNumber", "street", "city", "state", "zipCode")
                //.names(arrtemp)
                .names(new String[] { "firstName", "middleInitial", "lastName",
                        "addressNumber", "street", "city", "state", "zipCode"} )
                .targetType(Customer.class)
                .build();
    }

    /**
     * CustomerFieldSetMapper 선언시 : FieldSetMapper 클래스의 mapFieldSet()메서드를 overriding해서 수동 매핑
     * CustomerFieldSetMapper 클래스 선언 + mapFieldSet
     * 기존 targetType(Customer.class) 대신에
     * fieldSetMapper(new CustomerFieldSetMapper()) 사용해서 수동매핑함
     *
     *
     * 다음의 경우에 사용하기 괜춘
     * 1. read한 파일의 내용을 직접 로그로 확인하고 싶을때 : 보통 FlatFileItemReaderBuilder의 모든 메서드가 return문 안에 들어가있어 확인이 어려우므로
     * 2. 읽어온 데이터를 가공하고 싶을때 : 클래스안에 재정의한 mapFieldSet()메소드안에서 가공하는게 .. 이게 맞는 방법인지는 모르겠으나..가능은함.
     *    예를들어 FirstName에 FirstName + MiddleName + lastName을 합쳐서 넣어버릴수도 있음.
     *
     */

    @Bean
    @StepScope  // step에서 호출하는 하위메서드에서 Step의 파라미터를 받아오고 싶을때
    public FlatFileItemReader<Customer> flatFileItemReaderStep3(){

        Resource inputFile = new ClassPathResource("Input\\customerFixedWidthDelimited.txt"); // 이것도 가능

        logger.info( "flatFileItemReaderStep3 >> inputFile = " + inputFile);

        String deliStr = "|";

        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFileItemReaderStep3")
                .resource(inputFile)
                .delimited().delimiter(deliStr)    // default : ','로 구분함.
                .names(new String[] { "firstName", "middleInitial", "lastName",
                        "addressNumber", "street", "city", "state", "zipCode"} )
                //.targetType(Customer.class) <= 요거 대신
                .fieldSetMapper(new CustomerFieldSetMapper()) // <= 요거 사용
                .build();


    }





    /**
     * 커스텀레코드 파싱 : LineTokenizer 구현체를 직접 만들어 원하는 대로 각 레코드를 파싱할 수 있다.
     * CustomerFileLineTokenizer 클래스 선언 + LineTokenizer의 FieldSet() 메서드 오버라이딩
     */

    @Bean
    @StepScope  // step에서 호출하는 하위메서드에서 Step의 파라미터를 받아오고 싶을때
    public FlatFileItemReader<Customer> flatFileItemReaderStep4(){

        Resource inputFile = new ClassPathResource("Input\\customerFixedWidthDelimited.txt"); // 이것도 가능

        logger.info( "flatFileItemReaderStep4 >> inputFile = " + inputFile);

        String deliStr = "|";

        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFileItemReaderStep4")
                .lineTokenizer(new CustomerFileLineTokenizer(new DefaultFieldSetFactory()))
                //.lineTokenizer(new CustomerFileLineTokenizer())
                .resource(inputFile)
                .targetType(Customer.class)
                .build();

    }




    /**
     * json 파일 read
     *
     */

    @Bean
    @StepScope  // step에서 호출하는 하위메서드에서 Step의 파라미터를 받아오고 싶을때
    public JsonItemReader<Customer> flatFileItemReaderStep5() {

        Resource inputFile = new ClassPathResource("Input\\customer.json"); // 이것도 가능
        logger.info( "flatFileItemReaderStep5 >> inputFile = " + inputFile);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

        JacksonJsonObjectReader<Customer> jsonObjectReader = new JacksonJsonObjectReader<>(Customer.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Customer>()
                .name("flatFileItemReaderStep5")
                .jsonObjectReader(jsonObjectReader)
                .resource(inputFile)
                .build();

    }




    /**
     * json 파일 read
     *
     */

    @Bean
    @StepScope  // step에서 호출하는 하위메서드에서 Step의 파라미터를 받아오고 싶을때
    public JsonItemReader<CustomerJson> flatFileItemReaderStepJson() {

        Resource inputFile = new ClassPathResource("Input\\customerJson.json"); // 이것도 가능
        logger.info( "flatFileItemReaderStepJson >> inputFile = " + inputFile);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

        JacksonJsonObjectReader<CustomerJson> jsonObjectReader = new JacksonJsonObjectReader<>(CustomerJson.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<CustomerJson>()
                .name("flatFileItemReaderStepJson")
                .jsonObjectReader(jsonObjectReader)
                .resource(inputFile)
                .build();



    }



    /**
     * 실제 file write는 하지 않고 출력만 함.
     * @return
     */
    @Bean
    public ItemWriter<Customer> flatFileItemWriterStep(){
        return (items -> items.forEach(System.out::println));



    }



    /**
     *
     * @return
     */
    @Bean
    public ItemWriter<Customer> flatFileItemWriter_FileWrite(){
        //return (items -> items.forEach(System.out::println));

        /**
         * write 할때는 반드시 이렇게 사용
         */
        Path outputFilePath = Paths.get("src\\main\\resources\\Output", "CustomerStep3_formating.txt");
        Resource outputFile = new FileSystemResource(outputFilePath.toFile());

        logger.info( "flatFileItemWriter_FileWrite >> outputFile = " + outputFile);

        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFileItemWriter_FileWrite")
                .resource(outputFile)
                .formatted()
                .format("%s %s %s lives at %d %s %s in %s %s.") // text 포맷팅
                .names(new String[] {"firstName", "middleInitial","lastName", "addressNumber", "street", "city", "state", "zipCode"}) // 순서대로
                .build();


        // delimited 사용시
//        return new FlatFileItemWriterBuilder<Customer>()
//                .name("customerItemWriterWithDelimiter")
//                .resource(new FileSystemResource(outputFile))
//                .delimited() // 구분자 설정
//                .delimiter(";") // ; 로 구분
//                .names(new String[] {"firstName", "middleInitial","lastName", "addressNumber", "street", "city", "state", "zipCode"})
//                .build();


    }




    /**
     *
     * @return
     */
    @Bean
    public ItemWriter<CustomerJson> flatFileItemWriterStepJson(){
        //return (items -> items.forEach(System.out::println));


        /**
         *
         * json파일을 읽어들일때는 ClassPathResource()의 경로를 잘 찾아와 읽는데(resources/Input).....
         * json파일을 생성할때는 ClassPathResource() 여기서 경로를 못읽어들여 오류가 남 (Could not convert resource to file: [class path resource [Output/CustomerJsonWR.json]])
         *
         * 파일 생성시에는 반드시 FileSystemResource()를 사용할 것. FileSystemResource()는 writable이 가능. ClassPathResource() 는 불가능
         * 실예로 ClassPathResource()를 사용하고 write할 폴더밑에 write할 파일을 강제로 넣어두면 또 오류안남.
         * 변태같은 놈이 만든건가봄
         */




        JsonFileItemWriterBuilder<CustomerJson> builder = new JsonFileItemWriterBuilder<>();
        JacksonJsonObjectMarshaller<CustomerJson> marshaller = new JacksonJsonObjectMarshaller<>();

        /**
         * 요거는 쓰면 안됨 ClassPathResource()
         */
//        Resource outputFile = new ClassPathResource("Output\\CustomerJsonWR.json");
//        logger.info( "flatFileItemWriterStepJson >> outputFile = " + outputFile);
//        return new JsonFileItemWriterBuilder<CustomerJson>()
//                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
//                //.resource(new ClassPathResource("Output\\customerJson.json"))
//                .resource(outputFile)
//                .name("flatFileItemWriterStepJson")
//                .build();


        /**
         * write 할때는 반드시 이렇게 사용
         */
        Path outputFilePath = Paths.get("src\\main\\resources\\Output", "CustomerJsonWR.json");
        Resource outputFile = new FileSystemResource(outputFilePath.toFile());

        logger.info( "flatFileItemWriterStepJson >> outputFile = " + outputFile);
        return new JsonFileItemWriterBuilder<CustomerJson>()
                .name("flatFileItemWriterStepJson")
                .resource(outputFile)
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();


    }




}
