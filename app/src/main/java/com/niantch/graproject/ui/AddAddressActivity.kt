package com.niantch.graproject.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.niantch.graproject.R
import com.niantch.graproject.adapter.AddAddressAdapter
import com.niantch.graproject.databinding.ActivityAddAddressBinding
import com.niantch.graproject.model.AddressModel
import com.niantch.graproject.utils.DataUtil
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.`object`.param.Geo2AddressParam
import com.tencent.lbssearch.`object`.result.Geo2AddressResultObject
import com.tencent.lbssearch.httpresponse.BaseObject
import com.tencent.lbssearch.httpresponse.HttpResponseListener
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.LocationSource
import com.tencent.tencentmap.mapsdk.maps.MapView
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.Marker
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions
import io.reactivex.functions.Consumer


/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
class AddAddressActivity : AppCompatActivity(R.layout.activity_add_address), LocationSource,
        TencentLocationListener {
    lateinit var binding: ActivityAddAddressBinding
    lateinit var mapView: MapView
    lateinit var map: TencentMap
    private var myMarker: Marker? = null
    private var locationManager: TencentLocationManager? = null
    private var locationRequest: TencentLocationRequest? = null
    private var locationChangedListener: LocationSource.OnLocationChangedListener? = null
    private var adapterAdd: AddAddressAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

                })
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        initUI()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    fun initUI() {
        adapterAdd = AddAddressAdapter()
        binding.ivUserFragmentBack.setOnClickListener { finish() }
        binding.txAddressConfirm.setOnClickListener {
            val intent = Intent()
            val addressModel = AddressModel().apply {
                user_id = DataUtil.getCurrentUser()!!.userId
                name = binding.etName.text.toString()
                phone = binding.etPhone.text.toString()
                address = adapterAdd?.getAddress()
            }
            addressModel.save()
            intent.putExtra("new_address", addressModel)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.rvLocationList.adapter = adapterAdd
        binding.rvLocationList.layoutManager = LinearLayoutManager(applicationContext)
    }

    fun initTencentLocationRequest() {
        mapView = MapView(this)
        binding.flMapContainer.addView(mapView)
        map = mapView.map
        locationManager = TencentLocationManager.getInstance(this);
        //创建定位请求
        locationRequest = TencentLocationRequest.create();
        //设置定位周期（位置监听器回调周期）为3s
        locationRequest?.interval = 3000
        locationRequest?.requestLevel = TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA
        locationRequest?.isAllowGPS = true
        map.isMyLocationEnabled = true
        map.setLocationSource(this)
        map.setOnMapClickListener {
            myMarker?.remove()
            myMarker = map.addMarker(MarkerOptions(it))
            refreshList(it)
        }
    }

    fun refreshList(latLng: LatLng) {
        val param = Geo2AddressParam(latLng).getPoi(true).setPoiOptions(
                Geo2AddressParam.PoiOptions().setRadius(200)
        )
        val tencentSearch = TencentSearch(applicationContext)
        tencentSearch.geo2address(param, object : HttpResponseListener<BaseObject> {


            override fun onFailure(arg0: Int, arg1: String, arg2: Throwable?) {
                Log.e("test", "error code:$arg0, msg:$arg1");
            }

            override fun onSuccess(p0: Int, p1: BaseObject?) {
                if(p0 == null) {
                    return
                }
                val poiList = mutableListOf<String>()
                val obj = p1 as Geo2AddressResultObject
                val sb = StringBuilder()
                sb.append("逆地址解析")
                sb.append("\n地址：" + obj.result.address)
                sb.append("\npois:")
                for(poi in obj.result.pois) {
                    sb.append("\n\t" + poi.title)
                    poiList.add(poi.title)
                }
                Log.e(TAG, sb.toString())
                adapterAdd?.refreshDataSet(poiList)
            }
        })
    }

    private fun isPermitted(): Boolean {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
    }

    override fun onLocationChanged(
            tencentLocation: TencentLocation,
            i: Int,
            s: String?
    ) {
        //其中 locationChangeListener 为 LocationSource.active 返回给用户的位置监听器
        //用户通过这个监听器就可以设置地图的定位点位置
        if (i == TencentLocation.ERROR_OK && locationChangedListener != null) {
            val location = Location(tencentLocation.provider)
            //设置经纬度
            location.latitude = tencentLocation.latitude
            location.longitude = tencentLocation.longitude
            //设置精度，这个值会被设置为定位点上表示精度的圆形半径
            location.accuracy = tencentLocation.accuracy
            //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
            location.bearing = tencentLocation.bearing
            //将位置信息返回给地图
            locationChangedListener!!.onLocationChanged(location)
            Log.e(TAG, tencentLocation.address)
            refreshList(LatLng(location))
        }
    }


    override fun deactivate() {
        //当不需要展示定位点时，需要停止定位并释放相关资源
        locationManager?.removeUpdates(this)
        locationManager = null
        locationRequest = null
    }

    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        //这里我们将地图返回的位置监听保存为当前 Activity 的成员变量
        locationChangedListener = p0
        //开启定位
        val err = locationManager!!.requestSingleFreshLocation(
                null, this, Looper.myLooper()
        )
        when (err) {
            1 -> Toast.makeText(
                    this,
                    "设备缺少使用腾讯定位服务需要的基本条件",
                    Toast.LENGTH_SHORT
            ).show()
            2 -> Toast.makeText(
                    this,
                    "manifest 中配置的 key 不正确", Toast.LENGTH_SHORT
            ).show()
            3 -> Toast.makeText(
                    this,
                    "自动加载libtencentloc.so失败", Toast.LENGTH_SHORT
            ).show()
            else -> {
            }
        }
    }


    companion object {
        const val TAG = "AddAddressActivity"
        const val ACTIVITY_CODE = 1
        const val MAP_FRAGMENT = "map_fragment"
    }

}