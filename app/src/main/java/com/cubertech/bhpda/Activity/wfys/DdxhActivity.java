package com.cubertech.bhpda.Activity.wfys;

import android.content.Intent;
import android.os.Bundle;
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
 * Created by admin on 2019/12/30.
 */

public class DdxhActivity extends AppCompatActivity {
    @BindView(R.id.et_gddh)
    EditText etGddh;
    @BindView(R.id.ll_list)
    RecyclerView mList;
    private MyDdxhAdapter adapter;
    private ServiceUtils su = ServiceUtils.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wf_ddxh);
        ButterKnife.bind(this);
        mList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(DdxhActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mList.addItemDecoration(decoration);
        adapter = new MyDdxhAdapter();
        mList.setAdapter(adapter);
        etGddh.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode() && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    scanResult();
                }
                return false;
            }
        });
    }

    private void scanResult() {
        if (!etGddh.getText().toString().contains("#")) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        String strTm = etGddh.getText().toString();
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0])) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        etGddh.setText(split[0]);
        //todo 请填写  方法名
        String link = "ddxh";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(DdxhActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;
                adapter.setList(objectList);
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(DdxhActivity.this, str);
                su.closeParentDialog();//必须
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DdxhActivity.this, error, Toast.LENGTH_LONG).show();
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

    private void onUrlLoad() {
        //todo 请填写  方法名
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(DdxhActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {

            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(DdxhActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DdxhActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }


    @OnClick({R.id.left, R.id.del1d, R.id.btn_ll, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.del1d:
                etGddh.setText("");
                etGddh.setSelection(0);
                etGddh.requestFocus();
                break;
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

    public class MyDdxhAdapter extends RecyclerView.Adapter<MyDdxhAdapter.ViewHolder> {


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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_wf_ddxh, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //0出货通知单别  1出通单号 2客户编号  3客户名称 4项目编号 5项目名称
            List<Object> objectList = (List<Object>) list.get(position);
            holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(DdxhActivity.this, DdxhDetailActivity.class);
                    intent.putExtra("data", objectList.toString());
                    intent.putExtra("id", objectList.get(1).toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
            holder.tvCtdb.setText(String.valueOf(objectList.get(0)));
            holder.tvCtdh.setText(String.valueOf(objectList.get(1)));
            holder.tvKhbh.setText(String.valueOf(objectList.get(2)));
            holder.tvKhmc.setText(String.valueOf(objectList.get(3)));
            holder.tvXmbh.setText(String.valueOf(objectList.get(4)));
            holder.tvXmmc.setText(String.valueOf(objectList.get(5)));

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.list_ddxh_ctdh)
            TextView tvCtdh;
            @BindView(R.id.list_ddxh_ctdb)
            TextView tvCtdb;
            @BindView(R.id.list_ddxh_xmbh)
            TextView tvXmbh;
            @BindView(R.id.list_ddxh_xmmc)
            TextView tvXmmc;
            @BindView(R.id.list_ddxh_khbh)
            TextView tvKhbh;
            @BindView(R.id.list_ddxh_khmc)
            TextView tvKhmc;
            @BindView(R.id.list_ddxh_content)
            LinearLayout linlayContent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
