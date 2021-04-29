package com.niantch.graproject.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.niantch.graproject.R;
import androidx.recyclerview.widget.RecyclerView;
import com.niantch.graproject.model.AddressModel;
import java.util.List;


public class PopupAddressAdapter extends RecyclerView.Adapter<PopupAddressAdapter.ViewHolder>{
    private Context mContext;
    private List<AddressModel> list;
    private int selected = -1;
    private OnItemClickListener onItemClickListener;

    public void setSelected(int position){
        this.selected = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public PopupAddressAdapter(Context context,List<AddressModel> list){
        this.mContext = context;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv.setText(list.get(position).getAddress());
        if (position == selected){
            holder.selected.setVisibility(View.VISIBLE);
        }else{
            holder.selected.setVisibility(View.GONE);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_address_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View root;
        TextView tv;
        ImageButton selected;

        public ViewHolder(View root){
            super(root);
            this.root = root;
            this.tv = (TextView) root.findViewById(R.id.tv);
            this.selected = (ImageButton) root.findViewById(R.id.selected);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
