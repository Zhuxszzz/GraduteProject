package com.niantch.graproject.viewmodel

import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niantch.graproject.model.UserModel
import com.niantch.graproject.utils.GlobalContextUtil
import org.litepal.crud.DataSupport

/**
 * author: niantchzhu
 * date: 2021
 */
class UserViewModel: ViewModel() {
    val userLiveData = MutableLiveData<UserModel?>()

    fun initUser() {
        val userList = DataSupport.findAll(UserModel::class.java)
        if (userList.isNullOrEmpty()) {
            userLiveData.value = null
        } else {
            userLiveData.value = userList[0]
        }
    }

    fun doOUserLogout() {
        DataSupport.deleteAll(UserModel::class.java)
        val editor = PreferenceManager.getDefaultSharedPreferences(GlobalContextUtil.globalContext!!).edit()
        editor.putInt("user_id", -1)
        editor.apply()
    }

}