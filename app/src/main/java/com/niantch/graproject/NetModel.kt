package com.niantch.graproject

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author: niantchzhu
 * date: 2021
 */
data class NetModel(
        @SerializedName("Code")
        val code: Int= 0,
        @SerializedName("Data")
        val data: String = ""
): Serializable