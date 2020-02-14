package com.cubertech.bhpda.Activity.TransferPackage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.JgryUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.view.LinearListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 返工转移界面
 */
public class FgzyActivity extends AppCompatActivity {
    private SiteService siteService;
    private ImageView left;
    private Button btn_qd, btn_qx;
    private EditText et_tm, et_lx, et_jd, et_yjcl, et_ywgl;
    private TextView tv_gdxx, tv_cpph, tv_cppm, tv_cpgg, tv_jggy, tv_jggx, tv_jgzx, tv_jgsx;
    private EditText et_zysl;
    //private String str_jgsx;//存储加工顺序
    private String str_gyxz;//TA005 工艺性质 1.厂内工艺、2.委外工艺
    private String str_jyfs;//TA033 检验方式 0:免检、1:抽检(减量)、2:抽检(正常)、3:抽检(加严)、4:全检
    private String str_MX021;//数据库为空时此处传入也是空值
    private String str_MX026;//开始时间
    private String str_MX027;//结束时间
    private String str_gy;//所在工艺(工艺编号)MW001
    private String str_zxbh;//工作中心/供应商编号MW005
    AccountInfo ai;
    private TKApplication application;
    private List<List<Object>> uplist = new ArrayList<List<Object>>();
    private String cptm = null;
    private View fullMaskView;//蒙层
    private View addJgry;//添加加工人员布局
    private Button btn_jgry_add;
    private EditText et_jgry_tm;
    private TextView tv_jgry_name;
    private List<Object> jgryList = new ArrayList<Object>();
    private LinearListView list_jgry;
    private GxzyAdapter gxzyAdapter;
    private ScrollView scrollView;
    private int navigationBarHeight = 0;
    private int panelHeight = 0;
    private boolean isJgry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgzy);

        et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setSelection(0);
        et_tm.requestFocus();
        et_lx = (EditText) findViewById(R.id.et_lx);
        et_jd = (EditText) findViewById(R.id.et_jd);
        et_yjcl = (EditText) findViewById(R.id.et_yjcl);//预计产量
        et_ywgl = (EditText) findViewById(R.id.et_ywgl);//已完工量

        tv_gdxx = (TextView) findViewById(R.id.tv_gdxx);
        tv_cpph = (TextView) findViewById(R.id.tv_cpph);
        tv_cppm = (TextView) findViewById(R.id.tv_cppm);
        tv_cpgg = (TextView) findViewById(R.id.tv_cpgg);
        tv_jggy = (TextView) findViewById(R.id.tv_jggy);
        tv_jggx = (TextView) findViewById(R.id.tv_jggx);
        tv_jgzx = (TextView) findViewById(R.id.tv_jgzx);
        tv_jgsx = (TextView) findViewById(R.id.tv_jgsx);

        et_zysl = (EditText) findViewById(R.id.et_zysl);

        left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
        btn_qd = (Button) findViewById(R.id.btn_qd);
        btn_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FgzyActivity.this);
                builder.setMessage("是否确定?");
                builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        add();
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
        btn_qx = (Button) findViewById(R.id.btn_qx);
        btn_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressed();
                showAddJgry();
            }
        });
        initView();
        list_jgry = (LinearListView) findViewById(R.id.jgry_list); //加工人员 listview
        gxzyAdapter = new GxzyAdapter();
        list_jgry.setAdapter(gxzyAdapter);
        scrollView = (ScrollView) findViewById(R.id.scrollView1);
        attachView();
    }

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
     * 添加转移数据
     */
    private void add() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(FgzyActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
            return;
        }
        String gdxx = tv_gdxx.getText().toString().trim();
        if (gdxx.equals("")) {
            Toast.makeText(FgzyActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
            return;
        }
        String[] strs = gdxx.split("-");


        List<Object> PDAZYMX = new ArrayList<Object>();
        PDAZYMX.add(cptm);//MX002[0]明细表条码
        PDAZYMX.add(strs[0].trim());//MX003[1]工单单别
        PDAZYMX.add(strs[1].trim());//MX004[2]工单单号
        PDAZYMX.add(tv_cpph.getText().toString().trim());//MX005[3]产品品号
        PDAZYMX.add(tv_cppm.getText().toString().trim());//MX006[4]产品名称
        PDAZYMX.add(tv_cpgg.getText().toString().trim());//MX007[5]产品规格
        PDAZYMX.add("");//MX008[6]批号
        PDAZYMX.add(tv_jggx.getText().toString().trim());//MX009[7]所在工序  PDAZYMX中MX009 所在工序就是 加工工序（加工顺序）
        PDAZYMX.add(str_gy);//MX010[8]所在工艺
        PDAZYMX.add(tv_jggy.getText().toString().trim());//MX011[9]工艺名称
        PDAZYMX.add(str_gyxz);//MX012[10]工艺性质
        PDAZYMX.add(str_zxbh);//MX013[11]工作中心/供应商编号
        PDAZYMX.add(tv_jgzx.getText().toString().trim());//MX014[12]工作中心/供应商名称
        PDAZYMX.add("");//MX015[13]入库仓库
        PDAZYMX.add(et_zysl.getText().toString().trim());//MX016[14]转移数量
        PDAZYMX.add("0");//MX017[15]报废数量
        PDAZYMX.add("0");//MX018[16]返工数量
        PDAZYMX.add(str_jyfs.equals("0") ? "0" : "");//MX019[17]合格码 0.免检、1.合格、2.不良   (TA003)检查方式未免检，合格码也为免检其他由质检人员确定合格不合格
        PDAZYMX.add(et_jd.getText().toString().trim().equals("开始") ? "1" : "2");//MX020[18]开始码 1.开始、2.结束 （从阶段值中带出）
        //str_jyfs TA033[10]检验方式 0:免检、1:抽检(减量)、2:抽检(正常)、3:抽检(加严)、4:全检
        String MX021 = "";//0.免检、1.未检、2.待验、3.已检
        if (str_MX021.equals("")) {
            if (str_jyfs.equals("0")) {//与TA033免检对应免检，
                MX021 = "0";
            } else {
                MX021 = "1";//其他第一次扫描（开始）对应未检 下一工序也对应未检
            }
        } else {
            MX021 = str_MX021;
        }

        PDAZYMX.add(MX021);//MX021[19]检验码 0.免检、1.未检、2.待验、3.已检
        //根据 转移类型 判端返工码值
        String MX022;
        if (et_lx.getText().toString().trim().equals("正常转移")) {
            MX022 = "0";
        } else {
            MX022 = "1";
        }
        PDAZYMX.add(MX022);//MX022[20]返工码 0.无返工、1.有返工

        PDAZYMX.add("");//MX023[21]返工工序 //由质检员在质检界面确定哪道序
        PDAZYMX.add(ai.getName());//MX024[22]加工人员 //登录的转移界面是加工人员11
        PDAZYMX.add("");//MX025[23]质检人员21
        PDAZYMX.add(et_jd.getText().toString().trim().equals("开始") ? "1" : str_MX026);//MX026[24]开始时间 目前是开始时间11
        PDAZYMX.add(et_jd.getText().toString().trim().equals("结束") ? "1" : str_MX027);//MX027[25]结束时间
        PDAZYMX.add("");//MX028[26]检验时间21

        PDAZYMX.add("");//MX029[27]存储供应商编号
        PDAZYMX.add("");//MX030[28]存储供应商名称

        //todo  copy 工序转移 写法
        List<Object> PDABGXX = new ArrayList<Object>();
        List<Object> list1 = new ArrayList<Object>();
        for (int i = 0; i < uplist.size(); i++) {
            list1 = new ArrayList<>();
            list1.add(cptm);//[0]产品条码
            list1.add(et_tm.getText().toString().trim());//[1]容器条码
            list1.add(tv_jggx.getText().toString().trim());//[2]报工工序
            list1.add(str_gy);//[3]报工工艺
            list1.add(tv_jggy.getText().toString().trim());//[4]工艺名称
            list1.add(uplist.get(i) != null && uplist.get(i).size() >= 2 ? uplist.get(i).get(0).toString().trim() : "");//[5]加工人员
            list1.add(uplist.get(i) != null && uplist.get(i).size() >= 2 ? uplist.get(i).get(2).toString().trim() : "0");//[6]报工数量/数量  （int）
            list1.add("");//[7]检验人员
            list1.add("");//[8]是否返工
            PDABGXX.add(list1);
        }

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("PDABGXX", PDABGXX);
        params.put("database", ai.getData());
        params.put("PDAZYMX", PDAZYMX);
        params.put("rqm", et_tm.getText().toString().trim());//增加传输容器码值 wlg增加2018-6-1
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
        Observable<String> observable = siteService.AddZymxFgBh(params,
                FgzyActivity.this, IP, port);

        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(FgzyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String str) {
                if (!str.equals("")) {
                    //Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                    Toast.makeText(FgzyActivity.this, "完成！", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(FgzyActivity.this, "请扫描条码", Toast.LENGTH_LONG).show();
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
        //        todo 此处接口名称未改
        Observable<List<Object>> observable = siteService.DMcodeFgzyBh(params,
                FgzyActivity.this, IP, port);
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
                Toast.makeText(FgzyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
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
                //str_jgsx=obj.get(7).toString();//加工顺序
                tv_jggy.setText(obj.get(8).toString());//[8]工艺名称
                tv_jggx.setText(obj.get(7).toString());//[7]加工顺序 (加工工序)()
                tv_jgsx.setText(obj.get(7).toString());
                tv_jgzx.setText(obj.get(9).toString());//[9]工作中心/供应商名称
                str_jyfs = obj.get(10).toString();//TA033[10]检验方式 0:免检、1:抽检(减量)、2:抽检(正常)、3:抽检(加严)、4:全检
                str_gyxz = obj.get(11).toString();//[11]工艺性质 1.厂内工艺、2.委外工艺
                str_gy = obj.get(12).toString();//[12]所在工艺(工艺编号)MW001
                str_zxbh = obj.get(13).toString();//[13]工作中心/供应商编号MW005
                str_MX021 = obj.get(14).toString();//[14]MX021第一次扫或最新工序描此处传入空值
                str_MX026 = obj.get(15).toString();//开始时间
                str_MX027 = obj.get(16).toString();//结束时间
                Log.e("#######", obj.size() + "");
//todo          产品条码注释
                cptm = obj.get(18).toString();//产品条码
                //wlg 新增
                et_yjcl.setText(obj.get(19).toString().trim());
                et_ywgl.setText(obj.get(20).toString().trim());
                if (obj.get(11).toString().equals("2") && obj.get(1).toString().equals("开始")) {
                    //工艺性质是委外，而且是开始才 弹出提示框是否更换供应商
                    //如果是更换供应商增需要再次选择供应商
                    //修改供应商值即可！！！！！！！！！！！！！！！
                    //=============================
                    Toast.makeText(FgzyActivity.this, "弹出是否更换供应商功能！！！！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 扫描二维码  (加工人员)
     */
    private void jgryScanCode() {
        String tm = et_jgry_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(FgzyActivity.this, "请扫描条码", Toast.LENGTH_LONG).show();
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
        Observable<List<Object>> observable = siteService.getYg(params,
                FgzyActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(FgzyActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                jgryList = obj;
                tv_jgry_name.setText(obj.get(1).toString().trim());//员工姓名

                //List<Object> o=obj;
                //Toast.makeText(GxzyActivity.this, "" + obj.size(), Toast.LENGTH_LONG).show();
//                et_lx.setText(obj.get(0).toString());//[0]转移类型正常转移
//                et_jd.setText(obj.get(1).toString());//[1]开始
//                tv_gdxx.setText(obj.get(2).toString() + "-" + obj.get(3).toString());//[2]单别-[3]单号
//                tv_cpph.setText(obj.get(4).toString());//[4]产品品号
//                tv_cppm.setText(obj.get(5).toString());//[5]产品品名
//                tv_cpgg.setText(obj.get(6).toString());////[6]产品规格
//                //str_jgsx=obj.get(7).toString();//加工顺序
//                tv_jggy.setText(obj.get(8).toString());//[8]工艺名称
//                tv_jggx.setText(obj.get(7).toString());//[7]加工顺序 (加工工序)()
//                tv_jgzx.setText(obj.get(9).toString());//[9]工作中心/供应商名称
//                str_jyfs = obj.get(10).toString();//TA033[10]检验方式 0:免检、1:抽检(减量)、2:抽检(正常)、3:抽检(加严)、4:全检
//                str_gyxz = obj.get(11).toString();//[11]工艺性质 1.厂内工艺、2.委外工艺
//                str_gy = obj.get(12).toString();//[12]所在工艺(工艺编号)MW001
//                str_zxbh = obj.get(13).toString();//[13]工作中心/供应商编号MW005
//                str_MX021 = obj.get(14).toString();//[14]MX021第一次扫或最新工序描此处传入空值
//                str_MX026 = obj.get(15).toString();//开始时间
//                str_MX027 = obj.get(16).toString();//结束时间
////todo          产品条码注释
////                cptm = obj.get(18).toString();//产品条码
//                if (obj.get(11).toString().equals("2") && obj.get(1).toString().equals("开始")) {
//                    //工艺性质是委外，而且是开始才 弹出提示框是否更换供应商
//                    //如果是更换供应商增需要再次选择供应商
//                    //修改供应商值即可！！！！！！！！！！！！！！！
//                    //=============================
//                    Toast.makeText(FgzyActivity.this, "弹出是否更换供应商功能！！！！", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }


    public void xiangji(View view) {
        isJgry = false;
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class);
        Intent scanIntent = intentIntegrator.createScanIntent();
        startActivityForResult(scanIntent, 999);
    }

    public void xiangji1(View view) {

        xiangji(view);
        isJgry = true;
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
            if (!isJgry) {
                et_tm.setText(ScanResult);

                scanCode();
            } else {
                et_jgry_tm.setText(ScanResult);
                jgryScanCode();
            }
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

    // 蒙层绑定
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
        addJgry = View.inflate(this, R.layout.dialog_gxzy_jgry, null);
        addJgry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        final String[] pick_num = {"50"};
        EditText picke_jgry_num = (EditText) addJgry.findViewById(R.id.pick_jgry_num);
//        picke_jgry_num.setSelected(50);
//        picke_jgry_num.setOnSelectListener(new PickerView.onSelectListener() {
//            @Override
//            public void onSelect(String text) {
//                pick_num[0] = text;
//            }
//        });
//        List<String> data = new ArrayList<>();
//        for (int i = 0; i < 101; i++) {
//            data.add(i + "");
//        }
//        picke_jgry_num.setData(data);
        btn_jgry_add = (Button) addJgry.findViewById(R.id.btn_jgry_add);
        et_jgry_tm = (EditText) addJgry.findViewById(R.id.et_tm);
        tv_jgry_name = (TextView) addJgry.findViewById(R.id.tv_jgry_name);
        Button btn_jgry_cancel = (Button) addJgry.findViewById(R.id.btn_jgry_cancel);
        btn_jgry_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAddJgry();
            }
        });
        final boolean[] isFirst = {true};
        btn_jgry_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_jgry_tm.getText().toString())) {
                    Toast.makeText(FgzyActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(tv_jgry_name.getText().toString())) {
                    Toast.makeText(FgzyActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(picke_jgry_num.getText().toString())) {
                    Toast.makeText(FgzyActivity.this, "请填写报工数量!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isOnly = true;

//                left.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        left.performClick();
//                    }
//                });

                jgryList.add(picke_jgry_num.getText().toString());
                if (uplist != null && uplist.size() > 0 && jgryList != null)
                    for (int i = 0; i < uplist.size(); i++) {
                        if (TextUtils.equals(uplist.get(i).get(0).toString().trim(), jgryList.get(0).toString().trim())) {
                            isOnly = false;
                            break;
                        }
                    }
                if (!isOnly) {
                    Toast.makeText(FgzyActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
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
                hideAddJgry();
            }
        });
        et_jgry_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    jgryScanCode();//
                }
                return false;
            }
        });


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.leftMargin = 40;
        params.rightMargin = 40;

        ((ViewGroup) getWindow().getDecorView()).addView(addJgry, params);
        addJgry.setVisibility(View.GONE);
        if (JgryUtils.hasNavigationBar(this)) {
            navigationBarHeight = JgryUtils.getNavigationBarHeight(this);
        }

    }

    //显示蒙层
    private void showAddJgry() {
        if (TextUtils.isEmpty(et_tm.getText().toString())) {
            Toast.makeText(FgzyActivity.this, "请扫码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_jd.getText().toString())) {
            Toast.makeText(FgzyActivity.this, "阶段状态不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.equals(et_jd.getText().toString(), "开始")) {
            Toast.makeText(FgzyActivity.this, "开始阶段不需要添加人员", Toast.LENGTH_SHORT).show();
            return;
        }
        fullMaskView.setVisibility(View.VISIBLE);
        addJgry.setVisibility(View.VISIBLE);
        et_jgry_tm.setText("");
        tv_jgry_name.setText("");
        addJgry.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                addJgry.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup parent = (ViewGroup) addJgry.getParent();
                panelHeight = parent.getHeight() - addJgry.getTop();

                AnimatorSet animatorSet = new AnimatorSet();//组合动画
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(addJgry, "scaleX", 0, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(addJgry, "scaleY", 0, 1f);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(addJgry, "alpha", 0f, 1f);
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
        addJgry.setVisibility(View.GONE);
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(addJgry, "scaleX", 1f, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(addJgry, "scaleY", 1f, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(addJgry, "alpha", 1f, 0f);
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY).with(alpha);//两个动画同时开始
        animatorSet.start();
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(addJgry, "alpha", 1f, 0f).setDuration(200);
//        alpha.start();
//        ObjectAnimator.ofFloat(addJgry, "translationY", -navigationBarHeight, panelHeight).setDuration(200).start();
    }

    private class GxzyAdapter extends BaseAdapter {
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
                view = LayoutInflater.from(FgzyActivity.this).inflate(R.layout.gxzy_activity_adapter_item, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            List<Object> objectList = this.list.get(i);
            if (objectList != null && objectList.size() > 0) {
                holder.tv_name.setText(objectList.get(1).toString().trim());//1、员工姓名；0、员工编号；3、数量
                holder.tv_num.setText(objectList.get(2).toString().trim());
                final int finalI = i;
                holder.imgv_reduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FgzyActivity.this);
                        builder.setMessage("是否删除该加工人员");
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

            private final TextView tv_name;
            private final TextView tv_num;
            private final ImageView imgv_reduce;


            public ViewHolder(View view) {
                tv_name = (TextView) view.findViewById(R.id.jgry_name);
                tv_num = (TextView) view.findViewById(R.id.jgry_num);
                imgv_reduce = (ImageView) view.findViewById(R.id.imgv_del);
            }
        }
    }
}
