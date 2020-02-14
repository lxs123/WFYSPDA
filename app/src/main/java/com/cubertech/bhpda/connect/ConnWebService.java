package com.cubertech.bhpda.connect;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/5/10.
 */

public class ConnWebService {
    private static Context context;
    private static String url = null;
    private static String update=null;
    private static String port = null;

    public ConnWebService(){

    }

    public ConnWebService(Context context){
        SharedPreferences sp = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        url = sp.getString("IP", null);
        port = sp.getString("port", null);
        //update=sp.getString("update",null);

    }



    public String getUrl() {
        //http://192.168.1.223:8686/DbWebService.asmx
        String WsUrl="http://"+url+":"+port+"/TKWebService.asmx";
        return WsUrl;
    }
}
