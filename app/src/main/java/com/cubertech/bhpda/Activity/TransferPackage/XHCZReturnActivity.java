package com.cubertech.bhpda.Activity.TransferPackage;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class XHCZReturnActivity extends AppCompatActivity {

    @BindView(R.id.xhcz_return_list)
    ListView mReturnList;

    private MyReturnAdapter adapter;
    private AccountInfo ai;

    @BindView(R.id.et_value)
    EditText et_value;


    int IDS=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xhczreturn);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        IDS= intent.getIntExtra("IDS",-1);

        adapter=new MyReturnAdapter();

        mReturnList.setAdapter(adapter);

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
                    insertData(et_value.getText().toString().trim());
                }
                return false;
            }
        });

    }
    /**
     * 插入数据到listview中
     * 作用记录要返库的数据
     * @param data
     */
    private void insertData(String data){
        List<Object> l = adapter.getLists();
        for(Object o : l){
            if(o.toString().equals(data)){
                et_value.setText("");
                et_value.postDelayed(new Runnable() {//非线程则requestFocus不起作用
                    @Override
                    public void run() {
                        et_value.requestFocus();//
                    }
                },100);
                ToastUtils.showToast("已经存在不能再次扫描");
                return;
            }
        }

        l.add(data);
        adapter.setLists(l);

        et_value.setText("");
        et_value.postDelayed(new Runnable() {//非线程则requestFocus不起作用
            @Override
            public void run() {
                et_value.requestFocus();//
            }
        },100);

    }


    @OnClick({R.id.left, R.id.btn_submit, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_cancel://取消
                if(adapter.getLists().size()>0) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("退出后数据清空");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<Object> listrs = new ArrayList<>();
                            intentTemp = new Intent();
                            intentTemp.putExtra("IDS", IDS);
                            intentTemp.putExtra("listr", (Serializable) listrs);
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
                }else{
                    onBackPressed();
                }

                break;
            case R.id.btn_submit://确定
                //读取数据返回相应的值
                List<Object> listr=adapter.getLists();
                intentTemp = new Intent();
                intentTemp.putExtra("IDS", IDS);
                intentTemp.putExtra("listr", (Serializable) listr);
                //setResult(2, intentTemp);


                onBackPressed();
                break;
        }

    }

    Intent intentTemp=null;

    @Override
    public void onBackPressed() {

        if(intentTemp==null){
            List<Object> listr =new ArrayList<>();
            intentTemp = new Intent();
            intentTemp.putExtra("IDS", IDS);
            intentTemp.putExtra("listr", (Serializable) listr);

        }
        setResult(2, intentTemp);
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
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
            //et_tm.setText(ScanResult);
            //scanCode();

            //insertData(et_value.getText().toString().trim());
            insertData(ScanResult);
        }
    }

    public class MyReturnAdapter extends BaseAdapter {
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
                view = LayoutInflater.from(XHCZReturnActivity.this).inflate(R.layout.listitem_xhcz_return_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
           // List<Object> obj = (List<Object>) lists.get(i);
            if (!ListUtils.isEmpty(lists)){
                holder.tvmz.setText(lists.get(i).toString());//[0]MZ箱码或托盘码值

            }
            return view;
        }

        public class ViewHolder {
            @BindView(R.id.tv_mz)
            TextView tvmz;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }



}
