package com.niantch.graproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.model.ResDetailBean
import com.niantch.graproject.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * author: niantchzhu
 * date: 2021
 */
class ResViewModel: ViewModel() {

    companion object {
        const val TAG = "ResViewModel"
    }

    val homePageRes = MutableLiveData<ArrayList<ResDetailBean>>()

    fun fetchMediaLiveData() {
        Log.d(TAG, "go fetch data")
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_RECOMMEND_SHOP,HashMap(),  object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.message)
                homePageRes.postValue(arrayListOf())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body().string()
                Log.e(TAG, responseText)
                homePageRes.postValue(Gson().fromJson<Any>(responseText, object : TypeToken<ArrayList<ResDetailBean?>?>() {}.type) as ArrayList<ResDetailBean>?)
            }
        })
    }
}