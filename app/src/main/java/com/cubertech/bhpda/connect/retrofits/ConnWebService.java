package com.cubertech.bhpda.connect.retrofits;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Administrator on 2017/5/10.
 */

public class ConnWebService {
    private static Context context;
    private static String url = null;
    private static String update = null;
    private static String port = null;

    public ConnWebService() {

    }

    public ConnWebService(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",0);
        url = sp.getString("IP", null);
        port = sp.getString("port", null);
        update = sp.getString("update", null);
        Log.e("2#####", url + ":" + port);
    }


    public String getUrl() {
        //http://192.168.1.223:8686/DbWebService.asmx
        //String WsUrl="http://"+url+":"+port+"/DbWebService.asmx";
        Log.e("1#####", url + ":" + port);
        String Url = "http://" + url + ":" + port + "/";
        return Url;
    }
}
