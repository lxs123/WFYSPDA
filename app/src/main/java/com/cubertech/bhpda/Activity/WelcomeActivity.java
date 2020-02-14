package com.cubertech.bhpda.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.utils.Constant;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#315CAB"));
        }


        Observable.timer(Constant.SPLASH_LENGTH, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).map(l -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return null;
        }).subscribe();
    }
}
