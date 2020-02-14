package com.cubertech.bhpda.Activity.wfys.wgrk;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
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
 * 钣金完工入库
 * Created by Administrator on 2019/12/31.
 */

public class BjWgrkActivity extends AppCompatActivity {
    @BindView(R.id.et_value)
    EditText etTm;
    @BindView(R.id.bjrk_recycle)
    RecyclerView mList;
    private MyBjrkAdapter adapter;
    ServiceUtils su = ServiceUtils.getInstance();
    private String tm;
    private List<Object> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_wgrk);
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
        DividerItemDecoration decoration = new DividerItemDecoration(BjWgrkActivity.this, DividerItemDecoration.VERTICAL);
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
        String link = "bjwgrkxx";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("code", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(BjWgrkActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();

                List<Object> objectList = (List<Object>) o;
                Log.e("######", objectList.toString());
                boolean isEquest = false;
                for (Object obj : list) {
                    List<Object> objectList1 = (List<Object>) obj;
                    if (objectList1.containsAll(objectList)) {
                        isEquest = true;
                        break;
                    }
                }
                if (!isEquest) {
                    list.add(objectList);
                    adapter.setList(list);
                }


                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(BjWgrkActivity.this, str, Toast.LENGTH_LONG).show();
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
                Toast.makeText(BjWgrkActivity.this, error, Toast.LENGTH_LONG).show();
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
// ,8 工单单号,9 项目编号
        List<Object> list = adapter.getList();
        if (ListUtils.isEmpty(list)) {
            ToastUtils.showToast("请扫描单号！");
        }
        List<Object> shjy = new ArrayList<>();
        int count = 1;
        for (Object o : list) {
            List<Object> objectList = (List<Object>) o;
            List<Object> objectList1 = new ArrayList<>();
            objectList1.add("5815");//0
            objectList1.add("11");//1
            objectList1.add("");//2
            objectList1.add(objectList.get(0));//3工作中心
            objectList1.add(objectList.get(9));//项目号4
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 4 - String.valueOf(count).length(); i++) {
                builder.append("0");
            }
            builder.append(count);
            count++;
            objectList1.add(builder.toString());//序号5
            objectList1.add(objectList.get(1));//6品号
            String s = String.valueOf(objectList.get(4));
            String[] split = s.split("#");
            objectList1.add(split[0]);//7入库库位
            objectList1.add(objectList.get(6));//8完工数量
            objectList1.add(0);//9
            objectList1.add(objectList.get(6));//10验收数量
            objectList1.add(objectList.get(7));//11单别
            objectList1.add(objectList.get(8));//12单号
            objectList1.add("Y");//13
            objectList1.add("********************");//14批号
            objectList1.add("##########");//15库位
            objectList1.add("");//16
            shjy.add(objectList1);

        }
        //todo 请填写  方法名
        String link = "bcbjwgrk";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("shjy", shjy);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(BjWgrkActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();

                List<Object> objectList = (List<Object>) o;
                Log.e("######", objectList.toString());
                boolean isEquest = false;
                for (Object obj : BjWgrkActivity.this.list) {
                    List<Object> objectList1 = (List<Object>) obj;
                    if (objectList1.containsAll(objectList)) {
                        isEquest = true;
                        break;
                    }
                }
                if (!isEquest) {
                    BjWgrkActivity.this.list.add(objectList);
                    adapter.setList(BjWgrkActivity.this.list);
                }


                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(BjWgrkActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BjWgrkActivity.this, error, Toast.LENGTH_LONG).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_other_wgrk, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Object> objectList = (List<Object>) list.get(position);
//0 工作中心,1 品号,2 品名,3 规格,4入库库位,5 预计产量,6 已完工量,'完工数量' 手动输入,7 工单单别
// ,8 工单单号,9 项目编号
            holder.tvGg.setText(String.valueOf(objectList.get(3)));
            holder.tvPm.setText(String.valueOf(objectList.get(2)));
            holder.tvSl.setText(String.valueOf(objectList.get(5)));
            holder.tvPh.setText(String.valueOf(objectList.get(1)));
            String strCkKw = String.valueOf(objectList.get(4));
            String[] split = strCkKw.split("#");
            holder.tvCk.setText(split == null ? "" : split[0]);
            holder.tvKw.setText((split == null || split.length == 1) ? "" : split[1]);
//            holder.tvCk.setText(String.valueOf(objectList.get(11)));
//            holder.tvKw.setText(String.valueOf(objectList.get(12)));
            holder.tvSl.setText(String.valueOf(objectList.get(6)));
            holder.linlaySlContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BjWgrkActivity.this, R.style.MP_Theme_alertDialog);
                    builder.setTitle("入库数量填写");
                    View view1 = LayoutInflater.from(BjWgrkActivity.this).inflate(R.layout.dialog_edit_sl, null);
                    EditText etSl = (EditText) view1.findViewById(R.id.dialog_sl);
                    TextView tvTitle = (TextView) view1.findViewById(R.id.dialog_spsl_title);
                    tvTitle.setText("入库数量");
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
                                    holder.tvSl.setText(etSl.getText().toString());
                                    objectList.set(6, (etSl.getText().toString()));
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}

