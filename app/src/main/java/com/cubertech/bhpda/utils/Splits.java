package com.cubertech.bhpda.utils;

public class Splits {
    private String s;

    public Splits(String s){
        this.s = s;
    }

    public Splits() {
        this("unknow");
    }

    public String[] getStr(){
        String[] str_string = s.split("\\d");//  \d 为正则表达式表示[0-9]数字
        return str_string;
    }

    public int[] getNum(){
        String[] num_string = s.split("\\D");  // \D 为正则表达式表示非数字
        String a = "";

        for(String m : num_string){
            a += m;
        }

        String[] num = a.split("");  //将分离出的重新保存在新数组num中不要直接用num_string，  因为在正则表达式对字符串进行选择时若前面的几个字符不符合要求但num_string数组中仍会存有其位置 是空格
        int[] inte = new int[num.length];

        for(int i =0; i < num.length; i++){
            if (!num[i].equals("")) {
                inte[i] = Integer.parseInt(num[i]); //将该数组中的数字存入int数组
            }
        }

        return inte;
    }
    //打印字符串中的字母
    public String returnStr(){
        String a = "";
        for(String n : getStr()){
            a += n;
        }
        return a;
    }
    //打印字符串中的数字
    public String returnNum(){
        String a = "";
        for(int n : getNum()){
            a += n;
        }
        return a.substring(1);
    }
}