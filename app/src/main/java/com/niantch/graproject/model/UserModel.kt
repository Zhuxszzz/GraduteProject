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
    //存到本地数据库中的id
    var id: Int = 0,
    //用户id
    @SerializedName("user_id")
    var userId: Int = 0,

//用户名（昵称）
    @SerializedName("nickname")
    var userName: String? = null,

//用户头像
    @SerializedName("user_image")
    var userImg: String? = null,

    @SerializedName("user_tel") //手机号(账号)
    var userPhone: String? = null,

//密码
    @SerializedName("password")
    var password: String? = null,

    @SerializedName("sex") //性别
    var userSex: Int = 0
) : DataSupport()