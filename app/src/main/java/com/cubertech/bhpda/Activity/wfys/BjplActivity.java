package com.cubertech.bhpda.Activity.wfys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2020/3/7.
 */

public class BjplActivity extends AppCompatActivity {
    @BindView(R.id.et_gddh)
    EditText etGddh;
    @BindView(R.id.tv_sbh)
    TextView tvSbh;
    @BindView(R.id.tv_xmh)
    TextView tvXmh;
    @BindView(R.id.tv_xmmc)
    TextView tvXmmc;
    @BindView(R.id.tv_cpph)
    TextView tvCpph;
    @BindView(R.id.tv_cppm)
    TextView tvCppm;
    @BindView(R.id.tv_cpgg)
    TextView tvCpgg;
    @BindView(R.id.ll_list)
    RecyclerView mList;
    private String tm;
    private ServiceUtils su = ServiceUtils.getInstance();
    private List<Map<String, Object>> mapList = new ArrayList<>();
    private MyBjplAdapter adapter;
    private String tmAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjpl);
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
        adapter = new MyBjplAdapter();
        mList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(decoration);
        mList.setAdapter(adapter);
    }

    private void scanCode() {
        if (!etGddh.getText().toString().contains("#")) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        String strTm = etGddh.getText().toString();
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0]) || split.length != 3) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        etGddh.setText(split[0]);
        tvSbh.setText(split[split.length - 1]);
        //todo 请填写  方法名
        String link = "hqtzcpm";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("barcode", strTm);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(BjplActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tmAll = strTm;
                tm = etGddh.getText().toString();
                List<Object> objectList = (List<Object>) o;
                //0成品品号   1成品品名   2成品规格   3项目编号   4项目名称

                tvCpph.setText(objectList.get(0).toString());
                tvCppm.setText(objectList.get(1).toString());
                tvCpgg.setText(objectList.get(2).toString());
                tvXmh.setText(objectList.get(3).toString());
                tvXmmc.setText(objectList.get(4).toString());

                mapList = new ArrayList<>();
                adapter.setList(mapList);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(BjplActivity.this, str);
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
                Toast.makeText(BjplActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
                tvSbh.setText("");
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

    private void scanCodePl(String plm, int position) {
        if (!plm.contains("#")) {
            ToastUtils.showToast("请扫描正确格式的配料码！");
            return;
        }
        String[] split = plm.split("#");
        if (TextUtils.isEmpty(split[0]) || split.length < 2) {
            ToastUtils.showToast("请扫描正确格式的配料码！");
            return;
        }
//        try {
//            split[0] = plm.substring(plm.indexOf("#")+1, plm.indexOf("#") + 13);
//            Log.e("######", split[0]);
//        } catch (IndexOutOfBoundsException e) {
//            ToastUtils.showToast("请扫描正确格式的配料码！");
//            return;
//        }

        //todo 请填写  方法名
        String link = "hqtzplm";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("barcode", plm);
//        params.put("pmdsdocno2", tmAll);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(BjplActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;
                adapter.replaceItem(split[1], objectList, position, plm);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(BjplActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BjplActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须


            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    private void upLoading() {
        List<Object> list1 = new ArrayList<>();
        list1.add(tmAll);//0成品码
        list1.add(tvCpph.getText().toString());
        List<Object> list2 = new ArrayList<>();
        List<Map<String, Object>> adapterList = adapter.getList();
        for (Map<String, Object> map : adapterList) {
            List<Object> objectList = new ArrayList<>();
            List<Object> objectList1 = (List<Object>) map.get("data");
            if (!TextUtils.isEmpty(map.get("plmAll").toString())) {
                objectList.add(map.get("plmAll"));//0配料码
                objectList.add(objectList1.get(0));//1配料品号
                SharedPreferences sp = getSharedPreferences(
                        "config", Activity.MODE_PRIVATE);

                String name = sp.getString("name", null);
                objectList.add(name);//[2]操作人员
                list2.add(objectList);
            }

        }
        if (ListUtils.isEmpty(list2)) {
            ToastUtils.showToast("请扫描配料码！");
            return;

        }

        //todo 请填写  方法名
        String link = "bctzgg";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", list1);
        params.put("shjy2", list2);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(BjplActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                ToastUtils.showToast("提交成功！");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 1500);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(BjplActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BjplActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须


            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }


    @OnClick({R.id.left, R.id.del1d, R.id.btn_submit, R.id.btn_addpl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
                onBackPressed();
                break;
            case R.id.del1d:
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
                break;
            case R.id.btn_submit:
                if (TextUtils.isEmpty(tm) || TextUtils.isEmpty(tvSbh.getText().toString())) {
                    ToastUtils.showToast("请扫描成品码！");
                    return;
                }
                List<Map<String, Object>> list = adapter.getList();
                if (ListUtils.isEmpty(list)) {
                    ToastUtils.showToast("请扫描配料码！");
                    return;
                }
                upLoading();
                break;
            case R.id.btn_addpl:
                if (TextUtils.isEmpty(tm) || TextUtils.isEmpty(tvSbh.getText().toString())) {
                    ToastUtils.showToast("请扫描成品码！");
                    return;
                }
                adapter.addItem("");
                break;
        }
    }


    public class MyBjplAdapter extends RecyclerView.Adapter<MyBjplAdapter.ViewHolder> {

        private List<Map<String, Object>> list = new ArrayList<>();

        public List<Map<String, Object>> getList() {
            return list;
        }

        public void setList(List<Map<String, Object>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_bjpl_add, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Map<String, Object> map = list.get(position);
            holder.etTm.setText(map.get("plm").toString());
            //0配料品号  1配料品名   2配料规格
            List<Object> objectList = (List<Object>) map.get("data");
            holder.tvCpph.setText(String.valueOf(objectList.get(0)));
            holder.tvCppm.setText(String.valueOf(objectList.get(1)));
            holder.tvCpgg.setText(String.valueOf(objectList.get(2)));
            holder.etTm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                        scanCodePl(holder.etTm.getText().toString(), position);//
                    }
                    return false;
                }
            });
            holder.btnTmClean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.etTm.setText("");
                    holder.etTm.setSelection(0);
                    holder.etTm.requestFocus();
                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public void addItem(String plm) {
            Map<String, Object> map = new HashMap<>();
            map.put("plm", plm);
            map.put("plmAll", plm);
            List<Object> objectList = new ArrayList<>();
            objectList.add("");
            objectList.add("");
            objectList.add("");
            map.put("data", objectList);
            list.add(map);
            setList(list);
        }

        public void replaceItem(String plm, List<Object> objects, int position, String plmAll) {
            Map<String, Object> map = new HashMap<>();
            map.put("plm", plm);
            map.put("plmAll", plmAll);
            map.put("data", objects);
            list.set(position, map);
            setList(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.et_tm)
            EditText etTm;
            @BindView(R.id.del1)
            ImageView btnTmClean;
            @BindView(R.id.tv_cpph)
            TextView tvCpph;
            @BindView(R.id.tv_cppm)
            TextView tvCppm;
            @BindView(R.id.tv_cpgg)
            TextView tvCpgg;
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
