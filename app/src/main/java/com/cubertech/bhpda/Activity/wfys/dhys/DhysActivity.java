package com.cubertech.bhpda.Activity.wfys.dhys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.wfys.cgrk.CgrkActivity;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.data.DbCgtzd;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/12.
 */

public class DhysActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.dhys_recycle)
    RecyclerView mDhysList;
    @BindView(R.id.dhys_swipe)
    SwipeRefreshLayout mSwipe;
    @BindView(R.id.et_value)
    EditText etTm;
    ServiceUtils su = ServiceUtils.getInstance();
    private String tm;
    private MyDhysAdapter adapter;
    @NonNull
    private final DbDataSource mDbRepository;
    private SharedPreferences sharedPreferences;

    public DhysActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhys);
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
                    initDate();//
                }
                return false;
            }
        });
        mDhysList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyDhysAdapter();
        mDhysList.setAdapter(adapter);
        mSwipe.setRefreshing(false);
        mSwipe.setEnabled(false);
        mSwipe.setOnRefreshListener(this);
        initCode();
    }

    private void initDate() {
        if (TextUtils.isEmpty(etTm.getText().toString())) {
            ToastUtils.showToast("请扫描收货单号!");
            return;
        }
        String dhysd = sharedPreferences.getString("dhysd", "");
        if (!TextUtils.isEmpty(dhysd)) {
            mDbRepository.getDbDhysList(dhysd, new DbDataSource.GetDbCgtzdCallback() {
                @Override
                public void onDbCgtzdListLoaded(List<DbCgtzd> dbZszfList) {
                    if (!ListUtils.isEmpty(dbZszfList)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DhysActivity.this, R.style.MP_Theme_alertDialog);
                        builder.setTitle("提示");
                        builder.setMessage("当前存在未提交的数据,是否继续!");
                        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tm = dhysd;
                                etTm.setText("");
                                etTm.setSelection(0);
                                etTm.requestFocus();
                                List<Object> list = DbCgtzd.toListObject(dbZszfList);
                                adapter.setList(list);
                            }
                        });
                        builder.setNegativeButton("删除数据", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDbRepository.deleteDbDhys(dhysd);
                                ToastUtils.showToast("删除成功!");
                                adapter.setList(null);
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
                    scanCode();
                }
            });
            return;
        } else
            scanCode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(tm)) {

        }
    }

    /**
     * 初始化接口
     */
    private void initCode() {
        String link = "hqshjyqb";
        HashMap<String, Object> params = new HashMap<String, Object>();
        adapter.setList(null);
        params.put("pmdsdocno", "");
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(DhysActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = "all";
                List<Object> objectList = (List<Object>) o;

                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                Log.e("#######", objectList.toString());
                List<Object> objectList2 = new ArrayList<>();
                for (Object o1 : objectList) {
                    List<Object> objects = (List<Object>) o1;
                    objects.add("0");//10
                    objects.add("0");//11
                    objects.add("");//12
                    objects.add("0");//13
                    objectList2.add(objects);
                }
                adapter.setList(objectList2);
//                SharedPreferences.Editor edit = sharedPreferences.edit();
//                edit.putString("dhysd", tm);
//                edit.commit();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(DhysActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DhysActivity.this, error, Toast.LENGTH_LONG).show();
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

    private void scanCode() {
        String link = "hqshjy";
        HashMap<String, Object> params = new HashMap<String, Object>();
        adapter.setList(null);
        if (!etTm.getText().toString().contains("#")) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        String strTm = etTm.getText().toString();
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0])) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        etTm.setText(split[0]);

        params.put("pmdsdocno", etTm.getText().toString());
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(DhysActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                List<Object> objectList = (List<Object>) o;

                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                Log.e("#######", objectList.toString());
                List<Object> objectList2 = new ArrayList<>();
                for (Object o1 : objectList) {
                    List<Object> objects = (List<Object>) o1;
                    objects.add("0");//10
                    objects.add("0");//11
                    objects.add("");//12
                    objects.add("0");//13
                    objectList2.add(objects);
                }
                adapter.setList(objectList2);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("dhysd", tm);
                edit.commit();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(DhysActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DhysActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.btn_cancel, R.id.btn_submit, R.id.del1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.del1:
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                break;
            case R.id.left:
//                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_submit:
                List<Object> shjy1 = new ArrayList<>();
                List<Object> shjy2 = new ArrayList<>();
                mDbRepository.getDbDhysList(tm, new DbDataSource.GetDbCgtzdCallback() {
                    @Override
                    public void onDbCgtzdListLoaded(List<DbCgtzd> dbZszfList) {
                        Format format = new DecimalFormat("#0");
                        boolean isFinish = false;
                        for (DbCgtzd cgtzd : dbZszfList) {
                            List<Object> objectList = new ArrayList<>();
                            shjy1.add(format.format(Double.parseDouble(String.valueOf(cgtzd.getQYBH()))));
                            shjy1.add(cgtzd.getYYJD());
                            if (TextUtils.equals(cgtzd.getSTATE(), "0")) {
                                isFinish = true;
                            }
                            objectList.add(cgtzd.getCgdh());
                            objectList.add(cgtzd.getXC());
                            objectList.add(cgtzd.getHGSL());
                            objectList.add(cgtzd.getBHGSL());
                            objectList.add(cgtzd.getBHGYY());
                            shjy2.add(objectList);
                        }
                        if (isFinish) {
                            ToastUtils.showToast("当前存在未完成的数据!");
                            return;
                        }
                        String link = "bcshjy";
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("shjy1", shjy1);
                        params.put("shjy2", shjy2);
                        params.put("strToken", "");
                        params.put("strVersion", Utils.getVersions(DhysActivity.this));
                        params.put("strPoint", "");
                        params.put("strActionType", "1001");
                        su.callService(DhysActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                            @Override
                            public void onResponse(Object o) {
                                ToastUtils.showToast("上传成功!");
                                mDbRepository.deleteDbDhys(tm);

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
                                CommonUtil.showErrorDialog(DhysActivity.this, str);
                                su.closeParentDialog();//必须
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(DhysActivity.this, error, Toast.LENGTH_LONG).show();
                                su.closeParentDialog();//必须
                            }

                            @Override
                            public void onCompleted() {
                                su.closeParentDialog();//必须
                            }
                        });

                    }

                    @Override
                    public void onDataNotAvailable() {
                        ToastUtils.showToast("数据存在异常!");
                    }
                });

                break;
        }
    }

    @Override
    public void onBackPressed() {
        tm = sharedPreferences.getString("dhysd", "");
        if (TextUtils.isEmpty(tm)) {
            finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
        mDbRepository.getDbDhysList(tm, new DbDataSource.GetDbCgtzdCallback() {
            @Override
            public void onDbCgtzdListLoaded(List<DbCgtzd> dbZszfList) {
                mDbRepository.deleteDbDhys(tm);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                boolean isFinish = false;
//                for (DbCgtzd cgtzd : dbZszfList) {
//                    List<Object> objectList = new ArrayList<>();
//                    if (TextUtils.equals(cgtzd.getSTATE(), "0")) {
//                        isFinish = true;
//                    }
//                }
//                if (isFinish) {
//                    finish();
//                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                } else {
//
//                }

            }

            @Override
            public void onDataNotAvailable() {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });

    }

    @Override
    public void onRefresh() {
        scanCode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) return;
        switch (requestCode) {
            case 23:
                int position = data.getIntExtra("position", 0);
                String hgsl = data.getStringExtra("hgsl");
                String bhgsl = data.getStringExtra("bhgsl");
                String bhgyy = data.getStringExtra("bhgyy");
                List<Object> list = adapter.getList();
                for (int i = 0; i < ListUtils.getSize(list); i++) {
                    if (i == position) {
                        List<Object> objects = (List<Object>) list.get(i);
                        objects.set(10, hgsl);
                        objects.set(11, bhgsl);
                        objects.set(12, bhgyy);
                        objects.set(13, "1");
                        list.set(i, objects);
                        if (TextUtils.equals(tm, "all")) {
                            initCode();
                        } else {
                            etTm.setText(tm + "#");
                            scanCode();
                        }


                    } else {
                        List<Object> objects = (List<Object>) list.get(i);
                        list.set(i, objects);
                    }
                }
                adapter.setList(list);
                break;
        }
    }

    public class MyDhysAdapter extends RecyclerView.Adapter<MyDhysAdapter.ViewHolder> {
        List<Object> list = new ArrayList<>();


        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
            if (ListUtils.isEmpty(list)) {
                return;
            }
            List<Object> objectList2 = list;
            List<DbCgtzd> dbZszfList = new ArrayList<>();
            for (Object obj : objectList2) {
                List<Object> objectList1 = (List<Object>) obj;
                DbCgtzd dbPick = new DbCgtzd(tm,//采购单号扫描/出货单
                        objectList1.get(0).toString()//企业编号    ==>  //0供应商编号
                        , ""//营业据点    ==> //10品管类型//
                        , ""//采购单号    ==> //
                        , objectList1.get(1).toString()//采购运营商    ==>   //1采购供应商 名称
                        , ""//                      11 检验名称
                        , objectList1.get(2).toString()//项次    ==>//2单别
                        , objectList1.get(3).toString()//料件编号  ==>//3单号
                        , objectList1.get(4).toString()//库位    ==>//4品号
                        , objectList1.get(9).toString()//结果    ==>//9是否急料
                        , objectList1.get(8).toString()//采购数量  ==>////8待检数量
                        , objectList1.get(5).toString()//名称    ==>//5品名
                        , objectList1.get(7).toString()//规格    ==>//7规格
                        , objectList1.get(6).toString()//单位    ==>//6序号
                        , objectList1.get(10).toString()//合格数量 ==>//10合格数量
                        , objectList1.get(11).toString()//不合格数量==>//11不合格数量
                        , objectList1.get(12).toString()//不合格原因==>//12不良原因
                        , objectList1.get(13).toString()//状态   ==>//13状态
                );
                dbZszfList.add(dbPick);
            }
            mDbRepository.deleteDbDhys(tm);
            mDbRepository.saveDbDhys(dbZszfList);
        }

        public List<Object> getList() {
            return list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_dhys_adapter, null, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Object> objList = (List<Object>) list.get(position);
/**
 *  //0单别1单号2供应商编号3采购供应商4//序号 //5品号6名称7规格
 *  8//待验数量9判定结果 是否急料10合格数量11不合格数量12不良原因13状态
 */
            holder.tvShdh.setText(objList.get(1).toString());
            holder.tvDb.setText(objList.get(0).toString());
            holder.tvGg.setText(objList.get(7).toString());
            holder.tvWlname.setText(objList.get(6).toString());
            holder.tvGysname.setText(objList.get(3).toString());
            holder.tvDysl.setText(objList.get(8).toString());
            holder.falayJL.setVisibility(TextUtils.equals(String.valueOf(objList.get(9)), "Y") ? View.VISIBLE : View.GONE);
            holder.imageSel.setImageDrawable(TextUtils.equals(objList.get(13).toString(), "1")
                    ? getResources().getDrawable(R.mipmap.checkbox3_on) : getResources().getDrawable(R.mipmap.checkbox3_off));
            holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i = 0; i < ListUtils.getSize(objList); i++) {
                        arrayList.add(objList.get(i).toString());
                    }
                    Intent intent = new Intent();
                    intent.putExtra("data", arrayList);
                    intent.putExtra("position", position);
                    intent.setClass(DhysActivity.this, DhysDetailActivity.class);
                    startActivityForResult(intent, 23);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_shdh)
            TextView tvShdh;
            @BindView(R.id.tv_xc)
            TextView tvDb;
            @BindView(R.id.tv_gg)
            TextView tvGg;
            @BindView(R.id.tmsm)
            LinearLayout tmsm;
            @BindView(R.id.tv_wlname)
            TextView tvWlname;
            @BindView(R.id.tv_gysname)
            TextView tvGysname;
            @BindView(R.id.tv_dysl)
            TextView tvDysl;
            @BindView(R.id.listitem_content)
            LinearLayout linlayContent;
            @BindView(R.id.listitem_xhcz)
            FrameLayout falayDhys;
            @BindView(R.id.image_state)
            ImageView imageSel;
            @BindView(R.id.listitem_success)
            FrameLayout falayJL;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
