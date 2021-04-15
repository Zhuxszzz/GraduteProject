package com.niantch.graproject.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.adapter.OrderFragmentAdapter
import com.niantch.graproject.databinding.OrderFagmentBinding
import com.niantch.graproject.model.OrderBean
import com.niantch.graproject.model.UserBean
import com.niantch.graproject.ui.UserFragment.Companion.REQUEST_LOGIN
import com.niantch.graproject.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.litepal.crud.DataSupport
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * author: niantchzhu
 * date: 2021
 */
class OrderFragment: Fragment() {
    private lateinit var binding: OrderFagmentBinding
    private var adapter: OrderFragmentAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var orderList = ArrayList<OrderBean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = OrderFagmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initData()
        initUI()
    }

    fun initUI() {
        binding.loginBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivityForResult(intent, REQUEST_LOGIN)
        })
        if (DataSupport.findAll(UserBean::class.java).size > 0) {
            binding.loginBtn.setVisibility(View.GONE)
            binding.orderFragmentRecycler.setLayoutManager(linearLayoutManager)
            requestListData()
        } else {
            binding.orderFragmentRecycler.setVisibility(View.GONE)
            binding.listEmpty.setVisibility(View.GONE)
            binding.loginBtn.setVisibility(View.VISIBLE)
        }
    }

    fun initData() {
        linearLayoutManager = LinearLayoutManager(context)
        adapter = OrderFragmentAdapter(context!!, orderList)
        adapter?.setOnItemBtnClickListener(object : OrderFragmentAdapter.OnItemBtnClickListener {
            override fun onItemBtnClick(position: Int, state: Int) {
                when (state) {
                    2 -> {
                        binding.firstLoad.visibility = View.VISIBLE
                        val hashMap = HashMap<String, String?>()
                        hashMap["order_id"] = orderList.get(position).orderId
                        hashMap["buyer_id"] = PreferenceManager.getDefaultSharedPreferences(context).getInt("user_id", -1).toString() + ""
                        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.ORDER_CANCEL, hashMap, object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                activity!!.runOnUiThread {
                                    binding.firstLoad.visibility = View.GONE
                                    Toast.makeText(context, "网络错误，请检查网络!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            @Throws(IOException::class)
                            override fun onResponse(call: Call, response: Response) {
                                val responseText = response.body().string()
                                activity!!.runOnUiThread {
                                    try {
                                        val jsonObject = JSONObject(responseText)
                                        if (jsonObject.getInt("state") == 1) {
                                            orderList.clear()
                                            orderList.addAll(Gson().fromJson<Any>(jsonObject.getJSONArray("data").toString(), object : TypeToken<List<OrderBean?>?>() {}.type) as List<OrderBean>)
                                            adapter?.notifyDataSetChanged()
                                            Toast.makeText(context, "订单已取消!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "失败!", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: JSONException) {
                                        Toast.makeText(context, "失败!", Toast.LENGTH_SHORT).show()
                                    }
                                    binding.firstLoad.visibility = View.GONE
                                }
                            }
                        })
                    }
                }
            }
        })
        binding.orderFragmentRecycler.layoutManager = linearLayoutManager
        binding.orderFragmentRecycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (DataSupport.findAll(UserBean::class.java).size == 0) {
            binding.loginBtn.visibility = View.VISIBLE
            binding.orderFragmentRecycler.visibility = View.GONE
            binding.listEmpty.visibility = View.GONE
        } else {
            binding.loginBtn.visibility = View.GONE
            requestListData()
        }
    }

    //请求订单数据
    private fun requestListData() {
        //请求数据
        val hashMap = HashMap<String, String?>()
        hashMap["buyer_id"] = PreferenceManager.getDefaultSharedPreferences(context).getInt("user_id", -1).toString()
        binding.firstLoad.visibility = View.VISIBLE
        binding.orderFragmentRecycler.visibility = View.GONE
        binding.listEmpty.visibility = View.GONE
        HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.GET_ORDER, hashMap, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, e.toString())
                activity!!.runOnUiThread {
                    binding.firstLoad.visibility = View.GONE
                    binding.listEmpty.visibility = View.VISIBLE
                    Toast.makeText(context, "网络错误，请检查网络设置！", Toast.LENGTH_SHORT).show()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body().string()
                activity!!.runOnUiThread {
                    binding.firstLoad.visibility = View.GONE
                    try {
                        val jsonObject = JSONObject(jsonString)
                        val status = jsonObject.getInt("status")
                        if (status != 0) {
                            orderList.clear()
                            orderList.addAll(Gson().fromJson<Any>(jsonObject.getJSONArray("data").toString(), object : TypeToken<List<OrderBean?>?>() {}.type) as List<OrderBean>)
                            if (orderList.size == 0) {
                                binding.orderFragmentRecycler.visibility = View.GONE
                                binding.listEmpty.visibility = View.VISIBLE
                            } else {
                                adapter?.notifyDataSetChanged()
                                binding.orderFragmentRecycler.visibility = View.VISIBLE
                                //置顶binding.orderFragmentRecycler
                                linearLayoutManager?.scrollToPositionWithOffset(0, 0)
                            }
                        } else {
                            binding.listEmpty.visibility = View.VISIBLE
                            Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        binding.listEmpty.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    companion object {
        const val TAG ="OrderFragment"
    }

}