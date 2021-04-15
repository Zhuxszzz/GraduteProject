package com.niantch.graproject.model

import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 * author: niantchzhu
 * date: 2021
 */
data class GoodsBuyCategoryNum(var id: Int = 0,

                               val resId //商店id
                         : String? = null,

                               val categoryId //范畴id
                         : String? = null,

                               val buyNum: Int = 0) : DataSupport(), Serializable