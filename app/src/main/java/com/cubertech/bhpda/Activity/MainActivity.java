package com.cubertech.bhpda.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.cubertech.bhpda.Activity.Adapter.MainPagerAdapter;
import com.cubertech.bhpda.Activity.Fragment.RelevanceFragment;
import com.cubertech.bhpda.Activity.Fragment.SetFragment;
import com.cubertech.bhpda.Activity.Fragment.StorageFragment;
import com.cubertech.bhpda.Activity.Fragment.TransferFragment;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int DEFAULT_PAGE_INDEX = 0;
    private Toolbar toolbar;
    private ViewPager viewPager;

    private RadioButton production, transfer, storage, qc;
    private Button setting;

    private List<RadioButton> radioButtons;

    private String[] mTitles;
    AccountInfo ai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setting = (Button) findViewById(R.id.button_settings);


        production = (RadioButton) findViewById(R.id.radio1);//投产
        transfer = (RadioButton) findViewById(R.id.radio2);//转移
        storage = (RadioButton) findViewById(R.id.radio3);//仓库
        qc = (RadioButton) findViewById(R.id.radio4);//设置

        //新增内容
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ai = bundle.getParcelable("account");
        String per = bundle.getString("permission");//权限
        Log.i("loong",per+"");

        production.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onRadioButtonChecked((RadioButton) buttonView, isChecked);
            }

        });

        transfer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onRadioButtonChecked((RadioButton) buttonView, isChecked);
            }
        });
        storage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onRadioButtonChecked((RadioButton) buttonView, isChecked);
            }

        });

        qc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onRadioButtonChecked((RadioButton) buttonView, isChecked);
            }
        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onBackPressed();
                test();
            }
        });

        String[] str = per.replace("[","").replace("]","").split(":");
        //Fragment[] fragments = new Fragment[str.length];
        int fcount = 0;
        radioButtons = new ArrayList<RadioButton>();
        mTitles = getResources().getStringArray(R.array.mp_main_titles);
        List<String> list = new ArrayList();
        List<Fragment> listf = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            if (!str[i].toString().equals("-1")) {
                //list.add(mTitles[i]);
                switch (i) {
                    case 0:
                        //工序转移
                        radioButtons.add(production);
                        listf.add(TransferFragment.newInstance("", "", ai.getData(),
                                ai.getName(), str[0].toString()));//工艺转移
                        list.add(mTitles[0]);
                        fcount++;
                        production.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        //关联关系
                        radioButtons.add(transfer);
                        listf.add(RelevanceFragment.newInstance("", "", ai.getData(),
                                ai.getName(), str[1].toString()));
                        list.add(mTitles[1]);
                        fcount++;
                        transfer.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        //出入库
                        radioButtons.add(storage);
                        //出入库
                        listf.add(StorageFragment.newInstance("", "", ai.getData(),
                                ai.getName(), str[2].toString()));
                        list.add(mTitles[2]);
                        fcount++;
                        storage.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        list.add(mTitles[3]);
        //listf.add(new SetFragment());//设置
        listf.add(SetFragment.newInstance("","",ai.getData(),ai.getName()));
        radioButtons.add(qc);
        fcount++;
        //Fragment[] fragments = new Fragment[fcount];
        //String[] toBeStored = list.toArray(new String[list.size()]);
        Fragment[] fragments = listf.toArray(new Fragment[fcount]);
        String[] Titles = list.toArray(new String[fcount]);
        mTitles=Titles;//最后对应 onItemChecked

        /*radioButtons = new ArrayList<RadioButton>();
        radioButtons.add(production);
        radioButtons.add(transfer);
        radioButtons.add(storage);
        radioButtons.add(qc);
        // Main Controls' Titles
        mTitles = getResources().getStringArray(R.array.mp_main_titles);

        Fragment[] fragments = new Fragment[mTitles.length];
       *//* fragments[0]=new ProductionFragment();*//*
         *//*fragments[3]=new QCFragment();*//*
        fragments[0]=new TransferFragment();//工艺转移
        fragments[1]=new RelevanceFragment();//关联关系
        fragments[2]=new StorageFragment();//出入库
        fragments[3]=new SetFragment();//设置*/


        //MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), mTitles, fragments);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), Titles, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.mp_margin_large));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Empty
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty
            }

            @Override
            public void onPageSelected(int position) {
                radioButtons.get(position).setChecked(true);
            }
        });

        radioButtons.get(DEFAULT_PAGE_INDEX).setChecked(true);

    }

    /*@Override 原
    public void onBackPressed() {
        moveTaskToBack(true);
    }*/


    private void test() {
        onBackPressed();
    }

    /**
     * 退出程序提示框
     */
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this,R.style.MP_Theme_alertDialog);
        normalDialog.setMessage("是否确定退出系统");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
       /* View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.dialog_finish, null);
        final Dialog dialog = new Dialog(MainActivity.this,
                R.style.dialog_backTransparent);
        Button btn_sure = (Button) view.findViewById(R.id.btn_sure);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                MainActivity.this.finish();
                dialog.dismiss();

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();*/
    }

    public void onRadioButtonChecked(RadioButton button, boolean isChecked) {
        if (isChecked) {
            onItemChecked(radioButtons.indexOf(button));
        }
    }

    private void onItemChecked(int position) {
        //Toast.makeText(this,mTitles[position],Toast.LENGTH_LONG).show();
        viewPager.setCurrentItem(position);
        toolbar.setTitle(mTitles[position]);
    }
}
