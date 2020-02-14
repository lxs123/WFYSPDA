package com.cubertech.bhpda.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;
import com.cubertech.bhpda.view.LinearListView;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/8/27.
 */

public class InStorageActivity extends Activity {
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_rk_type)
    TextView tvRkType;
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.tv_ph)
    TextView tvPh;
    @BindView(R.id.tv_xhgg)
    TextView tvXhgg;
    @BindView(R.id.et_rqm)
    EditText etRqm;
    @BindView(R.id.tv_sl)
    EditText etSl;
    @BindView(R.id.tv_jhh)
    EditText etJhh;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.et_ck)
    TextView etCk;
    @BindView(R.id.et_ckh)
    TextView etCkh;
    @BindView(R.id.et_kw)
    TextView etKw;
    @BindView(R.id.tv_pc)
    EditText tvPc;
    @BindView(R.id.in_storage_list_title)
    TextView tvListTitle;
    @BindView(R.id.in_storage_list)
    LinearListView mStorage_list;
    ServiceUtils su = ServiceUtils.getInstance();
    private AccountInfo ai;
    private String ckStr;
    private String kwStr;
    private String rqmStr;
    private boolean isChange = false;
    private String id;
    private MyStorageAdapter adapter;
    private List<Map<String, String>> list1 = new ArrayList<>();
    private String rqm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rk_test);
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
        etRqm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                    //  scanCode("rqm");//

                    String rqm = etRqm.getText().toString();
                    if (!TextUtils.isEmpty(rqmStr) && !TextUtils.equals(rqm, rqmStr)) {
                        isChange = true;
                    }

                    scanCode("rqm");


                }
                return false;
            }
        });
        etCk.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
//                    scanCode("ck");//
                    String ck = etCk.getText().toString();
                    if (!TextUtils.isEmpty(ckStr) && !TextUtils.equals(ck, ckStr)) {
                        isChange = true;
                    }
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
//                    scanCode("kw");//
                    String kw = etKw.getText().toString();
                    if (!TextUtils.isEmpty(kwStr) && !TextUtils.equals(kw, kwStr)) {
                        isChange = true;
                    }
                }
                return false;
            }
        });
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
        adapter = new MyStorageAdapter();
        mStorage_list.setAdapter(adapter);
    }

    int anint = 0;

    @OnClick({R.id.left, R.id.btn_rk, R.id.btn_qx, R.id.tv_rk_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_qx:
            case R.id.left:
                onBackPressed();
                break;
            case R.id.tv_rk_type:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MP_Theme_alertDialog);
                String[] strings = {"采购入库"};

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
                } /*else if (TextUtils.isEmpty(etJhh.getText().toString())) {
                    ToastUtils.showToast("请输入计划号");
                    return;
                } else if (TextUtils.isEmpty(etCk.getText().toString())) {
                    ToastUtils.showToast("请扫描仓库码!");
                    return;
                } else if (TextUtils.isEmpty(etKw.getText().toString())) {
                    ToastUtils.showToast("请扫描库位码!");
                    return;
                }*/
                SharedPreferences sp = getSharedPreferences(
                        "config", Activity.MODE_PRIVATE);

                String name = sp.getString("name", null);
                List<Object> list = new ArrayList<>();
                list.add(id);//物料号==>id     0
                list.add(tvPm.getText().toString());//品名       1
                list.add(tvPh.getText().toString());//品号       2
                list.add(tvXhgg.getText().toString());//型号规格     3
                list.add(tvRkType.getText().toString().substring(0, 2));//入库类型   4
                list.add(rqm);//容器码    5
                list.add(etSl.getText().toString());//数量       6
                list.add(etJhh.getText().toString());//计划号    7
                list.add(etCk.getText().toString());//仓库       8
                list.add(etKw.getText().toString());//库位       9
                list.add(tvPc.getText().toString());//批次       10
                list.add(tvDate.getText().toString());//日期     11
                list.add(name);//用户名                       12
                list.add(isChange ? "1" : "0");//修改状态 1为修改,0为未修改       13
                Log.e("####3", list.toString());
                params.put("wlxx", list);
                params.put("strToken", "");
                params.put("strVersion", Utils.getVersions(this));
                params.put("strPoint", "");
                params.put("strActionType", "1001");
                su.callService(InStorageActivity.this, "instoragemsg", params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {
                        ToastUtils.showToast("入库成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFailure(String str) {
                        Toast.makeText(InStorageActivity.this, str, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(InStorageActivity.this, error, Toast.LENGTH_LONG).show();
                        su.closeParentDialog();//必须
                    }

                    @Override
                    public void onCompleted() {
                        su.closeParentDialog();//必须
                    }
                });

                break;
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 999) {
//            String ScanResult = "";
//            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//            if (intentResult != null) {
//                if (intentResult.getContents() == null) {
//                    //Toast.makeText(getActivity(),"内容为空",Toast.LENGTH_LONG).show();
//                    return;
//                } else {
//                    // ScanResult 为 获取到的字符串
//                    ScanResult = intentResult.getContents();
//                }
//            } else {
//                super.onActivityResult(requestCode, resultCode, data);
//            }
//            etTm.setText(ScanResult);
//
//            scanCode();
//        }
//    }

    private void scanCode(String type) {
        String link = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        boolean isReturn = false;
        switch (type) {
            case "wlm"://物料码;
//                GetContainerToIn
                link = "containertoin";
                params.put("wlm", etTm.getText().toString());

                break;
            case "rqm"://容器码;
                link = "containertorqm";
                params.put("rqm", etRqm.getText().toString());
                break;
            case "ckm"://仓库码;
                link = "";

                break;
            case "kwm"://库位码;
                link = "";

                break;
        }

        adapter.setList(null);
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
        su.callService(InStorageActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                switch (type) {
                    case "wlm"://物料码;
                        Log.e("#####", o.toString());
                        List<Object> list = (List<Object>) o;

                        tvPm.setText(list.get(2).toString());//品名
                        tvPh.setText(list.get(1).toString());//品号
                        tvXhgg.setText(list.get(3).toString());
                        etCk.setText(list.get(8) + "(" + list.get(4).toString().trim() + ")");
                        etKw.setText(list.get(9) + "(" + list.get(5).toString().trim() + ")");
                        etCkh.setText(list.get(4).toString().trim());
//                        etCk.setText(etTm.getText().toString());
                        etRqm.setText(list.get(6).toString());
                        ckStr = list.get(4).toString();
                        kwStr = list.get(5).toString();
                        rqmStr = list.get(6).toString();
                        Format format = new DecimalFormat("#0");
                        id = String.valueOf(format.format(list.get(0)));//id[0]
                        Log.e("####", id);
                        Calendar calendar = Calendar.getInstance();
//获取系统的日期
//年
                        int year = calendar.get(Calendar.YEAR);
//月
                        int month = calendar.get(Calendar.MONTH) + 1;
//日
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
//获取系统时间
//小时
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//分钟
                        int minute = calendar.get(Calendar.MINUTE);
//秒
                        int second = calendar.get(Calendar.SECOND);
                        tvPc.setText(year + "" + ((month < 10) ? "0" + month : month));
//                        tvDate.setText(year + "-" + ((month < 10) ? "0" + month : month) + "-" + ((day < 10) ? "0" + day : day) + "  " +
//                                ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute) + ":" + ((second < 10) ? "0" + second : second));
                        tvDate.setText(String.valueOf(year).substring(2, 4) + ((month < 10) ? "0" + month : month) + ((day < 10) ? "0" + day : day));
                        isChange = false;//初始化
                        etTm.setHint(etTm.getText().toString());
                        etTm.setText("");
                        etTm.setSelection(0);
//                        etTm.requestFocus();
                        etRqm.setSelection(etRqm.getText().toString().length());
                        etRqm.requestFocus();
                        if (ListUtils.getSize(list) > 9) {
                            List<Object> dataList2 = (List<Object>) list.get(10);
                            list1 = new ArrayList<>();
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
                                //Format format = new DecimalFormat("#0");
                                //id = String.valueOf(format.format(list.get(0)));//id[0]
                                list1.add(map);
                            }
//                        id = dataList.get(2).toString();
                            adapter.setList(list1);
                        }

                        break;
                    case "rqm"://容器码;

                        List<String> list1 = (List<String>) o;
                        rqm = etRqm.getText().toString();
                        Log.e("#######", list1.toString());
                        etCkh.setText(list1.get(4).trim().trim());//仓库码
                        etCk.setText(list1.get(2).trim() + "(" + list1.get(0).trim().trim() + ")");
                        etKw.setText(list1.get(3).trim() + "(" + list1.get(1).trim().trim() + ")");
                        etRqm.setText("");
                        etRqm.setHint(rqm);
//                        etRqm.setSelection(0);
//                        etRqm.requestFocus();
                        etSl.setSelection(etSl.getText().toString().length());
                        etSl.requestFocus();
                        break;
                    case "ckm"://仓库码;

                        break;
                    case "kwm"://库位码;

                        break;
                }
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(InStorageActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(InStorageActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
                switch (type) {
                    case "wlm"://物料码;
                        etTm.setText("");
                        etTm.setSelection(0);
                        etTm.requestFocus();

                        break;
                    case "rqm"://容器码;
                        etRqm.setText("");
                        etRqm.setSelection(0);
                        etRqm.requestFocus();
                        break;
                    case "ckm"://仓库码;

                        break;
                    case "kwm"://库位码;

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

    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
//        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
//                .setOrientationLocked(false)
//                .setCaptureActivity(ScanActivity.class);
//        Intent scanIntent = intentIntegrator.createScanIntent();
//        startActivityForResult(scanIntent, 999);
        onDelClick("wlm");
    }

    public void xiangjiRqm(View view) {
        onDelClick("rqm");
    }

    public void xiangjiCk(View view) {
        onDelClick("ckm");
    }

    public void xiangjiKw(View view) {
        onDelClick("kwm");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(InStorageActivity.this, R.style.MP_Theme_alertDialog);
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
                        etRqm.setSelection(0);
                        etRqm.requestFocus();
                        etRqm.setText("");
                        etCk.setText("");
                        etKw.setText("");
                        break;
                    case "ckm":
                        etCk.setText("");
                        break;
                    case "kwm":
                        etKw.setText("");
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
                tvListTitle.setVisibility(View.VISIBLE);
            } else {
                tvListTitle.setVisibility(View.GONE);
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
                view = LayoutInflater.from(InStorageActivity.this).inflate(R.layout.listitem_my, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Map<String, String> map = list.get(i);
            holder.imgSel.setImageResource(TextUtils.equals(map.get("sel"), "1") ? R.mipmap.ic_select : R.mipmap.ic_unselect);


            holder.tvCk.setText(map.get("ck"));
            holder.tvKw.setText(map.get("kw"));
            holder.tvRqm.setText(map.get("rqm"));
            holder.tvPc.setText(map.get("pc"));
            double p = Double.parseDouble(map.get("sl").trim());
            Format format = new DecimalFormat("#.0");//当前保留1位小数
            if (p == 0) {
                holder.tvSl.setText("0");
            } else {
                holder.tvSl.setText(format.format(p));
            }

            //holder.tvSl.setText(Integer.valueOf(map.get("sl").trim()).intValue());
            double s = Double.parseDouble(map.get("cksl").trim());
            if (s == 0) {
                holder.tvcksl.setText("0");
            } else {
                holder.tvcksl.setText(format.format(s));
            }

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

            public ViewHolder(View item) {
                ButterKnife.bind(this, item);
            }
        }
    }
}
