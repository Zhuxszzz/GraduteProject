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
data class AddressModel(
    var id: Int = 0,
    @SerializedName("buyer_id")
    var user_id: Int = 0,

    @SerializedName("receiver_address")
    var address: String? = null,

    @SerializedName("receiver_name")
    var name: String? = null,

    @SerializedName("receiver_tel")
    var phone: String? = null,
    var selected: Int = 0
): DataSupport(), Serializable