package com.niantch.graproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.adapter.GoodsCategoryRecyclerAdapter
import com.niantch.graproject.adapter.GoodsItemRecyclerAdapter
import com.niantch.graproject.event.GoodsListEvent
import com.niantch.graproject.model.*
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.crud.DataSupport
import kotlin.collections.ArrayList

/**
 * author: niantchzhu
 * date: 2021
 */
class GoodsFragment : Fragment() {

    private var goodsCategoryRecycler: RecyclerView? = null
    private var mGoodsCategoryRecyclerAdapter: GoodsCategoryRecyclerAdapter? = null

    //商品类别列表
    private val goodsCategoryList: ArrayList<GoodsCategoryModel> = ArrayList()

    //商品列表
    private val goodsItemList: ArrayList<GoodsItemModel> = ArrayList()

    //存储各个分类下第一个商品的位置
    private val categoryFirstItemPosi: ArrayList<Int> = ArrayList()

    private var goodsItemRecycler: RecyclerView? = null
    private var goodsItemRecyclerAdapter: GoodsItemRecyclerAdapter? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null

    private var dataList: GoodsListModel? = null

    private var root: View? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.goods_fragment, container, false)
        initView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initView() {
        goodsCategoryRecycler = root!!.findViewById<View>(R.id.goods_category_list) as RecyclerView
        goodsItemRecycler = root!!.findViewById<View>(R.id.goods_item_list) as RecyclerView
    }

    private fun initData() {
        dataList = (activity as ResActivity?)?.getGoodListModel() //这是得到ResActivity中网络请求到的数据
        //从本地数据库中得到category的购买总数和每个category下item的购买数量,然后设置给请求得到的数据dataList
        val resBuyCategoryNumList: List<GoodsBuyCategoryNum> = DataSupport.where("resId = ?", java.lang.String.valueOf(dataList?.resId)).find(GoodsBuyCategoryNum::class.java)
        for (i in resBuyCategoryNumList.indices) {
            val categoryId: String? = resBuyCategoryNumList[i].categoryId
            val butNum: Int = resBuyCategoryNumList[i].buyNum
            for (j in dataList?.goodsCategoryList!!.indices) {
                if (categoryId?.toInt() == dataList?.goodsCategoryList?.get(j)?.categoryId) {
                    dataList?.goodsCategoryList?.get(j)?.buyNum = (butNum)
                    val resBuyItemNumList: List<GoodsBuyItemNum> = DataSupport.where("resId = ? and categoryId =? ", java.lang.String.valueOf(dataList?.resId), categoryId).find(GoodsBuyItemNum::class.java)
                    for (k in resBuyItemNumList.indices) {
                        val goodId: String? = resBuyItemNumList[k].goodId
                        val buyNum: Int = resBuyItemNumList[k].buyNum
                        for (m in dataList?.goodsCategoryList?.get(j)?.goodsItemList!!.indices) {
                            if (goodId?.toInt() == dataList?.goodsCategoryList?.get(j)?.goodsItemList?.get(m)!!.goodId) dataList?.goodsCategoryList?.get(j)?.goodsItemList?.get(m)!!.buyNum = (buyNum)
                        }
                    }
                }
            }
        }

        var j = 0
        var isFirst: Boolean
        if (dataList?.goodsCategoryList != null)
        for ((i, dataItem) in dataList?.goodsCategoryList!!.withIndex()) {
            goodsCategoryList.add(dataItem)
            isFirst = true
            if(goodsItemList!= null)
            for (goodsItemBean in dataItem.goodsItemList!!) {
                if (isFirst) {
                    categoryFirstItemPosi.add(j)
                    isFirst = false
                }
                j++
                goodsItemBean.id = (i)
                goodsItemBean.categoryId = (dataItem.categoryId) //同一个范畴下的商品的categoryId是一样的
                goodsItemList.add(goodsItemBean)
            }
        }

        mGoodsCategoryRecyclerAdapter = GoodsCategoryRecyclerAdapter(goodsCategoryList, activity)
        val linearLayoutManager = LinearLayoutManager(context)
        goodsCategoryRecycler?.layoutManager = linearLayoutManager
        goodsCategoryRecycler?.adapter = mGoodsCategoryRecyclerAdapter
        mGoodsCategoryRecyclerAdapter?.setOnItemClickListener { _, position -> //如果直接使用recyclerView的scrollToPosition有些情况下不会置顶该位置，所以用这种方法
            mLinearLayoutManager?.scrollToPositionWithOffset(categoryFirstItemPosi[position], 0)
            mGoodsCategoryRecyclerAdapter?.setCheckPosition(position)
        }

        mLinearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        goodsItemRecycler?.layoutManager = mLinearLayoutManager
        goodsItemRecyclerAdapter = GoodsItemRecyclerAdapter(activity, goodsItemList, goodsCategoryList)
        goodsItemRecycler?.adapter = goodsItemRecyclerAdapter
        goodsItemRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                for (i in categoryFirstItemPosi.indices) {
                    if (mLinearLayoutManager?.findFirstVisibleItemPosition()!! >= categoryFirstItemPosi[i]) {
                        mGoodsCategoryRecyclerAdapter?.setCheckPosition(i)
                        goodsCategoryRecycler?.smoothScrollToPosition(i)
                    }
                }
            }
        })
    }

    /**
     * 添加 或者  删除  商品发送的消息处理
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: GoodsListEvent) {
        if (event.buyNums.isNotEmpty()) {
            for (i in event.buyNums.indices) {
                goodsCategoryList[i].buyNum = (event.buyNums[i])
            }
            mGoodsCategoryRecyclerAdapter?.changeData(goodsCategoryList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}