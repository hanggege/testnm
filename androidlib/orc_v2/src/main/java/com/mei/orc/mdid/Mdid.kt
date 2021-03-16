package com.mei.orc.mdid

import android.content.Context
import android.util.Log
import com.bun.miitmdid.core.ErrorCode
import com.bun.miitmdid.core.JLibrary
import com.bun.miitmdid.core.MdidSdkHelper
import com.bun.supplier.IIdentifierListener
import com.bun.supplier.IdSupplier
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.putValue

/**
 *  Created by zzw on 2019-10-16
 *  Des:     androidQ以上imei替代方案
 *           mdid官方链接:http://www.msa-alliance.cn/col.jsp?id=120
 */


private var mdid: String = ""
private val MDID_ID_KEY: String = "MDID_ID_KEY"

fun initMdid(context: Context) {
    mdid = MDID_ID_KEY.getNonValue("")
    if (mdid.isEmpty()) {
        @Suppress("INACCESSIBLE_TYPE")
        JLibrary.InitEntry(context)
    }
}


fun getMdid(context: Context, back: (String) -> Unit = {}) {
    if (mdid.isNotEmpty()) {
        back(mdid)
        return
    }
    val code = callFromReflect(context, back = {
        it?.apply {
            mdid = this
            MDID_ID_KEY.putValue(this)
            back(this)
        }
    })
    Log.d("Mdid", "callFromReflect code = $code")

    when (code) {
        ErrorCode.INIT_ERROR_BEGIN -> {//错误
            Log.d("Mdid", "错误")
        }
        ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT -> {//不支持的设备
            Log.d("Mdid", "不支持的设备")
        }
        ErrorCode.INIT_ERROR_LOAD_CONFIGFILE -> {//加载配置文件出错
            Log.d("Mdid", "加载配置文件出错")
        }
        ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT -> {//不支持的设备厂商
            Log.d("Mdid", "不支持的设备厂商")
        }
        ErrorCode.INIT_ERROR_RESULT_DELAY -> {//获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
            Log.d("Mdid", "获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程")
        }
        ErrorCode.INIT_HELPER_CALL_ERROR -> {//反射调用出错
            Log.d("Mdid", "反射调用出错")
        }

    }
}

/*
    * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
    *
    * */
private fun callFromReflect(cxt: Context, back: (String?) -> Unit = {}): Int {
    return MdidSdkHelper.InitSdk(cxt, true, object : IIdentifierListener {
        override fun OnSupport(isSupport: Boolean, idSupplier: IdSupplier?) {
            if (idSupplier == null || !isSupport) {
                back(null)
                return
            }

//            OAID: 51d680a6be35102d
//            VAID: 76a01d6bfaac3684
//            AAID: a561600a-bc08-4e3e-9f11-5b572a36e859

            val oaid = idSupplier.oaid
            val vaid = idSupplier.vaid
            val aaid = idSupplier.aaid
//            val udid = idSupplier.udid
//            val builder = StringBuilder()
//            builder.append("support: ").append(if (isSupport) "true" else "false").append("\n")
//            builder.append("UDID: ").append(udid).append("\n")
//            builder.append("OAID: ").append(oaid).append("\n")
//            builder.append("VAID: ").append(vaid).append("\n")
//            builder.append("AAID: ").append(aaid).append("\n")
//            val idstext = builder.toString()
//            Log.d("Mdid", idstext)
//            idSupplier.shutDown()

            when {
                !oaid.isNullOrEmpty() -> back(oaid)
                !vaid.isNullOrEmpty() -> back(vaid)
                else -> back(aaid)
            }
        }
    })
}