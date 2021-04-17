package com.niantch.graproject.model

import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 * author: niantchzhu
 * date: 2021
 */
data class GoodsBuyCategoryNum(var id: Int = 0,

                               var resId //商店id
                               : String? = null,

                               var categoryId //范畴id
                               : String? = null,

                               var buyNum: Int = 0) : DataSupport(), Serializable