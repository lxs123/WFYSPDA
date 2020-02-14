package com.cubertech.bhpda.Activity.wfys.cgrk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.data.DbPickItem;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 库存信息添加
 * Created by Administrator on 2019/12/20.
 */

public class CgrkAddActivity extends AppCompatActivity {
    @BindView(R.id.zszf_add_list)
    RecyclerView mAddList;
    private MyAddAdapter adapter;
    private String data;
    private List toList;
    private String id;
    private double flslAll = 0;
    private int position;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.tv_ll)
    TextView tvlh;
    @BindView(R.id.tv_ll_name)
    TextView tvName;
    @BindView(R.id.tv_ll_gg)
    TextView tvLlGg;
    @BindView(R.id.tv_ll_sqsl)
    TextView tvLlSqsl;
    @BindView(R.id.tv_ppsl)
    TextView tvPpsl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kcxx_add);
        ButterKnife.bind(this);
        mAddList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(CgrkAddActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mAddList.addItemDecoration(decoration);
        adapter = new MyAddAdapter();
        mAddList.setAdapter(adapter);
        Intent intent = getIntent();
        data = intent.getStringExtra("data");
        toList = ListUtils.toList(data);
        id = intent.getStringExtra("id");
        position = intent.getIntExtra("position", 0);
        mDbRepository.getDbCgrkListItem(id, new DbDataSource.GetDbPickItemCallback() {
            @Override
            public void onDbPickListItemLoaded(List<DbPickItem> dbPickList) {
                List<Object> list = DbPickItem.toListObject(dbPickList);
                adapter.setList(list);
            }

            @Override
            public void onDataNotAvailable() {
                btnAdd.performClick();
            }
        });
        //料号//[4] 料号
        String lh = String.valueOf(toList.get(4));
        //[9] 指定仓库
        String ck = String.valueOf(toList.get(9));
        ////[10] 指定批号
        String pc = String.valueOf(toList.get(10));
        tvlh.setText(lh);
        tvLlGg.setText(String.valueOf(toList.get(6)));
        tvLlSqsl.setText(String.valueOf(toList.get(8)));
        tvName.setText(String.valueOf(toList.get(5)));
    }

    @OnClick({R.id.left, R.id.btn_add, R.id.btn_qx, R.id.btn_wcjy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_wcjy:

                onBackPressed();
                break;
            case R.id.left:
            case R.id.btn_qx:
                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("ppsl", tvPpsl.getText().toString());
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.btn_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(CgrkAddActivity.this, R.style.MP_Theme_alertDialog);
                builder.setTitle("填写参数");
                View view1 = LayoutInflater.from(CgrkAddActivity.this).inflate(R.layout.dialog_kcxx_params, null);
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(calendar.YEAR);
                int month = calendar.get(calendar.MONTH) + 1;
                EditText etPC = (EditText) view1.findViewById(R.id.dialog_pc);
                EditText etKw = (EditText) view1.findViewById(R.id.dialog_kw);//dialog_sl
                EditText etSl = (EditText) view1.findViewById(R.id.dialog_sl);//dialog_sl
                etPC.setText(year + "" + ((month < 10) ? "0" + month : month));
                etPC.setSelection(etPC.getText().toString().length());
                etPC.requestFocus();
                builder.setPositiveButton("保存", null);
                builder.setNegativeButton("取消", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.setView(view1);
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (TextUtils.isEmpty(etPC.getText().toString())) {
                                    ToastUtils.showToast("请填写指定批次!");
                                    return;
                                } else if (TextUtils.isEmpty(etKw.getText().toString())) {
                                    ToastUtils.showToast("请填写指定库位!");
                                    return;//etSl
                                } else if (TextUtils.isEmpty(etSl.getText().toString())) {
                                    ToastUtils.showToast("请填写匹配数量!");
                                    return;//
                                }

                                List<DbPickItem> itemList = new ArrayList<>();
                                DbPickItem pickItem = new DbPickItem(
                                        toList.get(5).toString()//品号
                                        , toList.get(6).toString()//品名
                                        , toList.get(7).toString()//规格
                                        , ""//仓库
                                        , etKw.getText().toString()//库位
                                        , etPC.getText().toString()//批次
                                        , toList.get(8).toString()//单位
                                        , toList.get(9).toString()//库存数量
                                        , ""//时间
                                        , id
                                        , "1"
                                        , etSl.getText().toString());
                                itemList.add(pickItem);
                                List<Object> objectsList = DbPickItem.toListObject(itemList);
                                List<Object> list = adapter.getList();
                                list.add(objectsList.get(0));
                                adapter.setList(list);
                                mAddList.scrollToPosition(adapter.getItemCount() - 1);

//                                objList.set(10, etKw.getText().toString());
//                                objList.set(11, etSl.getText().toString());
//                                objList.set(12, etPC.getText().toString());
//                                list.set(i, objList);
//                                setList(list);
                                dialogInterface.cancel();
                            }
                        });
                    }
                });
                alertDialog.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @NonNull
    private final DbDataSource mDbRepository;

    public CgrkAddActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }

    public class MyAddAdapter extends RecyclerView.Adapter<MyAddAdapter.ViewHolder> {
        private List<Object> list = new ArrayList<>();


        public List<Object> getList() {
            return list;
        }

        public void setList(List<Object> list) {
            this.list = list;
            flslAll = 0;
            notifyDataSetChanged();
            mDbRepository.deleteDbCgrkItem(id);
            if (ListUtils.isEmpty(list)) return;

            List<DbPickItem> dbZszfList = new ArrayList<>();
            List<Object> objectList1 = this.list;
            for (Object obj : objectList1) {
                List<Object> objectList2 = (List<Object>) obj;
                DbPickItem dbPick = new DbPickItem(
                        objectList2.get(0).toString()
                        , objectList2.get(1).toString()
                        , objectList2.get(2).toString()
                        , objectList2.get(3).toString()
                        , objectList2.get(4).toString()
                        , objectList2.get(5).toString()
                        , objectList2.get(6).toString()
                        , objectList2.get(7).toString()
                        , objectList2.get(8).toString()
                        , id
                        , objectList2.get(9).toString()
                        , objectList2.get(10).toString()
                );
                dbZszfList.add(dbPick);
                if (TextUtils.equals(objectList2.get(9).toString(), "1")) {
                    flslAll += Double.parseDouble(objectList2.get(10).toString());
                }
            }
            tvPpsl.setText(String.valueOf(flslAll));

            mDbRepository.saveDbCgrkItem(dbZszfList);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_kcxx_params, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Object> objectList = (List<Object>) list.get(position);
            holder.tvKw.setEnabled(false);
            holder.tvPc.setEnabled(false);
            holder.tvSl.setEnabled(false);
            holder.tvKw.setText(String.valueOf(objectList.get(4)));
            holder.tvPc.setText(String.valueOf(objectList.get(5)));
            holder.tvSl.setText(String.valueOf(objectList.get(10)));
            holder.linlayDeleteContent.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    setList(list);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.dialog_pc)
            EditText tvPc;
            @BindView(R.id.dialog_kw)
            EditText tvKw;
            @BindView(R.id.dialog_sl)
            EditText tvSl;
            @BindView(R.id.dialog_delete)
            Button btnDelete;
            @BindView(R.id.dialog_delete_content)
            LinearLayout linlayDeleteContent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
