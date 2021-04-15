package com.niantch.graproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.CouponItemBinding
import com.niantch.graproject.model.CouponModel
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.utils.GlobalContextUtil

/**
 * author: niantchzhu
 * date: 2021
 */
class CouponAdapter(private var allMoney: Double = 0.0, private var list: MutableList<CouponModel> = mutableListOf()) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    private var listener: OnUseBtnClickListener? = null

    private val context = GlobalContextUtil.globalContext!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = CouponItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val price = list[position].price
        holder.binding.couponMoney.text = price.toString()
        holder.binding.couponName.text = list[position].shopName.toString() + "红包"
        holder.binding.couponDate.text = list[position].deadline
        holder.binding.minUse.text = "满" + list[position].miniPrice.toString() + "元可用"
        //从我的界面进入红包，查看拥有的全部店铺红包
        //从我的界面进入红包，查看拥有的全部店铺红包
        if (allMoney == 0.0) {
            holder.binding.use.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ResActivity::class.java)
                intent.putExtra("res_id", list[position].shopId.toString() + "")
                intent.putExtra("res_name", list[position].shopName)
                context.startActivity(intent)
            })
        } else {
            holder.binding.use.setVisibility(View.VISIBLE)
            if (allMoney >= list[position].miniPrice) {
                if (listener != null) {
                    holder.binding.use.setOnClickListener(View.OnClickListener { listener?.useBtnClickListener(position, list[position]) })
                }
                holder.binding.use.background = context.getResources().getDrawable(R.drawable.red_ban_yuan)
            } else {
                holder.binding.use.background = context.getResources().getDrawable(R.drawable.grey_ban_yuany)
            }
        }
    }

    class CouponViewHolder(val binding: CouponItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface OnUseBtnClickListener {
        fun useBtnClickListener(position: Int, couponModel: CouponModel?)
    }

    fun setOnUseBtnClickListener(onUseBtnClickListener: OnUseBtnClickListener) {
        listener = onUseBtnClickListener
    }
}