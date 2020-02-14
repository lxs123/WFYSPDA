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
 * Created by Administrator on 2018/1/11.
 */

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<Map<String, Object>> listData;
    //private Context context;


    public QueryAdapter(Context context,
                        ArrayList<Map<String, Object>> listData){
        mInflater = LayoutInflater.from(context);
        this.listData=listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=mInflater.inflate(R.layout.query_fragment_adapter_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_q_cpph.setText(listData.get(position).get("cpph").toString());
        holder.tv_q_cpmc.setText(listData.get(position).get("cpmc").toString());
        holder.tv_q_yjph.setText(listData.get(position).get("yjph").toString());
        holder.tv_q_yjmc.setText(listData.get(position).get("yjmc").toString());
        holder.tv_q_yjcs.setText(listData.get(position).get("yjcs").toString());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_q_cpph,tv_q_cpmc, tv_q_yjph,tv_q_yjmc, tv_q_yjcs;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_q_cpph=(TextView) itemView.findViewById(R.id.tv_q_cpph);//产品品号
            tv_q_cpmc=(TextView) itemView.findViewById(R.id.tv_q_cpmc);//产品品名
            tv_q_yjph=(TextView) itemView.findViewById(R.id.tv_q_yjph);//元件品号
            tv_q_yjmc=(TextView) itemView.findViewById(R.id.tv_q_yjmc);//元件名称
            tv_q_yjcs=(TextView) itemView.findViewById(R.id.tv_q_yjcs);//元件厂商
        }
    }

    public void addItems(ArrayList<Map<String,Object>> newlistData){
        listData.removeAll(listData);
        listData.addAll(newlistData);
        notifyDataSetChanged();
    }
}
