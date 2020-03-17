package com.cubertech.bhpda.Activity.wfys.db;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.data.DbPickItem;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/2/25.
 */

public class KjdbKcActivity extends AppCompatActivity {
    @BindView(R.id.tv_ll)
    TextView tvPh;
    @BindView(R.id.ll_list)
    RecyclerView mList;
    @BindView(R.id.tv_ll_name)
    TextView tvPm;
    @BindView(R.id.tv_ll_gg)
    TextView tvLlGg;
    @BindView(R.id.tv_ll_sqsl)
    TextView tvLlSqsl;
    @BindView(R.id.tv_ppsl)
    TextView tvPpsl;
    private MyKcAdapter adapter;
    ServiceUtils su = ServiceUtils.getInstance();
    private String lh;
    private String ck;
    private String pc;
    private String id = "";

    @NonNull
    private final DbDataSource mDbRepository;
    private SharedPreferences sharedPreferences;
    private int position;
    private String type = "调拨";
    //转移类型  拨出-1，拨入 1
    private String transferType = "";//替换时间transferType

    public KjdbKcActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_kc);
        ButterKnife.bind(this);
        mList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(decoration);
        adapter = new MyKcAdapter();
        mList.setAdapter(adapter);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        position = intent.getIntExtra("position", 0);
        id = intent.getStringExtra("id");
        String typeIntent = intent.getStringExtra("type");
        type = TextUtils.equals(typeIntent, "bc") ? "拨出" : "拨入";
        transferType = TextUtils.equals(typeIntent, "bc") ? "-1" : "1";

        //0单别 1 单号 2序号 3品号 4品名 5规格 6数量 7 转出仓库 8转入仓库  9 部门编号 10部门名称 11是否启用条码
        //12管理层级   13批号   14 单位  15库位 16 匹配数量 17状态
        List list = ListUtils.toList(data);
        //料号//[3] 品号
        lh = String.valueOf(list.get(3));
        //[9] 库位
        ck = TextUtils.equals(typeIntent, "bc") ? String.valueOf(list.get(7))
                : String.valueOf(list.get(8));
        ////[10] 指定批号
        pc = String.valueOf(list.get(13));
        tvPh.setText(lh);
        tvLlGg.setText(String.valueOf(list.get(5)));
        tvLlSqsl.setText(String.valueOf(list.get(6)));
        tvPm.setText(String.valueOf(list.get(4)));
        mDbRepository.getDbKjdbListItem(id, transferType, new DbDataSource.GetDbPickItemCallback() {
            @Override
            public void onDbPickListItemLoaded(List<DbPickItem> dbPickList) {
                List<Object> objects = DbPickItem.toListObject(dbPickList);
                adapter.setList(objects);
            }

            @Override
            public void onDataNotAvailable() {
                onUrlLoading();
            }
        });


    }

    private void onUrlLoading() {
        String link = "hqsskcxx";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("KC001", lh);//品号
        params.put("KC004", ck);//仓库
        params.put("KC005", "");//库位
        params.put("KC006", pc);//批号
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
//        params.put("strUserName", name);
//        params.put("strPassword", pwd);
//        params.put("data", data);
//        params.put("strToken", "");
//        params.put("strVersion", "");
//        params.put("strPoint", "");
//        params.put("strActionType", "1001");
        su.callService(this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;
                List<Object> objectList1 = new ArrayList<>();
                for (Object o1 : objectList) {
                    List<Object> objects = (List<Object>) o1;
                    objects.add("0");
                    objects.add("");
                    objectList1.add(objects);
                }
                adapter.setList(objectList1);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(KjdbKcActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(KjdbKcActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick({R.id.btn_ll, R.id.btn_qx, R.id.left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ll:
                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("ppsl", tvPpsl.getText().toString());
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
        }
    }

    public class MyKcAdapter extends RecyclerView.Adapter<MyKcAdapter.ViewHolder> {

        private List<Object> list = new ArrayList<>();
        private double flslAll = 0;

        public void setList(List<Object> list) {
            this.list = list;
            List<DbPickItem> dbKjdbList = new ArrayList<>();
            List<Object> objectList1 = this.list;
            flslAll = 0;
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
                        , transferType
                        , id
                        , objectList2.get(9).toString()
                        , objectList2.get(10).toString()
                );
                dbKjdbList.add(dbPick);
                if (TextUtils.equals(objectList2.get(9).toString(), "1")) {
                    flslAll += Double.parseDouble(objectList2.get(10).toString());
                }

            }
            mDbRepository.deleteDbKjdbItem(id);
            mDbRepository.saveDbKjdbItem(dbKjdbList);
            //tvPpsl.setText(String.valueOf(flslAll));
            tvPpsl.setText(String.valueOf(BigDecimal.valueOf(flslAll).toString()));
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_pick_kc, null, false);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            //[0]品号 //[1]品名//[2]规格//[3]仓库//[4]库位
            //[5]批次//[6]单位//[7]库存数量//[8]最后更新时间
            List<Object> objectList = (List<Object>) list.get(position);
            if (TextUtils.equals(objectList.get(9).toString(), "1")) {
                holder.imgSel.setImageDrawable(getResources().getDrawable(R.mipmap.ic_select));
            } else {
                holder.imgSel.setImageDrawable(getResources().getDrawable(R.mipmap.ic_unselect));
            }
            holder.tvCk.setText(String.valueOf(objectList.get(3)));
            holder.tvKw.setText(String.valueOf(objectList.get(4)));
            holder.tvPc.setText(String.valueOf(objectList.get(5)));
            holder.tvKcSl.setText(String.valueOf(objectList.get(7)));
            holder.tvFlsl.setText(String.valueOf(objectList.get(10)));
            holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(holder.tvFlsl.getText().toString()) || TextUtils.equals(holder.tvFlsl.getText().toString(), "0")) {
                        ToastUtils.showToast("请填写不为0的入库数量!");
                        return;
                    }
                    if (TextUtils.equals(objectList.get(9).toString(), "1")) {
                        objectList.set(9, "0");
                    } else {
                        objectList.set(9, "1");
                    }
                    list.set(position, objectList);
                    setList(list);

                }
            });
            holder.imgSel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(holder.tvFlsl.getText().toString()) || TextUtils.equals(holder.tvFlsl.getText().toString(), "0")) {
                        ToastUtils.showToast("请填写不为0的入库数量!");
                        return;
                    }
                    if (TextUtils.equals(objectList.get(9).toString(), "1")) {
                        objectList.set(9, "0");
                    } else {
                        objectList.set(9, "1");
                        objectList.set(10, holder.tvFlsl.getText().toString());
                        list.set(position, objectList);
                    }
                    list.set(position, objectList);
                    setList(list);
                }
            });
            holder.tvFlsl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                        if (!TextUtils.isEmpty(holder.tvFlsl.getText().toString())
                                && Double.parseDouble(holder.tvFlsl.getText().toString()) > 0) {
                            objectList.set(9, "1");
                            objectList.set(10, holder.tvFlsl.getText().toString());
                            list.set(position, objectList);
                            setList(list);
                        }
                    }
                    return false;
                }
            });
//            holder.linlayFlslClick.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MP_Theme_alertDialog);
//                    builder.setTitle("填写" + type + "数量");
//                    View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_now_pd_sl, null);
//                    TextView tvTitle = (TextView) view1.findViewById(R.id.dialog_spsl_title);
//                    tvTitle.setText(type + "数量");
//                    EditText etSl = (EditText) view1.findViewById(R.id.dialog_spsl);
//                    etSl.setHint("请填写" + type + "数量");
//                    etSl.setText(holder.tvFlsl.getText().toString());
//                    builder.setView(view1);
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            holder.tvFlsl.setText(etSl.getText().toString());
//                            if (TextUtils.equals(objectList.get(9).toString(), "1")) {
//                                objectList.set(10, holder.tvFlsl.getText().toString());
//                                list.set(position, objectList);
//                            }
//
//                        }
//                    });
//                    builder.setNegativeButton("取消", null);
//                    builder.show();
//                }
//            });
            holder.tvFlslTitle.setText(type + "数量");

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.list_kndb_img)
            ImageView imgSel;
            @BindView(R.id.list_kc_ck)
            TextView tvCk;
            @BindView(R.id.list_kc_kw)
            TextView tvKw;
            @BindView(R.id.list_kndb_pc)
            TextView tvPc;
            @BindView(R.id.list_kndb_sl)
            TextView tvKcSl;
            @BindView(R.id.list_kc_flsl)
            TextView tvFlsl;
            @BindView(R.id.list_pick_kc_content)
            LinearLayout linlayContent;
            @BindView(R.id.list_kc_flsl_click)
            LinearLayout linlayFlslClick;
            @BindView(R.id.list_kc_flsl_title)
            TextView tvFlslTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
