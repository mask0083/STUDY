package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;


class MemberServiceTest {


    //MemberService memberService = new MemberService();
    //MemoryMemberRepository memoryMemberRepository = new MemoryMemberRepository();

    MemoryMemberRepository memoryMemberRepository;
    MemberService memberService;


    @BeforeEach
    public void beforeEach(){
        System.out.println("Before Each...");
        memoryMemberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memoryMemberRepository);

    }


    @AfterEach // method가 끝나고 무조건 호출
    public void afterEach(){
        System.out.println("afterEach...");
        memoryMemberRepository.clearStore();

    }

    @Test
    void join() {

        Member member1 = new Member();
        member1.setName("member1");


        Long id = memberService.Join(member1);

        System.out.println("id = " + id);

        Optional<Member> one = memberService.findOne(id);
        Long oneId = one.get().getId();

        Assertions.assertThat(oneId).isEqualTo(id);


    }
    @Test
    public void 중복회원예외(){
        Member member1 = new Member();
        member1.setName("member1");

        Member member2 = new Member();
        member2.setName("member1");

        memberService.Join(member1);
        try {
            memberService.Join(member2);
            //fail();
        }catch(IllegalStateException e){

            System.out.println("e = " + e.getMessage());

        }




    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}