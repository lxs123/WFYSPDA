package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.content.Context;
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

import static java.security.AccessController.getContext;

/**
 * 拆解操作界面
 * Created by Administrator on 2018/7/9.
 */

public class CjczActivity extends AppCompatActivity implements View.OnClickListener {

    private SiteService siteService;
    private static int TAB_MARGIN_DIP = 5;
    AccountInfo ai;
    private TKApplication application;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<Fragment> list;
    private MyAdapter adapter;
    private String[] titles = {" 拆箱 ", " 拆托 "};

    CjczLeftFragment leftFragment;

    CjczRightFragment rightFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cjcz);
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
        leftFragment = new CjczLeftFragment();
        leftFragment.setArguments(bundleF);
        leftFragment.application=application;
        //RightFragment rightFragment=new RightFragment();
        rightFragment = new CjczRightFragment();
        rightFragment.setArguments(bundleF);
        rightFragment.application=application;
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

    /**
     * @see android.app.Activity#onBackPressed() 返回事件 @ add wlg
     */
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
            case R.id.btn_cancel:
                if (TextUtils.equals(adapter.getPageTitle(tabLayout.getSelectedTabPosition()), titles[0])) {
                    List<Map<String, String>> list = leftFragment.onDataFinished(view);
                    if (ListUtils.isEmpty(list)) {
                        return;
                    }
                    Log.e("###", list.toString());
                    //拆箱操作
                    cxsave(list);
                } else {
                    List<Map<String, String>> maps = rightFragment.onDataFinished(view);
                    if (ListUtils.isEmpty(maps)) {
                        return;
                    }
                    Log.e("####", maps.toString());
                    //拆托操作
                    ctsave(maps);
                }
                break;
            case R.id.btn_submit:
                if (TextUtils.equals(adapter.getPageTitle(tabLayout.getSelectedTabPosition()), titles[0])) {
                    leftFragment.onShowDialog(view);
                } else {
                    rightFragment.onShowDialog(view);
                }

                break;
        }
    }

    /**
     * 拆托保存
     */
    private void ctsave(List<Map<String, String>> list){
        List<Object> l=new ArrayList<>();
        for(Map<String,String> map: list){
            //System.out.println(item);
            l.add(map.get("et_tm"));
        }

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("list", l);
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
        Observable<String> observable = siteService.SaveCj_T(params,
                CjczActivity.this, IP, port);
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
                Toast.makeText(CjczActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String obj) {
                if (!obj.equals("")) {
                    Toast.makeText(CjczActivity.this, "拆托成功！", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

        });
    }

    /**
     * 拆箱保存
     */
    private void cxsave(List<Map<String, String>> list){
        List<Object> l=new ArrayList<>();
        for(Map<String,String> map: list){
            //System.out.println(item);
            l.add(map.get("et_tm"));
        }

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("list", l);
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
        Observable<String> observable = siteService.SaveCj_X(params,
                CjczActivity.this, IP, port);
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
                Toast.makeText(CjczActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String obj) {
                if (!obj.equals("")) {
                    Toast.makeText(CjczActivity.this, "拆箱成功！", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

        });
    }
}
