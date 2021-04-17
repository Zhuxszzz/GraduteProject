package com.niantch.graproject.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.niantch.graproject.R;
import androidx.recyclerview.widget.RecyclerView;
import com.niantch.graproject.model.GoodsCategoryModel;
import java.util.List;

/**
 * author: niantchzhu
 * date: 2021
 */

public class GoodsCategoryRecyclerAdapter extends RecyclerView.Adapter<GoodsCategoryRecyclerAdapter.ViewHolder> {
    //当前选中的位置
    private int selectPosition;
    private List<GoodsCategoryModel> dataList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public GoodsCategoryRecyclerAdapter(List<GoodsCategoryModel> dataList, Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public GoodsCategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_category_item, parent, false);
        return new GoodsCategoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodsCategoryRecyclerAdapter.ViewHolder holder, final int position) {
        holder.goodsCategoryName.setText(dataList.get(position).getName());
        if(dataList.get(position).getBuyNum()>0){
            holder.shopCartNum.setVisibility(View.VISIBLE);
            holder.shopCartNum.setText(String.valueOf(dataList.get(position).getBuyNum()));
        }else{
            holder.shopCartNum.setVisibility(View.GONE);
        }

        if (selectPosition != -1) {
            if (selectPosition == position) {
                holder.root.setBackgroundResource(R.color.white);
                holder.goodsCategoryName.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.goodsCategoryName.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            } else {
                holder.root.setBackgroundResource(R.color.goods_category_bg);
                holder.goodsCategoryName.setTextColor(mContext.getResources().getColor(R.color.color_666));
                holder.goodsCategoryName.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
            }
        } else {
            holder.root.setBackgroundResource(R.color.goods_category_bg);
            holder.goodsCategoryName.setTextColor(mContext.getResources().getColor(R.color.color_666));
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(view,position);
                }
            }
        });

    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void changeData(List<GoodsCategoryModel> dataList){
        this.dataList=dataList;
        notifyDataSetChanged();
    }

    /**
     * 设置选中index
     * @param position
     */
    public void setCheckPosition(int position) {
        this.selectPosition = position;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView goodsCategoryName;
        public final TextView shopCartNum;
        public final View root;

        public ViewHolder(View root) {
            super(root);
            goodsCategoryName = (TextView) root.findViewById(R.id.goodsCategoryNameTV);
            shopCartNum = (TextView) root.findViewById(R.id.shopCartNumTV);
            this.root = root;
        }

    }
}
