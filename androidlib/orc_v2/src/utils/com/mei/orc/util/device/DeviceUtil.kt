package com.mei.orc.util.device

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.mei.orc.Cxt
import com.mei.orc.util.encrypt.MD5
import com.mei.orc.util.permission.PermissionCheck
import kotlin.math.abs

/**
 * @author Steven
 */


val _phone_sn: String?
    @SuppressLint("HardwareIds", "MissingPermission")
    get() {
        return try {
            val imei = imei
            if (is_valid_imei(imei)) {
                imei
            } else pseudo_phone_sn
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

    }


private val imei: String
    @SuppressLint("MissingPermission", "HardwareIds")
    get() {
        return try {
            val tm = Cxt.get().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var imei = ""
            if (PermissionCheck.hasPermission(Cxt.get(), android.Manifest.permission.READ_PHONE_STATE)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    @Suppress("DEPRECATION")
                    imei = tm.deviceId
                } else {
                    // TODO: zzw 2019-07-03 有了好的Q适配方案需要添加代码
                }
            }
            imei
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

    }

@Suppress("DEPRECATION")
private val pseudo_phone_sn: String
    @SuppressLint("HardwareIds")
    get() {
        val builder = Settings.Secure.getString(Cxt.get().contentResolver, Settings.Secure.ANDROID_ID) + "|" +
                Build.BRAND + "|" +
                Build.MANUFACTURER + "|" +
                Build.DEVICE + "|" +
                Build.SERIAL + "|"
        return MD5.hexdigest(builder)
    }

private fun is_valid_imei(imei: String): Boolean {
    if (imei.isEmpty()) {
        return false
    }
    return try {
        val valueLong = java.lang.Long.parseLong(imei)
        abs(valueLong) > 1000000
    } catch (e: NumberFormatException) {
        false
    }
}


