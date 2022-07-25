/**
 * -- create database mybatis_db default character set utf8mb4 default collate utf8mb4_general_ci;
 * -- create user 'mybatis'@'%' identified by '3800ksam!';
 * -- grant all on mybatis_db.* to 'mybatis'@'%';
 * -- use mybatis_db;
 *
 * -- create table company(
 * -- id INTEGER auto_increment primary KEY,
 * -- company_name VARCHAR(128),
 * -- company_address VARCHAR(128),
 * -- INDEX (company_name)
 * -- )
 */

/**
 * create table employee(
 * id INTEGER AUTO_INCREMENT PRIMARY KEY ,
 * company_id INTEGER,
 * employee_name VARCHAR(128),
 * employee_address varchar(128),
 * INDEX(employee_name),
 * foreign key(company_id) REFERENCES company(id)
 * )
 */

package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyService companyService;

    @PostMapping("")
    public Company post(@RequestBody Company company) throws Exception{
        //return companyMapper.insert(company);
        //companyMapper.insert(company);

        companyService.add(company);    // db 트랜잭션 롤백 테스트시

        return company;
    }

    @GetMapping("")
    public List<Company> getAll(){
        //return companyMapper.getAll(); // companyMapper.getAll() : mapper안에는 select로 company정보만 가져옴

        /** companyMapper.getAll() : mapper안에는 select로 company정보만 가져왔으나  @Result(property="employeeList",... 로 employeeList까지 가져오게 변경
         * 아래   companyService.getAll() 대신 companyMapper를 이용해서 employeeList까지 가져오는 방법
        **/
        //return companyMapper.getAll();

        /**
         * companyService.getAll(); Service안에는 select로 company정보를 가져온후
         * + id를 가지고 다시 employee테이블을 조회하여 setting
         */
        return companyService.getAll();

    }

    @GetMapping("/{id}")
    public Company getById(@PathVariable("id") int id ){
        return companyMapper.getById(id);
    }




}
