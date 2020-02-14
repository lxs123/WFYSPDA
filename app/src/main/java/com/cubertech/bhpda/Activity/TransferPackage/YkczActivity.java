package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * 移库操作界面
 * Created by Administrator on 2018/7/9.
 */

public class YkczActivity extends Activity implements View.OnClickListener {
    private boolean isNow = false;
    private EditText et_dqkw;//当前库码
    private EditText et_mdkw;//目的库码
    private TextView tv_dqcpph;
    private TextView tv_mdkwcpph;
    private TextView tv_dqcppm;
    private TextView tv_mdkwcppm;
    private SiteService siteService;
    private TKApplication application;
    private AccountInfo ai;
    private RecyclerView ykcz_recycle;
    private AlertDialog.Builder builder;
    private EditText et_tm;
    private TextView tv_cpph;
    private TextView tv_cppm;
    private TextView tv_cpgg;
    private TextView et_zysl;
    private boolean isDialog;
    private YkczAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<>();

    private String ckbh;//仓库编号

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ykcz);
        application = (TKApplication) getApplication();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(this);
        Button btn_addyk = (Button) findViewById(R.id.btn_addyk);
        btn_addyk.setOnClickListener(this);
        et_dqkw = (EditText) findViewById(R.id.et_dqkw);
        et_mdkw = (EditText) findViewById(R.id.et_mdkw);
        Button btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_dqcpph = (TextView) findViewById(R.id.tv_cpph);
        tv_mdkwcpph = (TextView) findViewById(R.id.tv_mdkwcpph);
        ykcz_recycle = (RecyclerView) findViewById(R.id.ykcz_recycle);
        ykcz_recycle.setLayoutManager(new LinearLayoutManager(this));
        adapter = new YkczAdapter();
        ykcz_recycle.setAdapter(adapter);
        tv_dqcppm = (TextView) findViewById(R.id.tv_cppm);
        tv_mdkwcppm = (TextView) findViewById(R.id.tv_mdkwcppm);
        et_dqkw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_DONE || (keyEvent != null
                        && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    showDqkwScan();
                }
                return false;
            }

        });
        et_mdkw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_DONE || (keyEvent != null
                        && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    showMdkwScan();
                }
                return false;
            }

        });

        et_mdkw.setSelection(0);
        et_mdkw.requestFocus();
        ckbh="";
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
            case R.id.left:
                onBackPressed();
                break;
            case R.id.btn_addyk:
                if(ckbh.equals("")){
                    showMdkwScan();
                }
                onShowDialog(view);
                break;
            case R.id.btn_submit:
                String str_mdkw = et_mdkw.getText().toString();
                //String str_dqkw = et_dqkw.getText().toString();
//                if (TextUtils.isEmpty(str_dqkw)) {
//                    Toast.makeText(YkczActivity.this, "请扫描当前库位条码! ", Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (TextUtils.isEmpty(tv_dqcpph.getText().toString())) {
//                    Toast.makeText(YkczActivity.this, "请扫描当前库位条码! ", Toast.LENGTH_SHORT).show();
//                    return;
//                } else
                if (TextUtils.isEmpty(str_mdkw)) {
                    Toast.makeText(YkczActivity.this, "请扫描目的库位条码! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*else if (TextUtils.isEmpty(tv_mdkwcpph.getText().toString())) {
                    Toast.makeText(YkczActivity.this, "请扫描目的库位条码! ", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                List<Map<String, String>> list = onDataFinished(view);
                if (ListUtils.isEmpty(list)) {
                    Toast.makeText(YkczActivity.this, "请先扫码！", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("确定要转移库位么?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //sjsave(list);
                        yksave(list);
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
        }
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

    /**
     * 移库操作保存数据
     *
     * @param list
     */
    private void yksave(List<Map<String, String>> list) {
        List<Object> l = new ArrayList<>();
        for (Map<String, String> map : list) {
            //System.out.println(item);
            List<Object> o = new ArrayList<>();
            o.add(map.get("et_tm"));//唛箱头码或托盘码
            o.add(map.get("tv_cpph"));//品号
            o.add(map.get("et_zysl"));//数量
            //l.add(map.get("et_tm"));
            l.add(o);
        }

        String kwbh = et_mdkw.getText().toString().trim();


        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("list", l);
        params.put("kwbh", kwbh);//库位编号
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
        Observable<String> observable = siteService.SaveYK(params,
                YkczActivity.this, IP, port);
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
                Toast.makeText(YkczActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String obj) {
                if (!obj.equals("")) {
                    Toast.makeText(YkczActivity.this, "完成！", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

        });
    }

    public void xiangji(View view) {
        isNow = false;
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
        isNow = true;
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
                scanResultDialog();//scan
            } else {
                et_mdkw.setText(ScanResult);
                showMdkwScan();//scan
            }
        }
    }

    //暂时不调用===============================================
    private void showDqkwScan() {
        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("code", tm);
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
        Observable<List<Object>> observable = siteService.getHT(params,
                YkczActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_dqkw.setText("");
                et_dqkw.setSelection(0);
                et_dqkw.requestFocus();
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
                tv_dqcpph.setText("dqcpph");//产品品号
                tv_dqcppm.setText("dqcppm");//产品品名

            }

        });

    }

    public void onShowDialog(View view) {

        builder = new AlertDialog.Builder(YkczActivity.this);
        View view1 = LayoutInflater.from(YkczActivity.this).inflate(R.layout.listitem_cjcz_left_adapter, null);
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
                isNow = true;
                onXiangji();
//                xiangji(view);
            }
        });
        builder.setTitle("扫描托盘或唛箱头码");

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
                Button button = dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
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
                        ykcz_recycle.scrollToPosition(adapter.getItemCount() - 1);
                        dialog.dismiss();
                    }
                });
                Button button1 = dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE);
                button1.setTextColor(Color.parseColor("#33b4e4"));
            }
        });
        dialog.show();

//        builder.show();

    }

    private void scanResultDialog() {
        String t = et_tm.getText().toString().trim();
        if (t.equals("")) {
            Toast.makeText(YkczActivity.this, "请先扫码", Toast.LENGTH_LONG).show();
            return;
        }

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
        Observable<List<Object>> observable = siteService.getYK(params,
                YkczActivity.this, IP, port);
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

    private void showMdkwScan() {
        String tm = et_mdkw.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(YkczActivity.this, "请先扫描库位码", Toast.LENGTH_LONG).show();
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
                YkczActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_mdkw.setText("");
                et_mdkw.setSelection(0);
                et_mdkw.requestFocus();
                ckbh="";
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }

                ckbh = obj.get(0).toString().trim();//仓库编号

            }

        });

    }

    public class YkczAdapter extends RecyclerView.Adapter<YkczAdapter.ViewHolder> {
        private List<Map<String, String>> list = new ArrayList<>();

        public YkczAdapter() {
        }

        public void setList(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<Map<String, String>> getList() {
            return list;
        }

        public YkczAdapter(List<Map<String, String>> list) {
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
            holder.xiangji.setBackgroundResource(R.mipmap.ic_delete);
            holder.xiangji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(YkczActivity.this);
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
            holder.tv_tm_title.setText("码值");
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
//                        anInt = position;
//                        scanResult(holder.et_tm.getText().toString());
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
            private Button xiangji;
            private EditText et_tm;
            private TextView tv_cpph;
            private TextView tv_cppm;
            private TextView tv_cpgg;
            private TextView et_zysl;

            private TextView tv_tm_title;

            public ViewHolder(View itemView) {
                super(itemView);
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
