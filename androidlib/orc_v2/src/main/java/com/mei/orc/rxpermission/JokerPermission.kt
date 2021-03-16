package com.mei.orc.rxpermission

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/10/29
 */


/**
 * 批量请求权限
 * back: (permission,granted,shouldShowRequestPermissionRationale)
 * (权限名，是否授权，下次是否能继续弹框请求)
 */
fun FragmentActivity.requestMulPermission(vararg permissions: String, back: (List<Triple<String, Boolean, Boolean>>) -> Unit = {}) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val list = it.map { map ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Triple(map.key, map.value, shouldShowRequestPermissionRationale(map.key))
            } else {
                Triple(map.key, map.value, false)
            }
        }
        back(list)
    }.launch(permissions)
}

fun FragmentActivity.requestMulPermissionZip(vararg permissions: String, hasAll: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        hasAll(it.filterNot { it.value }.isEmpty())
    }.launch(permissions)
}

fun FragmentActivity.requestSinglePermission(permission: String, back: (Pair<Boolean, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            back(Pair(it, shouldShowRequestPermissionRationale(permission)))
        } else {
            back(Pair(it, false))
        }
    }.launch(permission)
}