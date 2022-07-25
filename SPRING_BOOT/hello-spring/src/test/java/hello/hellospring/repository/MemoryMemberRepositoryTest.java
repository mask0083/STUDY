package hello.hellospring.repository;

import hello.hellospring.domain.Member;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


public class MemoryMemberRepositoryTest {
    //MemberRepository repository = new MemoryMemberRepository();   // interface로 객체선언 가능. 연관된 class를 생성자로해서
    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach // method가 끝나고 무조건 호출
    public void afterEach(){
        System.out.println("afterEach...");
        repository.clearStore();

    }

    @Test
    public void save(){
        Member member = new Member();
        member.setName("TEST_NAME");
        repository.Save(member);

        Member result = repository.findById(member.getId()).get();

        System.out.println("result =  " + (result == member));
        System.out.println("member.getId() =  " + member.getId());
        System.out.println("member.getName() =  " + member.getName());
        System.out.println("member.=  " + member);

        assertThat(member).isEqualTo(result);
    }

    @Test
    public void findByName(){
        Member member1 = new Member();
        member1.setName("test-name1");
        repository.Save(member1);


        Member member2 = new Member();
        member2.setName("test-name2");
        repository.Save(member2);

        //Member f_mem1 = repository.findByName(member1.getName());
        Member f_mem1 = new Member();
        f_mem1 = repository.findByName(member1.getName()).get();

        System.out.println(" f_mem1.name =  " + f_mem1.getName());
        assertThat(f_mem1).isEqualTo(member1);
    }

    @Test
    public void findAll(){

        Member member1 = new Member();
        member1.setName("test-name1");
        repository.Save(member1);


        Member member2 = new Member();
        member2.setName("test-name2");
        repository.Save(member2);

        List<Member> result = repository.findAll();

        for(Member temp:result)
        {
            System.out.println(temp.getName());
        }


        assertThat(result.size()).isEqualTo(2);

    }

}
