package com.niantch.graproject

import android.app.Application
import com.niantch.graproject.utils.GlobalContextUtil
import org.litepal.LitePal

class MidnightDinerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContextUtil.globalContext = applicationContext
        LitePal.initialize(applicationContext)
    }
}