package com.niantch.graproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.niantch.graproject.R
import com.niantch.graproject.databinding.OneFragmentBinding
import com.niantch.graproject.model.ResDetailBean
import com.niantch.graproject.utils.ImageUtil
import com.niantch.graproject.viewmodel.ResViewModel

/**
 * author: niantchzhu
 * date: 2021
 */
class HomePageFragment : Fragment(R.layout.one_fragment) {

    companion object {
        const val TAG = "HomePageFragment"
        const val RES_DETAIL = "res_detail"
        const val RES_TITLE = "res_title"
        const val DELICIOUS = "美食"
        const val ONE_FLOUR = "公寓一楼"
        const val TWO_FLOUR = "公寓二楼"
        const val THREE_FLOUR = "公寓三楼"
        const val SWEET = "甜品饮品"
        const val DELIVER = "众包专送"
        const val SIMPLE = "炸鸡汉堡"
        const val FAVOUR = "新店特惠"
        const val FRUIT = "水果生鲜"
        const val COOK = "家常菜"
    }

    private lateinit var binding: OneFragmentBinding
    private val resViewModel: ResViewModel by activityViewModels()
    private var adapter: BaseQuickAdapter<ResDetailBean, BaseViewHolder>? = null
    private var recycleHeadView: View? = null

    private val homeRecResDetailList = mutableListOf<ResDetailBean>()
    var discountString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OneFragmentBinding.inflate(layoutInflater)
        initUI()
        initRecycler()
        initObserver()
    }

    fun initUI() {
        recycleHeadView = LayoutInflater.from(activity).inflate(R.layout.one_fragemnt_head_item, null)
        val headFood = recycleHeadView?.findViewById<View>(R.id.head_icon_food) as LinearLayout
        val headOne = recycleHeadView?.findViewById<View>(R.id.head_icon_one) as LinearLayout
        val headTwo = recycleHeadView?.findViewById<View>(R.id.head_icon_two) as LinearLayout
        val headThree = recycleHeadView?.findViewById<View>(R.id.head_icon_three) as LinearLayout
        val headSweet = recycleHeadView?.findViewById<View>(R.id.head_icon_sweet) as LinearLayout
        val headDeliver = recycleHeadView?.findViewById<View>(R.id.head_icon_deliver) as LinearLayout
        val headSimple = recycleHeadView?.findViewById<View>(R.id.head_icon_simple) as LinearLayout
        val headPrefer = recycleHeadView?.findViewById<View>(R.id.head_icon_prefer) as LinearLayout
        val headFruit = recycleHeadView?.findViewById<View>(R.id.head_icon_fruit) as LinearLayout
        val headCook = recycleHeadView?.findViewById<View>(R.id.head_icon_cook) as LinearLayout

        binding.homeSearch.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }
        headFood.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_food))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, DELICIOUS)
            startActivity(intent)
        }

        headOne.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_one))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, ONE_FLOUR)
            startActivity(intent)
        }

        headTwo.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_two))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, TWO_FLOUR)
            startActivity(intent)
        }

        headThree.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_three))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, THREE_FLOUR)
            startActivity(intent)
        }

        headSweet.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_sweet))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, SWEET)
            startActivity(intent)
        }

        headDeliver.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_deliver))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, DELIVER)
            startActivity(intent)
        }

        headSimple.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_ham))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, SIMPLE)
            startActivity(intent)
        }

        headPrefer.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_prefer))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, FAVOUR)
            startActivity(intent)
        }

        headFruit.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_fruit))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, FRUIT)
            startActivity(intent)
        }

        headCook.setOnClickListener {
            val intent = Intent(context, ClassifyResActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_cook))
            intent.putExtra(ClassifyResActivity.RES_CLASSIFY, COOK)
            startActivity(intent)
        }
        binding.oneFragmentSml.setOnRefreshListener { resViewModel.fetchMediaLiveData() }
    }

    fun initRecycler() {
        resViewModel.fetchMediaLiveData()
        val linearLayoutManager = LinearLayoutManager(context)
        binding.oneFragmentRv.setLayoutManager(linearLayoutManager)

        adapter = object : BaseQuickAdapter<ResDetailBean, BaseViewHolder>(R.layout.one_fragment_content_item, homeRecResDetailList) {
            override fun convert(holder: BaseViewHolder, item: ResDetailBean) {
                holder.setGone(R.id.one_fragment_item_reduce_container, false)

                //设置添加到购物车的数量，红点显示
                if (item.buyNum > 0) {
                    holder.setText(R.id.one_content_item_buy_num, item.buyNum.toString() + "")
                    holder.setVisible(R.id.one_content_item_buy_num, true)
                } else {
                    holder.setVisible(R.id.one_content_item_buy_num, false)
                }

                //设置img
                val iv: ImageView = holder.getView(R.id.one_content_item_iv)
                ImageUtil.load(context, item.resImg, iv, ImageUtil.REQUEST_OPTIONS)
                //店名
                holder.setText(R.id.one_fragment_content_item_name, item.resName)
                //评分
                val ratingBar: RatingBar = holder.getView(R.id.one_fragment_star)
                ratingBar.rating = item.resStar
                holder.setText(R.id.one_fragment_score, item.resStar.toString() + "")
                //月售订单
                var orderNum: String = context.getResources().getString(R.string.res_month_sell_order)
                orderNum = java.lang.String.format(orderNum!!, item.resOrderNum)
                holder.setText(R.id.one_fragment_order_num, orderNum)

                //起送
                var deliverMoney: String = context.resources.getString(R.string.res_deliver_money)
                deliverMoney = java.lang.String.format(deliverMoney!!, item.resDeliverMoney)
                holder.setText(R.id.one_fragment_deliver, deliverMoney)

                //配送费
                if (item.resExtraMoney > 0) {
                    var extraMoney: String = context.getResources().getString(R.string.res_extra_money)
                    extraMoney = java.lang.String.format(extraMoney!!, item.resExtraMoney)
                    holder.setText(R.id.one_fragment_extra, extraMoney)
                } else {
                    holder.setText(R.id.one_fragment_extra, "免配送费")
                }
                holder.setText(R.id.one_fragment_address, item.resAddress)
                //配送时间
                var deliverTime: String = context.resources.getString(R.string.res_deliver_time)
                deliverTime = java.lang.String.format(deliverTime, item.resDeliverTime)
                holder.setText(R.id.one_fragment_deliver_time, deliverTime)
                holder.setGone(R.id.divider, false)
                if (item.discountList != null && item.discountList?.isNotEmpty()!!) {
                    holder.setVisible(R.id.one_fragment_item_reduce_container, true)
                    holder.setVisible(R.id.divider, true)
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
                    discountString = sb.toString().substring(0, sb.length - 1)
                    holder.setText(R.id.one_fragment_item_reduce, discountString)
                }
            }
        }
        adapter?.setOnItemClickListener { _, _, position ->
            val intent = Intent(context, ResActivity::class.java)
            intent.putExtra(RES_DETAIL, homeRecResDetailList[position])
            //启动具体店铺页面
            startActivity(intent)
        }
        adapter?.addHeaderView(recycleHeadView!!)
        binding.oneFragmentRv.adapter = adapter
        if (homeRecResDetailList.size > 0) {
            binding.oneFragmentRv.setAdapter(adapter)
            binding.oneFragmentRv.visibility = View.VISIBLE
            binding.emptyView.root.setVisibility(View.GONE)
        } else {
            binding.oneFragmentRv.setVisibility(View.GONE)
            binding.emptyView.root.setVisibility(View.VISIBLE)
        }
    }

    fun initObserver() {
        resViewModel.homePageRes.observe(viewLifecycleOwner, Observer {
            homeRecResDetailList.clear()
            binding.oneFragmentSml.finishRefresh()
            if (homeRecResDetailList.isNotEmpty()) {
                binding.oneFragmentRv.setVisibility(View.VISIBLE)
                binding.emptyView.root.visibility = View.GONE
                adapter!!.notifyDataSetChanged()
            }
        })
    }

}