package com.mei.wood.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import org.json.JSONObject


/**
 *  Created by zzw on 2019-10-15
 *  Des:
 */
@SuppressLint("ServiceCast")
class NativeLocationImpl(context: Context) : LocationImpl(context) {

    private val TAG = "LocationImpl"
    private val locationManager: LocationManager by lazy {
        mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private var locationListener: LocationRequestListener? = null

    @SuppressLint("MissingPermission")
    override fun start(success: (LocationBean) -> Unit, failure: (Int, String) -> Unit) {
        //2.获取位置提供器，GPS或是NetWork
        val providers = locationManager.getProviders(true) ?: return

        val locationProvider: String = when {
//            providers.contains(LocationManager.GPS_PROVIDER) -> {
//                //如果是网络定位
//                Log.d(TAG, "GPS定位")
//                LocationManager.GPS_PROVIDER
//            }
            providers.contains(LocationManager.NETWORK_PROVIDER) -> {
                //如果是网络定位
                Log.d(TAG, "网络定位")
                LocationManager.NETWORK_PROVIDER
            }
            else -> {
                Log.d(TAG, "没有可用的位置提供器")
                ""
            }
        }
        if (locationProvider.isEmpty()) {
            failure(-1, "没有开启定位权限或者手机不支持定位")
            return
        }

        //获取上次的位置，一般第一次运行，此值为null
//        Location location = locationManager.getLastKnownLocation(locationProvider);
//        if (location != null) {
//            notifyListener(location);
//        }
        stop()

        // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
        LocationRequestListener(success = { locationBean ->
            com.mei.orc.util.http.requestGet("http://api.map.baidu.com/geocoder?output=json&location=${locationBean.latitude},${locationBean.longitude}",
                    success = {
                        Log.d(TAG, it)
                        if (it.isNotEmpty()) {
                            try {
                                val addressComponentObject = JSONObject(it)
                                        .getJSONObject("result")
                                        .getJSONObject("addressComponent")

                                locationBean.city = addressComponentObject.getString("city")
                                locationBean.province = addressComponentObject.getString("province")

                                success(locationBean)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                failure(-1, "get url result to json error")
                            }
                        } else {
                            failure(-1, "get url result is null")
                        }
                    }
            )
            stop()
        }, failure = { code, msg ->
            failure(code, msg)
            stop()
        }).apply {
            locationListener = this
            locationManager.requestLocationUpdates(locationProvider, 0L, 0f, this)
        }

    }


    override fun stop() {
        try {
            locationListener?.let {
                locationManager.removeUpdates(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        locationListener = null
    }


    class LocationRequestListener(val success: (LocationBean) -> Unit, val failure: (Int, String) -> Unit) : LocationListener {
        private val TAG = "LocationImpl"

        /**
         * 手机位置发生变动
         */
        override fun onLocationChanged(location: Location?) {
            Log.d(TAG, "onLocationChanged")
            location?.accuracy//精确度
            if (location == null) {
                failure(-1, "location is null")
            } else {
                success(location2LocationBen(location))
            }
        }

        /**
         * 当某个位置提供者的状态发生改变时
         */
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.d(TAG, "onStatusChanged")
        }

        /**
         * 某个设备打开时
         */
        override fun onProviderEnabled(provider: String?) {
            Log.d(TAG, "onProviderEnabled")
        }

        /**
         * 某个设备关闭时
         */
        override fun onProviderDisabled(provider: String?) {
            Log.d(TAG, "onProviderDisabled")
        }

        private fun location2LocationBen(location: Location): LocationBean {
            val bean = LocationBean()
            bean.latitude = location.latitude
            bean.longitude = location.longitude
            Log.d(TAG, bean.toString())
            return bean
        }
    }
}