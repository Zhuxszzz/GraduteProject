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
data class ResDetailBean(
    //存到本地数据库中都要设置id
    val id: Int = 0,

    //用户在该商店里加入购物车商品的数量，不是从server端获取的，是本地数据库中查到后，代码设置的
    var buyNum: Int = 0,

    @SerializedName("shop_id")
    var resId: Int = 0,

    @SerializedName("shop_logo")
    var resImg: String? = null,

    @SerializedName("shop_pic")
    var shopPic: String? = null,

    @SerializedName("shop_name")
    var resName: String? = null,

    //星级
    @SerializedName("eval_decription")
    var resStar: Float = 0f,

    //月售多少订单,应该是在订单表中查询的订单数
    @SerializedName("order_num")
    var resOrderNum: Int = 0,

    //多少钱起送
    @SerializedName("ship_money")
    var resDeliverMoney: Int = 0,

    //配送费
    @SerializedName("deliver_money")
    var resExtraMoney: Int = 0,

    //包装费
    @SerializedName("pack_expense")
    var packExpense: Double = 0.0,

    //地址
    @SerializedName("shop_addr")
    var resAddress: String? = null,

    //配送时间
    @SerializedName("avg_delitime")
    var resDeliverTime: Int = 0,

    //商家描述
    @SerializedName("shop_intro")
    var resDescription: String? = null,

    //减价
    @SerializedName("discount_list")
    var discountList: List<DiscountBean>? = null
) : DataSupport(), Serializable