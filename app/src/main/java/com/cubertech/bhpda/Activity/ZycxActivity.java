package com.cubertech.bhpda.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.ListUtils;
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

public class ZycxActivity extends AppCompatActivity {
    ServiceUtils su = ServiceUtils.getInstance();
    @BindView(R.id.et_tm)
    EditText etTm;
    @BindView(R.id.tv_zjh)
    TextView tvZjh;
    @BindView(R.id.tv_zyjh)
    TextView tvZyjh;
    @BindView(R.id.tv_lh)
    TextView tvLh;//品号
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.out_storage_list_title)
    TextView tvStorageTitle;
    @BindView(R.id.out_storage_list)
    LinearListView mStorageList;
    @BindView(R.id.tv_gg)
    TextView tvGg;
    @BindView(R.id.tv_sl)
    TextView tvSl;

    private AccountInfo ai;
    private MyStorageAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<>();
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zycx);
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
                link = "queryprocess";
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
        params.put("zyjhh", etTm.getText().toString());
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
        su.callService(ZycxActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                switch (type) {
                    case "wlm"://物料码;
                        Log.d("params", o.toString());
                        list = new ArrayList<>();
                        List<Object> dataList = (List<Object>) o;
                        List<Object> dataList1 = (List<Object>) dataList.get(0);
                        tvZjh.setText(dataList1.get(0).toString());
                        tvZyjh.setText(dataList1.get(1).toString());
                        tvLh.setText(dataList1.get(3).toString());
                        tvPm.setText(dataList1.get(2).toString());
                        tvGg.setText(dataList1.get(4).toString());
                        tvSl.setText(dataList1.get(5).toString());
                        //todo
//                        tvPh.setText(dataList1.get(1).toString());
//                        tvXhgg.setText(dataList1.get(2).toString());

                        List<Object> dataList2 = (List<Object>) dataList.get(1);
                        for (int i = 0; i < ListUtils.getSize(dataList2); i++) {
                            List<Object> objectList = (List<Object>) dataList2.get(i);
                            Map<String, String> map = new HashMap<>();
                            map.put("sel", "0");
//                            map.put("rqm", objectList.get(2).toString());//2容器码
                            map.put("lx", objectList.get(2).toString());
                            map.put("gxmc", objectList.get(0).toString());
                            map.put("time", TextUtils.equals(String.valueOf(objectList.get(1)), "null") ? "" : String.valueOf(objectList.get(1)));
                            map.put("sl", TextUtils.equals(String.valueOf(objectList.get(3)), "null") ? "" : String.valueOf(objectList.get(3)));
//                            map.put("cksl", "0");//出库数量
//                            map.put("id", objectList.get(5).toString().trim());//id
                            //Format format = new DecimalFormat("#0");
                            //id = String.valueOf(format.format(list.get(0)));//id[0]
                            list.add(map);
                        }
                        etTm.setText("");
                        etTm.setSelection(0);
                        etTm.requestFocus();
//                        id = dataList.get(2).toString();
                        adapter.setList(list);
                        break;

                }
            }

            @Override
            public void onFailure(String str) {
                Toast.makeText(ZycxActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ZycxActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.btn_qx})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ZycxActivity.this, R.style.MP_Theme_alertDialog);
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
                view = LayoutInflater.from(ZycxActivity.this).inflate(R.layout.listitem_zyjh, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Map<String, String> map = list.get(i);
            holder.imgSel.setImageResource(TextUtils.equals(map.get("sel"), "1") ? R.mipmap.ic_select : R.mipmap.ic_unselect);
            holder.tvGxmc.setText(map.get("gxmc"));
            holder.tvTime.setText(map.get("time"));
            holder.tvType.setText(map.get("lx"));
//            double p = Double.parseDouble(map.get("sl").trim());
//            Format format = new DecimalFormat("#.0");//当前保留1位小数
//            if (p == 0) {
//                holder.tvSl.setText("0");
//            } else {
            holder.tvSl.setText(map.get("sl"));
//            }

            //holder.tvSl.setText(Integer.valueOf(map.get("sl").trim()).intValue());
//            double s = Double.parseDouble(map.get("cksl").trim());
//            if (s == 0) {
//                holder.tvcksl.setText("0");
//            } else {
//                holder.tvcksl.setText(format.format(s));
//            }

            return view;
        }


        public class ViewHolder {
            @BindView(R.id.list_ck_img)
            ImageView imgSel;
            @BindView(R.id.tv_type)
            TextView tvType;
            @BindView(R.id.list_ck_content)
            LinearLayout linlayContent;
            @BindView(R.id.tv_cksl)
            TextView tvcksl;

            @BindView(R.id.tv_gxmc)
            TextView tvGxmc;
            @BindView(R.id.tv_sl)
            TextView tvSl;
            @BindView(R.id.tv_time)
            TextView tvTime;

            public ViewHolder(View item) {
                ButterKnife.bind(this, item);
            }
        }


    }
}
