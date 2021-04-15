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
data class ResBuyItemNum(
    var resId: String? = null,

    var resName: String = "",
    //多少钱起送
    var resDeliverMoney: Int = 0,
    //配送费
    var resExtraMoney: Int = 0,
    //包装费
    @SerializedName("gs_pack_money")
    var itemPackageMoney: Double = 0.0,
    var categoryId: String? = null,

    @SerializedName("gs_id")
    var goodId: String? = null,

    @SerializedName("quantity")
    var buyNum: Int = 0,

    @SerializedName("gs_name")
    var itemName: String = "",

    @SerializedName("gs_newprice")
    var itemPrice: Double = 0.0,

    @SerializedName("gs_pic")
    var itemImg: String = ""
) : DataSupport(), Serializable