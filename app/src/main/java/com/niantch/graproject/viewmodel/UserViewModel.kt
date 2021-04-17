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
        val user = UserModel(0,1, "Niantch", "https://ftp.bmp.ovh/imgs/2021/04/2637c47b0071dcd7.jpeg","13545667562","112233", 1)
//        val userList = DataSupport.findAll(UserModel::class.java)
//        if (userList.isNullOrEmpty()) {
//            userLiveData.value = null
//        } else {
//            userLiveData.value = userList[0]
//        }
        user.save()
        userLiveData.postValue(user)
    }

    fun doOUserLogout() {
        DataSupport.deleteAll(UserModel::class.java)
        val editor = PreferenceManager.getDefaultSharedPreferences(GlobalContextUtil.globalContext!!).edit()
        editor.putInt("user_id", -1)
        editor.apply()
    }

}