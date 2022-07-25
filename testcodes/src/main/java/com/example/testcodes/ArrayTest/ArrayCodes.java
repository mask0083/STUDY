package com.example.testcodes.ArrayTest;

import org.w3c.dom.ls.LSOutput;


//git 주석 추가 테스트
//노트북 추가 1234
//데스크탑 추가
public class ArrayCodes {

    public ArrayCodes() {
    }

    public static void main(String[] args){
        System.out.println("test");

        Array1();
        //2차원 배열
        Array2();
    }


    public static void Array1(){

        String []array1 = {"AA","BB","CC"};

        for(String temp:array1){
            System.out.println("temp = " + temp);
        }
    }
    public static void Array2(){
        System.out.println("2Array");

        //두번재 배열의 갯수를 맘대로 지정가능(동적+맘대로)
        String [][]array2 = new String[2][];
        array2[0] = new String[2];
        array2[1] = new String[3];


        array2[0][0] = "A1";
        array2[0][1] = "A2";
        array2[1][0] = "B1";
        array2[1][1] = "B2";

        // 이중배열 이상에서는 향상된 for문이 안먹는듯... 그냥 일반for문으로 돌릴것
        for( int i=0;i<array2.length;i++ ){
            for(int j=0;j<array2[i].length;j++){
                System.out.println("array2["+i+"]["+j+"]= "+ array2[i][j]);
            }
        }

    }



}

