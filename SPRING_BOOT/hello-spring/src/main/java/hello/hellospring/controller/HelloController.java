package hello.hellospring.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    @GetMapping("hello") // http://localhost:8080/hello
    public String hello(Model model){
        model.addAttribute("data","hello");
        return "hello";
    }

    @GetMapping("hello-mvc") // http://localhost:8080/hello-mvc?name=spring
    public String helloMvc(@RequestParam("name") String aa, Model model){
    model.addAttribute("name",aa);
    return "hello-template";        // hello-template.html파일을 화면으로 리턴

    }


    // 여기서부터 API 방식
    @GetMapping("hello-string")
    @ResponseBody   // http의 boby부에 html이 아닌 데이터를 직접 내려준다는 의미
    public String helloString(@RequestParam("name") String name){
        return "hello"+name;    // string으로 리턴하면 StringConver 동작
    }


    @GetMapping("hello-API")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();

        hello.setName(name);
        return hello;// 객체로 리턴시 json converter 동작
    }


    static class Hello{
        private String  name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
