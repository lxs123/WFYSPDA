package com.cubertech.bhpda.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cubertech.bhpda.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/16.
 */

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<Map<String,Object>> listData;

    public TransferAdapter(Context context, ArrayList<Map<String,Object>> listData){
        mInflater = LayoutInflater.from(context);
        this.listData=listData;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=mInflater.inflate(R.layout.transfer_fragment_adapter_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(listData.get(position).get("name").toString());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_name=(TextView) itemView.findViewById(R.id.tv_name);//
        }
    }
}
