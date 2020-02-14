package com.cubertech.bhpda.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.wfys.RkjyActivity;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/11/21.
 */

public class ChtzdActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.tv_ll_unit)
    TextView tvUnit;
    @BindView(R.id.tv_ll_address)
    TextView tvAddress;//据点
    @BindView(R.id.tv_ll_corp_id)
    TextView tvCorpId;//企业编号
    @BindView(R.id.tv_ll_shipping_no)
    TextView tvShippingNo;//料件编号
    @BindView(R.id.tv_sl)
    EditText editSl;//数量
    @BindView(R.id.tv_ll_kw)
    TextView tvKw;//指定库位
    @BindView(R.id.tv_pc)
    TextView tvPc;//指定批次
    @BindView(R.id.tv_xc)
    TextView tvXc;//项次
    ServiceUtils su = ServiceUtils.getInstance();
    private String tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chtzd_test);
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
        su.callService(ChtzdActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(ChtzdActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChtzdActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.del1, R.id.btn_qx})
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
            case R.id.btn_qx:
                onBackPressed();
                break;
        }
    }
//    领料
}
