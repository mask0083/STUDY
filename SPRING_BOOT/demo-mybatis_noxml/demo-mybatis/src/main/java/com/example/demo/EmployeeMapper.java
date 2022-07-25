package com.example.demo;

import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Insert("INSERT INTO employee(company_id, employee_name, employee_address) values(#{employee.companyId}, #{employee.name}, #{employee.address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("employee") Employee employee);

    @Select("select * from employee")
    @Results (id="employeeMap", value={
            @Result(property="companyId", column="company_id"),
            @Result(property="name", column="employee_name"),
            @Result(property="address", column="employee_address")

    })
    List<Employee> getAll();

    @Select("select * from employee where id=#{id}")
    @ResultMap("employeeMap")        // 위에서 선언한 Result를 재사용
    Employee getById(@Param("id") int id);

    @Select("select * from employee where company_id = #{companyId}")
    @ResultMap("employeeMap")        // 위에서 선언한 Result를 재사용
    List<Employee> getByCompanyId(@Param("companyId") int companyId);




}
