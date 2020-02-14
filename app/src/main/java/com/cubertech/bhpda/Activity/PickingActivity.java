package com.cubertech.bhpda.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 领料
 * Created by Administrator on 2019/11/21.
 */

public class PickingActivity extends AppCompatActivity {

    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.tv_ll_unit)
    TextView tvUnit;//单位
    @BindView(R.id.tv_ll_address)
    TextView tvAddress;//据点
    @BindView(R.id.tv_ll_corp_id)
    TextView tvCorpId;//企业编号
    @BindView(R.id.tv_ll_work_order)
    TextView tvWorkOrder;//工单单号
    @BindView(R.id.tv_ll_demand_material_no)
    TextView tvDemandMaterialNo;//需求料号
    @BindView(R.id.tv_sl)
    EditText editSl;
    @BindView(R.id.tv_ll_kw)
    TextView tvKw;//指定库位
    @BindView(R.id.tv_pc)
    TextView tvPc;//指定批次
    @BindView(R.id.tv_xc)
    TextView tvXc;//项次
    @BindView(R.id.tv_ztm)
    TextView tvZtm;//状态码
    ServiceUtils su = ServiceUtils.getInstance();
    private String tm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_test);
        ButterKnife.bind(this);
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
                    scanCode();//
                }
                return false;
            }
        });
    }

    private void scanCode() {
        //todo 请填写  方法名
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("wlm", etTm.getText().toString());
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
//        params.put("strUserName", name);
//        params.put("strPassword", pwd);
//        params.put("data", data);
//        params.put("strToken", "");
//        params.put("strVersion", "");
//        params.put("strPoint", "");
//        params.put("strActionType", "1001");
        su.callService(PickingActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(PickingActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PickingActivity.this, error, Toast.LENGTH_LONG).show();
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


    @OnClick({R.id.left, R.id.del1, R.id.btn_ll, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
                onBackPressed();
                break;
            case R.id.del1:
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                break;
            case R.id.btn_ll://领料
                if (TextUtils.isEmpty(tm)) {
                    ToastUtils.showToast("请扫描领料单号！");
                    return;
                }

                //todo 请填写  方法名
                String link = "";
                HashMap<String, Object> params = new HashMap<String, Object>();
                //todo 请修改 增加  参数
                params.put("wlm", tm);
                params.put("strToken", "");
                params.put("strVersion", Utils.getVersions(this));
                params.put("strPoint", "");
                params.put("strActionType", "1001");
//        params.put("strUserName", name);
//        params.put("strPassword", pwd);
//        params.put("data", data);
//        params.put("strToken", "");
//        params.put("strVersion", "");
//        params.put("strPoint", "");
//        params.put("strActionType", "1001");
                su.callService(PickingActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {
                        ToastUtils.showToast("领料成功");
                        //延迟退出
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFailure(String str) {
                        Toast.makeText(PickingActivity.this, str, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PickingActivity.this, error, Toast.LENGTH_LONG).show();
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
                break;
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
}
