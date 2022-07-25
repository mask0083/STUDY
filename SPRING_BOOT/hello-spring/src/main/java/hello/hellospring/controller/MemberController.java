package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller //  스프링빈에 직접 등록시에도 지우면 안됨. (controller의 anntation은 지우면 안됨.)
public class MemberController {

    private final MemberService memberService;

    // 스프링 컨테이너에 MemberController를 등록시킴. MemberService를 등록하는게 아닌 MemberController를 등록
    // MemberService는 별도로 @Service로 등록시켜줘야 함.
    @Autowired //  스프링빈에 직접 등록시에도 지우면 안됨
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members/new") // 호출 url http://localhost:8080/members/new
    public String createForm(){
        return "members/createMemberForm"; // 리턴 html
    }

    // Q:MemberForm 객체를 어떻게 알고 자동으로 받아들이는거지?
    // name이란 이름이 동일하면 그냥 String name변수뿐 아니라 객체로 선언해도 가져올수 있는듯
    // 마찬가지로 job이란 이름으로 하나더 선언해서 form 클래스안에 넣으면 가져옴. 이름으로 구분하는 듯
    @PostMapping("/members/new")        // 호출 url http://localhost:8080/members/new
    public String create(MemberForm form, String name, String job ){
        Member member = new Member();
        member.setName(form.getName());

        memberService.Join(member);

        System.out.println("name = " + name);
        System.out.println("job = " + job);

        System.out.println("form.getname = " + form.getName());
        System.out.println("form.getJob = " + form.getJob());



        return "redirect:/"; //   리턴 html
    }


    @GetMapping("/members")
    public String List(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";    // 리턴 html
    }



}
