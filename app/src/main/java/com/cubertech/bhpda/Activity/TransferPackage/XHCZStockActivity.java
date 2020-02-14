package com.cubertech.bhpda.Activity.TransferPackage;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.data.DbData;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 库存列表
 */
public class XHCZStockActivity extends AppCompatActivity {
    @NonNull
    private final DbDataSource mDbRepository;

    @BindView(R.id.xhcz_stock_list)
    ListView mStockList;

    private AccountInfo ai;
    private SiteService siteService;
    private MyStockAdapter adapter;

    private AlertDialog.Builder builder;

    private EditText et_tm;

    @BindView(R.id.et_value)
    EditText et_value;
    @BindView(R.id.tv_yjl)
    TextView tv_yjl;

    @BindView(R.id.cb_xjxc)
    CheckBox cbxjxc;
    @BindView(R.id.tv_ppsl)
    TextView tv_ppsl;

    String PH;
    Map<String,String> MapSL;

    String ID;//通知序号
    int IDS=-1;
    public XHCZStockActivity() {
        this.mDbRepository = DbRepository.getInstance(null,DbLocalDataSource.getInstance(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xhczstock);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        ID = intent.getStringExtra("ID");//通知序号
        IDS= intent.getIntExtra("IDS",-1);
        PH = intent.getStringExtra("PH");
        MapSL = (HashMap<String, String>) intent.getSerializableExtra("MapSL");

        String yjl= intent.getStringExtra("YJL");
        String xjxc=intent.getStringExtra("XJXC");
        if(xjxc.equals("Y")){
            cbxjxc.setChecked(true);
        }else{
            cbxjxc.setChecked(false);
        }
        DecimalFormat format = new DecimalFormat("0.00");
        tv_yjl.setText(format.format(new BigDecimal(yjl)));

        View header = LayoutInflater.from(XHCZStockActivity.this).inflate(R.layout.header_xhcz_stock, null);
        TextView ph=(TextView) header.findViewById(R.id.tv_stock_ph);
        ph.setText(PH);
        adapter=new MyStockAdapter();
        mStockList.addHeaderView(header);

        mStockList.setAdapter(adapter);

        et_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //执行相关读取操作
                    onShowDialog(et_value.getText().toString().trim());
                }
                return false;
            }
        });

        //读取本地数据
        initLocal(PH,xjxc,Float.parseFloat(yjl));
        //ToastUtils.showToast("IDS:"+IDS);
    }

    /**
     * 读取本地数据
     */
    private void initLocal(String ph, String xjxc, float yjl){
        mDbRepository.getDbDatasASC(ph, new DbDataSource.LoadDbCallback() {
            @Override
            public void onDbsLoaded(List<DbData> dbdatas) {
                List<Object> l = new ArrayList<>();
                String dates = "";//记录日期
                float numall = 0f;
                for (DbData d : dbdatas) {
                    List<Object> o = new ArrayList<>();
                    o.add(d.getMZ());//[0]
                    o.add(d.getRQ());//[1]
                    o.add(d.getPH());//[2]
                   /* if (MapSL != null && MapSL.size() > 0 && MapSL.containsKey(d.getMZ().trim())) {
                        float sysl = Float.parseFloat(d.getSL().trim());
                        float ppsl = Float.parseFloat(MapSL.get(d.getMZ()).trim());
                        o.add(String.valueOf(sysl+ppsl));//[3]剩余数量
                    } else {
                        o.add(d.getSL());//[3]剩余数量
                    }*/
                    o.add(d.getSL());//[3]剩余数量
                    if (MapSL != null && MapSL.size() > 0 && MapSL.containsKey(d.getMZ().trim())) {
                        o.add(MapSL.get(d.getMZ()).trim());//[4]匹配数量
                    } else {
                        o.add("0");//[4]匹配数量
                    }
                    o.add(d.getCK());//[5]仓库
                    o.add(d.getKW());//[6]库位
                    if (xjxc.equals("Y")) {//是否是先进先出
                        numall += Float.parseFloat(d.getSL().trim());
                        if (numall > yjl) {//数量是否大于预计数量
                            if (dates.equals("")) {
                                dates = d.getRQ().trim();
                                o.add("FSD");//[7]是否锁定，SD锁定，FSD非锁定
                            } else {
                                if (dates.equals(d.getRQ().trim())) {
                                    o.add("FSD");//[7]是否锁定，SD锁定，FSD非锁定
                                } else {
                                    o.add("SD");//[7]是否锁定，SD锁定，FSD非锁定
                                }
                            }
                        } else {
                            o.add("FSD");//[7]是否锁定，SD锁定，FSD非锁定
                        }
                    } else {
                        numall += Float.parseFloat(d.getSL().trim());
                        o.add("FSD");//[7]是否锁定，SD锁定，FSD非锁定
                    }
                    o.add(d.getYSL().trim());//[8]原数量
                    l.add(o);
                }

                if(numall<yjl){
                    ToastUtils.showToast("剩余数据量不足");
                }

                //填写匹配量==
                float allnum=0;
                for (Object o : l) {
                    List<Object> oo = (List<Object>) o;
                    float a = Float.parseFloat(oo.get(4).toString());
                    allnum+=a;
                }
                tv_ppsl.setText(String.valueOf(allnum));
                //==

                adapter.setLists(l);
            }

            @Override
            public void onDataNotAvailable() {
                Toast.makeText(XHCZStockActivity.this, "无数据！", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class);
        Intent scanIntent = intentIntegrator.createScanIntent();
        startActivityForResult(scanIntent, 999);
    }
    //重写   处理返回信息的监听（回调方法）
    //onActivityResult通用监听  监听所有返回信息的
    //必须要有requestCode区分有哪个请求返回的
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
            String ScanResult = "";
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    //Toast.makeText(getActivity(),"内容为空",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // ScanResult 为 获取到的字符串
                    ScanResult = intentResult.getContents();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            et_value.setText(ScanResult);
            //执行相关读取操作
            onShowDialog(et_value.getText().toString().trim());

        }
    }

    public void onShowDialog(String mz) {
        List<Object> list=adapter.getLists();
        String sysl="";
        String ppsl="";
        String mzs="";
        String xjxc="";
        int i=0;
        for(;i<list.size();i++){
            List<Object> l=(List<Object>)list.get(i);
            if(mz.equals(l.get(0).toString().trim())){
                mzs=mz;
                float s=Float.parseFloat(l.get(3).toString().trim());
                float p=Float.parseFloat(l.get(4).toString().trim());
                //sysl=l.get(3).toString().trim();
                sysl=String.valueOf(s+p);
                ppsl=l.get(4).toString().trim();
                xjxc=l.get(5).toString().trim();
                break;
            }
        }
        final int position=i;
        if(mzs.equals("")||xjxc.equals("SD")){
            et_value.setText("");
            et_value.postDelayed(new Runnable() {//非线程则requestFocus不起作用
                @Override
                public void run() {
                    et_value.requestFocus();//
                }
            },100);
            if(mzs.equals("")){
                ToastUtils.showToast("无匹配的值!");
            }else{
                ToastUtils.showToast("锁定数据!");
            }
            return;
        }

        builder = new AlertDialog.Builder(XHCZStockActivity.this);
        View view1 = LayoutInflater.from(XHCZStockActivity.this).inflate(R.layout.dialog_xhcz_stock_add, null);
        TextView tvsysl=(TextView)view1.findViewById(R.id.tv_sysl);
        EditText etppsl=(EditText)view1.findViewById(R.id.et_ppsl);
        TextView tvmz=(TextView)view1.findViewById(R.id.tv_mz);
        Button btn_jgry_add = (Button) view1.findViewById(R.id.btn_jgry_add);
        Button btn_jgry_cancel = (Button) view1.findViewById(R.id.btn_jgry_cancel);
        tvsysl.setText(sysl);
        if(ppsl.equals("")||ppsl.equals("0")){
            etppsl.setText(sysl);//若匹配数量为空则把剩余量添加上

        }else{
            etppsl.setText(ppsl);
        }

        tvmz.setText(mz);
        AlertDialog dialog = builder.create();
        btn_jgry_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_value.setText("");
                et_value.postDelayed(new Runnable() {//非线程requestFocus()不起作用
                    @Override
                    public void run() {
                        et_value.requestFocus();//
                    }
                },100);
                dialog.dismiss();
            }
        });
        btn_jgry_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                   /* if(!isNumeric(etppsl.getText().toString().trim().replaceAll("^(0+)", ""))){
                        ToastUtils.showToast("添加的数值不正确！");
                        return;
                    }*/
                    if(etppsl.getText().toString().trim().replaceAll("^(0+)", "").equals("")){
                        ToastUtils.showToast("添加的数值不正确！");
                        return;
                    }
                    List<Object> lists = adapter.getLists();
                    List<Object> objectList = (List<Object>) lists.get(position);

                    float syl = Float.parseFloat(tvsysl.getText().toString().trim().replaceAll("^(0+)", ""));//剩余量
                    float ppl = Float.parseFloat(etppsl.getText().toString().trim().replaceAll("^(0+)", ""));//匹配量
                    //float ysl=Float.parseFloat(objectList.get(8).toString().trim());//原数量
                    if(ppl>syl){
                        ToastUtils.showToast("匹配量不能大于剩余量！");
                        return;
                    }

                    float jg=syl-ppl;



                    objectList.set(4, etppsl.getText().toString().trim().replaceAll("^(0+)", ""));//匹配量
                    objectList.set(3, String.valueOf(jg));//匹配完剩余量
                    lists.set(position, objectList);

                    float allnum=0;
                    for (Object o : lists) {
                        List<Object> oo = (List<Object>) o;
                        float a = Float.parseFloat(oo.get(4).toString());
                        allnum+=a;
                    }



                    tv_ppsl.setText(String.valueOf(allnum));
                    adapter.setListsAtLine(lists);
                    mStockList.setSelection(position+1);
                    adapter.notifyDataSetChanged();



                    dialog.dismiss();
                } catch (Exception e) {
                    ToastUtils.showToast("添加失败,请检查填写数据是否正常!");
                    e.printStackTrace();
                }
                et_value.setText("");
                et_value.postDelayed(new Runnable() {//非线程则requestFocus不起作用
                    @Override
                    public void run() {
                        et_value.requestFocus();//
                    }
                },100);
            }
        });
        dialog.setView(view1);
        dialog.show();
    }


    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    Intent intentTemp=null;
    @Override
    public void onBackPressed() {
        if(intentTemp==null){
            List<Object> list =new ArrayList<>();
            intentTemp = new Intent();
            intentTemp.putExtra("PH", PH);
            intentTemp.putExtra("IDS", IDS);
            intentTemp.putExtra("ID", ID);
            intentTemp.putExtra("num","0");
            intentTemp.putExtra("list", (Serializable) list);
        }
        setResult(1, intentTemp);
        super.onBackPressed();


        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }

    @OnClick({R.id.left, R.id.btn_submit, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_cancel://取消
                if(tv_ppsl.getText().toString().trim().equals("0")){
                    List<Object> list =new ArrayList<>();
                    intentTemp = new Intent();
                    intentTemp.putExtra("PH", PH);
                    intentTemp.putExtra("IDS", IDS);
                    intentTemp.putExtra("ID", ID);
                    intentTemp.putExtra("num",tv_ppsl.getText().toString().trim());
                    intentTemp.putExtra("list", (Serializable) list);
                    //setResult(1, intentTemp);
                    onBackPressed();

                }else{
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("退出后扫描数据清空");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<Object> list =new ArrayList<>();
                            intentTemp = new Intent();
                            intentTemp.putExtra("PH", PH);
                            intentTemp.putExtra("IDS", IDS);
                            intentTemp.putExtra("ID", ID);
                            intentTemp.putExtra("num","0");
                            intentTemp.putExtra("list", (Serializable) list);
                            //setResult(1, intentTemp);
                            onBackPressed();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }

                break;
            case R.id.btn_submit://确定

                //计算相应的值
                float a = Float.parseFloat(tv_yjl.getText().toString().trim());
                float b = Float.parseFloat(tv_ppsl.getText().toString().trim());
                if(a!=b){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("数据错误！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //saveResult();
                        }
                    });
                    builder.show();
                }else{
                    saveResult();
                }
                break;

        }
    }

    /**
     * 返回值
     */
    private void saveResult() {
        List<Object> list = new ArrayList<>();
        List<Object> lists = adapter.getLists();
        for (Object o : lists) {
            List<Object> oo = (List<Object>) o;
            if (!oo.get(4).toString().trim().equals("0")) {
                List<Object> l = new ArrayList<>();
                l.add(oo.get(0).toString());//[0]码值
                l.add(oo.get(4).toString());//[1]匹配数量
                l.add(oo.get(5).toString());//[2]仓库
                l.add(oo.get(6).toString());//[3]库位
                list.add(l);

                //数据库修改数据数量
                mDbRepository.updateMZDbData(oo.get(0).toString().trim(),oo.get(3).toString());//剩余数量
            }

        }


        //读取数据返回相应的值
        intentTemp = new Intent();
        intentTemp.putExtra("PH", PH);
        intentTemp.putExtra("IDS", IDS);
        intentTemp.putExtra("ID", ID);
        intentTemp.putExtra("num",tv_ppsl.getText().toString().trim());
        intentTemp.putExtra("list", (Serializable) list);
        //setResult(1, intentTemp);



        onBackPressed();
    }


    public class MyStockAdapter extends BaseAdapter {
        List<Object> lists = new ArrayList<>();
        private ViewHolder holder;

        public void setLists(List<Object> lists) {
            this.lists = lists;
            notifyDataSetChanged();
        }

        public void setListsAtLine(List<Object> lists){
            this.lists = lists;
            //notifyDataSetChanged();
        }

        public List<Object> getLists() {
            return lists;
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(lists);
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
                view = LayoutInflater.from(XHCZStockActivity.this).inflate(R.layout.listitem_xhcz_stock_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            List<Object> obj = (List<Object>) lists.get(i);
            if (!ListUtils.isEmpty(obj)){
                holder.tvmz.setText(obj.get(0).toString());//[0]MZ箱码或托盘码值
                holder.tvrq.setText(obj.get(1).toString());//[1]RQ日期
                //[2]PH品号
                holder.tvsl.setText(obj.get(3).toString());//[3]SL数量
                holder.tvppl.setText(obj.get(4).toString());//[4]匹配数量
                holder.tvck.setText(obj.get(5).toString());//[5]仓库
                holder.tvkw.setText(obj.get(6).toString());//[6]库位
                if(obj.get(7).toString().trim().equals("SD")){
                    holder.ll_sd.setVisibility(View.VISIBLE);
                }else{
                    holder.ll_sd.setVisibility(View.GONE);
                }
            }
            holder.btnadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //弹出扫描框扫描获取数量修改数量
                    //onShowDialog(i);//弹出操作销货功能
                }
            });
            return view;
        }

        public class ViewHolder {
            @BindView(R.id.tv_mz)
            TextView tvmz;
            @BindView(R.id.tv_rq)
            TextView tvrq;
            @BindView(R.id.tv_sl)
            TextView tvsl;
            @BindView(R.id.tv_ppl)
            TextView tvppl;
            @BindView(R.id.btn_add)
            Button btnadd;
            @BindView(R.id.ll_sd)
            LinearLayout ll_sd;
            @BindView(R.id.tv_ck)
            TextView tvck;
            @BindView(R.id.tv_kw)
            TextView tvkw;
            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
