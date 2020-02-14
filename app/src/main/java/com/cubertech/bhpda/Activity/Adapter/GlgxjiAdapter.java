package com.cubertech.bhpda.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.utils.ListUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/11.
 */

public class GlgxjiAdapter extends RecyclerView.Adapter<GlgxjiAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<Map<String, Object>> listData;
    //private Context context;

    public GlgxjiAdapter(Context context,
                         ArrayList<Map<String, Object>> listData) {
        mInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    public void setListData(ArrayList<Map<String, Object>> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.glgxjl_activity_adapter_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_b_tm.setText(listData.get(position).get("b_tm").toString());
        holder.tv_b_yjph.setText(listData.get(position).get("b_yjph").toString());
        holder.tv_b_yjmc.setText(listData.get(position).get("b_yjmc").toString());
        holder.tv_b_yjgg.setText(listData.get(position).get("b_yjgg").toString());
    }

    @Override
    public int getItemCount() {
        return ListUtils.getSize(listData);
    }

    /**
     * 返回listData
     *
     * @return
     */
    public ArrayList<Map<String, Object>> getListData() {
        return listData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_b_tm, tv_b_yjph, tv_b_yjmc, tv_b_yjgg;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_b_tm = (TextView) itemView.findViewById(R.id.tv_b_tm);//元件条码
            tv_b_yjph = (TextView) itemView.findViewById(R.id.tv_b_yjph);//元件品号
            tv_b_yjmc = (TextView) itemView.findViewById(R.id.tv_b_yjmc);//元件“品名”
            tv_b_yjgg = (TextView) itemView.findViewById(R.id.tv_b_yjgg);//元件规格
        }
    }

    public void addItems(ArrayList<Map<String, Object>> newlistData) {
        listData.removeAll(listData);
        listData.addAll(newlistData);
        notifyDataSetChanged();
    }

    public void addItem(Map<String, Object> newDatas) {
        listData.add(newDatas);
        notifyDataSetChanged();
    }

    public boolean equalsBarcode(String tm) {
        boolean isEquals = false;
        for (int i = 0; i < ListUtils.getSize(listData); i++) {
            if (TextUtils.equals(listData.get(i).get("b_tm").toString().trim(), tm.trim())) {
                isEquals = true;
                break;
            }
        }
        return isEquals;
    }
}
