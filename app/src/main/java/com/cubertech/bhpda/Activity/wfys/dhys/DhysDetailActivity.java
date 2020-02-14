package com.cubertech.bhpda.Activity.wfys.dhys;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.printerB3s.BhgConfirmActivity;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/12.
 */

public class DhysDetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_shdh)
    TextView tvShdh;
    @BindView(R.id.tv_gysname)
    TextView tvGysname;
    @BindView(R.id.tv_gg)
    TextView tvGg;
    @BindView(R.id.tv_dysl)
    TextView tvDysl;
    @BindView(R.id.et_bhgsl)
    EditText etBhgsl;
    @BindView(R.id.et_bhgyy)
    EditText etBhgyy;
    @BindView(R.id.et_hgsl)
    EditText etHgsl;
    @BindView(R.id.tv_db)
    TextView tvDb;
    @BindView(R.id.tv_ph)
    TextView tvPh;
    @BindView(R.id.tv_pm)
    TextView tvPm;
    @BindView(R.id.tv_xh)
    TextView tvXh;
    @BindView(R.id.image_state)
    ImageView imageJl;
    @BindView(R.id.tv_jylx)
    TextView tvJylx;
    @BindView(R.id.tv_jymc)
    TextView tvJymc;
    @BindView(R.id.btn_dy)
    Button btnDy;
    private int position;
    private String hgsl;
    private String bHgsl;
    private ServiceUtils su = ServiceUtils.getInstance();
    private MyDhysDialogAdapter adapter;
    private boolean isListEmpty;

    //    /**
//     * //0供应商编号1采购供应商 名称2单别3单号4//品号5名称6//序号7规格
//     * 8//待验数量9判定结果 是否急料10合格数量11不合格数量12不良原因13状态
//     */
//    //0单别1单号2供应商编号3采购供应商4//序号 //5品号6名称7规格
// *  8//待验数量9判定结果 是否急料10合格数量11不合格数量12不良原因13状态
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhys_detail_zj);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        ArrayList<String> data = intent.getStringArrayListExtra("data");
        position = intent.getIntExtra("position", 0);
        tvShdh.setText(data.get(1));
        tvGysname.setText(data.get(3));
        tvGg.setText(data.get(7));
        try {
            double v = Double.parseDouble(data.get(8));
            tvDysl.setText(data.get(8));
        } catch (NumberFormatException e) {
            tvDysl.setText("0.00");
            ToastUtils.showToast("服务错误！");
            Log.e("#####", "NumberFormatException for dhysdetail");
        }
        tvDysl.setText(data.get(8));
        tvDb.setText(data.get(0));
        tvPh.setText(data.get(5));
        tvPm.setText(data.get(6));
        tvXh.setText(data.get(4));
        tvJylx.setText("");
        tvJymc.setText("");
        imageJl.setImageDrawable(TextUtils.equals(data.get(9).toString(), "Y")
                ? getResources().getDrawable(R.mipmap.checkbox3_on) : getResources().getDrawable(R.mipmap.checkbox3_off));
        etHgsl.setText(data.get(10));
        etBhgsl.setText(data.get(11));
        adapter = new MyDhysDialogAdapter();
        initCode();
        etHgsl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    double dysl = Double.parseDouble(TextUtils.isEmpty(tvDysl.getText().toString()) ? "0" : tvDysl.getText().toString());
                    double hgsl = Double.parseDouble(TextUtils.isEmpty(editable.toString()) ? "0" : editable.toString());
                    if (dysl - hgsl >= 0) {
                        etBhgsl.setText(String.valueOf(dysl - hgsl));
                    } else {
                        ToastUtils.showToast("合格数量不能大于待验数量！");
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });
        etBhgsl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //执行相关读取操作
                    double dysl = Double.parseDouble(TextUtils.isEmpty(tvDysl.getText().toString()) ? "0" : tvDysl.getText().toString());
                    double hgsl = Double.parseDouble(TextUtils.isEmpty(etHgsl.getText().toString()) ? "0" : etHgsl.getText().toString());
                    double bhgsl = Double.parseDouble(TextUtils.isEmpty(etBhgsl.getText().toString()) ? "0" : etBhgsl.getText().toString());
                    if (bhgsl > dysl) {
                        ToastUtils.showToast("不合格数量不能大于待验数量！");
                    } else if ((hgsl + bhgsl) > dysl) {
                        ToastUtils.showToast("不合格数量加上合格数量不能大于待验数量！");
                    } else if (Double.parseDouble(etBhgsl.getText().toString()) > 0) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(DhysDetailActivity.this, R.style.MP_Theme_alertDialog);
//                        builder.setTitle("不良原因");
//                        builder.setPositiveButton("确定", null);
//                        builder.setNegativeButton("取消", null);
//                        AlertDialog alertDialog = builder.create();
//                        View view1 = LayoutInflater.from(DhysDetailActivity.this).inflate(R.layout.dialog_edit_dhys_bhgsl, null);
//                        TextView tvSl = (TextView) view1.findViewById(R.id.dialog_sl);
//                        EditText edYY = (EditText) view1.findViewById(R.id.dialog_bhgyy);
//                        tvSl.setText(etBhgsl.getText().toString());
//                        edYY.setText(data.get(12));
//                        edYY.setSelection(edYY.getText().toString().length());
//                        edYY.requestFocus();
//                        alertDialog.setView(view1);
//                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                            @Override
//                            public void onShow(DialogInterface dialogInterface) {
//                                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                                button.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        if (TextUtils.isEmpty(edYY.getText().toString())) {
//                                            ToastUtils.showToast("请描述不良原因!");
//                                            return;
//                                        }
//                                        etBhgyy.setText(edYY.getText().toString());
//                                        dialogInterface.cancel();
//                                    }
//                                });
//                            }
//                        });
//                        alertDialog.show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(DhysDetailActivity.this, R.style.MP_Theme_alertDialog);
                        builder.setTitle("不良原因");
                        builder.setPositiveButton("确定", null);
                        builder.setNegativeButton("取消", null);
                        AlertDialog alertDialog = builder.create();
                        View view1 = LayoutInflater.from(DhysDetailActivity.this).inflate(R.layout.dialog_edit_dhys_bhgsl_new, null);
                        RecyclerView list = (RecyclerView) view1.findViewById(R.id.dialog_list);
                        alertDialog.setView(view1);
                        list.setLayoutManager(new LinearLayoutManager(DhysDetailActivity.this));
                        list.setAdapter(adapter);
                        btnDy.setVisibility(View.VISIBLE);

                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        List<Object> list1 = adapter.getList();
                                        double count = 0;
                                        double countAll = 0;
                                        double bhgsl = Double.parseDouble(etBhgsl.getText().toString());
                                        for (Object object : list1) {
                                            List<Object> objectList = (List<Object>) object;
                                            String s = String.valueOf(objectList.get(2));
                                            count = Double.parseDouble(TextUtils.isEmpty(s) ? "0" : s);
                                            countAll += Double.parseDouble(TextUtils.isEmpty(s) ? "0" : s);
                                            if (count > bhgsl) {
                                                ToastUtils.showToast("输入的数量不能大于不合格数量!");
                                                return;
                                            }
                                        }

                                        if (countAll == 0) {
                                            ToastUtils.showToast("请输入数量!");
                                            return;
                                        }
//                                        else if (count > bhgsl) {
//                                            ToastUtils.showToast("输入的数量不能大于不合格数量!");
//                                            return;
//                                        }
//                                        if (TextUtils.isEmpty(edYY.getText().toString())) {
//                                            ToastUtils.showToast("请描述不良原因!");
//                                            return;
//                                        }
//                                        etBhgyy.setText(edYY.getText().toString());
                                        dialogInterface.cancel();
                                    }
                                });
                            }
                        });
                        if (ListUtils.isEmpty(adapter.getList())) {
                            ToastUtils.showToast("检验项加载失败，请退出本界面，重新加载！");
                            btnDy.setVisibility(View.GONE);
                        } else
                            alertDialog.show();
                        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

                    } else {
                        btnDy.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 初始化接口
     */
    private void initCode() {
        String link = "hqshjyxm";
        HashMap<String, Object> params = new HashMap<String, Object>();
        adapter.setList(null);
        params.put("pmdsdocno", tvPh.getText().toString());
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(DhysDetailActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> objectList = (List<Object>) o;

                Log.e("#######", objectList.toString());
                List<Object> objectList2 = new ArrayList<>();
                if (ListUtils.isEmpty(objectList)) {
                    isListEmpty = true;
                    List<Object> objects = new ArrayList<>();
                    objects.add("");
                    objects.add("");
                    objects.add("0");//2
                    objects.add("");//3
                    objectList2.add(objects);
                } else {
                    isListEmpty = false;
                }

                for (Object o1 : objectList) {
                    List<Object> objects = (List<Object>) o1;
                    if (TextUtils.isEmpty(String.valueOf(objects.get(0))) && TextUtils.isEmpty(String.valueOf(objects.get(1)))) {
                        isListEmpty = true;
                    }
                    objects.add("0");//2
                    objects.add("");//3
                    objectList2.add(objects);
                }

                adapter.setList(objectList2);
            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(DhysDetailActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DhysDetailActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.btn_wcjy, R.id.btn_qx, R.id.btn_dy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
//                break;
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_dy:
                if (Double.parseDouble(etBhgsl.getText().toString()) == 0) {
                    ToastUtils.showToast("当前不存在需要打印的数据！");
                    return;
                }
                List<Object> list1 = adapter.getList();
                ArrayList<String> objList = new ArrayList<>();
                for (Object object : list1) {
                    List<Object> objectList1 = (List<Object>) object;
                    List<Object> objectList2 = new ArrayList<>();
                    if (TextUtils.isEmpty(String.valueOf(objectList1.get(1)))) {
                        ToastUtils.showToast("检验项不能为空！");
                        return;
                    }
                    if (Double.parseDouble(String.valueOf(objectList1.get(2))) > 0) {
                        for (Object o : objectList1) {
                            objectList2.add(o);
                        }

                        objList.add(String.valueOf(objectList1));
                    }
                }
                if (ListUtils.isEmpty(objList)) {
                    ToastUtils.showToast("请输入不良检验数量！");
                } else {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("data", objList);
                    intent.putExtra("wlmc", tvPm.getText().toString());
                    intent.putExtra("zzdw", tvGysname.getText().toString());
                    intent.setClass(DhysDetailActivity.this, BhgConfirmActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                }

                break;
            case R.id.btn_wcjy:

                double dyslDouble = Double.parseDouble(tvDysl.getText().toString());
                double hgslDouble = Double.parseDouble(etHgsl.getText().toString());
                double bhgslDouble = Double.parseDouble(etBhgsl.getText().toString());
                if (hgslDouble > dyslDouble) {
                    ToastUtils.showToast("合格数量不能大于待验数量！");
                    return;
                } else if (hgslDouble + bhgslDouble != dyslDouble) {
                    ToastUtils.showToast("请注意，合格数量加上不合格数量等于验收数量！");
                    return;
                }
//
                AlertDialog.Builder builder = new AlertDialog.Builder(DhysDetailActivity.this, R.style.MP_Theme_alertDialog);
                builder.setTitle("提示");
                hgsl = etHgsl.getText().toString();
                hgsl = TextUtils.isEmpty(hgsl) ? "0" : hgsl;
                bHgsl = etBhgsl.getText().toString();
                bHgsl = TextUtils.isEmpty(bHgsl) ? "0" : bHgsl;
                if (TextUtils.equals(hgsl, "0") && TextUtils.equals(bHgsl, "0")) {
                    builder.setMessage("当前合格数量与不合格数量都为0,确定检验?");

                } else {
                    builder.setMessage("确定检验?");
                }
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<Object> shjy1 = new ArrayList<>();
                        shjy1.add(tvDb.getText().toString());//0单别
                        shjy1.add(tvShdh.getText().toString());//1单号
                        shjy1.add(tvXh.getText().toString());//2序号
                        shjy1.add(tvPh.getText().toString());//3品号
//                        shjy1.add(tvPm.getText().toString());//4品名
                        shjy1.add(hgsl);//5合格数量
                        shjy1.add(bHgsl);//6不合格数量
                        shjy1.add(tvJylx.getText().toString());//7检验类型
                        SharedPreferences sp = DhysDetailActivity.this.getSharedPreferences(
                                "config", Activity.MODE_PRIVATE);

                        String name = sp.getString("name", null);
                        shjy1.add(name);//[8]操作人员
                        //yyyymmddhhmmss
                        long timeMillis = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String format = sdf.format(timeMillis);
                        shjy1.add(etBhgyy.getText().toString());//9不良原因
                        shjy1.add(format);//当前时间
                        List<Object> list = adapter.getList();
                        List<Object> objectList = new ArrayList<>();
                        for (Object object : list) {
                            List<Object> objectList1 = (List<Object>) object;
                            List<Object> objectList2 = new ArrayList<>();
                            if (Double.parseDouble(String.valueOf(objectList1.get(2))) > 0) {
                                for (Object o : objectList1) {
                                    objectList2.add(o);
                                }

                                objectList.add(objectList2);
                            }
                        }
                        shjy1.add(objectList);
                        String link = "bcshjy";
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("shjy", shjy1);
//                params.put("shjy2", shjy2);
                        params.put("strToken", "");
                        params.put("strVersion", Utils.getVersions(DhysDetailActivity.this));
                        params.put("strPoint", "");
                        params.put("strActionType", "1001");
                        su.callService(DhysDetailActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                            @Override
                            public void onResponse(Object o) {
                                ToastUtils.showToast("上传成功!");

                                //延迟退出
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent();
                                        intent.putExtra("hgsl", hgsl);
                                        intent.putExtra("bhgsl", bHgsl);
                                        intent.putExtra("bhgyy", etBhgyy.getText().toString());
                                        intent.putExtra("position", position);
                                        setResult(RESULT_OK, intent);
                                        onBackPressed();
                                    }
                                }, 500);
                            }

                            @Override
                            public void onFailure(String str) {
                                CommonUtil.showErrorDialog(DhysDetailActivity.this, str);
                                su.closeParentDialog();//必须
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(DhysDetailActivity.this, error, Toast.LENGTH_LONG).show();
                                su.closeParentDialog();//必须
                            }

                            @Override
                            public void onCompleted() {
                                su.closeParentDialog();//必须
                            }
                        });


                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();

                break;

        }
    }

    public class MyDhysDialogAdapter extends RecyclerView.Adapter<MyDhysDialogAdapter.ViewHolder> {

        private List<Object> list = new ArrayList<>();
        private ViewHolder holder;

        public List<Object> getList() {
            return list;
        }

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void setListNo(List<Object> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_edit_dhys_bhgsl_new, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Object> objectList = (List<Object>) list.get(position);
            holder.tvJyx.setText(String.valueOf(objectList.get(0) + "  " + String.valueOf(objectList.get(1))));
            if (isListEmpty) {
                holder.tvJyx.setText(holder.tvJyx.getText().toString().replace(" ", ""));
                holder.tvJyx.setHint("请输入检验项");
                holder.tvJyx.setEnabled(true);
            } else {
                holder.tvJyx.setEnabled(false);
            }
            if (TextUtils.equals(String.valueOf(objectList.get(2)), "0")) {
                holder.etSl.setHint(String.valueOf(objectList.get(2)));
            } else {
                holder.etSl.setText(String.valueOf(objectList.get(2)));
            }
            holder.etBhgyy.setText(String.valueOf(objectList.get(3)));
            holder.tvJyx.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    objectList.set(1, TextUtils.isEmpty(editable.toString()) ? "0" : editable.toString());
                    double bhgsl = Double.parseDouble(TextUtils.isEmpty(etBhgsl.getText().toString())
                            ? "0" : etBhgsl.getText().toString());
                    double editSl = Double.parseDouble(TextUtils.isEmpty(editable.toString())
                            ? "0" : editable.toString());
                    if (editSl > bhgsl) {
                        ToastUtils.showToast("输入的数量不能大于不合格数量！");
                    }
                    list.set(position, objectList);
                    setListNo(list);
                }
            });
            holder.etSl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    objectList.set(2, TextUtils.isEmpty(editable.toString()) ? "0" : editable.toString());
                    list.set(position, objectList);
                    setListNo(list);
                }
            });
            holder.etBhgyy.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    objectList.set(3, editable.toString());
                    list.set(position, objectList);
                    setListNo(list);
                }
            });
        }


        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup viewGroup) {
//            if (view == null) {
//                view = LayoutInflater.from(DhysDetailActivity.this).inflate(R.layout.listitem_edit_dhys_bhgsl_new, viewGroup,false);
//                holder = new ViewHolder(view);
//                view.setTag(holder);
//            } else {
//                holder = (ViewHolder) view.getTag();
//            }
//
//
//            View finalView = view;
//            return view;
//        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.dialog_jyx)
            EditText tvJyx;
            @BindView(R.id.dialog_sl)
            EditText etSl;
            @BindView(R.id.dialog_bhgyy)
            EditText etBhgyy;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
