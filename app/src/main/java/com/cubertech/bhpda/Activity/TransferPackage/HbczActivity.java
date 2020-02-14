package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.Fragment.Cjcz.CjczLeftFragment;
import com.cubertech.bhpda.Activity.Fragment.Cjcz.CjczRightFragment;
import com.cubertech.bhpda.Activity.Fragment.Hbcz.HbczLeftFragment;
import com.cubertech.bhpda.Activity.Fragment.Hbcz.HbczRightFragment;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.ListUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 合并操作
 * Created by Administrator on 2018/7/9.
 */

public class HbczActivity extends AppCompatActivity implements View.OnClickListener {

    private SiteService siteService;
    private static int TAB_MARGIN_DIP = 5;
    AccountInfo ai;
    private TKApplication application;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<Fragment> list;
    private MyAdapter adapter;
    private String[] titles = {" 合箱 ", " 合托 "};

    HbczLeftFragment leftFragment;

    HbczRightFragment rightFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hbcz);
        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(this);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        Button btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //todo
        ai = bundle.getParcelable("account");
        application = (TKApplication) getApplication();

        Bundle bundleF = new Bundle();
        bundleF.putParcelable("account", ai);


        //实例化
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        //页面，数据源
        list = new ArrayList<>();
       /* list.add(new LeftFragment());
        list.add(new RightFragment());*/
        //LeftFragment leftFragment = new LeftFragment();
        leftFragment = new HbczLeftFragment();
        leftFragment.setArguments(bundleF);
        leftFragment.application = application;
        //RightFragment rightFragment=new RightFragment();
        rightFragment = new HbczRightFragment();
        rightFragment.setArguments(bundleF);
        rightFragment.application = application;
        list.add(leftFragment);
        list.add(rightFragment);
        //ViewPager的适配器
        //adapter = new MyAdapter(getSupportFragmentManager());
        FragmentManager manager = getSupportFragmentManager();
        adapter = new MyAdapter(manager);
        viewPager.setAdapter(adapter);
        //绑定
        tabLayout.setupWithViewPager(viewPager);
        //MODE_SCROLLABLE可滑动的展示 0
        //MODE_FIXED固定展示(默认)1
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //TabLayout添加分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.layout_divider_vertical);

        /*linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));*/
        linearLayout.setDividerDrawable(drawable);

        setIndicator(this, tabLayout, TAB_MARGIN_DIP, TAB_MARGIN_DIP);

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
                onBackPressed();
                break;
            case R.id.btn_cancel:
                if (TextUtils.equals(adapter.getPageTitle(tabLayout.getSelectedTabPosition()), titles[0])) {
                    String tm = leftFragment.gettm();
                    if (tm.equals("")) {
                        Toast.makeText(HbczActivity.this, "请先扫描箱唛头码！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<Map<String, String>> list = leftFragment.onDataFinished(view);
                    if (ListUtils.isEmpty(list)) {
                        Toast.makeText(HbczActivity.this, "请先扫码！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("确定操作么?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //合并箱操作
                            HXSave(list, tm);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    String tm = rightFragment.gettm();
                    if (tm.equals("")) {
                        Toast.makeText(HbczActivity.this, "请先扫描托盘埋头码！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<Map<String, String>> maps = rightFragment.onDataFinished(view);
                    if (ListUtils.isEmpty(maps)) {
                        Toast.makeText(HbczActivity.this, "请先扫码！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("确定操做么?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //合并托操作
                            HTSave(maps, tm);
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
            case R.id.btn_submit:
                if (TextUtils.equals(adapter.getPageTitle(tabLayout.getSelectedTabPosition()), titles[0])) {

                    leftFragment.onShowDialog(view);
                } else {
                    rightFragment.onShowDialog(view);
                }

        }
    }

    /**
     * 合并箱保存
     *
     * @param list
     */
    public void HXSave(List<Map<String, String>> list, String tm) {
        List<Object> l = new ArrayList<>();
        for (Map<String, String> map : list) {
            List<Object> ls=new ArrayList<>();
            ls.add(map.get("et_tm"));//容器或箱码值
            ls.add(map.get("et_zysl"));//数量
            l.add(ls);
        }

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("list", l);
        params.put("database", ai.getData());
        params.put("XX001", tm);//合并后的箱唛头码
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
        Observable<String> observable = siteService.SaveHb_X(params,
                HbczActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                /*et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();*/
                Toast.makeText(HbczActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String obj) {
                if (!obj.equals("")) {
                    Toast.makeText(HbczActivity.this, "合并成功！", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

        });
    }

    public void HTSave(List<Map<String, String>> list, String tm) {
        List<Object> l = new ArrayList<>();
        for (Map<String, String> map : list) {
            //System.out.println(item);
            l.add(map.get("et_tm"));
        }

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("list", l);
        params.put("database", ai.getData());
        params.put("XX004", tm);//合并后的托盘唛头码
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
        Observable<String> observable = siteService.SaveHB_T(params,
                HbczActivity.this, IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                /*et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();*/
                Toast.makeText(HbczActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String obj) {
                if (!obj.equals("")) {
                    Toast.makeText(HbczActivity.this, "合并成功！", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

        });
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        //重写这个方法，将设置每个Tab的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

    /**
     * 底部横线的长度
     *
     * @param context
     * @param tabs
     * @param leftDip
     * @param rightDip
     */
    public static void setIndicator(Context context, TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout ll_tab = null;
        try {
            ll_tab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) (getDisplayMetrics(context).density * leftDip);
        int right = (int) (getDisplayMetrics(context).density * rightDip);

        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View child = ll_tab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric;
    }
}