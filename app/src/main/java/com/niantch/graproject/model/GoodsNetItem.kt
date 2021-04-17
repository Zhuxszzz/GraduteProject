package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author: niantchzhu
 * date: 2021
 */
data class GoodsNetItem (
        @SerializedName("shop_id")
        var resId: Int = 0,
        @SerializedName("shop_name")
        var resName: String? = null,
        @SerializedName("category_id")
        var categoryId: Int = 0,
        @SerializedName("category_name")
        var name: String? = null,

        @SerializedName("category_des")
        var categoryDescription: String? = null,

        @SerializedName("gs_id")
        var goodId: Int = 0,//该商品的id ，唯一标识

        @SerializedName("gs_name")
        var gs_name: String? = null,

        @SerializedName("gs_newprice")
        var price: Double = 0.0,

        @SerializedName("gs_des")
        var introduce: String? = null,

        @SerializedName("gs_pic")
        var goodsImgUrl: String? = null,

        @SerializedName("order_num")
        var monthOrder: Int = 0,

        @SerializedName("gs_score")
        var goodComment: Int = 0,

        @SerializedName("gs_pack_money")
        var goodPackageMoney: Float = 0.0f
        ): Serializable