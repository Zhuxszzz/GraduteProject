package com.niantch.graproject.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.R
import com.niantch.graproject.adapter.TabFragmentAdapter
import com.niantch.graproject.databinding.ActivityResBinding
import com.niantch.graproject.event.MessageEvent
import com.niantch.graproject.model.*
import com.niantch.graproject.model.Constants.RES_DETAIL
import com.niantch.graproject.utils.FileStorage
import com.niantch.graproject.utils.GlideUtil
import com.niantch.graproject.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import org.litepal.crud.DataSupport
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * author: niantchzhu
 * date: 2021
 */
class ResActivity : AppCompatActivity(), View.OnClickListener {

    val RES_ID = "res_id"
    private lateinit var binding: ActivityResBinding
    private var homeRecResDetailModel: ResDetailModel? = null

    //优惠总数
    private var specialNum = 0

    //fragment列表
    private val mFragments: MutableList<Fragment> = ArrayList<Fragment>()

    //tab名的列表
    private val mTitles: MutableList<String> = ArrayList()

    private var totalMoney = 0.0

    private var goodsListBean: GoodListModel? = null
    private var categoryBeanList: List<GoodsCategoryModel>? = null

    private var adapter: TabFragmentAdapter? = null
    private var anim_mask_layout //动画层
            : ViewGroup? = null

    private var resId = 0
    private var resName: String? = null
    private var discountString: String? = null
    private var discountBeanList: List<DiscountModel>? = null

    fun getResDetailModel(): ResDetailModel? {
        return homeRecResDetailModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResBinding.inflate(layoutInflater)
        //        setTheme(R.style.AppTheme_Fullscreen);
//        setStatusBarTransparent()
        Log.d("process test", "main process :" + Process.myPid())
        Log.d("process test", "main process current thread:" + Process.myTid())
        Thread(Runnable {
            Log.d("process test", "main process son thread:" + Process.myTid())
            runOnUiThread { Log.d("process test", "main process main thread:" + Process.myTid()) }
        }).start()
    }

    fun initView() {
        binding.returnBtn.setOnClickListener(this)
        binding.myShop.goToAccount.setOnClickListener(this)
        binding.searchLl.setOnClickListener(this)
        binding.myShop.popRl.setOnClickListener(this)
        binding.moreIv.setOnClickListener(this)
    }

    fun initData() {
        goodsListBean = GoodListModel()
        val intent = intent
        homeRecResDetailModel = intent.getSerializableExtra(RES_DETAIL) as ResDetailModel
        if (homeRecResDetailModel == null) {
            resId = intent.getStringExtra(RES_ID).toInt()
            resName = intent.getStringExtra("res_name")

            //请求店铺信息
            binding.progressBar.visibility = View.VISIBLE
            val hashMap = HashMap<String, String?>()
            hashMap["shop_id"] = intent.getStringExtra(RES_ID)
            HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_SHOP_BY_ID, hashMap, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("homeRecResDetail fail", e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ResActivity, "网络连接超时，请检查网络!", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val responseText = response.body().string()
                    try {
                        val jsonObject = JSONObject(responseText)
                        val status = jsonObject.getInt("status")
                        if (status == 1) {
                            homeRecResDetailModel = Gson().fromJson(jsonObject.getJSONObject("data").toString(), ResDetailModel::class.java)
                            runOnUiThread {
                                setResDetail()
                                requestResGoods()
                            }
                        }
                    } catch (e: JSONException) {
                    }
                }
            })
        } else {
            setResDetail()
            resId = homeRecResDetailModel?.resId ?: 0
            resName = homeRecResDetailModel?.resName
            requestResGoods()
        }
    }

    private fun requestResGoods() {
        //请求server端的店铺商品列表
        binding.progressBar.visibility = View.VISIBLE
        val hashMap = HashMap<String, String?>()
        hashMap["shop_id"] = resId.toString()
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.OBTAIN_SHOP_GOODS, hashMap, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("", e.toString())
                runOnUiThread {
                    Toast.makeText(this@ResActivity, "网络连接超时，请检查网络!", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                categoryBeanList = Gson().fromJson<List<GoodsCategoryModel>>(response.body().string(), object : TypeToken<List<GoodsCategoryModel?>?>() {}.type)
                runOnUiThread {
                    try {
                        goodsListBean?.goodsCategoryList = categoryBeanList
                        goodsListBean?.resName = resName
                        goodsListBean?.resId = resId
                        binding.progressBar.visibility = View.GONE
                        setViewPager()
                    } catch (e: Exception) {
                        Toast.makeText(this@ResActivity, "网络连接超时，请检查网络!", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setViewPager() {
        val goodsFragment = GoodsFragment()
        val evaluateFragment = EvaluateFragment()
        val resDetailFragment = ResDetailFragment()
        mFragments.add(goodsFragment)
        mFragments.add(evaluateFragment)
        mFragments.add(resDetailFragment)
        mTitles.add(resources.getString(R.string.order))
        mTitles.add(resources.getString(R.string.evaluate))
        mTitles.add(resources.getString(R.string.restaurant))
        adapter = TabFragmentAdapter(supportFragmentManager)
        adapter?.fragments = mFragments as ArrayList<Fragment>
        adapter?.titles = mTitles as ArrayList<String>
        binding.vp.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.vp)
        //设置tabLayout的下划线的宽度
//        setTabIndicator(tabLayout,36,36);
        binding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> binding.myShop.shopCartMain.visibility = View.VISIBLE
                    1 -> binding.myShop.shopCartMain.visibility = View.GONE
                    2 -> binding.myShop.shopCartMain.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
//
//    fun setTabIndicator(tabs: TabLayout, leftDip: Int, rightDip: Int) {
//        val tabLayout: Class<*> = tabs.getClass()
//        var tabStrip: Field? = null
//        try {
//            tabStrip = tabLayout.getDeclaredField("mTabStrip")
//        } catch (e: NoSuchFieldException) {
//            e.printStackTrace()
//        }
//        tabStrip!!.isAccessible = true
//        var llTab: LinearLayout? = null
//        try {
//            llTab = tabStrip[tabs] as LinearLayout
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }
//        val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip.toFloat(), Resources.getSystem().displayMetrics).toInt()
//        val right = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip.toFloat(), Resources.getSystem().displayMetrics).toInt()
//        for (i in 0 until llTab!!.childCount) {
//            val child = llTab.getChildAt(i)
//            child.setPadding(0, 0, 0, 0)
//            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1
//            params.leftMargin = left
//            params.rightMargin = right
//            child.layoutParams = params
//            child.invalidate()
//        }
//    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.return_btn -> finish()
            R.id.search_ll -> {
                //应该启动店铺内搜索界面，只搜索该店铺内的商品;而不是启动SearchActivity去搜索店铺
//                Intent intent = new Intent(this,SearchActivity.class);
//                startActivity(intent);
//                val intent = Intent(this, OtherProcessActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
            }
            R.id.pop_rl -> showSelectedDetailDialog()
            R.id.more_iv -> {
            }
            R.id.go_to_account -> if (DataSupport.findAll(UserModel::class.java).size > 0) {
                val accountIntent = Intent(this, AccountActivity::class.java)
                accountIntent.putExtra("res_id", resId)
                accountIntent.putExtra("res_name", resName)
                //                    if (discountBeanList != null && discountBeanList.size() > 0){
//                        double reduceMoney = 0;
//                        for (DiscountModel discountBean : discountBeanList){
//                            if (totalMoney >= discountBean.filledVal){
//                                reduceMoney = discountBean.reduceVal;
//                            }
//                        }
//                        accountIntent.putExtra("reduce_money",reduceMoney);
//                    }
                startActivity(accountIntent)
            } else {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
    }

    /**
     * 添加 或者  删除  商品发送的消息处理
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        if (event != null) {
            if (event.num > 0) {
                //double类型保留小数点后一位
                val df = DecimalFormat("#0.0")
                binding.myShop.shopCartNum.text = java.lang.String.valueOf(event.num)
                binding.myShop.shopCartNum.visibility = View.VISIBLE
                binding.myShop.totalPrice.visibility = View.VISIBLE
                binding.myShop.noShop.visibility = View.GONE
                //设置购买的总价钱
                val price2 = event.price.toInt()
                totalMoney = event.price
                if (event.price > price2) {
                    binding.myShop.totalPrice.text = "¥" + df.format(event.price)
                } else {
                    binding.myShop.totalPrice.text = "¥$price2"
                }
                if (event.price >= homeRecResDetailModel!!.resDeliverMoney) {
                    binding.myShop.howMoneyToDelivery.visibility = View.GONE
                    binding.myShop.goToAccount.visibility = View.VISIBLE
                    binding.myShop.goToAccount.text = getString(R.string.go_to_account)
                } else {
                    binding.myShop.goToAccount.visibility = View.GONE
                    binding.myShop.howMoneyToDelivery.visibility = View.VISIBLE
                    //设置还差多少钱起送
                    val price = (homeRecResDetailModel!!.resDeliverMoney - event.price.toInt()) as Int
                    if (homeRecResDetailModel!!.resDeliverMoney - event.price.toInt() > price) {
                        binding.myShop.howMoneyToDelivery.text = "还差￥" + df.format(homeRecResDetailModel!!.resDeliverMoney - event.price.toInt()) + "起送"
                    } else {
                        binding.myShop.howMoneyToDelivery.text = "还差￥" + price + "起送"
                    }
                }
            } else {
                binding.myShop.shopCartNum.visibility = View.GONE
                binding.myShop.totalPrice.visibility = View.GONE
                binding.myShop.noShop.visibility = View.VISIBLE
                binding.myShop.goToAccount.visibility = View.GONE
                binding.myShop.howMoneyToDelivery.visibility = View.VISIBLE
                var deliverMoney = resources.getString(R.string.res_deliver_money)
                deliverMoney = java.lang.String.format(deliverMoney, homeRecResDetailModel!!.resDeliverMoney)
                binding.myShop.howMoneyToDelivery.text = deliverMoney
            }

//            Log.d("ResActivity","添加的数量："+event.goods.size());
        }
    }

    /**
     * 设置动画（点击添加商品）
     * @param v
     * @param startLocation
     */
    fun setAnim(v: View, startLocation: IntArray) {
        anim_mask_layout = null
        anim_mask_layout = createAnimLayout()
        anim_mask_layout!!.addView(v) //把动画小球添加到动画层
        val view = addViewToAnimLayout(anim_mask_layout, v, startLocation)
        val endLocation = IntArray(2) // 存储动画结束位置的X、Y坐标
        binding.myShop.shopCartNum.getLocationInWindow(endLocation)
        // 计算位移
        val endX = 0 - startLocation[0] + 50 // 动画位移的X坐标
        val endY = endLocation[1] - startLocation[1] // 动画位移的y坐标
        val translateAnimationX = TranslateAnimation(0F, endX.toFloat(), 0F, 0F)
        translateAnimationX.interpolator = LinearInterpolator()
        translateAnimationX.repeatCount = 0 // 动画重复执行的次数
        translateAnimationX.fillAfter = true
        val translateAnimationY = TranslateAnimation(0F, 0F, 0F, endY.toFloat())
        translateAnimationY.interpolator = AccelerateInterpolator()
        translateAnimationY.repeatCount = 0 // 动画重复执行的次数
        translateAnimationY.fillAfter = true
        val set = AnimationSet(false)
        set.fillAfter = false
        set.addAnimation(translateAnimationY)
        set.addAnimation(translateAnimationX)
        set.duration = 400 // 动画的执行时间
        view.startAnimation(set)
        // 动画监听事件
        set.setAnimationListener(object : Animation.AnimationListener {
            // 动画的开始
            override fun onAnimationStart(animation: Animation) {
                v.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}

            // 动画的结束
            override fun onAnimationEnd(animation: Animation) {
                v.visibility = View.GONE
            }
        })
    }

    /**
     * 初始化动画图层
     * @return
     */
    private fun createAnimLayout(): ViewGroup? {
        val rootView = this.window.decorView as ViewGroup
        val animLayout = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        animLayout.layoutParams = lp
        animLayout.id = Int.MAX_VALUE - 1
        animLayout.setBackgroundResource(R.color.transparent)
        rootView.addView(animLayout)
        return animLayout
    }

    /**
     * 将View添加到动画图层
     * @param parent
     * @param view
     * @param location
     * @return
     */
    private fun addViewToAnimLayout(parent: ViewGroup?, view: View, location: IntArray): View {
        val x = location[0]
        val y = location[1]
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.leftMargin = x
        lp.topMargin = y
        view.layoutParams = lp
        return view
    }

    fun getGoodListModel(): GoodListModel? {
        return goodsListBean
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun showSelectedDetailDialog() {
        val list: List<ResBuyItemModel> = DataSupport.where("resId = ?", resId.toString()).find(ResBuyItemModel::class.java)
        if (list.size > 0) {
            var packageMoney = 0.0
            val dialog = Dialog(this, R.style.ActionSheetDialogStyle)
            //填充对话框的布局
            val view: View = LayoutInflater.from(this).inflate(R.layout.popup_goods_detail, null)
            val itemLl: LinearLayout = view.findViewById<View>(R.id.item_ll) as LinearLayout
            val packageMoneyTv = view.findViewById<View>(R.id.good_package_money) as TextView
            val packageMoneyRl = view.findViewById<View>(R.id.package_money_rl) as RelativeLayout
            for (resBuyItemNum in list) {
                itemLl.addView(initGoodDetailItemView(resBuyItemNum.itemName, resBuyItemNum.buyNum, resBuyItemNum.itemPrice * resBuyItemNum.buyNum))
                packageMoney += resBuyItemNum.itemPackageMoney * resBuyItemNum.buyNum
            }
            if (packageMoney > 0) {
                packageMoneyRl.visibility = View.VISIBLE
                val price = packageMoney.toInt()
                if (packageMoney > price) {
                    packageMoneyTv.text = "" + packageMoney
                } else {
                    packageMoneyTv.text = "" + price
                }
            } else {
                packageMoneyRl.visibility = View.GONE
            }
            dialog.setCancelable(true)
            //将布局设置给Dialog
            dialog.setContentView(view)
            //获取当前Activity所在的窗体
            val dialogWindow = dialog.window
            //设置Dialog从窗体底部弹出
            dialogWindow!!.setGravity(Gravity.BOTTOM)
            val lp = dialogWindow.attributes
            lp.y = resources.getDimensionPixelOffset(R.dimen.dimen_54dp) //设置Dialog距离底部的距离
            //设置dialog宽度满屏
            val m = dialogWindow.windowManager
            val d = m.defaultDisplay
            lp.width = d.width
            //将属性设置给窗体
            dialogWindow.attributes = lp
            dialog.show()
        }
    }

    private fun initGoodDetailItemView(goodNameText: String?, num: Int?, goodPriceText: Double): View? {
        val view: View = LayoutInflater.from(this).inflate(R.layout.goods_detail_item, null)
        val goodName = view.findViewById<View>(R.id.good_name) as TextView
        val goodNum = view.findViewById<View>(R.id.good_num) as TextView
        val goodPrice = view.findViewById<View>(R.id.good_price) as TextView
        goodName.text = goodNameText
        goodNum.text = "×$num"
        val price = goodPriceText.toInt()
        if (goodPriceText > price) {
            goodPrice.text = "" + goodPriceText
        } else {
            goodPrice.text = "" + price
        }
        return view
    }

    private fun setResDetail() {
        //设置满减活动
        discountBeanList = homeRecResDetailModel!!.discountList
        if (discountBeanList != null && discountBeanList!!.isNotEmpty()) {
            val sb = StringBuffer()
            for (discountBean in homeRecResDetailModel!!.discountList!!) {
                val fillPrice = discountBean.filledVal.toInt()
                val reducePrice = discountBean.reduceVal.toInt()
                if (discountBean.filledVal > fillPrice) {
                    sb.append("满" + discountBean.filledVal)
                } else {
                    sb.append("满$fillPrice")
                }
                if (discountBean.reduceVal > reducePrice) {
                    sb.append("减" + discountBean.reduceVal.toString() + ",")
                } else {
                    sb.append("减$reducePrice,")
                }
            }
            discountString = sb.toString().substring(0, sb.length - 1)
        }
        if (!TextUtils.isEmpty(discountString)) {
            binding.resReduceContainer.visibility = View.VISIBLE
            binding.resReduce.text = discountString
            specialNum = 1
        }

        //设置优惠总数
        if (specialNum > 0) {
            binding.resSpecialNum.text = specialNum.toString() + "个优惠"
            binding.resSpecialNum.visibility = View.VISIBLE
        }

        //设置商家名称
        val resName: String? = homeRecResDetailModel?.resName
        binding.resName.text = resName

        //设置起送费
        var deliverMoney = resources.getString(R.string.res_deliver_money)
        deliverMoney = java.lang.String.format(deliverMoney, homeRecResDetailModel!!.resDeliverMoney)
        binding.myShop.howMoneyToDelivery.text = deliverMoney

        //设置店铺顶部图片
        val resImg: String? = homeRecResDetailModel?.shopPic
        if (!TextUtils.isEmpty(resImg)) {
            GlideUtil.load(this, resImg, binding.resImg, GlideUtil.REQUEST_OPTIONS)
            Thread(Runnable { //该方法必须在子线程中执行
                val drawable: Drawable? = FileStorage.loadImageFromNetwork(resImg)
                //回到主线程更新ui
                runOnUiThread { binding.collapsing.contentScrim = drawable }
            }).start()
        }

        //设置月售多少单
        val monthOrder: Int = homeRecResDetailModel!!.resOrderNum
        var monthOrderNum = resources.getString(R.string.res_month_sell_order)
        monthOrderNum = String.format(monthOrderNum, monthOrder)
        binding.resOrderNum.text = monthOrderNum

        //设置配送时间
        val deliverTime: Int = homeRecResDetailModel?.resDeliverTime ?: 0
        var businessDeliverTime = resources.getString(R.string.res_business_deliver_time)
        businessDeliverTime = String.format(businessDeliverTime, deliverTime)
        binding.resDeliverTime.text = businessDeliverTime

        //设置星星评分
        val starNum: Float = homeRecResDetailModel!!.resStar
        if (starNum > 0) {
            binding.resStar.rating = starNum
            binding.resScore.text = starNum.toString() + ""
            binding.resStar.visibility = View.VISIBLE
        }

        //设置商家描述
        val resDescription: String = homeRecResDetailModel?.resDescription ?: ""
        if (!TextUtils.isEmpty(resDescription)) {
            binding.resDescription.text = resDescription
        }
    }

}