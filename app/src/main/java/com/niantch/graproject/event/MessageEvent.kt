package com.niantch.graproject.event

import com.niantch.graproject.model.GoodItemModel

/**
 * author: niantchzhu
 * date: 2021
 */
class MessageEvent {
    var num = 0
    var price = 0.0
    var goods: List<GoodItemModel>? = null

    fun MessageEvent(totalNum: Int, price: Double, goods: List<GoodItemModel>?) {
        num = totalNum
        this.price = price
        this.goods = goods
    }
}