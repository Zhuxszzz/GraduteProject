package com.niantch.graproject.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.Dataset
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.niantch.graproject.adapter.TabFragmentAdapter
import com.niantch.graproject.databinding.ActivityMainBinding
import com.niantch.graproject.model.ShopDetailModel
import com.niantch.graproject.viewmodel.UserViewModel
import org.litepal.crud.DataSupport

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<ShopDetailModel>()
    private val fragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()
        initViewPager()
    }

    override fun onStart() {
        super.onStart()
//        binding.ivAddAddress.setOnClickListener {
//            val intent = Intent(this, AddAddressActivity::class.java)
//            startActivityForResult(intent, ACTIVITY_CODE)
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initFragment() {
        fragments.add(HomePageFragment())
        fragments.add(CartFragment())
        fragments.add(OrderFragment())
        fragments.add(UserFragment())
    }

    private fun initViewPager() {
        val fragmentAdapter = TabFragmentAdapter(supportFragmentManager)
        fragmentAdapter.fragments = this.fragments
        fragmentAdapter.titles = arrayListOf("主页", "购物车", "订单", "我的")
        binding.vpFragmentsContainer.adapter = fragmentAdapter
        binding.tlFragmentBottom.setupWithViewPager(binding.vpFragmentsContainer)
    }
}