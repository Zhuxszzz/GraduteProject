package com.niantch.graproject.utils

import com.niantch.graproject.model.*

/**
 * author: niantchzhu
 * date: 2021
 */
object DataUtil {

    @JvmStatic
    fun add(resId: String?, categoryId: String?, goodId: String?, buyNum: Int, itemName: String, itemPrice: Double, itemImg: String, resName: String,
            resDeliverMoney: Int, resExtraMoney: Int, goodPackageMoney: Double) {
        val resBuyItemNum = GoodsBuyItemNum()
        resBuyItemNum.resId = resId
        resBuyItemNum.categoryId =  categoryId
        resBuyItemNum.goodId =  goodId
        resBuyItemNum.buyNum = buyNum
        resBuyItemNum.itemName = itemName
        resBuyItemNum.itemPrice = itemPrice
        resBuyItemNum.itemImg = itemImg
        resBuyItemNum.resName = resName
        resBuyItemNum.resDeliverMoney = resDeliverMoney
        resBuyItemNum.resExtraMoney = resExtraMoney
        resBuyItemNum.itemPackageMoney = goodPackageMoney
        resBuyItemNum.save()
    }

    @JvmStatic
    open fun add(resId: String?, categoryId: String?, buyNum: Int): Unit {
        val resBuyCategoryNum = GoodsBuyCategoryNum()
        resBuyCategoryNum.buyNum =  buyNum
        resBuyCategoryNum.resId = resId
        resBuyCategoryNum.categoryId = categoryId
        resBuyCategoryNum.save()
    }

    fun getGoodListModel(list: List<GoodsNetItem>): GoodsListModel {
        val goodListModel = GoodsListModel(list[0].resId, list[0].resName,null)
        val map = HashMap<Int,GoodsCategoryModel>()
        for (item in list) {
            val goods = GoodsItemModel(item.resId,item.categoryId,item.goodId, item.gs_name,
                    item.price, item.introduce, item.goodsImgUrl, item.monthOrder
                    , item.goodComment, item.goodPackageMoney)
            if (map[item.categoryId] != null) {
                map[item.categoryId]?.goodsItemList?.add(goods)
            } else {
                val goodsList = ArrayList<GoodsItemModel>()
                goodsList.add(goods)
                map[item.categoryId] = GoodsCategoryModel(item.categoryId,item.name, item.categoryDescription,0, goodsList)
            }
        }
        goodListModel.goodsCategoryList = map.values.toMutableList()
        return goodListModel
    }
}