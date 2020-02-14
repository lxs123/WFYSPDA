package com.cubertech.bhpda.utils;

import android.widget.Toast;

import com.cubertech.bhpda.TKApplication;

/**
 * toast工具类 防止多次点击,造成大量的吐司提示,影响用户体验
 * Created by Administrator on 2018/5/15.
 */

public class ToastUtils {
    private static Toast mToast;

    public static void showToast(String text) {
        try {
            if (mToast == null) {
                mToast = Toast.makeText(TKApplication.getAppContext(), text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void showToastShort(String text) {
        try {
            if (mToast == null) {
                mToast = Toast.makeText(TKApplication.getAppContext(), text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void showToastLong(String text) {
        try {
            if (mToast == null) {
                mToast = Toast.makeText(TKApplication.getAppContext(), text, Toast.LENGTH_LONG);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
