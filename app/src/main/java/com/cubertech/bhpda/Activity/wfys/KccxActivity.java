package com.cubertech.bhpda.Activity.wfys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
 * Created by admin on 2020/2/17.
 */

public class KccxActivity extends AppCompatActivity {
    @BindView(R.id.kccx_type)
    Spinner mTypeSpinner;
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.kccx_list)
    RecyclerView mKccxList;
    private final String[] mTypeStrs = {"库存", "物料"};
    private MyKccxAdapter adapter;
    ServiceUtils su = ServiceUtils.getInstance();
    private String name;
    private String tm;
    private boolean isKc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kccx);
        ButterKnife.bind(this);
        ArrayAdapter<String> adapters = new ArrayAdapter<>(this, R.layout.single_textview, mTypeStrs);
        mTypeSpinner.setAdapter(adapters);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view;
                tv.setTextColor(Color.parseColor("#323232"));
                tv.setTextSize(16);
                tv.setGravity(Gravity.CENTER);
                if (TextUtils.equals(tv.getText().toString(), mTypeStrs[0])) {
                    isKc = false;
                } else {
                    isKc = true;
                }
                etTm.setHint("请扫描" + tv.getText().toString() + "码!");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mKccxList.addItemDecoration(decoration);
        mKccxList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyKccxAdapter();
        mKccxList.setAdapter(adapter);
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
    }

    //扫码请求
    private void scanCode() {
        String strTm = etTm.getText().toString();
        if (TextUtils.isEmpty(strTm)) {
            ToastUtils.showToast("请先扫码！");
            return;
        }
        if (!isKc && !strTm.contains("#")) {
            ToastUtils.showToast("请扫描正确格式的库位码！");
            return;
        }
        if (!isKc) {
            String[] split = strTm.split("#");
            if (split.length != 2 || TextUtils.isEmpty(split[0]) || TextUtils.isEmpty(split[1])) {
                ToastUtils.showToast("请扫描正确格式的库位码！");
                return;
            }
        }
        if (isKc && !strTm.contains("#")) {
            ToastUtils.showToast("请扫描正确格式的物料码！");
            return;
        }
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0])) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        etTm.setText(isKc ? split[0] : strTm);
        SharedPreferences sp = this.getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        name = sp.getString("name", null);
        //todo 请填写  方法名
        String link = "kccx";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", (!TextUtils.equals(mTypeSpinner.getSelectedItem().toString(), mTypeStrs[0]) ? "1-" + split[0] : "2-" + strTm));
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(KccxActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = split[0];
                List<Object> objectList = (List<Object>) o;
                Log.e("#####", objectList.toString());
                adapter.setList(objectList);
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(KccxActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(KccxActivity.this, error, Toast.LENGTH_LONG).show();
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
                break;
            case R.id.left:
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public class MyKccxAdapter extends RecyclerView.Adapter<MyKccxAdapter.ViewHolder> {

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_kccx, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Object> objectList = (List<Object>) list.get(position);
            ///0品号//1品名//2规格//3仓库//4库位//5批次//6数量
            holder.tvPh.setText(String.valueOf(objectList.get(0)));
            holder.tvBPm.setText(String.valueOf(objectList.get(1)));
            holder.tvGg.setText(String.valueOf(objectList.get(2)));
            holder.tvCk.setText(String.valueOf(objectList.get(3)));
            holder.tvKw.setText(String.valueOf(objectList.get(4)));
            holder.tvPc.setText(String.valueOf(objectList.get(5)));
            holder.tvSl.setText(String.valueOf(objectList.get(6)));
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_ph)
            TextView tvPh;
            @BindView(R.id.tv_ck)
            TextView tvCk;
            @BindView(R.id.tv_b_pm)
            TextView tvBPm;
            @BindView(R.id.tv_gg)
            TextView tvGg;
            @BindView(R.id.tv_pc)
            TextView tvPc;
            @BindView(R.id.tv_kw)
            TextView tvKw;
            @BindView(R.id.tv_sl)
            TextView tvSl;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
