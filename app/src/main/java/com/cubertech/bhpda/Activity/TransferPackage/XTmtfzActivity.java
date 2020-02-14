package com.cubertech.bhpda.Activity.TransferPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.SQLiteUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.TimeUtil;
import com.cubertech.bhpda.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class XTmtfzActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView left;
    private EditText et_value;
    private SiteService siteService;
    private TKApplication application;
    private AccountInfo ai;
    TextView tv_sl,tv_xort,tv_bb,tv_th,tv_ljh;
    TextView tv_rq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtmtfz);

        tv_rq=(TextView) findViewById(R.id.tv_rq);
        tv_rq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });
        tv_sl=(TextView)findViewById(R.id.tv_sl);
        tv_xort=(TextView)findViewById(R.id.tv_xort);
        tv_bb=(TextView)findViewById(R.id.tv_bb);
        tv_th=(TextView)findViewById(R.id.tv_th);
        tv_ljh=(TextView)findViewById(R.id.tv_ljh);

        et_value = (EditText) findViewById(R.id.et_value);
        et_value.setSelection(0);
        et_value.requestFocus();

        et_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
        Button btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");

        application = (TKApplication) getApplication();



    }

    public void datePicker(){
        Calendar calendar=Calendar.getInstance();

        //TimeUtil.showDatePickerDialog(this,0,tv_rq,calendar);
       // TimeUtil.showDatePickerDialog(this,1,tv_rq,calendar);
        //TimeUtil.showDatePickerDialog(this,2,tv_rq,calendar);
        TimeUtil.showDatePickerDialog(this,3,tv_rq,calendar);
        //TimeUtil.showDatePickerDialog(this,4,tv_rq,calendar);
        //TimeUtil.showDatePickerDialog(this,5,tv_rq,calendar);
    }

    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class);
        Intent scanIntent = intentIntegrator.createScanIntent();
        startActivityForResult(scanIntent, 999);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (TextUtils.isEmpty(et_value.getText().toString().trim()) || TextUtils.isEmpty(tv_xort.getText().toString().trim())) {
                    Toast.makeText(XTmtfzActivity.this, "请扫描条码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newrq=tv_rq.getText().toString().trim();
                if(newrq.length()!=8){
                    Toast.makeText(XTmtfzActivity.this, "日期不正确！", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code=et_value.getText().toString().trim();
                String[] c=code.split("-");
                String ls=c[2].substring(8,12);
                String newcode=c[0]+"-"+c[1]+"-"+newrq+ls;

                if(!code.trim().equals(newcode.trim())){
                    updataFZMT(code.trim(),newcode.trim());
                    break;
                }

                if(code.toString().trim().contains("XMT")){
                    //复制箱码
                    Intent intent = new Intent();
                    intent.setClass(XTmtfzActivity.this, XmtdyConfirmActivity.class);
                    intent.putExtra("dm", newcode);
                    List<Object> resultList = new ArrayList<>();

                    resultList.add(tv_sl.getText().toString().trim());//[0]数量
                    resultList.add(tv_xort.getText().toString().trim());//[1]类型(箱/托)
                    resultList.add(tv_bb.getText().toString().trim());//[2]版本号
                    resultList.add(tv_th.getText().toString().trim());//[3]图号(规格)
                    resultList.add(tv_ljh.getText().toString().trim());//[4]零件号
                    resultList.add(tv_rq.getText().toString().trim());//[5]日期
                    intent.putExtra("result", (Serializable) resultList);
                    startActivity(intent);
                    //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else{
                    Intent intent1 = new Intent();

//                    intent1.setClass(XTmtfzActivity.this, XmtdyConfirmActivity.class);
                    intent1.putExtra("dm", newcode);
                    //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                    //新值[0]数量[1]类型(箱/托)[2]版本号[3]图号(规格)[4]零件号
                    //最新版[0]托盘码 [1]时间
                    List<Object> resultList = new ArrayList<>();
                    resultList.add(tv_sl.getText().toString().trim());//[0]数量
                    resultList.add("托");//[1]箱/托
                    resultList.add(tv_bb.getText().toString().trim());//[2]版本
                    resultList.add(tv_th.getText().toString().trim());//[3]图号
                    resultList.add(tv_ljh.getText().toString().trim());//[4]零件号
                    resultList.add(newcode);//[5]托盘码
                    resultList.add(tv_rq.getText().toString().trim());//[6]时间
                    intent1.putExtra("result", (Serializable) resultList);
                    intent1.setClass(XTmtfzActivity.this, TpmtdyConfirmActivity.class);
                    startActivity(intent1);
                    //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

                break;
        }

    }

    private void updataFZMT(String code,String newcode){
        HashMap<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("newcode", newcode);
        params.put("database", ai.getData());
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
        Observable<List<Object>> observable = siteService.updataFZMT(params, XTmtfzActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(XTmtfzActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> str) {
                if (ListUtils.isEmpty(str)) {
                    return;
                }

                if(str.get(0).toString().trim().contains("XMT")){
                    //复制箱码
                    Intent intent = new Intent();
                    intent.setClass(XTmtfzActivity.this, XmtdyConfirmActivity.class);
                    intent.putExtra("dm", newcode);
                    List<Object> resultList = new ArrayList<>();

                    resultList.add(tv_sl.getText().toString().trim());//[0]数量
                    resultList.add(tv_xort.getText().toString().trim());//[1]类型(箱/托)
                    resultList.add(tv_bb.getText().toString().trim());//[2]版本号
                    resultList.add(tv_th.getText().toString().trim());//[3]图号(规格)
                    resultList.add(tv_ljh.getText().toString().trim());//[4]零件号
                    resultList.add(tv_rq.getText().toString().trim());//[5]日期
                    intent.putExtra("result", (Serializable) resultList);
                    startActivity(intent);
                    //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }else{
                    Intent intent1 = new Intent();
                    Log.e("######", str.toString());
                    //intent.putExtras(getIntent().getExtras());
                    intent1.setClass(XTmtfzActivity.this, XmtdyConfirmActivity.class);
                    intent1.putExtra("dm", str.get(0).toString());
                    //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                    //新值[0]数量[1]类型(箱/托)[2]版本号[3]图号(规格)[4]零件号
                    //最新版[0]托盘码 [1]时间
                    List<Object> resultList = new ArrayList<>();
                    resultList.add(tv_sl.getText().toString().trim());//[0]数量
                    resultList.add("托");//[1]箱/托
                    resultList.add(tv_bb.getText().toString().trim());//[2]版本
                    resultList.add(tv_th.getText().toString().trim());//[3]图号
                    resultList.add(tv_ljh.getText().toString().trim());//[4]零件号
                    resultList.add(newcode);//[5]托盘码
                    resultList.add(tv_rq.getText().toString().trim());//[6]时间
                    intent1.putExtra("result", (Serializable) resultList);
                    intent1.setClass(XTmtfzActivity.this, TpmtdyConfirmActivity.class);
                    //intent.putExtra("data", tpmtdyAdapter.getData());
                    startActivity(intent1);
                    //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }


            }
        });
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
            et_value.setText(ScanResult);

            scanCode();
        }
    }

    /**
     * 扫描箱唛头或托盘唛头
     */
    public void scanCode() {
        String code=et_value.getText().toString().trim();
        if (!code.contains("MT-")) {
            ToastUtils.showToast("唛头码格式不正确！");
            et_value.setText("");
            return;
        }

        //===================
        HashMap<String, Object> params = new HashMap<>();
        params.put("database", ai.getData());
        params.put("code", et_value.getText().toString().trim());
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
        //(HashMap<String, Object> params, Context context, String ip, String port)
        Observable<List<Object>> observable = siteService.getFZMT(params, XTmtfzActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_value.setText("");
                et_value.setFocusable(true);
                et_value.setFocusableInTouchMode(true);
                et_value.requestFocus();
                Toast.makeText(XTmtfzActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> str) {
                if (ListUtils.isEmpty(str)) {
                    return;
                }
                String[] c=code.split("-");
                String rq=c[2].substring(0,8);
                tv_rq.setText(rq);//日期
                tv_sl.setText(str.get(3).toString());//数量
                if (code.contains("XMT")){
                    tv_xort.setText("箱");//箱/托
                }else{
                    tv_xort.setText("托");
                }

                tv_bb.setText(str.get(2).toString().trim());//版本
                tv_th.setText(str.get(4).toString().trim());//图号
                tv_ljh.setText(str.get(5).toString().trim());//零件号

               /* Log.e("#####", str.toString());
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("mtm", et_value.getText().toString().trim());
                data.put("mtmcount", str.get(3).toString());//[3]数量
                //[0]产品品号[1]产品品名 [4]产品规格 (图号)  [3] 数量[2] 产品版本[5] 零件号
                cpVersion = str.get(2).toString().trim();
                partNumber = str.get(5).toString().trim();
                drawNumber = str.get(4).toString().trim();

                //服务的判断是否是容器码
                tpmtdyAdapter.addItem(data);*/

                //et_value.setText("");
                et_value.setFocusable(true);
                et_value.setFocusableInTouchMode(true);
                et_value.requestFocus();


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
