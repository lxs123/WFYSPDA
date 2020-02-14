package com.cubertech.bhpda.connect.retrofits;

import android.net.ParseException;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class CustomException {
  /*  *//**
     * 未知错误
     *//*
    public static final int UNKNOWN = 1000;

    *//**
     * 解析错误
     *//*
    public static final int PARSE_ERROR = 1001;

    *//**
     * 网络错误
     *//*
    public static final int NETWORK_ERROR = 1002;

    *//**
     * 协议错误
     *//*
    public static final int HTTP_ERROR = 1003;*/

    public static String handleException(Throwable e) {

        if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            //解析错误
           // ex = new ApiException(PARSE_ERROR, e.getMessage());
            return "解析错误！代码:9001";
        } else if (e instanceof ConnectException) {
            //网络错误
           // ex = new ApiException(NETWORK_ERROR, e.getMessage());
            return "网络错误！代码:9001";
        } else if (e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
            //连接错误
            //ex = new ApiException(NETWORK_ERROR, e.getMessage());
            return "连接错误！代码:9001";
        } else {
            //未知错误
            //ex = new ApiException(UNKNOWN, e.getMessage());
            return "未知错误！代码:9001";
        }
    }
}
