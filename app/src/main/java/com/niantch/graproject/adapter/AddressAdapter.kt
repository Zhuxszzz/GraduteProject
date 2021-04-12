package com.niantch.graproject.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.AddressItemBinding
import com.niantch.graproject.utils.GlobalContextUtil

/**
 * author: niantchzhu
 * date: 2021
 */
class AddressAdapter: RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private val addressSet = mutableListOf<String>()
    var selectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = AddressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvAddress.setOnClickListener {
            selectedIndex = addressSet.indexOf(binding.tvAddress.text)
            notifyDataSetChanged()
        }
        return AddressViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return addressSet.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.binding.tvAddress.text = addressSet[position]
        if(position == selectedIndex) {
            holder.binding.tvAddress.setBackgroundColor(GlobalContextUtil.globalContext!!.getColor(R.color.selected_color))
        } else {
            holder.binding.tvAddress.setBackgroundColor(GlobalContextUtil.globalContext!!.getColor(R.color.white))
        }
    }

    fun refreshDataSet(list: MutableList<String>) {
        addressSet.clear()
        addressSet.addAll(list)
        selectedIndex = -1
        notifyDataSetChanged()
    }

    class AddressViewHolder(val binding: AddressItemBinding): RecyclerView.ViewHolder(binding.root) {
    }


}