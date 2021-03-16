package com.mei.wechat

import androidx.fragment.app.FragmentActivity
import com.mei.base.network.netchange.checkNetAndLogin
import com.mei.wechat.dialog.WechatDialogNew

/**
 *
 * @author Created by Ling on 2019-07-17
 */
data class MainAppGNData(val main_app_gn_pro: String? = "", val main_app_gn_cate: String? = "", val main_app_gn_type: String? = "",
                         val main_app_gn_id: String? = "", val main_app_gn_content: String? = "", val main_app_gn_mentor_id: String? = "")

/**
 * 添加微信
 **/
@JvmOverloads
fun FragmentActivity.addWechatNumber(mentor_id: Int, from_type: String, from_id: String, mainAppGNData: MainAppGNData? = null) {
    checkNetAndLogin(back = {
        if (it) {
            WechatDialogNew().showWXNumber(this, mentor_id, from_type, from_id, mainAppGNData)
        }
    })
}

//
///**
// * 添加导师微信号码
// * @param wechatJson //微信获取的数据，一般在点击按钮时回调
// * @param follow //是否关注成功
// */
//@JvmOverloads
//fun FragmentActivity.addWechatNumber(data: WechatData, wechatJson:(WeChat_Number_Response?) -> Unit = {}, follow:(Boolean) -> Unit = {},mainAppGNData: MainAppGNData? = null){
//    checkNetAndLogin(back ={
//        if(it){
//            WechatDialogNewLauncher.newInstance().showWXNumber(this, data, object : WechatListener<WeChat_Number_Response?>{
//                override fun onBackWechatJasn(t: WeChat_Number_Response?) {
//                    wechatJson(t)
//                }
//
//                override fun onFollow(b: Boolean) {
//                    follow(b)
//                }
//            },mainAppGNData)
//        }
//    })
//}
//
///**
// * 添加微信公众号
// **/
//fun FragmentActivity.addWechatNumber(info: WechatJson){
//    checkNetAndLogin(back ={
//        if(it){
//            WechatDialogNewLauncher.newInstance().showWXAccount(this, info)
//        }
//    })
//}
//
///**
// * 复制微信
// **/
//@JvmOverloads
//fun FragmentActivity.copyWechatNumber(mentor_id: Int, from_type: String, from_id: String, wechatJson:(WeChat_Number_Response?) -> Unit = {}, follow:(Boolean) -> Unit = {}){
//    checkNetAndLogin(back ={
//        if(it){
//            WechatCopyDialogLauncher.newInstance().showWXNumber(this, mentor_id, from_type, from_id, object : WechatListener<WeChat_Number_Response?> {
//                override fun onBackWechatJasn(t: WeChat_Number_Response?) {
//                    wechatJson(t)
//                }
//
//                override fun onFollow(b: Boolean) {
//                    follow(b)
//                }
//            })
//        }
//    })
//
//}
//
///**
// *  关注导师后的添加微信弹框
// **/
//@JvmOverloads
//fun FragmentActivity.followAddWechat(wechatJson: FollowGetWechatJson, callback:(FollowGetWechatJson?) -> Unit = {},mainAppGNData: MainAppGNData? = null){
//    checkNetAndLogin(back ={
//        if(it){
//            FollowGetWechatDialogLauncher.newInstance().showFollowed(this, wechatJson, Callback {
//                callback(it)
//            },mainAppGNData)
//        }
//    })
//
//}
//
///**
// * 微信广告位导流弹框
// */
//fun FragmentActivity.freeAddWechat(wechatFree: WechatFree){
//    checkNetAndLogin(back ={
//        if(it){
//            WechatFreeDialogLauncher.newInstance().show(this, wechatFree)
//        }
//    })
//}
