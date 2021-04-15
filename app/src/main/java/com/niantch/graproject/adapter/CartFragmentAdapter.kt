package com.niantch.graproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.databinding.CartFragmentItemBinding
import com.niantch.graproject.model.GoodsBuyItemNum
import com.niantch.graproject.ui.AccountActivity
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.ui.ResActivity.Companion.RES_ID
import com.niantch.graproject.utils.ImageUtil
import java.text.DecimalFormat

class CartFragmentAdapter(val context: Context, val goodsBuyItemNumList: ArrayList<GoodsBuyItemNum>): RecyclerView.Adapter<CartFragmentAdapter.CartItemViewHolder>() {
    private val resIdList =  ArrayList<String>()
    private var listener: ItemDeleteBtnListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = CartFragmentItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun getItemCount(): Int {

        //该方法计算ItemCount返回的不是resBuyItemNumList总数而是店铺数量,所以对应的position也不是resBuyItemNumList中的position
        for (resBuyItemNum in goodsBuyItemNumList) {
            if (!resIdList.contains(resBuyItemNum.resId)) {
                resIdList.add(resBuyItemNum.resId ?: "")
            }
        }
        return resIdList.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val df = DecimalFormat("#0.0")
        //该list为点击结算跳页时携带的数据,该resId的店铺的购买数据
        //该list为点击结算跳页时携带的数据,该resId的店铺的购买数据
        val list: MutableList<GoodsBuyItemNum> = java.util.ArrayList()
        var sum = 0.0
        var packageMoney = 0.0
        var deliverPrice = 0.0
        holder.binding.resItemContainer.removeAllViews()
        for (resBuyItemNum in goodsBuyItemNumList) {
            if (resBuyItemNum.resId.equals(resIdList[position])) {
                holder.binding.resItemContainer.addView(
                        initResBuyItem(resBuyItemNum.itemImg, resBuyItemNum.itemName, resBuyItemNum.buyNum,
                                resBuyItemNum.buyNum * df.format(resBuyItemNum.itemPrice).toDouble())
                )
                sum += resBuyItemNum.buyNum * resBuyItemNum.itemPrice
                packageMoney += resBuyItemNum.itemPackageMoney * resBuyItemNum.buyNum
                list.add(resBuyItemNum)
            }
        }
        if (packageMoney > 0) {
            holder.binding.resPackageMoney.visibility = View.VISIBLE
            holder.binding.dividerTwo.visibility = View.VISIBLE
            holder.binding.resPackageMoneyTv.text = "￥$packageMoney"
        } else {
            holder.binding.resPackageMoney.visibility = View.GONE
            holder.binding.dividerTwo.visibility = View.GONE
        }
        sum += packageMoney
        if (list.size > 0) {
            holder.binding.resNameCartFragmentItem.text = list[0].resName
            deliverPrice = list[0].resDeliverMoney - sum
        }
        val sum1 = sum.toInt()
        if (sum > sum1) {
            holder.binding.resAllPrice.text = "￥" + df.format(sum)
        } else {
            holder.binding.resAllPrice.text = "￥$sum1"
        }

        if (deliverPrice > 0) {
            val dp = deliverPrice.toInt()
            if (deliverPrice > dp) {
                holder.binding.resDeliverMoney.text = df.format(deliverPrice)
            } else {
                holder.binding.resDeliverMoney.text = dp.toString() + ""
            }
            holder.binding.resDeliverMoney.visibility = View.VISIBLE
            holder.binding.goToBuy.visibility = View.VISIBLE
            holder.binding.goToAccount.visibility = View.INVISIBLE
            holder.binding.haicha.visibility = View.VISIBLE
            holder.binding.qisong.visibility = View.VISIBLE
        } else {
            holder.binding.resDeliverMoney.visibility = View.GONE
            holder.binding.goToBuy.visibility = View.INVISIBLE
            holder.binding.goToAccount.visibility = View.VISIBLE
            holder.binding.haicha.visibility = View.GONE
            holder.binding.qisong.visibility = View.GONE
            holder.binding.goToAccount.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, AccountActivity::class.java)
                intent.putExtra("res_id", list[0].resId?.toInt())
                intent.putExtra("res_name", list[0].resName)
                context.startActivity(intent)
            })
        }

        holder.binding.root.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ResActivity::class.java)
            intent.putExtra(RES_ID, list[0].resId)
            intent.putExtra("res_name", list[0].resName)
            context.startActivity(intent)
        })

        holder.binding.deleteBtn.setOnClickListener(View.OnClickListener {
                listener?.onItemDeleteBtnListener(holder.binding.deleteBtn, position, list[0].resId)

        })

    }

    class CartItemViewHolder(val binding: CartFragmentItemBinding):RecyclerView.ViewHolder(binding.root)

    private fun initResBuyItem(imgUrl: String, itemName: String, itemNum: Int, itemPrice: Double): View? {
        val linearLayout: View = LayoutInflater.from(context).inflate(R.layout.cart_fragment_item_container, null)
        val item_img_cart_fragment_item = linearLayout.findViewById<View>(R.id.item_img_cart_fragment_item) as ImageView
        ImageUtil.load(context, imgUrl, item_img_cart_fragment_item, ImageUtil.REQUEST_OPTIONS)
        val item_name_cart_fragment_item = linearLayout.findViewById<View>(R.id.item_name_cart_fragment_item) as TextView
        item_name_cart_fragment_item.text = itemName
        val item_num_cart_fragment_item = linearLayout.findViewById<View>(R.id.item_num_cart_fragment_item) as TextView
        var num: String = context.resources.getString(R.string.res_item_num)
        num = String.format(num, itemNum)
        item_num_cart_fragment_item.text = num
        val res_item_price = linearLayout.findViewById<View>(R.id.res_item_price) as TextView
        res_item_price.text = "￥$itemPrice"
        return linearLayout
    }

    //子项的垃圾桶按钮的监听接口
    interface ItemDeleteBtnListener {
        fun onItemDeleteBtnListener(btn: ImageView?, position: Int, resId: String?)
    }

    fun setItemDeleteBtnListener(deleteBtnListener: ItemDeleteBtnListener) {
        listener = deleteBtnListener
    }

}