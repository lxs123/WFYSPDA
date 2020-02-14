package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 上架操作界面
 * Created by Administrator on 2018/7/9.
 */

public class SjczActivity extends Activity implements View.OnClickListener {
    private boolean isNow;
    private EditText et_kw;
    private EditText et_tp;

    private EditText et_tm;
    private TextView tv_cpph;
    private TextView tv_cppm;
    private TextView tv_cpgg;
    private TextView et_zysl;

    private boolean isDialog;

    private AlertDialog.Builder builder;

    private List<Map<String, String>> list = new ArrayList<>();

    private SjczAdapter adapter;
    private RecyclerView Sjcz_recycle;

    private int anInt;
    private SiteService siteService;
    private TKApplication application;
    AccountInfo ai;

    private String ckbh;//仓库编号

    //kuweitiaoma 库位条码
    //tuopantiaoma 托盘条码
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjcz);
        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(this);
        //Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        //btn_cancel.setOnClickListener(this);
        Button btn_true = (Button) findViewById(R.id.btn_true);
        btn_true.setOnClickListener(this);
        application = (TKApplication) getApplication();
        Button btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        et_kw = (EditText) findViewById(R.id.et_kw);
        et_tp = (EditText) findViewById(R.id.et_tp);

        Sjcz_recycle = (RecyclerView) findViewById(R.id.sjcz_recycle);
        Sjcz_recycle.setLayoutManager(new LinearLayoutManager(this));
//        cjcz_recycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new SjczAdapter();
        Sjcz_recycle.setAdapter(adapter);

        et_kw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanResult();//
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                String tm= et_kw.getText().toString().trim();
                if(tm.equals("")){
                    Toast.makeText(SjczActivity.this,"请先扫描库位码",Toast.LENGTH_LONG).show();
                    return;
                }
                onShowDialog(null);
                break;
            case R.id.btn_true:
                List<Map<String, String>> list = onDataFinished(view);
                if (ListUtils.isEmpty(list)) {
                    Toast.makeText(SjczActivity.this, "请先扫码！", Toast.LENGTH_LONG).show();
                    return;
                }
                //上架操作确定保存
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("确定执行上架操作吗?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        sjsave(list);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

                break;
            case R.id.left:
                onBackPressed();
                break;
        }
    }

    /**
     * 上架操作保存
     * @param list
     */
    private void sjsave(List<Map<String, String>> list){
        List<Object> l=new ArrayList<>();
        List<Object> all=new ArrayList<>();
        for(Map<String,String> map: list){
            //System.out.println(item);
            l.add(map.get("et_tm"));//唛箱头码或托盘码

            List<Object> o=new ArrayList<>();
            o.add(map.get("et_tm"));//唛箱头码或托盘码
            o.add(map.get("tv_cpph"));//品号
            o.add(map.get("et_zysl"));//数量
            //l.add(map.get("et_tm"));
            all.add(o);
        }

        String kwbh= et_kw.getText().toString().trim();


        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("list", l);
        params.put("alllist",all);
        params.put("kwbh",kwbh);//库位编号
        params.put("ckbh", ckbh);//仓库编号
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
        Observable<String> observable = siteService.SaveSJ(params,
                SjczActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                /*et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();*/
                Toast.makeText(SjczActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String obj) {
                if (!obj.equals("")) {
                    Toast.makeText(SjczActivity.this, "完成！", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

        });
    }

    private void scanResult(String et_tm) {
        Map<String, String> map = (Map<String, String>) list.get(anInt);
        map.put("et_tm", et_tm);
        map.put("tv_cpph", "直接填写扫描结果");
        map.put("tv_cppm", "直接填写扫描结果");
        map.put("tv_cpgg", "直接填写扫描结果");
        map.put("et_zysl", "直接填写扫描结果");

        adapter.setList(list);
    }

    public List<Map<String, String>> onDataFinished(View view) {
        List<Map<String, String>> list = adapter.getList();
        List<Map<String, String>> list1 = new ArrayList<>();
        if (ListUtils.isEmpty(list)) {
            return null;
        }
        for (int i = 0; i < ListUtils.getSize(list); i++) {
            Map<String, String> map = list.get(i);
            if (!TextUtils.isEmpty(map.get("et_tm"))) {
                list1.add(list.get(i));
            }

        }
        return list1;
    }

    public void onShowDialog(View view) {

        builder = new AlertDialog.Builder(SjczActivity.this);
        View view1 = LayoutInflater.from(SjczActivity.this).inflate(R.layout.listitem_cjcz_left_adapter, null);
        Button btn_xiangji = (Button) view1.findViewById(R.id.del1);
        TextView tv_tm_title = (TextView) view1.findViewById(R.id.listitem_tm_title);
        tv_tm_title.setText("扫      码");
        et_tm = (EditText) view1.findViewById(R.id.et_tm);
        tv_cpph = view1.findViewById(R.id.tv_cpph);
        tv_cppm = view1.findViewById(R.id.tv_cppm);
        tv_cpgg = view1.findViewById(R.id.tv_cpgg);
        et_zysl = view1.findViewById(R.id.et_zysl);
        et_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    scanResultDialog();
                }
                return false;
            }
        });
        btn_xiangji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialog = true;
                xiangji(view);
            }
        });
        builder.setTitle("扫描上架箱头或托盘码");

        builder.setView(view1);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setTextColor(Color.parseColor("#33b4e4"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(et_tm.getText().toString())) {
                            ToastUtils.showToast("请先扫描! ");
                            return;
                        } else if (TextUtils.isEmpty(tv_cpph.getText().toString())) {
                            ToastUtils.showToast("请先扫描! ");
                            return;
                        }
                        Map<String, String> map = new HashMap<>();
                        map.put("et_tm", et_tm.getText().toString());
                        map.put("tv_cpph", tv_cpph.getText().toString());
                        map.put("tv_cppm", tv_cppm.getText().toString());
                        map.put("tv_cpgg", tv_cpgg.getText().toString());
                        map.put("et_zysl", et_zysl.getText().toString());
                        list.add(map);

                        adapter.setList(list);
                        Sjcz_recycle.scrollToPosition(adapter.getItemCount() - 1);
                        dialog.dismiss();
                    }
                });
                Button button1 = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                button1.setTextColor(Color.parseColor("#33b4e4"));
            }
        });
        dialog.show();

//        builder.show();

    }

    private void scanResultDialog() {


        String t=et_tm.getText().toString().trim();
        if(t.equals("")){
            Toast.makeText(SjczActivity.this,"请先扫码",Toast.LENGTH_LONG).show();

            return;
        }
        /*if(!t.startsWith("TMT")){
            Toast.makeText(SjczActivity.this,"只能扫描托盘码！",Toast.LENGTH_LONG).show();
            et_tm.setText("");
            return;
        }*/

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", t);
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
        Observable<List<Object>> observable = siteService.getSJ(params,
                SjczActivity.this, IP, port);
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
                ToastUtils.showToastLong(e.getMessage());
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                /*tv_cpph.setText("cpph" + adapter.getItemCount());
                  tv_cppm.setText("cppm" + adapter.getItemCount());
                  tv_cpgg.setText("cpgg" + adapter.getItemCount());
                  et_zysl.setText("number" + adapter.getItemCount());*/
                tv_cpph.setText(obj.get(0).toString());//[0]品号
                tv_cppm.setText(obj.get(1).toString());//[1]品名
                tv_cpgg.setText(obj.get(2).toString());//[2]规格
                et_zysl.setText(obj.get(3).toString());//[3]数量

            }

        });

    }

    private void scanResult() {
        String tm= et_kw.getText().toString().trim();
        if(tm.equals("")){
            Toast.makeText(SjczActivity.this,"请先扫描库位码",Toast.LENGTH_LONG).show();
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
        Observable<List<Object>> observable = siteService.getKW(params,
                SjczActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_kw.setText("");
                et_kw.setSelection(0);
                et_kw.requestFocus();
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                /*tv_cpph.setText("cpph" + adapter.getItemCount());
                  tv_cppm.setText("cppm" + adapter.getItemCount());
                  tv_cpgg.setText("cpgg" + adapter.getItemCount());
                  et_zysl.setText("number" + adapter.getItemCount());*/
                /*tv_cpph.setText(obj.get(0).toString());//[0]品号
                tv_cppm.setText(obj.get(1).toString());//[1]品名
                tv_cpgg.setText(obj.get(2).toString());//[2]规格
                et_zysl.setText("0");//[3]数量*/

                //list.Add(model.NI001);//[0]仓库编号
                //list.Add(model.NI002);//[1]库位编号

                ckbh=obj.get(0).toString().trim();//仓库编号

            }

        });

    }

    public void xiangji(View view) {
        isNow = true;
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        onXiangji();
    }

    private void onXiangji() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class);
        Intent scanIntent = intentIntegrator.createScanIntent();
        startActivityForResult(scanIntent, 999);
    }

    public void xiangji1(View view) {
        isNow = false;
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        onXiangji();
    }

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
            if (isNow) {
                et_tm.setText(ScanResult);
                scanResultDialog();
            } else {
                et_kw.setText(ScanResult);
                scanResult();
            }
        }
    }

    public class SjczAdapter extends RecyclerView.Adapter<SjczAdapter.ViewHolder> {
        private List<Map<String, String>> list = new ArrayList<>();

        public SjczAdapter() {
        }

        public void setList(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<Map<String, String>> getList() {
            return list;
        }

        public SjczAdapter(List<Map<String, String>> list) {
            this.list = list;
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_cjcz_left_adapter, null, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.et_tm.setFocusable(false);
            holder.et_tm.setFocusableInTouchMode(false);

            holder.listitem_tm_title.setText("码      值");

            holder.xiangji.setBackgroundResource(R.mipmap.ic_delete);//adaptter应用是把相机修改成立删除按钮
            holder.xiangji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SjczActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("确定要删除该条数据么?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            list.remove(position);
                            notifyDataSetChanged();

                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
//                    isDialog = false;
//                    anInt = position;
//                    xiangji(view);

                }
            });
            holder.tv_tm_title.setText("唛头码 ");
            Map<String, String> o = (Map<String, String>) list.get(position);
            holder.et_tm.setText(o.get("et_tm"));
            holder.tv_cpph.setText(o.get("tv_cpph"));
            holder.tv_cppm.setText(o.get("tv_cppm"));
            holder.tv_cpgg.setText(o.get("tv_cpgg"));
            holder.et_zysl.setText(o.get("et_zysl"));
            holder.et_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
                    // ACTION_DOWN时也触发
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//                        Map<String, String> map = (Map<String, String>) list.get(position);
//                        map.put("et_tm", holder.et_tm.getText().toString());
                        anInt = position;
                        scanResult(holder.et_tm.getText().toString());
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView listitem_tm_title;//原值 “箱唛头码”
            private Button xiangji;
            private EditText et_tm;
            private TextView tv_cpph;
            private TextView tv_cppm;
            private TextView tv_cpgg;
            private TextView et_zysl;

            private TextView tv_tm_title;

            public ViewHolder(View itemView) {
                super(itemView);


                listitem_tm_title = (TextView) itemView.findViewById(R.id.listitem_tm_title); //原值 “箱唛头码”

                xiangji = (Button) itemView.findViewById(R.id.del1);
                et_tm = (EditText) itemView.findViewById(R.id.et_tm);
                tv_cpph = itemView.findViewById(R.id.tv_cpph);
                tv_cppm = itemView.findViewById(R.id.tv_cppm);
                tv_cpgg = itemView.findViewById(R.id.tv_cpgg);
                et_zysl = itemView.findViewById(R.id.et_zysl);
                tv_tm_title = (TextView) itemView.findViewById(R.id.listitem_tm_title);
            }
        }
    }
}
