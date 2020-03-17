package com.cubertech.bhpda.Activity.RelevancePackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.Adapter.GlgxjiAdapter;
import com.cubertech.bhpda.Activity.TransferPackage.GxzyActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class GlgxjlActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    private SiteService siteService;
    private Context mContext;

    AccountInfo ai;
    TKApplication application;

    private OnFragmentInteractionListener mListener;
    //private View Iview;
    private EditText et_gd, et_zj, et_yj, et_ry, et_bz;
    private EditText et_bgsl;//报工数量

    private TextView tv_gy, tv_zj_ph, tv_zj_pm, tv_zj_gg;
    //private TextView tv_yj_ph,tv_yj_pm,tv_yj_gg;

    private Button btn_submit;//提交

    public static int flag = R.id.et_gd;
    public static String hint = "工单信息";
    private RelativeLayout xiangji, xiangji2, xiangji3, xiangji4, xiangji5, xiangji6;


    private String TA015 = "";//TA015预计产量

    private RecyclerView recycler;
    private GlgxjiAdapter glgxjiAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_glgxjl);
        this.mContext = GlgxjlActivity.this;
        /*Iview = inflater.inflate(R.layout.fragment_build, container, false);
        initView(Iview);*/

/*
        this.mContext = GlgxjlActivity.this;
        if (getIntent() != null) {
            mParam1 = getIntent().getStringExtra(ARG_PARAM1);
            mParam2 = getIntent().getStringExtra(ARG_PARAM2);

            mData = getIntent().getStringExtra(ARG_DATA);//账套
            mName = getIntent().getStringExtra(ARG_NAME);//用户登录名
        }*/
        initView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //todo
        ai = bundle.getParcelable("account");
        application = (TKApplication) getApplication();

    }

    private void initView() {
        et_gd = (EditText) findViewById(R.id.et_gd);
        et_zj = (EditText) findViewById(R.id.et_zj);
        et_yj = (EditText) findViewById(R.id.et_yj);
        et_ry = (EditText) findViewById(R.id.et_ry);
//        et_jq = (EditText) findViewById(R.id.et_jq);
        et_bz = (EditText) findViewById(R.id.et_bz);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_bgsl = (EditText) findViewById(R.id.et_bgsl);

        tv_gy = (TextView) findViewById(R.id.tv_gy);
        tv_zj_ph = (TextView) findViewById(R.id.tv_zj_ph);
        tv_zj_pm = (TextView) findViewById(R.id.tv_zj_pm);
        tv_zj_gg = (TextView) findViewById(R.id.tv_zj_gg);

        /*tv_yj_ph = (TextView) view.findViewById(R.id.tv_yj_ph);
        tv_yj_pm = (TextView) view.findViewById(R.id.tv_yj_pm);
        tv_yj_gg = (TextView) view.findViewById(R.id.tv_yj_gg);*/

        xiangji = (RelativeLayout) findViewById(R.id.del1);
        xiangji2 = (RelativeLayout) findViewById(R.id.del1d);
        xiangji3 = (RelativeLayout) findViewById(R.id.del1dd);
        xiangji4 = (RelativeLayout) findViewById(R.id.del1dd3);
//        xiangji5 = (RelativeLayout) findViewById(R.id.del1dd33);
//        xiangji6 = (RelativeLayout) findViewById(R.id.del1dd33444);

        et_gd.setOnFocusChangeListener(this);
        et_zj.setOnFocusChangeListener(this);
        et_yj.setOnFocusChangeListener(this);
        et_ry.setOnFocusChangeListener(this);
//        et_jq.setOnFocusChangeListener(this);
        et_bz.setOnFocusChangeListener(this);
        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Button btn_qx = (Button) findViewById(R.id.btn_qx);
        btn_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        et_gd.setOnEditorActionListener(new MyEditorAction());
        et_zj.setOnEditorActionListener(new MyEditorAction());
        et_yj.setOnEditorActionListener(new MyEditorAction());
        et_ry.setOnEditorActionListener(new MyEditorAction());
//        et_jq.setOnEditorActionListener(new MyEditorAction());
        et_bz.setOnEditorActionListener(new MyEditorAction());

        recycler = (RecyclerView) findViewById(R.id.recycler_b_id);

        //相机
        xiangji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setClass(getActivity(), CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivityForResult(intent, 1);*/
                IntentIntegrator intentIntegrator = new IntentIntegrator(GlgxjlActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class);// 设置自定义的activity是ScanActivity
                Intent scanIntent = intentIntegrator.createScanIntent();
                startActivityForResult(scanIntent, 1);

            }
        });
        xiangji2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent();
                intent.setClass(getActivity(), CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivityForResult(intent, 2);*/
                IntentIntegrator intentIntegrator = new IntentIntegrator(GlgxjlActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class);
                Intent scanIntent = intentIntegrator.createScanIntent();
                startActivityForResult(scanIntent, 2);
            }
        });
        xiangji3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent();
                intent.setClass(getActivity(), CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivityForResult(intent, 3);*/
                IntentIntegrator intentIntegrator = new IntentIntegrator(GlgxjlActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class);
                Intent scanIntent = intentIntegrator.createScanIntent();
                startActivityForResult(scanIntent, 3);
            }
        });
        xiangji4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent();
                intent.setClass(getActivity(), CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivityForResult(intent, 4);*/
                IntentIntegrator intentIntegrator = new IntentIntegrator(GlgxjlActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class);
                Intent scanIntent = intentIntegrator.createScanIntent();
                startActivityForResult(scanIntent, 4);
            }
        });
        xiangji5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setClass(getActivity(), CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivityForResult(intent, 5);*/
                IntentIntegrator intentIntegrator = new IntentIntegrator(GlgxjlActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class);
                Intent scanIntent = intentIntegrator.createScanIntent();
                startActivityForResult(scanIntent, 5);
            }
        });
        xiangji6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setClass(getActivity(), CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivityForResult(intent, 6);*/
                IntentIntegrator intentIntegrator = new IntentIntegrator(GlgxjlActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class);
                Intent scanIntent = intentIntegrator.createScanIntent();
                startActivityForResult(scanIntent, 6);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GlgxjlActivity.this);
                builder.setMessage("是否确定?");
                builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //提交
                        submit();
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


        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        //添加分隔线
        //recycler_r.addItemDecoration(new AdvanceDecoration(this, OrientationHelper.VERTICAL));

        ArrayList<Map<String, Object>> mapdata = new ArrayList<Map<String, Object>>();
        glgxjiAdapter = new GlgxjiAdapter(mContext, mapdata);
        recycler.setAdapter(glgxjiAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }

    /**
     * 提交函数
     */
    private void submit() {



        //Toast.makeText(mContext, "", Toast.LENGTH_LONG).show();

        String gdcode = et_gd.getText().toString().trim();
//        if (gdcode.equals("")) {
//            Toast.makeText(mContext, "工单条码不能为空", Toast.LENGTH_LONG).show();
//            return;
//        }
        String zjcode = et_zj.getText().toString().trim();
        if (zjcode.equals("")) {
            Toast.makeText(mContext, "主件码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(tv_zj_ph.getText().toString().trim())){
            Toast.makeText(mContext, "请查询主件品号", Toast.LENGTH_LONG).show();
            return;
        }

        //还要判断元件不能为空
        if(glgxjiAdapter.getListData()==null||glgxjiAdapter.getListData().size()<=0){
            Toast.makeText(mContext, "元件码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        //value{ ，，，，，，，，}
        //yjlist  元件条码0，元件品号1
        String[] gds = gdcode.split("-");//条码结构：工单单别-工单单号-工序编号

        List<Object> value = new ArrayList<Object>();
        value.add("");//工单单别0
        value.add("");//工单单号1
        value.add(tv_gy.getText().toString().trim().equals("") ? "" : tv_gy.getText().toString().trim());//工艺名称2
        value.add(et_zj.getText().toString().trim().equals("") ? "" : et_zj.getText().toString().trim());//主件条码3
        value.add(tv_zj_ph.getText().toString().trim().equals("") ? "" : tv_zj_ph.getText().toString().trim());//主件品号4
        value.add(et_ry.getText().toString().trim().equals("") ? "" : et_ry.getText().toString().trim());//人员条码5
//        value.add(et_jq.getText().toString().trim().equals("") ? "" : et_jq.getText().toString().trim());//机器条码6
        value.add(et_bz.getText().toString().trim().equals("") ? "" : et_bz.getText().toString().trim());//班组条码7
        value.add(et_bgsl.getText().toString().trim().equals("") ? "0" : et_bgsl.getText().toString().trim());//报工数量8
        List<Object> yjlist = new ArrayList<Object>();

        List<Object> list;
        for (Map<String, Object> m : glgxjiAdapter.getListData()) {
            list = new ArrayList<Object>();
            list.add(m.get("b_tm").toString().trim().equals("") ? "" : m.get("b_tm").toString().trim());//元件条码
            list.add(m.get("b_yjph").toString().trim().equals("") ? "" : m.get("b_yjph").toString().trim());//元件品号
            yjlist.add(list);
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("value", value);
        params.put("yjlist", yjlist);
        params.put("name", ai.getName());
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
        Observable<String> observable = siteService.addlist(params,
                mContext, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
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
            public void onNext(String str) {
                if (str.trim().equals("1")) {
                    Toast.makeText(mContext, "提交成功！", Toast.LENGTH_LONG).show();
                    //?清空内容？还是标识已经上传？？？？？
                    finish();
                }
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//           /* throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");*/
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        //原值
        //EditText viewById = (EditText) Iview.findViewById(flag);//Fragment的用法
        EditText viewById = (EditText) this.findViewById(flag);
        if (viewById.getText().toString().equals("") || viewById.getText().toString() == null) {
            //viewById.setText(null);
            //viewById.clearComposingText();
            viewById.setHint(hint);
        }
        //现值
        int id = view.getId();
        //EditText editText = (EditText) Iview.findViewById(id);//Fragment的用法
        EditText editText = (EditText) this.findViewById(id);
        //editText.setText(" ");
        hint = editText.getHint().toString();
        editText.setHint("");
        flag = id;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
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
            et_gd.setText(ScanResult);
        } else if (requestCode == 2) {
            et_zj.setText(ScanResult);
            keycodegd("zj");
        } else if (requestCode == 3) {
            et_yj.setText(ScanResult);
            keycodegd("yj");
        } else if (requestCode == 4) {
            et_ry.setText(ScanResult);
        } else if (requestCode == 5) {
//            et_jq.setText(ScanResult);
        } else if (requestCode == 6) {
            et_bz.setText(ScanResult);
        }
        /*switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容 工单信息
                    et_gd.setText(bundle.getString("result"));

                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容 主件条码
                    et_zj.setText(bundle.getString("result"));

                }
                break;
            case 3:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容 元件条码
                    et_yj.setText(bundle.getString("result"));

                }
                break;
            case 4:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容 人员条码
                    et_ry.setText(bundle.getString("result"));

                }
                break;
            case 5:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容 机器条码
                    et_jq.setText(bundle.getString("result"));

                }
                break;
            case 6:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容 班组条码
                    et_bz.setText(bundle.getString("result"));

                }
                break;

        }*/
    }

    /**
     * 监控回车事件
     */
    class MyEditorAction implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.et_gd:
                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
                    // ACTION_DOWN时也触发
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        // 执行扫描事件函数
                        keycodegd("gd");
                    }
                    break;
                case R.id.et_zj:
                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
                    // ACTION_DOWN时也触发
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        // 执行扫描事件函数
                        keycodegd("zj");
                    }
                    break;
                case R.id.et_yj:
                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
                    // ACTION_DOWN时也触发
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        // 执行扫描事件函数
                        keycodegd("yj");
                    }
                    break;
                case R.id.et_ry:
                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
                    // ACTION_DOWN时也触发
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        // 执行扫描事件函数
                        keycodegd("ry");
                    }
                    break;
//                case R.id.et_jq:
//                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
//                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
//                    // ACTION_DOWN时也触发
//                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
//                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
//                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//                        // 执行扫描事件函数
//                        keycodegd("jq");
//                    }
//                    break;
                case R.id.et_bz:
                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
                    // ACTION_DOWN时也触发
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        // 执行扫描事件函数
                        keycodegd("bz");
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    public void keycodegd(String param) {
        String gdcode = et_gd.getText().toString().trim();
//        if (gdcode.equals("")) {
//            Toast.makeText(mContext, "工单条码不能为空", Toast.LENGTH_LONG).show();
//            return;
//        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        if (param.equals("gd")) {
            params.put("code", gdcode);
        } else if (param.equals("zj")) {
            String zjcode = et_zj.getText().toString().trim();
            if (zjcode.equals("")) {
                Toast.makeText(mContext, "主件条码不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            String[] gds = gdcode.split("-");//条码结构：工单单别-工单单号-工序编号
            params.put("code", zjcode);
            params.put("TA001", "");//工单单别
            params.put("TA002", "");//工单单号
            glgxjiAdapter.setListData(new ArrayList<>());
            et_yj.setText("");
        } else if (param.equals("yj")) {
            String zjcode = et_zj.getText().toString().trim();
            String yjcode = et_yj.getText().toString().trim();
            if (glgxjiAdapter.equalsBarcode(yjcode)) {
                et_yj.setText("");
                ToastUtils.showToast("请扫描不同的元件条码!");
                return;
            }
            if (zjcode.equals("")) {
                et_yj.setText("");
                Toast.makeText(mContext, "主件条码不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (yjcode.equals("")) {
                Toast.makeText(mContext, "元件条码不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            String[] gds = gdcode.split("-");

            params.put("zjcode", zjcode);//主件条码值
            params.put("TA015", TA015);//TA015预计产量//全局变量扫描主键后获取的数值
            params.put("TB001", "");//工单单别
            params.put("TB002", "");//工单单号
            params.put("code", yjcode);//元件条码值
        }

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
       /* Observable<List<Object>> observable = siteService.getgd(params,
                mContext, IP, port);*/
        Observable<List<Object>> observable = null;
        if (param.equals("gd")) {
            observable = siteService.getgd(params,
                    mContext, IP, port);
        } else if (param.equals("zj")) {
            observable = siteService.getzj(params,
                    mContext, IP, port);
        } else if (param.equals("yj")) {
            observable = siteService.getyj(params,
                    mContext, IP, port);
        }

        //一个订阅者
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(mContext, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                /*如果对edittext组件设置了editText.setFocusable(false);需要重新获取焦点则必须执行：
                editText.setFocusable(ture);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();*/
                if (param.equals("gd")) {
                    tv_gy.setText("");
                    et_gd.setText("");
                    et_gd.requestFocus();
                } else if (param.equals("zj")) {
                    tv_zj_ph.setText("");
                    tv_zj_pm.setText("");
                    tv_zj_gg.setText("");
                    et_zj.setText("");
                    et_zj.requestFocus();
                } else if (param.equals("yj")) {
                    /*tv_yj_ph.setText("");
                    tv_yj_pm.setText("");
                    tv_yj_gg.setText("");*/
                    et_yj.setText("");
                    et_yj.requestFocus();
                }
            }

            @Override
            public void onNext(List<Object> obj) {
                if (param.equals("gd")) {
                    if (obj != null && obj.size() > 0) {
                        List<Object> o = (List<Object>) obj;
                        String str1 = o.get(0).toString().trim();//MW002工艺名称
                        tv_gy.setText(str1);
                    }
                } else if (param.equals("zj")) {
                    if (obj != null && obj.size() > 0) {
                        List<Object> o = (List<Object>) obj;
                        String ph = o.get(0).toString().trim();//品号
                        String pm = o.get(1).toString().trim();//品名
                        String gg = o.get(2).toString().trim();//规格
                        TA015 = o.get(3).toString().trim();//TA015预计产量
                        tv_zj_ph.setText(ph);
                        tv_zj_pm.setText(pm);
                        tv_zj_gg.setText(gg);
                        //Toast.makeText(mContext,"TA015预产量"+TA015,Toast.LENGTH_LONG).show();
                    }
                } else if (param.equals("yj")) {
                    if (obj != null && obj.size() > 0) {
                        List<Object> o = (List<Object>) obj;
                        if (o.size() > 1) {
                            //正确结果值
                            String ph = o.get(0).toString().trim();//品号
                            String pm = o.get(1).toString().trim();//品名
                            String gg = o.get(2).toString().trim();//规格
                           /* tv_yj_ph.setText(ph);
                            tv_yj_pm.setText(pm);
                            tv_yj_gg.setText(gg);*/
                            Map<String, Object> item;
                            item = new HashMap<String, Object>();
                            item.put("b_tm", et_yj.getText().toString());//条码
                            item.put("b_yjph", ph);//品号
                            item.put("b_yjmc", pm);//品名
                            item.put("b_yjgg", gg);//规格
                            glgxjiAdapter.addItem(item);

                            et_yj.setText("");
                            et_yj.requestFocus();
                        } else {
                            String r = o.get(0).toString().trim();
                            if (r.equals("0")) {
                                Toast.makeText(mContext, "元件数量超过限值", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, "预计产量或需领用量数值录入错误", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else if (param.equals("ry")) {
                    if (obj != null && obj.size() > 0) {

                    }
                } else if (param.equals("jq")) {
                    if (obj != null && obj.size() > 0) {

                    }
                } else if (param.equals("bz")) {
                    if (obj != null && obj.size() > 0) {

                    }
                }
            }
        });
    }
}
