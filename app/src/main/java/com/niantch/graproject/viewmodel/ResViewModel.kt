package com.niantch.graproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.model.GoodsBuyCategoryNum
import com.niantch.graproject.model.GoodsNetItem
import com.niantch.graproject.model.ShopDetailModel
import com.niantch.graproject.utils.DataUtil
import com.niantch.graproject.utils.HttpUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * author: niantchzhu
 * date: 2021
 */
class ResViewModel : ViewModel() {

    companion object {
        const val TAG = "ResViewModel"
    }

    val homePageRes = MutableLiveData<ArrayList<ShopDetailModel>>()
    val shopGoodsLiveData = MutableLiveData<List<GoodsNetItem>>()
    val shopListLiveData = MutableLiveData<List<ShopDetailModel>?>()

    fun fetchShopData() {
        Log.d(TAG, "go fetch data")
        val shops = ArrayList<ShopDetailModel>()
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_RECOMMEND_SHOP, HashMap(), object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.message)
                homePageRes.postValue(shops)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body().string()
                val data = HttpUtil.requireData(responseText)
                Log.e(TAG, data)
                (Gson().fromJson<Any>(data, object : TypeToken<ArrayList<ShopDetailModel?>?>() {}.type) as ArrayList<ShopDetailModel>?)?.let { shops.addAll(it) }
                homePageRes.postValue(shops)
            }
        })

    }

    fun fetchShopsGoodsData(shopID: Int) {
        val data = ArrayList<GoodsNetItem>()
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
                data.addAll(Gson().fromJson<List<GoodsNetItem>>(responseText, object : TypeToken<List<GoodsNetItem?>?>() {}.type))
                shopGoodsLiveData.postValue(data)
            }
        })
    }

    fun searchShop(keyWord: String?) {
        var data = DataUtil.searchShop(keyWord)
        if (data != null) data = setButNum(data)
        shopListLiveData.postValue(data)
    }

    private fun setButNum(list: List<ShopDetailModel>): List<ShopDetailModel> {
        val resBuyCategoryNumList: List<GoodsBuyCategoryNum> = DataSupport.findAll(GoodsBuyCategoryNum::class.java)
        val resIdList: MutableList<String> = java.util.ArrayList()
        if (resBuyCategoryNumList.isNotEmpty()) {
            val resBuyNumTable = Hashtable<String, Int>()
            //将resBuyCategoryNumList中的添加到购物车的数量按resId设置给resBuyNumTable
            for (i in list.indices) {
                resBuyNumTable[java.lang.String.valueOf(list.get(i).shopId)] = 0
                resIdList.add(list[i].shopId.toString() + "")
            }
            for (resBuyCategoryNum in resBuyCategoryNumList) {
                if (resIdList.contains(resBuyCategoryNum.resId)) {
                    val num: Int = resBuyNumTable[resBuyCategoryNum.resId]!! + resBuyCategoryNum.buyNum
                    resBuyNumTable[resBuyCategoryNum.resId] = num
                }
            }
            //得到resBuyNumTable中的keyList
            val keyList: MutableList<String> = java.util.ArrayList()
            val itr: Iterator<String> = resBuyNumTable.keys.iterator()
            while (itr.hasNext()) {
                val str = itr.next()
                keyList.add(str)
            }
            //遍历设置homeRecResDetailList各项的buyNum
            for (i in list.indices) {
                for (j in keyList.indices) {
                    if (list[i].shopId == keyList[j].toInt()) {
                        list[i].buyNum = resBuyNumTable[keyList[j]]!!
                    }
                }
            }
        } else {
            for (i in list.indices) {
                list[i].buyNum = 0
            }
        }

        return list
    }

}