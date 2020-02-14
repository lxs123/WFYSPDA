package com.cubertech.bhpda.Activity.RelevancePackage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.Adapter.QueryAdapter;
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
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class YjcxActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    private SiteService siteService;
    private Context mContext;

    AccountInfo ai;
    TKApplication application;

    private Button btn_query;

    private EditText et_ccp;
    private RelativeLayout xiangji;
    private EditText et_value;

    public static int flag = R.id.et_ccp;
    public static String hint = "产成品条码";

    private RecyclerView recycler;
    private QueryAdapter queryAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yjcx);
        this.mContext = YjcxActivity.this;
        initView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //todo
        ai = bundle.getParcelable("account");
        application = (TKApplication) getApplication();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        //原
        EditText viewById = (EditText) findViewById(flag);
        if (viewById.getText().toString().equals("") || viewById.getText().toString() == null) {
            //viewById.setText(null);
            //viewById.clearComposingText();
            viewById.setHint(hint);
        }

        //现
        int id = view.getId();
        EditText editText = (EditText) this.findViewById(id);
        //editText.setText(" ");
        hint = editText.getHint().toString();
        editText.setHint("");

        flag = id;
    }

    private void initView() {
        xiangji = (RelativeLayout) findViewById(R.id.del1);
        et_ccp = (EditText) findViewById(R.id.et_ccp);
        et_value = (EditText) findViewById(R.id.et_value);
        et_ccp.setOnFocusChangeListener(this);
        et_value.setOnFocusChangeListener(this);
        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recycler = (RecyclerView) findViewById(R.id.recycler_id);

        xiangji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setClass(getActivity(), CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivityForResult(intent, 1);*/
                IntentIntegrator intentIntegrator = new IntentIntegrator(YjcxActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class);// 设置自定义的activity是ScanActivity
                Intent scanIntent = intentIntegrator.createScanIntent();
                startActivityForResult(scanIntent, 1);

            }
        });

        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        //添加分隔线
        //recycler_r.addItemDecoration(new AdvanceDecoration(this, OrientationHelper.VERTICAL));

        ArrayList<Map<String, Object>> mapdata = new ArrayList<Map<String, Object>>();
        queryAdapter = new QueryAdapter(mContext, mapdata);
        recycler.setAdapter(queryAdapter);

        btn_query = (Button) findViewById(R.id.btn_query);
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query(null);
                //Toast.makeText(mContext, "弹出测试!", Toast.LENGTH_LONG).show();

                //test();//弹出测试代码
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }

    /**
     * 查询
     *
     * @param v
     */
    private void query(View v) {
        String zjcode = et_ccp.getText().toString().trim();
        if (zjcode.equals("")) {
            Toast.makeText(mContext, "产成品条码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        String value = et_value.getText().toString().trim();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("zjtm", zjcode);
        params.put("value", value);
        params.put("data", ai.getData());
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
        Observable<List<Object>> observable = siteService.query(params,
                mContext, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(mContext, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                ArrayList<Map<String, Object>> mapdata = new ArrayList<Map<String, Object>>();
                for (Object o : obj) {
                    List<Object> oo = (List<Object>) o;

                    Map<String, Object> item;

                    item = new HashMap<String, Object>();
                    item.put("cpph", oo.get(0).toString());
                    item.put("cpmc", oo.get(1).toString());
                    item.put("yjph", oo.get(2).toString());
                    item.put("yjmc", oo.get(3).toString());
                    item.put("yjcs", oo.get(4).toString());
                    mapdata.add(item);
                }
                queryAdapter.addItems(mapdata);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        if (requestCode == 1) {
            et_ccp.setText(ScanResult);
        }
    }
}
