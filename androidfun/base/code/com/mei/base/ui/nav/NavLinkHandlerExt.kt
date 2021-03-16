package com.mei.base.ui.nav

import androidx.fragment.app.FragmentActivity
import com.joker.TdType
import com.mei.base.common.CLOSE_LOADING
import com.mei.orc.event.postAction
import com.mei.wood.R
import com.mei.wood.dialog.share.ShareCmd
import com.mei.wood.dialog.share.ShareData
import com.mei.wood.dialog.share.ShareManager
import com.mei.wood.dialog.share.ShareWeb
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020-01-07
 */

/**
 * 分享
 */
fun FragmentActivity.shareFromWeb(url: String) {
    try {
        val shareWeb = MeiUtil.getJsonObject<ShareWeb>(url, AmanLink.URL.SHARE_FROM_WEB)
        //分享的类型：无/"default":之前普通分享；"long_image":加有长图分享；"only_image":分享单独的图片；"only_long":仅长图分享
        //当前2.0.0版本只加入了default和only_image的分类，其他是为了以后方便就顺手加上的
        /** by joker  这里分享截取webview的长图是无用 的，没有在 setcontentView之前加 enableSlowWholeDocumentDraw() */
        shareWeb?.let { info ->
            postAction(CLOSE_LOADING)
            val codeInfo = if (info.shareReferId.orEmpty().isNotEmpty()) ShareCmd(info.shareReferType, info.shareReferId) else null
            val shareData = if (info.shareType.orEmpty().isNotEmpty() && "only_image" == info.shareType) {
                ShareData("", "", info.imageUrl, "", "", R.mipmap.ic_launcher, false, TdType.image_share)
            } else if (info.miniProgramPath.orEmpty().isNotEmpty()) {
                ShareData(
                        info.title, info.content, info.imageUrl, info.shareUrl, info.regularUrl,
                        type = TdType.mini_program_share, miniProgramPath = info.miniProgramPath, codeCmd = codeInfo
                )
            } else {
                ShareData(info.title, info.content, info.imageUrl, info.shareUrl, info.regularUrl, codeCmd = codeInfo)
            }
            ShareManager.instance.show(this, shareData)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun FragmentActivity.shareTargetWeb(url: String) {
    try {
        MeiUtil.getJsonObject<ShareWeb>(url, AmanLink.URL.SHARE_TARGET_WEB)?.let { info ->
            postAction(CLOSE_LOADING)
            val codeInfo = if (info.shareReferId.orEmpty().isNotEmpty()) ShareCmd(info.shareReferType, info.shareReferId) else null
            val shareData = if (info.shareType.orEmpty().isNotEmpty() && "only_image" == info.shareType) {
                ShareData("", "", info.imageUrl, "", "", R.mipmap.ic_launcher, false, TdType.image_share)
            } else if (info.miniProgramPath.orEmpty().isNotEmpty()) {
                ShareData(
                        info.title, info.content, info.imageUrl, info.shareUrl, info.regularUrl,
                        type = TdType.mini_program_share, miniProgramPath = info.miniProgramPath, codeCmd = codeInfo
                )
            } else {
                ShareData(info.title, info.content, info.imageUrl, info.shareUrl, info.regularUrl, codeCmd = codeInfo)
            }
            ShareManager.instance.handleShareChoice(this, info.shareClient, shareData)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
