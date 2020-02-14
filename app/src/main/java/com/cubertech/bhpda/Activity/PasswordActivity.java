package com.cubertech.bhpda.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class PasswordActivity extends AppCompatActivity {
    private SiteService siteService;
    private TKApplication application;
    AccountInfo ai;

    @BindView(R.id.oldpwd)
    TextView oldpwd;
    @BindView(R.id.newpwd)
    TextView newpwd;
    @BindView(R.id.turepwd)
    TextView turepwd;

    @BindView(R.id.btn_Pwd_submit)
    Button btn_Pwd_submit;

    @BindView(R.id.left)
    ImageView left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //todo
        ai = bundle.getParcelable("account");
        application = (TKApplication) getApplication();

        btn_Pwd_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmits();
            }
        });


        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

    }

    private  void  onSubmits(){
        if(oldpwd.getText().toString().trim().equals("")||newpwd.getText().toString().trim().equals("")||
             turepwd.getText().toString().trim().equals("")){
            Toast.makeText(PasswordActivity.this, "请填写完整数据！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!newpwd.getText().toString().trim().equals(turepwd.getText().toString().trim())){
            Toast.makeText(PasswordActivity.this, "新密码与确认密码不一致！", Toast.LENGTH_SHORT).show();
            return;
        }

        //String test=ai.getName();


        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("UserName", ai.getName());
        params.put("oldpwd", oldpwd.getText().toString().trim());
        params.put("newpwd", newpwd.getText().toString().trim());
        params.put("data", ai.getData());
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //通过Application传值//此处不用实现
        String IP = "";
        String port = "";
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        //先弄一个观察者
        Observable<String> observable = siteService.updataPwd(params,
                PasswordActivity.this, IP, port);
        //一个订阅者
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(PasswordActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String str) {
                if (!str.equals("")) {
                    Toast.makeText(PasswordActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }

    /**
     * @see android.app.Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {

            super.onBackPressed();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            finish();


    }
}
