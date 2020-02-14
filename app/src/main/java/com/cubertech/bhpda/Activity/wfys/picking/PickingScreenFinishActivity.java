package com.cubertech.bhpda.Activity.wfys.picking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 工单领料
 * Created by admin on 2019/12/27.
 */

public class PickingScreenFinishActivity extends AppCompatActivity {

    @BindView(R.id.ll_list)
    RecyclerView mList;
    @BindView(R.id.et_gddh)
    EditText etGddh;
    @BindView(R.id.tv_ll_gddh)
    TextView tvLlGddh;
    @BindView(R.id.tv_ll_gzzx)
    TextView tvLlGzzx;
    @BindView(R.id.tv_ll_xmh)
    TextView tvLlXmh;
    @BindView(R.id.tv_ll_bm)
    TextView etLlBm;
    private ServiceUtils su = ServiceUtils.getInstance();
    private MyPickAdapter adapter;
    private String tm;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_screen_finish);
        ButterKnife.bind(this);
        etGddh.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        mList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(PickingScreenFinishActivity.this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(dividerItemDecoration);
        adapter = new MyPickAdapter();
        mList.setAdapter(adapter);
    }

    //扫码请求
    private void scanCode() {
        String strTm = etGddh.getText().toString();
        if (!strTm.contains("#")) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0])) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        etGddh.setText(split[0]);
        SharedPreferences sp = PickingScreenFinishActivity.this.getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        name = sp.getString("name", null);
        //todo 请填写  方法名
        String link = "hqlld";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("sfdadocno", split[0]);
        params.put("username", name);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(PickingScreenFinishActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = split[0];
                List<Object> objectList = (List<Object>) o;
                Log.e("#####", objectList.toString());
                adapter.setList(objectList);
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(PickingScreenFinishActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PickingScreenFinishActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    //上传请求
    private void onUrlLoad() {
//        假数据
//        if (ListUtils.isEmpty(adapter.getList())) {
//            List<Object> objects = new ArrayList<>();
//            for (int i = 0; i < 10; i++) {
//                List<Object> objectList = new ArrayList<>();
//                objectList.add("0工单单别");
//                objectList.add("1工单单号");
//                objectList.add("2品号 ");
//                objectList.add("3品名");
//                objectList.add("4规格");
//                objectList.add("5仓库");
//                objectList.add("6");
//                objectList.add("7工作中心");
//                objectList.add("8项目号");
//                objects.add(objectList);
//            }
//            adapter.setList(objects);
//
//        }
//
//        tm = "12";
        if (TextUtils.isEmpty(tm)) {
            ToastUtils.showToast("请扫描领料单号！");
            return;
        }
        List<Object> lld = new ArrayList<>();
        int count = 1;
        //0工单单别 1工单单号  2品号  3品名  4规格  5仓库  6需领数量  7工作中心  8项目号9部门10，单位 11批次
        for (Object o : adapter.getList()) {
            List<Object> objectList1 = (List<Object>) o;
            if (TextUtils.equals(String.valueOf(objectList1.get(14)), "1")) {
                if (Double.parseDouble(TextUtils.isEmpty(String.valueOf(objectList1.get(12))) ? "0"
                        : String.valueOf(objectList1.get(12))) == 0 ||
                        TextUtils.isEmpty(String.valueOf(objectList1.get(13)))) {
                    ToastUtils.showToast("选中领料单后，请完善领料数量，库位！");
                    return;
                }
            }
            if (ListUtils.getSize(objectList1) == 15 && !TextUtils.isEmpty(String.valueOf(objectList1.get(12)))
                    && Double.parseDouble(String.valueOf(objectList1.get(12))) > 0 &&
                    !TextUtils.isEmpty(String.valueOf(objectList1.get(13))) &&
                    TextUtils.equals(String.valueOf(objectList1.get(14)), "1")) {
                List<Object> objectList = new ArrayList<>();

                objectList.add("5401");//0
                objectList.add("11");//1
                objectList.add(objectList1.get(7));//工作中心2
                objectList.add("");//3
                objectList.add("54");//4
                objectList.add(etLlBm.getText().toString());//5
                objectList.add(name);//登录账号6
                objectList.add(objectList1.get(8));//项目号7
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 4 - String.valueOf(count).length(); i++) {
                    builder.append("0");
                }
                builder.append(count);
                count++;
                objectList.add(builder.toString());//序号8
                objectList.add(objectList1.get(2));//品号9
                String sl = (ListUtils.getSize(objectList1) >= 13) ? String.valueOf(objectList1.get(12)) : "0";
                objectList.add(sl);//数量10
                objectList.add(objectList1.get(5));//仓库11
                objectList.add("****");//12
                objectList.add("********************");//13
                objectList.add(objectList1.get(0));//单别14
                objectList.add(objectList1.get(1));//单号15
                objectList.add("");//备注16
                objectList.add("1");//17
                objectList.add("##########");//18
                objectList.add(objectList1.get(3));//19pm
                objectList.add(objectList1.get(4));//20gg
                objectList.add(objectList1.get(10));//21dw
                objectList.add(objectList1.get(13));//22kw
                objectList.add(objectList1.get(11));//23pc

                lld.add(objectList);
            }
        }
        if (ListUtils.isEmpty(lld)) {
            ToastUtils.showToast("请选择领料单，选中领料单后，请完善领料数量，库位！");
            return;
        }
        //todo 请填写  方法名
        String link = "bclld";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", lld);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(PickingScreenFinishActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                ToastUtils.showToast("领料成功！");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 500);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(PickingScreenFinishActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PickingScreenFinishActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.btn_ll, R.id.btn_qx, R.id.del1d})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.del1d:
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
                break;

            case R.id.btn_ll:
                onUrlLoad();
                break;
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
        }
    }

    public class MyPickAdapter extends RecyclerView.Adapter<MyPickAdapter.ViewHolder> {
        List<Object> list = new ArrayList<>();


        public List<Object> getList() {
            return list;
        }

        public void setList(List<Object> list) {
            this.list = list;
            Log.e("######", list.toString());
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_picking_screen_finish, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            //0工单单别 1工单单号  2品号  3品名  4规格  5仓库  6需领数量  7工作中心  8项目号9部门10，单位 11批次
            List<Object> objectList = (List<Object>) list.get(position);
            if (ListUtils.getSize(objectList) < 15) {
                objectList.add("");
                objectList.add("");
                objectList.add("0");

            }
            tvLlGddh.setText(String.valueOf(objectList.get(1)));
            tvLlGzzx.setText(String.valueOf(objectList.get(7)));
            tvLlXmh.setText(String.valueOf(objectList.get(8)));
            holder.tvPh.setText(String.valueOf(objectList.get(2)));
            holder.tvPm.setText(String.valueOf(objectList.get(3)));
            holder.tvGg.setText(String.valueOf(objectList.get(4)));
            holder.tvCk.setText(String.valueOf(objectList.get(5)));
            holder.tvXlsl.setText(String.valueOf(objectList.get(6)));
            etLlBm.setText(String.valueOf(objectList.get(9)));
            if (ListUtils.getSize(objectList) >= 13) {
                holder.tvLlsl.setText(String.valueOf(objectList.get(12)));
            } else {
                holder.tvLlsl.setHint("请填写");
            }
            if (ListUtils.getSize(objectList) > 14) {
                holder.etKw.setText(String.valueOf(objectList.get(13)));
            }
//            if (ListUtils.getSize(objectList) == 15) {
            holder.imgSel.setImageDrawable(getResources().getDrawable(
                    TextUtils.equals(String.valueOf(objectList.get(14)), "0") ? R.mipmap.ic_unselect : R.mipmap.ic_select));
//            } else {
//                holder.imgSel.setImageDrawable(getResources().getDrawable(
//                        R.mipmap.ic_unselect));
//            }
            holder.imgSel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (ListUtils.getSize(objectList) == 15) {
                    objectList.set(14, TextUtils.equals(String.valueOf(objectList.get(14)), "0") ? "1" : "0");
//                    } else if (ListUtils.getSize(objectList) == 12) {
//                        objectList.add("");//sl
//                        objectList.add("");//kw
//                        objectList.add("1");//sel
//                    } else if (ListUtils.getSize(objectList) == 13) {
//                        objectList.add("");//kw
//                        objectList.add("1");//sel
//                    } else {
//                        objectList.add("1");//sel
//                    }
                    list.set(position, objectList);
                    notifyDataSetChanged();
                }
            });
            holder.etKw.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (ListUtils.getSize(objectList) == 13) {
                        objectList.add(editable.toString());
                    } else if (ListUtils.getSize(objectList) == 12) {
                        objectList.add("");
                        objectList.add(editable.toString());
                    } else {
                        objectList.set(13, editable.toString());
                    }
                }
            });
            holder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.etKw.setText("");
                }
            });
            holder.tvLlsl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickingScreenFinishActivity.this, R.style.MP_Theme_alertDialog);
                    builder.setTitle("领料数量");

                    View view1 = LayoutInflater.from(PickingScreenFinishActivity.this).inflate(R.layout.dialog_edit_sl, null);
                    TextView tvTitle = (TextView) view1.findViewById(R.id.dialog_spsl_title);
                    EditText etSl = (EditText) view1.findViewById(R.id.dialog_sl);

                    builder.setPositiveButton("确定", null);
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
                                    String sl = etSl.getText().toString();
                                    double aDoubleSl = Double.parseDouble(TextUtils.isEmpty(sl) ? "0" : sl);
                                    String xlslStr = holder.tvXlsl.getText().toString();
                                    double aDoubleXlSl = Double.parseDouble(TextUtils.isEmpty(xlslStr) ? "0" : xlslStr);
                                    if (aDoubleSl == 0) {
                                        ToastUtils.showToast("请输入领料数量！");
                                        return;
                                    } else if (aDoubleSl > aDoubleXlSl) {
                                        ToastUtils.showToast("领料数量不能大于需领数量！");
                                        return;
                                    }
                                    holder.tvLlsl.setText(String.valueOf(aDoubleSl));
                                    if (ListUtils.getSize(objectList) >= 13) {
                                        objectList.set(12, String.valueOf(aDoubleSl));
                                    } else {
                                        objectList.add(String.valueOf(aDoubleSl));
                                    }

                                    list.set(position, objectList);
                                    notifyItemChanged(position);
                                    dialogInterface.cancel();
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_b_pm)
            TextView tvPm;
            @BindView(R.id.tv_ph)
            TextView tvPh;
            @BindView(R.id.tv_gg)
            TextView tvGg;
            @BindView(R.id.tv_ck)
            TextView tvCk;
            @BindView(R.id.tv_llsl)
            TextView tvLlsl;
            @BindView(R.id.tv_xlsl)
            TextView tvXlsl;
            @BindView(R.id.list_pick_img)
            ImageView imgSel;
            @BindView(R.id.et_kw)
            EditText etKw;
            @BindView(R.id.list_pick_del)
            Button btnDel;
            @BindView(R.id.list_pick_content)
            LinearLayout linlayContent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
