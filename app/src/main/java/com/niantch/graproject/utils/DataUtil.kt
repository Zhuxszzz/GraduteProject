package com.niantch.graproject.utils

import com.niantch.graproject.model.*
import org.litepal.crud.ClusterQuery
import org.litepal.crud.DataSupport

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
        resBuyItemNum.categoryId = categoryId
        resBuyItemNum.goodId = goodId
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
        resBuyCategoryNum.buyNum = buyNum
        resBuyCategoryNum.resId = resId
        resBuyCategoryNum.categoryId = categoryId
        resBuyCategoryNum.save()
    }

    fun getGoodListModel(list: List<GoodsNetItem>): GoodsListModel {
        val goodListModel = GoodsListModel(list[0].resId, list[0].resName, null)
        val map = HashMap<Int, GoodsCategoryModel>()
        for (item in list) {
            val goods = GoodsItemModel(item.resId, item.categoryId, item.goodId, item.gs_name,
                    item.price, item.introduce, item.goodsImgUrl, item.monthOrder, item.goodComment, item.goodPackageMoney)
            if (map[item.categoryId] != null) {
                map[item.categoryId]?.goodsItemList?.add(goods)
            } else {
                val goodsList = ArrayList<GoodsItemModel>()
                goodsList.add(goods)
                map[item.categoryId] = GoodsCategoryModel(item.categoryId, item.name, item.categoryDescription, 0, goodsList)
            }
        }
        goodListModel.goodsCategoryList = map.values.toMutableList()
        return goodListModel
    }

    fun getDefaultAddress(): String {
        //设置默认收货地址
        val addressList: List<AddressModel> = DataSupport.where("selected = ?","1").find(AddressModel::class.java)
        return if (addressList.isNotEmpty()) {
            addressList[0].address ?: "请添加地址"
        } else {
            "请添加地址"
        }
    }

    fun getCurrentUser(): UserModel? {
        val list: List<UserModel> = DataSupport.findAll(UserModel::class.java)
        return if (list.isNotEmpty()) {
            list[0]
        } else {
            null
        }
    }

    fun getUserWithPhone(tel: String, password: String): UserModel? {
        val list: List<UserModel> = DataSupport.where("user_tel = $tel and password = $password").find(UserModel::class.java)
        return list[0]
    }

    fun getShopWithID(shopID: Int): ShopDetailModel? {
        val list: List<ShopDetailModel> = DataSupport.where("shopId = ?", shopID.toString()).find(ShopDetailModel::class.java)
        return if (list.isNotEmpty()) {
            list[0]
        } else {
            null
        }
    }

    fun searchShop(keyWord: String?): List<ShopDetailModel>? {
        return if (keyWord != null) {
            DataSupport.where("resName like ?", "%$keyWord%").find(ShopDetailModel::class.java)
        } else {
            DataSupport.findAll(ShopDetailModel::class.java)
        }
    }

    fun updateShops(shops: List<ShopDetailModel>) {
        DataSupport.deleteAll(ShopDetailModel::class.java)
        for (shop in shops) {
            shop.save()
        }
    }

    fun getItemAdded(resId: String?): MutableList<GoodsBuyItemNum> {
        return DataSupport.where("resId = ?", resId.toString()).find(GoodsBuyItemNum::class.java)
    }

    @Synchronized
    fun deleteAll(modelClass: Class<*>?, vararg conditions: String?): Int {
//        return DataSupport.deleteAll(modelClass,vararg conditions)
        return 1
    }


    @Synchronized
    fun where(vararg conditions: String?): ClusterQuery {
        return DataSupport.where("conditions")
//        cQuery.mConditions = conditions
    }
}