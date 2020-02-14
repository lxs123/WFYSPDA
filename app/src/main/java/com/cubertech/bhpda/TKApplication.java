package com.cubertech.bhpda;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/12/28.
 */

public class TKApplication extends Application {
    private String url = "";
    private String port = "";
    private String userName = "";
    /**
     * 系统上下文
     */
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

}
