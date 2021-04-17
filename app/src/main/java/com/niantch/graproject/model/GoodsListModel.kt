package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
data class GoodsListModel(
    //商店id
    @SerializedName("shop_id")
    var resId: Int = 0,
    @SerializedName("shop_name")
    var resName: String? = null,

    @SerializedName("goods_category")
    var goodsCategoryList: List<GoodsCategoryModel>? = null
)