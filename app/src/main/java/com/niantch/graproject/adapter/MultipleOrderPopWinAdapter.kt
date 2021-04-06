package com.niantch.graproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.MultipleOrderItemBinding

/**
 * author: niantchzhu
 * date: 2021
 */
class MultipleOrderPopWinAdapter(private var mContext: Context?, private var list: List<String>?, private var onMultipleOrderItemClickListener: OnMultipleOrderItemClickListener?) : RecyclerView.Adapter<MultipleOrderPopWinAdapter.ViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.multiple_order_item, parent, false)
        val binding = MultipleOrderItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderMode.text = list!![position]
        if (selectedPosition == position) {
            holder.orderSelected.visibility = View.VISIBLE
            holder.orderMode.setTextColor(mContext!!.resources.getColor(R.color.bottom_tab_text_selected_color))
        } else {
            holder.orderSelected.visibility = View.GONE
            holder.orderMode.setTextColor(mContext!!.resources.getColor(R.color.black_60))
        }
        holder.root.setOnClickListener {
            if (onMultipleOrderItemClickListener != null) {
                onMultipleOrderItemClickListener!!.onMultipleOrderItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun setSelectedPosition(position: Int) {
        if (selectedPosition != position) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    class ViewHolder(binding: MultipleOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val orderMode: TextView = binding.orderMode
        val orderSelected: ImageView = binding.orderSelected
        val root: View = binding.root
    }

    interface OnMultipleOrderItemClickListener {
        fun onMultipleOrderItemClick(position: Int)
    }

    fun setOnMultipleOrderItemClickListener(onMultipleOrderItemClickListener: OnMultipleOrderItemClickListener?) {
        this.onMultipleOrderItemClickListener = onMultipleOrderItemClickListener
    }
}