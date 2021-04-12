package com.niantch.graproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.niantch.graproject.adapter.TabFragmentAdapter
import com.niantch.graproject.databinding.ActivityMainBinding
import com.niantch.graproject.model.ResDetailModel
import com.niantch.graproject.ui.AddAddressActivity.Companion.ACTIVITY_CODE

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<ResDetailModel>()
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
        binding.ivAddAddress.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(intent, ACTIVITY_CODE)
        }

    }

    private fun initFragment() {
        fragments.add(HomePageFragment())
        fragments.add(CartFragment())
        fragments.add(UserFragment())
    }

    private fun initViewPager() {
        val fragmentAdapter = TabFragmentAdapter(supportFragmentManager)
        fragmentAdapter.fragments = this.fragments
        fragmentAdapter.titles = arrayListOf("1","2","3","4")
        binding.vpFragmentsContainer.adapter = fragmentAdapter
        binding.tlFragmentBottom.setupWithViewPager(binding.vpFragmentsContainer)
    }
}