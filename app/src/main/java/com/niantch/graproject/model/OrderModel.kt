package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
data class OrderModel(
    var id: Int = 0,
    @SerializedName("order_id")
    var orderId: String? = null,

    @SerializedName("buyer_id")
    var userId: Int = 0,

    @SerializedName("shop_id")
    var resId: Int = 0,

    @SerializedName("shop_logo")
    var resImg: String? = null,

    @SerializedName("shop_name")
    var resName: String? = null,

    @SerializedName("order_time")
    var orderTime: String? = null,

    @SerializedName("pay_amount")
    var orderPrice: Double = 0.0,

    @SerializedName("order_state")
    var orderState: Int = 0,

    @SerializedName("order_description")
    var orderDescription: String? = null,

    @SerializedName("order_detail")
    var orderDetail: List<ResBuyItemModel>? = null
): Serializable