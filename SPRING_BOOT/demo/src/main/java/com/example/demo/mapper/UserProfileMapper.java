package com.example.demo.mapper;

import com.example.demo.model.UserProfile;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface UserProfileMapper {
    @Select("select * from UserProfile where id = #{id}")
    UserProfile getUserProfile(@Param("id") String id);

    @Select("select * from UserProfile")
    List<UserProfile> getUserProfileList();

    @Insert("insert into UserProfile values(#{id},#{name},#{phone},#{address})")
    int postUserProfile(@Param("id") String id, @Param("name") String name, @Param("phone") String phone, @Param("address") String address);

    @Update("update UserProfile set name=#{name}, phone=#{phone}, address=#{address}")
    int putUserProfile(@Param("id") String id, @Param("name") String name, @Param("phone") String phone, @Param("address") String address);


    @Delete("delete UserProfile where id = #{id}")
    int deleteUserProfile(@Param("id") String id);

}
