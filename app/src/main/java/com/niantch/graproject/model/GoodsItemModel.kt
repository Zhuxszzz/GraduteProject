package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
data class GoodsItemModel(
    var id: Int = 0,//此id就是categoryId，是代码中设置的GoodsCategoryBean中的此id就是categoryId,不是再次通过server端取到的

    @SerializedName("category_id")
    var categoryId: Int = 0,

    @SerializedName("gs_id")
    var goodId: Int = 0,//该商品的id ，唯一标识

    @SerializedName("gs_name")
    var name: String? = null,

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
    var goodPackageMoney: Float = 0.0f,
    var buyNum: Int = 0//代码中设置的，不是server中取到的
)