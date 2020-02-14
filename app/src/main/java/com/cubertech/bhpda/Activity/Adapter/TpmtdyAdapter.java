package com.cubertech.bhpda.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cubertech.bhpda.R;

import java.util.ArrayList;
import java.util.Map;

public class TpmtdyAdapter extends RecyclerView.Adapter<TpmtdyAdapter.ViewHolder> {
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    private LayoutInflater mInflater;
    private ArrayList<Map<String, Object>> listData;

    public TpmtdyAdapter(Context context,
                         ArrayList<Map<String, Object>> listData, TpmtdyAdapter.OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mInflater = LayoutInflater.from(context);
        this.listData = listData;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.tpmtdy_activity_adapter_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_mtm.setText(listData.get(position).get("mtm").toString());
        holder.tv_mtm_count.setText(listData.get(position).get("mtmcount").toString());
        holder.imgv_reduce.setOnClickListener(new View.OnClickListener() {
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
     * 删除某个数据
     *
     * @param i
     */
    public void delItem(int i) {
        listData.remove(i);
        //addItems(listData);
        notifyDataSetChanged();
    }

    /**
     * 重新加载数据
     *
     * @param newlistData
     */
    public void addItems(ArrayList<Map<String, Object>> newlistData) {
        listData.removeAll(listData);
        listData.addAll(newlistData);
        notifyDataSetChanged();
    }

    /**
     * 单个价值数据
     *
     * @param data
     */
    public void addItem(Map<String, Object> data) {
        listData.add(data);
        //addItems();
        notifyDataSetChanged();
    }

    public ArrayList getData() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            arrayList.add(listData.get(i).get("mtm").toString());
        }
        return arrayList;
    }

    public double getNumCount() {
        double count = 0.00;
        try {
            for (int i = 0; i < listData.size(); i++) {
                double mtmcount = Double.parseDouble(listData.get(i).get("mtmcount").toString());
                count += mtmcount;
            }
        } catch (Exception e) {
            Log.e("tpm", "格式转换异常");
            e.printStackTrace();
        }

        return count;
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

        private EditText tv_mtm;
        private ImageView imgv_reduce;
        private EditText tv_mtm_count;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_mtm_count = (EditText) itemView.findViewById(R.id.tv_mtm_count);
            tv_mtm = (EditText) itemView.findViewById(R.id.tv_mtm);//唛头码
            imgv_reduce = (ImageView) itemView.findViewById(R.id.imgv_del);//删除
        }
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
