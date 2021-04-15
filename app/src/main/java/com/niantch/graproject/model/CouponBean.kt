package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author: niantchzhu
 * date: 2021
 */
data class CouponBean(@SerializedName("shop_name")
                      val shopName: String? = null,

                      @SerializedName("shop_id")
                      val shopId: Int = 0,

                      @SerializedName("red_packet_id")
                      val redPaperId: Int = 0,

        //消费门槛
                      @SerializedName("mini_consume")
                      val miniPrice: Double = 0.0,

        //金额
                      @SerializedName("amount")
                      val price: Double = 0.0,

        //是否通用，1通用，0不通用
                      @SerializedName("iscommon")
                      val iscommon: Int = 0,

        //截至时间
                      @SerializedName("deadline")
                      val deadline: String? = null): Serializable