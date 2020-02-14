package com.cubertech.bhpda.Activity.TransferPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.CMSMCActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ScanActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WgrkActivity extends AppCompatActivity {
    private SiteService siteService;
    AccountInfo ai;
    private TKApplication application;

    private ImageView left;
    private Button btn_rk, btn_qx;
    private EditText et_tm, et_lx, et_jd;
    private TextView tv_gdxx, tv_cpph, tv_cppm, tv_cpgg;
    private TextView tv_rkck, tv_ckmc;
    private EditText et_sl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wgrk);
        et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setSelection(0);
        et_tm.requestFocus();
        et_lx = (EditText) findViewById(R.id.et_lx);
        et_jd = (EditText) findViewById(R.id.et_jd);
        et_sl = (EditText) findViewById(R.id.et_sl);
        et_sl.setEnabled(false);
        tv_gdxx = (TextView) findViewById(R.id.tv_gdxx);
        tv_cpph = (TextView) findViewById(R.id.tv_cpph);
        tv_cppm = (TextView) findViewById(R.id.tv_cppm);
        tv_cpgg = (TextView) findViewById(R.id.tv_cpgg);

        tv_rkck = (TextView) findViewById(R.id.tv_rkck);
        tv_rkck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceCK();
            }
        });
        tv_ckmc = (TextView) findViewById(R.id.tv_ckmc);

        left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

        btn_rk = (Button) findViewById(R.id.btn_rk);
        btn_rk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        btn_qx = (Button) findViewById(R.id.btn_qx);
        btn_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        initView();
    }

    /**
     * 初始化
     */
    private void initView() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        application = (TKApplication) getApplication();

        et_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //执行相关读取操作
                    //Toast.makeText(GxzyActivity.this, "执行读取数据库操作", Toast.LENGTH_SHORT).show();
                    scanCode();//
                }
                return false;
            }
        });

    }

    /**
     * 添加入库数据
     */
    private void add() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(WgrkActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
            return;
        }

        /*Toast.makeText(WgrkActivity.this,"完成！",Toast.LENGTH_SHORT).show();
        finish();*/


        List<Object> PDARKMX = new ArrayList<>();
        PDARKMX.add(tm);//MX001[0]容器码 在泰开项目中保存的是产品码
        PDARKMX.add(et_sl.getText().toString().trim());//MX004[1]//数量

        PDARKMX.add(tv_rkck.getText().toString().trim());//MX005[2]//仓库

        PDARKMX.add(tv_cpph.getText().toString().trim());//[3]品号;
        //tv_gdxx.setText(obj.get(2).toString()+"-"+obj.get(3).toString());//[2]单别-[3]单号
        String str = tv_gdxx.getText().toString().trim();
        String[] str1 = str.split("-");
        PDARKMX.add(str1[0].toString());//[4]单别; MX003
        PDARKMX.add(str1[1].toString());//[5]单号; MX004


        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("database", ai.getData());
        params.put("PDARKMX", PDARKMX);
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //通过Application传值
        String IP = "";
        String port = "";
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        //先弄一个观察者
        Observable<String> observable = siteService.Addwgrk(params,
                WgrkActivity.this, IP, port);

        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(WgrkActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String str) {
                if (!str.equals("")) {
                    //Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                    Toast.makeText(WgrkActivity.this, "完成！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }


    /**
     * 扫描二维码
     */
    private void scanCode() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(WgrkActivity.this, "请扫描条码", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", tm);
        params.put("database", ai.getData());
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //通过Application传值
        String IP = "";
        String port = "";
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        //先弄一个观察者
        Observable<List<Object>> observable = siteService.QRcodeWgrk(params,
                WgrkActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_tm.setSelection(0);
                et_tm.requestFocus();
                Toast.makeText(WgrkActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }

                //List<Object> o=obj;
                //Toast.makeText(GxzyActivity.this, "" + obj.size(), Toast.LENGTH_LONG).show();
                et_lx.setText(obj.get(0).toString());//[0]转移类型正常转移
                et_jd.setText(obj.get(1).toString());//[1]开始
                tv_gdxx.setText(obj.get(2).toString() + "-" + obj.get(3).toString());//[2]单别-[3]单号
                tv_cpph.setText(obj.get(4).toString());//[4]产品品号
                tv_cppm.setText(obj.get(5).toString());//[5]产品品名
                tv_cpgg.setText(obj.get(6).toString());////[6]产品规格

                tv_rkck.setText(obj.get(7).toString());//[7]仓库编号
                tv_ckmc.setText(obj.get(8).toString());//[8]仓库名称
                //[9]数量
                et_sl.setText(obj.get(9).toString());

            }
        });
    }


    /**
     * 入库仓库选择
     */
    private void choiceCK() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", ai);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(this, CMSMCActivity.class);
        this.startActivityForResult(intent, 1);
        this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    //重写   处理返回信息的监听（回调方法）
    //onActivityResult通用监听  监听所有返回信息的
    //必须要有requestCode区分有哪个请求返回的
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //获取返回信息
                String MC001 = data.getExtras().getString("MC001");//编号
                String MC002 = data.getExtras().getString("MC002");//名称

                tv_rkck.setText(MC001);
                tv_ckmc.setText(MC002);

            } else {
                Toast.makeText(this, "数据选择错误，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 999) {
            String ScanResult = "";
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    //Toast.makeText(getActivity(),"内容为空",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // ScanResult 为 获取到的字符串
                    ScanResult = intentResult.getContents();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            et_tm.setText(ScanResult);

            scanCode();
        }
    }


    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class);
        Intent scanIntent = intentIntegrator.createScanIntent();
        startActivityForResult(scanIntent, 999);
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
