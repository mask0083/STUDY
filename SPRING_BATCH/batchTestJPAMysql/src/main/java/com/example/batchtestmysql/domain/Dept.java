package com.example.batchtestmysql.domain;


import lombok.*;


import javax.persistence.Entity;
import javax.persistence.Id;    // 이걸로 선언 안하면 에러발생


@Setter
@Getter
/**
 * debug를 위해서는 각 멤버별로(ex. 객체.getfirstName()을 호출하면 값이 출력됨.
 * 단 객체.toString()을 하면 이상한 객체값이나오게 됨.
 * toString()메소드를 재정의하여 객체.toString()을 하면 모든 멤버들의 값이 출력되기 위해 @ToString 어노테이션을 설정함
 */
@ToString
@Entity // JPA일때만 설정
@AllArgsConstructor
@NoArgsConstructor

public class Dept {

    @Id
    Integer deptNo;
    String dName;
    String loc;

}
