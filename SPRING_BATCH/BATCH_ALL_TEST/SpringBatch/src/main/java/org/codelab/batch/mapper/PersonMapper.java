package org.codelab.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.codelab.batch.dto.Person;

@Mapper
public interface PersonMapper {
	List<Person> getPerson();
	int insertPerson(Person person);
}
