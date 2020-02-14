package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ZjActivity extends AppCompatActivity {
    private SiteService siteService;
    private ImageView left;
    private EditText et_tm;
    private Button btn_wcjy, btn_qx;

    private TextView tv_cpph, tv_cppm, tv_cpgg, tv_zylx, tv_sl, tv_szgx, tv_szgy;
    private TextView tv_jypd, tv_fggx, tv_fggy;
    private EditText et_yssl, et_bfsl, et_fgsl, et_ycsl;

    private String str_MX003, str_MX004, str_MX014, str_MX012, str_MX010, str_MX013, str_MX021, str_MX026, str_MX027, str_MX024;
    private String str_MX016;//转移数量
    private String str_MX022;////返工码状态
    private String datas_gx = "";//获取返工工序
    private String datas_gybh = "";//工艺编号
    private String gybh = "";//选定的工艺编号
    private String datas_gymc = "";//工艺名称
    AccountInfo ai;
    private TKApplication application;


    private TextView fgsl, fggx, fggy;

    private List<List<Object>> uplist = new ArrayList<>();// 上传时 加工人员数据 列表
    private String cptm = "";
    private ToggleButton togbtn_zrfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zj);

        fgsl = (TextView) findViewById(R.id.fgsl);
        fggx = (TextView) findViewById(R.id.fggx);
        fggy = (TextView) findViewById(R.id.fggy);

        et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setSelection(0);
        et_tm.requestFocus();
        tv_cpph = (TextView) findViewById(R.id.tv_cpph);
        tv_cppm = (TextView) findViewById(R.id.tv_cppm);
        tv_cpgg = (TextView) findViewById(R.id.tv_cpgg);
        tv_zylx = (TextView) findViewById(R.id.tv_zylx);
        tv_sl = (TextView) findViewById(R.id.tv_sl);
        tv_szgx = (TextView) findViewById(R.id.tv_szgx);
        tv_szgy = (TextView) findViewById(R.id.tv_szgy);

        et_yssl = (EditText) findViewById(R.id.et_yssl);
        et_bfsl = (EditText) findViewById(R.id.et_bfsl);
        et_fgsl = (EditText) findViewById(R.id.et_fgsl);
        et_ycsl = (EditText) findViewById(R.id.et_ycsl);

        tv_jypd = (TextView) findViewById(R.id.tv_jypd);
        tv_jypd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice();
            }
        });
        tv_fggx = (TextView) findViewById(R.id.tv_fggx);
        tv_fggx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice1();
            }
        });
        tv_fggy = (TextView) findViewById(R.id.tv_fggy);

        left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
        btn_wcjy = (Button) findViewById(R.id.btn_wcjy);
        btn_wcjy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        btn_qx = (Button) findViewById(R.id.btn_qx);
        btn_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        togbtn_zrfg = (ToggleButton) findViewById(R.id.togbtn_zrfg);


        initView();
    }

    /**
     * 初始化
     */
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
     * 扫描二维码
     */
    private void scanCode() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(ZjActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
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
        Observable<List<Object>> observable = siteService.DMcodeZjBh(params,
                ZjActivity.this, IP, port);
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
                Toast.makeText(ZjActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                List<Object> obj_s = (List<Object>) obj.get(0);
                tv_cpph.setText(obj_s.get(4).toString());//产品品号
                tv_cppm.setText(obj_s.get(5).toString());//产品品名
                tv_cpgg.setText(obj_s.get(6).toString());//产品规格
                tv_zylx.setText(obj_s.get(0).toString());//转移类型
                for (int i = 0; i < obj_s.size(); i++) {
                    Log.e("####1", obj_s.get(i).toString());
                }

                if (obj_s.get(18).toString().contains("."))
                    tv_sl.setText(obj_s.get(18).toString().replace(obj_s.get(18).toString().substring(obj_s.get(18).toString().indexOf(".")
                            , obj_s.get(18).toString().length()), ""));//数量(转移数量)
                else
                    tv_sl.setText(obj_s.get(18).toString());
                et_yssl.setText(obj_s.get(18).toString().trim());
                tv_szgx.setText(obj_s.get(7).toString());//所在工序
                tv_szgy.setText(obj_s.get(8).toString());//所在工艺
                str_MX003 = obj_s.get(2).toString();//单别
                str_MX004 = obj_s.get(3).toString();//单号
                str_MX014 = obj_s.get(9).toString();//工作中心/供应商名称
                str_MX012 = obj_s.get(11).toString();//工艺性质 1.厂内工艺、2.委外工艺
                str_MX010 = obj_s.get(12).toString();//所在工艺(工艺编号)
                str_MX013 = obj_s.get(13).toString();//工作中心 / 供应商编号MW005
                str_MX021 = obj_s.get(14).toString();//MX021 检验码 检验状态
                str_MX026 = obj_s.get(15).toString();//开始时间
                str_MX027 = obj_s.get(16).toString();//结束时间
                str_MX024 = obj_s.get(17).toString();//加工人员
                str_MX016 = obj_s.get(18).toString();//转移数量
                str_MX022 = obj_s.get(19).toString();//返工码状态
                cptm = obj_s.get(20).toString();

                if (str_MX022.equals("1") || str_MX022.equals("2")) {
                    //返工或返工后续
                    //fgsl,fggx,fggy;
                    fgsl.setText("数量");
                    fggx.setText("指定工序");
                    fggy.setText("指定工序");
                } else {
                    fgsl.setText("返工数量");
                    fggx.setText("返工工序");
                    fggy.setText("返工工序");
                }


                List<Object> obj_gx = (List<Object>) obj.get(1);//获取返工序列
                for (Object o : obj_gx) {
                    if (!datas_gx.equals("")) {
                        datas_gx += ",";
                    }
                    datas_gx += o.toString().trim();
                }

                List<Object> obj_gybh = (List<Object>) obj.get(2);//工艺编号
                for (Object o : obj_gybh) {
                    if (!datas_gybh.equals("")) {
                        datas_gybh += ",";
                    }
                    datas_gybh += o.toString().trim();
                }
                //Toast.makeText(ZjActivity.this, "编号"+datas_gybh, Toast.LENGTH_LONG).show();
                List<Object> obj_gymc = (List<Object>) obj.get(3);//工艺名称
                for (Object o : obj_gymc) {
                    if (!datas_gymc.equals("")) {
                        datas_gymc += ",";
                    }
                    datas_gymc += o.toString().trim();
                }
                //Toast.makeText(ZjActivity.this, "名称"+datas_gymc, Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 添加转移数据
     */
    private void add() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(ZjActivity.this, "请扫描条码", Toast.LENGTH_LONG).show();
            return;
        }
        String cpph = tv_cpph.getText().toString().trim();
        if (cpph.equals("")) {
            Toast.makeText(ZjActivity.this, "请扫描条码", Toast.LENGTH_LONG).show();
            return;
        }
        if (tv_jypd.getText().toString().trim().equals("")) {
            Toast.makeText(ZjActivity.this, "请选择检验结果！", Toast.LENGTH_LONG).show();
            return;
        } else if (tv_jypd.getText().toString().trim().equals("不合格") && tv_fggx.getText().toString().trim().equals("")) {
            Toast.makeText(ZjActivity.this, "请选返工工序", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.equals(tv_jypd.getText().toString().trim(), "合格")) {
            if (TextUtils.equals(fggx.getText().toString().trim(), "返工工序") && !TextUtils.isEmpty(tv_fggx.getText().toString())) {
                ToastUtils.showToast("合格状态下返工工序不能选择!");
                return;
            }

        }

        if(TextUtils.equals(tv_zylx.getText().toString().trim(),"返工转移")){
            if(TextUtils.equals(tv_fggx.getText().toString().trim(),"")){
                ToastUtils.showToast("返工转移时请选择转移工序!");
                return;
            }
        }

        //验收数量+报废数量+异常数量+返工数量等于数量

        float yssl = Float.parseFloat(et_yssl.getText().toString().trim());//验收数量
        float bfsl = Float.parseFloat(et_bfsl.getText().toString().trim());//报废数量
        float ycsl = Float.parseFloat(et_ycsl.getText().toString().trim());//异常数量
        float fgsl = Float.parseFloat(et_fgsl.getText().toString().trim());//返工数量
        float sl = Float.parseFloat(tv_sl.getText().toString().trim());//数量
        float zs = yssl + bfsl + ycsl + fgsl;
        if (sl != zs) {
            Toast.makeText(ZjActivity.this, "验收数量错误！", Toast.LENGTH_LONG).show();
            return;
        }

        if (tv_jypd.getText().toString().trim().equals("合格")) {
            if (str_MX022.equals("1") || str_MX022.equals("2")) {
                if (tv_fggx.getText().toString().trim().equals("") || et_fgsl.getText().toString().trim().equals("")) {
                    //Toast.makeText(ZjActivity.this, "请指定工序", Toast.LENGTH_LONG).show();
                    //return;
                    //返工的工序所有的必须指定下一工序完工则不需要指定
                    AlertDialog.Builder builder = new AlertDialog.Builder(ZjActivity.this);
                    //builder.setTitle("注意");
                    builder.setMessage("确定全部完工?");
                    //设置正面按钮
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            confirm(tm);
                            dialog.dismiss();
                        }
                    });
                    //设置反面按钮
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ZjActivity.this, "数量和指定工序不能为空！", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    //显示对话框
                    dialog.show();
                    return;
                }
            }
        }

        confirm(tm);
    }

    private void confirm(String tm) {
        if (TextUtils.isEmpty(et_yssl.getText().toString().trim())) {
            Toast.makeText(ZjActivity.this, "请添加验收数量", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Object> PDAZYMX = new ArrayList<Object>();
        PDAZYMX.add(cptm);//MX002[0]明细表条码
        PDAZYMX.add(str_MX003);//MX003[1]工单单别
        PDAZYMX.add(str_MX004);//MX004[2]工单单号
        PDAZYMX.add(tv_cpph.getText().toString().trim());//MX005[3]产品品号
        PDAZYMX.add(tv_cppm.getText().toString().trim());//MX006[4]产品名称
        PDAZYMX.add(tv_cpgg.getText().toString().trim());//MX007[5]产品规格
        PDAZYMX.add("");//MX008[6]批号
        PDAZYMX.add(tv_szgx.getText().toString().trim());//MX009[7]所在工序  PDAZYMX中MX009 所在工序就是 加工工序（加工顺序）
        PDAZYMX.add(str_MX010);//MX010[8]所在工艺
        PDAZYMX.add(tv_szgy.getText().toString().trim());//MX011[9]工艺名称
        PDAZYMX.add(str_MX012);//MX012[MX00310]工艺性质
        PDAZYMX.add(str_MX013);//MX013[11]工作中心/供应商编号
        PDAZYMX.add(str_MX014);//MX014[12]工作中心/供应商名称
        PDAZYMX.add("");//MX015[13]入库仓库

        //根据"合格" 和"不合格"读取不同数值 //免检不能进入质检界面
        if (tv_jypd.getText().toString().trim().equals("合格")) {
           /* PDAZYMX.add(tv_sl.getText().toString().trim());//MX016[14]转移数量
            PDAZYMX.add("0");//MX017[15]报废数量
            PDAZYMX.add("0");//MX018[16]返工数量*/
            PDAZYMX.add(et_yssl.getText().toString().trim());//MX016[14]转移数量
            PDAZYMX.add(et_bfsl.getText().toString().trim());//MX017[15]报废数量
            PDAZYMX.add("0");//MX018[16]返工数量
            PDAZYMX.add("1");//MX019[17]合格码 0.免检、1.合格、2.不良   (TA003)检查方式未免检，合格码也为免检其他由质检人员确定合格不合格
        } else {
            PDAZYMX.add(et_yssl.getText().toString().trim());//MX016[14]转移数量
            PDAZYMX.add(et_bfsl.getText().toString().trim());//MX017[15]报废数量
            PDAZYMX.add(et_fgsl.getText().toString().trim());//MX018[16]返工数量
            PDAZYMX.add("2");//MX019[17]合格码 0.免检、1.合格、2.不良   (TA003)检查方式未免检，合格码也为免检其他由质检人员确定合格不合格
        }

        PDAZYMX.add("2");//MX020[18]开始码 1.开始、2.结束  质检界面所以此处数值为 2 即结束状态
        PDAZYMX.add("3");//MX021[19]检验码 0.免检、1.未检、2.待验、3.已检 此处为确定值已检测


        PDAZYMX.add(str_MX022);//MX022[20]返工码 0.无返工、1.有返工 //返工码根据出入值
        PDAZYMX.add(tv_fggx.getText().toString().trim());//MX023[21]返工工序 //由质检员在质检界面确定哪道序


        PDAZYMX.add(str_MX024);//MX024[22]加工人员 //登录的转移界面是加工人员11
        PDAZYMX.add(ai.getName());//MX025[23]质检人员21
        PDAZYMX.add(str_MX026);//MX026[24]开始时间 目前是开始时间11
        PDAZYMX.add(str_MX027);//MX027[25]结束时间
        PDAZYMX.add("1");//MX028[26]检验时间21

        PDAZYMX.add("");//MX029[27]存储供应商编号
        PDAZYMX.add("");//MX030[28]存储供应商名称
        PDAZYMX.add(togbtn_zrfg.isChecked() ? "1" : "0"); // 0无责,1有责     //MX030[29]有无责任返工

        List<Object> PDARKMX = new ArrayList<>();
        PDARKMX.add(tm);//MX001[0]容器码 在泰开项目中保存的是产品码
        PDARKMX.add(et_ycsl.getText().toString().trim());//MX004[1]//异常数量
        PDARKMX.add(tv_cpph.getText().toString().trim());//[2]产品品号
        PDARKMX.add(str_MX003);//[3]工单单别
        PDARKMX.add(str_MX004);//[4]工单单号
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("database", ai.getData());
        params.put("PDAZYMX", PDAZYMX);
        params.put("PDARKMX", PDARKMX);
        //2018-5-31 wlg 增加容器码传输
        params.put("rqm", et_tm.getText().toString().trim());
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
        Observable<String> observable = siteService.AddzjmxZJBH(params,
                ZjActivity.this, IP, port);

        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(ZjActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String str) {
                if (!str.equals("")) {
                    //Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ZjActivity.this, "完成！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    /**
     * 检验判断选择
     *
     * @param v
     */
    int yourChoice = -1;

    private void choice() {
        final String[] items = {"合格", "不合格"};
        //final String[] items=datas.split(",");
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(ZjActivity.this);
        singleChoiceDialog.setTitle("选择");
        int Choice = 0;
        final String tvdata = tv_jypd.getText().toString().trim();
        if (tvdata.length() == 0) {
            Choice = -1;
        } else {
            for (int i = 0; i < items.length; i++) {
                String ite = items[i];
                if (ite.equals(tvdata)) {
                    Choice = i;
                }
            }
        }
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

                            tv_jypd.setText(String.valueOf(items[yourChoice]));

                        }
                    }
                });
        singleChoiceDialog.show();
    }

    /**
     * 返工工序选择
     */
    int yourChoice1 = -1;

    private void choice1() {
        //final String[] items = {"合格", "不合格"};
        final String[] items = datas_gx.split(",");
        final String[] itemsgybh = datas_gybh.split(",");
        final String[] itemsgymc = datas_gymc.split(",");
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(ZjActivity.this);
        singleChoiceDialog.setTitle("选择");
        int Choice = 0;
        final String tvdata = tv_fggx.getText().toString().trim();
        if (tvdata.length() == 0) {
            Choice = -1;
        } else {
            for (int i = 0; i < items.length; i++) {
                String ite = items[i];
                if (ite.equals(tvdata)) {
                    Choice = i;
                }
            }
        }
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, Choice,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice1 = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice1 != -1) {
                            //选择工序
                            tv_fggx.setText(String.valueOf(items[yourChoice1]));
                            //选择工艺编号
                            gybh = String.valueOf(itemsgybh[yourChoice1]);
                            //选择工艺名称
                            tv_fggy.setText(String.valueOf(itemsgymc[yourChoice1]));
                        }
                    }
                });
        singleChoiceDialog.show();
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
