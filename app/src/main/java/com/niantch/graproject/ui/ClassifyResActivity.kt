package com.niantch.graproject.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.niantch.graproject.R
import com.niantch.graproject.adapter.ClassifyResActivityAdapter
import com.niantch.graproject.adapter.MultipleOrderPopWinAdapter
import com.niantch.graproject.databinding.ActivityClassifyResBinding
import com.niantch.graproject.model.GoodsBuyItemNum
import com.niantch.graproject.model.ResDetailBean
import com.niantch.graproject.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException
import java.util.*

class ClassifyResActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = "ClassifyResActivity"
        val RES_CLASSIFY = "res_classify"
    }
    lateinit var binding: ActivityClassifyResBinding


    private var adapter: ClassifyResActivityAdapter? = null
    private var list: MutableList<ResDetailBean>? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var resClassify: String? =  null
    private var popWin: MultipleOrderPopupWindow? = null
    private var selectedFlag = 0

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityClassifyResBinding.inflate(layoutInflater)
    }

    protected fun initData() {
        resClassify = intent.getStringExtra(RES_CLASSIFY)
        list = ArrayList<ResDetailBean>()
        linearLayoutManager = LinearLayoutManager(this@ClassifyResActivity)
        binding.classifyRecycler.setLayoutManager(linearLayoutManager)
        adapter = ClassifyResActivityAdapter(this@ClassifyResActivity, list)
        binding.classifyRecycler.setAdapter(adapter)
        //刚进入界面请求商家列表数据
        val hashMap =
            HashMap<String, String?>()
        hashMap["label_name"] = resClassify
        notifyResList(hashMap)
        val list: MutableList<String> =
            ArrayList()
        list.add(resources.getString(R.string.multiple_order))
        list.add(resources.getString(R.string.sale_highest))
        list.add(resources.getString(R.string.deliver_price_lowest))
        list.add(resources.getString(R.string.deliver_time_lowest))
        list.add(resources.getString(R.string.extra_price_lowest))
        popWin = MultipleOrderPopupWindow(this, list, object : MultipleOrderPopWinAdapter.OnMultipleOrderItemClickListener {
            override fun onMultipleOrderItemClick(position: Int) {
                popWin?.setSelectedPosition(position)
                popWin?.dismiss()
                binding.orderMultipleOrder.text = list[position]
                //重新请求商家列表数据
//                HashMap<String,String> hashMap = new HashMap<>();
//                hashMap.put(RES_CLASSIFY,resClassify);
//                hashMap.put(RES_ORDER_MODE,list.get(position));
//                notifyResList(hashMap);
            }
        })
        popWin?.setOnDismissListener(PopupWindow.OnDismissListener {
            val selectedPosition: Int = popWin!!.getSelectedPosition()
            if (selectedPosition == -1) {
                binding.orderMultipleOrder.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.mipmap.down_grey),
                    null
                )
                binding.orderMultipleOrder.setTextColor(resources.getColor(R.color.black_60))
                if (selectedFlag == 1) {
                    binding.orderGoodCommon.setTextColor(resources.getColor(R.color.black))
                    binding.orderShortDistance!!.setTextColor(resources.getColor(R.color.black_60))
                } else if (selectedFlag == 2) {
                    binding.orderGoodCommon.setTextColor(resources.getColor(R.color.black_60))
                    binding.orderShortDistance!!.setTextColor(resources.getColor(R.color.black))
                }
            } else {
                binding.orderMultipleOrder.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.mipmap.down_black),
                    null
                )
                binding.orderMultipleOrder.setTextColor(resources.getColor(R.color.black))
                binding.orderGoodCommon.setTextColor(resources.getColor(R.color.black_60))
                binding.orderShortDistance!!.setTextColor(resources.getColor(R.color.black_60))
            }
        })
    }

    fun initView() {
//        backBtn!!.setOnClickListener(this)
//        searchBtn!!.setOnClickListener(this)
        binding.orderMultipleOrder.setOnClickListener(this)
        binding.orderShortDistance.setOnClickListener(this)
        binding.orderGoodCommon.setOnClickListener(this)
//        title!!.text = intent.getStringExtra(OneFragment.RES_TITLE)
//        searchBtn!!.visibility = View.VISIBLE

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        binding.classifyRecycler.setLayoutManager(linearLayoutManager);
//        adapter = new ClassifyResActivityAdapter(this,list);
//        binding.classifyRecycler.setAdapter(adapter);
    }

    override fun onResume() {
        super.onResume()
        notifyResBuyNum()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.order_good_common -> {
                //                HashMap<String,String> hashMap = new HashMap<>();
//                hashMap.put(RES_CLASSIFY,resClassify);
//                hashMap.put(RES_ORDER_MODE,"order_by_comment");
//                notifyResList(hashMap);
                binding.orderGoodCommon.setTextColor(resources.getColor(R.color.black))
                binding.orderShortDistance!!.setTextColor(resources.getColor(R.color.black_60))
                binding.orderMultipleOrder.text = resources.getString(R.string.multiple_order)
                binding.orderMultipleOrder.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.mipmap.down_grey),
                    null
                )
                binding.orderMultipleOrder.setTextColor(resources.getColor(R.color.black_60))
                popWin?.setSelectedPosition(-1)
                selectedFlag = 1
            }
            R.id.order_short_distance -> {
                //                HashMap<String,String> disHashMap = new HashMap<>();
//                disHashMap.put(RES_CLASSIFY,resClassify);
//                disHashMap.put(RES_ORDER_MODE,"order_by_distance");
//                notifyResList(disHashMap);
                binding.orderGoodCommon.setTextColor(resources.getColor(R.color.black_60))
                binding.orderShortDistance.setTextColor(resources.getColor(R.color.black))
                binding.orderMultipleOrder.text = resources.getString(R.string.multiple_order)
                binding.orderMultipleOrder.setTextColor(resources.getColor(R.color.black_60))
                binding.orderMultipleOrder.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.mipmap.down_grey),
                    null
                )
                popWin?.setSelectedPosition(-1)
                selectedFlag = 2
            }
            R.id.order_multiple_order -> {
                popWin?.showAsDropDown(binding.divider)
                binding.orderMultipleOrder.setTextColor(resources.getColor(R.color.bottom_tab_text_selected_color))
                binding.orderMultipleOrder.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.mipmap.down_selected),
                    null
                )
                binding.orderGoodCommon.setTextColor(resources.getColor(R.color.black_60))
                binding.orderShortDistance!!.setTextColor(resources.getColor(R.color.black_60))
            }
        }
    }

    private fun notifyResList(hashMap: HashMap<String, String?>) {
        binding.firstLoad.visibility = View.VISIBLE
        binding.order.setVisibility(View.GONE)
        binding.classifyRecycler.setVisibility(View.GONE)
        binding.emptyView.visibility = View.GONE
        HttpUtil.sendOkHttpPostRequest(
            HttpUtil.HOME_PATH + HttpUtil.OBTAIN_SHOP_BY_LABEL,
            hashMap,
            object : Callback {
                override fun onFailure(call: Call?, e: IOException) {
                    runOnUiThread {
                        binding.emptyView.visibility = View.VISIBLE
                        binding.firstLoad.visibility = View.GONE
                        Toast.makeText(
                            this@ClassifyResActivity,
                            "连接超时，请检查网络设置!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.d(TAG, e.toString())
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call?, response: Response) {
                    list!!.clear()
                    // TODO: 4/7/21  add mockked data
//                    list!!.addAll(
//                        (Gson().fromJson(
//                            response.body().string(),
//                            object :
//                                TypeToken<List<ResDetailModel?>?>() {}.type
//                        ) as List<*>?)!!
//                    )
                    runOnUiThread { //请求完数据在UI线程更新ui
                        binding.firstLoad.visibility = View.GONE
                        if (list!!.size == 0 || list == null) {
                            binding.order.setVisibility(View.GONE)
                            binding.emptyView.visibility = View.VISIBLE
                        } else {
                            notifyResBuyNum()
                            binding.order.setVisibility(View.VISIBLE)
                            binding.classifyRecycler.setVisibility(View.VISIBLE)
                        }
                    }
                }
            })
    }

    private fun notifyResBuyNum() {
        val goodsBuyCategoryNumList: List<GoodsBuyItemNum> =
            DataSupport.findAll(
                GoodsBuyItemNum::class.java
            )
        val resIdList: MutableList<String> =
            ArrayList()
        if (list != null) {
            if (goodsBuyCategoryNumList != null && goodsBuyCategoryNumList.size > 0) {
                val resBuyNumTable =
                    Hashtable<String, Int>()
                //将resBuyCategoryNumList中的添加到购物车的数量按resId设置给resBuyNumTable
                for (i in list!!.indices) {
                    resBuyNumTable[java.lang.String.valueOf(list!![i].resId)] = 0
                    resIdList.add(list!![i].resId.toString() + "")
                }
                for (resBuyCategoryNum in goodsBuyCategoryNumList) {
                    if (resIdList.contains(resBuyCategoryNum.resId)) {
                        val num: Int =
                            resBuyNumTable[resBuyCategoryNum.resId]!! + resBuyCategoryNum.buyNum
                        resBuyNumTable[resBuyCategoryNum.resId] = num
                    }
                }
                //得到resBuyNumTable中的keyList
                val keyList: MutableList<String> =
                    ArrayList()
                val itr: Iterator<String> = resBuyNumTable.keys.iterator()
                while (itr.hasNext()) {
                    val str = itr.next()
                    keyList.add(str)
                }
                //遍历设置homeRecResDetailList各项的buyNum
                for (i in list!!.indices) {
                    for (j in keyList.indices) {
                        if (list!![i].resId === keyList[j].toInt()) {
                            list!![i].buyNum = resBuyNumTable[keyList[j]]!!
                        }
                    }
                }
            } else {
                for (i in list!!.indices) {
                    list!![i].buyNum = 0
                }
            }
            //刷新商家列表，显示红点数量
            adapter?.notifyDataSetChanged()
        }
    }

}