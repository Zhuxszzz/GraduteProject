package com.niantch.graproject.adapter

import android.content.Context
import android.view.LayoutInflater

import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.TotalAddressItemBinding
import com.niantch.graproject.model.AddressModel

/**
 * author: niantchzhu
 * date: 2021
 */
class AddressAdapter(val context: Context,val list: ArrayList<AddressModel>): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemDeleteListener: OnItemDeleteListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemDeleteListener(onItemDeleteListener: OnItemDeleteListener?) {
        this.onItemDeleteListener = onItemDeleteListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?) {
        this.onItemLongClickListener = onItemLongClickListener
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TotalAddressItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvAddress.text = list[position].address
        holder.binding.tvName.text = list[position].name
        holder.binding.tvPhone.text = list[position].phone
        holder.binding.addressDelete.setOnClickListener {
            if (onItemDeleteListener != null) {
                onItemDeleteListener!!.onItemDelete(position)
            }
        }
        holder.itemView.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClick(position)
            }
        }
        holder.itemView.setOnLongClickListener(OnLongClickListener {
            if (onItemLongClickListener != null) {
                onItemLongClickListener!!.onItemLongClick(position)
                return@OnLongClickListener true
            }
            false
        })
    }

    class ViewHolder(var binding: TotalAddressItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemDeleteListener {
        fun onItemDelete(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

}