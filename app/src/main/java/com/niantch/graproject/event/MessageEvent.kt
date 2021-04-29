package com.niantch.graproject.event

import com.niantch.graproject.model.GoodsItemModel

/**
 * author: niantchzhu
 * date: 2021
 */
class MessageEvent(val num: Int,val price: Double,val goods: List<GoodsItemModel>) {
}