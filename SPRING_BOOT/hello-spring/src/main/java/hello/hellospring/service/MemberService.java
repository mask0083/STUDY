package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

//@Service    // 이게 있어야 MemberService를 스프링에 등록,  스프링빈에 직접 등록시 지워야 함.
public class MemberService {

    //private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final MemberRepository memberRepository;



    //@Autowired // 스프링에 생성자 등록,  스프링빈에 직접 등록시 지워야 함.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //회원가입
    public Long Join(Member member){

        //같은 이름이 있는 중복회원은 안됨.
//        Optional<Member> result = memberRepository.findByName(member.getName());
//        if(null==result && result.get().getId()==0){
//            throw new IllegalStateException("이미 존재하는 회원입니다");
//        }


//        memberRepository.findByName(member.getName()).ifPresent(m-> { throw new IllegalStateException("aa"); } );

        //1.중복회원 검증
        validateDuplicateMember(member);
        memberRepository.Save(member);
        return member.getId();

    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> result = memberRepository.findByName(member.getName());
        result.ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        });
    }

    /**
     * 전체회원 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
   }

   public Optional<Member> findOne(Long memberId){
       return memberRepository.findById(memberId);
   }


}
