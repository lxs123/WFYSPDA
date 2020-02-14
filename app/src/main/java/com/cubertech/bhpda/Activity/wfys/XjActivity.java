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
 * 工序检验
 * Created by admin on 2019/12/27.
 */

public class XjActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_cpph)
    TextView tvCpph;
    @BindView(R.id.tv_cppm)
    TextView tvCppm;
    @BindView(R.id.tv_cpgg)
    TextView tvCpgg;
    @BindView(R.id.tv_zylx)
    TextView tvZylx;
    @BindView(R.id.tv_sl)
    TextView tvSl;
    @BindView(R.id.tv_szgx)
    TextView tvSzgx;
    @BindView(R.id.tv_szgy)
    TextView tvSzgy;
    @BindView(R.id.et_yssl)
    EditText etHgsl;
    @BindView(R.id.et_bfsl)
    EditText etBfsl;
    @BindView(R.id.tv_gddh)
    TextView tvGddh;
    @BindView(R.id.tv_yjcl)
    TextView tvYjcl;
    private String blyy = "";//不良原因

    private ServiceUtils su = ServiceUtils.getInstance();
    private int selPosition;
    private String dbStr;
    private String dhStr;
    private List<Object> objectList;
    private List<Object> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xj);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(XjActivity.this, R.style.MP_Theme_alertDialog);
                        builder.setTitle("不良原因");
                        builder.setPositiveButton("确定", null);
                        builder.setNegativeButton("取消", null);
                        AlertDialog alertDialog = builder.create();
                        View view1 = LayoutInflater.from(XjActivity.this).inflate(R.layout.dialog_edit_dhys_bhgsl, null);
                        TextView tvSl = (TextView) view1.findViewById(R.id.dialog_sl);
                        EditText edYY = (EditText) view1.findViewById(R.id.dialog_bhgyy);
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
        etTm.setText(split[0]);
        //todo 请填写  方法名
        String link = "xjjy";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(XjActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                /**
                 item.TA001      0单别
                 item.TA002      1工单单号
                 item.TA003      2加工顺序
                 item.TA004      3工艺
                 item.MW002      4工艺名称
                 item.TA006      5产品品号
                 item.TA034      6品名
                 item.TA035      7规格
                 item.TA015      8预计产量
                 item.TA017      9已完工量
                 */
                List<Object> objects = (List<Object>) o;

                Log.e("######", objects.toString());
                data = objects;
                List<Object> data1 = (List<Object>) objects.get(0);
                dbStr = String.valueOf(data1.get(0));
                dhStr = String.valueOf(data1.get(1));

                onGyURL();
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(XjActivity.this, str);
                su.closeParentDialog();//必须
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(XjActivity.this, error, Toast.LENGTH_LONG).show();
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

    private void onGyURL() {
        String link = "gyxx";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", dbStr + "-" + dhStr);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(XjActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                objectList = (List<Object>) o;
                tvSzgy.performClick();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(XjActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(XjActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    private void onUrlLoad() {
        List<Object> shjy1 = new ArrayList<>();
        shjy1.add(dbStr);//0单别
        shjy1.add(dhStr);//1单号
        shjy1.add(tvZylx.getText().toString());//2序号
        shjy1.add(tvCpph.getText().toString());//3品号
        shjy1.add(tvSzgx.getText().toString());//4工艺
        SharedPreferences sp = XjActivity.this.getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        String name = sp.getString("name", null);
        shjy1.add(name);//[5]操作人员
//        shjy1.add(etHgsl.getText().toString());//6合格数量
        shjy1.add(blyy);//6不合格原因
        shjy1.add(etBfsl.getText().toString());//7不合格数量


        //yyyymmddhhmmss
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format = sdf.format(timeMillis);
        shjy1.add(format);//当前时间8

        //todo 请填写  方法名
        String link = "bcxjjy";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", shjy1);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(XjActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
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
                CommonUtil.showErrorDialog(XjActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(XjActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    @OnClick({R.id.left, R.id.del1, R.id.tv_szgy, R.id.btn_wcjy, R.id.btn_qx, R.id.tv_szgx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.del1:
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                break;
            case R.id.tv_szgx:
            case R.id.tv_szgy:
                onGyClick();
                break;
            case R.id.btn_wcjy:
                double dyslDouble = Double.parseDouble(tvYjcl.getText().toString());
                double hgslDouble = Double.parseDouble(etHgsl.getText().toString());
                double bhgslDouble = Double.parseDouble(etBfsl.getText().toString());
                if (hgslDouble > dyslDouble) {
                    ToastUtils.showToast("合格数量不能大于已完工数量！");
                    return;
                } else if (hgslDouble + bhgslDouble != dyslDouble) {
                    ToastUtils.showToast("请注意，合格数量加上不合格数量等于已完工数量！");
                    return;
                } else if (TextUtils.isEmpty(dbStr) || TextUtils.isEmpty(dhStr)) {
                    ToastUtils.showToast("请扫描！");
                    return;
                } else if (TextUtils.isEmpty(tvSzgy.getText().toString())) {
                    ToastUtils.showToast("请选择工艺！");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(XjActivity.this, R.style.MP_Theme_alertDialog);
                builder.setTitle("提示");
                builder.setMessage("确定要进行完工检验么？");
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
            case R.id.btn_qx:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 工艺选择 onGyClick
     */
    private void onGyClick() {
        if (TextUtils.isEmpty(dbStr) || TextUtils.isEmpty(dhStr)) {
            ToastUtils.showToast("请先扫描！");
            return;
        } else if (ListUtils.isEmpty(objectList)) {
            ToastUtils.showToast("请先扫描！");
            return;
        }
        List<Object> objectList = this.objectList;
//        Log.e("#####", objectList.toString());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(XjActivity.this, R.style.MP_Theme_alertDialog);
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
                for (Object o : data) {
                    List<Object> objectList1 = (List<Object>) o;
                    if (objectList1.contains(tvSzgx.getText().toString())) {
                        tvCpph.setText(String.valueOf(objectList1.get(5)));
                        tvCppm.setText(String.valueOf(objectList1.get(6)));
                        tvCpgg.setText(String.valueOf(objectList1.get(7)));
                        tvZylx.setText(String.valueOf(objectList1.get(2)));
                        tvGddh.setText(String.valueOf(objectList1.get(1)));
                        tvYjcl.setText(String.valueOf(objectList1.get(8)));
                        tvSl.setText(String.valueOf(objectList1.get(9)));
                    }
                }

            }
        });

        builder.show();
    }
}
