package com.cubertech.bhpda.Activity.wfys.db;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.data.DbKjdb;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/12.
 */

public class KjdbActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;

    ServiceUtils su = ServiceUtils.getInstance();
    @BindView(R.id.tv_qybh)
    TextView tvQybh;
    @BindView(R.id.kjdb_list)
    RecyclerView mKjdbList;
    @BindView(R.id.tv_yyjd)
    TextView tvYyjd;
    private String tm;
    private MyKjdbAdapter adapter;
    @NonNull
    private final DbDataSource mDbRepository;
    private SharedPreferences sharedPreferences;
    private final int toDetail = 12;
    private boolean isFinish;

    public KjdbActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kjdb);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("dataset", 0);
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
        mKjdbList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyKjdbAdapter();
        mKjdbList.setAdapter(adapter);
    }

    private void scanCode() {
        if (TextUtils.isEmpty(etTm.getText().toString())) {
            ToastUtils.showToast("请扫描库间调拨码!");
            return;
        }
        String kjdbd = sharedPreferences.getString("kjdbd", "");
        if (!TextUtils.isEmpty(kjdbd)) {
            mDbRepository.getDbKjdbList(kjdbd, new DbDataSource.GetDbKjdbCallback() {
                @Override
                public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
                    if (!ListUtils.isEmpty(dbZszfList)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(KjdbActivity.this, R.style.MP_Theme_alertDialog);
                        builder.setTitle("提示");
                        builder.setMessage("当前存在未提交的数据,是否继续!");
                        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                etTm.setText("");
                                etTm.setSelection(0);
                                etTm.requestFocus();
                                List<Object> list = DbKjdb.toListObject(dbZszfList);
                                adapter.setList(list);
                            }
                        });
                        builder.setNegativeButton("删除数据", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tm = kjdbd;
                                mDbRepository.deleteDbKjdb(kjdbd);
                                ToastUtils.showToast("删除成功!");
                                adapter.setList(null);
                                tvQybh.setText("");
                                tvYyjd.setText("");
                            }
                        });
                        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                        return;
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    onScanUrl();
                }
            });
            return;
        }
        onScanUrl();

    }

    private void onScanUrl() {
        String link = "hqkjdb";
        HashMap<String, Object> params = new HashMap<String, Object>();
        adapter.setList(null);
        params.put("inbbdocno", etTm.getText().toString());
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(KjdbActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                List<Object> objectList = (List<Object>) o;
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                Log.e("#######", o.toString());
                adapter.setList(objectList);

                List<DbKjdb> dbZszfList = new ArrayList<>();
                for (Object obj : objectList) {
                    List<Object> objectList1 = (List<Object>) obj;
                    DbKjdb dbZszf = new DbKjdb(tm,
                            objectList1.get(0).toString()
                            , objectList1.get(1).toString()
                            , "Y"
                            , objectList1.get(3).toString()
                            , objectList1.get(4).toString()
                            , ""
                            , objectList1.get(9).toString()
                            , ""
                            , ""
                            , ""
                            , objectList1.get(10).toString()
                            , ""
                            , ""
                            , objectList1.get(8).toString()
                            , objectList1.get(5).toString()
                            , objectList1.get(6).toString()
                            , objectList1.get(7).toString()
                            , "0"//默认0,
                    );
                    dbZszfList.add(dbZszf);
                }
                //保存 杂收杂发单号

                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("kjdbd", tm);
                edit.commit();
                mDbRepository.saveDbKjdb(dbZszfList);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(KjdbActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(KjdbActivity.this, error, Toast.LENGTH_LONG).show();
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
//                break;
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.del1:
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                break;
            case R.id.btn_ll:
                if (TextUtils.isEmpty(tm)) {
                    ToastUtils.showToast("请扫描库间调拨单号!");
                    return;
                }
                isFinish = false;
                mDbRepository.getDbKjdbList(tm, new DbDataSource.GetDbKjdbCallback() {
                    @Override
                    public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
                        if (!ListUtils.isEmpty(dbZszfList)) {
                            for (DbKjdb kjdb : dbZszfList) {
                                if (TextUtils.equals(kjdb.getSTATE(), "0")) {
                                    isFinish = true;
                                }
                            }
                        } else {
                            isFinish = false;
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        isFinish = false;
                    }
                });
                if (isFinish) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(KjdbActivity.this, R.style.MP_Theme_alertDialog);
                    builder.setTitle("提示");
                    builder.setMessage("当前存在未保存的数据,请保存后上传!");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                    return;
                }
                onUrl();
                break;

        }
    }

    private void onUrl() {
        String link = "bckjdb";
        HashMap<String, Object> params = new HashMap<String, Object>();
        mDbRepository.getDbKjdbList(tm, new DbDataSource.GetDbKjdbCallback() {
            @Override
            public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
                if (!ListUtils.isEmpty(dbZszfList)) {
                    List<List<Object>> list1 = new ArrayList<>();
                    List<Object> list = null;
                    for (DbKjdb kjdb : dbZszfList) {
                        list = new ArrayList<>();
                        List<Object> list2 = new ArrayList<>();
                        list.add(tvQybh.getText().toString());
                        list.add(tvYyjd.getText().toString());
                        list.add(etTm.getText().toString());
                        list.add("Y");
                        list2.add(kjdb.getXC());//项次
                        list2.add(kjdb.getLJBH());//物料编号
                        list2.add(kjdb.getBCSL());//拨出数量
                        list2.add(kjdb.getBCKW());//拨出库位
                        list2.add(kjdb.getBCCW());//拨出储位默认为空
                        list2.add(kjdb.getBCPH());//拨出批号默认为空
                        list2.add(kjdb.getBRSL());//拨入数量，等于拨出数量
                        list2.add(kjdb.getBRKW());//  #拨入库位
                        list2.add(kjdb.getBRCW());//#拨入储位默认为空
                        list2.add(kjdb.getBRPH());//#拨入批号默认为空
                        list1.add(list2);
                    }


                    params.put("kjdb", list);
                    params.put("kjdb2", list1);
                    params.put("strToken", "");
                    params.put("strVersion", Utils.getVersions(KjdbActivity.this));
                    params.put("strPoint", "");
                    params.put("strActionType", "1001");
                    su.callService(KjdbActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                        @Override
                        public void onResponse(Object o) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onBackPressed();
                                }
                            }, 500);
                            ToastUtils.showToast("保存成功!");
                        }

                        @Override
                        public void onFailure(String str) {
                            CommonUtil.showErrorDialog(KjdbActivity.this, str);
                            su.closeParentDialog();//必须
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(KjdbActivity.this, error, Toast.LENGTH_LONG).show();
                            su.closeParentDialog();//必须
                        }

                        @Override
                        public void onCompleted() {
                            su.closeParentDialog();//必须
                        }
                    });
                } else {
                    ToastUtils.showToast("数据异常!");
                }
            }

            @Override
            public void onDataNotAvailable() {
                ToastUtils.showToast("数据异常!");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) return;
        switch (requestCode) {
            case toDetail:
                Intent intent = data;
                String dbsl = intent.getStringExtra("dbsl");
                String bckw = intent.getStringExtra("bckw");
                String bcph = intent.getStringExtra("bcph");
                String brkw = intent.getStringExtra("brkw");
                String brph = intent.getStringExtra("brph");
                int position = intent.getIntExtra("position", 0);
                mDbRepository.getDbKjdbList(tm, new DbDataSource.GetDbKjdbCallback() {
                    @Override
                    public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
                        for (int i = 0; i < ListUtils.getSize(dbZszfList); i++) {
                            if (position == i) {
                                DbKjdb kjdb = dbZszfList.get(position);
                                DbKjdb kjdb1 = new DbKjdb(kjdb.getKJDBD(),
                                        kjdb.getQYBH(),
                                        kjdb.getYYJD(),
                                        kjdb.getZDY(),
                                        kjdb.getXC(),
                                        kjdb.getLJBH(),
                                        kjdb.getBCSL(),
                                        bckw,
                                        kjdb.getBCCW(),
                                        bcph,
                                        kjdb.getBRSL(),
                                        brkw,
                                        kjdb.getBRCW(),
                                        brph,
                                        dbsl,
                                        kjdb.getMC(),
                                        kjdb.getGG(),
                                        kjdb.getDW(),
                                        "1");
                                Log.e("######", kjdb1.toString());
                                dbZszfList.set(position, kjdb1);
                            } else {
                                DbKjdb kjdb = dbZszfList.get(i);
                                DbKjdb kjdb1 = new DbKjdb(kjdb.getKJDBD(),
                                        kjdb.getQYBH(),
                                        kjdb.getYYJD(),
                                        kjdb.getZDY(),
                                        kjdb.getXC(),
                                        kjdb.getLJBH(),
                                        kjdb.getBCSL(),
                                        kjdb.getBCKW(),
                                        kjdb.getBCCW(),
                                        kjdb.getBCPH(),
                                        kjdb.getBRSL(),
                                        kjdb.getBRKW(),
                                        kjdb.getBRCW(),
                                        kjdb.getBRPH(),
                                        kjdb.getDBSL(),
                                        kjdb.getMC(),
                                        kjdb.getGG(),
                                        kjdb.getDW(),
                                        kjdb.getSTATE());
                                Log.e("######1", kjdb1.toString());
                                dbZszfList.set(i, kjdb1);
                            }
                        }
                        List<Object> list = DbKjdb.toListObject(dbZszfList);
                        adapter.setList(list);
                        mDbRepository.deleteDbKjdb(tm);
                        mDbRepository.saveDbKjdb(dbZszfList);
                    }

                    @Override
                    public void onDataNotAvailable() {

                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public class MyKjdbAdapter extends RecyclerView.Adapter<MyKjdbAdapter.ViewHolder> {

        private List<Object> list = new ArrayList<>();

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<Object> getList() {
            return list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_kjdb_test, null, false);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Object> objectList = (List<Object>) list.get(position);
            //0企业编号 1 据点 2挑拨单号 3项次 4料号 5名称 6规格 7 单位8 调拨数量 9 拨出库位 10拨入库位
            tvQybh.setText(objectList.get(0).toString());
            tvYyjd.setText(objectList.get(1).toString());
            holder.tvWlbh.setText(objectList.get(4).toString());
            holder.tvDbsl.setText(objectList.get(8).toString());
            holder.tvXc.setText(objectList.get(3).toString());
            holder.tvBcck.setText(objectList.get(9).toString());
            holder.tvBrck.setText(objectList.get(10).toString());
            if (ListUtils.getSize(objectList) > 11) {
                if (TextUtils.equals(objectList.get(11).toString(), "1")) {
                    holder.imageState.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox3_on));
                } else {
                    holder.imageState.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox3_off));
                }
            } else {
                objectList.add("0");
                holder.imageState.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox3_off));
            }

            holder.linlayKjdbContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i = 0; i < ListUtils.getSize(objectList); i++) {
                        arrayList.add(String.valueOf(objectList.get(i)));
                    }
                    Intent intent = new Intent();
                    intent.setClass(KjdbActivity.this, KjdbDetailActivity.class);
                    intent.putExtra("data", arrayList);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, toDetail);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_wlbh)
            TextView tvWlbh;
            @BindView(R.id.tv_dbsl)
            TextView tvDbsl;
            @BindView(R.id.tv_xc)
            TextView tvXc;
            @BindView(R.id.tv_bcck)
            TextView tvBcck;
            @BindView(R.id.tv_brck)
            TextView tvBrck;
            @BindView(R.id.list_item_kjdb_content)
            LinearLayout linlayKjdbContent;
            @BindView(R.id.image_state)
            ImageView imageState;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
