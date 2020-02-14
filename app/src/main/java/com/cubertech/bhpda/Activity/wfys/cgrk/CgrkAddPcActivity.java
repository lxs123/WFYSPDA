package com.cubertech.bhpda.Activity.wfys.cgrk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2020/2/11.
 */

public class CgrkAddPcActivity extends AppCompatActivity {
    @BindView(R.id.cg_pc_list)
    RecyclerView mPcList;
    @BindView(R.id.cg_pc_add)
    LinearLayout linlayAddPc;
    private MyPcAdapter adapter;
    //  如果12=2&&13=P      ==》0
    //  12=3，13=P 14！=Y   ==》1
    //  12=3，13=P 14=Y     ==》2
    private String state;
    private String pc;
    private String ph;
    private int position;
    private String sl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cg_add_pc);
        ButterKnife.bind(this);
        mPcList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyPcAdapter();
        mPcList.setAdapter(adapter);
        Intent intent = getIntent();
        //                0到货单别，1到货单号，2供应商编号，3供应商名称，4序号，5品号，6品名,7规格，8交货仓库，
//                9仓库名称10合格数量，11验退数量 12条码类别，13品号属性，14是否供应商条码
// ，15入库数量 16状态
        String data = intent.getStringExtra("data");
        position = intent.getIntExtra("position", 0);
        ArrayList<String> pcList = intent.getStringArrayListExtra("pcList");
        if (!ListUtils.isEmpty(pcList)) {
            List<Object> objectList = new ArrayList<>();
            for (String s : pcList) {
                List list = ListUtils.toList(s);
                objectList.add(list);
            }
            adapter.setList(objectList);
        }
        List list = ListUtils.toList(data);
        String str12 = String.valueOf(list.get(12)).replace(" ", "");
        String str13 = String.valueOf(list.get(13)).replace(" ", "");
        String str14 = String.valueOf(list.get(14)).replace(" ", "");
        sl = String.valueOf(list.get(10));
        ph = String.valueOf(list.get(5));
        if (TextUtils.equals(str12, "2") && TextUtils.equals(str13, "P")) {
            state = "0";
        } else if (TextUtils.equals(str12, "3") && TextUtils.equals(str13, "P") && !TextUtils.equals(str14, "Y")) {
            state = "1";
        } else if (TextUtils.equals(str12, "3") && TextUtils.equals(str13, "P") && TextUtils.equals(str14, "Y")) {
            state = "2";
        }
    }

    @OnClick({R.id.left, R.id.btn_submit, R.id.btn_cancel, R.id.cg_pc_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_submit:
                if (adapter.getNum() == 0) {
                    ToastUtils.showToast("请输入数量！");
                    return;
                } else if (adapter.getNum() > Double.parseDouble(sl)) {
                    ToastUtils.showToast("输入的数量不能大于入库数量！");
                    return;
                } else if (adapter.getDataFinished()) {
                    ToastUtils.showToast("批次，库位不能为空，请扫描！");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("pcList", adapter.getData());
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.left:
            case R.id.btn_cancel:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(CgrkAddPcActivity.this, R.style.MP_Theme_alertDialog);
                builder1.setTitle("提示");
                builder1.setMessage("是否保存并退出批次生成？");
                builder1.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (adapter.getNum() == 0) {
                            ToastUtils.showToast("请输入数量！");
                            return;
                        } else if (adapter.getDataFinished()) {
                            ToastUtils.showToast("批次，库位不能为空，请扫描！");
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra("pcList", adapter.getData());
                        intent.putExtra("position", position);
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                builder1.show();

                break;
            case R.id.cg_pc_add:
                List<Object> objects = new ArrayList<>();
                long timeMillis = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String format = sdf.format(timeMillis);
                if (TextUtils.isEmpty(state)) {
                    ToastUtils.showToast("数据异常！");
                    return;
                }
                switch (state) {
                    case "0":
                        pc = ph + format;
                        objects.add(pc);//批次
                        break;
                    case "1":
                        List<Object> list = adapter.getList();
                        if (ListUtils.isEmpty(list)) {
                            pc = ph + format + "0001";
                            objects.add(pc);//批次
                            break;
                        }
                        List<Object> objectList = (List<Object>) list.get(ListUtils.getSize(list) - 1);
                        String s = String.valueOf(objectList.get(0));
                        if (TextUtils.isEmpty(s)) {
                            pc = ph + format + "0001";
                            objects.add(pc);//批次
                            break;
                        }

                        try {
                            String strSerial = s.substring(s.length() - 4, s.length());//
                            int serial = Integer.parseInt(strSerial);
                            int i = serial + 1;
                            StringBuilder builder = new StringBuilder();
                            for (int j = 0; j < 4 - String.valueOf(i).length(); j++) {
                                builder.append("0");
                            }
                            builder.append(i);
                            pc = ph + format + builder;
                            objects.add(pc);//批次
                        } catch (Exception e) {
                            pc = ph + format + "0001";
                            objects.add(pc);//批次
                            e.printStackTrace();
                        }

                        break;
                    case "2":
                        pc = "";
                        objects.add(pc);//批次
                        break;

                }
                objects.add("");//库位
                objects.add("");//数量
                objects.add(state);//状态
                adapter.addItems(objects);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public class MyPcAdapter extends RecyclerView.Adapter<MyPcAdapter.ViewHolder> {

        private List<Object> list = new ArrayList<>();

        public List<Object> getList() {
            return list;
        }

        public ArrayList<String> getData() {
            ArrayList<String> arrayList = new ArrayList<>();
            for (Object o : list) {
                arrayList.add(o.toString());
            }
            return arrayList;
        }

        double count;

        public double getNum() {
            count = 0;
            for (Object o : list) {
                List<Object> objects = (List<Object>) o;
                String s = String.valueOf(objects.get(2)).replace(" ", "");
                double i = Double.parseDouble(TextUtils.isEmpty(s) ? "0" : s);
                count = count + i;
            }
            return count;
        }

        boolean isData = false;

        public boolean getDataFinished() {
            for (Object o : list) {
                List<Object> objects = (List<Object>) o;
                if (TextUtils.isEmpty(String.valueOf(objects.get(0)))) {
                    isData = true;
                    break;
                } else if (TextUtils.isEmpty(String.valueOf(objects.get(1)))) {
                    isData = true;
                    break;
                }

            }
            return isData;
        }

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addItems(List<Object> list) {
            this.list.add(list);
            Log.e("#####", this.list.toString());
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_cg_add_pc, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            List<Object> objectList = (List<Object>) list.get(position);
            switch (String.valueOf(objectList.get(3))) {
                case "0":
                case "1":
                    holder.etPc.setEnabled(false);
                    break;
                case "2":
                    holder.etPc.setEnabled(true);

                    break;
            }
            holder.etPc.setTag(position);
            holder.etKw.setTag(position);
            holder.etSl.setTag(position);

            holder.etPc.setText(String.valueOf(objectList.get(0)));
            holder.etKw.setText(String.valueOf(objectList.get(1)));
            holder.etSl.setText(String.valueOf(objectList.get(2)));
            holder.dialogDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    if (ListUtils.getSize(list) == 0) {
                        notifyDataSetChanged();
                    }
                    if (TextUtils.equals(String.valueOf(objectList.get(3)), "1")) {//删除后，重新赋值批号流水
                        int serial = 0;
                        for (int i = 0; i < ListUtils.getSize(list); i++) {
                            try {
                                int anInt = serial + 1;
                                serial = anInt;
                                StringBuilder builder = new StringBuilder();
                                for (int j = 0; j < 4 - String.valueOf(i).length(); j++) {
                                    builder.append("0");
                                }
                                builder.append(anInt);
                                List<Object> objects = (List<Object>) list.get(i);
                                String s = String.valueOf(objects.get(0));
                                String substring = s.substring(0, s.length() - 4);
                                StringBuffer buffer = new StringBuffer();
                                buffer.append(substring + builder);
                                objects.set(0, buffer);
                                list.set(i, objects);
                                notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else
                        notifyDataSetChanged();
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
                    objectList.set(2, holder.etSl.getText().toString());
                    int position = (int) holder.etSl.getTag();
                    list.set(position, objectList);
                }
            });
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int position = (int) holder.etKw.getTag();
                    objectList.set(1, holder.etKw.getText().toString());
                    list.set(position, objectList);
                }
            };

            holder.etKw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        holder.etKw.addTextChangedListener(textWatcher);
                    } else {
                        holder.etKw.removeTextChangedListener(textWatcher);
                    }
                }
            });

            holder.etPc.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.equals(String.valueOf(objectList.get(3)), "2")) {
                        int position = (int) holder.etPc.getTag();
                        objectList.set(0, holder.etPc.getText().toString());
                        list.set(position, objectList);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.dialog_pc)
            EditText etPc;
            @BindView(R.id.dialog_kw)
            EditText etKw;
            @BindView(R.id.dialog_sl)
            EditText etSl;
            @BindView(R.id.dialog_delete)
            Button dialogDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
