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
        //[0]企业编号//[1]据点//[2]清单编号//[3]序号//[4]物料编号//[5]物料名称
        //[6]物料规格//[7]仓库//[8]库位//[9]批次//[10]库存数量//[11]实盘数量//[12]差异数量
        //[13]调整数量//[14]创建者//[15]盘点者//[16]调整者//[17]创建时间//[18]盘点时间
        //[19]调整时间//[20]盘点状态//[21]盘点设备//[22]盘点调整状态//[23] 备用字段1
        //[24]备用字段2//[25]备用字段3//[26]备用字段4//[27]备用字段5//[28]备用字段6
        //[29]备用字段7//[30]备用字段8//[31]备用字段9//[32]备用字段10
        tvQdbh.setText(String.valueOf(list.get(2)));
        tvXh.setText(String.valueOf(list.get(3)));
        tvWlbh.setText(String.valueOf(list.get(4)));
        tvWlmc.setText(String.valueOf(list.get(5)));
        tvWlgg.setText(String.valueOf(list.get(6)));
        tvQybh.setText(String.valueOf(list.get(0)));
        tvJd.setText(String.valueOf(list.get(1)));
        tvKcsl.setText(String.valueOf(list.get(10)));
        tvCk.setText(String.valueOf(list.get(7)));
        tvKw.setText(String.valueOf(list.get(8)));
        tvPc.setText(String.valueOf(list.get(9)));
        etSpsl.setText(TextUtils.isEmpty(String.valueOf(list.get(11))) ? String.valueOf(list.get(10)) : String.valueOf(list.get(11)));
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
                list.set(11, etSpsl.getText().toString());
                list.set(12, etCysl.getText().toString());
                list.set(13, etTzsl.getText().toString());
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

    /**
     * @see Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
