package com.cubertech.bhpda.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cubertech.bhpda.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/3.
 */

public class JylrAty extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_jylr);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.cancel, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
