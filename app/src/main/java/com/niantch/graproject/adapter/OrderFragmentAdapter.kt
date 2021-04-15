package com.niantch.graproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.databinding.OrderFragmentItemBinding
import com.niantch.graproject.model.OrderBean
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.utils.ImageUtil
import java.lang.String

/**
 * author: niantchzhu
 * date: 2021
 */
class OrderFragmentAdapter(val context: Context, val orderList: List<OrderBean>) : RecyclerView.Adapter<OrderFragmentAdapter.ViewHolder>() {

    private var listener: OnItemBtnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderFragmentItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ImageUtil.load(context, orderList[position].resImg, holder.binding.orderFragmentItemResImg, ImageUtil.REQUEST_OPTIONS)
        holder.binding.orderFragmentItemResName.text = orderList[position].resName
        holder.binding.orderFragmentItemResBuyTime.text = orderList[position].orderTime
        holder.binding.orderFragmentItemResItemPrice.text = "￥" + orderList[position].orderPrice
        holder.binding.orderFragmentItemResItemName.text = orderList[position].orderDescription

        when (orderList[position].orderState) {
            2 -> {
                holder.binding.orderState.text = "商家待接单"
                holder.binding.orderFragmentItemBuy.text = "取消订单"
                holder.binding.orderFragmentItemBuy.setOnClickListener(View.OnClickListener {
                    listener?.onItemBtnClick(position, 2)
                })
            }
            3 -> {
                holder.binding.orderState.text = "商家制作中"
                holder.binding.orderFragmentItemBuy.text = "再来一单"
                holder.binding.orderFragmentItemBuy.setOnClickListener(View.OnClickListener {
                    val intent = Intent(context, ResActivity::class.java)
                    intent.putExtra(ResActivity.RES_ID, String.valueOf(orderList[position].resId))
                    context.startActivity(intent)
                })
            }
            4 -> {
                holder.binding.orderState.text = "商家派送中"
                holder.binding.orderFragmentItemBuy.text = "再来一单"
                holder.binding.orderFragmentItemBuy.setOnClickListener(View.OnClickListener {
                    val intent = Intent(context, ResActivity::class.java)
                    intent.putExtra(ResActivity.RES_ID, String.valueOf(orderList[position].resId))
                    context.startActivity(intent)
                })
            }
            5 -> {
                holder.binding.orderState.text = "订单已完成"
                holder.binding.orderFragmentItemBuy.text = "再来一单"
                holder.binding.orderFragmentItemBuy.setOnClickListener(View.OnClickListener {
                    val intent = Intent(context, ResActivity::class.java)
                    intent.putExtra(ResActivity.RES_ID, String.valueOf(orderList[position].resId))
                    context.startActivity(intent)
                })
            }
            6 -> {
                holder.binding.orderState.text = "订单待评价"
                holder.binding.orderFragmentItemBuy.text = "去评价"
            }
            0 -> {
                holder.binding.orderState.text = "订单已取消"
                holder.binding.orderFragmentItemBuy.text = "再来一单"
                holder.binding.orderFragmentItemBuy.setOnClickListener(View.OnClickListener {
                    val intent = Intent(context, ResActivity::class.java)
                    intent.putExtra(ResActivity.RES_ID, String.valueOf(orderList[position].resId))
                    context.startActivity(intent)
                })
            }
        }
    }


    class ViewHolder(val binding: OrderFragmentItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemBtnClickListener {
        fun onItemBtnClick(position: Int, state: Int)
    }

    fun setOnItemBtnClickListener(onItemBtnClickListener: OnItemBtnClickListener) {
        listener = onItemBtnClickListener
    }

}