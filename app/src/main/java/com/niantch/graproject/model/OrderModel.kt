package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName
import org.litepal.crud.DataSupport
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

    @SerializedName("pay_way")
    var payWay:String? = null,

    @SerializedName("order_state")
    var orderState: Int = 0,

    @SerializedName("order_description")
    var orderDescription: String? = null,

    @SerializedName("order_address")
    var orderAddress: String? = null,

    @SerializedName("order_amount")
    var orderAmount: Float = 0.0f,

    @SerializedName("order_detail")
    var orderDetail: List<GoodsBuyItemNum>? = null
): Serializable,DataSupport()