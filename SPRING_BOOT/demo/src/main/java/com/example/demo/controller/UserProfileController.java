/**
 * db mysql
 * create table
 * create table userProfile
 * (
 * id varchar(64) primary key,
 * name varchar(64) ,
 * phone varchar(64) ,
 * address varchar(128)
 * )
 *
 * db정보 : test@userProfile
 */



package com.example.demo.controller;
import com.example.demo.mapper.UserProfileMapper;
import com.example.demo.model.UserProfile;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserProfileController {


    private UserProfileMapper mapper;

    public UserProfileController(UserProfileMapper mapper) {
        this.mapper = mapper;
    }

//    private Map<String, UserProfile> userMap;
//    @PostConstruct
//    public void init(){
//        userMap = new HashMap<String, UserProfile>();
//        userMap.put("1", new UserProfile("1", "홍길동", "111-1111" ,"서울시 강남구") );
//        userMap.put("2", new UserProfile("2", "홍길자", "222-2222" ,"서울시 강서구") );
//        userMap.put("3", new UserProfile("3", "홍길순", "333-3333" ,"서울시 양천구")) ;
//    }

    @GetMapping("/user/{id}")
    public UserProfile getUserProfile(@PathVariable ("id") String id){
        //return userMap.get(id); // Map을 사용시
        return mapper.getUserProfile(id); // mysql 접속시
    }


    @GetMapping("user/all")
    public List<UserProfile> getUserProfileList(){
        //return new ArrayList<UserProfile>( userMap.values());
        return mapper.getUserProfileList();
    }

    @PostMapping("user/{id}")
    public void postUserProfile(@PathVariable("id") String id, @RequestParam("name") String name,@RequestParam("phone") String phone, @RequestParam("address") String address){

//        UserProfile userProfile = new UserProfile(id,name,phone,address);
//        userMap.put(id, userProfile);
        mapper.postUserProfile(id,name,phone,address);
    }

    @PutMapping("user/{id}")
    public void putUserProfile(@PathVariable("id") String id, @RequestParam("name") String name,@RequestParam("phone") String phone, @RequestParam("address") String address){
//        UserProfile userProfile  = userMap.get(id);
//        userProfile.setName(name);
//        userProfile.setPhone(phone);
//        userProfile.setAddress(address);

        mapper.putUserProfile(id,name,phone,address);

    }

    @DeleteMapping("user/{id}")
    public void deleteUserProfile(@PathVariable("id") String id){
        //userMap.remove(id);
        mapper.deleteUserProfile(id);
    }


}
