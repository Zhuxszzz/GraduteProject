package com.niantch.graproject.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.niantch.graproject.R

class PreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        startActivity(intent)
        finish()
    }
}