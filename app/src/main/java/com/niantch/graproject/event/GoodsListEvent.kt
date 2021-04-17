package com.niantch.graproject.event

/**
 * author: niantchzhu
 * date: 2021
 */
class GoodsListEvent(ints: IntArray) {
    lateinit var buyNums: IntArray

    fun GoodsListEvent(buyNums: IntArray) {
        this.buyNums = buyNums
    }
}