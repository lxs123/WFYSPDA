package com.cubertech.bhpda.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cubertech.bhpda.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/8.
 */

public class PURMAAdapter extends RecyclerView.Adapter<PURMAAdapter.ViewHolder> {
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    private LayoutInflater mInflater;
    private ArrayList<Map<String, Object>> listData;
    //private Context context;


    public PURMAAdapter(Context context,
                        ArrayList<Map<String, Object>> listData) {
        mInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    public PURMAAdapter(Context context,
                        ArrayList<Map<String, Object>> listData, OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mInflater = LayoutInflater.from(context);
        this.listData = listData;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.query_adapter_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_bh.setText(listData.get(position).get("bh").toString());
        holder.tv_mc.setText(listData.get(position).get("mc").toString());
        holder.ly_xz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    /**
     * 删除所有数据
     */
    public void delAll() {
        listData.removeAll(listData);
        notifyDataSetChanged();

    }

    /**
     * 返回指定Map
     *
     * @param items
     * @return
     */
    public Map<String, Object> getItem(int items) {
        return listData.get(items);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_bh, tv_mc;
        private LinearLayout ly_xz;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_bh = (TextView) itemView.findViewById(R.id.tv_bh);//编号
            tv_mc = (TextView) itemView.findViewById(R.id.tv_mc);//名称
            ly_xz = (LinearLayout) itemView.findViewById(R.id.ly_xz);//
        }
    }

    public void addItems(ArrayList<Map<String, Object>> newlistData) {
        listData.removeAll(listData);
        listData.addAll(newlistData);
        notifyDataSetChanged();
    }

    public interface OnRecyclerItemClickListener {
        /**
         * item view 回调方法
         *
         * @param view     被点击的view
         * @param position 点击索引
         */
        void onItemClick(View view, int position);
    }
}
