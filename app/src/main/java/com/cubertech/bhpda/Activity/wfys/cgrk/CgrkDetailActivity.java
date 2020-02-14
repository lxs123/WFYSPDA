package com.cubertech.bhpda.Activity.wfys.cgrk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cubertech.bhpda.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/12.
 */

public class CgrkDetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_shdh)
    TextView tvShdh;
    @BindView(R.id.tv_xc)
    TextView tvXc;
    @BindView(R.id.tv_lh)
    TextView tvLh;
    @BindView(R.id.tv_wlname)
    TextView tvWlname;
    @BindView(R.id.tv_gysname)
    TextView tvGysname;
    @BindView(R.id.tv_gg)
    TextView tvGg;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.tv_dysl)
    TextView tvDysl;
    @BindView(R.id.et_bhgsl)
    EditText etBhgsl;
    @BindView(R.id.et_bhgyy)
    EditText etBhgyy;
    @BindView(R.id.et_hgsl)
    TextView tvPpsl;//入库数量
    private int position;
    private String id;
    private boolean isEditPPsl;
    private ArrayList<String> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cg_detail_zj);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        data = intent.getStringArrayListExtra("data");
        id = intent.getStringExtra("id");
        position = intent.getIntExtra("position", 0);
        tvShdh.setText(data.get(2));
        tvXc.setText(data.get(4));
        tvWlname.setText(data.get(6));
        tvLh.setText(data.get(5));
        tvGysname.setText(data.get(3));
        tvGg.setText(data.get(7));
        tvUnit.setText(data.get(8));
        tvDysl.setText(data.get(9));
        tvPpsl.setText(data.get(13));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) return;
        switch (requestCode) {
            case 22:
                int positon = data.getIntExtra("position", 0);
                String ppsl = data.getStringExtra("ppsl");
                if (TextUtils.equals(ppsl, tvPpsl.getText().toString())) {
                    isEditPPsl = false;
                } else {
                    isEditPPsl = true;
                    tvPpsl.setText(ppsl);
                }

                break;
            case 21:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.putExtra("ppsl", tvPpsl.getText().toString());
        intent.putExtra("isEdit", isEditPPsl);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick({R.id.left, R.id.btn_wcjy, R.id.btn_qx, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
//                break;
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_wcjy:
                break;
            case R.id.btn_add:
                Intent intent = new Intent(CgrkDetailActivity.this, CgrkAddActivity.class);
                intent.putExtra("data", data.toString());
                intent.putExtra("id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, 22);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

        }
    }
}
