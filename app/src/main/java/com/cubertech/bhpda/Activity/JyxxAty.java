package com.cubertech.bhpda.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cubertech.bhpda.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/3.
 */

public class JyxxAty extends AppCompatActivity {

    @BindView(R.id.jyxx_list)
    RecyclerView jyxxList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_jyxx);
        ButterKnife.bind(this);
        MyAdapter adapter = new MyAdapter();
        jyxxList.setLayoutManager(new LinearLayoutManager(this));
        jyxxList.setAdapter(adapter);

    }

    @OnClick({R.id.cancel, R.id.btn_submit, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_submit:
                break;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_jyxx, null, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(JyxxAty.this, JylrAty.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.list_jyxx_content)
            LinearLayout linlayContent;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
