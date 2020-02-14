package com.cubertech.bhpda.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统常量类
 * Created by LiQi on 2017/6/9.
 */

public class Constant {
    //相机扫描中的常量
    public static final int SCANNIN_GREQUEST_CODE = 1;
    //首页跳转时间
    public static final int SPLASH_LENGTH = 1;

    public static final int RESULT_CODE = 4587;

    public static final int REQUEST_CODE =87;

    public static String CreateNewId(){

        /*SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());*/
        String id = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(
                System.currentTimeMillis()));
      /*  String id = new SimpleDateFormat("yyyyMMddHHmmssfffffff").format(new Date(
                System.currentTimeMillis()));*/
        String guid=java.util.UUID.randomUUID().toString().replace("-","");
        id+=guid.substring(0,17);
        return id;
        /*string id = DateTime.Now.ToString("yyyyMMddHHmmssfffffff");
        string guid = Guid.NewGuid().ToString().Replace("-", "");

        id += guid.Substring(0, 10);
        return id;*/
    }
    public static String getDate(){
        String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
                System.currentTimeMillis()));;
        return date;
    }
}
