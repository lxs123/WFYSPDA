package com.cubertech.bhpda.Activity.wfys.db;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/13.
 */

public class KndbActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.et_tm1)
    EditText etTm1;
    @BindView(R.id.et_tm2)
    EditText etTm2;
    @BindView(R.id.kndb_list)
    RecyclerView mKndbList;
    private MyKndbAdapter adapter;
    ServiceUtils su = ServiceUtils.getInstance();
    private String tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kndb_test);
        ButterKnife.bind(this);
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
                    scanCode("wlm");//
                }
                return false;
            }
        });
        etTm1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanCode("kwm");//
                }
                return false;
            }
        });
        etTm2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanCode("mdkwm");//
                }
                return false;
            }
        });
        mKndbList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyKndbAdapter();
        mKndbList.setAdapter(adapter);
    }

    private void scanCode(String type) {
        String wlm = etTm.getText().toString();
        String kwm = etTm1.getText().toString();
        String mdkwm = etTm2.getText().toString();
        switch (type) {
            case "wlm":
                wlm = etTm.getText().toString();
                etTm1.setSelection(0);
                etTm1.requestFocus();
                break;
            case "kwm":
                kwm = etTm1.getText().toString();
//                etTm2.setSelection(0);
//                etTm2.requestFocus();
                break;
            case "mdkwm":
//                mdkwm = etTm2.getText().toString();
//                if (TextUtils.isEmpty(wlm)) {
//                    etTm.setSelection(0);
//                    etTm.requestFocus();
//                } else if (TextUtils.isEmpty(kwm)) {
//                    etTm1.setSelection(0);
//                    etTm1.requestFocus();
//                }
                break;
        }
        if (TextUtils.isEmpty(wlm) || TextUtils.isEmpty(kwm)) {
            return;
        }
        String link = "kndb";
        HashMap<String, Object> params = new HashMap<String, Object>();
        adapter.setList(null);
        String ckkw = etTm1.getText().toString();
        if (!ckkw.contains("#")) {
            ToastUtils.showToast("请扫描正确格式的库位码");
            return;
        }
        String[] split = ckkw.split("#");
        params.put("code", etTm.getText().toString() + "-" + (split.length == 1 ? "" : split[1]));
//        params.put("KC004", split[0]);
//        params.put("KC005", split.length == 1 ? "" : split[1]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(KndbActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                List<Object> objectList = (List<Object>) o;

                Log.e("#######", o.toString());
                List<Map<String, String>> mapList = new ArrayList<>();
                for (int i = 0; i < ListUtils.getSize(objectList); i++) {
                    List<Object> objectList1 = (List<Object>) objectList.get(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("sel", "0");
//                    map.put("pc", objectList1.get(7).toString());
//                    map.put("sl", String.valueOf(objectList1.get(9)));
                    map.put("list", objectList1.toString());

                    mapList.add(map);
                }
                adapter.setList(mapList);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(KndbActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(KndbActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.del1, R.id.del2, R.id.del3, R.id.btn_ll, R.id.btn_qx})
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
            case R.id.del2:
                etTm1.setText("");
                etTm1.setSelection(0);
                etTm1.requestFocus();
                break;
            case R.id.del3:
                etTm2.setText("");
                etTm2.setSelection(0);
                etTm2.requestFocus();
                break;
            case R.id.btn_ll:
                if (TextUtils.isEmpty(tm)) {
                    ToastUtils.showToast("请扫描物料码!");
                    return;
                } else if (TextUtils.isEmpty(etTm1.getText().toString())) {
                    ToastUtils.showToast("请扫描库位码!");
                    return;
                } else if (TextUtils.isEmpty(etTm2.getText().toString())) {
                    ToastUtils.showToast("请扫描目的库位码!");
                    return;
                } else if (ListUtils.isEmpty(adapter.getList())) {
                    ToastUtils.showToast("请扫描物料和库位码!");
                    return;
                }
                String ckkw = etTm1.getText().toString();
                if (!ckkw.contains("#")) {
                    ToastUtils.showToast("请扫描正确格式的库位码");
                    return;
                }
                String[] split = ckkw.split("#");
                String ckkw1 = etTm2.getText().toString();
                if (!ckkw1.contains("#")) {
                    ToastUtils.showToast("请扫描正确格式的目的库位码");
                    return;
                }
                String[] split1 = ckkw1.split("#");
                List<Map<String, String>> list = adapter.getList();

                List<Object> objects = new ArrayList<>();
                for (int i = 0; i < ListUtils.getSize(list); i++) {
                    Map<String, String> map = list.get(i);
                    if (TextUtils.equals(map.get("sel"), "1")) {

                        String list1 = map.get("list");
                        List list2 = ListUtils.toList(list1);
                        for (int j = 0; j < 2; j++) {
                            List<Object> objects1 = new ArrayList<>();
//                            Format format = new DecimalFormat("#0");
//                            objects1.add(format.format(Double.parseDouble(String.valueOf(list2.get(0)))));//企业编号[0
//                            objects1.add(list2.get(1));//据点[1
//                            objects1.add(j == 0 ? "1" : "-1");//入库wei1 移动类型
//                            objects1.add(tm);//单据号[3
//                            objects1.add("0");//xc[4

                            //0品号 1品名  2规格  3 仓库 4库位 5批次  6单位  7库存数量
                            objects1.add(list2.get(0));//品号[0
                            objects1.add(list2.get(1));//品名[1
                            objects1.add(list2.get(2));//规格[2
                            objects1.add(list2.get(6));//单位[3
                            objects1.add(j == 0 ? list2.get(7) : list2.get(8));//数量[4
                            objects1.add(list2.get(3));//仓库5[
                            objects1.add(j == 1 ? (split1.length == 1 ? "" : split1[1]) : (split.length == 1 ? "" : split[1]));//库位[6
                            objects1.add(list2.get(5));//批号[7
                            SharedPreferences sp = KndbActivity.this.getSharedPreferences(
                                    "config", Activity.MODE_PRIVATE);

                            String name = sp.getString("name", null);
                            objects1.add(name);//用户[8
//                            objects1.add("PDA");//pda14
                            objects.add(objects1);
                        }

                    }
                }
                if (ListUtils.isEmpty(objects)) {
                    ToastUtils.showToast("请选择需要调拨的数据!");
                    return;
                }
                onUrlLoading(objects);
                break;

        }
    }

    private void onUrlLoading(List<Object> objects) {
        String link = "bckndb";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", objects);
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
        su.callService(KndbActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                ToastUtils.showToast("调拨成功");
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
                CommonUtil.showErrorDialog(KndbActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(KndbActivity.this, error, Toast.LENGTH_LONG).show();
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

    public class MyKndbAdapter extends RecyclerView.Adapter<MyKndbAdapter.ViewHolder> {
        List<Map<String, String>> list = new ArrayList<>();


        public void setList(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<Map<String, String>> getList() {
            return list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_kndb_test, null, false);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            Map<String, String> map = list.get(position);

//            holder.tvKndbPc.setText(map.get("pc"));
//            holder.tvKndbSl.setText(map.get("sl"));
            String list2 = map.get("list");
            List list1 = ListUtils.toList(list2);
            if (ListUtils.getSize(list1) < 9) {
                list1.add(list1.get(7));
            }
//            holder.tvQybh.setText(String.valueOf(list1.get(0)));
//            holder.tvJd.setText(String.valueOf(list1.get(1)));
            //0品号 1品名  2规格  3 仓库 4库位 5批次  6单位  7库存数量
            holder.tvPm.setText(String.valueOf(list1.get(1)).replace(" ",""));
            holder.tvPh.setText(String.valueOf(list1.get(0)).replace(" ",""));
            holder.tvCk.setText(String.valueOf(list1.get(3)).replace(" ",""));
            holder.tvKndbPc.setText(String.valueOf(list1.get(5)).replace(" ",""));
            holder.tvKndbOldSl.setText(String.valueOf(list1.get(7)).replace(" ",""));
            holder.tvKndbSl.setText(String.valueOf(list1.get(8)).replace(" ",""));
            if (TextUtils.equals(map.get("sel"), "1")) {
                holder.imgKndb.setImageDrawable(getResources().getDrawable(R.mipmap.ic_select));
            } else {
                holder.imgKndb.setImageDrawable(getResources().getDrawable(R.mipmap.ic_unselect));
            }
            holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.equals(map.get("sel"), "1")) {
                        map.put("sel", "0");
                    } else {
                        map.put("sel", "1");
                    }
                    list.set(position, map);
                    setList(list);

                }
            });
            holder.tvKndbSl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(editable.toString())) {
                        try {
                            if (Double.parseDouble(editable.toString()) > Double.parseDouble(String.valueOf(list1.get(7)))) {
                                ToastUtils.showToast("数量不能大于库存数量！");
                                return;
                            }
                            list1.set(8, editable.toString());
                            map.put("list", list1.toString());
                            list.set(position, map);

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.list_kndb_img)
            ImageView imgKndb;
            @BindView(R.id.list_kndb_pc)
            TextView tvKndbPc;
            @BindView(R.id.list_kndb_sl)
            EditText tvKndbSl;
            @BindView(R.id.list_kndb_content)
            LinearLayout linlayContent;
            @BindView(R.id.list_kndb_qybh)
            TextView tvQybh;
            @BindView(R.id.list_kndb_jd)
            TextView tvJd;
            @BindView(R.id.list_kndb_pm)
            TextView tvPm;
            @BindView(R.id.list_kndb_ph)
            TextView tvPh;
            @BindView(R.id.list_kndb_ck)
            TextView tvCk;
            @BindView(R.id.list_kndb_sl_old)
            TextView tvKndbOldSl;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
