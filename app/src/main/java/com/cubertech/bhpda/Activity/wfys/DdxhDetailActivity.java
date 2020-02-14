package com.cubertech.bhpda.Activity.wfys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.data.DbDdxhItem;
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
 * Created by admin on 2019/12/30.
 */

public class DdxhDetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_ll_gddh)
    TextView tvLlGddh;
    @BindView(R.id.tv_ll_xmh)
    TextView tvLlXmh;
    @BindView(R.id.tv_ll_khbh)
    TextView tvLlKhbh;
    @BindView(R.id.ll_list)
    RecyclerView mList;
    private MyDdxhAdapter adapter;
    private ServiceUtils su = ServiceUtils.getInstance();
    @NonNull
    private final DbDataSource mDbRepository;
    private List list;
    private List<Object> toList;
    //    private SharedPreferences sharedPreferences;
    private String id;

    public DdxhDetailActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wf_ddxh_detail);
        ButterKnife.bind(this);
//        sharedPreferences = getSharedPreferences("dataset", 0);
        mList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(DdxhDetailActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(decoration);
        adapter = new MyDdxhAdapter();
        mList.setAdapter(adapter);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String data = intent.getStringExtra("data");
        //0出货通知单别  1出通单号 2客户编号  3客户名称 4项目编号 5项目名称
        toList = ListUtils.toList(data);
        tvLlGddh.setText(id);
        tvLlKhbh.setText(String.valueOf(toList.get(2)));
        tvLlXmh.setText(String.valueOf(toList.get(4)));
        mDbRepository.getDbDdxhItemList(id, new DbDataSource.GetDbDdxhItemCallback() {
            @Override
            public void onDbDdxhItemListLoaded(List<DbDdxhItem> dbDdxhItemList) {
                List<Object> objectList = DbDdxhItem.toObjectList(dbDdxhItemList);
                adapter.setList(objectList);
            }

            @Override
            public void onDataNotAvailable() {
                scanResult();
            }
        });
    }

    private void scanResult() {
        //todo 请填写  方法名
        String link = "fhwl";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", toList.get(0) + "-" + toList.get(1));
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(DdxhDetailActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;
                List<Object> objectList1 = new ArrayList<>();
                for (Object o1 : objectList) {
                    List<Object> objects = (List<Object>) o1;
                    objects.add("0");
                    objectList1.add(objects);
                }
                adapter.setList(objectList1);
//                SharedPreferences.Editor edit = sharedPreferences.edit();
//                edit.putString("ddxhdh", id);
//                edit.commit();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(DdxhDetailActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DdxhDetailActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    private void onUrlLoad() {
        List<Object> list = adapter.getList();
        List<Object> shjy = new ArrayList<>();
        //0序号 1品号 2品名 3规格 4仓库  5预计出货量  6项目编号  7项目名称8 提交单别 如果订单单别==2201
        // 则单别=2301.如果等于2701 则单别2801  如果 等于2205 则2305
        for (Object o : list) {
            List<Object> objectList = (List<Object>) o;
            List<Object> objectList1 = new ArrayList<>();
            switch (String.valueOf(objectList.get(8))) {
                case "2201":
                    objectList1.add("2301");//0
                    break;
                case "2701":
                    objectList1.add("2801");//0
                    break;
                case "2205":
                    objectList1.add("2305");//0
                    break;
            }

            objectList1.add(toList.get(2));//1客户编号
            objectList1.add(objectList.get(6));//xmh2
            objectList1.add(objectList.get(1));//3品号
            objectList1.add(objectList.get(9));//4出货数量
            String s = String.valueOf(objectList.get(4));
            String[] strings = s.contains("#") ? s.split("#") : (s + "#").split("#");
            objectList1.add(strings[0]);//ck5
            objectList1.add("********************");//ph5
            objectList1.add(toList.get(0));//6
            objectList1.add(toList.get(1));//7dh
            objectList1.add(objectList.get(0));//8
            objectList1.add("##########");//9
            objectList1.add("");//10
            shjy.add(objectList1);

        }
        //todo 请填写  方法名
        String link = "bcddxh";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", shjy);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(DdxhDetailActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                mDbRepository.deleteDbDdxhItem(id);
                ToastUtils.showToast("销货完成！");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 500);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(DdxhDetailActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DdxhDetailActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }


    @OnClick({R.id.left, R.id.btn_ll, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ll:
                onUrlLoad();
                break;
            case R.id.left:
//                break;
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

    public class MyDdxhAdapter extends RecyclerView.Adapter<MyDdxhAdapter.ViewHolder> {
        private List<Object> list = new ArrayList<>();

        public List<Object> getList() {
            return list;
        }

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
            if (ListUtils.isEmpty(list)) return;
            List<DbDdxhItem> dbDdxhItems = new ArrayList<>();
            for (Object o : list) {

                List<Object> objectList = (List<Object>) o;
                //0序号 1品号 2品名 3规格 4仓库  5预计出货量  6项目编号  7项目名称
                DbDdxhItem ddxhItem = new DbDdxhItem(id,
                        String.valueOf(objectList.get(0)),
                        String.valueOf(objectList.get(1)),
                        String.valueOf(objectList.get(2)),
                        String.valueOf(objectList.get(3)),
                        String.valueOf(objectList.get(4)),
                        String.valueOf(objectList.get(5)),
                        String.valueOf(objectList.get(9)),
                        String.valueOf(objectList.get(6)),
                        String.valueOf(objectList.get(7)),
                        String.valueOf(objectList.get(8)));
                dbDdxhItems.add(ddxhItem);
            }
            mDbRepository.deleteDbDdxhItem(id);
            mDbRepository.saveDbDdxhItem(dbDdxhItems);
        }

        public void setListNoHasNotifity(List<Object> list) {
            this.list = list;
            if (ListUtils.isEmpty(list)) return;
            List<DbDdxhItem> dbDdxhItems = new ArrayList<>();
            for (Object o : list) {

                List<Object> objectList = (List<Object>) o;
                //0序号 1品号 2品名 3规格 4仓库  5预计出货量  6项目编号  7项目名称
                DbDdxhItem ddxhItem = new DbDdxhItem(id,
                        String.valueOf(objectList.get(0)),
                        String.valueOf(objectList.get(1)),
                        String.valueOf(objectList.get(2)),
                        String.valueOf(objectList.get(3)),
                        String.valueOf(objectList.get(4)),
                        String.valueOf(objectList.get(5)),
                        String.valueOf(objectList.get(9)),
                        String.valueOf(objectList.get(6)),
                        String.valueOf(objectList.get(7)),
                        String.valueOf(objectList.get(8)));
                dbDdxhItems.add(ddxhItem);
            }
            mDbRepository.deleteDbDdxhItem(id);
            mDbRepository.saveDbDdxhItem(dbDdxhItems);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_wf_ddxh_detail, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //0序号 1品号 2品名 3规格 4仓库  5预计出货量  6项目编号  7项目名称8 to004 提交单别
            List<Object> objectList = (List<Object>) list.get(position);
            holder.tvXh.setText(String.valueOf(objectList.get(0)));
            holder.tvPh.setText(String.valueOf(objectList.get(1)));
            holder.tvPm.setText(String.valueOf(objectList.get(2)));
            holder.tvGg.setText(String.valueOf(objectList.get(3)));
            holder.tvCk.setText(String.valueOf(objectList.get(4)));
            holder.tvChsl.setText(String.valueOf(objectList.get(5)));
            holder.tvSl.setText(String.valueOf(objectList.get(9)));
            holder.tvSl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if ((i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_DONE ||
                            (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction()))) {
                        if (!TextUtils.isEmpty(holder.tvSl.getText().toString()) || !TextUtils.equals(holder.tvSl.getText().toString(), "0")) {
                            objectList.set(9, holder.tvSl.getText().toString());
                            list.set(position, objectList);
                            adapter.setList(list);
                        }
                    }
                    return false;
                }
            });
            holder.tvSl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(editable.toString()) || !TextUtils.equals(editable.toString(), "0")) {
                        objectList.set(9, editable.toString());
                        list.set(position, objectList);
                        adapter.setListNoHasNotifity(list);
                    }
                }
            });
//            holder.tvSl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean b) {
//                    if (!b) {
//                        if (!TextUtils.isEmpty(holder.tvSl.getText().toString()) || !TextUtils.equals(holder.tvSl.getText().toString(), "0")) {
//                            objectList.set(8, holder.tvSl.getText().toString());
//                            list.set(position, objectList);
//                            adapter.setList(list);
//                        }
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.list_bjrk_xh)
            TextView tvXh;
            @BindView(R.id.list_bjrk_ph)
            TextView tvPh;
            @BindView(R.id.list_bjrk_pm)
            TextView tvPm;
            @BindView(R.id.list_bjrk_gg)
            TextView tvGg;
            @BindView(R.id.list_bjrk_ck)
            TextView tvCk;
            @BindView(R.id.list_ddxh_chsl)
            TextView tvChsl;
            @BindView(R.id.list_bjrk_sl)
            EditText tvSl;
            @BindView(R.id.list_bjrk_sl_content)
            LinearLayout linlaySlContent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
