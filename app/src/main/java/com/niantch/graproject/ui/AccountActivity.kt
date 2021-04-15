package com.niantch.graproject.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.R
import com.niantch.graproject.databinding.ActivityAccountBinding
import com.niantch.graproject.model.*
import com.niantch.graproject.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

/**
 * author: niantchzhu
 * date: 2021
 */
class AccountActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private var resId = 0
    private var resNameText: String? = null

    //用户购买的详细数据
    private var list: MutableList<GoodsBuyItemNum>? = null
    private var addressList: MutableList<AddressModel>? = null
    private var allMoney = 0.0
    private var packageMoney = 0.0
    private var payTvList: MutableList<String>? = null
    private var payIvList: MutableList<Int>? = null
    private val payDialog: Dialog? = null
    private val couponModel: CouponModel? = null
    private var reduceMoney = 0.0
    private var discountModelList: MutableList<DiscountModel>? = null

    var df = DecimalFormat("#0.0")

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    fun initData() {
        resId = intent.getIntExtra("res_id", -1)
        resNameText = intent.getStringExtra("res_name")
        list = DataSupport.where("resId = ?", resId.toString()).find(GoodsBuyItemNum::class.java)
        addressList = DataSupport.where("selected = ?", "1").find(AddressModel::class.java)
        if (!addressList.isNullOrEmpty()) {
            binding.tvLocation.setText(addressList!![0].address)
            binding.tvName.setText(addressList!![0].name)
            binding.tvPhone.setText(addressList!![0].phone)
        }
        for (resBuyItemNum in list!!) {
            val view: View = initBuyItem(resBuyItemNum)
            binding.llBuyItemContainer.addView(view)
            allMoney += resBuyItemNum.buyNum * resBuyItemNum.itemPrice
            packageMoney += resBuyItemNum.itemPackageMoney * resBuyItemNum.buyNum
        }
        allMoney += packageMoney

        //网络请求店铺满减信息并设置

        //网络请求店铺满减信息并设置
        binding.loading.setVisibility(View.VISIBLE)
        binding.shopCart.goToAccount.setClickable(false)
        val hashMap = HashMap<String, String?>()
        hashMap["shop_id"] = resId.toString()
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_SHOP_ACCOUNT, hashMap, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                discountModelList = Gson().fromJson<List<DiscountModel>>(response.body().string(), object : TypeToken<List<DiscountModel?>?>() {}.type) as MutableList<DiscountModel>?
                for (discountBean in discountModelList!!) {
                    if (allMoney >= discountBean.filledVal) {
                        reduceMoney = discountBean.reduceVal
                    }
                }
                runOnUiThread {
                    if (reduceMoney > 0) {
                        val price = reduceMoney.toInt()
                        binding.rlReduce.setVisibility(View.VISIBLE)
                        if (reduceMoney > price) {
                            binding.tvReduce.setText("-￥$reduceMoney")
                        } else {
                            binding.tvReduce.setText("-￥$price")
                        }
                    }
                    allMoney += list!!.get(0).resExtraMoney
                    binding.tvAllMoney.setText(df.format(allMoney - reduceMoney))
                    binding.shopCart.tvAllPriceBottom.setText("￥" + df.format(allMoney - reduceMoney))
                    binding.shopCart.goToAccount.setText("确认支付")
                    binding.shopCart.goToAccount.setOnClickListener {
                        doOnGotoAccount()
                    }
                    binding.loading.setVisibility(View.GONE)
                }
            }
        })
        binding.shopCart.howMoneyToDelivery.setText("￥" + (list as MutableList<GoodsBuyItemNum>).get(0).resExtraMoney)
        //设置包装费
        //设置包装费
        val price = packageMoney.toInt()
        if (packageMoney > price) {
            binding.tvPackageMoney.setText("￥$packageMoney")
        } else {
           binding.tvPackageMoney.setText("￥$price")
        }

        //初始化支付方式

        //初始化支付方式
        payTvList = arrayListOf("支付宝","银行卡支付","微信支付" ,"QQ钱包")
        payIvList = arrayListOf(R.mipmap.ali_pay,R.mipmap.card_pay ,R.mipmap.v_pay, R.mipmap.q_pay)

    }

    private fun initBuyItem(goodsBuyItemNum: GoodsBuyItemNum): View {
        val view: View = LayoutInflater.from(this).inflate(R.layout.buy_list_item, null)
        val name = view.findViewById<View>(R.id.account_item_name) as TextView
        val price = view.findViewById<View>(R.id.account_item_price) as TextView
        val num = view.findViewById<View>(R.id.account_item_num) as TextView
        name.setText(goodsBuyItemNum.itemName)
        price.text = "￥" + goodsBuyItemNum.buyNum * goodsBuyItemNum.itemPrice
        num.text = "×" + goodsBuyItemNum.buyNum.toString() + ""
        return view
    }

    fun doOnGotoAccount() {
        if (binding.tvDeliverTime.getText().toString() == "选择配送时间" && binding.tvTakenTime.getText().toString() == "选择堂取时间") {
            Toast.makeText(this, "请选择配送方式!", Toast.LENGTH_SHORT).show()
        } else if (binding.tvLocation.getText() == "请选择收货地址") {
            Toast.makeText(this, "请选择收货地址!", Toast.LENGTH_SHORT).show()
        } else {
            val orderTimeId = System.currentTimeMillis()
            val userId = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", -1)
            binding.loading.setVisibility(View.VISIBLE)

            //用户使用红包后需要在数据库中将该用户的红包使用状态改变
            if (couponModel != null) {
                val hashMap = HashMap<String, String?>()
                hashMap["buyer_id"] = userId.toString()
                hashMap["red_packet_id"] = java.lang.String.valueOf(couponModel.redPaperId)
                HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.ALTER_USER_RED_PACKET, hashMap, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.d("AccountActivity", response.body().string())
                    }
                })
            }

            //存到订单表
            val hashMap = HashMap<String, String?>()
            //orderId为当前时间戳+用户Id
            hashMap["order_id"] = orderTimeId.toString() + userId
            hashMap["buyer_id"] = userId.toString()
            hashMap["shop_id"] = list!![0].resId
            hashMap["order_address"] = binding.tvLocation.getText().toString()
            hashMap["pay_way"] = binding.tvPaySelected.getText().toString()

            //2代表待接单
            hashMap["order_state"] = 2.toString()
            hashMap["order_remark"] = binding.extraInfo.getText().toString()
            if (binding.tvDeliverTime.getText().toString() != "选择配送时间") {
                hashMap["isdeliver"] = 1.toString() //外送则为1
                hashMap["order_amount"] = allMoney.toString() //外送则加上配送费
                if (binding.tvDeliverTime.getText().toString() != "立即配送") {
                    //预约时间
                    hashMap["servicetime"] = binding.tvDeliverTime.getText().toString()
                }
                if (couponModel == null) {
                    hashMap["pay_amount"] = (allMoney - reduceMoney).toString()
                } else {
                    hashMap["pay_amount"] = java.lang.String.valueOf(allMoney - reduceMoney - couponModel.price)
                }
            } else {
                hashMap["isdeliver"] = 0.toString()
                hashMap["order_amount"] = java.lang.String.valueOf(allMoney - list!![0].resExtraMoney) //堂取减去配送费
                //预约时间
                hashMap["servicetime"] = binding.tvTakenTime.getText().toString()
                if (couponModel == null) {
                    hashMap["pay_amount"] = java.lang.String.valueOf(allMoney - reduceMoney - list!![0].resExtraMoney)
                } else {
                    hashMap["pay_amount"] = java.lang.String.valueOf(allMoney - reduceMoney - couponModel.price - list!![0].resExtraMoney)
                }
            }

            //订单明细表json数据
            hashMap["data"] = Gson().toJson(list)
            HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.SAVE_ORDER, hashMap, object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val responseText = response.body().string()
                    runOnUiThread {
                        Toast.makeText(this@AccountActivity, "支付成功", Toast.LENGTH_SHORT).show()
                        binding.loading.setVisibility(View.GONE)
                        //删除本地数据库购物车信息
                        DataSupport.deleteAll(GoodsBuyItemNum::class.java, "resId = ?", list!![0].resId)
                        DataSupport.deleteAll(GoodsBuyCategoryNum::class.java, "resId = ?", list!![0].resId)
                        startActivity(Intent(this@AccountActivity, MainActivity::class.java))
                        finish()
                    }
                }
            })
        }
    }

}