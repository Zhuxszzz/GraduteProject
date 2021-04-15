package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
data class DiscountModel(
    @SerializedName("shop_id")
    var resId: Int = 0,
    @SerializedName("filled_value")
    var filledVal: Double = 0.0,
    @SerializedName("reduce_value")
    val reduceVal: Double = 0.0
)