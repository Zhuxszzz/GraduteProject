package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName
import org.litepal.crud.DataSupport

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
data class UserModel(
    var id: Int = 0,
    @SerializedName("user_id")
    var userId: Int = 0,

    @SerializedName("nickname")
    var userName: String? = null,

    @SerializedName("user_image")
    var userImg: String? = null,

    @SerializedName("user_tel")
    var userPhone: String? = null,

    @SerializedName("password")
    var password: String? = null,

    @SerializedName("sex")
    var userSex: Int = 0
) : DataSupport()