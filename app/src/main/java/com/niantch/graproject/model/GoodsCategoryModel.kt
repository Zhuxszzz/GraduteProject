package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
data class GoodsCategoryModel(
    @SerializedName("category_id")
    var categoryId: Int = 0,
    @SerializedName("category_name")
    var name: String? = null,

    @SerializedName("category_des")
    var categoryDescription: String? = null,
    var buyNum: Int = 0,//代码中设置的，不是server中取到的

    @SerializedName("goods_item")
    var goodsItemList: ArrayList<GoodsItemModel>? = null
)