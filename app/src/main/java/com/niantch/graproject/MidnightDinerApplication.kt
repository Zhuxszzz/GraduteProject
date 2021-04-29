package com.niantch.graproject

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.niantch.graproject.utils.GlobalContextUtil
import dagger.hilt.android.HiltAndroidApp
import org.litepal.LitePal

class MidnightDinerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContextUtil.globalContext = applicationContext
        LitePal.initialize(applicationContext)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}