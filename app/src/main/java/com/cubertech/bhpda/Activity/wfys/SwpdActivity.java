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
 * Created by Administrator on 2019/12/13.
 */

public class SwpdActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.et_kw)
    EditText etKw;
    @BindView(R.id.out_storage_list)
    RecyclerView mList;
    private MySwpdAdapter adapter;
    ServiceUtils su = ServiceUtils.getInstance();
    private String tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swpd);
        ButterKnife.bind(this);
        mList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MySwpdAdapter();
        DividerItemDecoration decoration = new DividerItemDecoration(SwpdActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(decoration);
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
                    scanCode("wlh");//
                }
                return false;
            }
        });
        etKw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanCode("kw");//
                }
                return false;
            }
        });
    }

    private void scanCode(String type) {
        String wlh = etTm.getText().toString();
        String kw = etKw.getText().toString();
        switch (type) {
            case "wlh":
                etKw.setSelection(0);
                etKw.requestFocus();
                break;
            case "kw":
                if (TextUtils.isEmpty(wlh)) {
                    etTm.setSelection(0);
                    etTm.requestFocus();
                }
                break;
        }
        if (TextUtils.isEmpty(wlh) || TextUtils.isEmpty(kw)) {
            return;
        }
        String link = "hqswpd";
        HashMap<String, Object> params = new HashMap<String, Object>();
        adapter.setList(null);

//        params.put("KC001", etTm.getText().toString());
        if (!kw.contains("#")) {
            ToastUtils.showToast("请扫描正确格式的库位码");
            return;
        }
        String[] split = kw.split("#");
        params.put("KC001", etTm.getText().toString());
        params.put("KC004", split[0]);
        params.put("KC005", split.length == 1 ? "" : split[1]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(SwpdActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                SwpdActivity.this.tm = etTm.getText().toString();
                List<Object> objectList = (List<Object>) o;
                adapter.setList(objectList);
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                Log.e("#######", o.toString());
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(SwpdActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SwpdActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.del1, R.id.del2, R.id.btn_rk, R.id.btn_now, R.id.btn_qx})
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
                etKw.setText("");
                etKw.setSelection(0);
                etKw.requestFocus();
                break;
            case R.id.btn_rk:
                break;
            case R.id.btn_now:

                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public class MySwpdAdapter extends RecyclerView.Adapter<MySwpdAdapter.ViewHolder> {
        List<Object> list = new ArrayList<>();

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<Object> getList() {
            return list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_swpd, null, false);
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
