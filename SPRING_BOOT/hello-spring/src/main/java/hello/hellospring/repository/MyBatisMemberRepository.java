package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class MyBatisMemberRepository implements  MemberRepository {


    MemberService memberServiceMapper;
    @Override
    public List<Member> findAll() {
    return memberServiceMapper.findMembers();

    }

    @Override
    public Member Save(Member member) {
        return null;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findByName(String name) {
        return Optional.empty();
    }
}
