package ExceptionTest;

// MyException 클래스를 생성후 사용자 익센셥 처리 방법
public class UserExceptTest2 {

    public static class MyException extends Exception{
        public MyException(String mesg) {
            super(mesg);
        }
    }

    public static void main(String[] args) {

        try{

            MyExceptTest(7);

        }catch(MyException e){
        e.getMessage();
        e.printStackTrace();
        }finally{
            System.out.println("finally...");
        }

    }


    public static void  MyExceptTest( int num) throws MyException {
        if( num<10)
            throw new MyException("myexcept test : 작네 작어");
    }
}
