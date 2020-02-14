package com.cubertech.bhpda.Activity.TransferPackage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.JgryUtils;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.SQLiteUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.view.LinearListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class XmtdyActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_mtlx;
    private AccountInfo ai;
    private EditText et_tm;
    private SiteService siteService;
    private TKApplication application;
    private TextView tv_cpph, tv_cppm, tv_cpgg;
    private EditText et_zxsl;
    private String cpbb, ljh;

    private LinearListView list_jgry;
    private View fullMaskView;//蒙层背景
    private View addrqm; //添加容器码布局
    private XmtdyAdapter gxzyAdapter;
    private List<List<Object>> uplist = new ArrayList<>();// 上传时
    //private List<Object> jgryList = new ArrayList<>();// 扫码得到的
    private Button btn_xmtdy_add;
    private ScrollView scrollView;

    private int navigationBarHeight = 0;
    private int panelHeight = 0; //addJgry 高度
    private EditText et_xmtdy_tm;
    private Button btn_tj;
    private boolean isRqm;
    private EditText pick_jgry_num;
    private float count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmtdy);
        initView();
        SQLiteUtils.getInstance(XmtdyActivity.this).getStatus(SQLiteUtils.XMT);
    }

    private void initView() {
        attachView();

        ImageView back = (ImageView) findViewById(R.id.left);
        Button btnQX = (Button) findViewById(R.id.btn_qx);
        Button btnSc = (Button) findViewById(R.id.btn_sc);//生成
        et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setSelection(0);
        et_tm.requestFocus();
        tv_cpph = (TextView) findViewById(R.id.tv_cpph);
        tv_cppm = (TextView) findViewById(R.id.tv_cppm);
        tv_cpgg = (TextView) findViewById(R.id.tv_cpgg);
        tv_mtlx = (TextView) findViewById(R.id.tv_mtlx);
        et_zxsl = (EditText) findViewById(R.id.et_zxsl);


        list_jgry = (LinearListView) findViewById(R.id.jgry_list);
        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        gxzyAdapter = new XmtdyAdapter();
        list_jgry.setAdapter(gxzyAdapter);

        btn_tj = (Button) findViewById(R.id.btn_tj);
        btn_tj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressed();
                String cpm = et_tm.getText().toString().trim();
                if (cpm.equals("")) {
                    Toast.makeText(XmtdyActivity.this, "请先扫描产品码", Toast.LENGTH_LONG).show();
                    return;
                }

                showAddJgry();
            }
        });


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
            Toast.makeText(XmtdyActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
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
        Observable<List<Object>> observable = siteService.GetMTList(params,
                XmtdyActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(XmtdyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                Log.e("####xmt", obj.toString());
                tv_cpph.setText(obj.get(0).toString());//[0]产品品号
                tv_cppm.setText(obj.get(1).toString());//[1]产品品名
                tv_cpgg.setText(obj.get(4).toString());//[4]产品规格/图号
                et_zxsl.setText(obj.get(3).toString());//[3]sl 数量？
                cpbb = obj.get(2).toString().trim();//[2]产品版本
                ljh = obj.get(5).toString().trim();//[5]零件号

            }
        });
    }

    /**
     * 扫描容器码
     */
    private void ScanCode_rqm() {
        String tm = et_xmtdy_tm.getText().toString().trim();

        if (tm.equals("")) {
            Toast.makeText(XmtdyActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
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

        //todo 1
        //先弄一个观察者
        Observable<String> observable = siteService.getNumRQ(params,//getYg
                XmtdyActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();


            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_xmtdy_tm.setText("");
                Toast.makeText(XmtdyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String obj) {
                if (TextUtils.isEmpty(obj)) {
                    return;
                }
                pick_jgry_num.setText(obj.trim());
            }
        });
//                //jgryList = obj;
//                //判断返回的产品码是否和当前的产品码相等
//                if (obj.get(0).toString().trim().equals(et_tm.getText().toString().trim())) {
//                    List<Object> list = new ArrayList<>();
//                    //jgryList.add(tm);
//                    list.add(tm);
//                    //返回产品码跟当前的产品码对比，若相当则添加到列中
//                    uplist.add(list);
//
//                    gxzyAdapter.setList(uplist);
//                    scrollView.post(new Runnable() {//回传后是线程状态
//                        @Override
//                        public void run() {
//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
//                    hideAddJgry();
//                } else {
//                    Toast.makeText(XmtdyActivity.this, "产品码不相符，请重置后重新扫描！", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
    }

    public void xiangji1(View view) {

        xiangji(view);
        isRqm = true;
    }


    int yourChoice = -1;

    /**
     * 选择唛头类型
     *
     * @param v
     */
    public void choice(View v) {
        final String[] items = {"箱", "托盘"};
        //final String[] items = datas.split(",");
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(XmtdyActivity.this);
        singleChoiceDialog.setTitle("选择账套");
        int Choice = -1;

        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, Choice,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            tv_mtlx.setText(String.valueOf(items[yourChoice]));
                        }
                    }
                });
        singleChoiceDialog.show();
    }

    /**
     * @see android.app.Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {
        if (fullMaskView.getVisibility() == View.VISIBLE) {
            hideAddJgry();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_sc://生成箱唛码
                if (TextUtils.isEmpty(et_tm.getText().toString().trim()) || TextUtils.isEmpty(tv_cpph.getText().toString().trim())) {
                    Toast.makeText(XmtdyActivity.this, "请扫描条码", Toast.LENGTH_SHORT).show();
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
                List<Object> objectList = new ArrayList<>();
                List<Object> list;
                for (List<Object> x : uplist) {
                    list = new ArrayList<>();
                    list.add(x.get(0));
                    list.add(x.get(1));
                    objectList.add(list);
                }
                List<Object> info = new ArrayList<>();//[0]产品品号 [1]类型 [2]数量
                info.add(tv_cpph.getText().toString().trim());//[0]产品品号
                info.add(tv_mtlx.getText().toString().trim());//[1]类型
                info.add(count + "");//[2]数量lxs 20180929修改

                params.put("list", objectList);
                params.put("type", tv_mtlx.getText().toString());
                params.put("info", info);//[0]产品品号 [1]类型 [2]数量
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
                Observable<List<Object>> observable = siteService.getMT_Save(params, XmtdyActivity.this, IP, port);
                observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
                    @Override
                    public void onCompleted() {
                        siteService.closeParentDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        siteService.closeParentDialog();
                        Toast.makeText(XmtdyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(List<Object> str) {
                        if (ListUtils.isEmpty(str)) {
                            return;
                        }
                        //Toast.makeText(XmtdyActivity.this, "成功查看数据库数据", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent();
                        Log.e("######", str.toString());
                        //intent.putExtras(getIntent().getExtras());
                        intent.setClass(XmtdyActivity.this, XmtdyConfirmActivity.class);
                        intent.putExtra("dm", str.get(0).toString());
                        //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                        //新值[0]数量[1]类型(箱/托)[2]版本号[3]图号(规格)[4]零件号
                        List<Object> resultList = new ArrayList<>();
//                        float f = Float.parseFloat(et_zxsl.getText().toString().trim()); lxs 20180929注释
                        Log.e("####" + count, "///");
                        resultList.add(count);//[0]数量
                        resultList.add(tv_mtlx.getText().toString().trim());//[1]类型(箱/托)
                        resultList.add(cpbb);//[2]版本号
                        resultList.add(tv_cpgg.getText().toString().trim());//[3]图号(规格)
                        resultList.add(ljh);//[4]零件号
                        resultList.add(str.get(1).toString());//[5]日期   /[1]时分秒(str)
                        intent.putExtra("result", (Serializable) resultList);
                        //intent.putExtra("cptm", et_tm.getText().toString().trim());
                        SQLiteUtils instance = SQLiteUtils.getInstance(XmtdyActivity.this);
                        instance.saveDm(str.get(0).toString(), SQLiteUtils.XMT, "", resultList.toString());
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
        isRqm = false;
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
           /* et_tm.setText(ScanResult);

            scanCode();*/
            if (!isRqm) {
                et_tm.setText(ScanResult);

                scanCode();
            } else {
                //et_jgry_tm.setText(ScanResult);
                et_xmtdy_tm.setText(ScanResult);
                ScanCode_rqm();
            }
        }
    }


    // 蒙层绑定

    /**
     * 蒙层绑定
     */
    private void attachView() {
        fullMaskView = View.inflate(this, R.layout.ui_view_full_mask_layout, null);
        ((ViewGroup) getWindow().getDecorView()).addView(fullMaskView);
        fullMaskView.setVisibility(View.GONE);
        fullMaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAddJgry();
            }
        });
        //addrqm = View.inflate(this, R.layout.dialog_gxzy_jgry, null);
        addrqm = View.inflate(this, R.layout.dialog_xmtdy_tjrqm, null);
        addrqm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_xmtdy_add = (Button) addrqm.findViewById(R.id.btn_xmtdy_add);

       /* et_jgry_tm = (EditText) addrqm.findViewById(R.id.et_tm);
        tv_jgry_name = (TextView) addrqm.findViewById(R.id.tv_jgry_name);*/
        et_xmtdy_tm = (EditText) addrqm.findViewById(R.id.et_dm);
        pick_jgry_num = (EditText) addrqm.findViewById(R.id.pick_jgry_num);
//        pick_jgry_num.setEnabled(false);
        Button btn_xmtdy_cancel = (Button) addrqm.findViewById(R.id.btn_xmtdy_cancel);
        btn_xmtdy_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAddJgry();
            }
        });
        final boolean[] isFirst = {true};
        btn_xmtdy_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_xmtdy_tm.getText().toString())) {
                    ToastUtils.showToast("请扫描!");
                    return;
                }

                if (TextUtils.isEmpty(pick_jgry_num.getText().toString())) {
                    ToastUtils.showToast("请输入数量!");
                    return;
                }
                for (List<Object> o : uplist) {
                    if (TextUtils.equals(et_xmtdy_tm.getText().toString(), o.get(0).toString())) {
                        ToastUtils.showToast("请扫描不同的容器码!");
                        return;
                    }
                }
                //增加限制扫描的容器码时，容器码必须是相应产品的容器码2019-03-05
                String cpmz = et_tm.getText().toString().trim().substring(0,12);
                String rqmz = et_xmtdy_tm.getText().toString().trim().substring(0,12);
                if(!cpmz.equals(rqmz)){
                    ToastUtils.showToast("产品不相符!");
                    return;
                }

                List<Object> objectList = new ArrayList<>();
                objectList.add(et_xmtdy_tm.getText().toString());
                objectList.add(pick_jgry_num.getText().toString());
                uplist.add(objectList);
                gxzyAdapter.setList(uplist);
                count = 0;
                for (List<Object> o : uplist) {
                    count += Float.parseFloat(o.get(1).toString());
                }
//                et_zxsl.setText(count + ""); lxs 20180929注释
                et_xmtdy_tm.setText("");//只把内容清空
                pick_jgry_num.setText("");
                et_xmtdy_tm.setFocusable(true);
                et_xmtdy_tm.setFocusableInTouchMode(true);
                et_xmtdy_tm.requestFocus();
                hideAddJgry();

                /*只做重置处理
                if (TextUtils.isEmpty(et_xmtdy_tm.getText().toString())) {
                    Toast.makeText(XmtdyActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isOnly = true;

//                left.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        left.performClick();
//                    }
//                });

                //jgryList.add(picke_jgry_num.getText().toString());
                if (uplist != null && uplist.size() > 0 && jgryList != null)
                    for (int i = 0; i < uplist.size(); i++) {
                        if (TextUtils.equals(uplist.get(i).get(0).toString().trim(), jgryList.get(0).toString().trim())) {
                            isOnly = false;
                            break;
                        }
                    }
                if (!isOnly) {
                    Toast.makeText(XmtdyActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
                    return;
                }
                uplist.add(jgryList);

                gxzyAdapter.setList(uplist);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                hideAddJgry();*/
            }
        });
        et_xmtdy_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    //jgryScanCode();//
                    ScanCode_rqm();//扫描容器码
                }
                return false;
            }
        });


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.leftMargin = 40;
        params.rightMargin = 40;

        ((ViewGroup) getWindow().getDecorView()).addView(addrqm, params);
        addrqm.setVisibility(View.GONE);
        if (JgryUtils.hasNavigationBar(this)) {
            navigationBarHeight = JgryUtils.getNavigationBarHeight(this);
        }

    }


    //显示蒙层
    private void showAddJgry() {
        fullMaskView.setVisibility(View.VISIBLE);
        addrqm.setVisibility(View.VISIBLE);
        et_xmtdy_tm.setText("");

        addrqm.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                addrqm.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup parent = (ViewGroup) addrqm.getParent();
                panelHeight = parent.getHeight() - addrqm.getTop();

                AnimatorSet animatorSet = new AnimatorSet();//组合动画
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(addrqm, "scaleX", 0, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(addrqm, "scaleY", 0, 1f);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(addrqm, "alpha", 0f, 1f);
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.play(scaleX).with(scaleY).with(alpha);//两个动画同时开始
                animatorSet.start();
            }
        });
    }

    //隐藏蒙层
    private void hideAddJgry() {
        fullMaskView.setVisibility(View.GONE);
        addrqm.setVisibility(View.GONE);
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(addrqm, "scaleX", 1f, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(addrqm, "scaleY", 1f, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(addrqm, "alpha", 1f, 0f);
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY).with(alpha);//两个动画同时开始
        animatorSet.start();
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(addJgry, "alpha", 1f, 0f).setDuration(200);
//        alpha.start();
//        ObjectAnimator.ofFloat(addJgry, "translationY", -navigationBarHeight, panelHeight).setDuration(200).start();

    }


    private class XmtdyAdapter extends BaseAdapter {
        private List<List<Object>> list = new ArrayList<>();
        private ViewHolder holder;

        public void setList(List<List<Object>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (list == null)
                return 0;//todo moni list.size()
            else return this.list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                //view = LayoutInflater.from(XmtdyActivity.this).inflate(R.layout.gxzy_activity_adapter_item, viewGroup, false);
                view = LayoutInflater.from(XmtdyActivity.this).inflate(R.layout.xmtdy_activity_adapter_item, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            List<Object> objectList = this.list.get(i);
            if (objectList != null && objectList.size() > 0) {
                //holder.tv_name.setText(objectList.get(1).toString().trim());//1、员工姓名；0、员工编号；3、数量
                //holder.tv_num.setText(objectList.get(2).toString().trim());
                holder.tv_rqm.setText(objectList.get(0).toString().trim());//容器码
                holder.tv_sl.setText(objectList.get(1).toString().trim());
                final int finalI = i;
                holder.imgv_reduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(XmtdyActivity.this);
                        builder.setMessage("是否删除该容器码");
                        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                uplist.remove(finalI);
                                setList(uplist);
                            }
                        });
                        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();

                    }
                });
            }

            return view;
        }

        public class ViewHolder {

            /* private final TextView tv_name;
             private final TextView tv_num;
             private final ImageView imgv_reduce;*/
            private final TextView tv_rqm;
            private final ImageView imgv_reduce;
            private final TextView tv_sl;


            public ViewHolder(View view) {
                /*tv_name = (TextView) view.findViewById(R.id.jgry_name);
                tv_num = (TextView) view.findViewById(R.id.jgry_num);
                imgv_reduce = (ImageView) view.findViewById(R.id.imgv_del);*/
                tv_rqm = (TextView) view.findViewById(R.id.tv_rqm);
                imgv_reduce = (ImageView) view.findViewById(R.id.imgv_del);
                tv_sl = (TextView) view.findViewById(R.id.tv_sl);

            }
        }
    }


}
