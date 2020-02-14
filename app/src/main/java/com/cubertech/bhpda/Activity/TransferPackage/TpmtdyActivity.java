package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
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

import com.cubertech.bhpda.Activity.Adapter.TpmtdyAdapter;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.SQLiteUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class TpmtdyActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView left;
    private EditText et_value;
    private SiteService siteService;

    private RecyclerView recycler;
    private TpmtdyAdapter tpmtdyAdapter;
    private LinearLayoutManager linearLayoutManager;

    AccountInfo ai;
    private TKApplication application;
    private String cpVersion;
    private String partNumber;
    private String drawNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpmtdy);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        application = (TKApplication) getApplication();

        recycler = (RecyclerView) findViewById(R.id.recycler_id);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        //添加分隔线
        //recycler_r.addItemDecoration(new AdvanceDecoration(this, OrientationHelper.VERTICAL));

        ArrayList<Map<String, Object>> mapdata = new ArrayList<Map<String, Object>>();
        tpmtdyAdapter = new TpmtdyAdapter(this, mapdata, new TpmtdyAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //chooseData(position);
                delItem(position);
            }
        });
        recycler.setAdapter(tpmtdyAdapter);

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
                    //Toast.makeText(GxzyActivity.this, "执行读取数据库操作", Toast.LENGTH_SHORT).show();
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

        SQLiteUtils.getInstance(TpmtdyActivity.this).getStatus(SQLiteUtils.TPMT);
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
     * 扫描容器码
     */
    public void scanCode() {
        if (!et_value.getText().toString().trim().contains("XMT-")) {
            ToastUtils.showToast("箱唛头码格式不正确");
            et_value.setText("");
            return;
        }
        if (!ListUtils.isEmpty(tpmtdyAdapter.getData())) {
            ArrayList data = tpmtdyAdapter.getData();
            String o = data.get(0).toString();
            try {
                String[] split = o.split("-");
                String trim = et_value.getText().toString().trim();
                String[] split1 = trim.split("-");
                if (!TextUtils.equals(split[1], split1[1])) {
                    ToastUtils.showToast("请扫描相同品号的箱唛头码");
                    et_value.setText("");
                    return;
                }
                for (int i = 0; i < ListUtils.getSize(data); i++) {
                    if (TextUtils.equals(data.get(i).toString(), trim)) {
                        ToastUtils.showToast("箱唛头码不能重复");
                        et_value.setText("");
                        return;
                    }
                }

            } catch (Exception e) {
                ToastUtils.showToast("箱唛头码格式不正确");
                e.printStackTrace();
                et_value.setText("");
                return;
            }

        }

        //===================
        HashMap<String, Object> params = new HashMap<>();
        params.put("data", ai.getData());
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
        Observable<List<Object>> observable = siteService.getXMT(params, TpmtdyActivity.this, IP, port);
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
                Toast.makeText(TpmtdyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> str) {
                if (ListUtils.isEmpty(str)) {
                    return;
                }
                Log.e("#####", str.toString());
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("mtm", et_value.getText().toString().trim());
                data.put("mtmcount", str.get(3).toString());//[3]数量
                //[0]产品品号[1]产品品名 [4]产品规格 (图号)  [3] 数量[2] 产品版本[5] 零件号
                cpVersion = str.get(2).toString().trim();
                partNumber = str.get(5).toString().trim();
                drawNumber = str.get(4).toString().trim();

                //服务的判断是否是容器码
                tpmtdyAdapter.addItem(data);

                et_value.setText("");
                et_value.setFocusable(true);
                et_value.setFocusableInTouchMode(true);
                et_value.requestFocus();


            }
        });


    }


    public void delItem(int p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TpmtdyActivity.this);
        builder.setMessage("确定删除吗？");
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tpmtdyAdapter.delItem(p);
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
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

    //002
    //140  7558
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (tpmtdyAdapter.getData() == null || tpmtdyAdapter.getData().size() == 0) {
                    Toast.makeText(TpmtdyActivity.this, "请扫描条码", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> params = new HashMap<>();
                //生成条码时就直接保存了
               /* List<Object> list = new ArrayList<>();
                list.add(et_tm.getText().toString().trim());//产品条码
                list.add("");//容器条码
                list.add(tv_rqlx.getText().toString().trim());//容器名称?
                list.add(resultList.get(4));//标准收容数

                params.put("MM007", resultList.get(0).toString().trim());//品号
                params.put("XX002",  et_tm.getText().toString().trim());//产品条码
                params.put("data", ai.getData());
                params.put("PDARQXX", list);*/

                /*for(int x:arr) {
                    26             System.out.print(x+" ");
                    27         }*/
//
//                List<Object> list = new ArrayList<>();
//                for (List<Object> x : uplist) {
//                    list.add(x.get(0));
//                }
//                List<Object> info = new ArrayList<>();//[0]产品品号 [1]类型 [2]数量
//                info.add(tv_cpph.getText().toString().trim());//[0]产品品号
//                info.add(tv_mtlx.getText().toString().trim());//[1]类型
//                info.add(et_zxsl.getText().toString().trim());//[2]数量

                params.put("list", tpmtdyAdapter.getData());
//                params.put("type", tv_mtlx.getText().toString());
//                params.put("info", info);//[0]产品品号 [1]类型 [2]数量
                params.put("data", ai.getData());
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
                Observable<List<Object>> observable = siteService.getTP_Save(params, TpmtdyActivity.this, IP, port);
                observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
                    @Override
                    public void onCompleted() {
                        siteService.closeParentDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        siteService.closeParentDialog();
                        Toast.makeText(TpmtdyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(List<Object> str) {
                        if (ListUtils.isEmpty(str)) {
                            return;
                        }
                        //Toast.makeText(TpmtdyActivity.this, "成功查看数据库数据", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent();
                        Log.e("######", str.toString());
                        //intent.putExtras(getIntent().getExtras());
                        intent.setClass(TpmtdyActivity.this, XmtdyConfirmActivity.class);
                        intent.putExtra("dm", str.get(0).toString());
                        //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                        //新值[0]数量[1]类型(箱/托)[2]版本号[3]图号(规格)[4]零件号
                        //最新版[0]托盘码 [1]时间
                        List<Object> resultList = new ArrayList<>();
                        resultList.add(tpmtdyAdapter.getNumCount());//[0]数量
                        resultList.add("托");//[1]箱/托
                        resultList.add(cpVersion);//[2]版本
                        resultList.add(drawNumber);//[3]图号
                        resultList.add(partNumber);//[4]零件号
                        resultList.add(str.get(0).toString().trim());//[5]托盘码
                        resultList.add(str.get(1).toString().trim());//[6]时间
//                        float f = Float.parseFloat(et_zxsl.getText().toString().trim());
//                        resultList.add((int) f);//[0]数量
//                        resultList.add(tv_mtlx.getText().toString().trim());//[1]类型(箱/托)
//                        resultList.add(cpbb);//[2]版本号
//                        resultList.add(tv_cpgg.getText().toString().trim());//[3]图号(规格)
//                        resultList.add(ljh);//[4]零件号
                        intent.putExtra("result", (Serializable) resultList);
                        //intent.putExtra("cptm", et_tm.getText().toString().trim());


                        intent.setClass(TpmtdyActivity.this, TpmtdyConfirmActivity.class);
                        SQLiteUtils instance = SQLiteUtils.getInstance(TpmtdyActivity.this);
                        instance.saveDm(str.get(0).toString(), SQLiteUtils.TPMT, "", str.toString());
                        intent.putExtra("data", tpmtdyAdapter.getData());
                        startActivity(intent);
//                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


                    }
                });


                break;
        }
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

}
