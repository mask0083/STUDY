package com.example.batchtestmysql;

import com.example.batchtestmysql.domain.Dept;
import com.example.batchtestmysql.domain.DeptRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

@SpringBootTest
public class TestDeptRepository {

    @Autowired
    DeptRepository deptRepository;

    @Test
    @Commit
    public void dept01(){

        System.out.println("test");
        for(int i=1;i<101;i++){
            deptRepository.save(new Dept(i, "dname_"+String.valueOf(i), "loc_"+String.valueOf(i)));
        }
    }
}
