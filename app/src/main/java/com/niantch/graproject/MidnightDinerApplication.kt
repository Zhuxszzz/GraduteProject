package com.niantch.graproject

import android.app.Application
import com.niantch.graproject.utils.GlobalContextUtil

class MidnightDinerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContextUtil.globalContext = applicationContext
    }
}