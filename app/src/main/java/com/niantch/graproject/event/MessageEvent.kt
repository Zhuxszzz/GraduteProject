package com.niantch.graproject.event

import com.niantch.graproject.model.GoodItemBean

/**
 * author: niantchzhu
 * date: 2021
 */
class MessageEvent {
    var num = 0
    var price = 0.0
    var goods: List<GoodItemBean>? = null

    fun MessageEvent(totalNum: Int, price: Double, goods: List<GoodItemBean>?) {
        num = totalNum
        this.price = price
        this.goods = goods
    }
}