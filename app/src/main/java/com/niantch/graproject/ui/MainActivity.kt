package com.niantch.graproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.niantch.graproject.R
import com.niantch.graproject.databinding.ActivityMainBinding
import com.niantch.graproject.ui.AddAddressActivity.Companion.ACTIVITY_CODE
import com.tencent.tencentmap.mapsdk.maps.MapFragment
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    }
}