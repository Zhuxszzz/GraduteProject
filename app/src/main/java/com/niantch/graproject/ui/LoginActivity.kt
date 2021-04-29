package com.niantch.graproject.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.niantch.graproject.R
import com.niantch.graproject.databinding.ActivityLoginBinding
import com.niantch.graproject.viewmodel.UserViewModel

/**
 * author: niantchzhu
 * date: 2021
 */
class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initObserver()
    }


    fun initUI() {
        binding.bar.ivBack.setOnClickListener { finish() }
        binding.bar.ivAdd.visibility = View.GONE
        binding.bar.tvContainerText.text = "用户登陆"
        binding.loginBtn.setOnClickListener {
            userViewModel.login(binding.userNameText.text.toString(), binding.loginPassword.text.toString())
        }
    }

    fun initObserver() {
        userViewModel.userLiveData.observe(this,
                Observer {
                    if (it == null) {
                        Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show()
                    } else {
                        finish()
                    }
                }
        )
    }

}