package com.niantch.graproject.utils

import android.text.TextUtils
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.JsonParser
import com.niantch.graproject.NetModel
import okhttp3.Request
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * author: niantchzhu
 * date: 2021
 */
object HttpUtil {
    const val TAG = "HttpUtil"
    const val SERVER_HOST = "server_host"
    var HOME_PATH = "http://120.79.31.221:8888"

    init {
        val serverHost = PreferenceManager.getDefaultSharedPreferences(GlobalContextUtil.globalContext!!).getString(SERVER_HOST, "")
        if (!TextUtils.isEmpty(serverHost)) {
            HOME_PATH = serverHost + "/restaurant/index.php"
        }
    }

    //首页API
    const val OBTAIN_RECOMMEND_SHOP = "/HomePage/obtainShopByRecommend"
    const val OBTAIN_SHOP_BY_LABEL = "/HomePage/obtainShopByLabel"
    const val OBTAIN_SHOP_ACCOUNT = "/HomePage/obtainShopDiscount"
    const val OBTAIN_SHOP_GOODS = "/HomePage/obtainShopGoods"
    const val OBTAIN_SHOP_BY_ID = "/HomePage/obtainShopById"

    //用户信息API
    const val UPLOAD_IMG_API = "/UserInfo/upLoadImgs"
    const val SAVE_USER_NAME = "/UserInfo/alterUserName"
    const val SAVE_USER_SEX = "/UserInfo/alterUserSex"
    const val SAVE_USER_PHONE = "/UserInfo/alterUserPhone"
    const val LOGIN_BY_PWD = "/UserInfo/checkByTelAndPwd"
    const val LOGIN_BY_CODE = "/UserInfo/checkByTelAndCode"
    const val REGISTER = "/UserInfo/register"
    const val OBTAIN_BUILDING = "/UserInfo/obtainBuilding"
    const val ADD_USER_ADDRESS = "/UserInfo/addUserAddress"
    const val DELETE_USER_ADDRESS = "/UserInfo/deleteUserAddress"
    const val ALTER_USER_PWD = "/UserInfo/alterUserPwd"
    const val OBTAIN_USER_RED_PACKET_BY_SHOP = "/UserInfo/obtainUserRedPacketByShopId"
    const val OBTAIN_USER_RED_PACKET = "/UserInfo/obtainUserRedPacket"
    const val ALTER_USER_RED_PACKET = "/UserInfo/alterUserRedPacket"

    //订单信息API
    const val SAVE_ORDER = "/Order/saveToOrder"
    const val GET_ORDER = "/Order/getOrder"
    const val ORDER_CANCEL = "/Order/cancel"

    private val MEDIA_TYPE_IMAGE: MediaType = MediaType.parse("image/*")


    private object SingHolder {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
    }

    //第三个方法可以代替第二个方法
    fun sendOkHttpPostRequest(address: String?, hashMap: HashMap<String, String?>, callback: Callback?) {
        val builder: FormBody.Builder = FormBody.Builder()
        val set: Set<*> = hashMap.keys
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next() as String
            builder.add(key, hashMap[key])
        }
        //传递json字符串,后台用$_POST['json']获取,然后用json_decode()解析成数组或对象;
//        builder.add("json",new Gson().toJson());
        val request: Request = Request.Builder()
                .url(address)
                .post(builder.build())
                .build()
        SingHolder.okHttpClient.newCall(request).enqueue(callback)
    }

    fun upLoadImgsRequest(address: String?, hashMap: HashMap<String, String>, imgUrls: List<String?>?, callback: Callback?) {
        val builder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (imgUrls != null) {
            for (i in imgUrls.indices) {
                val f = File(imgUrls[i])
                if (f != null) {
                    builder.addFormDataPart("img$i", f.name, RequestBody.create(MEDIA_TYPE_IMAGE, f))
                }
            }
        }
        //builder.addFormDataPart("json",json字符串); 也可以这样传递json字符串,一般json字符串是用new Gson().toJson(obj)把对象转换成的，也可以解析list，这样就解析成jsonArray字符串了
        val set: Set<*> = hashMap.keys
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next() as String
            builder.addFormDataPart(key, hashMap[key])
        }

        //构建请求
        val request: Request = Request.Builder()
                .url(address) //地址
                .post(builder.build()) //添加请求体
                .build()
        SingHolder.okHttpClient.newCall(request).enqueue(callback)
    }


    fun requireData (json : String): String? {
        val jsonObj = JsonParser().parse(json)
        Log.e(TAG, jsonObj.asString)
        return jsonObj.asString
    }


}