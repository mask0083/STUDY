package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

// Repository 어노테이션이 있어야 함.
// @Controller + @Autowired(콘트롤러 등록) -> @Service(서비스등록) ->@Repository(저장소등록)  : 요 4개의 annotaion으로 스프링에 등록시켜줘야 스프링이 인식 가능
//@Repository ,  스프링빈에 직접 등록시 지워야 함.
public class MemoryMemberRepository implements MemberRepository{

    private static Map<Long,Member> store = new HashMap<>();
    private static Long sequence = 0L;

    @Override
    public Member Save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {

        return Optional.ofNullable(store.get(id)); // null 처리

    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream().filter(member ->  member.getName().equals(name)).findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }


    public void clearStore(){
        store.clear();
        System.out.println("clearStore");
    }
}
