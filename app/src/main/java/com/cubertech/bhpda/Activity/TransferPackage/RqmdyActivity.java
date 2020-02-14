package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.SQLiteUtils;
import com.cubertech.bhpda.utils.ScanActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class RqmdyActivity extends AppCompatActivity implements View.OnClickListener {
    private AccountInfo ai;
    private EditText et_tm;
    private SiteService siteService;
    private TKApplication application;
    private TextView tv_cpph, tv_cppm, tv_cpgg, tv_rqlx, tv_bzsl;
    private List<Object> resultList;
    public static RqmdyActivity instance = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rqmdy);
        instance = this;
        initView();
        SQLiteUtils.getInstance(RqmdyActivity.this).getStatus(SQLiteUtils.RQM);

    }

    private void initView() {
        ImageView back = (ImageView) findViewById(R.id.left);
        Button btnQX = (Button) findViewById(R.id.btn_qx);
        Button btnSc = (Button) findViewById(R.id.btn_sc);//生成
        et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setSelection(0);
        et_tm.requestFocus();
        tv_cpph = (TextView) findViewById(R.id.tv_cpph);
        tv_cppm = (TextView) findViewById(R.id.tv_cppm);
        tv_cpgg = (TextView) findViewById(R.id.tv_cpgg);
        tv_rqlx = (TextView) findViewById(R.id.tv_rqlx);
        tv_bzsl = (TextView) findViewById(R.id.tv_bzsl);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        back.setOnClickListener(this);
        btnQX.setOnClickListener(this);
        btnSc.setOnClickListener(this);

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
                    //Toast.makeText(RqmdyActivity.this, "执行读取数据库操作", Toast.LENGTH_SHORT).show();
                    scanCode();//
                }
                return false;
            }
        });
    }

    /**
     * 扫描二维码
     */
    private void scanCode() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(RqmdyActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
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
        Observable<List<Object>> observable = siteService.getRqsc(params,
                RqmdyActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();
                Toast.makeText(RqmdyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                resultList = obj;

//         obj 返回类型       MM007品号,MM008品名,MM009规格,MB409容器名称,MB410收容数
                //List<Object> o=obj;
//                Toast.makeText(RqmdyActivity.this, "" + obj.size(), Toast.LENGTH_LONG).show();
                tv_cpph.setText(obj.get(0).toString());
                tv_cppm.setText(obj.get(1).toString());
                tv_cpgg.setText(obj.get(2).toString());
//                tv_rqlx.setText(obj.get(3).toString());
                tv_bzsl.setText(obj.get(4).toString());
                tv_rqlx.setText("容器一");
//                et_lx.setText(obj.get(0).toString());//[0]转移类型正常转移
//                et_jd.setText(obj.get(1).toString());//[1]开始
//                tv_gdxx.setText(obj.get(2).toString()+"-"+obj.get(3).toString());//[2]单别-[3]单号
//                tv_cpph.setText(obj.get(4).toString());//[4]产品品号
//                tv_cppm.setText(obj.get(5).toString());//[5]产品品名
//                tv_cpgg.setText(obj.get(6).toString());////[6]产品规格
//                //str_jgsx=obj.get(7).toString();//加工顺序
//                tv_jggy.setText(obj.get(8).toString());//[8]工艺名称
//                tv_jggx.setText(obj.get(7).toString());//[7]加工顺序 (加工工序)()
//                tv_jgzx.setText(obj.get(9).toString());//[9]工作中心/供应商名称
//                str_jyfs=obj.get(10).toString();//TA033[10]检验方式 0:免检、1:抽检(减量)、2:抽检(正常)、3:抽检(加严)、4:全检
//                str_gyxz=obj.get(11).toString();//[11]工艺性质 1.厂内工艺、2.委外工艺
//                str_gy=obj.get(12).toString();//[12]所在工艺(工艺编号)MW001
//                str_zxbh=obj.get(13).toString();//[13]工作中心/供应商编号MW005
//                str_MX021=obj.get(14).toString();//[14]MX021第一次扫或最新工序描此处传入空值
//                str_MX026=obj.get(15).toString();//开始时间
//                str_MX027=obj.get(16).toString();//结束时间
//
//                str_MX022=obj.get(17).toString();
//                if(obj.get(11).toString().equals("2")&&obj.get(1).toString().equals("开始")){
//                    //工艺性质是委外，而且是开始才 弹出提示框是否更换供应商
//                    //如果是更换供应商增需要再次选择供应商
//                    //修改供应商值即可
//                    dialogShow();
//                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_sc:
                if (TextUtils.isEmpty(et_tm.getText().toString().trim()) || resultList == null || resultList.size() <= 0) {
                    Toast.makeText(RqmdyActivity.this, "请扫描条码", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, Object> params = new HashMap<>();
                //生成条码时就直接保存了
                List<Object> list = new ArrayList<>();
                list.add(et_tm.getText().toString().trim());//产品条码
                list.add("");//容器条码
                list.add(tv_rqlx.getText().toString().trim());//容器名称?
                list.add(resultList.get(4));//标准收容数

                params.put("MM007", resultList.get(0).toString().trim());//品号
                params.put("XX002",  et_tm.getText().toString().trim());//产品条码
                params.put("data", ai.getData());
                params.put("PDARQXX", list);
                params.put("strToken", "");
                params.put("strVersion", "");
                params.put("strPoint", "");
                params.put("strActionType", "1001");
                String IP = "";
                String port = "";
                if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
                    IP = application.getUrl();
                    port = application.getPort();
                }
                siteService = SiteService.getInstants();
                Observable<String> observable = siteService.getDM_Save(params, RqmdyActivity.this, IP, port);
                observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        siteService.closeParentDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        siteService.closeParentDialog();
                        Toast.makeText(RqmdyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String str) {
                        if (TextUtils.isEmpty(str)) {
                            return;
                        }

                        Intent intent = new Intent();
                        Log.e("######", str);
//                intent.putExtras(getIntent().getExtras());
                        intent.setClass(RqmdyActivity.this, RqmdyConfirmActivity.class);
                        intent.putExtra("dm", str);
                        //[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                        intent.putExtra("result", (Serializable) resultList);
                        intent.putExtra("cptm", et_tm.getText().toString().trim());
                        SQLiteUtils instance = SQLiteUtils.getInstance(RqmdyActivity.this);
                        instance.saveDm(str, SQLiteUtils.RQM, et_tm.getText().toString().trim(), resultList.toString());
                        startActivity(intent);

                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                    }
                });

                break;
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

    //重写   处理返回信息的监听（回调方法）
    //onActivityResult通用监听  监听所有返回信息的
    //必须要有requestCode区分有哪个请求返回的
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
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
