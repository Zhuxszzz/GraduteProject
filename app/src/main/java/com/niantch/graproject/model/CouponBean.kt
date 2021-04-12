package com.niantch.graproject.model

import com.google.gson.annotations.SerializedName

/**
 * author: niantchzhu
 * date: 2021
 */
data class CouponBean(@SerializedName("shop_name")
                      private val shopName: String? = null,

                      @SerializedName("shop_id")
                      private val shopId: Int = 0,

                      @SerializedName("red_packet_id")
                      private val redPaperId: Int = 0,

        //消费门槛
                      @SerializedName("mini_consume")
                      private val miniPrice: Double = 0.0,

        //金额
                      @SerializedName("amount")
                      private val price: Double = 0.0,

        //是否通用，1通用，0不通用
                      @SerializedName("iscommon")
                      private val iscommon: Int = 0,

        //截至时间
                      @SerializedName("deadline")
                      private val deadline: String? = null)