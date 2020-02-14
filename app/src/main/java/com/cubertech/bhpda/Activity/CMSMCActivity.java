package com.cubertech.bhpda.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.Adapter.CMSMCAdapter;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

//选择仓库的Activity
public class CMSMCActivity extends AppCompatActivity {
    private SiteService siteService;

    private RecyclerView recycler;
    private CMSMCAdapter cmsmcAdapter;
    private LinearLayoutManager linearLayoutManager;

    private Button btn_submit;
    private RelativeLayout rl_query;
    private EditText et_value;


    AccountInfo ai;
    private TKApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmsmc);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        application = (TKApplication)getApplication();

        et_value=(EditText)findViewById(R.id.et_value);

        btn_submit=(Button)findViewById(R.id.btn_submit);
        rl_query=(RelativeLayout)findViewById(R.id.rl_query);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querySupplier();
            }
        });
        rl_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querySupplier();
            }
        });

        recycler=(RecyclerView) findViewById(R.id.recycler_id);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        //添加分隔线
        //recycler_r.addItemDecoration(new AdvanceDecoration(this, OrientationHelper.VERTICAL));


        ArrayList<Map<String, Object>> mapdata = new ArrayList<Map<String, Object>>();
        cmsmcAdapter=new CMSMCAdapter (this,mapdata,new CMSMCAdapter.OnRecyclerItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                chooseData(position);
            }
        });
        recycler.setAdapter(cmsmcAdapter);

        querySupplier();
    }

    /**
     * 查询仓库数据
     */
    private void querySupplier(){
        cmsmcAdapter.delAll();//清空所有数据
        String condition=et_value.getText().toString().trim();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("database", ai.getData());
        params.put("condition", condition);
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
        Observable<List<Object>> observable = siteService.getCMSMC(params,
                CMSMCActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(CMSMCActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    Toast.makeText(CMSMCActivity.this, "不存在相关仓库数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Map<String, Object>> mapdata = new ArrayList<Map<String, Object>>();
                Map<String, Object> item;
                for (Object o : obj) {
                    List<Object> oo = (List<Object>) o;
                    item = new HashMap<String, Object>();
                    item.put("bh",oo.get(0).toString());//编号
                    item.put("mc",oo.get(1).toString());//名称
                    mapdata.add(item);
                }
                cmsmcAdapter.addItems(mapdata);
            }
        });
    }

    /**
     * 选择供应商
     * @param position
     */
    private void chooseData(int position) {
        Map<String, Object> item = cmsmcAdapter.getItem(position);
        LayoutInflater flater = getLayoutInflater();
        final View view = flater.inflate(R.layout.choose_data,
                (ViewGroup) findViewById(R.id.dialog));
        AlertDialog.Builder dialog = new AlertDialog.Builder(CMSMCActivity.this);
        dialog.setTitle("确认选择");

        TextView tv_gysbh = (TextView) view.findViewById(R.id.tv_gysbh);
        TextView tv_gysmc = (TextView) view.findViewById(R.id.tv_gysmc);

        tv_gysbh.setText(item.get("bh").toString());
        tv_gysmc.setText(item.get("mc").toString());
        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra("MC001", item.get("bh").toString());//仓库编号
                intent.putExtra("MC002", item.get("mc").toString());//仓库名称
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
