package com.niantch.graproject.utils

import java.util.*

/**
 * author: niantchzhu
 * date: 2021
 */
object CommonUtils {
    fun generateUUID(): String? {
        val uuid = UUID.randomUUID()
        val Uuid: String
        Uuid = uuid.toString().replace('-', '_')
        return Uuid
    }
}