package com.cubertech.bhpda.connect;

/**
 * Created by Administrator on 2017/5/10.
 */

public class UserInfo {

    private String Name;

    private String Pwd;

    public void setName(String Name){
        this.Name=Name;
    }
    public String getName(){
        return Name;
    }
    public void setPwd(String Pwd){
        this.Pwd=Pwd;
    }
    public String getPwd(){
        return Pwd;
    }

    @Override
    public String toString(){
        return "UserInfo{"+
                "Name='"+Name+'\''+
                ", Pwd='"+Pwd+'\''+
                '}';
    }
}
