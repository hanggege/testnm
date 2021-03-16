package com.mei.wood.location

/**
 * Created by zzw on 2019-10-15
 * Des:
 */
interface ILocation {
    fun start(success: (LocationBean) -> Unit = {}, failure: (Int, String) -> Unit = { _, _ -> })
    fun stop()
}


class LocationBean {
    //纬度
    var latitude: Double = 0.toDouble()

    //经度
    var longitude: Double = 0.toDouble()

    //地址
    var addrss: String = ""

    //国家
    var country: String = ""

    //省
    var province: String = ""

    //城市
    var city: String = ""

    //城区
    var district: String = ""

    //街道
    var street: String = ""

    //街道门牌号信息
    var streetNum: String = ""

    //城市编码
    var cityCode: String = ""

    //地区编码
    var adCode: String = ""

    //定位时间
    var time: Long = 0

    //位置描述信息
    var locationDescribe: String = ""

    override fun toString(): String {
        return "LocationBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", addrss='" + addrss + '\''.toString() +
                ", country='" + country + '\''.toString() +
                ", province='" + province + '\''.toString() +
                ", city='" + city + '\''.toString() +
                ", district='" + district + '\''.toString() +
                ", street='" + street + '\''.toString() +
                ", streetNum='" + streetNum + '\''.toString() +
                ", cityCode='" + cityCode + '\''.toString() +
                ", adCode='" + adCode + '\''.toString() +
                ", time=" + time +
                ", locationDescribe='" + locationDescribe + '\''.toString() +
                '}'.toString()
    }
}