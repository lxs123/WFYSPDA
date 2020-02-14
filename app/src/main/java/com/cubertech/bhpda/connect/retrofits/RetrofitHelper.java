package com.cubertech.bhpda.connect.retrofits;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.*;

public class RetrofitHelper {
    /*
        **打印retrofit信息部分
         */
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new Logger() {
        @Override
        public void log(String message) {
            //打印retrofit日志
            Log.e("RetrofitLog", "retrofitBack = " + message);
        }

    });


    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).
            readTimeout(15, TimeUnit.SECONDS).
            writeTimeout(15, TimeUnit.SECONDS).addInterceptor(loggingInterceptor).build();
    GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().setLenient().create());
    private static RetrofitHelper instance = null;
    private Retrofit mRetrofit = null;
    public static String urlRecord = "";


    public static RetrofitHelper getInstance(String url) {

        if (instance == null) {
            instance = new RetrofitHelper(url);
            urlRecord = url;
        } else if (!TextUtils.equals(url, urlRecord)) {//防止ip地址变换
            instance = new RetrofitHelper(url);
            urlRecord = url;
        }
        return instance;
    }

    private RetrofitHelper(String url) {
        init(url);
    }

    private void init(String url) {
        resetApp(url);
    }


    private void resetApp(String url) {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Log.e("####3", url);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)//.baseUrl("http://192.168.1.138:48658/")
                .client(client)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public RetrofitService getServer() {
        return mRetrofit.create(RetrofitService.class);
    }
}
