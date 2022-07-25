package CollectionTest;

import java.util.*;

public class ArrayListTest {
    public ArrayListTest() {

    }

    public static void main(String[] args){

        //1. List test
        //ListTest();

        //2.generic arraylist test
        //GenericListTest();

        //3. linkedlist
        //LinkedListTest();

        //4. HashTable
        //HashTableTest();

        //5. HashMapTest
        HashMapTest();

    }



    private static void ListTest(){
        //1. generic
        //List  arlist = new ArrayList();

        //generic type : 형변환 필요 없음
        // 향상된 for문을 사용하려면 ArrayList를 generic type으로 선언해야함. 아니면 컴파일에러
        List  <String> arlist = new ArrayList<String>();

        arlist.add("TEST1");
        arlist.add("TEST2");

        // 향상된 for문을 사용하려면 ArrayList를 generic type으로 선언해야함. 아니면 컴파일에러
        for(String str: arlist){
            System.out.println("str = "+str);
        }

        arlist.remove(1);

        for(String e: arlist){
            System.out.println("e = "+e);
        }

        // data type이 달라도 가능
        // List 또는ArrayList 모두 선언가능.
        //List로 받아야 data type이 달라도 add가 가능하다고 되어있으나 실제로 둘다 가능.
        ArrayList arlist2 = new ArrayList();


        arlist2.add("TEST1");
        arlist2.add(123);
        arlist2.add(123.123);


        for(int i=0;i<arlist2.size();i++){
            System.out.println("arlist2["+i+"] = " + arlist2.get(i));
        }


        // ArrayList의 인자는 하나만 선언가능.아래 표현식은 오류
        //List<String, Integer> arList3 = new ArrayList<>();


    }


    public static class Man{
        String name;
        int age;

        public Man(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }


    public static void  GenericListTest(){

        List <Man> manList = new ArrayList<Man>();

        manList.add(new Man("MAN_1",111));

        Man man = new Man("MAN_2",222);
        manList.add(man);

        System.out.println("---------- advanced for loop-------------");
        for(Man loop_man:manList){
            System.out.println("loop_man getname = " + loop_man.getName());
            System.out.println("loop_man getage = " + loop_man.getAge());
        }

        System.out.println("---------- 일반 while -------------");
        int i=0;
        while(i<manList.size()){

            System.out.println(" " + manList.get(i).getName());
            System.out.println(" " + manList.get(i).getAge());
            i++;
        }


        System.out.println("----------Iterator while -------------");

        Iterator <Man> I_list = manList.iterator();

        while(I_list.hasNext()){
//            System.out.println(" "+ I_list.next().getName() ); //
//            System.out.println(" "+ I_list.next().getAge() );   // 두번째 next()가 사용되었으므로 첫번째 값이 아닌 두번째 값을 읽어옴

            //System.out.println(" " + I_list.next());// 값이 아닌 클래스값임

            Man temp_m = I_list.next();
            System.out.println(" " + temp_m.getName());
            System.out.println(" " + temp_m.getAge());
        }

        I_list = manList.iterator();    // 루프를 처음부터 다시 돌리려면 manList.iterator()를 다시 받아와야함.

        System.out.println("---- 재사용 루프 확인----");
        while(I_list.hasNext()){
            Man temp_m = I_list.next();
            System.out.println(" " + temp_m.getName());
            System.out.println(" " + temp_m.getAge());
        }


    }


    public static void LinkedListTest(){
        LinkedList <Man> l_list = new LinkedList<Man>();

        l_list.add(new Man("MAN_1" ,1111));
        l_list.add(new Man("MAN_2" ,2222));
        l_list.add(new Man("MAN_3" ,3333));

        Iterator <Man> I_man = l_list.iterator();

        System.out.println("---------  Linked List -----------");
        while(I_man.hasNext()){
            Man temp_m = I_man.next();
            System.out.println(" " + temp_m.getName());
            System.out.println(" " + temp_m.getAge());
        }

        l_list.remove(1);
        I_man = l_list.iterator();  // 루프 재사용

        while(I_man.hasNext()){
            Man temp_m = I_man.next();
            System.out.println(" " + temp_m.getName());
            System.out.println(" " + temp_m.getAge());
        }

    }

    public static void HashTableTest(){
        Hashtable <String, String> hashtable = new Hashtable<String,  String>();
        hashtable.put("age","generic선언 age");
        hashtable.put("name","generic선언 name");
        hashtable.put("location","generic선언 서울");

        System.out.println(" "+hashtable.get("age") );
        System.out.println(" "+hashtable.get("name") );
        System.out.println(" "+hashtable.get("location") );


        Hashtable hashtable2 = new Hashtable();

        hashtable2.put("name","generic없는 일반 hashtable name");
        hashtable2.put("age","generic없는 일반 hashtable age");

        System.out.println(""+hashtable2.get("name"));
        System.out.println(""+hashtable2.get("age"));


        // hashtable + Man class...
        Hashtable <String, Man> h_man = new Hashtable<String, Man>();

        h_man.put("man1",new Man("man1", 111));
        h_man.put("man2",new Man("man2", 111));

        System.out.println("-------------hashtable + Man class -----------------");
        System.out.println("" + h_man.get("man1").getName());
        System.out.println("" + h_man.get("man1").getAge());
        System.out.println("" + h_man.get("man2").getName());
        System.out.println("" + h_man.get("man2").getAge());




        // list + hashtable
        // hashtable자체가 배열로 사용되는게 아닌 그때그때 [이름+값]을 넣고 나중에 조회하는 임시적인 용도이므로 굳이 list로 관리할 필요가 없음.
        // 아래코드 싹다 의미없음. 그냥 hashtable은 값넣고 나중에 가져오고 그게 다임.
//        List <Hashtable> h_list = new ArrayList<Hashtable>();
//        h_list.add(hashtable);
//
//        for(Hashtable h:h_list){
//            System.out.println(""+h.get("age"));
//            System.out.println(""+h.get("name"));
//            System.out.println(""+h.get("location"));
//        }
//
//
//        hashtable.put("job","pro");
//
//        h_list.add(hashtable);
//
//        for(Hashtable h:h_list){
//            System.out.println(""+h.get("age"));
//            System.out.println(""+h.get("name"));
//            System.out.println(""+h.get("location"));
//            System.out.println(""+h.get("job"));
//        }

    }

    public static void HashMapTest(){
        Map h_map = new HashMap();  // HashMap h_map로 선언가능

        h_map.put("name","generic 없음 : hashmap test :name");
        h_map.put("age","generic 없음 : hashmap test : age");

        System.out.println("" + h_map.get("name"));
        System.out.println("" + h_map.get("age"));


        HashMap <String, String> h_map2 = new HashMap<String, String>();
        h_map2.put("name","generic선언 name");
        h_map2.put("age","generic선언 age");


        System.out.println("" + h_map2.get("name"));
        System.out.println("" + h_map2.get("age"));

        System.out.println("-----------hashmap + Man class----------------");
        HashMap<String, Man> h_map3 = new HashMap<>();  // <String, Man> 생략가능

        Man man1 = new Man("hash_map1", 11);
        h_map3.put("man1",man1);

        System.out.println(""+h_map3.get("man1").getName());
        System.out.println(""+h_map3.get("man1").getAge());

        h_map3.put("man2",new Man("man2",22));
        System.out.println(""+h_map3.get("man2").getName());
        System.out.println(""+h_map3.get("man2").getAge());


        System.out.println("-----------hashmap to list convert ----------------");
        // 아래 두개처럼 list로 hashmap을 받을때 미리 객체를생성해 놓고 받으면 오류
        //List<Man> manList = new ArrayList<>();
        //List<Member> result = ArrayList<>(store.values());

        // hashmap.values()를 arraylist로 전환 가능
        // 단, 아래 두개처럼 manList 객체를 미리 생성해놓고 쓰는게 아니라 ArrayList를 생성해서 반환해야 함.
        List<Man> manList = new ArrayList<>(h_map3.values());
        List<Man> manList2 ;
        manList2 = new ArrayList<>(h_map3.values()) ;

        Iterator <Man> I_list = manList.iterator();

        while(I_list.hasNext()){
            Man temp_m = I_list.next();
            System.out.println("" + temp_m.getName());
            System.out.println("" + temp_m.getAge());
        }


        // hashmap object로 선언후 루프문
        System.out.println("-------------hashmap + object----------------");
        HashMap<String, Object> h_map4 = new HashMap<>();  // <String, Man> 생략가능
        h_map4.put("man4",new Man("man4",11));
        h_map4.put("man5",new Man("man5",22));

        List<Object> i_list1 = new ArrayList<>(h_map4.values());

        Iterator<Object> i_ir =  i_list1.iterator();

        while(i_ir.hasNext()){
            Man temp_m = (Man)i_ir.next();
            System.out.println(""+ temp_m.getAge());
            System.out.println(""+ temp_m.getName());

        }





    }


}
