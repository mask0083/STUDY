package com.example.demo;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CompanyMapper {

    @Insert("INSERT INTO company(company_name, company_address) values(#{company.name}, #{company.address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("company") Company company);

    @Select("select * from company")
    @Results (id="CompanyMap", value={
        @Result(property="name", column="company_name"),
        @Result(property="address", column="company_address"),
        @Result(property="employeeList", column="id", many=@Many(select="com.example.demo.EmployeeMapper.getByCompanyId"))

    })
    List<Company> getAll();

    @Select("select * from company where id=#{id}")
    @ResultMap("CompanyMap")        // 위에서 선언한 Result를 재사용
    Company getById(@Param("id") int id);



}
