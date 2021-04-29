package com.niantch.graproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.OneFragmentContentItemBinding
import com.niantch.graproject.model.Constants.RES_DETAIL
import com.niantch.graproject.model.ShopDetailModel
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.utils.ImageUtil

class ClassifyAdapter(var mContext: Context?, var homeRecShopDetailModelList: List<ShopDetailModel>? ): RecyclerView.Adapter<ClassifyAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return homeRecShopDetailModelList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = OneFragmentContentItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.oneFragmentItemReduceContainer.visibility = View.GONE

        //设置添加到购物车的数量，红点显示
        if (homeRecShopDetailModelList!![position].buyNum > 0) {
            holder.binding.oneContentItemBuyNum.text = homeRecShopDetailModelList!![position].buyNum.toString() + ""
            holder.binding.oneContentItemBuyNum.visibility = View.VISIBLE
        } else {
            holder.binding.oneContentItemBuyNum.visibility = View.GONE
        }

        //设置img
        ImageUtil.load(
            mContext!!,
            homeRecShopDetailModelList!![position].resImg,
            holder.binding.oneContentItemIv,
            ImageUtil.REQUEST_OPTIONS
        )
        //店名
        holder.binding.oneFragmentContentItemName.text = homeRecShopDetailModelList!![position].resName
        //评分
        holder.binding.oneFragmentStar.rating = homeRecShopDetailModelList!![position].resStar.toFloat()
        holder.binding.oneFragmentScore.text = homeRecShopDetailModelList!![position].resStar.toString() + ""
        //月售订单
        var orderNum =
            mContext!!.resources.getString(R.string.res_month_sell_order)
        orderNum = String.format(
            orderNum,
            homeRecShopDetailModelList!![position].resOrderNum
        )
        holder.binding.oneFragmentOrderNum.text = orderNum

        //起送
        var deliverMoney =
            mContext!!.resources.getString(R.string.res_deliver_money)
        deliverMoney = String.format(
            deliverMoney,
            homeRecShopDetailModelList!![position].resDeliverMoney
        )
        holder.binding.oneFragmentDeliver.text = deliverMoney

        //配送费
        var extraMoney = mContext!!.resources.getString(R.string.res_extra_money)
        extraMoney = String.format(
            extraMoney,
            homeRecShopDetailModelList!![position].resExtraMoney
        )
        holder.binding.oneFragmentExtra.text = extraMoney
        holder.binding.oneFragmentAddress.text = homeRecShopDetailModelList!![position].resAddress
        //配送时间
        var deliverTime =
            mContext!!.resources.getString(R.string.res_deliver_time)
        deliverTime = String.format(
            deliverTime,
            homeRecShopDetailModelList!![position].resDeliverTime
        )
        holder.binding.oneFragmentDeliverTime.text = deliverTime
        if (homeRecShopDetailModelList!![position]
                .discountList != null && homeRecShopDetailModelList!![position]
                        .discountList!!.isNotEmpty()
        ) {
            holder.binding.oneFragmentItemReduceContainer.visibility = View.VISIBLE
            holder.binding.divider.visibility = View.VISIBLE
            val sb = StringBuffer()
            for (discountBean in homeRecShopDetailModelList!![position]
                .discountList!!) {
                val fillPrice = discountBean.filledVal.toInt()
                val reducePrice = discountBean.reduceVal.toInt()
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
            holder.binding.oneFragmentItemReduce.text = sb.toString().substring(0, sb.length - 1)
        } else {
            holder.binding.divider.visibility = View.GONE
            holder.binding.oneFragmentItemReduceContainer.visibility = View.GONE
        }

        //设置每个item的点击事件
        holder.binding.root.setOnClickListener {
            val intent = Intent(mContext, ResActivity::class.java)
            intent.putExtra(RES_DETAIL, homeRecShopDetailModelList!![position])
            mContext!!.startActivity(intent)
        }
    }

    class ViewHolder(val binding: OneFragmentContentItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}