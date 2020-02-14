package com.cubertech.bhpda.Activity.wfys;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 入库检验
 * Created by admin on 2019/12/30.
 */

public class RkjyActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_cpph)
    TextView tvCpph;
    @BindView(R.id.tv_cppm)
    TextView tvCppm;
    @BindView(R.id.tv_cpgg)
    TextView tvCpgg;


    @BindView(R.id.et_yssl)
    EditText etYssl;
    @BindView(R.id.et_bfsl)
    EditText etBfsl;
    @BindView(R.id.tv_gddh)
    TextView tvGddh;
    @BindView(R.id.tv_yjcl)
    TextView tvYjcl;
    @BindView(R.id.tv_sl)
    TextView tvSl;
    @BindView(R.id.tv_szgx)
    TextView tvSzgx;
    @BindView(R.id.tv_szgy)
    TextView tvSzgy;
    private String blyy = "";//不良原因

    private ServiceUtils su = ServiceUtils.getInstance();
    private String tm;
    private String jybh;
    private String jymc;
    private List<Object> objectList;
    private int selPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rkjy);
        ButterKnife.bind(this);
        etTm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    onScanResult();
                }
                return false;
            }
        });
        etBfsl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //执行相关读取操作
                    if (Double.parseDouble(etBfsl.getText().toString()) > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RkjyActivity.this, R.style.MP_Theme_alertDialog);
                        builder.setTitle("不良原因");
                        builder.setPositiveButton("确定", null);
                        builder.setNegativeButton("取消", null);
                        AlertDialog alertDialog = builder.create();
                        View view1 = LayoutInflater.from(RkjyActivity.this).inflate(R.layout.dialog_edit_rkjy_bhgsl, null);
                        TextView tvSl = (TextView) view1.findViewById(R.id.dialog_sl);
                        EditText edYY = (EditText) view1.findViewById(R.id.dialog_bhgyy);
                        TextView tvJybh = (TextView) view1.findViewById(R.id.dialog_jybh);
                        TextView tvJymc = (TextView) view1.findViewById(R.id.dialog_jymc);
                        tvJymc.setText(jymc);
                        tvJybh.setText(jybh);
                        tvSl.setText(etBfsl.getText().toString());
                        edYY.setText(blyy);
                        edYY.setSelection(edYY.getText().toString().length());
                        edYY.requestFocus();
                        alertDialog.setView(view1);
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (TextUtils.isEmpty(edYY.getText().toString())) {
                                            ToastUtils.showToast("请描述不良原因!");
                                            return;
                                        }
                                        blyy = edYY.getText().toString();
                                        dialogInterface.cancel();
                                    }
                                });
                            }
                        });
                        alertDialog.show();

                    }
                }
                return false;
            }
        });
    }

    private void onScanResult() {
        if (!etTm.getText().toString().contains("#")) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        String strTm = etTm.getText().toString();
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0])) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }

        //todo 请填写  方法名
        String link = "rkjy";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(RkjyActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = split[0];
                //0 工单单号1 产品品号 2品名 3规格 4预计产量 5已完工量  6检验编号 7检验名称
                List<Object> objectList = (List<Object>) o;
                Log.e("#######", objectList.toString());
                tvGddh.setText(String.valueOf(objectList.get(0)));
                tvCpph.setText(String.valueOf(objectList.get(1)));
                tvCppm.setText(String.valueOf(objectList.get(2)));
                tvCpgg.setText(String.valueOf(objectList.get(3)));
                tvYjcl.setText(String.valueOf(objectList.get(4)));
                tvSl.setText(String.valueOf(objectList.get(5)));

                jybh = String.valueOf(objectList.get(6));
                jymc = String.valueOf(objectList.get(7));
//                onGyURL();
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(RkjyActivity.this, str);
                su.closeParentDialog();//必须
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RkjyActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    private void onUrlLoad() {
        List<Object> shjy1 = new ArrayList<>();
        String gddh = tvGddh.getText().toString();
        String[] split = gddh.split("-");
        shjy1.add(split[0]);//0单别
        shjy1.add(split[1]);//1单号
        shjy1.add("");//2序号
        shjy1.add(tvCpph.getText().toString());//3品号
        shjy1.add(tvSzgx.getText().toString());//4工艺
        SharedPreferences sp = getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        String name = sp.getString("name", null);
        shjy1.add(name);//[5]操作人员
//        shjy1.add(etHgsl.getText().toString());//6合格数量
        shjy1.add(jymc + blyy);//6不合格原因
        shjy1.add(etBfsl.getText().toString());//7不合格数量


        //yyyymmddhhmmss
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = sdf.format(timeMillis);
        shjy1.add(format);//当前时间8

        //todo 请填写  方法名
        String link = "bcrkjy";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", shjy1);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(RkjyActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                ToastUtils.showToast("检验成功！");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 500);

            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(RkjyActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RkjyActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    @OnClick({R.id.left, R.id.del1, R.id.btn_wcjy, R.id.btn_qx, R.id.tv_szgy, R.id.tv_szgx})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.del1:
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                break;
            case R.id.btn_wcjy:
                double dyslDouble = Double.parseDouble(TextUtils.isEmpty(tvYjcl.getText().toString()) ? "0" : tvYjcl.getText().toString());
                double hgslDouble = Double.parseDouble(TextUtils.isEmpty(etYssl.getText().toString()) ? "0" : etYssl.getText().toString());
                double bhgslDouble = Double.parseDouble(TextUtils.isEmpty(etBfsl.getText().toString()) ? "0" : etBfsl.getText().toString());
                if (hgslDouble > dyslDouble) {
                    ToastUtils.showToast("合格数量不能大于已完工数量！");
                    return;
                } else if (hgslDouble + bhgslDouble != dyslDouble) {
                    ToastUtils.showToast("请注意，合格数量加上不合格数量等于已完工数量！");
                    return;
                } else if (TextUtils.isEmpty(tvGddh.getText().toString())) {
                    ToastUtils.showToast("请扫描！");
                    return;
                }
//                else if (TextUtils.isEmpty(tvSzgy.getText().toString())) {
//                    ToastUtils.showToast("请选择工艺！");
//                    return;
//                }
                AlertDialog.Builder builder = new AlertDialog.Builder(RkjyActivity.this, R.style.MP_Theme_alertDialog);
                builder.setTitle("提示");
                builder.setMessage("确定要进行入库检验么？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onUrlLoad();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.left:
//                break;
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.tv_szgx:
            case R.id.tv_szgy:
                onGyClick();
                break;
        }
    }

    private void onGyURL() {
        String link = "gyxx";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", tvGddh.getText().toString());
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(RkjyActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                objectList = (List<Object>) o;
                tvSzgy.performClick();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(RkjyActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RkjyActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    /**
     * 工艺选择 onGyClick
     */
    private void onGyClick() {
        if (TextUtils.isEmpty(tvGddh.getText().toString())) {
            ToastUtils.showToast("请先扫描！");
            return;
        } else if (ListUtils.isEmpty(objectList)) {
            ToastUtils.showToast("请先扫描！");
            return;
        }
        List<Object> objectList = this.objectList;
//        Log.e("#####", objectList.toString());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RkjyActivity.this, R.style.MP_Theme_alertDialog);
        builder.setTitle("选择工艺");

        String[] strings = new String[objectList.size()];
        String[] nameStrings = new String[objectList.size()];

        String[] stringshwo = new String[objectList.size()];

        selPosition = 0;
//        //排序
//        Collections.sort(objectList, new Comparator<Object>() {
//            @Override
//            public int compare(Object objects, Object objects1) {
//                List<Object> objectList1 = (List<Object>) objects;
//                List<Object> objectList2 = (List<Object>) objects1;
//                if (parseInt(objectList1.get(0).toString()) < parseInt(objectList2.get(0).toString())) {
//                    return -1;
//                } else if (parseInt(objectList1.get(0).toString()) < parseInt(objectList2.get(0).toString())) {
//                    return 0;
//                }
//                return 1;
//            }
//        });
        for (int i = 0; i < objectList.size(); i++) {
            List<Object> objectList1 = (List<Object>) objectList.get(i);
            strings[i] = objectList1.get(0).toString();
            nameStrings[i] = objectList1.get(1).toString();
            stringshwo[i] = strings[i] + " " + nameStrings[i];
            if (TextUtils.equals(tvSzgx.getText().toString(), strings[i])) {
                selPosition = i;
            }
        }
        builder.setSingleChoiceItems(stringshwo, selPosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                selPosition = i;
//                tvGx.setText(strings[selPosition] + "  " + nameStrings[selPosition]);
//                tvJgxh.setText(strings[selPosition]);
//                tvGxmc.setText(nameStrings[selPosition]);
                tvSzgy.setText(nameStrings[selPosition]);
                tvSzgx.setText(strings[selPosition]);
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
