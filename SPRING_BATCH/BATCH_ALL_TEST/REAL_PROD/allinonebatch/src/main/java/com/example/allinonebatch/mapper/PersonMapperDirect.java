package com.example.allinonebatch.mapper;

import com.example.allinonebatch.dto.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface PersonMapperDirect {

    List<Person> getPerson(Person person);
    List<Person> getPersonbyId(Integer Id);

    Person getPersonbyIdOne(Integer Id);

    Integer getPersonCnt();

    //param 이용
    List<Person> getPersonbyParam(@Param("Id") Integer Id, @Param("lastname") String lastname);
    /**
     * 다중 parameter를 던져야 할때는 hashmap사용
     */
    List<Person> getPersonbyHash(HashMap<String, Object> map);


    int insertPerson(Person person);

}
