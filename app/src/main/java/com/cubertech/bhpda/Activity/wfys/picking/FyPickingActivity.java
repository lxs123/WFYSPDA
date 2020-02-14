package com.cubertech.bhpda.Activity.wfys.picking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
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
 * 费用领料
 * Created by admin on 2020/2/14.
 */

public class FyPickingActivity extends AppCompatActivity {
    @BindView(R.id.et_gddh)
    EditText etGddh;
    @BindView(R.id.tv_ll_gddh)
    TextView tvLlGddh;
    @BindView(R.id.ll_list)
    RecyclerView mList;
    //    @BindView(R.id.btn_ll)
//    Button btnLl;
//    @BindView(R.id.btn_qx)
//    Button btnQx;
    private MyFyPickingAdapter adapter;
    private String name;
    private String tm;
    ServiceUtils su = ServiceUtils.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fy_picking);
        ButterKnife.bind(this);
        mList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyFyPickingAdapter();
        mList.setAdapter(adapter);
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
        SharedPreferences sp = this.getSharedPreferences(
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
        su.callService(FyPickingActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
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
                CommonUtil.showErrorDialog(FyPickingActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(FyPickingActivity.this, error, Toast.LENGTH_LONG).show();
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

    //扫码请求
    private void onUrlLoad() {

        //todo 请填写  方法名
        String link = "hqlld";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(FyPickingActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(FyPickingActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(FyPickingActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.btn_ll, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_ll:
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

    public class MyFyPickingAdapter extends RecyclerView.Adapter<MyFyPickingAdapter.ViewHolder> {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_fy_picking, null);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
