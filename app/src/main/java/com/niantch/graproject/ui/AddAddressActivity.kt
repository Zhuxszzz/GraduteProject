package com.niantch.graproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.niantch.graproject.R
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.MapFragment
import io.reactivex.functions.Consumer
import java.util.logging.Logger

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
class AddAddressActivity: AppCompatActivity(R.layout.activity_add_address) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rxPermission = RxPermissions(this)
        rxPermission.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe(Consumer<Permission>() {
                    if (isPermitted()) {
                        // 已经同意该权限
                        initTencentLocationRequest();
                    } else {
                        // 拒绝了该权限
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                    }

            });
    }

    override fun onStart() {
        super.onStart()
        val transition = fragmentManager.beginTransaction()
        var frag = fragmentManager.findFragmentByTag(MAP_FRAGMENT) as MapFragment?
        if (frag == null) {
            frag = MapFragment.newInstance(this)
        }
        transition.replace(
            R.id.fl_map_container,
            frag,
            MAP_FRAGMENT
        )
        transition.commit()
    }

    fun initTencentLocationRequest() {
        val request = TencentLocationRequest.create().apply {
            this.interval = 30000
            this.requestLevel = 1
        }
        val locationManager = TencentLocationManager.getInstance(this);
        val error = locationManager.requestLocationUpdates(request, object : TencentLocationListener{
            override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
            }

            override fun onLocationChanged(p0: TencentLocation?, p1: Int, p2: String?) {
                if (p1 == TencentLocation.ERROR_OK) {
                    val pos = p0?.address
                    Log.e(TAG, pos)
                }
            }
        });
        if (error == 0)
            Log.i(TAG, "注册位置监听器成功！");
        else
            Log.i(TAG, "注册位置监听器失败！");
    }

    fun isPermitted(): Boolean {
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val TAG = "AddAddressActivity"
        const val ACTIVITY_CODE = 1
        const val MAP_FRAGMENT = "map_fragment"
    }
}