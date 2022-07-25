package ExceptionTest;

// 일반적 예외처리
public class ExceptionTest {

    public static void main(String[] args){

        try{
            //Except_1();

            // null exceptiong test
            Except_Null();

        }catch(Exception e){
            System.out.println("e.getMessage = " + e.getMessage());
            System.out.println("e.printStackTrace = ");
            System.out.println("e = " + e);

            e.printStackTrace();    // 이게 위치까지 상세하게 다 나옴

        }finally {
            System.out.println("finally");

        }

    }

    public static void Except_Null() throws Exception{
        String str = null;

        // 익셉션 발생
        if( str.equals(null)){
            System.out.println("step str = null");
        }

       // 익셉션 안발생
        if( "".equals(str)){
            System.out.println("step str = null");
        }


        Test test = new Test();
        test.setTemp(null);

        // 익셉션 안발생
//        if(test.getTemp() == null){
//            System.out.println("test is null");
//        }

        // 이건 익셉션 발생
//        if(test.getTemp().equals(null)){
//            System.out.println("test is null");
//        }

//        if("".equals(test.getTemp())){
//            System.out.println("test is null");
//        }
//
//        // 익셉션 안발생
//        if(str ==null){
//            System.out.println("test is null");
//        }

//        if( test.getTemp() == null){
//            System.out.println("test is null");
//        }


        if( null == test.getTemp()){
            System.out.println("test is null");
        }


    }


    public static void Except_1() throws Exception {

        Except_2();
    }

    public static void  Except_2() throws Exception{

    int num = 3/0;

    }




}

 class Test{
    private String temp;

    public Test() {

    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}



