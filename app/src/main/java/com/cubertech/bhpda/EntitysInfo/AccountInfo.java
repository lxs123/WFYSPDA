package com.cubertech.bhpda.EntitysInfo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator
 * on 2017/6/14.
 */

public class AccountInfo implements Parcelable {
    //登录名
    private String name;
    //账套
    private String data;
    //版本
    private String VersionCode="";

    public String getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(String versionCode) {
        VersionCode = versionCode;
    }

    public AccountInfo() {

    }
    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getData(){
        return data;
    }
    public void setData(String data){
        this.data=data;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel arg0,int arg1){
        arg0.writeString(name);
        arg0.writeString(data);
    }
    public static final Creator<AccountInfo> CREATOR = new Creator<AccountInfo>() {
        @Override
        public AccountInfo createFromParcel(Parcel source) {
            return new AccountInfo(source);
        }

        @Override
        public AccountInfo[] newArray(int size) {
            return new AccountInfo[size];
        }
    };

    public AccountInfo(Parcel in) {
        name=in.readString();
        data=in.readString();
    }
}
