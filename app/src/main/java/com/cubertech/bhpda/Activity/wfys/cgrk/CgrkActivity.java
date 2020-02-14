package com.cubertech.bhpda.Activity.wfys.cgrk;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.ZycxActivity;
import com.cubertech.bhpda.Activity.wfys.XjActivity;
import com.cubertech.bhpda.Activity.wfys.dhys.DhysActivity;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.data.DbCgtzd;
import com.cubertech.bhpda.data.DbPickItem;
import com.cubertech.bhpda.data.source.DbDataSource;
import com.cubertech.bhpda.data.source.DbRepository;
import com.cubertech.bhpda.data.source.local.DbLocalDataSource;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

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
 * 采购入库
 * Created by Administrator on 2019/12/12.
 */

public class CgrkActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.dhys_recycle)
    RecyclerView mDhysList;
    @BindView(R.id.dhys_swipe)
    SwipeRefreshLayout mSwipe;
    @BindView(R.id.et_value)
    EditText etTm;
    ServiceUtils su = ServiceUtils.getInstance();
    @BindView(R.id.tv_dhdh)
    TextView tvDhdh;
    @BindView(R.id.tv_gysname)
    TextView tvGysname;
    private String tm;
    private MyDhysAdapter adapter;

    @NonNull
    private final DbDataSource mDbRepository;
    private SharedPreferences sharedPreferences;

    private List<Map<String, Object>> mapList = new ArrayList<>();

    public CgrkActivity() {
        this.mDbRepository = DbRepository.getInstance(null, DbLocalDataSource.getInstance(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cg);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("dataset", 0);
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
                    initDate();//
                }
                return false;
            }
        });
        mDhysList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyDhysAdapter();
        mDhysList.setAdapter(adapter);
        mSwipe.setRefreshing(false);
        mSwipe.setEnabled(false);
        mSwipe.setOnRefreshListener(this);
    }

    private void initDate() {
        if (TextUtils.isEmpty(etTm.getText().toString())) {
            ToastUtils.showToast("请扫描收货单号!");
            return;
        }
        String dhysd = sharedPreferences.getString("cgdh", "");
        if (!TextUtils.isEmpty(dhysd)) {
            mDbRepository.getDbCgtzdList(dhysd, new DbDataSource.GetDbCgtzdCallback() {
                @Override
                public void onDbCgtzdListLoaded(List<DbCgtzd> dbZszfList) {
                    if (!ListUtils.isEmpty(dbZszfList)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CgrkActivity.this, R.style.MP_Theme_alertDialog);
                        builder.setTitle("提示");
                        builder.setMessage("当前存在未提交的数据,是否继续!");
                        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tm = dhysd;
                                etTm.setText("");
                                etTm.setSelection(0);
                                etTm.requestFocus();
                                List<Object> list = DbCgtzd.toListObjectCg(dbZszfList);
                                adapter.setList(list);
                            }
                        });
                        builder.setNegativeButton("删除数据", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDbRepository.getDbCgtzdList(dhysd, new DbDataSource.GetDbCgtzdCallback() {

                                    @Override
                                    public void onDbCgtzdListLoaded(List<DbCgtzd> dbZszfList) {
                                        int i = 0;
                                        for (DbCgtzd dbPick : dbZszfList) {
                                            mDbRepository.deleteDbCgrkItem(dhysd + dbPick.getLJBH() + i);
                                            i++;
                                        }
                                    }

                                    @Override
                                    public void onDataNotAvailable() {

                                    }
                                });
                                mDbRepository.deleteDbCgtzd(dhysd);
                                ToastUtils.showToast("删除成功!");
                                adapter.setList(null);
                            }
                        });
                        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                        return;
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    scanCode();
                }
            });
            return;
        } else {
            scanCode();
        }
//
    }

    private void scanCode() {
        String link = "hqcgrk";
        HashMap<String, Object> params = new HashMap<String, Object>();
        adapter.setList(null);
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

        params.put("pmdsdocno", split[0]);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(CgrkActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                tm = etTm.getText().toString();
                List<Object> objectList = (List<Object>) o;
//                0到货单别，1到货单号，2供应商编号，3供应商名称，4序号，5品号，6品名,7规格，8交货仓库，
//                9仓库名称10合格数量，11验退数量 12条码类别，13品号属性，14是否供应商条码,15单位
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                Log.e("#######", objectList.toString());
                List<Object> objectList2 = new ArrayList<>();
                for (Object o1 : objectList) {
                    List<Object> objects = (List<Object>) o1;
                    objects.add("");//16
                    objects.add("0");//17
                    objectList2.add(objects);
                }
                adapter.setList(objectList2);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("cgdh", tm);
                edit.commit();

            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(CgrkActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CgrkActivity.this, error, Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.left, R.id.btn_cancel, R.id.btn_submit, R.id.del1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.del1:
                etTm.setText("");
                etTm.setSelection(0);
                etTm.requestFocus();
                break;
            case R.id.left:
//                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_submit:
                if (TextUtils.isEmpty(tm)) {
                    ToastUtils.showToast("请扫描出货通知单号！");
                    return;
                }
            /*    List<Object> objList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    List<Object> list = new ArrayList<>();
                    list.add("getDB()");       //2单别  ==>0
                    list.add("getDH()");     //3单号  ==>1
                    list.add("getGYSBH()");     //0供应商编号  ==>2
                    list.add("getCggys()");     //1采购供应商 名称  ==>3
                    list.add("getXH()");     //6//序号==>4
                    list.add("getPH()");     //4//品号  ==>5
                    list.add("getMC()");      //5名称==>6
                    list.add("getGG()");     //7规格
                    list.add("getjhCK");   //8//交货仓库
                    list.add("getCK()"); //9仓库名称
                    list.add("12");   //10合格数量
                    list.add("4");   //11不合格数量
                    list.add("getBHGYY()");   //12不良原因
                    list.add("getSTATE()");   //13状态
                    objList.add(list);
                }
                adapter.setList(objList);*/
                List<Object> objectList = new ArrayList<>();
//                objectList.add(tm);//[2]领料单号llxx
//                objectList.add("Y");//[3]工单单号
                List<Object> list = adapter.getList();
                int i = 0;
                List<Object> lld2 = new ArrayList<>();
                List<Object> lld3 = new ArrayList<>();
//                for (Object o : list) {
//                    objectList = new ArrayList<>();
//                    List<Object> objectList3 = new ArrayList<>();
//                    List<Object> objects = (List<Object>) o;
//                    Format format = new DecimalFormat("#0");
//                    objectList.add(format.format(Double.parseDouble(String.valueOf(objects.get(0)))));//企业编号[0]llxx
//                    objectList.add(objects.get(1));//据点[1]llxx
////                    objectList.add("1");//据点[1]llxx
//                    SharedPreferences sp = CgrkActivity.this.getSharedPreferences(
//                            "config", Activity.MODE_PRIVATE);
//
//                    String name = sp.getString("name", null);
//                    objectList.add(name);//[2]操作人员
//                    objectList.add(tm);//[3]采购单
//
//
//                    objectList3.add(objects.get(10));//库位[0]
//                    objectList3.add(objects.get(12));//入库数量 [1]
//                    objectList3.add(objects.get(4));//项次    [2]
//                    lld2.add(objectList3);
//
//                    mDbRepository.getDbCgrkListItem(tm + objects.get(4) + i, new DbDataSource.GetDbPickItemCallback() {
//                        @Override
//                        public void onDbPickListItemLoaded(List<DbPickItem> dbPickList) {
//                            for (DbPickItem dbPickItem : dbPickList) {
//                                if (TextUtils.equals(dbPickItem.getSTATE(), "1")) {
//                                    List<Object> objList = new ArrayList<>();
//                                    objList.add(format.format(Double.parseDouble(String.valueOf(objects.get(0)))));//企业编号[0]llxx
//                                    objList.add(objects.get(1));//据点[1]llxx
//                                    objList.add("1");//  [2]入库为1出库-1
//                                    objList.add(tm);//[3]
//                                    objList.add(objects.get(4));//[4]xc
//                                    objList.add(dbPickItem.getPH());//[5]品号
//                                    objList.add(dbPickItem.getPM());//[6]品名
//                                    objList.add(dbPickItem.getGG());//[7]规格
//                                    objList.add(dbPickItem.getDW());//8单位
//                                    objList.add(dbPickItem.getFLSL());//9数量
//                                    objList.add(dbPickItem.getCK());//10仓库
//                                    objList.add(dbPickItem.getKW());//11库位
//                                    objList.add(dbPickItem.getPC());//12批次
//                                    SharedPreferences sp = CgrkActivity.this.getSharedPreferences(
//                                            "config", Activity.MODE_PRIVATE);
//
//                                    String name = sp.getString("name", null);
//                                    objList.add(name);//13
//                                    objList.add("PDA");//14
//                                    lld3.add(objList);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onDataNotAvailable() {
//
//                        }
//                    });
//                    i++;
//                }


       /*     //                0到货单别，1到货单号，2供应商编号，3供应商名称，4序号，5品号，6品名,7规格，8交货仓库，
//               9仓库名称10合格数量，11验退数量 ，12入库数量，13状态*/
                List<Object> cgrk1 = new ArrayList<>();
                int anInt = 0;
                for (Object o : list) {
                    if (ListUtils.getSize(mapList) != ListUtils.getSize(list)) {
                        ToastUtils.showToast("请完善批次，注：每条数据都需要生成批次！");
                    return;
                    }
//                    for (int j = 0; j < ListUtils.getSize(mapList); j++) {
//                        if (Integer.parseInt(String.valueOf(mapList.get(i).get("position"))) == anInt) {
                    ArrayList<String> data = (ArrayList<String>) mapList.get(anInt).get("data");
                    for (String str : data) {
                        List toList = ListUtils.toList(str);
                        List<Object> objectList1 = new ArrayList<>();
                        List<Object> objectList2 = (List<Object>) o;
                        objectList1.add("3401");//0
                        objectList1.add("11");//1
                        objectList1.add(objectList2.get(2));//供应商编号2
                        objectList1.add("");//3
                        double jhsl = Double.parseDouble(TextUtils.isEmpty(String.valueOf(objectList2.get(10))) ? "0"
                                : String.valueOf(objectList2.get(10))) + Double.parseDouble(TextUtils.isEmpty(String.valueOf(objectList2.get(11))) ? "0"
                                : String.valueOf(objectList2.get(11)));
                        objectList1.add(String.valueOf(jhsl));//进货数量==验收数量+yan'退数量4
                        objectList1.add(String.valueOf(objectList2.get(8)));//仓库编号5
                        objectList1.add("********************");//批号6
                        objectList1.add("");//7
                        objectList1.add("");//8
                        objectList1.add("");//9
                        objectList1.add("##########");//10
                        objectList1.add(objectList2.get(10));//验收数量11
                        objectList1.add(objectList2.get(11));//验收退货数量12
                        objectList1.add("");//13
                        objectList1.add("");//14
                        objectList1.add("");//15
                        objectList1.add(objectList2.get(0));//ys单别16
                        objectList1.add(objectList2.get(1));//ys单h17
                        objectList1.add(objectList2.get(4));//ysxh18
                        objectList1.add("Y");//库位19
                        objectList1.add(objectList2.get(5));//ph20
                        objectList1.add(objectList2.get(6));//pm21
                        objectList1.add(objectList2.get(7));//gg22
                        objectList1.add(objectList2.get(15));//dw23
                        objectList1.add(toList.get(1));//kuwei24
                        objectList1.add(toList.get(0));//pc25
                        SharedPreferences sp = getSharedPreferences(
                                "config", Activity.MODE_PRIVATE);

                        String name = sp.getString("name", null);
                        objectList1.add(name);//[26]操作人员
                        cgrk1.add(objectList1);
                    }
//                        }else {
//
//                        }
//                    }


                   /* List<Object> objectList1 = new ArrayList<>();
                    List<Object> objectList2 = (List<Object>) o;
                    objectList1.add("3401");//0
                    objectList1.add("11");//1
                    objectList1.add(objectList2.get(2));//供应商编号2
                    objectList1.add("");//3
                    double jhsl = Double.parseDouble(TextUtils.isEmpty(String.valueOf(objectList2.get(10))) ? "0"
                            : String.valueOf(objectList2.get(10))) + Double.parseDouble(TextUtils.isEmpty(String.valueOf(objectList2.get(11))) ? "0"
                            : String.valueOf(objectList2.get(11)));
                    objectList1.add(String.valueOf(jhsl));//进货数量==验收数量+yan'退数量4
                    objectList1.add(String.valueOf(objectList2.get(8)));//仓库编号5
                    objectList1.add("********************");//批号6
                    objectList1.add("");//7
                    objectList1.add("");//8
                    objectList1.add("");//9
                    objectList1.add("##########");//10
                    objectList1.add(objectList2.get(10));//验收数量11
                    objectList1.add(objectList2.get(11));//验收退货数量12
                    objectList1.add("");//13
                    objectList1.add("");//14
                    objectList1.add("");//15
                    objectList1.add(objectList2.get(0));//ys单别16
                    objectList1.add(objectList2.get(1));//ys单h17
                    objectList1.add(objectList2.get(4));//ysxh18
                    objectList1.add("Y");//库位19
                    cgrk1.add(objectList1);*/
                    anInt++;

                }
                String link = "bccgrk";
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("shjy", cgrk1);
//                params.put("cgrk2", lld2);
//                params.put("cgrk3", lld3);
                params.put("strToken", "");
                params.put("strVersion", Utils.getVersions(this));
                params.put("strPoint", "");
                params.put("strActionType", "1001");
                su.callService(CgrkActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
                    @Override
                    public void onResponse(Object o) {
                        ToastUtils.showToast("入库成功");
                        mDbRepository.getDbCgtzdList(tm, new DbDataSource.GetDbCgtzdCallback() {

                            @Override
                            public void onDbCgtzdListLoaded(List<DbCgtzd> dbZszfList) {
                                int i = 0;
                                for (DbCgtzd dbPick : dbZszfList) {
                                    mDbRepository.deleteDbCgrkItem(tm + dbPick.getLJBH() + i);
                                    i++;
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        mDbRepository.deleteDbCgtzd(tm);

                        //延迟退出
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFailure(String str) {
                        CommonUtil.showErrorDialog(CgrkActivity.this, str);
                        su.closeParentDialog();//必须
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(CgrkActivity.this, error, Toast.LENGTH_LONG).show();
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
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onRefresh() {
        scanCode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) return;
        switch (requestCode) {
            case 24:
                ArrayList<String> pcList = data.getStringArrayListExtra("pcList");
                int position = data.getIntExtra("position", 0);
                Map<String, Object> map = new HashMap<>();
                map.put("position", position);
                map.put("data", pcList);
                boolean isAddMap = false;
                for (int i = 0; i < ListUtils.getSize(mapList); i++) {
                    Map<String, Object> stringObjectMap = mapList.get(i);
                    if (TextUtils.equals(String.valueOf(stringObjectMap.get("position")), String.valueOf(position))) {
                        isAddMap = true;
                        mapList.set(i, map);
                    }
                }
                if (!isAddMap)
                    mapList.add(map);

                break;
            case 22:
                int positon = data.getIntExtra("position", 0);
                String ppsl = data.getStringExtra("ppsl");
                List<Object> list = adapter.getList();
                for (int i = 0; i < ListUtils.getSize(list); i++) {
                    if (i == positon) {
                        List<Object> objects = (List<Object>) list.get(i);
                        objects.set(16, ppsl);
                        list.set(i, objects);
                    } else {
                        List<Object> objects = (List<Object>) list.get(i);
                        list.set(i, objects);
                    }
                }
                adapter.setList(list);
                break;
            case 21:
                boolean isEdit = data.getBooleanExtra("isEdit", false);
                if (isEdit) {
                    int positon1 = data.getIntExtra("position", 0);
                    String ppsl1 = data.getStringExtra("ppsl");
                    List<Object> list1 = adapter.getList();
                    for (int i = 0; i < ListUtils.getSize(list1); i++) {
                        if (i == positon1) {
                            List<Object> objects = (List<Object>) list1.get(i);
                            objects.set(16, ppsl1);
                            list1.set(i, objects);
                        } else {
                            List<Object> objects = (List<Object>) list1.get(i);
                            list1.set(i, objects);
                        }
                    }
                    adapter.setList(list1);
                }
                break;
        }
    }

    public class MyDhysAdapter extends RecyclerView.Adapter<MyDhysAdapter.ViewHolder> {
        List<Object> list = new ArrayList<>();

        public List<Object> getList() {
            return list;
        }

        public void setList(List<Object> list) {
            this.list = list;
            notifyDataSetChanged();
            if (ListUtils.isEmpty(list)) {
                return;
            }
            List<Object> objectList2 = list;
            // TODO: 2019/12/20
            Log.e("#######1", objectList2.toString());
            List<DbCgtzd> dbZszfList = new ArrayList<>();
            for (Object obj : objectList2) {
                List<Object> objectList1 = (List<Object>) obj;
                DbCgtzd dbPick = new DbCgtzd(tm,//采购单号扫描/出货单
                        objectList1.get(2).toString()//企业编号    ==>  //2供应商编号
                        , objectList1.get(12).toString()//12条码类别
                        , objectList1.get(13).toString()//13品号属性
                        , String.valueOf(objectList1.get(3).toString())//采购运营商
                        , objectList1.get(14).toString()//14是否供应商条码
                        , objectList1.get(0).toString()//项次    ==>//0单别
                        , objectList1.get(1).toString()//料件编号  ==>//1单号
                        , objectList1.get(5).toString()//库位    ==>//5品号
                        , objectList1.get(9).toString()//结果    ==>//9是否急料
                        , objectList1.get(8).toString()//采购数量  ==>////8待检数量
                        , objectList1.get(6).toString()//名称    ==>//6品名
                        , objectList1.get(7).toString()//规格    ==>//7规格
                        , objectList1.get(4).toString()//单位    ==>//4序号
                        , objectList1.get(10).toString()//合格数量 ==>//10合格数量
                        , objectList1.get(11).toString()//不合格数量==>//11不合格数量
                        , objectList1.get(16).toString()//不合格原因==>//12不良原因
                        , objectList1.get(15).toString()//状态   ==>//13状态
                );
                Log.e("#######1", dbPick.toString());
                dbZszfList.add(dbPick);
            }
            mDbRepository.deleteDbCgtzd(tm);
            mDbRepository.saveDbCgtzd(dbZszfList);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_cg_adapter, null, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            List<Object> objList = (List<Object>) list.get(position);

            //                0到货单别，1到货单号，2供应商编号，3供应商名称，4序号，5品号，6品名,7规格，8交货仓库，
//                9仓库名称10合格数量，11验退数量 12条码类别，13品号属性，14是否供应商条码，15单位
// ，16入库数量 17状态
            holder.tvShdh.setText(objList.get(5).toString());
            holder.tvXc.setText(objList.get(4).toString());
            holder.tvGg.setText(objList.get(7).toString());
            holder.tvWlname.setText(objList.get(6).toString());
            holder.tvCk.setText(objList.get(9).toString());
            tvGysname.setText(objList.get(3).toString());
            tvDhdh.setText(tm);
            holder.tvDysl.setText(String.valueOf(objList.get(10)));
            holder.tvRksl.setText(String.valueOf(objList.get(10)));
            holder.tvYtsl.setText(String.valueOf(objList.get(11)));
            holder.tvYssl.setText(String.valueOf(objList.get(10)));
//            holder.tvPc.setText(String.valueOf(objList.get()));
//
            holder.btnAddPc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CgrkActivity.this, CgrkAddPcActivity.class);
                    intent.putExtra("data", objList.toString());
                    for (int i = 0; i < ListUtils.getSize(mapList); i++) {
                        if (position == Integer.parseInt(String.valueOf(mapList.get(i).get("position")))) {
                            ArrayList<String> o = (ArrayList<String>) mapList.get(position).get("data");
                            intent.putExtra("pcList", o);
                        }
                    }
//                    intent.putExtra("id", tm + String.valueOf(objList.get(5)) + position);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 24);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_shdh)
            TextView tvShdh;//品号
            @BindView(R.id.tv_xc)
            TextView tvXc;//序号
            @BindView(R.id.tv_gg)
            TextView tvGg;
            @BindView(R.id.tv_wlname)
            TextView tvWlname;//品名
            @BindView(R.id.tv_gysname)
            TextView tvGysname;
            @BindView(R.id.tv_dysl)
            TextView tvDysl;//匹配数量
            @BindView(R.id.listitem_content)
            LinearLayout linlayContent;
            @BindView(R.id.listitem_success)
            FrameLayout linlaySuccess;
            @BindView(R.id.listitem_xhcz)
            FrameLayout falayDhys;
            @BindView(R.id.tv_rksl)
            TextView tvRksl;
            @BindView(R.id.btn_add_pc)
            Button btnAddPc;
            @BindView(R.id.tv_ck)
            TextView tvCk;
            @BindView(R.id.tv_yssl)
            TextView tvYssl;
            @BindView(R.id.tv_ytsl)
            TextView tvYtsl;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
