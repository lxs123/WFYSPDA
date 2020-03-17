package com.cubertech.bhpda.Activity.wfys.db;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.data.DbKjdb;
import com.cubertech.bhpda.data.DbPickItem;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.text.BreakIterator;
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
        DividerItemDecoration decoration = new DividerItemDecoration(KjdbActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mKjdbList.addItemDecoration(decoration);
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
                    //0单别 1 单号 2序号 3品号 4品名 5规格 6数量 7 转出仓库 8转入仓库  9 部门编号 10部门名称 11是否启用条码
                    //12管理层级   13批号   14 单位  15库位 16 状态
                    DbKjdb dbZszf = new DbKjdb(tm,
                            objectList1.get(0).toString()
                            , objectList1.get(1).toString()
                            , objectList1.get(2).toString()
                            , objectList1.get(3).toString()
                            , objectList1.get(4).toString()
                            , objectList1.get(6).toString()
                            , ""
                            , objectList1.get(7).toString()
                            , objectList1.get(11).toString()
                            , objectList1.get(12).toString()
                            , ""
                            , objectList1.get(8).toString()
                            , objectList1.get(13).toString()
                            , objectList1.get(9).toString()
                            , objectList1.get(10).toString()
                            , objectList1.get(5).toString()
                            , objectList1.get(14).toString()
                            , objectList1.get(15).toString()
                            , "0"//默认0,
                            , "0"//0
                            , "0"//0
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
                    List<List<Object>> list3 = new ArrayList<>();
//                    List<Object> list = null;
                    int i = 0;
                    for (DbKjdb kjdb : dbZszfList) {
//                        list = new ArrayList<>();
                        List<Object> list2 = new ArrayList<>();
//                        list.add(tvQybh.getText().toString());
//                        list.add(tvYyjd.getText().toString());
                        list2.add(kjdb.getDB());//0d单别
                        list2.add(kjdb.getDH());//1单号
                        list2.add(kjdb.getBMBH());//2部门编号
//                        list.add("Y");
                        list2.add(kjdb.getXH());//序号3
//                        list2.add(kjdb.getLJBH());//物料编号
//                        list2.add(kjdb.getBCSL());//拨出数量
                        list2.add(kjdb.getBCCW());//拨出仓库4
                        list2.add(kjdb.getBRCW());//拨入仓库5
                        list2.add(kjdb.getPH());//品号6
                        list2.add(kjdb.getSL());//数量7
                        list2.add("");//8
//                        list2.add(kjdb.getBRPH());//#拨入批号默认为空
                        list1.add(list2);

                        mDbRepository.getDbKjdbListItem(tm + kjdb.getPH() + i, new DbDataSource.GetDbPickItemCallback() {
                            @Override
                            public void onDbPickListItemLoaded(List<DbPickItem> dbPickList) {
                                for (DbPickItem dbPickItem : dbPickList) {
                                    if (TextUtils.equals(dbPickItem.getSTATE(), "1")) {
                                        List<Object> objList = new ArrayList<>();
                                        objList.add(dbPickItem.getTIME());//移动类型[0]llxx[0]  入库为1出库-1
                                        objList.add(tm);//[1]
                                        objList.add(kjdb.getXH());//[2]xc
                                        objList.add(dbPickItem.getPH());//[3] ph
                                        objList.add(dbPickItem.getPM());//[4]品名
                                        objList.add(dbPickItem.getGG());//[5]规格
                                        objList.add(dbPickItem.getDW());//6单位
                                        objList.add(dbPickItem.getFLSL());//7发料数量
                                        objList.add(dbPickItem.getCK());//8仓库
                                        objList.add(dbPickItem.getKW());//9库位
                                        objList.add(dbPickItem.getPC());//10批次
                                        SharedPreferences sp = KjdbActivity.this.getSharedPreferences(
                                                "config", Activity.MODE_PRIVATE);

                                        String name = sp.getString("name", null);
                                        objList.add(name);//11操作人员
                                        objList.add("PDA");//12
                                        list3.add(objList);
                                    }
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        i++;
                    }


                    params.put("kjdb1", list1);
                    params.put("kjdb2", list3);
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
        Intent intent = data;
        String bcsl = intent.getStringExtra("bcsl");
        String brsl = intent.getStringExtra("brsl");
        int position = intent.getIntExtra("position", 0);
        switch (requestCode) {
            case toDetail:

//                String dbsl = intent.getStringExtra("dbsl");
                String bckw = intent.getStringExtra("bckw");
                String bcph = intent.getStringExtra("bcph");
                String brkw = intent.getStringExtra("brkw");
                String brph = intent.getStringExtra("brph");

                mDbRepository.getDbKjdbList(tm, new DbDataSource.GetDbKjdbCallback() {
                    @Override
                    public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
                        for (int i = 0; i < ListUtils.getSize(dbZszfList); i++) {
                            if (position == i) {
                                DbKjdb kjdb = dbZszfList.get(position);
                                DbKjdb kjdb1 = new DbKjdb(kjdb.getKJDBD(),
                                        kjdb.getDB(),
                                        kjdb.getDH(),
                                        kjdb.getXH(),
                                        kjdb.getPH(),
                                        kjdb.getPM(),
                                        kjdb.getSL().substring(0, kjdb.getSL().indexOf(",")),
                                        bcph,
                                        bckw,
                                        kjdb.getISTM(),
                                        kjdb.getGLCJ(),
                                        brph,
                                        brkw,
                                        kjdb.getPIH(),
                                        kjdb.getBMBH(),
                                        kjdb.getBMMC(),
                                        kjdb.getGG(),
                                        kjdb.getDW(),
                                        kjdb.getKW(),
                                        bcsl,
                                        brsl,
                                        "1");
                                Log.e("######", kjdb1.toString());
                                dbZszfList.set(position, kjdb1);
                            } else {
                                DbKjdb kjdb = dbZszfList.get(i);
                                DbKjdb kjdb1 = new DbKjdb(kjdb.getKJDBD(),
                                        kjdb.getDB(),
                                        kjdb.getDH(),
                                        kjdb.getXH(),
                                        kjdb.getPH(),
                                        kjdb.getPM(),
                                        kjdb.getSL(),
                                        kjdb.getBCKW(),
                                        kjdb.getBCCW(),
                                        kjdb.getPIH(),
                                        kjdb.getBMBH(),
                                        kjdb.getBRKW(),
                                        kjdb.getBRCW(),
                                        kjdb.getPIH(),
                                        kjdb.getBMBH(),
                                        kjdb.getBMMC(),
                                        kjdb.getGG(),
                                        kjdb.getDW(),
                                        kjdb.getKW(),
                                        kjdb.getBCSL(),
                                        kjdb.getBRSL(),
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
            case 22: //弃用
//                mDbRepository.getDbKjdbList(tm, new DbDataSource.GetDbKjdbCallback() {
//                    @Override
//                    public void onDbKjdbListLoaded(List<DbKjdb> dbZszfList) {
//                        for (int i = 0; i < ListUtils.getSize(dbZszfList); i++) {
//                            if (position == i) {
//                                DbKjdb kjdb = dbZszfList.get(position);
//                                DbKjdb kjdb1 = new DbKjdb(kjdb.getKJDBD(),
//                                        kjdb.getDB(),
//                                        kjdb.getDH(),
//                                        kjdb.getXH(),
//                                        kjdb.getPH(),
//                                        kjdb.getPM(),
//                                        kjdb.getSL(),
//                                        kjdb.getBCKW(),
//                                        kjdb.getBCCW(),
//                                        kjdb.getPIH(),
//                                        kjdb.getBMBH(),
//                                        kjdb.getBRKW(),
//                                        kjdb.getBRCW(),
//                                        kjdb.getPIH(),
//                                        kjdb.getBMBH(),
//                                        kjdb.getBMMC(),
//                                        kjdb.getGG(),
//                                        kjdb.getDW(),
//                                        kjdb.getKW(),
//                                        bcsl,
//                                        brsl,
//                                        "1");
//                                Log.e("######", kjdb1.toString());
//                                dbZszfList.set(position, kjdb1);
//                            } else {
//                                DbKjdb kjdb = dbZszfList.get(i);
//                                DbKjdb kjdb1 = new DbKjdb(kjdb.getKJDBD(),
//                                        kjdb.getDB(),
//                                        kjdb.getDH(),
//                                        kjdb.getXH(),
//                                        kjdb.getPH(),
//                                        kjdb.getPM(),
//                                        kjdb.getSL(),
//                                        kjdb.getBCKW(),
//                                        kjdb.getBCCW(),
//                                        kjdb.getPIH(),
//                                        kjdb.getBMBH(),
//                                        kjdb.getBRKW(),
//                                        kjdb.getBRCW(),
//                                        kjdb.getPIH(),
//                                        kjdb.getBMBH(),
//                                        kjdb.getBMMC(),
//                                        kjdb.getGG(),
//                                        kjdb.getDW(),
//                                        kjdb.getKW(),
//                                        kjdb.getBCSL(),
//                                        kjdb.getBRSL(),
//                                        kjdb.getSTATE());
//                                Log.e("######1", kjdb1.toString());
//                                dbZszfList.set(i, kjdb1);
//                            }
//                        }
//                        List<Object> list = DbKjdb.toListObject(dbZszfList);
//                        adapter.setList(list);
//                        mDbRepository.deleteDbKjdb(tm);
//                        mDbRepository.saveDbKjdb(dbZszfList);
//                    }
//
//                    @Override
//                    public void onDataNotAvailable() {
//
//                    }
//                });
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
            //0单别 1 单号 2序号 3品号 4品名 5规格 6数量 7 转出仓库 8转入仓库  9 部门编号 10部门名称 11是否启用条码
            //12管理层级   13批号   14 单位  15库位 16 拨出数量 17拨入数量  18状态
//            tvQybh.setText(objectList.get(0).toString());
//            tvYyjd.setText(objectList.get(1).toString());
            if (ListUtils.getSize(objectList) == 16) {
                objectList.add("0");//16
                objectList.add("0");//17
                objectList.add("0");//17
                holder.imageState.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox3_off));
            }
//            holder.tvPpsl.setText(String.valueOf(objectList.get(16)));
            holder.tvDh.setText(objectList.get(1).toString());
            holder.tvDbsl.setText(objectList.get(6).toString());
            holder.tvPc.setText(objectList.get(13).toString());
            holder.tvPh.setText(objectList.get(3).toString());
            holder.tvBcck.setText(objectList.get(7).toString());
            holder.tvBrck.setText(objectList.get(8).toString());
//            if (ListUtils.getSize(objectList) > 17) {
            if (TextUtils.equals(objectList.get(18).toString(), "1")) {
                holder.imageState.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox3_on));
            } else {
                holder.imageState.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox3_off));
            }
//            } else {
//                objectList.add("0");
//                holder.imageState.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox3_off));
//            }

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
                    intent.putExtra("ppsl", holder.tvPpsl.getText().toString());
                    startActivityForResult(intent, toDetail);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(KjdbActivity.this, KjdbKcActivity.class);
                    intent.putExtra("data", objectList.toString());
                    intent.putExtra("id", tm + String.valueOf(objectList.get(3)) + position);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 22);
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
            TextView tvDh;
            @BindView(R.id.tv_dbsl)
            TextView tvDbsl;
            @BindView(R.id.tv_xc)
            TextView tvPc;
            @BindView(R.id.tv_ph)
            TextView tvPh;
            @BindView(R.id.tv_bcck)
            TextView tvBcck;
            @BindView(R.id.tv_brck)
            TextView tvBrck;
            @BindView(R.id.list_item_kjdb_content)
            LinearLayout linlayKjdbContent;
            @BindView(R.id.image_state)
            ImageView imageState;
            @BindView(R.id.btn_add)
            Button btnAdd;
            @BindView(R.id.tv_ppsl)
            TextView tvPpsl;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
