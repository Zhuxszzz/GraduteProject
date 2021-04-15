package com.niantch.graproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.OneFragmentContentItemBinding
import com.niantch.graproject.model.Constants.RES_DETAIL
import com.niantch.graproject.model.ResDetailBean
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.utils.ImageUtil

class ClassifyResActivityAdapter(var mContext: Context?,var homeRecResDetailBeanList: List<ResDetailBean>? ): RecyclerView.Adapter<ClassifyResActivityAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return homeRecResDetailBeanList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = OneFragmentContentItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.one_fragment_item_reduce_container.visibility = View.GONE

        //设置添加到购物车的数量，红点显示
        if (homeRecResDetailBeanList!![position].buyNum > 0) {
            holder.one_content_item_buy_num.text = homeRecResDetailBeanList!![position].buyNum.toString() + ""
            holder.one_content_item_buy_num.visibility = View.VISIBLE
        } else {
            holder.one_content_item_buy_num.visibility = View.GONE
        }

        //设置img
        ImageUtil.load(
            mContext!!,
            homeRecResDetailBeanList!![position].resImg,
            holder.one_content_item_iv,
            ImageUtil.REQUEST_OPTIONS
        )
        //店名
        holder.one_fragment_content_item_name.text = homeRecResDetailBeanList!![position].resName
        //评分
        holder.one_fragment_star.rating = homeRecResDetailBeanList!![position].resStar
        holder.one_fragment_score.text = homeRecResDetailBeanList!![position].resStar.toString() + ""
        //月售订单
        var orderNum =
            mContext!!.resources.getString(R.string.res_month_sell_order)
        orderNum = String.format(
            orderNum,
            homeRecResDetailBeanList!![position].resOrderNum
        )
        holder.one_fragment_order_num.text = orderNum

        //起送
        var deliverMoney =
            mContext!!.resources.getString(R.string.res_deliver_money)
        deliverMoney = String.format(
            deliverMoney,
            homeRecResDetailBeanList!![position].resDeliverMoney
        )
        holder.one_fragment_deliver.text = deliverMoney

        //配送费
        var extraMoney = mContext!!.resources.getString(R.string.res_extra_money)
        extraMoney = String.format(
            extraMoney,
            homeRecResDetailBeanList!![position].resExtraMoney
        )
        holder.one_fragment_extra.text = extraMoney
        holder.one_fragment_address.text = homeRecResDetailBeanList!![position].resAddress
        //配送时间
        var deliverTime =
            mContext!!.resources.getString(R.string.res_deliver_time)
        deliverTime = String.format(
            deliverTime,
            homeRecResDetailBeanList!![position].resDeliverTime
        )
        holder.one_fragment_deliver_time.text = deliverTime
        if (homeRecResDetailBeanList!![position]
                .discountList != null && homeRecResDetailBeanList!![position]
                        .discountList!!.isNotEmpty()
        ) {
            holder.one_fragment_item_reduce_container.visibility = View.VISIBLE
            holder.divider.visibility = View.VISIBLE
            val sb = StringBuffer()
            for (discountBean in homeRecResDetailBeanList!![position]
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
            holder.one_fragment_item_reduce.text = sb.toString().substring(0, sb.length - 1)
        } else {
            holder.divider.visibility = View.GONE
            holder.one_fragment_item_reduce_container.visibility = View.GONE
        }

        //设置每个item的点击事件
        holder.binding.root.setOnClickListener {
            val intent = Intent(mContext, ResActivity::class.java)
            intent.putExtra(RES_DETAIL, homeRecResDetailBeanList!![position])
            mContext!!.startActivity(intent)
        }
    }

    class ViewHolder(val binding: OneFragmentContentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var one_content_item_iv: ImageView = binding.oneContentItemIv
        var one_content_item_buy_num: TextView = binding.oneContentItemBuyNum
        var one_fragment_content_item_name: TextView = binding.oneFragmentContentItemName
        var one_fragment_star: RatingBar = binding.oneFragmentStar
        var one_fragment_score: TextView = binding.oneFragmentScore
        var one_fragment_deliver: TextView = binding.oneFragmentDeliver
        var one_fragment_order_num: TextView = binding.oneFragmentOrderNum
        var one_fragment_extra: TextView = binding.oneFragmentExtra
        var one_fragment_address: TextView = binding.oneFragmentAddress
        var one_fragment_deliver_time: TextView = binding.oneFragmentDeliverTime
        var one_fragment_item_reduce_container: LinearLayout = binding.oneFragmentItemReduceContainer
        var one_fragment_item_reduce: TextView = binding.oneFragmentItemReduce
        var divider: View = binding.divider
    }

}