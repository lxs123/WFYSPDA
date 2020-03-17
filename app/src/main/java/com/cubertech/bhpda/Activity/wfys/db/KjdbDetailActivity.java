package com.cubertech.bhpda.Activity.wfys.db;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cubertech.bhpda.R;
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
    @BindView(R.id.tv_ph)
    TextView tvPh;
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.tv_dw)
    TextView tvDw;
    @BindView(R.id.tv_glcj)
    TextView tvGlcj;
    @BindView(R.id.tv_kw)
    TextView tvKw;
    @BindView(R.id.tv_bmbh)
    TextView tvBmbh;
    @BindView(R.id.tv_bmmc)
    TextView tvBmmc;
    @BindView(R.id.tv_xh)
    TextView tvXh;
    @BindView(R.id.tv_sl)
    TextView tvSl;
    @BindView(R.id.tv_dbsl)
    EditText etDbsl;
    @BindView(R.id.et_bckw)
    TextView etBckw;
    @BindView(R.id.et_brkw)
    TextView etBrkw;
    @BindView(R.id.tv_bcsl)
    TextView tvBcsl;
    @BindView(R.id.tv_brsl)
    TextView tvBrsl;
    @BindView(R.id.tv_brph)
    EditText etBrph;
    @BindView(R.id.tv_bcph)
    EditText etBcph;
    private String tm;
    private int position;
    private boolean isEdit = false;
    @NonNull
    private final DbDataSource mDbRepository;
    private ArrayList<String> data;

    public KjdbDetailActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kjdb_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        data = intent.getStringArrayListExtra("data");
        position = intent.getIntExtra("position", 0);
        //0单别 1 单号 2序号 3品号 4品名 5规格 6数量 7 转出仓库 8转入仓库  9 部门编号 10部门名称 11是否启用条码
        //12管理层级   13批号   14 单位  15库位 16 拨出数量 17拨入数量  18状态
        tvPh.setText(data.get(3));
        tvPm.setText(data.get(4));
        etTm.setText(data.get(1));
        tvXhgg.setText(data.get(5));
        tvXh.setText(data.get(2));
        tvSl.setText(data.get(6).contains(",") ? (data.get(6).substring(0, data.get(6).indexOf(","))) : data.get(6));
        tvXhgg.setText(data.get(6));
        tvBmbh.setText(data.get(9));
        etBckw.setText(data.get(7));
        etBrkw.setText(data.get(8));
        tvBmmc.setText(data.get(10));
        tvGlcj.setText(data.get(12));
        tvDw.setText(data.get(14));
        tvKw.setText(data.get(15));
        tvBrsl.setText(data.get(17));
        tvBcsl.setText(data.get(16));

        mDbRepository.getDbKjdbList(etTm.getText().toString(), new DbDataSource.GetDbKjdbCallback() {
            @Override
            public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
                if (!ListUtils.isEmpty(dbZszfList)) {
                    DbKjdb kjdb = dbZszfList.get(position);
                    etBckw.setText(kjdb.getBCCW());
                    etBcph.setText(kjdb.getBCKW());
                    etBrkw.setText(kjdb.getBCCW());
                    etBrph.setText(kjdb.getBRKW());
                    tvBrsl.setText(kjdb.getBRSL());
                    tvBcsl.setText(kjdb.getBCSL());
//                    etDbsl.setText(kjdb.getSL().contains(",") ? (kjdb.getSL().substring(kjdb.getSL().indexOf(","), kjdb.getSL().length())) : "");
                }
            }

            @Override
            public void onDataNotAvailable() {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) return;
        Intent intent = data;
        String ppsl = intent.getStringExtra("ppsl");
        switch (requestCode) {
            case 22:
                tvBcsl.setText(ppsl);
                break;
            case 23:
                tvBrsl.setText(ppsl);
                break;
        }
        if (Double.parseDouble(tvBcsl.getText().toString()) != 0
                || Double.parseDouble(tvBrsl.getText().toString()) != 0) {
            isEdit = true;
        } else {
            isEdit = false;
        }
    }

    @OnClick({R.id.left, R.id.btn_ll, R.id.btn_qx, R.id.tv_brsl, R.id.tv_bcsl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
//                break;
            case R.id.btn_qx:
                if (isEdit) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(KjdbDetailActivity.this, R.style.MP_Theme_alertDialog);
                    builder1.setTitle("提示");
                    builder1.setMessage("当前数据未保存，是否保存后退出?");
                    builder1.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
//                        intent.putExtra("dbsl", etDbsl.getText().toString());
                            intent.putExtra("bckw", etBckw.getText().toString());
                            intent.putExtra("bcph", etBcph.getText().toString());
                            intent.putExtra("brkw", etBrkw.getText().toString());
                            intent.putExtra("brph", etBrph.getText().toString());
                            intent.putExtra("brsl", tvBrsl.getText().toString());
                            intent.putExtra("bcsl", tvBcsl.getText().toString());
                            intent.putExtra("position", position);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        }
                    });
                    builder1.setNeutralButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDbRepository.deleteDbKjdbItem(tm + String.valueOf(data.get(3)) + position);
                            onBackPressed();
                        }
                    });
                    builder1.show();
                }

                break;
            case R.id.tv_bcsl:
                Intent intent = new Intent(KjdbDetailActivity.this, KjdbKcActivity.class);
                intent.putExtra("data", data.toString());
                intent.putExtra("id", tm + String.valueOf(data.get(3)) + position);
                intent.putExtra("position", position);
                intent.putExtra("type", "bc");
                startActivityForResult(intent, 22);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_brsl:
                Intent intent1 = new Intent(KjdbDetailActivity.this, KjdbKcActivity.class);
                intent1.putExtra("data", data.toString());
                intent1.putExtra("id", tm + String.valueOf(data.get(3)) + position);
                intent1.putExtra("position", position);
                intent1.putExtra("type", "br");
                startActivityForResult(intent1, 23);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.btn_ll:
                AlertDialog.Builder builder = new AlertDialog.Builder(KjdbDetailActivity.this, R.style.MP_Theme_alertDialog);
                builder.setTitle("提示");
                builder.setMessage("确定要保存当前数据么?");
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
//                        intent.putExtra("dbsl", etDbsl.getText().toString());
                        intent.putExtra("bckw", etBckw.getText().toString());
                        intent.putExtra("bcph", etBcph.getText().toString());
                        intent.putExtra("brkw", etBrkw.getText().toString());
                        intent.putExtra("brph", etBrph.getText().toString());
                        intent.putExtra("brsl", tvBrsl.getText().toString());
                        intent.putExtra("bcsl", tvBcsl.getText().toString());
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
