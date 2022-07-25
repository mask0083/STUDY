package ObjectTest;

public class ObjectCodeTest {


    public static void main(String args[]){

        Parent p1 = new Parent();
        p1.setName("parent_1");
        p1.setAge(100);


        // 상위클래스 = 하위클래스 : 형변환없이 가능
        Parent p2 = new Child();
        p2.setName("child로 생성 : parent_2");
        p2.setAge(200);


        // runtime error발생 : 형변환을 바로 해주면 실행시 오류발생
        //Child c1 = (Child)new Parent();

        // 이것도 실행시 오류 new Parent()가 아닌 new Child()로 해야됨.
        // Parent p3 = new Parent();

        // 상위클래스 = 하위클래스 선언후 형변환
        // 이렇게 해야 정상
        Child c1 = new Child();
        Parent p3 = new Child();

        c1 = (Child)p3;
        c1.setName("형변환후 name");
        c1.setAge(9999);

        System.out.println("c1 = " + c1.getName());
        System.out.println("c1 = " + c1.getAge());


    }

    public static class Parent{

        String name;
        Integer age;

        public Parent() {
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }


    public static class Child extends Parent{

        String name;
        Integer age;

        public Child() {

        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public Integer getAge() {
            return age;
        }

        @Override
        public void setAge(Integer age) {
            this.age = age;
        }
    }


}
