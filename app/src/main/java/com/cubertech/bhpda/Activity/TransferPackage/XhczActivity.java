package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 销货操作原值（新的NewXhczAcitvity）
 * Created by Administrator on 2018/7/9.
 */

public class XhczActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SiteService siteService;
    private TKApplication application;
    AccountInfo ai;

    private RecyclerView xhcz_recycle;
    private MyXhczAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xhcz);

        application = (TKApplication) getApplication();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");

        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(this);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        xhcz_recycle = (RecyclerView) findViewById(R.id.xhcz_recycle);
        xhcz_recycle.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.xhcz_swipe);
        refreshLayout.setOnRefreshListener(this);
       /* for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("hpdh", "32123");
            map.put("tv_cpph", "pinhao");
            map.put("tv_cppm", "pinming");
            map.put("tv_cpgg", "guige");
            map.put("et_zysl", "2");
            map.put("state", "first");

            list.add(map);
        }*/

        adapter = new MyXhczAdapter(list);
        xhcz_recycle.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        onUrlLoad(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }

    private void upSuccess(int position) {
        Map<String, String> map = list.get(position);
        map.put("state", "end");
        adapter.setList(list);
        Log.e("##", list.toString());
    }

    @Override
    public void onRefresh() {

        onUrlLoad(false);
        /* for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("hpdh", "32123");
            map.put("tv_cpph", "pinhao");
            map.put("tv_cppm", "pinming");
            map.put("tv_cpgg", "guige");
            map.put("et_zysl", "2");
            map.put("state", "first");

            list.add(map);
        }

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });*/

    }

    private void onUrlLoad(boolean isProgress) {
        if (list.size() > 0) {
            list.clear();
            adapter.notifyDataSetChanged();
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("database", ai.getData());
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //通过Application传值
        String IP = "";
        String port = "";
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        //先弄一个观察者
        //        todo 此处接口名称未改
        Observable<List<Object>> observable = siteService.getXH_new(params,
                XhczActivity.this, IP, port,isProgress);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                refreshLayout.setRefreshing(false);//关闭下拉控件
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                refreshLayout.setRefreshing(false);//关闭下拉控件
                Toast.makeText(XhczActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }

                for (Object o : obj) {
                    List<Object> oo = (List<Object>) o;
                    Map<String, String> map = new HashMap<>();
                    map.put("tvChtzddb", oo.get(0).toString().trim());// 出货通知单别[0]
                    map.put("tvChtzddh", oo.get(1).toString().trim());// 出货通知单号[1]
                    map.put("tvkhbm", oo.get(2).toString().trim());// 出货通知序号[2]=>//客户编码
                    map.put("tvkhmc", oo.get(3).toString().trim());// 订单单别[3]=>//客户名称
//                    map.put("tvDddh", oo.get(4).toString().trim());// 订单单号[4]
//                    map.put("tvDdxh", oo.get(5).toString().trim());// 订单序号[5]
//                    map.put("tvPh", oo.get(6).toString().trim());// 品号[6]
//                    map.put("tvPm", oo.get(7).toString().trim());// 品名[7]
//                    map.put("tvGg", oo.get(8).toString().trim());// 规格[8]
//                    map.put("tvYjxhsl", oo.get(9).toString().trim());// 预计销货数量[9]
//                    map.put("state", "first");

                    list.add(map);

                }
                adapter.setList(list);

            }
        });
    }

    public class MyXhczAdapter extends RecyclerView.Adapter<MyXhczAdapter.ViewHolder> {


        private List<Map<String, String>> list = new ArrayList<>();

        public MyXhczAdapter(List<Map<String, String>> list) {
            this.list = list;
        }

        public void setList(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_xhcz_adapter, null, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Map<String, String> map = list.get(position);

            holder.tvChtzddb.setText(map.get("tvChtzddb").toString());
            holder.tvChtzddh.setText(map.get("tvChtzddh").toString());
            holder.tvKhid.setText(map.get("tvkhbm"));
            holder.tvKhname.setText(map.get("tvkhmc"));
//            holder.tvChtzdxh.setText(map.get("tvChtzdxh").toString());
//            holder.tvDddb.setText(map.get("tvDddb").toString());
//            holder.tvDddh.setText(map.get("tvDddh").toString());
//            holder.tvDdxh.setText(map.get("tvDdxh").toString());
//            holder.tvPh.setText(map.get("tvPh").toString());
//            holder.tvPm.setText(map.get("tvPm").toString());
//            holder.tvGg.setText(map.get("tvGg").toString());
//            holder.tvYjxhsl.setText(map.get("tvYjxhsl").toString());
//            if (TextUtils.equals(map.get("state"), "first")) {
//                holder.falaySuccess.setVisibility(View.GONE);
//            } else if (TextUtils.equals(map.get("state"), "end")) {
//                holder.falaySuccess.setVisibility(View.VISIBLE);
//            }
            holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (TextUtils.equals(map.get("state"), "end")) {
//                        return;
//                    }
//                    AlertDialog.Builder builder = new AlertDialog.Builder(XhczActivity.this);
//                    builder.setTitle("提示");
//                    builder.setMessage("确定要对此货品进行销货操作么?");
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            upSuccess(position);
//                        }
//                    });
//                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    builder.show();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("account", ai);
                    Intent intent = new Intent();
                    intent.putExtra("chtzddh", map.get("tvChtzddh"));
                    intent.putExtra("chtzddb", map.get("tvChtzddb"));
                    intent.putExtras(bundle);
                    intent.setClass(XhczActivity.this, XHCZDetailActivity.class);//销货操作
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
            /*holder.tvChtzddb.setText("chtzddb");
            holder.tvChtzddh.setText("chtzddb");
            holder.tvChtzdxh.setText("chtzddb");
            holder.tvDddb.setText("dddb");
            holder.tvDddh.setText("dddh");
            holder.tvDdxh.setText("ddxh");
            holder.tvPh.setText("ph");
            holder.tvPm.setText("pm");
            holder.tvGg.setText("gg");
            holder.tvYjxhsl.setText("sl");*/


        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private FrameLayout falaySuccess;
            private LinearLayout linlayContent;
            @BindView(R.id.tv_chtzddb)
            TextView tvChtzddb; //出库通知单别
            @BindView(R.id.tv_chtzddh)
            TextView tvChtzddh;//出库通知单号
            @BindView(R.id.tv_khid)
            TextView tvKhid;//客户编号
            @BindView(R.id.tv_khname)
            TextView tvKhname;//客户名称
//            @BindView(R.id.tv_chtzdxh)
//            TextView tvChtzdxh;//出库通知序号
//            @BindView(R.id.tv_dddb)
//            TextView tvDddb;//订单单别
//            @BindView(R.id.tv_ddxh)
//            TextView tvDdxh;//订单序号
//            @BindView(R.id.tv_dddh)
//            TextView tvDddh;////订单单号
//            @BindView(R.id.tv_ph)
//            TextView tvPh;//产品品号
//            @BindView(R.id.tv_pm)
//            TextView tvPm;//产品品名
//            @BindView(R.id.tv_gg)
//            TextView tvGg;//产品规格
//            @BindView(R.id.tv_yjxhsl)
//            TextView tvYjxhsl;//预计销货数量


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                falaySuccess = (FrameLayout) itemView.findViewById(R.id.listitem_success);
                linlayContent = (LinearLayout) itemView.findViewById(R.id.listitem_content);
            }
        }
    }
}
