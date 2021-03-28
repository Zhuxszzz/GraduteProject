package com.niantch.graproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.niantch.graproject.R
import com.niantch.graproject.adapter.MyFragmentPagerAdapter
import com.niantch.graproject.databinding.ActivityMainBinding
import com.niantch.graproject.model.ResDetailModel
import com.niantch.graproject.ui.AddAddressActivity.Companion.ACTIVITY_CODE
import com.tencent.tencentmap.mapsdk.maps.MapFragment
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<ResDetailModel>()
    private val fragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.ivAddAddress.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(intent, ACTIVITY_CODE)
        }
        initFragment()
        initViewPager()
    }

    private fun initFragment() {
        fragments.add(UserFragment())
    }

    private fun initViewPager() {
        val fragmentAdapter = MyFragmentPagerAdapter(supportFragmentManager)
        fragmentAdapter.fragments = this.fragments
        binding.vpFragmentsContainer.adapter = fragmentAdapter
        binding.tlFragmentBottom.setupWithViewPager(binding.vpFragmentsContainer)
    }
}