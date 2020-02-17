package com.cubertech.bhpda.Activity.wfys;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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
 * Created by Administrator on 2019/12/25.
 */

public class BjrkActivity extends AppCompatActivity {
    @BindView(R.id.et_value)
    EditText etTm;
    @BindView(R.id.bjrk_recycle)
    RecyclerView mList;
    @BindView(R.id.tv_xmh)
    TextView tvXmh;
    @BindView(R.id.tv_gzzx)
    TextView tvGzzx;
    @BindView(R.id.tv_bm)
    TextView etBm;
    private MyBjrkAdapter adapter;
    ServiceUtils su = ServiceUtils.getInstance();
    private String tm;
    private List<Object> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjrk);
        ButterKnife.bind(this);
        mList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyBjrkAdapter();
        mList.setAdapter(adapter);
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
        DividerItemDecoration decoration = new DividerItemDecoration(BjrkActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(decoration);
    }

    private void scanCode() {
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
        //todo 请填写  方法名
        String link = "bjrkxx";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(BjrkActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                list = new ArrayList<>();
                List<Object> objectList = (List<Object>) o;
                Log.e("######" + objectList.size(), objectList.toString());
                boolean isEquest = false;
                for (Object obj : list) {
                    List<Object> objectList1 = (List<Object>) obj;
                    if (objectList1.containsAll(objectList)) {
                        isEquest = true;
                        break;
                    }
                }

                if (!isEquest) {
                    objectList.add("0");
                    objectList.add("");//pc
                    objectList.add("");//kw
                    list.add(objectList);
                    adapter.setList(list);
                }

                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(BjrkActivity.this, str);
                su.closeParentDialog();//必须
//                假数据
//                List<Object> objectList = new ArrayList<>();
//                objectList.add("工作中心");
//                objectList.add("品号");
//                objectList.add("品名");
//                objectList.add("规格");
//                objectList.add("入库#库位");
//                objectList.add("预计产量");
//                objectList.add("0");
//                objectList.add("工单单别");
//                objectList.add("工单单号");
//                objectList.add("材料品号");
//                objectList.add("10");
//                objectList.add("领料仓库");
//                objectList.add("领料库位");
//                list.add(objectList);
//                adapter.setList(list);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BjrkActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.del1, R.id.btn_submit, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.del1:
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                break;
            case R.id.btn_submit:
//                ProgressDialog progressDialog = ProgressDialog.show(BjrkActivity.this, "", "正在上传中...", false, false);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(BjrkActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
//                        onBackPressed();
//                    }
//                }, 1000);
                onSubmitClick();
                break;
            case R.id.left:
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }

    private void onSubmitClick() {
        if (TextUtils.isEmpty(etBm.getText().toString())) {
            ToastUtils.showToast("请输入部门！");
            return;
        }
//        if (!etTm.getText().toString().contains("#")) {
//            ToastUtils.showToast("请扫描正确格式的单号！");
//            return;
//        }
//        String strTm = etTm.getText().toString();
//        String[] split = strTm.split("#");
//        if (TextUtils.isEmpty(split[0])) {
//            ToastUtils.showToast("请扫描正确格式的单号！");
//            return;
//        }
        //0 工作中心,1 品号,2 品名,3 规格,4入库库位,5 预计产量,6 已完工量,'完工数量' 手动输入,7 工单单别
// ,8 工单单号,9 材料品号,10 需领用量,11 领料仓库,12 领料库位13项目编号,15手动输入
        List<Object> list = adapter.getList();
        if (ListUtils.isEmpty(list)) {
            ToastUtils.showToast("请扫描单号！");
        }
        List<Object> shjy = new ArrayList<>();
        List<Object> shjy2 = new ArrayList<>();
        int count = 1;
        int countPick = 1;
        for (Object o : list) {
            List<Object> objectList = (List<Object>) o;
            List<Object> objectList1 = new ArrayList<>();
            objectList1.add("5801");//0单别
            objectList1.add("11");//1工厂编号
            objectList1.add("");//2备注
            objectList1.add(objectList.get(0));//3工作中心
            objectList1.add(objectList.get(13));//4项目号
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 4 - String.valueOf(count).length(); i++) {
                builder.append("0");
            }
            builder.append(count);

            objectList1.add(builder.toString());//序号5
            objectList1.add(objectList.get(1));//6品号
            String s = String.valueOf(objectList.get(4));
            String[] split = s.split("#");
            objectList1.add(split[0]);//7入库库位
            objectList1.add(objectList.get(18));//8入库数量
            objectList1.add(0);//9
            objectList1.add(objectList.get(18));//10验收数量
            objectList1.add(objectList.get(7));//11单别
            objectList1.add(objectList.get(8));//12单号
            objectList1.add("Y");//13
            objectList1.add("********************");//14
            objectList1.add("##########");//15
            objectList1.add("");//16
            objectList1.add(objectList.get(2));//17pm
            objectList1.add(objectList.get(3));//18gg
            objectList1.add(objectList.get(17));//19dw
            objectList1.add(objectList.get(20));//20kw
            objectList1.add(objectList.get(19));//pc21
            SharedPreferences sp = getSharedPreferences(
                    "config", Activity.MODE_PRIVATE);

            String name = sp.getString("name", null);
            objectList1.add(name);//[22]操作人员
            shjy.add(objectList1);

            //0 工作中心,1 品号,2 品名,3 规格,4入库库位,5 预计产量,6 已完工量,'完工数量' 手动输入,7 工单单别
// ,8 工单单号,9 材料品号,10 需领用量,11 领料仓库,12 领料库位 13项目编号14 物料品号，数量#物料品号，数量
            //todo
            String s14 = String.valueOf(objectList.get(14));
            String[] split1 = s14.split("#");
            for (int i = 0; i < split1.length; i++) {
                String[] split2 = split1[i].split(",");


                double sl = 0;
                String t = "";
                try {
                    double PPSL = Double.parseDouble(String.valueOf(objectList.get(18)));//匹配数量
                    double YJCL = Double.parseDouble(String.valueOf(objectList.get(5)));//预计产量
                    double XLSL = Double.parseDouble(String.valueOf(split2[1]));//需领数量
                    double TB005 = Double.parseDouble(String.valueOf(split2[2]));
                    t = String.valueOf(split2[3]).replace(" ", "");
                    double YWGL = Double.parseDouble(String.valueOf(objectList.get(6)));//已完工量
                    if (t.equals("1")) {

                        if (((YWGL + PPSL) / YJCL * XLSL) > TB005) {
                            ToastUtils.showToast("工单缺领，缺领料号：" + split2[0].trim() + "！");
                            return;
                        }
                        /*else {
                            continue;
                        }*/
                    }
//                    if (!t.equals(2)) {
//                        continue;
//                    }

                    sl = (PPSL / YJCL) * XLSL;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


                List<Object> objectList2 = new ArrayList<>();
                objectList2.add("5403");//0
                objectList2.add("11");//1
                objectList2.add(objectList.get(0));//2工作中心  ;
                objectList2.add("");//3
                objectList2.add("54");//4
                objectList2.add(etBm.getText().toString());//手动输入5
                objectList2.add(name);//6
                objectList2.add(objectList.get(13));//7
                StringBuilder builder1 = new StringBuilder();
                for (int i1 = 0; i1 < 4 - String.valueOf(count).length(); i1++) {
                    builder1.append("0");
                }
                builder1.append(countPick);
                objectList2.add(builder1.toString());//序号8
                countPick++;
                objectList2.add(split2[0]);//9   //todo 物料品号
                objectList2.add(String.valueOf(sl));//10匹配数量   （10匹配数量/钣金预计产量）*物料需领数量
                objectList2.add(objectList.get(16));//11 入库仓库  //todo 领料仓库  2020 1.6修改
                objectList2.add("****");//12
                objectList2.add("********************");//13
                objectList2.add(objectList.get(7));//14
                objectList2.add(objectList.get(8));//15
                objectList2.add("");//16
                objectList2.add("1");//17
                objectList2.add("##########");//18
                objectList2.add(objectList.get(2));//19pm
                objectList2.add(objectList.get(3));//20gg
                objectList2.add(objectList.get(17));//21dw
                objectList2.add(name);//22
                if (TextUtils.equals(t, "2")) {
                    shjy2.add(objectList2);
                }

            }
            count++;


        }
        //todo 请填写  方法名
        String link = "bcbjrk";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", shjy);
        params.put("shjy2", shjy2);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(BjrkActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                BjrkActivity.this.list = new ArrayList<>();
                List<Object> objectList = (List<Object>) o;
                Log.e("######", objectList.toString());
                boolean isEquest = false;
                for (Object obj : BjrkActivity.this.list) {
                    List<Object> objectList1 = (List<Object>) obj;
                    if (objectList1.containsAll(objectList)) {
                        isEquest = true;
                        break;
                    }
                }
                if (!isEquest) {
                    objectList.add("0");
                    objectList.add("");
                    objectList.add("");//kw
                    BjrkActivity.this.list.add(objectList);
                    adapter.setList(BjrkActivity.this.list);
                }


                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(BjrkActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BjrkActivity.this, error, Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public class MyBjrkAdapter extends RecyclerView.Adapter<MyBjrkAdapter.ViewHolder> {

        private List<Object> list = new ArrayList<>();

        public List<Object> getList() {
            return list;
        }

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_bjrk, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            List<Object> objectList = (List<Object>) list.get(position);
//0 工作中心,1 品号,2 品名,3 规格,4入库库位,5 预计产量,6 已完工量,'完工数量' 手动输入,7 工单单别
// ,8 工单单号,9 材料品号,10 需领用量,11 领料仓库,12 领料库位 13项目编号,14 物料品号，需领数量#物料品号，需领数量
// 15 部门，16MD016 ，17单位


            tvXmh.setText(String.valueOf(objectList.get(13)));
            tvGzzx.setText(String.valueOf(objectList.get(0)));
            holder.tvGg.setText(String.valueOf(objectList.get(3)));
            holder.tvPm.setText(String.valueOf(objectList.get(2)));
            holder.tvPh.setText(String.valueOf(objectList.get(1)));
//            String strCkKw = String.valueOf(objectList.get(4));
//            String[] split = strCkKw.split("#");
//            holder.tvCk.setText(split == null ? "" : split[0]);
//            holder.tvKw.setText((split == null || split.length == 1) ? "" : split[1]);
            holder.tvCk.setText(String.valueOf(objectList.get(11)));
            holder.tvKw.setText(String.valueOf(objectList.get(12)));
//            holder.tvSl.setText(String.valueOf(objectList.get(6)));
            holder.tvSl.setText(String.valueOf(objectList.get(18)));
            holder.etPc.setText(String.valueOf(objectList.get(19)));
            holder.etKw.setText(String.valueOf(objectList.get(20)));
            etBm.setText(String.valueOf(objectList.get(15)));
            try {
                double yjsl = Double.parseDouble(String.valueOf(objectList.get(5)));
                double ywgl = Double.parseDouble(String.valueOf(objectList.get(6)));
                double unWgsl = yjsl - ywgl;
                holder.tvUnWgsl.setText(String.valueOf(unWgsl));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e("NumberFormatException", "for bjrkadapter");
                holder.tvUnWgsl.setText("0");
            }

            holder.linlaySlContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BjrkActivity.this, R.style.MP_Theme_alertDialog);
                    builder.setTitle("完工数量填写");
                    View view1 = LayoutInflater.from(BjrkActivity.this).inflate(R.layout.dialog_edit_sl, null);
                    EditText etSl = (EditText) view1.findViewById(R.id.dialog_sl);
                    TextView tvTitle = (TextView) view1.findViewById(R.id.dialog_spsl_title);
                    tvTitle.setText("完工数量");
                    etSl.setText(holder.tvSl.getText().toString());
                    etSl.setSelection(etSl.getText().toString().length());
                    etSl.requestFocus();
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
                                    double sl = Double.parseDouble(etSl.getText().toString());
                                    double v = Double.parseDouble(holder.tvUnWgsl.getText().toString());

                                    if (sl > v) {
                                        ToastUtils.showToast("入库数量不能大于未完工数量！");
                                        return;
                                    }
                                    holder.tvSl.setText(etSl.getText().toString());
                                    objectList.set(18, (etSl.getText().toString()));
                                    list.set(position, objectList);
                                    notifyDataSetChanged();
                                    dialogInterface.cancel();
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
            });
            holder.etPc.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    objectList.set(19, editable.toString());
                    list.set(position, objectList);

                }
            });
            holder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.etPc.setText("");
                    holder.etPc.setSelection(0);
                    holder.etPc.requestFocus();
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
                    objectList.set(20, editable.toString());
                    list.set(position, objectList);

                }
            });
            holder.btnKwDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.etKw.setText("");
                    holder.etKw.setSelection(0);
                    holder.etKw.requestFocus();
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.list_bjrk_ph)
            TextView tvPh;
            @BindView(R.id.list_bjrk_pm)
            TextView tvPm;
            @BindView(R.id.list_bjrk_gg)
            TextView tvGg;
            @BindView(R.id.list_bjrk_ck)
            TextView tvCk;
            @BindView(R.id.list_bjrk_kw)
            TextView tvKw;
            @BindView(R.id.list_bjrk_sl)
            TextView tvSl;
            @BindView(R.id.list_bjrk_sl_content)
            LinearLayout linlaySlContent;
            @BindView(R.id.list_bjrk_content)
            LinearLayout linlayContent;
            @BindView(R.id.list_bjrk_uwgsl)
            TextView tvUnWgsl;
            @BindView(R.id.et_pc)
            EditText etPc;
            @BindView(R.id.list_bjrk_del)
            Button btnDel;
            @BindView(R.id.et_kw)
            EditText etKw;
            @BindView(R.id.list_bjrk_del_kw)
            Button btnKwDel;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}

