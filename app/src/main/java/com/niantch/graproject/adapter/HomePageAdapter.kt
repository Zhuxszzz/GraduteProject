package com.niantch.graproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.OneFragmentContentItemBinding
import com.niantch.graproject.model.ShopDetailModel
import com.niantch.graproject.ui.HomePageFragment
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.utils.ImageUtil

/**
 * author: niantchzhu
 * date: 2021
 */
class HomePageAdapter(val context: Context, var data: MutableList<ShopDetailModel>) : RecyclerView.Adapter<HomePageAdapter.ShopItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val binding = OneFragmentContentItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ShopItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = data[position]
        holder.binding.oneFragmentItemReduceContainer.visibility = View.GONE
        //设置添加到购物车的数量，红点显示
        if (item.buyNum > 0) {
            holder.binding.oneContentItemBuyNum.text = item.buyNum.toString() + ""
            holder.binding.oneContentItemBuyNum.visibility = View.VISIBLE
        } else {
            holder.binding.oneContentItemBuyNum.visibility = View.GONE
        }

        //设置img
        val iv: ImageView = holder.binding.oneContentItemIv
        ImageUtil.load(context, item.resImg, iv, ImageUtil.REQUEST_OPTIONS)
        //店名
        holder.binding.oneFragmentContentItemName.text = item.resName
        //评分
        val ratingBar: RatingBar = holder.binding.oneFragmentStar
        ratingBar.rating = item.resStar.toFloat()
        holder.binding.oneFragmentScore.text = item.resStar.toString() + ""
        //月售订单
        var orderNum: String = context.resources.getString(R.string.res_month_sell_order)
        orderNum = java.lang.String.format(orderNum, item.resOrderNum)
        holder.binding.oneFragmentOrderNum.text = orderNum

        //起送
        var deliverMoney: String = context.resources.getString(R.string.res_deliver_money)
        deliverMoney = java.lang.String.format(deliverMoney, item.resDeliverMoney)
        holder.binding.oneFragmentDeliver.text = deliverMoney

        //配送费
        if (item.resExtraMoney > 0) {
            var extraMoney: String = context.resources.getString(R.string.res_extra_money)
            extraMoney = java.lang.String.format(extraMoney, item.resExtraMoney)
            holder.binding.oneFragmentExtra.text = extraMoney
        } else {
            holder.binding.oneFragmentExtra.text = "免配送费"
        }
        holder.binding.oneFragmentAddress.text = item.resAddress
        //配送时间
        var deliverTime: String = context.resources.getString(R.string.res_deliver_time)
        deliverTime = java.lang.String.format(deliverTime, item.resDeliverTime)
        holder.binding.oneFragmentDeliverTime.text = deliverTime
        holder.binding.divider.visibility = View.GONE
        if (item.discountList != null && item.discountList?.isNotEmpty()!!) {
            holder.binding.oneFragmentItemReduceContainer.visibility = View.VISIBLE
            holder.binding.divider.visibility = View.VISIBLE
            val sb = StringBuffer()
            for (discountBean in item.discountList!!) {
                val fillPrice = discountBean.filledVal
                val reducePrice = discountBean.reduceVal
                if (discountBean.filledVal > fillPrice) {
                    sb.append("满" + discountBean.filledVal)
                } else {
                    sb.append("满$fillPrice")
                }
                if (discountBean.reduceVal > reducePrice) {
                    sb.append("减" + discountBean.reduceVal.toString() + ",")
                } else {
                    sb.append("减$reducePrice,")
                }
            }
            val discountString = sb.toString().substring(0, sb.length - 1)
            holder.binding.oneFragmentItemReduce.text = discountString
        }
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, ResActivity::class.java)
            intent.putExtra(HomePageFragment.RES_DETAIL, data[position])
            //启动具体店铺页面
            context.startActivity(intent)
        }
    }

    class ShopItemViewHolder(val binding: OneFragmentContentItemBinding): RecyclerView.ViewHolder(binding.root)

}