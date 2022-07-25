package ObjectTest;

public class ObjectGenericTest {


    public static void main(String args[]){


        //1.generic이 아니므로 형변환이 필요
        //TestObject();

        //2.형변환 필요없는 generic type 으로 생성한  class 호출
        //TestObjectGeneric();

        //3. object type으로 메소드 호출
        Object obj = new Object();
        obj = TestObjectMethod("object method 호출 테스트 ");
        System.out.println("호출후 = " + obj);

        obj = TestObjectMethod(123.111);
        System.out.println("호출후 = " + obj);

    }

    public static class Exam{

        public  Exam() {

        }

        private Object obj;

        public void setObj(Object obj) {
            this.obj = obj;
        }


        public Object getObj() {

            return obj;
        }
    }


    // generic type으로 class 선언. 주로 <E>를 많이 사용하나 아무 알파벳이나 들어가도 노상관.
    // <A,B>를 그냥 data type이라 생각하고 갯수만큼 생성하면 됨.
    public static class Exam_G<A,B>{

        private A obj;
        private B obj2;

        public Exam_G() {
        }

        public A getObj() {
            return obj;
        }

        public void setObj(A obj) {
            this.obj = obj;
        }

        public B getObj2() {
            return obj2;
        }

        public void setObj2(B obj2) {
            this.obj2 = obj2;
        }
    }




    public static Object TestObjectMethod(Object  obj){
        System.out.println("obj.getClass() = " + obj.getClass());

        if(obj instanceof String ) {
            obj = (String)obj + ": 자동 string 형변환완료";
        }
        else if(obj instanceof Double){
            obj = (Double)obj + 1000000.0;
        }

        return obj;
    }

    public static void TestObjectGeneric(){
        // object를 그냥 data type으로 생각하고 갯수만큼 생성하면 됨.
        Exam_G<Object,Object> exam_g = new Exam_G<Object,Object>();

        exam_g.setObj("형변환 필요없음 : generic type test");
        System.out.println("" + exam_g.getObj());

        exam_g.setObj(1234.123);
        System.out.println("" + exam_g.getObj());

        exam_g.setObj("boolean 체크");
        exam_g.setObj2(true);
        System.out.println("" + exam_g.getObj());
        System.out.println("" + exam_g.getObj2());



    }

    public static void TestObject(){
        Exam ex = new Exam();
        ex.setObj("string object");

        String str = (String)ex.getObj();
        System.out.println("str = " + str);

        ex.setObj(1234.1234);
        Double d = (Double)ex.getObj();
        System.out.println("d = " + d);
    }


}
