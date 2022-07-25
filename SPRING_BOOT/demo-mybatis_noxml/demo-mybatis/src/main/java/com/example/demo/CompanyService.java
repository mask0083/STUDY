package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    public List<Company> getAll(){
        List<Company> companyList = companyMapper.getAll();

        if(null!=companyList && companyList.size()>0){
            for(Company company:companyList ){
                company.setEmployeeList(employeeMapper.getByCompanyId(company.getId()));
            }
        }

        // 최종 return전 json파일 생성
        JsonFileCreate(companyList);

        return companyList;
    }

    //@Transactional  // db 트랜잭션 롤백용
    @Transactional(rollbackFor = Exception.class)
    public Company add(Company company) throws Exception{
        companyMapper.insert(company);

        //db 트랜잭션 롤백 테스트
        if(true) {
            //throw new RuntimeException("Legacy Exception"); // runtime시 발생하는 exception. @Transactional 을 선언해야함
            throw new Exception();  // runtime시가 아닌 모든 상황에서 발생하는 exception. @Transactional(rollbackFor = Exception.class) 으로 선언해야 함.
        }
        return company;
    }

    public void JsonFileCreate(List<Company> companyList){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String str = gson.toJson(companyList);

        System.out.println("str = " + str);

        try {
            FileWriter file = new FileWriter("D:/INFOPLUS/STUDY/demo-mybatis/JsonFIle/jsonfile-mybatis");
            file.write(str);
            file.flush();
            file.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

}
