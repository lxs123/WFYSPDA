package com.cubertech.bhpda.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/8/28.
 */

public class PdTestDetailActivity extends Activity {

    @BindView(R.id.tv_qdbh)
    TextView tvQdbh;
    @BindView(R.id.tv_xh)
    TextView tvXh;
    @BindView(R.id.tv_wlbh)
    TextView tvWlbh;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.tv_wlmc)
    TextView tvWlmc;
    @BindView(R.id.tv_wlgg)
    TextView tvWlgg;
    @BindView(R.id.tv_qybh)
    TextView tvQybh;
    @BindView(R.id.tv_jd)
    TextView tvJd;
    @BindView(R.id.tv_kcsl)
    TextView tvKcsl;
    @BindView(R.id.tv_ck)
    TextView tvCk;
    @BindView(R.id.tv_kw)
    TextView tvKw;
    @BindView(R.id.tv_pc)
    TextView tvPc;
    @BindView(R.id.et_spsl)
    EditText etSpsl;//=>写入库存数量
    @BindView(R.id.et_cysl)
    TextView etCysl;//==>计算值 禁止修改
    @BindView(R.id.et_tzsl)
    EditText etTzsl;//==>取值差异数量,可以修改
    private ArrayList<String> list;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_test_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra("list");
        position = intent.getIntExtra("position", 0);
        //[0]清单编号//[1]序号//[2]物料编号//[3]物料名称
        //[4]物料规格//[5]仓库//[6]库位//[7]批次//[8]库存数量//[9]实盘数量//[10]差异数量
        //[11]调整数量//[12]创建者//[13]盘点者//[14]调整者//[15]创建时间//[16]盘点时间
        //[17]盘点状态//[18]盘点设备//[19]盘点调整状态//[20] 备用字段1
        //[22]备用字段2//[25]备用字段3//[26]备用字段4//[27]备用字段5//[28]备用字段6
        //[29]备用字段7//[30]备用字段8//[31]备用字段9//[32]备用字段10
        tvQdbh.setText(String.valueOf(list.get(0)));
        tvXh.setText(String.valueOf(list.get(1)));
        tvWlbh.setText(String.valueOf(list.get(2)));
        tvWlmc.setText(String.valueOf(list.get(3)));
        tvWlgg.setText(String.valueOf(list.get(4)));
        tvKcsl.setText(String.valueOf(list.get(8)));
        tvCk.setText(String.valueOf(list.get(5)));
        tvKw.setText(String.valueOf(list.get(6)));
        tvPc.setText(String.valueOf(list.get(7)));
        etSpsl.setText(TextUtils.isEmpty(String.valueOf(list.get(9))) ? String.valueOf(list.get(8)) : String.valueOf(list.get(9)));
        try {
            double spsl = Double.parseDouble(String.valueOf(etSpsl.getText().toString()));
            double kcsl = Double.parseDouble(String.valueOf(tvKcsl.getText().toString()));
            double s = spsl - kcsl;
            etCysl.setText(String.valueOf(s).replace("-", ""));
            etTzsl.setText(String.valueOf(s).replace("-", ""));
        } catch (NumberFormatException e) {
            etCysl.setText("0");
            etTzsl.setText("0");
        }

        etSpsl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    try {
                        double spsl = Double.parseDouble(String.valueOf(etSpsl.getText().toString()));
                        double kcsl = Double.parseDouble(String.valueOf(tvKcsl.getText().toString()));
                        double s = spsl - kcsl;
                        etCysl.setText(String.valueOf(s).replace("-", ""));
                        etTzsl.setText(String.valueOf(s).replace("-", ""));
                    } catch (NumberFormatException e) {
                        etCysl.setText("0");
                    }
                }
            }
        });

    }

    @OnClick({R.id.left, R.id.btn_save, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
                onBackPressed();
                break;
            case R.id.btn_save:
                if (TextUtils.isEmpty(etSpsl.getText().toString())) {
                    ToastUtils.showToast("请输入实盘数量!");
                    return;
                } else if (TextUtils.isEmpty(etCysl.getText().toString())) {
                    ToastUtils.showToast("请输入差异数量!");
                    return;
                } else if (TextUtils.isEmpty(etTzsl.getText().toString())) {
                    ToastUtils.showToast("请输入调整数量!");
                    return;
                }
                Intent intent = new Intent();
                list.set(9, etSpsl.getText().toString());
                list.set(10, etCysl.getText().toString());
                list.set(11, etTzsl.getText().toString());
                intent.putStringArrayListExtra("list", list);
                intent.putExtra("position", position);
                setResult(-1, intent);
                onBackPressed();
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
