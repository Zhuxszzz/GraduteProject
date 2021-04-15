package com.niantch.graproject.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.R
import com.niantch.graproject.adapter.CouponAdapter
import com.niantch.graproject.databinding.ActivityCouponBinding
import com.niantch.graproject.model.CouponModel
import com.niantch.graproject.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * author: niantchzhu
 * date: 2021
 */
class CouponActivity: AppCompatActivity(R.layout.activity_coupon) {
    private lateinit var binding: ActivityCouponBinding
    private var adapter: CouponAdapter? = null
    private var list : ArrayList<CouponModel>? = null
    private var allMoney = 0.0

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityCouponBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    fun initData() {
        list = intent.getSerializableExtra("coupon_list") as ArrayList<CouponModel>
        if (!list.isNullOrEmpty()) {
            adapter = CouponAdapter( 0.0, list!!)
            binding.recycler.adapter = adapter
            binding.recycler.layoutManager = LinearLayoutManager(this@CouponActivity)
        } else {
            allMoney = intent.getDoubleExtra("all_money", 0.0)
            //得到用户还未使用的红包
            binding.progressBar.visibility = View.VISIBLE
            val hashMap = HashMap<String, String?>()
            hashMap["shop_id"] = intent.getStringExtra("res_id")
            hashMap["buyer_id"] = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", -1).toString()
            HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_USER_RED_PACKET_BY_SHOP, hashMap, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("CouponActivity", e.message)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val responseText = response.body().string()
                    list?.clear()
                    list?.addAll(Gson().fromJson<List<CouponModel>>(responseText, object : TypeToken<List<CouponModel?>?>() {}.type))
                    adapter = CouponAdapter(allMoney, list!!)
                    adapter?.setOnUseBtnClickListener(object : CouponAdapter.OnUseBtnClickListener {
                        override fun useBtnClickListener(position: Int, couponModel: CouponModel?) {
                            setResult(RESULT_OK, Intent().putExtra("coupon", couponModel))
                            finish()
                        }
                    })
                    runOnUiThread {
                        binding.recycler.adapter = adapter
                        binding.recycler.layoutManager = LinearLayoutManager(this@CouponActivity)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            })
        }
    }
}