package com.cubertech.bhpda.Activity.wfys.db;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.data.DbKjdb;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/12.
 */

public class KjdbDetailActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    TextView etTm;
    @BindView(R.id.tv_mc)
    TextView tvMc;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.tv_dbsl)
    EditText tvDbsl;
    @BindView(R.id.et_bckw)
    EditText etBckw;
    @BindView(R.id.et_brkw)
    EditText etBrkw;
    @BindView(R.id.tv_bcph)
    EditText etBcph;
    @BindView(R.id.tv_brph)
    EditText etBrph;
    ServiceUtils su = ServiceUtils.getInstance();
    @BindView(R.id.left)
    ImageView left;
    @BindView(R.id.tv_wlbh)
    TextView tvWlbh;
    @BindView(R.id.tv_qybh)
    TextView tvQybh;
    @BindView(R.id.tv_jd)
    TextView tvJd;
    @BindView(R.id.tv_xc)
    TextView tvXc;
    private String tm;
    private int position;
    @NonNull
    private final DbDataSource mDbRepository;

    public KjdbDetailActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kjdb_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        ArrayList<String> data = intent.getStringArrayListExtra("data");
        position = intent.getIntExtra("position", 0);
        //0企业编号 1 据点 2挑拨单号 3项次 4料号 5名称 6规格 7 单位8 调拨数量 9 拨出库位 10拨入库位
        tvQybh.setText(data.get(0));
        tvJd.setText(data.get(1));
        etTm.setText(data.get(2));
        tvXc.setText(data.get(3));
        tvWlbh.setText(data.get(4));
        tvMc.setText(data.get(5));
        tvXhgg.setText(data.get(6));
        tvUnit.setText(data.get(7));
        etBckw.setText(data.get(9));
        etBrkw.setText(data.get(10));
        tvDbsl.setText(data.get(8));
        mDbRepository.getDbKjdbList(etTm.getText().toString(), new DbDataSource.GetDbKjdbCallback() {
            @Override
            public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
                if (!ListUtils.isEmpty(dbZszfList)) {
                    DbKjdb kjdb = dbZszfList.get(position);
                    etBckw.setText(kjdb.getBCKW());
                    etBcph.setText(kjdb.getBCPH());
                    etBrkw.setText(kjdb.getBRKW());
                    etBrph.setText(kjdb.getBRPH());
                }
            }

            @Override
            public void onDataNotAvailable() {

            }
        });


    }


    @OnClick({R.id.left, R.id.btn_ll, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
//                break;
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_ll:
                AlertDialog.Builder builder = new AlertDialog.Builder(KjdbDetailActivity.this, R.style.MP_Theme_alertDialog);
                builder.setTitle("提示");
                builder.setMessage("确定要保存当前数据么?");
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.putExtra("dbsl", tvDbsl.getText().toString());
                        intent.putExtra("bckw", etBckw.getText().toString());
                        intent.putExtra("bcph", etBcph.getText().toString());
                        intent.putExtra("brkw", etBrkw.getText().toString());
                        intent.putExtra("brph", etBrph.getText().toString());
                        intent.putExtra("position", position);
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                });
                builder.setNeutralButton("取消", null);
                builder.show();
                break;

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
