package com.niantch.graproject.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.niantch.graproject.R
import com.niantch.graproject.databinding.ActivityLoginBinding

/**
 * author: niantchzhu
 * date: 2021
 */
class LoginActivity: AppCompatActivity(R.layout.activity_login) {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    internal class LoginTextWatcher : TextWatcher {
        var editText: EditText? = null
        var lastContentLength = 0
        var isDelete = false
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //隐藏显示登陆按钮
            if (loginByPasswordLl.getVisibility() === View.VISIBLE) {
                if (!(userName.getText().toString() == "" || password.getText().toString() == "")) {
                    loginBtn.setEnabled(true)
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.login_selected_bg))
                }
            } else {
                if (!(loginPhoneEt.getText().toString() == "" || loginIdentifyCodeEt.getText().toString() == "")) {
                    loginBtn.setEnabled(true)
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.login_selected_bg))
                }
            }
            /**
             *
             * 手机号分隔处理
             *
             */
            if (loginByPasswordLl.getVisibility() === View.VISIBLE) {
                editText = userName
            } else {
                editText = loginPhoneEt
            }
            val sb = StringBuffer(editText!!.text.toString())
            //是否为输入状态
            isDelete = if (editText!!.text.toString().length > lastContentLength) false else true

            //输入是第4，第9位，这时需要插入空格
            if (!isDelete && (editText!!.text.toString().length == 4 || editText!!.text.toString().length == 9)) {
                if (editText!!.text.toString().length == 4) {
                    sb.insert(3, " ")
                } else {
                    sb.insert(8, " ")
                }
                setContent(sb)
            }

            //删除的位置到4，9时，剔除空格
            if (isDelete && (editText!!.text.toString().length == 4 || editText!!.text.toString().length == 9)) {
                sb.deleteCharAt(sb.length - 1)
                setContent(sb)
            }
            lastContentLength = sb.length
        }

        /**
         * 添加或删除空格EditText的设置
         *
         * @param sb
         */
        private fun setContent(sb: StringBuffer) {
            editText!!.setText(sb.toString())
            //移动光标到最后面
            editText!!.setSelection(sb.length)
        }

        override fun afterTextChanged(s: Editable) {
            if (s.toString() == "") {
                loginBtn.setEnabled(false)
                loginBtn.setBackground(getResources().getDrawable(R.drawable.login_bg))
            }
            if (loginByIdentifyCodeLl.getVisibility() === View.VISIBLE) {
                if (loginPhoneEt.getText().toString().length == 13 && loginGetIdentifyCodeTv.getText().toString() == "获取验证码") {
                    loginGetIdentifyCodeTv.setTextColor(getResources().getColor(R.color.bottom_tab_text_selected_color))
                    loginGetIdentifyCodeTv.setEnabled(true)
                } else {
                    loginGetIdentifyCodeTv.setEnabled(false)
                    loginGetIdentifyCodeTv.setTextColor(getResources().getColor(R.color.text_color_grey))
                }
            }
        }
    }


    private fun clearEditText(et: EditText) {
        et.setText("")
        et.isFocusable = true
        et.isFocusableInTouchMode = true
        et.requestFocus()
    }

}