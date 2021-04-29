package com.niantch.graproject.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.R
import com.niantch.graproject.adapter.AddressActivity
import com.niantch.graproject.adapter.PopupPayWayAdapter
import com.niantch.graproject.adapter.PupupTimeAdapter
import com.niantch.graproject.databinding.ActivityAccountBinding
import com.niantch.graproject.model.*
import com.niantch.graproject.utils.DataUtil
import com.niantch.graproject.utils.HttpUtil
import kotlinx.android.synthetic.main.activity_classify_res.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * author: niantchzhu
 * date: 2021
 */
class AccountActivity: AppCompatActivity(R.layout.activity_account) {

    companion object {
        const val REQUEST_ADDRESS = 8080
        const val REQUEST_COUPON = 8018
    }

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
    private var payDialog: Dialog? = null
    private var couponModel: CouponModel? = null
    private var reduceMoney = 0.0
    private var discountModelList: MutableList<DiscountModel>? = null

    var df = DecimalFormat("#0.0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        initData()
        initView()
    }

    fun initData() {
        binding.bar.ivAdd.visibility = View.GONE
        binding.bar.ivBack.setOnClickListener { finish() }
        resId = intent.getIntExtra("res_id", -1)
        resNameText = intent.getStringExtra("res_name")
        binding.bar.tvContainerText.text = resNameText
        list = DataSupport.where("resId = ?", resId.toString()).find(GoodsBuyItemNum::class.java)
        addressList = DataSupport.where("selected = ?", "1").find(AddressModel::class.java)
        if (!addressList.isNullOrEmpty()) {
            binding.tvLocation.text = addressList!![0].address
            binding.tvName.text = addressList!![0].name
            binding.tvPhone.text = addressList!![0].phone
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
        binding.loading.visibility = View.VISIBLE
        binding.shopCart.goToAccount.isClickable = false
        val hashMap = HashMap<String, String?>()
        hashMap["shop_id"] = resId.toString()
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_SHOP_ACCOUNT, hashMap, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var responseText = response.body().string()
                responseText = HttpUtil.requireData(responseText)
                discountModelList = Gson().fromJson<List<DiscountModel>>(responseText, object : TypeToken<List<DiscountModel?>?>() {}.type) as MutableList<DiscountModel>?
                if (discountModelList != null)
                    for (discountBean in discountModelList!!) {
                        if (allMoney >= discountBean.filledVal) {
                            reduceMoney = discountBean.reduceVal
                        }
                    }
                runOnUiThread {
                    if (reduceMoney > 0) {
                        val price = reduceMoney.toInt()
                        binding.rlReduce.visibility = View.VISIBLE
                        if (reduceMoney > price) {
                            binding.tvReduce.text = "-￥$reduceMoney"
                        } else {
                            binding.tvReduce.text = "-￥$price"
                        }
                    }
                    allMoney += list!!.get(0).resExtraMoney
                    binding.tvAllMoney.text = df.format(allMoney - reduceMoney)
                    binding.shopCart.tvAllPriceBottom.text = "￥" + df.format(allMoney - reduceMoney)
                    binding.shopCart.goToAccount.text = "确认支付"
                    binding.shopCart.goToAccount.setOnClickListener {
                        doOnGotoAccount()
                    }
                    binding.loading.visibility = View.GONE
                }
            }
        })
        binding.tvDeliverMoney.text = "￥" + (list as MutableList<GoodsBuyItemNum>).get(0).resExtraMoney
        //设置包装费
        //设置包装费
        val price = packageMoney.toInt()
        if (packageMoney > price) {
            binding.tvPackageMoney.text = "￥$packageMoney"
        } else {
            binding.tvPackageMoney.text = "￥$price"
        }

        //初始化支付方式

        //初始化支付方式
        payTvList = arrayListOf("支付宝", "银行卡支付", "微信支付", "QQ钱包")
        payIvList = arrayListOf(R.mipmap.ali_pay, R.mipmap.card_pay, R.mipmap.v_pay, R.mipmap.q_pay)

    }

    fun initView() {
        binding.shopCart.goToAccount.setVisibility(View.VISIBLE)
        binding.tvDeliverMoney.setVisibility(View.GONE)
        binding.shopCart.noShop.setVisibility(View.GONE)
        binding.shopCart.image.setVisibility(View.GONE)
        binding.shopCart.tvAllPriceBottom.setVisibility(View.VISIBLE)
        binding.tvResName.setText(resNameText)
        binding.tvPaySelected.setText(payTvList!![0])
        binding.rlOwnTaken.setOnClickListener { showTimeSelectedDialog(false) }
        binding.addressLl.setOnClickListener { val intent = Intent(this, AddressActivity::class.java)
            startActivityForResult(intent, REQUEST_ADDRESS) }
        binding.payWay.setOnClickListener { payDialog?.show() }
        binding.rlImmediatelyDeliver.setOnClickListener { showTimeSelectedDialog(true) }
        initPayDialog()

        //刚进入界面选择默认收货地址
        addressList = DataSupport.where("selected = ?", "1").find(AddressModel::class.java) as MutableList<AddressModel>
        if (addressList!!.size >0 ) {
            binding.tvLocation.setText(addressList!!.get(0).address)
            binding.tvName.text = addressList!!.get(0).name
            binding.tvPhone.text = addressList!!.get(0).phone
        } else {
            binding.tvLocation.setText("请选择收货地址")
            binding.tvName.setText("")
            binding.tvPhone.setText("")
        }
    }

    private fun initBuyItem(goodsBuyItemNum: GoodsBuyItemNum): View {
        val view: View = LayoutInflater.from(this).inflate(R.layout.buy_list_item, null)
        val name = view.findViewById<View>(R.id.account_item_name) as TextView
        val price = view.findViewById<View>(R.id.account_item_price) as TextView
        val num = view.findViewById<View>(R.id.account_item_num) as TextView
        name.text = goodsBuyItemNum.itemName
        price.text = "￥" + goodsBuyItemNum.buyNum * goodsBuyItemNum.itemPrice
        num.text = "×" + goodsBuyItemNum.buyNum.toString() + ""
        return view
    }

    fun doOnGotoAccount() {
        if (binding.tvDeliverTime.text.toString() == "选择配送时间" && binding.tvTakenTime.text.toString() == "选择堂取时间") {
            Toast.makeText(this, "请选择配送方式!", Toast.LENGTH_SHORT).show()
        } else if (binding.tvLocation.text == "请选择收货地址") {
            Toast.makeText(this, "请选择收货地址!", Toast.LENGTH_SHORT).show()
        } else {
            val orderTimeId = System.currentTimeMillis()
            val userId = DataUtil.getCurrentUser()?.userId ?: 1
            binding.loading.visibility = View.VISIBLE

            //用户使用红包后需要在数据库中将该用户的红包使用状态改变
            if (couponModel != null) {
                val hashMap = HashMap<String, String?>()
                hashMap["buyer_id"] = userId.toString()
                hashMap["red_packet_id"] = java.lang.String.valueOf(couponModel!!.redPaperId)
                HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.ALTER_USER_RED_PACKET, hashMap, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.d("AccountActivity", response.body().string())
                    }
                })
            }

            val newOrder = OrderModel()

            //存到订单表
            val hashMap = HashMap<String, String?>()
            //orderId为当前时间戳+用户Id
            hashMap["order_id"] = orderTimeId.toString() + userId
            newOrder.orderId = orderTimeId.toString() + userId
            hashMap["buyer_id"] = userId.toString()
            newOrder.userId = userId
            hashMap["shop_id"] = list!![0].resId
            newOrder.resId = list!![0].resId?.toInt() ?: 0
            hashMap["order_address"] = binding.tvLocation.text.toString()
            newOrder.orderAddress = binding.tvLocation.text.toString()
            hashMap["pay_way"] = binding.tvPaySelected.text.toString()
            newOrder.payWay = binding.tvPaySelected.text.toString()

            //2代表待接单
            hashMap["order_state"] = 2.toString()
            newOrder.orderTime = getNow()
            newOrder.orderState = 2
            hashMap["order_remark"] = binding.extraInfo.text.toString()
            newOrder.orderDescription = binding.extraInfo.text.toString()
            newOrder.orderAddress = binding.tvLocation.text as String?
            if (binding.tvDeliverTime.text.toString() != "选择配送时间") {
                hashMap["isdeliver"] = 1.toString() //外送则为1
                hashMap["order_amount"] = allMoney.toString() //外送则加上配送费
                newOrder.orderAmount = allMoney.toFloat()
                if (binding.tvDeliverTime.text.toString() != "立即配送") {
                    //预约时间
                    hashMap["servicetime"] = binding.tvDeliverTime.text.toString()
                }
                if (couponModel == null) {
                    hashMap["pay_amount"] = (allMoney - reduceMoney).toString()
                    newOrder.orderAmount = (allMoney - reduceMoney).toFloat()
                } else {
                    hashMap["pay_amount"] = java.lang.String.valueOf(allMoney - reduceMoney - couponModel!!.price)
                    newOrder.orderAmount = (allMoney - reduceMoney- couponModel!!.price).toFloat()
                }
            } else {
                hashMap["isdeliver"] = 0.toString()
                hashMap["order_amount"] = java.lang.String.valueOf(allMoney - list!![0].resExtraMoney) //堂取减去配送费
                newOrder.orderAmount = allMoney.toFloat()
                //预约时间
                hashMap["servicetime"] = binding.tvTakenTime.text.toString()
                if (couponModel == null) {
                    hashMap["pay_amount"] = java.lang.String.valueOf(allMoney - reduceMoney - list!![0].resExtraMoney)
                    newOrder.orderAmount = (allMoney - reduceMoney - list!![0].resExtraMoney).toFloat()
                } else {
                    hashMap["pay_amount"] = java.lang.String.valueOf(allMoney - reduceMoney - couponModel!!.price - list!![0].resExtraMoney)
                    newOrder.orderAmount = (allMoney - reduceMoney - couponModel!!.price - list!![0].resExtraMoney).toFloat()
                }
            }
            val shop = DataUtil.getShopWithID(resId)
            newOrder.resImg = shop?.resImg
            newOrder.resName = shop?.resName
            newOrder.orderAmount = allMoney.toFloat()
            newOrder.orderPrice = allMoney
            //订单明细表json数据
            hashMap["data"] = Gson().toJson(list)
            newOrder.save()
            val data = Gson().toJson(newOrder)
            HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.SAVE_ORDER, hashMap, object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val responseText = response.body().string()
                    runOnUiThread {
                        Toast.makeText(this@AccountActivity, "支付成功", Toast.LENGTH_SHORT).show()
                        binding.loading.visibility = View.GONE
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


    private fun initPayDialog() {
        payDialog = Dialog(this, R.style.ActionSheetDialogStyle)
        //填充对话框的布局
        val view = LayoutInflater.from(this).inflate(R.layout.popup_pay_bottom, null)
        val payRecyclerView: RecyclerView = view.findViewById<View>(R.id.pay_recycler) as RecyclerView
        val close = view.findViewById<View>(R.id.close) as ImageButton
        close.setOnClickListener { payDialog?.dismiss() }
        val linearLayoutManager = LinearLayoutManager(this)
        payRecyclerView.setLayoutManager(linearLayoutManager)
        val adapter = PopupPayWayAdapter(this, payTvList, payIvList)
        adapter.setOnItemClickListener { position ->
            payDialog?.dismiss()
            adapter.setSelected(position)
            binding.loading.setVisibility(View.VISIBLE)
            Handler(mainLooper).postDelayed({
                binding.loading.setVisibility(View.GONE)
                binding.tvPaySelected.setText(payTvList!![position])
            }, 500)
        }
        payRecyclerView.setAdapter(adapter)
        payDialog?.setCancelable(false)
        //将布局设置给Dialog
        payDialog?.setContentView(view)
        //获取当前Activity所在的窗体
        val dialogWindow = payDialog?.getWindow()
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        val lp = dialogWindow.attributes
        lp.y = 0 //设置Dialog距离底部的距离
        //设置dialog宽度满屏
        val m = dialogWindow.windowManager
        val d = m.defaultDisplay
        lp.width = d.width
        //将属性设置给窗体
        dialogWindow.attributes = lp
    }

    private fun showTimeSelectedDialog(isDeliver: Boolean) {
        //初始化堂取时间
        val timeList: MutableList<String> = ArrayList()
        val format: DateFormat = SimpleDateFormat("HH:mm")
        //开始时间
        var currentTime = System.currentTimeMillis()
        currentTime += (20 * 60 * 1000).toLong()
        //结束时间
        val c1: Calendar = GregorianCalendar()
        c1[Calendar.HOUR_OF_DAY] = 22
        val nineTime = c1.timeInMillis
        if (isDeliver) {
            timeList.add("立即配送")
        }
        var i = currentTime
        while (i < nineTime) {
            val date = Date(i)
            timeList.add(format.format(date))
            i += (20 * 60 * 1000).toLong()
        }
        val dialog = Dialog(this, R.style.ActionSheetDialogStyle)
        //填充对话框的布局
        val view = LayoutInflater.from(this).inflate(R.layout.popup_time_from_bottom, null)
        val time = view.findViewById<View>(R.id.time) as TextView
        if (isDeliver) {
            time.text = "选择配送时间"
        }
        val recyclerView: RecyclerView = view.findViewById<View>(R.id.time_recycler) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(linearLayoutManager)
        val adapter = PupupTimeAdapter(this, timeList)
        if (isDeliver) {
            adapter.setOnItemClickListener { position ->
                binding.tvTakenTime.setText("选择堂取时间")
                binding.tvDeliverMoney.setText("￥" + list!![0].resExtraMoney)
                if (couponModel == null) {
                    binding.tvAllMoney.setText(df.format(allMoney - reduceMoney))
                    binding.shopCart.tvAllPriceBottom.setText("￥" + df.format(allMoney - reduceMoney))
                } else {
                    binding.tvAllMoney.setText(df.format(allMoney - couponModel!!.price - reduceMoney))
                    binding.shopCart.tvAllPriceBottom.setText("￥" + df.format(allMoney - couponModel!!.price - reduceMoney))
                }
                binding.tvDeliverTime.setText(timeList[position])
                dialog.dismiss()
            }
        } else {
            adapter.setOnItemClickListener { position ->
                binding.tvTakenTime.setText(timeList[position])
                binding.tvDeliverTime.setText("选择配送时间")
                binding.tvDeliverMoney.setText("免")
                if (couponModel == null) {
                    binding.tvAllMoney.setText(df.format(allMoney - list!![0].resExtraMoney - reduceMoney))
                    binding.shopCart.tvAllPriceBottom.setText("￥" + df.format(allMoney - list!![0].resExtraMoney - reduceMoney))
                } else {
                    binding.tvAllMoney.setText(df.format(allMoney - list!![0].resExtraMoney - couponModel!!.price - reduceMoney))
                    binding.shopCart.tvAllPriceBottom.setText("￥" + df.format(allMoney - list!![0].resExtraMoney - couponModel!!.price - reduceMoney))
                }
                dialog.dismiss()
            }
        }
        recyclerView.setAdapter(adapter)

        //将布局设置给Dialog
        dialog.setContentView(view)
        //获取当前Activity所在的窗体
        val dialogWindow = dialog.window
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        val lp = dialogWindow.attributes
        lp.y = 0 //设置Dialog距离底部的距离
        //设置dialog宽度满屏
        val m = dialogWindow.windowManager
        val d = m.defaultDisplay
        lp.width = d.width
        //将属性设置给窗体
        dialogWindow.attributes = lp
        dialog.show() //显示对话框
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AccountActivity.REQUEST_ADDRESS -> if (resultCode == RESULT_OK) {
                binding.loading.setVisibility(View.VISIBLE)
                Handler(mainLooper).postDelayed({
                    val addressBean: AddressModel = data?.getSerializableExtra("address") as AddressModel
                    binding.tvLocation.setText(addressBean.address)
                    binding.tvName.setText(addressBean.name)
                    binding.tvPhone.setText(addressBean.phone)
                    binding.loading.setVisibility(View.GONE)
                }, 700)
            }
            AccountActivity.REQUEST_COUPON -> if (resultCode == RESULT_OK) {
                couponModel = data?.getSerializableExtra("coupon") as CouponModel?
                if (couponModel == null) return
                val price = couponModel!!.price as Int
                if (couponModel!!.price > price) {
                    binding.tvRedPaper.setText("-￥" + couponModel!!.price)
                } else {
                    binding.tvRedPaper.setText("-￥$price")
                }
                binding.tvRedPaper.setTextColor(resources.getColor(R.color.colorRed))
                binding.tvRedPaper.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                if (binding.tvTakenTime.getText().toString() == "选择堂取时间") {
                    binding.tvAllMoney.setText(df.format(allMoney - couponModel!!.price.toDouble() - reduceMoney))
                    binding.shopCart.tvAllPriceBottom.setText("￥" + df.format(allMoney - couponModel!!.price.toDouble() - reduceMoney))
                } else {
                    binding.tvAllMoney.setText(df.format(allMoney - list!![0].resExtraMoney - couponModel!!.price.toDouble() - reduceMoney))
                    binding.shopCart.tvAllPriceBottom.setText("￥" + df.format(allMoney - list!![0].resExtraMoney - couponModel!!.price.toDouble() - reduceMoney))
                }
            }
        }
    }
    fun getNow(): String {
        if (android.os.Build.VERSION.SDK_INT >= 24){
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        }else{
            var tms = Calendar.getInstance()
            return tms.get(Calendar.YEAR).toString() + "-" + tms.get(Calendar.MONTH).toString() + "-" + tms.get(Calendar.DAY_OF_MONTH).toString() + " " + tms.get(Calendar.HOUR_OF_DAY).toString() + ":" + tms.get(Calendar.MINUTE).toString() +":" + tms.get(Calendar.SECOND).toString()
        }

    }

}