package hello.hellospring;

import hello.hellospring.repository.JdbcTemplateMemberRepository;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import hello.hellospring.repository.MyBatisMemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

// 스프링빈에 직접 등록시 설정파일
@Configuration
public class SpringConfig {

    private DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository(){

        // MemberRepository가 interface이므로 db같은 저장매체가 변경되면 interface부는 그대로 두고
        // 구현체만 변경후(  MemoryMemberRepository -> JdbcTemplateMemberRepository)
        // interface 호출부는 그대로 호출하면 됨.
        //return new MemoryMemberRepository();    // 인터페이스 말고 실제 구현체 리턴.
       // return new JdbcTemplateMemberRepository(dataSource);
        return new MyBatisMemberRepository();

    }


}
