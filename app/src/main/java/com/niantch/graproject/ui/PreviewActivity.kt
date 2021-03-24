package com.niantch.graproject.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.niantch.graproject.R
import java.util.*

class PreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, MainActivity::class.java)
        val timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    startActivity(intent)
                    finish()
                    timer.cancel()
                }
            }, 800
        )
    }
}