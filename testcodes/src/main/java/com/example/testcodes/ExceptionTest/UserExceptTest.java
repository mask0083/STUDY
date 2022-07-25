package ExceptionTest;

// 1. 사용자 정의 exception
// 기존 exception 클래스를 이용해서 단순 문자 처리
public class UserExceptTest {

    public static void main(String[] args){

        try{
            UserExcept(7);

        }catch(Exception e){
            e.getMessage();
            e.printStackTrace();

        }finally{
            System.out.println("finally..");
        }
    }


    public static void UserExcept(int num ) throws Exception{

        if(num<10)
            throw new Exception("사용자정의 exception : 값이 적다");    // 반드시 new로 생성한다음 던져야 함.

    }


}
