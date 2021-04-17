package com.niantch.graproject.event

import com.niantch.graproject.model.GoodsItemModel

/**
 * author: niantchzhu
 * date: 2021
 */
class MessageEvent(i: Int, d: Double, list: MutableList<GoodsItemModel>) {
    var num = 0
    var price = 0.0
    var goods: List<GoodsItemModel>? = null

    fun MessageEvent(totalNum: Int, price: Double, goods: List<GoodsItemModel>?) {
        num = totalNum
        this.price = price
        this.goods = goods
    }
}