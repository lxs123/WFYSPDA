package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.data.DbData;
import com.cubertech.bhpda.data.DbListresult;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2018/9/25.
 */

public class XHCZDetailActivity extends Activity {

    @NonNull
    private final DbDataSource mDbRepository;
    //    @BindView(R.id.tv_customer_id)
    TextView tvCustomerId;
    //    @BindView(R.id.tv_cpph)
//    TextView tvCpph;
//    @BindView(R.id.tv_cppm)
//    TextView tvCppm;
//    @BindView(R.id.tv_cpgg)
//    TextView tvCpgg;
//    @BindView(R.id.tv_ppl)
//    TextView tvPpl;
//    @BindView(R.id.tv_yjchl)
//    TextView tvYjchl;
//    @BindView(R.id.tv_chtzdb)
    TextView tvChtzdb;
    //    @BindView(R.id.tv_chtzdh)
    TextView tvChtzdh;
    @BindView(R.id.xhcz_detail_list)
    ListView mDetailList;
    private AccountInfo ai;
    private SiteService siteService;
    private MyDetailAdapter adapter;
    private AlertDialog.Builder builder;

    //private EditText et_tm;

    //private TextView tv_jgry_name;
    //private TextView tv_ck, tv_kw;

    //private EditText edit_num;

    private double number;

    List<Object> listresult;


    Map<String,String> maps;

    public XHCZDetailActivity() {
        this.mDbRepository = DbRepository.getInstance(null,DbLocalDataSource.getInstance(this));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xhcz_detail);
        ButterKnife.bind(this);

        maps=new HashMap<>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        String chtzddh = intent.getStringExtra("chtzddh");
        String chtzddb = intent.getStringExtra("chtzddb");

        View header = LayoutInflater.from(XHCZDetailActivity.this).inflate(R.layout.header_xhcz_detail, null);
        tvChtzdh = (TextView) header.findViewById(R.id.tv_chtzdh);
        tvChtzdb = (TextView) header.findViewById(R.id.tv_chtzdb);
        tvCustomerId = (TextView) header.findViewById(R.id.tv_customer_id);
        tvChtzdb.setText(chtzddb);
        tvChtzdh.setText(chtzddh);
        mDetailList.addHeaderView(header);
        adapter = new MyDetailAdapter();
        mDetailList.setAdapter(adapter);
        onUrlLoad(chtzddb, chtzddh);

        listresult = new ArrayList<Object>();

        //删除数据库数据
        mDbRepository.deleteAllDbData();



    }

    private void onUrlLoad(String chtzdb, String chtzdh) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("database", ai.getData());
        params.put("TN001", chtzdb);
        params.put("TN002", chtzdh);
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        TKApplication application = (TKApplication) getApplication();
        //通过Application传值
        String IP = "";
        String port = "";
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        //先弄一个观察者
        Observable<List<Object>> observable = siteService.getCH(params,
                XHCZDetailActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                ToastUtils.showToast("" + e.toString().replace("java.lang.Exception:", ""));
            }

            @Override
            public void onNext(List<Object> o) {
                if (ListUtils.isEmpty(o)) {
                    return;
                }
                adapter.setLists(o);
                List<Object> obj = (List<Object>) o.get(0);
                if (ListUtils.isEmpty(obj)) {
                    return;
                }
                tvCustomerId.setText(obj.get(5).toString());//[5]客户编码

                //读取本地数据库，若存在则添加数据，并修改相关数据！20190312
                initLocal(chtzdb,chtzdh);
            }
        });
    }

    /**
     * 读取本地数据
     */
    private void initLocal(String db,String dh){
        Map<String, String> map = new HashMap<String, String>();
        mDbRepository.getDbListresult(db,dh,new DbDataSource.GetDbLCallback() {
            @Override
            public void onDbListLoaded(List<DbListresult> dblistresults) {
               for(DbListresult os : dblistresults){
                   List<Object> o=new ArrayList<>();
                   o.add(os.getKHBH().trim());//客户编号[0]
                   o.add(os.getPH().trim());// 品号[1]
                   o.add(os.getSL().trim());//数量[2]
                   o.add(os.getCK().trim());//仓库[3]
                   o.add(os.getDB().trim());//出货通知单别[4]
                   o.add(os.getDH().trim());// 出货通知单号[5]
                   o.add(os.getTZXH().trim());// 出货通知序号[6]
                   o.add(os.getKW().trim());//库位[7]
                   //2018-11-02新增箱子唛头或托唛头传递后端处理数据
                   o.add(os.getMT().trim());//[8]箱唛头码或托盘唛头码
                   listresult.add(o);

                   //计算数量（求数量总和）==========================
                   if(map.containsKey(os.getTZXH().trim())){ //键值是通知序号
                       //存在
                       float num=0;
                       //num+= Float.parseFloat(oo.get(2).toString().trim());
                       num=Float.parseFloat(os.getSL().trim())+Float.parseFloat(map.get(os.getTZXH().trim()));
                       DecimalFormat format = new DecimalFormat("0.00");
                       String n=format.format(new BigDecimal(num));
                       map.put(os.getTZXH().trim(),n);

                   }else{
                       //不存在
                       map.put(os.getTZXH().trim(),os.getSL());
                   }

                   //Key value 值确认(Map)

               }
               List<Object> lists=adapter.getLists();

               for(int i=0;i<lists.size();i++){
                   List<Object> objectList = (List<Object>) lists.get(i);
                   if(map.containsKey(objectList.get(6).toString().trim())){
                       DecimalFormat format = new DecimalFormat("0.00");
                       objectList.set(4, format.format(new BigDecimal(map.get((objectList.get(6).toString().trim())))));
                       lists.set(i, objectList);
                   }
               }
                adapter.setLists(lists);
            }

            @Override
            public void onDataNotAvailable() {
                //无数据
                //ToastUtils.showToast("无数据！");
            }
        });
    }



    /**
     * lxs  18 - 09 - 26修改
     * 不再使用
     * @param view
     */
/*    public void onShowDialog(View view, int position) {
        builder = new AlertDialog.Builder(XHCZDetailActivity.this);
        View view1 = LayoutInflater.from(XHCZDetailActivity.this).inflate(R.layout.dialog_xhcz_detail_add, null);
        Button btn_xiangji = (Button) view1.findViewById(R.id.del1);
        Button btn_jgry_add = (Button) view1.findViewById(R.id.btn_jgry_add);
        Button btn_jgry_cancel = (Button) view1.findViewById(R.id.btn_jgry_cancel);
        edit_num = (EditText) view1.findViewById(R.id.pick_jgry_num);
        et_tm = (EditText) view1.findViewById(R.id.et_tm);
        //tv_jgry_name = (TextView) view1.findViewById(R.id.tv_jgry_name);
        tv_ck = (TextView) view1.findViewById(R.id.tv_ck);
        tv_kw = (TextView) view1.findViewById(R.id.tv_kw);

        et_tm.setFocusable(true);
        et_tm.setFocusableInTouchMode(true);
        et_tm.requestFocus();

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
                if (TextUtils.isEmpty(tv_ck.getText().toString().trim())) {
                    ToastUtils.showToast("请扫码!");
                    return;
                }

                if (TextUtils.isEmpty(edit_num.getText().toString())) {
                    ToastUtils.showToast("请添加数量!");
                    return;
                }

                //增加判断修改的数量必须小于带出的数量！！！
                double num= Double.parseDouble(edit_num.getText().toString());
                if(num>number){
                    ToastUtils.showToast("输入值大于实际值！");
                    return;
                }

                try {
                    List<Object> lists = adapter.getLists();
                    List<Object> objectList = (List<Object>) lists.get(position);

                    List<Object> o = new ArrayList<>();
                    o.add(tvCustomerId.getText().toString().trim());//客户编号[0]
                    o.add(objectList.get(0).toString().trim());// 品号[1]
                    o.add(edit_num.getText().toString().trim());//数量[2]
                    o.add(tv_ck.getText().toString().trim());//仓库[3]
                    o.add(tvChtzdh.getText().toString().trim());//出货通知单别[4]
                    o.add(tvChtzdh.getText().toString().trim());// 出货通知单号[5]
                    o.add(objectList.get(6).toString().trim());// 出货通知序号[6]
                    o.add(tv_kw.getText().toString().trim());//库位[7]

                    //2018-11-02新增箱子唛头或托唛头传递后端处理数据
                    o.add(et_tm.getText().toString().trim());//[8]箱唛头码或托盘唛头码

                    listresult.add(o);


                    String pplStr = objectList.get(4).toString();
                    double ppl = Double.parseDouble(pplStr);
                    ppl += Double.parseDouble(edit_num.getText().toString());
                    Format format = new DecimalFormat("#.00");//当前保留两位小数

                    objectList.set(4, format.format(ppl));
                    lists.set(position, objectList);
                    adapter.setLists(lists);
                    dialog.dismiss();
                } catch (Exception e) {
                    ToastUtils.showToast("添加失败,请检查填写数据是否正常!");
                    e.printStackTrace();
                }

            }
        });

        et_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    scanCode();
                }
                return false;
            }
        });
        btn_xiangji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                xiangji(view);
            }
        });
        dialog.setView(view1);
        dialog.show();
    }*/

    /**
     * 扫描二维码
     */
 /*   private void scanCode() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            ToastUtils.showToast("请先扫描");
            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", tm);
        params.put("database", ai.getData());
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //通过Application传值
        String IP = "";
        String port = "";
        TKApplication application = (TKApplication) getApplication();
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        //先弄一个观察者
        Observable<List<Object>> observable = siteService.getXH_KW(params,
                XHCZDetailActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                //ToastUtils.showToast("" + e.toString().replace("java.lang.Exception:", ""));
                Toast.makeText(XHCZDetailActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                tv_ck.setText(obj.get(1).toString().trim());//仓库
                tv_kw.setText(obj.get(2).toString().trim());//库位
                et_tm.setText(obj.get(3).toString().trim());//重新赋值
                number=Double.parseDouble(obj.get(0).toString().trim());//记录接收的数据
                edit_num.setText(obj.get(0).toString().trim());//数量
                edit_num.setSelection(0);
                edit_num.requestFocus();
            }
        });
    }*/

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
            //et_tm.setText(ScanResult);
            //scanCode();
        }
        if(requestCode == 1){
            //正常操作
            try {
                String tzxh =data.getStringExtra("ID");////"ID", tzxh;//通知序号
                String PH=data.getStringExtra("PH");//品号
                List<Object> list = (List<Object>) data.getSerializableExtra("list");
                for (Object os : list) {
                    List<Object> oo = (List<Object>) os;
                    List<Object> o=new ArrayList<>();

                    o.add(tvCustomerId.getText().toString().trim());//客户编号[0]
                    o.add(PH);// 品号[1]
                    o.add(oo.get(1).toString().trim());//数量[2]
                    o.add(oo.get(2).toString().trim());//仓库[3]
                    o.add(tvChtzdb.getText().toString().trim());//出货通知单别[4]
                    o.add(tvChtzdh.getText().toString().trim());// 出货通知单号[5]
                    o.add(tzxh);// 出货通知序号[6]
                    o.add(oo.get(3).toString().trim());//库位[7]

                    //2018-11-02新增箱子唛头或托唛头传递后端处理数据
                    o.add(oo.get(0).toString().trim());//[8]箱唛头码或托盘唛头码

                    listresult.add(o);

                    //保存到数据库
                    List<DbListresult> dbListresults=new ArrayList<>();
                    DbListresult db=new DbListresult( UUID.randomUUID().toString().replaceAll("-", ""),//id
                            tvCustomerId.getText().toString().trim(),//客户编号[0]
                            PH,// 品号[1]
                            oo.get(1).toString().trim(),//数量[2]
                            oo.get(2).toString().trim(),//仓库[3]
                            tvChtzdb.getText().toString().trim(),//出货通知单别[4]
                            tvChtzdh.getText().toString().trim(),// 出货通知单号[5]
                            tzxh,// 出货通知序号[6]
                            oo.get(3).toString().trim(),//库位[7]
                            oo.get(0).toString().trim());//[8]箱唛头码或托盘唛头码
                    dbListresults.add(db);
                    mDbRepository.batchSaveDbListresult(dbListresults);


                }
                int ids = data.getIntExtra("IDS", -1);
                String num = data.getStringExtra("num");
                if (num.equals("0")) {
                    return;
                }

                if (ids != -1) {
                    List<Object> lists = adapter.getLists();
                    List<Object> objectList = (List<Object>) lists.get(ids);
                    DecimalFormat format = new DecimalFormat("0.00");
                    objectList.set(4, format.format(new BigDecimal(num)));
                    lists.set(ids, objectList);
                    adapter.setLists(lists);
                }
            }catch(Exception e){
                Toast.makeText(XHCZDetailActivity.this,"异常数据，请重新操作",Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == 2){
            //退库操作
            List<Object> listr = (List<Object>) data.getSerializableExtra("listr");
            List<Object> del=new ArrayList<>();
            float num=0;
            for(Object o:listresult){
                List<Object> oo = (List<Object>) o;
                //通知序号[6]
                //[8]箱唛头码或托盘唛头码
                //数量[2]
                /*if(oo.get(6).toString().trim().equals(tzxh)){
                    MapSL.put(oo.get(8).toString().trim(),oo.get(2).toString().trim());
                    del.add(o);
                }*/
                for(Object oos:listr){
                    if(oo.get(8).toString().trim().equals(oos.toString().trim())){
                        num+= Float.parseFloat(oo.get(2).toString().trim());

                        del.add(oo);
                    }
                }

            }

            for(Object o: del){
                List<Object> oo = (List<Object>) o;
                //o.add(oo.get(1).toString().trim());//数量[2]
                //float a = Float.parseFloat(o.get(1).toString().trim());
                listresult.remove(o);


                //根据唛头删除数据库数据
                mDbRepository.deletTKDbListresult(oo.get(8).toString().trim());
            }

            int ids = data.getIntExtra("IDS", -1);
           /* String num = data.getStringExtra("num");
            if (num.equals("0")) {
                return;
            }*/

            if (ids != -1) {
                List<Object> lists = adapter.getLists();

                List<Object> objectList = (List<Object>) lists.get(ids);

                float a = Float.parseFloat(objectList.get(4).toString().trim());
                a-=num;
                DecimalFormat format = new DecimalFormat("0.00");
                objectList.set(4, format.format(new BigDecimal(String.valueOf(a))));
                lists.set(ids, objectList);
                adapter.setLists(lists);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //删除数据库数据
        //mDbRepository.deleteAllDbData();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }

    @OnClick({R.id.left, R.id.btn_submit, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_cancel:
                android.app.AlertDialog.Builder builders = new android.app.AlertDialog.Builder(this);
                builders.setTitle("提示");
                //builders.setMessage("退出后扫描数据清空");
                builders.setMessage("确定退出吗？");
                builders.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                builders.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builders.show();

                break;
            case R.id.btn_submit:
                if (ListUtils.isEmpty(listresult)) {
                    Toast.makeText(XHCZDetailActivity.this, "数据不正确！", Toast.LENGTH_LONG).show();
                    return;
                }

                //2019-03-06取消检查
                /*List<Object> lists = adapter.getLists();
                for (Object o : lists) {
                    List<Object> oo = (List<Object>) o;
                    *//*String df="12.2";
                    float a=Float.parseFloat(df);*//*
                    float a = Float.parseFloat(oo.get(3).toString().trim());
                    float b = Float.parseFloat(oo.get(4).toString().trim());
                    if (a != b) {
                        Toast.makeText(XHCZDetailActivity.this, "匹配数量不正确！", Toast.LENGTH_LONG).show();
                        return;
                    }
                }*/

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("确定销货操做么?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveXH();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

                break;

        }
    }

    /**
     * 销货保存
     */
    private void saveXH() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("data", ai.getData());
        params.put("list", listresult);
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        TKApplication application = (TKApplication) getApplication();
        //通过Application传值
        String IP = "";
        String port = "";
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        siteService = SiteService.getInstants();
        Observable<String> observable = siteService.saveXH(params, XHCZDetailActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(XHCZDetailActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String str) {
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                //销货操作成功后删除数据库对应得数据
                mDbRepository.deletXHDbListresult(tvChtzdb.getText().toString().trim(),tvChtzdh.getText().toString().trim());
                Toast.makeText(XHCZDetailActivity.this, "完成！", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }


    public class MyDetailAdapter extends BaseAdapter {
        List<Object> lists = new ArrayList<>();
        private ViewHolder holder;

        public void setLists(List<Object> lists) {
            this.lists = lists;
            notifyDataSetChanged();
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
                view = LayoutInflater.from(XHCZDetailActivity.this).inflate(R.layout.listitem_xhcz_detail_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            List<Object> obj = (List<Object>) lists.get(i);
            if (!ListUtils.isEmpty(obj) && ListUtils.getSize(obj) >= 5) {
                holder.tvCpph.setText(obj.get(0).toString());//[0]产品品号
                holder.tvCppm.setText(obj.get(1).toString());//[1]产品品名
                holder.tvCpgg.setText(obj.get(2).toString());//[2]产品规格
                DecimalFormat format = new DecimalFormat("0.00");
                holder.tvYjchl.setText(format.format(new BigDecimal(obj.get(3).toString().trim())));//[3]预计出货量
                holder.tvPpl.setText(obj.get(4).toString());//[4]匹配量
                //[5]客户编号
                holder.tvtzxh.setText(obj.get(6).toString().trim());//[6]通知序号
                holder.tvDd.setText(obj.get(7).toString().trim() //订单单别[7]
                        +"-"+obj.get(8).toString().trim()        //订单单号[8]
                        +"-"+obj.get(9).toString().trim());      //订单序号[9]
            }
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //onShowDialog(view, i);//原来直接弹出扫描界面

                    if(initLocal(obj.get(0).toString().trim())){
                        //本地存在数据直接跳转到新的Acitvity中
                        onStartActivity(i,obj.get(0).toString(),obj.get(6).toString().trim(),obj.get(3).toString());
                    }else{
                        //本地数据不存在则获取服务器数据
                        initRemote(i,obj.get(0).toString().trim(),obj.get(6).toString().trim(),obj.get(3).toString());
                    }

                }
            });

            holder.btnReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if(initLocal(obj.get(0).toString().trim())){
                        //本地已经存在则跳转到界面删除数据
                       // intentTemp.putExtra("IDS", IDS);
                        onStartReturnActivity(i);
                    }else{
                       //不存在则提示不存在数据
                        Toast.makeText(XHCZDetailActivity.this, "不存在数据无需删除", Toast.LENGTH_LONG).show();
                    }*/
                    if(holder.tvPpl.getText().toString().trim().equals("0")){
                        Toast.makeText(XHCZDetailActivity.this, "不存在数据无需删除", Toast.LENGTH_LONG).show();
                    }else{
                        onStartReturnActivity(i);
                    }
                }
            });

            return view;
        }

        /**
         * 退库操作
         */
        private void onStartReturnActivity(int i){
            Bundle bundle = new Bundle();
            bundle.putParcelable("account", ai);
            Intent intent = new Intent();
            intent.putExtra("IDS",i);
            intent.putExtras(bundle);
            intent.setClass(XHCZDetailActivity.this, XHCZReturnActivity.class);//销货操作
            startActivityForResult(intent,2);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }


        /**
         * 启动新activity添加数量
         * @param
         */
        private  void onStartActivity(int i,String ph,String  tzxh,String yjl){

            Map<String,String> MapSL=new HashMap<>();
            List<Object> del=new ArrayList<>();
            for(Object o:listresult){
                List<Object> oo = (List<Object>) o;
                //通知序号[6]
                //[8]箱唛头码或托盘唛头码
                //数量[2]
                if(oo.get(6).toString().trim().equals(tzxh)){
                    MapSL.put(oo.get(8).toString().trim(),oo.get(2).toString().trim());
                    del.add(o);
                }
            }
            for(Object o: del){
                listresult.remove(o);
            }


            Bundle bundle = new Bundle();
            bundle.putParcelable("account", ai);
            Intent intent = new Intent();
            intent.putExtra("IDS",i);
            intent.putExtra("ID", tzxh);//通知序号
            intent.putExtra("PH", ph);
            intent.putExtra("YJL",yjl);
            intent.putExtra("XJXC",maps.get(tzxh).trim());
            intent.putExtra("MapSL", (Serializable) MapSL);
            //intent.putExtra("message",(Serializable)message);
            intent.putExtras(bundle);
            intent.setClass(XHCZDetailActivity.this, XHCZStockActivity.class);//销货操作
            startActivityForResult(intent,1);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }




        /**
         * 读取本地数据
         * 若本地数据存在
         * 则本地读取
         * 不存在则从远程获取
         */
        private boolean initLocal(String ph){
            return mDbRepository.ExistsPH(ph);
        }

        /**
         * 获取远程数据
         */
        public void initRemote(int i,String ph, String  tzxh,String yjl){
            //判断是否是先进先出
            HashMap<String, Object> params = new HashMap<>();
            params.put("data", ai.getData());
            params.put("code", ph);//obj.get(0).toString());
            params.put("strToken", "");
            params.put("strVersion", "");
            params.put("strPoint", "");
            params.put("strActionType", "1001");
            TKApplication application = (TKApplication) getApplication();
            //通过Application传值
            String IP = "";
            String port = "";
            if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
                IP = application.getUrl();
                port = application.getPort();
            }
            siteService = SiteService.getInstants();
            Observable<List<Object>> observable = siteService.getXH_XJXC(params, XHCZDetailActivity.this, IP, port);
            observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
                @Override
                public void onCompleted() {
                    siteService.closeParentDialog();
                }

                @Override
                public void onError(Throwable e) {
                    siteService.closeParentDialog();
                    Toast.makeText(XHCZDetailActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNext(List<Object> str) {
                    if(str.equals(null)||str.size()<=0){
                        Toast.makeText(XHCZDetailActivity.this, "未能获取到数据", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String xjxc=str.get(str.size()-1).toString();

                    if(!maps.containsKey(tzxh)){
                        maps.put(tzxh,xjxc);
                    }

                    List<DbData> dbDatalist=new ArrayList<DbData>();
                    DbData dbData;
                    for(int i=0;i<str.size()-1;i++){
                        List<Object> oo=(List<Object>) str.get(i);
                        String rq=oo.get(0).toString().trim();
                        String phs=oo.get(1).toString().trim();
                        String sl=oo.get(2).toString().trim();
                        String mz=oo.get(3).toString().trim();
                        String ck=oo.get(4).toString().trim();
                        String kw=oo.get(5).toString().trim();
                        dbData=new DbData(mz,rq,phs,sl,ck,kw,sl);
                        dbDatalist.add(dbData);
                    }

                    mDbRepository.batchSaveDbData(dbDatalist);
                    mDbRepository.getDbDatasASC(ph, new DbDataSource.LoadDbCallback() {
                        @Override
                        public void onDbsLoaded(List<DbData> dbdatas) {
                            //跳转到新的activity
                            onStartActivity(i,ph,tzxh,yjl);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            Toast.makeText(XHCZDetailActivity.this, "库存数据错误！", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        }

        public class ViewHolder {
            @BindView(R.id.tv_cpph)
            TextView tvCpph;
            @BindView(R.id.tv_cppm)
            TextView tvCppm;
            @BindView(R.id.tv_cpgg)
            TextView tvCpgg;
            @BindView(R.id.tv_ppl)
            TextView tvPpl;
            @BindView(R.id.tv_yjchl)
            TextView tvYjchl;
            @BindView(R.id.btn_add)
            Button btnAdd;
            @BindView(R.id.tv_dd)
            TextView tvDd;
            @BindView(R.id.tv_tzxh)
            TextView tvtzxh;
            @BindView(R.id.btn_return)
            Button btnReturn;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
