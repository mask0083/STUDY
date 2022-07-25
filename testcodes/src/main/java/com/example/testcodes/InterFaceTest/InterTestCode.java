package InterFaceTest;


public class InterTestCode implements InterTest {

// String [] : [] 배열 없으면 run이 안됨. 주의!
    public static void main(String[] args){
        Bird bird = new Bird();
        bird.fly();

        Airplane air = new Airplane();
        air.fly();

        // 인터페이스로 객체 생성 가능
        System.out.println("인터페이스로 객체 생성 가능!! ");
        InterTest instBird = new Bird();
        instBird.fly();

        InterTest instAir = new Airplane();
        instAir.fly();
    }

    @Override
    public void fly() {
        System.out.println(" InterTestCode Fly");
    }

    // 클래스안에 다시 클래스정의할때도 인터페이스 받을수 있음.
    public static class Bird implements  InterTest{
        @Override
        public void fly() {
            System.out.println("Bird Fly");
        }

    }


    public static class Airplane implements InterTest{

        @Override
        public void fly() {
            System.out.println("Airplane Fly");
        }
    }







}
