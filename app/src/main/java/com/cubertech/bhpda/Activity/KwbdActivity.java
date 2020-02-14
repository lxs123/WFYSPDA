package com.cubertech.bhpda.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KwbdActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.et_rqm)
    EditText etRqm;
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.tv_ph)
    TextView tvPh;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.tv_sl)
    EditText etSl;
    @BindView(R.id.tv_pc)
    EditText tvPc;

    private String ckStr;
    private String kwStr;
    private String id;
    ServiceUtils su = ServiceUtils.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwbd);
        ButterKnife.bind(this);
        etTm.setSelection(0);
        etTm.requestFocus();
        etTm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanCode("wlm");//
                }
                return false;
            }
        });

    }

    private void scanCode(String type) {
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        link = "containertoin";
        params.put("wlm", etTm.getText().toString());
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(KwbdActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                switch (type) {
                    case "wlm"://物料码;
                        if (o != null) {
                            Log.e("#####", o.toString());
                            List<String> list = (List<String>) o;
                            if (list.size() > 0) {
                                tvPm.setText(list.get(2).toString());//品名
                                tvPh.setText(list.get(1).toString());//品号
                                tvXhgg.setText(list.get(3).toString());//规格
                                ckStr = list.get(4).toString();
                                kwStr = list.get(5).toString();
                                Format format = new DecimalFormat("#0");
                                id = String.valueOf(format.format(list.get(0)));//id[0]
                                etTm.setText("");
                                etTm.setSelection(0);
                                etTm.requestFocus();
//                                etRqm.setSelection(0);
//                                etRqm.requestFocus();
                            } else {

                                ToastUtils.showToast("请先维护物料信息!");
                            }

                        } else {
                            ToastUtils.showToast("请先维护物料信息!");
                        }
                }
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(KwbdActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(KwbdActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.btn_bd, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_qx:
            case R.id.left:
                onBackPressed();
                break;

            case R.id.btn_bd:
                HashMap<String, Object> params = new HashMap<String, Object>();
                if (TextUtils.isEmpty(tvPm.getText().toString())) {
                    ToastUtils.showToast("请扫描物料码!");
                    return;
                } else if (TextUtils.isEmpty(etRqm.getText().toString())) {
                    ToastUtils.showToast("请扫描容器码!");
                    return;
                } else if (TextUtils.isEmpty(etSl.getText().toString())) {
                    ToastUtils.showToast("请输入数量!");
                    return;
                } else if (TextUtils.isEmpty(tvPc.getText().toString())) {
                    ToastUtils.showToast("请输入批次号");
                    return;
                } else if (TextUtils.isEmpty(tvPh.getText().toString())) {
                    ToastUtils.showToast("品号不能为空,请先维护物料信息!");
                    return;
                }/* else if (TextUtils.isEmpty(etKw.getText().toString())) {
                    ToastUtils.showToast("请扫描库位码!");
                    return;
                }*/
                List<Object> list = new ArrayList<>();
                list.add(id);//物料号==>id     [0]
                list.add(tvPm.getText().toString());//品名[1]
                list.add(tvPh.getText().toString());//品号[2]
                list.add(tvXhgg.getText().toString());//型号规格[3]
                list.add(etRqm.getText().toString());//容器码[4]
                list.add(etSl.getText().toString());//数量[5]
                list.add(tvPc.getText().toString());//批次[6]
                list.add(ckStr);//仓库[7]
                list.add(kwStr);//库位[8]

                params.put("ckmx", list);
                params.put("strToken", "");
                params.put("strVersion", Utils.getVersions(this));
                params.put("strPoint", "");
                params.put("strActionType", "1001");
                su.callService(KwbdActivity.this, "kcstoragemsg", params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {
                        ToastUtils.showToast("入库成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFailure(String str) {
                        Toast.makeText(KwbdActivity.this, str, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(KwbdActivity.this, error, Toast.LENGTH_LONG).show();
                        su.closeParentDialog();//必须
                    }

                    @Override
                    public void onCompleted() {
                        su.closeParentDialog();//必须
                    }
                });

                break;
        }
    }

    /**
     * @see Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
//        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
//                .setOrientationLocked(false)
//                .setCaptureActivity(ScanActivity.class);
//        Intent scanIntent = intentIntegrator.createScanIntent();
//        startActivityForResult(scanIntent, 999);
        onDelClick("wlm");
    }

    public void xiangjiRqm(View view) {
        onDelClick("rqm");
    }
    private void onDelClick(String type) {
        String typeStr = "";
        switch (type) {
            case "wlm":
                typeStr = "物料码";
                break;
            case "rqm":
                typeStr = "库位码";
                break;
            case "ckm":
                typeStr = "仓库码";
                break;
            case "kwm":
                typeStr = "库位码";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(KwbdActivity.this, R.style.MP_Theme_alertDialog);
        builder.setTitle("提示");
        builder.setMessage("是否清空" + typeStr + "?");
        builder.setPositiveButton("清空", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (type) {
                    case "wlm":
                        etTm.setText("");
                        etTm.setSelection(0);
                        etTm.requestFocus();
                        break;
                    case "rqm":
                        etRqm.setSelection(0);
                        etRqm.requestFocus();
                        etRqm.setText("");

                        break;
                    case "ckm":
                        break;
                    case "kwm":
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
