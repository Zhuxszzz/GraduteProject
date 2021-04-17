package com.niantch.graproject.viewmodel

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.model.GoodsNetItem
import com.niantch.graproject.model.ShopDetailModel
import com.niantch.graproject.ui.ResActivity
import com.niantch.graproject.utils.DataUtil
import com.niantch.graproject.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.StringReader
import java.lang.Exception
import java.lang.reflect.Type

/**
 * author: niantchzhu
 * date: 2021
 */
class ResViewModel: ViewModel() {

    companion object {
        const val TAG = "ResViewModel"
    }

    val homePageRes = MutableLiveData<ArrayList<ShopDetailModel>>()
    val shopGoodsLiveData = MutableLiveData<List<GoodsNetItem>>()

    fun fetchShopData() {
        Log.d(TAG, "go fetch data")
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_RECOMMEND_SHOP, HashMap(),  object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.message)
                homePageRes.postValue(arrayListOf())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body().string()
                val data = HttpUtil.requireData(responseText)
                Log.e(TAG, data)
                homePageRes.postValue(Gson().fromJson<Any>(data, object : TypeToken<ArrayList<ShopDetailModel?>?>() {}.type) as ArrayList<ShopDetailModel>?)
            }
        })
    }

    fun fetchShopsGoodsData(shopID : Int) {
        val hashMap = java.util.HashMap<String, String?>()
        hashMap["shop_id"] = shopID.toString()
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_SHOP_GOODS, hashMap, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("", e.toString())
                shopGoodsLiveData.postValue(emptyList())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var responseText = response.body().string()
                responseText = HttpUtil.requireData(responseText)
                val data = Gson().fromJson<List<GoodsNetItem>>(responseText, object : TypeToken<List<GoodsNetItem?>?>() {}.type)
                shopGoodsLiveData.postValue(data)
            }
        })
    }

}