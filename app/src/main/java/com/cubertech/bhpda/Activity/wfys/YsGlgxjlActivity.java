package com.cubertech.bhpda.Activity.wfys;

import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
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
 * Created by admin on 2019/12/27.
 */

public class YsGlgxjlActivity extends AppCompatActivity {
    @BindView(R.id.et_zj)
    EditText etZj;
    @BindView(R.id.tv_zj_ph)
    TextView tvZjPh;
    @BindView(R.id.tv_zj_pm)
    TextView tvZjPm;
    @BindView(R.id.tv_zj_gg)
    TextView tvZjGg;
    @BindView(R.id.et_yj)
    EditText etYj;
    @BindView(R.id.recycler_b_id)
    RecyclerView mYjList;
    private MyYjAdapter adapter;
    ServiceUtils su = ServiceUtils.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glgxjl);
        ButterKnife.bind(this);
        mYjList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(YsGlgxjlActivity.this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mYjList.addItemDecoration(itemDecoration);
        adapter = new MyYjAdapter();
        mYjList.setAdapter(adapter);
        etZj.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanCode("zj");//
                }
                return false;
            }
        });
        etYj.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanCode("yj");//
                }
                return false;
            }
        });
    }

    private void scanCode(String type) {
        //todo 请填写  方法名
        String link = "";
        String code = "";
        switch (type) {
            case "zj":
                if (TextUtils.isEmpty(etZj.getText().toString())) {
                    ToastUtils.showToast("请扫描主键序号！");
                    return;
                }
                link = "";
                code = etZj.getText().toString();
                break;
            case "yj":
                if (TextUtils.isEmpty(etYj.getText().toString())) {
                    ToastUtils.showToast("请扫描子键序号！");
                    return;
                }
                link = "";
                code = etYj.getText().toString();
                break;
        }


        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", code);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(YsGlgxjlActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {

                List<Object> objectList = (List<Object>) o;
                Log.e("######", objectList.toString());
                switch (type) {
                    case "zj":
                        etZj.setText("");
                        etZj.setSelection(0);
                        etZj.requestFocus();
                        break;
                    case "yj":
                        etYj.setText("");
                        etYj.setSelection(0);
                        etYj.requestFocus();
                        break;
                }

            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(YsGlgxjlActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(YsGlgxjlActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
                switch (type) {
                    case "zj":
                        etZj.setText("");
                        etZj.setSelection(0);
                        etZj.requestFocus();
                        break;
                    case "yj":
                        etYj.setText("");
                        etYj.setSelection(0);
                        etYj.requestFocus();
                        break;
                }
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    @OnClick({R.id.del1d, R.id.del1dd, R.id.btn_submit, R.id.btn_qx, R.id.left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.del1d:
                etZj.setText("");
                etZj.setSelection(0);
                etZj.requestFocus();
                break;
            case R.id.del1dd:
                etYj.setText("");
                etYj.setSelection(0);
                etYj.requestFocus();
                break;
            case R.id.btn_submit:
//                if (TextUtils.isEmpty()) {
//                    ToastUtils.showToast("请扫描正确格式的单号！");
//                    return;
//                }
                //todo 请填写  方法名
                String link = "bjrkxx";
                HashMap<String, Object> params = new HashMap<String, Object>();
                //todo 请修改  参数名
//                params.put("code", split[0]);
                params.put("strToken", "");
                params.put("strVersion", Utils.getVersions(this));
                params.put("strPoint", "");
                params.put("strActionType", "1001");
                su.callService(YsGlgxjlActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {


                    }

                    @Override
                    public void onFailure(String str) {
                        Toast.makeText(YsGlgxjlActivity.this, str, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(YsGlgxjlActivity.this, error, Toast.LENGTH_LONG).show();
                        su.closeParentDialog();//必须
                    }

                    @Override
                    public void onCompleted() {
                        su.closeParentDialog();//必须
                    }
                });
                break;
            case R.id.left:
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

    public class MyYjAdapter extends RecyclerView.Adapter<MyYjAdapter.ViewHolder> {
        List<Object> list = new ArrayList<>();

        public List<Object> getList() {
            return list;
        }

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_ys_glgxjl, null);
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
            @BindView(R.id.tv_b_tm)
            TextView tvYjxh;
            @BindView(R.id.tv_b_yjmc)
            TextView tvYjmc;
            @BindView(R.id.tv_b_yjph)
            TextView tvYjph;
            @BindView(R.id.tv_b_yjgg)
            TextView tvYjgg;
            @BindView(R.id.tv_b_xgll)
            TextView tvXgll;
            @BindView(R.id.tv_b_ygll)
            TextView tvYgll;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
