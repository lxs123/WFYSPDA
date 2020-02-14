package com.cubertech.bhpda.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.TransferPackage.XHCZStockActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;
import com.cubertech.bhpda.view.LinearListView;

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
 * Created by Administrator on 2019/8/27.
 */

public class OutStorageActivity extends Activity {
    ServiceUtils su = ServiceUtils.getInstance();
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.tv_ph)
    TextView tvPh;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.tv_rk_type)
    TextView tvRkType;
    @BindView(R.id.tv_sl)
    EditText etSl;
    @BindView(R.id.tv_jhh)
    EditText etJhh;
    @BindView(R.id.out_storage_list)
    LinearListView mStorageList;
    @BindView(R.id.out_storage_list_title)
    TextView tvStorageTitle;
    private AccountInfo ai;
    private MyStorageAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<>();
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ck_test);
        ButterKnife.bind(this);
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        ai = bundle.getParcelable("account");
        etTm.setSelection(0);
        etTm.requestFocus();
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
//        mStorageList.setLayoutManager(new LinearLayoutManager(this){
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        });
        adapter = new MyStorageAdapter();
        mStorageList.setAdapter(adapter);
    }

    private void scanCode(String type) {
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        switch (type) {
            case "wlm"://物料码;
                link = "containertoout";
                break;
            case "rqm"://容器码;
                link = "";
                break;
            case "ckm"://仓库码;
                link = "";
                break;
            case "kwm"://库位码;
                link = "";
                break;
        }
        adapter.setList(null);
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
        su.callService(OutStorageActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                switch (type) {
                    case "wlm"://物料码;
                        Log.d("params", o.toString());
                        list = new ArrayList<>();
                        List<Object> dataList = (List<Object>) o;
                        List<Object> dataList1 = (List<Object>) dataList.get(0);
                        tvPm.setText(dataList1.get(0).toString());
                        tvPh.setText(dataList1.get(1).toString());
                        tvXhgg.setText(dataList1.get(2).toString());

                        List<Object> dataList2 = (List<Object>) dataList.get(1);
                        for (int i = 0; i < ListUtils.getSize(dataList2); i++) {
                            List<Object> objectList = (List<Object>) dataList2.get(i);
                            Map<String, String> map = new HashMap<>();
                            map.put("sel", "0");
                            map.put("rqm", objectList.get(2).toString());//2容器码
                            map.put("ck", objectList.get(0).toString());
                            map.put("kw", objectList.get(1).toString());
                            map.put("pc", objectList.get(3).toString());
                            map.put("sl", objectList.get(4).toString());
                            map.put("cksl", "0");//出库数量
                            map.put("id", objectList.get(5).toString().trim());//id
                            map.put("ids", String.valueOf(i + 1));//出库界面显示使用
                            //Format format = new DecimalFormat("#0");
                            //id = String.valueOf(format.format(list.get(0)));//id[0]
                            list.add(map);
                        }
//                        id = dataList.get(2).toString();
                        etTm.setHint(etTm.getText().toString());
                        etTm.setText("");
//                        etTm.setSelection(0);
//                        etTm.requestFocus();
                        etSl.setSelection(etSl.getText().toString().length());
                        etSl.requestFocus();
                        adapter.setList(list);
                        break;

                }
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(OutStorageActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(OutStorageActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
                switch (type) {
                    case "wlm"://物料码;
                        etTm.setText("");
                        etTm.setSelection(0);
                        etTm.requestFocus();

                        break;

                }
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    /**
     * @see Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    private int anint;

    @OnClick({R.id.left, R.id.tv_rk_type, R.id.btn_rk, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.tv_rk_type:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MP_Theme_alertDialog);
                String[] strings = {"生产领料"};

                builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        anint = i;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvRkType.setText(strings[anint]);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.btn_rk:
                HashMap<String, Object> params = new HashMap<String, Object>();
                if (TextUtils.isEmpty(tvPm.getText().toString())) {
                    ToastUtils.showToast("请扫描物料码!");
                    return;
                } else if (TextUtils.isEmpty(tvPh.getText().toString())) {
                    ToastUtils.showToast("请扫描物料码!");
                    return;

                } else if (TextUtils.isEmpty(etSl.getText().toString())) {
                    ToastUtils.showToast("请输入数量!");
                    return;
                }/*else if (TextUtils.isEmpty(etJhh.getText().toString())) {
                    ToastUtils.showToast("请输入计划号!");
                    return;
                }*/
                List<Object> Wlxx = new ArrayList<>();
                List<Object> list = new ArrayList<>();
                list.add(etTm.getText().toString());//物料号  id   0
                list.add(tvPm.getText().toString());//品名       1
                list.add(tvPh.getText().toString());//品号       2
                list.add(tvXhgg.getText().toString());//型号规格     3
                list.add(tvRkType.getText().toString());//入库类型   4
                list.add(etSl.getText().toString());//数量      5
                list.add(etJhh.getText().toString());//计划号    6
                Wlxx.add(list);
                List<Object> objectList = new ArrayList<>();

                float cksl = Float.parseFloat(etSl.getText().toString());//出库数量
//list [
//       list1,
//       list2[list3]
//      ]
                float zs = 0;//勾选的总数据
                for (int i = 0; i < ListUtils.getSize(adapter.getList()); i++) {
                    Map<String, String> map = adapter.getList().get(i);
                    if (TextUtils.equals(map.get("sel"), "1")) {
                        List<Object> objectList1 = new ArrayList<>();
                        objectList1.add(map.get("ck"));//仓库    0
                        objectList1.add(map.get("kw"));//库位     1
                        objectList1.add(map.get("rqm"));//容器码   2
                        objectList1.add(map.get("pc"));//批次      3
                        objectList1.add(map.get("sl"));//数量      4
                        objectList1.add(map.get("cksl"));//出库数量[5]

                        objectList1.add(map.get("id").toString().trim());//id
                        zs += Float.parseFloat(map.get("cksl").trim());
                        objectList.add(objectList1);
                    }
                }

                if (cksl > zs) {
                    ToastUtils.showToast("数量不匹配!");
                    return;
                }
                Wlxx.add(objectList);
//                list.add(etCk.getText().toString());//仓库       8
//                list.add(etKw.getText().toString());//库位       9
//                list.add(tvPc.getText().toString());//批次       10
//                list.add(tvDate.getText().toString());//日期     11
//                list.add("admin");//用户名                       12
//                list.add(isChange ? "1" : "0");//修改状态 1为修改,0为未修改       13
                if (ListUtils.isEmpty(objectList)) {
                    ToastUtils.showToast("请选择需要出库的物料!");
                    return;
                }
                params.put("wlxx", Wlxx);
                params.put("strToken", "");
                params.put("strVersion", Utils.getVersions(this));
                params.put("strPoint", "");
                params.put("strActionType", "1001");
                su.callService(OutStorageActivity.this, "outstoragemsg", params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {
                        ToastUtils.showToast("出库成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFailure(String str) {
                        Toast.makeText(OutStorageActivity.this, str, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(OutStorageActivity.this, error, Toast.LENGTH_LONG).show();
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

    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
//        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
//                .setOrientationLocked(false)
//                .setCaptureActivity(ScanActivity.class);
//        Intent scanIntent = intentIntegrator.createScanIntent();
//        startActivityForResult(scanIntent, 999);
        onDelClick("wlm");
    }

    private void onDelClick(String type) {
        String typeStr = "";
        switch (type) {
            case "wlm":
                typeStr = "物料码";
                break;
            case "rqm":
                typeStr = "容器码";
                break;
            case "ckm":
                typeStr = "仓库码";
                break;
            case "kwm":
                typeStr = "库位码";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(OutStorageActivity.this, R.style.MP_Theme_alertDialog);
        builder.setTitle("提示");
        builder.setMessage("是否清空" + typeStr + "?");
        builder.setPositiveButton("清空", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (type) {
                    case "wlm":
                        etTm.setText("");
                        etTm.setSelection(0);
                        etTm.requestFocus();
                        break;
                    case "rqm":
                        break;
                    case "ckm":
                        break;
                    case "kwm":
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public class MyStorageAdapter extends BaseAdapter {
        private List<Map<String, String>> list = new ArrayList<>();
        private ViewHolder holder;

        public void setList(List<Map<String, String>> list) {
            this.list = list;
            if (ListUtils.getSize(list) != 0) {
                tvStorageTitle.setVisibility(View.VISIBLE);
            } else {
                tvStorageTitle.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }

        public List<Map<String, String>> getList() {
            return list;
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(list);
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(OutStorageActivity.this).inflate(R.layout.listitem_ck_test, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Map<String, String> map = list.get(i);
            holder.imgSel.setImageResource(TextUtils.equals(map.get("sel"), "1") ? R.mipmap.ic_select : R.mipmap.ic_unselect);
            holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    map.put("sel", TextUtils.equals(map.get("sel"), "1") ? "0" : "1");
                    if (TextUtils.equals(map.get("sel"), "1")) {
                        map.put("cksl", map.get("sl").toString());
                    } else {
                        map.put("cksl", "0");
                    }

                    list.set(i, map);
                    notifyDataSetChanged();
                }
            });
            holder.linlayContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //ToastUtils.showToast("测试");
                    if (TextUtils.equals(map.get("sel"), "1")) {
                        //弹出窗口修改数量
                        AlertDialog.Builder builder = new AlertDialog.Builder(OutStorageActivity.this, R.style.MP_Theme_alertDialog);
                        View view1 = LayoutInflater.from(OutStorageActivity.this).inflate(R.layout.dialog_cksl, null);
                        EditText etppsl = (EditText) view1.findViewById(R.id.et_ppsl);
                        TextView tvmz = (TextView) view1.findViewById(R.id.tv_mz);
                        TextView nums = (TextView) view1.findViewById(R.id.tvnums);

                        Button btn_jgry_add = (Button) view1.findViewById(R.id.btn_jgry_add);
                        Button btn_jgry_cancel = (Button) view1.findViewById(R.id.btn_jgry_cancel);

                        double sp = Double.parseDouble(map.get("cksl").trim());
//                        Format format = new DecimalFormat("#.0");//当前保留1位小数

                        nums.setText(map.get("ids").trim());

                        etppsl.setText(String.valueOf(sp));

                        AlertDialog dialog = builder.create();
                        btn_jgry_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialog.dismiss();
                            }
                        });
                        btn_jgry_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {

                                    if (etppsl.getText().toString().trim().replaceAll("^(0+)", "").equals("")) {
                                        ToastUtils.showToast("添加的数值不正确！");
                                        return;
                                    }

                                    float ppl = Float.parseFloat(etppsl.getText().toString().trim().replaceAll("^(0+)", ""));//匹配量
                                    float p = Float.parseFloat(map.get("sl").trim());
                                    //float ysl=Float.parseFloat(objectList.get(8).toString().trim());//原数量
                                    if (ppl > p) {
                                        ToastUtils.showToast("出库数量超出库存量！");
                                        return;
                                    }
                                    if (ppl == 0) {
                                        ToastUtils.showToast("出库数量必须大于0！");
                                        return;
                                    }

                                    map.put("cksl", String.valueOf(ppl));

                                    list.set(i, map);
                                    notifyDataSetChanged();
                                    //mStorageList.setOverScrollMode(3);

                                   /* 1.recyclerView.setFocusableInTouchMode(false);

                                    2.recyclerView.setFocusable(false);

                                    3.setHasFixedSize(true);*/

                                    dialog.dismiss();
                                } catch (Exception e) {
                                    ToastUtils.showToast("添加失败,请检查填写数据是否正常!");
                                    e.printStackTrace();
                                }

                            }
                        });
                        dialog.setView(view1);
                        dialog.show();
                    }
                    return true;
                }
            });
           /* holder.imgSel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    map.put("sel", TextUtils.equals(map.get("sel"), "1") ? "0" : "1");
                    list.set(i, map);
                    notifyDataSetChanged();
                }
            });*/
            holder.tvCk.setText(map.get("ck"));
            holder.tvKw.setText(map.get("kw"));
            holder.tvRqm.setText(map.get("rqm"));
            holder.tvPc.setText(map.get("pc"));
            double p = Double.parseDouble(map.get("sl").trim());
//            Format format = new DecimalFormat("#.0");//当前保留1位小数
            if (p == 0) {
                holder.tvSl.setText("0");
            } else {
                holder.tvSl.setText(String.valueOf(p));
            }

            //holder.tvSl.setText(Integer.valueOf(map.get("sl").trim()).intValue());
            double s = Double.parseDouble(map.get("cksl").trim());
            if (s == 0) {
                holder.tvcksl.setText("0");
            } else {
                holder.tvcksl.setText(String.valueOf(s));
            }
            holder.tvnum.setText(map.get("ids").trim());

            return view;
        }

        public class ViewHolder {
            @BindView(R.id.list_ck_img)
            ImageView imgSel;
            @BindView(R.id.tv_rqm)
            TextView tvRqm;
            @BindView(R.id.tv_ck)
            TextView tvCk;
            @BindView(R.id.tv_kw)
            TextView tvKw;
            @BindView(R.id.tv_pc)
            TextView tvPc;
            @BindView(R.id.tv_sl)
            TextView tvSl;
            @BindView(R.id.list_ck_content)
            LinearLayout linlayContent;
            @BindView(R.id.tv_cksl)
            TextView tvcksl;
            @BindView(R.id.tv_num)
            TextView tvnum;

            public ViewHolder(View item) {
                ButterKnife.bind(this, item);
            }
        }
    }
}
