package com.niantch.graproject.viewmodel

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niantch.graproject.model.GoodsBuyCategoryNum
import com.niantch.graproject.model.GoodsBuyItemNum
import com.niantch.graproject.model.GoodsItemModel
import com.niantch.graproject.model.GoodsListModel
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.utils.DataUtil
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import org.litepal.crud.DataSupport
import java.util.ArrayList

/**
 * author: niantchzhu
 * date: 2021
 */
class OperateGoodsViewModel : ViewModel() {

    private val cartItemList = MutableLiveData<List<GoodsBuyItemNum>>()
    private val goodsItemNumLiveData = MutableLiveData<IntArray>()

    fun onGoodsAdded(position: Int) {
        goodsNum[position]++
        if (goodsNum[position] == 1) {
            val resBuyCategoryNumList = DataUtil
                    .where("resId = ? and categoryId = ?", resId.toString(), dataList[position].categoryId.toString()).find(GoodsBuyCategoryNum::class.java)
            if (resBuyCategoryNumList.size == 0) {
                DataUtil.add(resId.toString(), dataList[position].categoryId.toString(), goodsCategoryBuyNums[dataList[position].id])
            } else {
                val resBuyCategoryNum = GoodsBuyCategoryNum()
                resBuyCategoryNum.buyNum = goodsCategoryBuyNums[dataList[position].id]
                resBuyCategoryNum.updateAll("resId = ? and categoryId = ?", resId.toString(), dataList[position].categoryId.toString())
            }
            DataUtil.add(resId.toString(), dataList[position].categoryId.toString(), dataList[position].goodId.toString(),
                    goodsNum[position], dataList[position].name!!, dataList[position].price, dataList[position].goodsImgUrl!!,
                    resName, resDeliverMoney, resExtraMoney, dataList[position].goodPackageMoney.toDouble())
        } else if (goodsNum[position] > 1) {
            val resBuyItemNum = GoodsBuyItemNum()
            resBuyItemNum.buyNum = goodsNum[position]
            resBuyItemNum.updateAll("resId = ? and categoryId = ? and goodId = ?", resId.toString(), dataList[position].categoryId.toString(), dataList[position].goodId.toString())
            val resBuyCategoryNum = GoodsBuyCategoryNum()
            resBuyCategoryNum.buyNum = goodsCategoryBuyNums[dataList[position].id]
            resBuyCategoryNum.updateAll("resId = ? and categoryId = ?", resId.toString(), dataList[position].categoryId.toString())
        }
        goodsItemNumLiveData.postValue(goodsNum)
        cartItemList.postValue(DataUtil.getItemAdded(resId.toString()))
    }

    fun onGoodsDelete(position: Int) {
        goodsNum[position]--
        if (goodsCategoryBuyNums[dataList[position].id] == 0) {
            DataUtil.deleteAll(GoodsBuyCategoryNum::class.java, "resId = ? and categoryId = ?", resId.toString(), dataList[position].categoryId.toString())
        } else {
            val resBuyCategoryNum = GoodsBuyCategoryNum()
            resBuyCategoryNum.buyNum = goodsCategoryBuyNums[dataList[position].id]
            resBuyCategoryNum.updateAll("resId = ? and categoryId = ?", resId.toString(), dataList[position].categoryId.toString())
        }
        if (goodsNum[position] == 0) {
            DataUtil.deleteAll(GoodsBuyItemNum::class.java, "resId = ? and categoryId = ? and goodId = ?", resId.toString(), dataList[position].categoryId.toString(), dataList[position].goodId.toString())
        } else if (goodsNum[position] > 0) {
            val resBuyItemNum = GoodsBuyItemNum()
            resBuyItemNum.buyNum = goodsNum[position]
            resBuyItemNum.updateAll("resId = ? and categoryId = ? and goodId = ?", resId.toString(), dataList[position].categoryId.toString(), dataList[position].goodId.toString())
        }
        goodsItemNumLiveData.postValue(goodsNum)
        cartItemList.postValue(DataUtil.getItemAdded(resId.toString()))
    }


    private lateinit var goodsNum //各个商品的购买数量,goodsNum中的数据也要存入本地数据库中,下次进入该商店界面加载每个物品数量
            : IntArray
    private var buyNum //buyNum和totalPrice要存到本地数据库中，作为购物车中的信息
            = 0
    private var totalPrice = 0.0
    private lateinit var mSectionIndices: IntArray
    private val goodsCategoryBuyNums: IntArray = TODO()
    private var shopCart: TextView? = null
    private var buyImg: ImageView? = null
    private var mSectionLetters: Array<String?>
    private val selectGoods: MutableList<GoodsItemModel> = ArrayList()


    val resId = 1
    val resName = ""
    val resDeliverMoney = 0
    val resExtraMoney = 0
    val dataList: List<GoodsItemModel>

}