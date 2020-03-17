package com.cubertech.bhpda.Activity.TransferPackage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.ChtzdActivity;
import com.cubertech.bhpda.Activity.PURMAActivity;
import com.cubertech.bhpda.Activity.TransferPackage.Wgrk.rkActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.JgryUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;
import com.cubertech.bhpda.view.LinearListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static java.lang.Integer.*;

/**
 * 工序转移界面 山东未来界面
 */
public class GxzySdwlActivity extends AppCompatActivity {
    @BindView(R.id.tv_jhh)
    TextView tvGdh;
    @BindView(R.id.tv_scbm)
    TextView tvScbm;
    @BindView(R.id.tv_bm_name)
    TextView tvJgzx;
    @BindView(R.id.tv_ljkch)
    TextView tvPH;
    @BindView(R.id.tv_ljkcmc)
    TextView tvPM;
    @BindView(R.id.tv_ljkcgg)
    TextView tvGG;
    @BindView(R.id.tv_jgxh)
    TextView tvJgxh;
    @BindView(R.id.tv_gx)
    TextView tvGx;
    @BindView(R.id.tv_gxmc)
    TextView tvGxmc;
    @BindView(R.id.tv_jgsb)
    TextView tvYwgl;
    @BindView(R.id.tv_jslx)
    TextView tvJslx;
    @BindView(R.id.et_jssl)
    EditText etYjcl;
    @BindView(R.id.tv_jssj)
    TextView tvJssj;
    @BindView(R.id.et_jsry)
    TextView etJsry;
    @BindView(R.id.lx_title)
    TextView tvLxTitle;
    @BindView(R.id.sl_title)
    TextView tvSlTitle;
    @BindView(R.id.sj_title)
    TextView tvSjTitle;
    @BindView(R.id.ry_title)
    TextView tvRyTitle;
    @BindView(R.id.tv_jhh01)
    TextView tvGdh01;
    @BindView(R.id.btn_js_sl)
    TextView tvJsSl;
    @BindView(R.id.btn_ks_sl)
    TextView tvKsSl;
    @BindView(R.id.btn_jsend_sl)
    TextView tvJsEndSl;

    @BindView(R.id.gxzy_btn_add_content)
    FrameLayout falayBtnAdd;
    @BindView(R.id.gxzy_btn_add)
    Button btnAdd;
    private SiteService siteService;
    private ImageView left;
    //private Button btn_qd, btn_qx;
    private Button btn_js, btn_ks, btn_jsend;

    private EditText et_tm, et_lx, et_jd, et_yjcl, et_ywgl;


    private TextView et_rqxcsl;//容器现存数量
    //    private EditText et_zysl;
    //private String str_jgsx;//存储加工顺序
    private String str_gyxz;//TA005 工艺性质 1.厂内工艺、2.委外工艺
    private String str_jyfs;//TA033 检验方式 0:免检、1:抽检(减量)、2:抽检(正常)、3:抽检(加严)、4:全检
    private String str_MX021;//数据库为空时此处传入也是空值
    private String str_MX026;//开始时间
    private String str_MX027;//结束时间
    private String str_gy;//所在工艺(工艺编号)MW001
    private String str_zxbh;//工作中心/供应商编号MW005

    private String str_MX022;//预留值
    AccountInfo ai;
    private TKApplication application;
    private LinearListView list_jgry;
    private View fullMaskView;//蒙层背景
    private View addJgry; //添加加工人员布局
    private int navigationBarHeight = 0;
    private int panelHeight = 0; //addJgry 高度
    private EditText et_jgry_tm;
    private boolean isJgry;
    private TextView tv_jgry_name;
    private GxzyAdapter gxzyAdapter;
    private List<List<Object>> uplist = new ArrayList<>();// 上传时 加工人员数据 列表
    private List<Object> jgryList = new ArrayList<>();// 扫码得到的 加工人员 数据 列表
    private Button btn_jgry_add;
    private ScrollView scrollView;
    private String cptm;
    private EditText picke_jgry_num;
    private LinearLayout linlay_bz;
    private EditText et_bz;
    private EditText et_qh;
    private boolean isLPH;
    ServiceUtils su = ServiceUtils.getInstance();
    private Object dialogObject;
    private String tm;
    private EditText tv_jgry_scrap_num;
    private EditText tv_jgry_repair_num;
    private EditText tv_jgry_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);*/
        setContentView(R.layout.activity_gxzy_sdwl);
        ButterKnife.bind(this);
        /*et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setFocusable(true);
        et_tm.setFocusableInTouchMode(true);
        et_tm.requestFocus();*/


        et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setSelection(0);
        et_tm.requestFocus();

        et_lx = (EditText) findViewById(R.id.et_lx);
        et_jd = (EditText) findViewById(R.id.et_jd);
        et_yjcl = (EditText) findViewById(R.id.et_yjcl);//预计产量
        et_ywgl = (EditText) findViewById(R.id.et_ywgl);//已完工量

        list_jgry = (LinearListView) findViewById(R.id.jgry_list); //加工人员 listview
        linlay_bz = (LinearLayout) findViewById(R.id.linlay_bz);//炉批号父布局
        et_bz = (EditText) findViewById(R.id.et_bz);//炉批号
        //腔号
        et_qh = (EditText) findViewById(R.id.et_qh);
        gxzyAdapter = new GxzyAdapter();
        list_jgry.setAdapter(gxzyAdapter);
        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        et_rqxcsl = (TextView) findViewById(R.id.et_rqxcsl);//
//        TextView tv_jgxh = (TextView) findViewById(R.id.tv_jgxh);
        tvJgxh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGxhClick();
            }
        });
        tvGx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGxhClick();
            }
        });
        TextView tv_gxmc = (TextView) findViewById(R.id.tv_gxmc);
        tv_gxmc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGxhClick();
            }
        });

//        et_zysl=(EditText) findViewById(R.id.et_zysl);

        left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
        btn_js = (Button) findViewById(R.id.btn_js);
        btn_ks = (Button) findViewById(R.id.btn_ks);
        btn_jsend = (Button) findViewById(R.id.btn_jsend);

        btn_js.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GxzySdwlActivity.this, R.style.MP_Theme_alertDialog);
                builder.setMessage("是否接收?");
                builder.setPositiveButton("接收", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (falayBtnAdd.getVisibility() == View.VISIBLE) {
                            falayBtnAdd.setVisibility(View.GONE);
                        }
                        add("接收");
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        btn_ks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GxzySdwlActivity.this, R.style.MP_Theme_alertDialog);
                builder.setMessage("是否开始?");
                builder.setPositiveButton("开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (falayBtnAdd.getVisibility() == View.VISIBLE) {
                            falayBtnAdd.setVisibility(View.GONE);
                        }
                        add("开始");
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        btn_jsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GxzySdwlActivity.this, R.style.MP_Theme_alertDialog);
                builder.setMessage("是否结束?");
                builder.setPositiveButton("结束", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (falayBtnAdd.getVisibility() == View.GONE) {
//                            falayBtnAdd.setVisibility(View.VISIBLE);
//                            showAddJgry();
//                        } else
                        add("结束");
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddJgry();
            }
        });

       /* btn_qd = (Button) findViewById(R.id.btn_qd);
        btn_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GxzySdwlActivity.this, R.style.MP_Theme_alertDialog);
                builder.setMessage("是否确定?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        add();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
//                add();
            }
        });
        btn_qx = (Button) findViewById(R.id.btn_qx);
        btn_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                showAddJgry();
            }
        });*/
        initView();
    }

    private void initView() {
        attachView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //todo
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
        et_bz.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
//
                }
                return false;
            }
        });
        tvJslx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selLxPosition = 0;
                AlertDialog.Builder builder = new AlertDialog.Builder(GxzySdwlActivity.this, R.style.MP_Theme_alertDialog);
                builder.setTitle("类型选择");
                String[] strings = new String[]{"正常", "返修"};
                if (TextUtils.isEmpty(tvJslx.getText().toString()) || TextUtils.equals(strings[0], tvJslx.getText().toString())) {
                    selLxPosition = 0;
                } else {
                    selLxPosition = 1;
                }
                builder.setSingleChoiceItems(strings, selLxPosition, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selLxPosition = i;
                        tvJslx.setText(strings[selLxPosition]);
                        dialogInterface.cancel();
                    }
                });
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                    }
//                });
//                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    private void clearData() {
        tvGdh.setText("");//[0]计划号
        tvScbm.setText("");//[1] 生产部门
        tvJgzx.setText("");//[2] 部门名称
        tvPH.setText("");//[3] 库存号
        tvPM.setText("");//[4] 库存名称
        tvGG.setText("");//[5] 库存规格
        tvJssj.setText("");//[6] 接收时间
        etJsry.setText("");//[7]接收数量
        tvYwgl.setText("");//[8]加工设备
        tvJslx.setText("");//[9]接收类型
        //btn_qd.setText(objectList.get(10).toString());//[10] 类型
        tvJgxh.setText("");//11工序号
        tvGxmc.setText("");//12工序名称
//        dialogObject = objectList.get(13);//13工序列表
        tvGdh01.setText("");//14 备 注 计划号zy00n18y
        String lx = "";
        tvLxTitle.setText(lx + "类型");
//        tvSlTitle.setText(lx + "数量");
        tvSjTitle.setText(lx + "时间");
        tvRyTitle.setText(lx + "人员");

        etJsry.setText("");
        tvJsSl.setText("0");
        tvKsSl.setText("0");
        tvJsEndSl.setText("0");
        tvGx.setText("");
        tvJgxh.setText("");
        tvGxmc.setText("");
        et_tm.setText("");
        et_tm.setSelection(0);
        et_tm.requestFocus();
        tm = "";
        uplist = new ArrayList<>();
        gxzyAdapter.setList(uplist);
        falayBtnAdd.setVisibility(View.GONE);
    }

    private int selPosition;
    private int selLxPosition;

    private void onGxhClick() {
        //if (!TextUtils.equals(btn_qd.getText().toString(), "接收")) return;
        if (TextUtils.isEmpty(tvGdh.getText().toString())) {
            ToastUtils.showToast("请先扫码!");
            return;
        } else if (TextUtils.isEmpty(dialogObject.toString())) {
            ToastUtils.showToast("请先扫码!");
            return;
        }
        List<Object> objectList = (List<Object>) dialogObject;
        Log.e("#####", objectList.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(GxzySdwlActivity.this, R.style.MP_Theme_alertDialog);
        builder.setTitle("选择工序号");

        String[] strings = new String[objectList.size()];
        String[] nameStrings = new String[objectList.size()];

        String[] stringshwo = new String[objectList.size()];

        selPosition = 0;

        Collections.sort(objectList, new Comparator<Object>() {
            @Override
            public int compare(Object objects, Object objects1) {
                List<Object> objectList1 = (List<Object>) objects;
                List<Object> objectList2 = (List<Object>) objects1;
                if (objectList1.get(0).toString().compareTo(objectList2.get(0).toString()) < 0) {
                    return -1;
                } else if (objectList1.get(0).toString().compareTo(objectList2.get(0).toString()) == 0) {
                    return 0;
                }
                return 1;
            }
        });
        for (int i = 0; i < objectList.size(); i++) {
            List<Object> objectList1 = (List<Object>) objectList.get(i);
            strings[i] = objectList1.get(0).toString();
            nameStrings[i] = objectList1.get(1).toString();
            stringshwo[i] = strings[i] + " " + nameStrings[i];
            if (TextUtils.equals(tvJgxh.getText().toString(), strings[i])) {
                selPosition = i;
            }
        }
//        builder.setItems(strings, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
        builder.setSingleChoiceItems(stringshwo, selPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                selPosition = i;
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("zyjhh", tm);

                /*params.put("zygxh", nameStrings[selPosition]);*/
                params.put("zygxh", stringshwo[selPosition]);
                params.put("zygxhz", nameStrings[selPosition]);
                params.put("strToken", "");
                params.put("strVersion", Utils.getVersions(GxzySdwlActivity.this));
                params.put("strPoint", "");
                params.put("strActionType", "1001");
                su.callService(GxzySdwlActivity.this, "querynum", params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {
                        List<Object> obj = (List<Object>) o;
                        tvJsSl.setText(obj.get(0).toString());
                        tvKsSl.setText(obj.get(1).toString());
                        tvJsEndSl.setText(obj.get(2).toString());
                    }

                    @Override
                    public void onFailure(String str) {
                        CommonUtil.showErrorDialog(GxzySdwlActivity.this, str);
                        su.closeParentDialog();//必须
                    }

                    @Override
                    public void onError(String error) {
                        su.closeParentDialog();
                        ToastUtils.showToast(error);
                    }

                    @Override
                    public void onCompleted() {
                        su.closeParentDialog();
                    }
                });

                tvGx.setText(strings[selPosition] + "  " + nameStrings[selPosition]);
                tvJgxh.setText(strings[selPosition]);
                tvGxmc.setText(nameStrings[selPosition]);
            }
        });

        builder.show();
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                tvJgxh.setText(strings[selPosition]);
//                tvGxmc.setText(nameStrings[selPosition]);
//            }
//        });
//        builder.setNegativeButton("取消", null);
//        builder.show();
    }

    /**
     * 最后一道 工序 完成后选择是否直接入库
     */
    private void rkshow() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(GxzySdwlActivity.this);
        normalDialog.setMessage("是否直接入库");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rk();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 直接入库，调用的是rkActivity 和完工入库相同的界面
     */
    private void rk() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", ai);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.putExtra("tm", et_tm.getText().toString().trim());
        intent.setClass(this, rkActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        this.finish();
    }


    /**
     * 添加转移数据
     */
    private void add(String zt) {
        if (TextUtils.isEmpty(tvGdh.getText().toString())) {
            ToastUtils.showToast("请扫描条码!");
            return;
        } else if (TextUtils.isEmpty(tvJgxh.getText().toString())) {
            ToastUtils.showToast("请选择工序号!");
            return;
        } else if (TextUtils.isEmpty(tvJslx.getText().toString())) {
            ToastUtils.showToast("请选择类型!");
            return;
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<Object> objectList = new ArrayList<>();
        objectList.add(tvGdh.getText().toString());//0 计划号
        objectList.add(tvScbm.getText().toString());//1  生产部门
        objectList.add(tvJgzx.getText().toString());//2  部门名称
        objectList.add(tvPH.getText().toString());//3  库存号
        objectList.add(tvPM.getText().toString());//4  库存名称
        objectList.add(tvGG.getText().toString());//5  库存规格
        objectList.add(tvJssj.getText().toString());//6  时间
        objectList.add(etYjcl.getText().toString());//7  数量
        objectList.add(tvYwgl.getText().toString());//8  加工设备
        objectList.add(tvJslx.getText().toString());//9  接收类型

        objectList.add(tvJgxh.getText().toString());//10 工序号
        objectList.add(tvGxmc.getText().toString());//11  工序名称
        objectList.add(etJsry.getText().toString());//12  接收人员
        //objectList.add(btn_qd.getText().toString());//13 状态
        objectList.add(zt.trim());//13 状态
        if (TextUtils.equals(zt, "结束"))
            objectList.add(uplist);//14 结束时传

        params.put("code", objectList);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(GxzySdwlActivity.this, "bccode", params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                ToastUtils.showToast("保存成功!");
                clearData();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        onBackPressed();
//                    }
//                }, 500);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(GxzySdwlActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                ToastUtils.showToast(error);
                su.closeParentDialog();
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();
            }
        });
    }


    /**
     * 扫描二维码
     */
    private void scanCode() {
        tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(GxzySdwlActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
            return;
        }
        String strTm = et_tm.getText().toString();
//        if (!strTm.contains("#")) {
//            ToastUtils.showToast("请扫描正确格式的单号！");
//            return;
//        }
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0])) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        et_tm.setText(split[0]);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("jhh", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(GxzySdwlActivity.this, "hqcode", params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;
                //0工单单号  1原来部门名称  2加工中心  3品号  4品名  5规格  6时间  7数量  8已完工量
                //9加工类型  10状态   11工序号  12工序名称  13工序列表  14备注计划 15接收数量，16开始时间
                //17j结束时间
                Log.e("######gxzy", objectList.toString());
                tvGdh.setText(objectList.get(0).toString());
                tvGdh01.setText(objectList.get(0).toString());//[0]0工单单号
                tvScbm.setText(objectList.get(1).toString());//[1] 生产部门
                tvJgzx.setText(objectList.get(2).toString());//[2] 部门名称
                tvPH.setText(objectList.get(3).toString());//[3] 3品号
                tvPM.setText(objectList.get(4).toString());//[4] 4品名
                tvGG.setText(objectList.get(5).toString());//[5] 5规格
                tvJssj.setText(objectList.get(6).toString());//[6] 接收时间
                etYjcl.setText(objectList.get(7).toString());//[7]7数量
                tvYwgl.setText(objectList.get(8).toString());//[8]8已完工量
                tvJslx.setText(objectList.get(9).toString());//[9]接收类型
                //btn_qd.setText(objectList.get(10).toString());//[10] 类型
                tvJgxh.setText(objectList.get(11).toString());//11工序号
                tvGxmc.setText(objectList.get(12).toString());//12工序名称
                dialogObject = objectList.get(13);//13工序列表
//                tvGdh01.setText(objectList.get(14).toString());//14 备 注 计划号zy00n18y
                String lx = objectList.get(10).toString();
                tvLxTitle.setText(lx + "类型");
//                tvSlTitle.setText(lx + "数量");
                tvSjTitle.setText(lx + "时间");
                tvRyTitle.setText(lx + "人员");
                //lxs 2019 11 5 弃用
//                tvJsSl.setText(String.valueOf(objectList.get(15)));//15 接收累计数量
//                tvKsSl.setText(String.valueOf(objectList.get(16)));//16 开始累计数量
//                tvJsEndSl.setText(String.valueOf(objectList.get(17)));//17 结束累计数量
                SharedPreferences sp = getSharedPreferences(
                        "config", Activity.MODE_PRIVATE);

                String name = sp.getString("name", null);
                etJsry.setText(name);

                et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();

                onGxhClick();//弹出选择
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(GxzySdwlActivity.this, str);
                su.closeParentDialog();//必须
                et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();
            }

            @Override
            public void onError(String error) {
                ToastUtils.showToast(error);
                su.closeParentDialog();
                et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();
            }
        });

//        siteService = SiteService.getInstants();
//        //通过Application传值
//        String IP = "";
//        String port = "";
//        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
//            IP = application.getUrl();
//            port = application.getPort();
//        }
//        //先弄一个观察者
//        Observable<List<Object>> observable = siteService.DMcodeGxzyBh(params,
//                GxzySdwlActivity.this, IP, port);
//        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
//            @Override
//            public void onCompleted() {
//                siteService.closeParentDialog();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                siteService.closeParentDialog();
//                Toast.makeText(GxzySdwlActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
//                et_tm.setText("");
//                et_tm.setSelection(0);
//                et_tm.requestFocus();
//            }
//
//            @Override
//            public void onNext(List<Object> obj) {
//                if (obj == null || obj.size() <= 0) {
//                    return;
//                }
//
//                //List<Object> o=obj;
//                //Toast.makeText(GxzyActivity.this, "" + obj.size(), Toast.LENGTH_LONG).show();
//                et_lx.setText(obj.get(0).toString());//[0]转移类型正常转移
//                et_jd.setText(obj.get(1).toString());//[1]开始
//                tv_gdxx.setText(obj.get(2).toString() + "-" + obj.get(3).toString());//[2]单别-[3]单号
//                tv_cpph.setText(obj.get(4).toString());//[4]产品品号
//                tv_cppm.setText(obj.get(5).toString());//[5]产品品名
//                tv_cpgg.setText(obj.get(6).toString());////[6]产品规格
//                //str_jgsx=obj.get(7).toString();//加工顺序
//                tv_jggy.setText(obj.get(8).toString());//[8]工艺名称
//                tv_jggx.setText(obj.get(7).toString());//[7]加工顺序 (加工工序)()
//                if (TextUtils.equals(obj.get(7).toString().trim(), "0010") &&
//                        TextUtils.equals(obj.get(1).toString().trim(), "开始")) {
//                    linlay_bz.setVisibility(View.VISIBLE);
//                } else {
//                    linlay_bz.setVisibility(View.GONE);
//                }
//                tv_jgsx.setText(obj.get(7).toString());//[7]加工顺序 (加工工序)()
//                tv_jgzx.setText(obj.get(9).toString());//[9]工作中心/供应商名称
//                str_jyfs = obj.get(10).toString();//TA033[10]检验方式 0:免检、1:抽检(减量)、2:抽检(正常)、3:抽检(加严)、4:全检
//                str_gyxz = obj.get(11).toString();//[11]工艺性质 1.厂内工艺、2.委外工艺
//                str_gy = obj.get(12).toString();//[12]所在工艺(工艺编号)MW001
//                str_zxbh = obj.get(13).toString();//[13]工作中心/供应商编号MW005
//                str_MX021 = obj.get(14).toString();//[14]MX021第一次扫或最新工序描此处传入空值
//                str_MX026 = obj.get(15).toString();//开始时间
//                str_MX027 = obj.get(16).toString();//结束时间
//
//                str_MX022 = obj.get(17).toString();
//                cptm = obj.get(18).toString();
//                //todo xin增
//                double yjcl = Double.parseDouble(obj.get(19).toString().trim());
//                Format f_yjcl = new DecimalFormat("#.00");//当前保留两位小数
//                et_yjcl.setText(f_yjcl.format(yjcl));
//                et_ywgl.setText(obj.get(20).toString().trim());
//                if (obj.size() > 21) {
//                    //todo wang 新增
//                    if (obj.get(21).toString().trim() != "") {
//                        double p = Double.parseDouble(obj.get(21).toString().trim());
//                        Format format = new DecimalFormat("#.00");//当前保留两位小数
//                        et_rqxcsl.setText(format.format(p));//容器现存数量
//                        //String.format("%.2f", obj.get(21).toString().trim());//报错
//                    }
//
//                }
//
//                //只有第一道序的开始阶段能够修改数值
//                if (TextUtils.isEmpty(et_rqxcsl.getText().toString().trim()) && et_jd.getText().toString().trim().equals("结束")) {
//                    et_rqxcsl.setCursorVisible(true);
//                    et_rqxcsl.setFocusable(true);
//                    et_rqxcsl.setFocusableInTouchMode(true);
//                }
//
//                if (obj.get(11).toString().equals("2") && obj.get(1).toString().equals("开始")) {
//                    //工艺性质是委外，而且是开始才 弹出提示框是否更换供应商
//                    //如果是更换供应商增需要再次选择供应商
//                    //修改供应商值即可
//                    dialogShow();
//                }
//            }
//        });
    }

    /**
     * 扫描二维码
     */
    private void jgryScanCode() {
        String tm = et_jgry_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(GxzySdwlActivity.this, "请先扫描", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("UserName", tm);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
//        siteService = SiteService.getInstants();
//        //通过Application传值
//        String IP = "";
//        String port = "";
//        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
//            IP = application.getUrl();
//            port = application.getPort();
//        }
//
        su.callService(GxzySdwlActivity.this, "gtuserinfo", params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;
                if (objectList == null || objectList.size() <= 0) {
                    return;
                }
                jgryList = new ArrayList<>();
                jgryList = objectList;
                tv_jgry_name.setText(objectList.get(1).toString().trim());//员工姓名

            }

            @Override
            public void onFailure(String str) {
                ToastUtils.showToast(str);
            }

            @Override
            public void onError(String error) {
                ToastUtils.showToast(error);
                su.closeParentDialog();
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();
            }
        });
//        //todo 1
//        //先弄一个观察者
//        Observable<List<Object>> observable = siteService.getYg(params,
//                GxzySdwlActivity.this, IP, port);
//        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
//            @Override
//            public void onCompleted() {
//                siteService.closeParentDialog();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                siteService.closeParentDialog();
//                Toast.makeText(GxzySdwlActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNext(List<Object> obj) {
//                if (obj == null || obj.size() <= 0) {
//                    return;
//                }
//                jgryList = new ArrayList<>();
//                jgryList = obj;
//                tv_jgry_name.setText(obj.get(1).toString().trim());//员工姓名
//            }
//        });
    }

    /**
     * 提示是否更换供应商
     */
    private void dialogShow() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(GxzySdwlActivity.this);
        normalDialog.setMessage("是否更换供应商?");
        normalDialog.setPositiveButton("确定更换",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiceGYS();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 选择供应商
     */
    private void choiceGYS() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", ai);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(this, PURMAActivity.class);
        this.startActivityForResult(intent, 1);
        this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }

    //重写   处理返回信息的监听（回调方法）
    //onActivityResult通用监听  监听所有返回信息的
    //必须要有requestCode区分有哪个请求返回的
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //获取返回信息
                String MA001 = data.getExtras().getString("MA001");//编号
                String MA002 = data.getExtras().getString("MA002");//名称

                //tv_jgzx.setText(obj.get(9).toString());//[9]工作中心/供应商名称
                //str_zxbh=obj.get(13).toString();//[13]工作中心/供应商编号MW005
                //确定更换供应商======================================================================

//                tv_jgzx.setText(MA002);
                str_zxbh = MA001;
                //Toast.makeText(this,"返回为：MA001:"+MA001+" MA002:"+MA002,Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "数据选择错误，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 999) {
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
                if (!isLPH) {
                    et_tm.setText(ScanResult);
                    scanCode();
                } else {
                    et_bz.setText(ScanResult);
                }


            } else {
                et_jgry_tm.setText(ScanResult);
                jgryScanCode();
            }

        }
    }

    public void xiangji(View view) {
        et_tm.setText("");
        et_tm.setSelection(0);
        et_tm.requestFocus();
//        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
//                .setOrientationLocked(false)
//                .setCaptureActivity(ScanActivity.class);
//        Intent scanIntent = intentIntegrator.createScanIntent();
//        startActivityForResult(scanIntent, 999);
        isJgry = false;
        isLPH = false;
    }

    public void xiangji2(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        xiangji(view);
        isLPH = true;
    }


    /**
     * @see Activity#onBackPressed() 返回事件 @ add wlg
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

    public void xiangji1(View view) {
        et_jgry_tm.setText("");
        et_jgry_tm.setSelection(0);
        et_jgry_tm.requestFocus();
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
        addJgry = View.inflate(this, R.layout.dialog_gxzy_jgry_sdwl, null);
        addJgry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        final String[] pick_num = {"50"};
        picke_jgry_num = (EditText) addJgry.findViewById(R.id.pick_jgry_num);
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
        tv_jgry_scrap_num = (EditText) addJgry.findViewById(R.id.pick_jgry_scrap_num);
        tv_jgry_repair_num = (EditText) addJgry.findViewById(R.id.pick_jgry_repair_num);
        tv_jgry_note = (EditText) addJgry.findViewById(R.id.pick_jgry_note);
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
                    Toast.makeText(GxzySdwlActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(tv_jgry_name.getText().toString())) {
                    Toast.makeText(GxzySdwlActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(picke_jgry_num.getText().toString())) {
                    Toast.makeText(GxzySdwlActivity.this, "请填写合格数量!!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(tv_jgry_scrap_num.getText().toString())) {
                    Toast.makeText(GxzySdwlActivity.this, "请填写报废数量!!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(tv_jgry_repair_num.getText().toString())) {
                    Toast.makeText(GxzySdwlActivity.this, "请填写返修数量!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isOnly = true;

//                left.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        left.performClick();
//                    }
//                });
//                jgryList.add(tv_jgry_name.getText().toString());//
                jgryList.add(picke_jgry_num.getText().toString());
                jgryList.add(tv_jgry_scrap_num.getText().toString());
                jgryList.add(tv_jgry_repair_num.getText().toString());
                jgryList.add(tv_jgry_note.getText().toString());
                if (uplist != null && uplist.size() > 0 && jgryList != null)
                    for (int i = 0; i < uplist.size(); i++) {
                        if (TextUtils.equals(uplist.get(i).get(0).toString().trim(), jgryList.get(0).toString().trim())) {
                            isOnly = false;
                            break;
                        }
                    }
                if (!isOnly) {
                    Toast.makeText(GxzySdwlActivity.this, "请先扫描", Toast.LENGTH_SHORT).show();
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
                et_jgry_tm.setText("");
                tv_jgry_name.setText("");
//                hideAddJgry();
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
        if (TextUtils.isEmpty(tm)) {
            Toast.makeText(GxzySdwlActivity.this, "请扫码", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (TextUtils.isEmpty(et_jd.getText().toString())) {
//            Toast.makeText(GxzySdwlActivity.this, "阶段状态不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (TextUtils.equals(et_jd.getText().toString(), "开始")) {
//            Toast.makeText(GxzySdwlActivity.this, "开始阶段不需要添加人员", Toast.LENGTH_SHORT).show();
//            return;
//        }
        fullMaskView.setVisibility(View.VISIBLE);
        addJgry.setVisibility(View.VISIBLE);
        et_jgry_tm.setText("");
        tv_jgry_name.setText("");
        picke_jgry_num.setText("");
        tv_jgry_scrap_num.setText("");
        tv_jgry_repair_num.setText("");
        tv_jgry_note.setText("");

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
                view = LayoutInflater.from(GxzySdwlActivity.this).inflate(R.layout.sdwl_gxzy_activity_adapter_item, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            List<Object> objectList = this.list.get(i);
            if (objectList != null && objectList.size() > 0) {
                holder.tv_name.setText(objectList.get(1).toString().trim());//1、员工姓名；0、员工编号；3、数量
                holder.tv_num.setText(objectList.get(2).toString().trim());
                holder.tv_scrap_number.setText(objectList.get(3).toString().trim());
                holder.tv_repair_num.setText(objectList.get(4).toString());
                holder.tv_note.setText(objectList.get(5).toString());
                final int finalI = i;
                holder.imgv_reduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GxzySdwlActivity.this);
                        builder.setMessage("是否删除该人员");
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
            private final TextView tv_scrap_number;
            private final TextView tv_repair_num;
            private final TextView tv_note;


            public ViewHolder(View view) {
                tv_name = (TextView) view.findViewById(R.id.jgry_name);
                tv_num = (TextView) view.findViewById(R.id.jgry_num);
                imgv_reduce = (ImageView) view.findViewById(R.id.imgv_del);
                tv_scrap_number = (TextView) view.findViewById(R.id.jgry_scrap_number);
                tv_repair_num = (TextView) view.findViewById(R.id.jgry_repair_num);
                tv_note = (TextView) view.findViewById(R.id.jgry_note);

            }
        }
    }
}
