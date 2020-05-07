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
    private String tmAll;
    private String tm;

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
                    scanCode();//
                }
                return false;
            }
        });
    }

    private void scanCode() {
        if (!etZj.getText().toString().contains("#")) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
        String strTm = etZj.getText().toString();
        String[] split = strTm.split("#");
        if (TextUtils.isEmpty(split[0]) || split.length < 2) {
            ToastUtils.showToast("请扫描正确格式的单号！");
            return;
        }
//        try {
//            split[0] = strTm.substring(strTm.indexOf("#") + 1, strTm.indexOf("#") + 12);
//            Log.e("######", split[0]);
//        } catch (IndexOutOfBoundsException e) {
//            ToastUtils.showToast("请扫描正确格式的主件码！");
//            return;
//        }
        etZj.setText(strTm);
        //todo 请填写  方法名
        String link = "hqzjtomysql";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("barcode", strTm);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(YsGlgxjlActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tmAll = strTm;
                tm = etZj.getText().toString();
                List<Object> objectList = (List<Object>) o;
                //0成品品号   1成品品名   2成品规格

                tvZjPh.setText(objectList.get(0).toString());
                tvZjPm.setText(objectList.get(1).toString());
                tvZjGg.setText(objectList.get(2).toString());

                adapter.setList(new ArrayList<>());
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(YsGlgxjlActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(YsGlgxjlActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
                etZj.setText("");
                etZj.setSelection(0);
                etZj.requestFocus();

            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    private void scanCodePl(String yjm, int position) {
        if (!yjm.contains("#")) {
            ToastUtils.showToast("请扫描正确格式的元件码！");
            return;
        }
        String[] split = yjm.split("#");
        if (TextUtils.isEmpty(split[0]) || split.length < 2) {
            ToastUtils.showToast("请扫描正确格式的元件码！");
            return;
        }
//        try {
//            split[0] = yjm.substring(yjm.indexOf("#") + 1, yjm.indexOf("#") + 12);
//            Log.e("######", split[0]);
//        } catch (IndexOutOfBoundsException e) {
//            ToastUtils.showToast("请扫描正确格式的主件码！");
//            return;
//        }

        //todo 请填写  方法名
        String link = "hqyjtomysql";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("barcode", yjm);
//        params.put("pmdsdocno2", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(YsGlgxjlActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;
                adapter.replaceItem(split[1], objectList, position, yjm);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(YsGlgxjlActivity.this, str);
                su.closeParentDialog();//必须
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
                if (TextUtils.isEmpty(tm) || TextUtils.isEmpty(tvZjPh.getText().toString())) {
                    ToastUtils.showToast("请扫描主件码！");
                    return;
                }
                List<Map<String, Object>> list = adapter.getList();
                if (ListUtils.isEmpty(list)) {
                    ToastUtils.showToast("请扫描元件码！");
                    return;
                }
                upLoading();
                break;
            case R.id.left:
                onBackPressed();
                break;

            case R.id.btn_qx:
                adapter.addItem("");
                break;
        }
    }

    private void upLoading() {
        List<Object> list1 = new ArrayList<>();

        list1.add(tvZjPh.getText().toString());
        list1.add(tmAll);//1成品码
        List<Object> list2 = new ArrayList<>();
        List<Map<String, Object>> adapterList = adapter.getList();
        for (Map<String, Object> map : adapterList) {
            List<Object> objectList = new ArrayList<>();
            List<Object> objectList1 = (List<Object>) map.get("data");
            if (!TextUtils.isEmpty(map.get("yjmAll").toString())) {
                objectList.add(objectList1.get(0));//0元件品号
                objectList.add(map.get("yjmAll"));//1元件码
//
                SharedPreferences sp = getSharedPreferences(
                        "config", Activity.MODE_PRIVATE);

                String name = sp.getString("name", null);
                objectList.add(name);//[2]操作人员
                list2.add(objectList);
            }

        }
        if (ListUtils.isEmpty(list2)) {
            ToastUtils.showToast("请扫描元件码！");
            return;

        }

        //todo 请填写  方法名
        String link = "bcglgx";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", list1);
        params.put("shjy2", list2);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(YsGlgxjlActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
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
                CommonUtil.showErrorDialog(YsGlgxjlActivity.this, str);
                su.closeParentDialog();//必须
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public class MyYjAdapter extends RecyclerView.Adapter<MyYjAdapter.ViewHolder> {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_glgxjl_add, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Map<String, Object> map = list.get(position);
            holder.etTm.setText(map.get("yjm").toString());
            //0元件品号  1元件品名   2元件规格
            List<Object> objectList = (List<Object>) map.get("data");
            holder.tvCpph.setText(String.valueOf(objectList.get(0)));
            holder.tvCppm.setText(String.valueOf(objectList.get(1)));
            holder.tvCpgg.setText(String.valueOf(objectList.get(2)));
            holder.etTm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                        //执行相关读取操作
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

        public void addItem(String yjm) {
            Map<String, Object> map = new HashMap<>();
            map.put("yjm", yjm);
            map.put("yjmAll", yjm);
            List<Object> objectList = new ArrayList<>();
            objectList.add("");
            objectList.add("");
            objectList.add("");
            map.put("data", objectList);
            list.add(map);
            setList(list);
        }

        public void replaceItem(String yjm, List<Object> objects, int position, String yjmAll) {
            Map<String, Object> map = new HashMap<>();
            map.put("yjm", yjm);
            map.put("yjmAll", yjmAll);
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
