package com.cubertech.bhpda.Activity.wfys.picking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.Utils;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2019/12/27.  作废
 */

public class PickingScreenActivity extends AppCompatActivity {
    @BindView(R.id.et_gddh)
    EditText etGddh;
    @BindView(R.id.et_llry)
    EditText etLlry;
    @BindView(R.id.et_gzzx)
    EditText etGzzx;
    @BindView(R.id.et_xm)
    EditText etXm;
    @BindView(R.id.tv_yjkg_date)
    TextView tvYjkgDate;
    private ServiceUtils su = ServiceUtils.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_screen);
        ButterKnife.bind(this);
    }

    int mYear;
    int mMonth;
    int mDay;

    @OnClick({R.id.left, R.id.del1d, R.id.del1d1, R.id.btn_hqfld, R.id.btn_qx, R.id.tv_yjkg_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_yjkg_date:
                Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH) + 1;
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(PickingScreenActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvYjkgDate.setText(year + "-" + month + "-" + day);
                        mYear = year;
                        mMonth = month;
                        mDay = day;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.del1d:
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
                break;
            case R.id.del1d1:
                etLlry.setText("");
                etLlry.setSelection(0);
                etLlry.requestFocus();
                break;
            case R.id.btn_hqfld:
//                onUrlLoad();  当前注释联网请求
                Intent intent = new Intent();
                intent.setClass(PickingScreenActivity.this, PickingScreenFinishActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
        }
    }

    private void onUrlLoad() {
        //todo 请填写  方法名
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(PickingScreenActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {

            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(PickingScreenActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PickingScreenActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
