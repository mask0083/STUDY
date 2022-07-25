package com.example.testcodes.JsonTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonWrite {

    public static void main(String[] args) {

        /* 1.단순 write. 문제점 : 정렬이 안됨. JSONObject에서 별도로 정렬기능을 제공안하므로 뒤죽박죽 write됨*/
        //JsonWrite_1();

        /* json 정렬후 출력만 */
        //JsonPrint();

        /* 2.json 정렬후 json file 생성 + LinkedHashMap사용 */
        //JsonWrite_2();

        /* 3. Gson을 사용한 정렬후 json file 생성 : 요게 젤 편함. */
        //JsonWrite_3();

        /* 5. 이중 클랙스 선언 + Gson 정렬 + file 생성  */
        JsonWrite_4();


    }


    /**
     * 객체 생성후 json파일로 생성
     * GSON을 사용하면 클래스 생성자의 인자 순서대로 출력됨.
     */
    private static void JsonWrite_4() {

        /**
         * 이중객체 한방 생성자 호출
         */
        Person person = new Person("김용현", 45, "com", "서울 강서구", new FriendInfo("이승원", 45, "com"));
        Gson gson = new Gson();
        String str = gson.toJson(person);
        System.out.println("str : " + str);

        /**
         * 이중객체 별도 생성자호출
         */

        Person person2 = new Person("김용현2", 35, "com", "서울 강남구");
        FriendInfo friendInfo = new FriendInfo("이승원2", 35, "com");
        person2.setFriendInfo(friendInfo);

        String str2 = gson.toJson(person2);
        System.out.println("str2 = " + str2);


        /**
         * 3. 문자열 이쁘게 출력 : GsonBuilder().setPrettyPrinting().create();
         */
        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        String strPretty = gsonPretty.toJson(person2);
        System.out.println("strPretty = " + strPretty);

        try {
            /**
             * FlleWrite 사용시 new FileWrite(파일명); => 추가로 이어쓰기 안됨
             *                 new FileWrite(파일명, true)  => 추가로 이어쓰기 가능
             */
            FileWriter file = new FileWriter("D:/INFOPLUS/STUDY/testcodes/JsonTestFile/JsonWrite_4.json");
            file.write(strPretty);
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void JsonWrite_3() {

        /**
         * 1. gson 라이브러리 JsonObject 사용시 HashMap이나 LinkedHashMap 사용할필요없이 바로 데이터 넣을수 있음.
         */
        JsonObject jObj = new JsonObject(); // gson 라이브러리임. JSONObject 아님.

        jObj.addProperty("name", "김용현");
        jObj.addProperty("age", 45);
        jObj.addProperty("address", "서울 강서");
        jObj.addProperty("job", "com");

        /**
         * 2. JsonObject자체에 정렬기능이 있어서 입력한 순서대로 print됨
         */
        System.out.println(" toString = " + jObj.toString());

        /**
         * 3. Gson 객체를 이용해 gson.toJson(jObj) 으로 해도 입력순으로 출력됨.
         */
        Gson gson = new Gson();
        String str = gson.toJson(jObj);
        System.out.println("str = " + str);


        try {
            /**
             * FlleWrite 사용시 new FileWrite(파일명); => 추가로 이어쓰기 안됨
             *                 new FileWrite(파일명, true)  => 추가로 이어쓰기 가능
             */
            FileWriter file = new FileWriter("D:/INFOPLUS/STUDY/testcodes/JsonTestFile/JsonWrite_3.json");
            file.write(jObj.toString());
            file.flush();   // buffer 비우기
            file.close();   // file descriptor close
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void JsonPrint() {

        /* 그냥 HashMap으로 선언하면 정렬이 안됨. */
        //Map obj = new HashMap();

        /* 반드시 LinkedHashMap()으로 선언해야 정렬이 됨.*/
        Map obj = new LinkedHashMap();
        obj.put("name", "foo");
        obj.put("num", 100);
        obj.put("balance", 1000.21);
        obj.put("is_vip", Boolean.TRUE);
        obj.put("nickname", null);

        String jsonText = JSONValue.toJSONString(obj);
        System.out.println(jsonText);
    }


    private static void JsonWrite_1() {

        JSONObject obj = new JSONObject();

        try {

            obj.put("name", "김용현");
            obj.put("sex", "Male");
            obj.put("age", 45);
            obj.put("address", "강서");


            // c처럼 fileopen()할 필요가 없음 FileWriter()안에서 자동으로 open.
            /**
             * FlleWrite 사용시 new FileWrite(파일명); => 추가로 이어쓰기 안됨
             *                 new FileWrite(파일명, true)  => 추가로 이어쓰기 가능
             */
            FileWriter file = new FileWriter("D:/INFOPLUS/STUDY/testcodes/JsonTestFile/JsonWrite_1.json");
            file.write(obj.toJSONString());
            file.flush();   // buffer 비우기
            file.close();   // file descriptor close


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(obj);

    }

    private static void JsonWrite_2() {

        /* 1. json파일 정렬을 하려면 반드시  LinkedHashMap으로 선언 */
        //Map jsonmap = new HashMap();
        Map<String, Object> jsonmap = new LinkedHashMap<>();

        jsonmap.put("name", "김용현");
        jsonmap.put("sex", "Male");
        jsonmap.put("age", 45);
        jsonmap.put("job", "com");

        System.out.println("JSONValue.toJSONString(jsonmap) = " + JSONValue.toJSONString(jsonmap));

        /* 2. 위에서 LinkedHashMap()으로 선언했어도 obj.putAll(jsonmap)을 하는 순간 정렬이 다 뒤틀어짐
         *    JSONObject 자체가 정렬을 아예 무시하므로....
         */
//        JSONObject obj = new JSONObject();
//        obj.putAll(jsonmap);


        /* 오류 : JSONObject(jsonmap) 생성자 인자가 없어야 됨. */
        //JSONObject json =  new JSONObject(jsonmap);

        try {
            /**
             * FlleWrite 사용시 new FileWrite(파일명); => 추가로 이어쓰기 안됨
             *                 new FileWrite(파일명, true)  => 추가로 이어쓰기 가능
             */
            FileWriter file = new FileWriter("D:/INFOPLUS/STUDY/testcodes/JsonTest/JsonWrite_2.json");

            /**
             * 반드시 아래처럼
             * LinkedHashMap()으로 선언한 jsonmap 객체를 JSONValue.toJSONString()으로 string변환후 write 해야 함.
             * LinkedHashMap()자체가 이미 삽입한 순서대로 정렬되어 있으므로 별도 정렬은 필요없음.
             * file write 하기 위해 jsonmap 객체를 string으로 변환만 해주면 됨.
             */
            String jsonSortString = JSONValue.toJSONString(jsonmap);
            System.out.println("jsonSortString = " + jsonSortString);
            file.write(jsonSortString);
            file.flush();   // buffer 비우기
            file.close();   // file descriptor close
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static class Person {

        private String name;
        private int age;
        private String job;
        private String address;

        private FriendInfo friendInfo;

        public Person(String name, int age, String job, String address, FriendInfo friendInfo) {
            this.name = name;
            this.age = age;
            this.job = job;
            this.address = address;
            this.friendInfo = friendInfo;
        }

        public Person(String name, int age, String job, String address) {
            this.name = name;
            this.age = age;
            this.job = job;
            this.address = address;
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

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public FriendInfo getFriendInfo() {
            return friendInfo;
        }

        public void setFriendInfo(FriendInfo friendInfo) {
            this.friendInfo = friendInfo;
        }
    }

    private static class FriendInfo {
        private String friend_name;
        private int friend_age;
        private String friend_job;

        public FriendInfo(String friend_name, int friend_age, String friend_job) {
            this.friend_name = friend_name;
            this.friend_age = friend_age;
            this.friend_job = friend_job;
        }

        public String getFriend_name() {
            return friend_name;
        }

        public void setFriend_name(String friend_name) {
            this.friend_name = friend_name;
        }

        public int getFriend_age() {
            return friend_age;
        }

        public void setFriend_age(int friend_age) {
            this.friend_age = friend_age;
        }

        public String getFriend_job() {
            return friend_job;
        }

        public void setFriend_job(String friend_job) {
            this.friend_job = friend_job;
        }
    }

}
