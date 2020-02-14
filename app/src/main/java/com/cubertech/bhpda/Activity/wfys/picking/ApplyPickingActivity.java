package com.cubertech.bhpda.Activity.wfys.picking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.wfys.BjrkActivity;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 领料申请
 * Created by admin on 2019/12/26.
 */

public class ApplyPickingActivity extends AppCompatActivity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_llry)
    TextView tvLlry;
    @BindView(R.id.tv_lldb)
    TextView tvLldb;
    @BindView(R.id.llsq_list)
    RecyclerView mList;
    private ServiceUtils su = ServiceUtils.getInstance();
    private MyApplyPickingAdapter adapter;
    private String tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llsq);
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
                    scanCode();//
                }
                return false;
            }
        });
        mList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyApplyPickingAdapter();
        DividerItemDecoration decoration = new DividerItemDecoration(ApplyPickingActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(decoration);
        mList.setAdapter(adapter);
    }

    private void scanCode() {
        //todo 请填写  方法名
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("wlm", etTm.getText().toString());
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
        su.callService(ApplyPickingActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(ApplyPickingActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ApplyPickingActivity.this, error, Toast.LENGTH_LONG).show();
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
//                break;
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

    public class MyApplyPickingAdapter extends RecyclerView.Adapter<MyApplyPickingAdapter.ViewHolder> {
        private List<Object> list = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_llsq, null);
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
