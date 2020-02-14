package com.cubertech.bhpda.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/8/28.
 */

public class PdTestActivity extends Activity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.out_storage_list)
    RecyclerView mStorageList;
    ServiceUtils su = ServiceUtils.getInstance();
    private MyPdAdapter adapter;
    private EditText et_wlbm;
    private EditText et_kw;
    private TextView tv_xh;
    private String qdbh;
    private TextView tv_ck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pd_test);
        ButterKnife.bind(this);
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
                    scanCode();//
                }
                return false;
            }
        });
        adapter = new MyPdAdapter();
        mStorageList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(PdTestActivity.this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider));
        mStorageList.addItemDecoration(decoration);
        mStorageList.setAdapter(adapter);
    }

    private void scanCode() {
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        link = "hqpdqd";
        adapter.setObjectsList(null);
        params.put("QD001", etTm.getText().toString());
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
        su.callService(PdTestActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                Log.d("params", o.toString());
                List<Object> objectList = (List<Object>) o;
//                for (int i = 0; i < ListUtils.getSize(objectList); i++) {
//                    ArrayList<String> arrayList = (ArrayList<String>) objectList.get(i);
//                    arrayList.add("0");
//                    arrayList.add("0");
//                    arrayList.add("0");
//                    objectList.set(i, arrayList);
//                }
//                for (Object ob : objectList) {
//
//                }
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                adapter.setObjectsList(objectList);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(PdTestActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PdTestActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    private void scanCode(String state) {
        if (TextUtils.equals(state, "wlm")) {
            et_kw.setSelection(0);
            et_kw.requestFocus();
        } else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            String link = "";
            switch (state) {
                case "xh":
                    link = "hqpdqdbh";
                    params.put("sp", "");
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
                    su.callService(PdTestActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                        @Override
                        public void onResponse(Object o) {
                            Log.d("params", o.toString());
                            List<Object> list = (List<Object>) o;
                            switch (state) {
                                case "xh":
                                    qdbh = list.get(0).toString();
                                    break;
                            }
//                    List<Object> objectList = (List<Object>) o;
                        }

                        @Override
                        public void onFailure(String str) {
                            CommonUtil.showErrorDialog(PdTestActivity.this, str);
                            su.closeParentDialog();//必须
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(PdTestActivity.this, error, Toast.LENGTH_LONG).show();
                            su.closeParentDialog();//必须
                        }

                        @Override
                        public void onCompleted() {
                            su.closeParentDialog();//必须
                        }
                    });
                    break;
                case "kw":
                    if (!et_kw.getText().toString().contains("#")) {
                        ToastUtils.showToast("请扫描正确格式的库位码");
                        return;
                    }
                    String[] split = et_kw.getText().toString().split("#");

                    et_kw.setText(split[0]);
                    tv_ck.setText(split.length == 1 ? "" : split[1]);
                    break;
            }


//            adapter.setObjectsList(null);

        }

    }

    @OnClick({R.id.left, R.id.btn_rk, R.id.btn_qx, R.id.btn_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_rk:
                if (adapter == null || adapter.getObjectsList() == null || adapter.getObjectsList().size() <= 0) {
                    ToastUtils.showToast("无数据不能上传");
                    return;
                }
                HashMap<String, Object> params = new HashMap<String, Object>();
                String link = "bcpdqd";

                params.put("pdqd", adapter.getObjectsList());
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
                su.callService(PdTestActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {
                        ToastUtils.showToast("上传成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFailure(String str) {
                        CommonUtil.showErrorDialog(PdTestActivity.this, str);
                        su.closeParentDialog();//必须
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PdTestActivity.this, error, Toast.LENGTH_LONG).show();
                        su.closeParentDialog();//必须
                    }

                    @Override
                    public void onCompleted() {
                        su.closeParentDialog();//必须
                    }
                });
                break;
            case R.id.left:
//                break;

            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_now:
                onNowPdClick();
                break;
        }
    }

    private void onNowPdClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PdTestActivity.this, R.style.MP_Theme_alertDialog);
        builder.setTitle("实盘");
        View view = LayoutInflater.from(PdTestActivity.this).inflate(R.layout.dialog_now_pd_add, null);
        et_wlbm = (EditText) view.findViewById(R.id.et_wlbm);
        et_kw = (EditText) view.findViewById(R.id.et_kw);
        tv_xh = (TextView) view.findViewById(R.id.tv_xh);
        tv_ck = (TextView) view.findViewById(R.id.tv_ck);
        List<Object> objectsList = adapter.getObjectsList();
        String xh = "";
        for (int i = 0; i < ListUtils.getSize(objectsList); i++) {
            List<Object> list1 = (List<Object>) objectsList.get(i);
            xh = String.valueOf(list1.get(3));//
        }
        try {
            double i = Double.parseDouble(TextUtils.isEmpty(xh) ? "0" : (xh.contains("SP") ? xh.replace("SP", "") : "0"));
            double i1 = i + 1;
            Double aDouble = new Double(i1);
            String format1 = String.valueOf(aDouble.intValue());
            StringBuilder sb = new StringBuilder();
            sb.append("SP");
            for (int j = 0; j < 4 - format1.length(); j++) {
                sb.append("0");
            }
            sb.append(format1);
            tv_xh.setText(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EditText tv_ph = (EditText) view.findViewById(R.id.tv_ph);
        EditText et_spsl = (EditText) view.findViewById(R.id.et_spsl);
        Button del1 = (Button) view.findViewById(R.id.del1);
        Button del2 = (Button) view.findViewById(R.id.del2);
//        scanCode("xh");
        del1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_wlbm.setText("");
                et_wlbm.setSelection(0);
                et_wlbm.requestFocus();
            }
        });
        del2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_kw.setText("");
                et_kw.setSelection(0);
                et_kw.requestFocus();
            }
        });
        et_kw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        et_wlbm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    scanCode("wlm");//todo 此处逻辑有问题  lxs
                }
                return false;
            }
        });

        builder.setView(view);
        builder.setPositiveButton("上传", null);
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(qdbh)) {

//                            scanCode("xh");
                            return;
                        } else if (TextUtils.isEmpty(et_wlbm.getText().toString())) {
                            ToastUtils.showToast("请扫描物料编码!");
                            return;
                        } else if (TextUtils.isEmpty(et_kw.getText().toString())) {
                            ToastUtils.showToast("请扫描库位码!");
                            return;
                        } else if (TextUtils.isEmpty(tv_ck.getText().toString())) {
                            ToastUtils.showToast("请扫描库位码!");
                            return;
                        } else if (TextUtils.isEmpty(tv_ph.getText().toString())) {
                            ToastUtils.showToast("请输入批号!");
                            return;
                        }

                        List<Object> list = (List<Object>) objectsList.get(0);
                        List<Object> newList = new ArrayList<>();
                        for (int i = 0; i < ListUtils.getSize(list); i++) {
                            newList.add(list.get(i));
                        }

                        //[0]企业编号//[1]据点//[2]清单编号//[3]序号//[4]物料编号//[5]物料名称
                        //[6]物料规格//[7]仓库//[8]库位//[9]批次//[10]库存数量//[11]实盘数量//[12]差异数量<自己计算>
                        //[13]调整数量 <不显示>//[14]创建者//[15]盘点者//[16]调整者//[17]创建时间//[18]盘点时间
                        //[19]调整时间//[20]盘点状态//[21]盘点设备//[22]盘点调整状态//[23] 备用字段1
                        //[24]备用字段2//[25]备用字段3//[26]备用字段4//[27]备用字段5//[28]备用字段6
                        //[29]备用字段7//[30]备用字段8//[31]备用字段9//[32]备用字段10
                        list.set(4, et_wlbm.getText().toString());

                        list.set(2, qdbh);
                        list.set(3, tv_xh.getText().toString());
                        list.set(8, et_kw.getText().toString());
                        list.set(7, tv_ck.getText().toString());
                        list.set(9, tv_ph.getText().toString());
                        list.set(11, et_spsl.getText().toString());
                        TKApplication application = (TKApplication) getApplication();
                        list.set(15, application.getUserName());
                        objectsList.add(list);
                        adapter.setObjectsList(objectsList);

//                        List<Object> objectsList = adapter.getObjectsList();

                        dialogInterface.cancel();
                    }
                });
            }
        });

        dialog.show();
    }


    /**
     * @see Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
                typeStr = "盘点号";
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
        AlertDialog.Builder builder = new AlertDialog.Builder(PdTestActivity.this, R.style.MP_Theme_alertDialog);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) return;
        switch (requestCode) {
            case 22:
                ArrayList<String> list = data.getStringArrayListExtra("list");
                int position = data.getIntExtra("position", 0);
                List<Object> objectsList = adapter.getObjectsList();
                objectsList.set(position, list);
                adapter.setObjectsList(objectsList);
                break;
        }
    }

    public class MyPdAdapter extends RecyclerView.Adapter<MyPdAdapter.ViewHolder> {

        private List<Object> objectsList = new ArrayList<>();

        public void setObjectsList(List<Object> objectsList) {
            this.objectsList = objectsList;
            notifyDataSetChanged();
        }

        public List<Object> getObjectsList() {
            return objectsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_pd_test, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //[0]企业编号//[1]据点//[2]清单编号//[3]序号//[4]物料编号//[5]物料名称
            //[6]物料规格//[7]仓库//[8]库位//[9]批次//[10]库存数量//[11]实盘数量//[12]差异数量
            //[13]调整数量//[14]创建者//[15]盘点者//[16]调整者//[17]创建时间//[18]盘点时间
            //[19]调整时间//[20]盘点状态//[21]盘点设备//[22]盘点调整状态//[23] 备用字段1
            //[24]备用字段2//[25]备用字段3//[26]备用字段4//[27]备用字段5//[28]备用字段6
            //[29]备用字段7//[30]备用字段8//[31]备用字段9//[32]备用字段10

            ArrayList<String> list = (ArrayList<String>) objectsList.get(position);
            holder.tvQddh.setText(list.get(2).toString());//清单编号[2]
            holder.tvCk.setText(String.valueOf(list.get(7)));//仓库//[7]
            holder.tvKw.setText(String.valueOf(list.get(8)));//库位//[8]
            String xh = String.valueOf(list.get(3));
            holder.linlayBtnPdContent.setVisibility(xh.contains("SP") ? View.VISIBLE : View.GONE);
            // 容器[5]
            holder.tvPc.setText(list.get(9).toString());//批号//[9]
            holder.tvSl.setText(TextUtils.isEmpty(String.valueOf(list.get(11)))
                    ? String.valueOf(list.get(10)) : String.valueOf(list.get(11)));//数量//[11]  实盘数量为空填写库存数量
            holder.listCkContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (!TextUtils.isEmpty(String.valueOf(list.get(11)))) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("list", list);
                    intent.putExtra("position", position);
                    intent.setClass(PdTestActivity.this, PdTestDetailActivity.class);
                    startActivityForResult(intent, 22);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                    } else {
//                        onNowPdClick(String.valueOf(list.get(11)));
//                    }

                }
            });
            holder.btnNowPd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PdTestActivity.this, R.style.MP_Theme_alertDialog);
                    builder.setTitle("提示");
                    builder.setMessage("是否删除该条物料实盘信息？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            list.remove(position);
                            notifyDataSetChanged();
                            ToastUtils.showToast("删除成功！");
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();

                }
            });
//            holder.btnNowPd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    onNowPdClick(String.valueOf(list.get(11)));
//                }
//            });
        }


        @Override
        public int getItemCount() {
            return ListUtils.getSize(objectsList);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.list_ck_img)
            ImageView listCkImg;
            @BindView(R.id.tv_rqm)
            TextView tvQddh;
            @BindView(R.id.tv_ck)
            TextView tvCk;
            @BindView(R.id.tv_kw)
            TextView tvKw;
            @BindView(R.id.tv_pc)
            TextView tvPc;
            @BindView(R.id.tv_sl)
            TextView tvSl;
            @BindView(R.id.list_ck_content)
            LinearLayout listCkContent;
            @BindView(R.id.list_now_pd)
            Button btnNowPd;//删除按钮
            @BindView(R.id.list_now_pd_content)
            LinearLayout linlayBtnPdContent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
