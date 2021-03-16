package com.mei.wood.dialog.share

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentActivity
import com.mei.base.manager.isWoodApp
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.ext.addSelfIf
import com.mei.wood.R
import kotlin.properties.Delegates

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/29
 */


interface ShareBack {
    fun handle(@ShowFlag type: Int)
}


const val TYPE_ITEM_1 = 1111//微信，朋友圈，微博，IM
const val TYPE_ITEM_2 = 2222//微信，朋友圈，微博，IM，复制链接

/**  只分享图片时不显示复制链接，直达口令 **/
const val TYPE_ITEM_3 = 3333//长图，微信，朋友圈，微博，IM
const val TYPE_ITEM_4 = 4444//微信，朋友圈，微博，IM，直达口令，复制链接
const val TYPE_ITEM_5 = 5555//微信，直达口令

const val TYPE_ITEM_6 = 6666//微信，朋友圈，复制链接

const val TYPE_ITEM_7 = 7777//微信，朋友圈，微博，复制链接

const val TYPE_ITEM_8 = 8888//微信，朋友圈

class ShareKtDialog : MeiSupportDialogFragment(), ShareViewBack {


    private val listItem = arrayListOf<ShowItem>()
    private var shareBack: ShareBack by Delegates.notNull()

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val params = it.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.BOTTOM
            it.attributes = params
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setWindowAnimations(R.style.animate_push_dialog)
        val at = activity
        return when {
            at == null -> null
            listItem.size > 5 -> ShareGridView(at)
            else -> ShareLineView(at)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (view is ShareGridView) {
            view.refresh(listItem, this)
        } else if (view is ShareLineView) {
            view.refresh(listItem, this)
        }
    }


    override fun handle(@ShowFlag type: Int) {
        shareBack.handle(type)
        dismissAllowingStateLoss()
    }

    override fun cancel() {
        dismissAllowingStateLoss()
    }

    fun showDialog(activity: FragmentActivity?, type: Int, back: (Int) -> Unit) {
        showDialog(activity, type, object : ShareBack {
            override fun handle(type: Int) {
                back(type)
            }
        })
    }

    fun showDialog(activity: FragmentActivity?, type: Int, back: ShareBack) {
        activity?.let {
            shareBack = back
            listItem.clear()
            when (type) {
                TYPE_ITEM_1 -> {
                    listItem.addAll(initShowList())
                }
                TYPE_ITEM_2 -> {
                    listItem.addAll(initShowList())
                    listItem.add(ShowItem(copy, R.drawable.share_copy_link_icon, "复制链接"))
                }
                TYPE_ITEM_3 -> {
                    listItem.add(ShowItem(long_image, R.drawable.share_long_img_icon, "长图分享"))
                    listItem.addAll(initShowList())
                }
                TYPE_ITEM_4 -> {
                    listItem.addAll(initShowList())
                    listItem.add(ShowItem(copy, R.drawable.share_copy_link_icon, "复制链接"))
                }
                TYPE_ITEM_5 -> {
                    listItem.add(ShowItem(wechat, R.drawable.share_wechat_icon, "微信"))
                }
                TYPE_ITEM_6 -> {
                    listItem.add(ShowItem(wechat, R.drawable.share_wechat_icon, "微信"))
                    listItem.add(ShowItem(wechat_circle, R.drawable.share_friends_icon, "朋友圈"))
                    listItem.add(ShowItem(copy, R.drawable.share_copy_link_icon, "复制链接"))
                }
                TYPE_ITEM_7 -> {
                    listItem.add(ShowItem(wechat, R.drawable.share_wechat_icon, "微信"))
                    listItem.add(ShowItem(wechat_circle, R.drawable.share_friends_icon, "朋友圈"))
                    listItem.add(ShowItem(weibo, R.drawable.share_weibo_icon, "微博"))
                    listItem.add(ShowItem(copy, R.drawable.share_copy_link_icon, "复制链接"))
                }
                TYPE_ITEM_8 -> {
                    listItem.add(ShowItem(wechat, R.drawable.share_wechat_icon, "微信"))
                    listItem.add(ShowItem(wechat_circle, R.drawable.share_friends_icon, "朋友圈"))
                }
            }
            show(activity.supportFragmentManager, ShareKtDialog::class.java.simpleName)
        }
    }


}

fun initShowList(): List<ShowItem> {
    val list = arrayListOf<ShowItem>()
    list.add(ShowItem(wechat, R.drawable.share_wechat_icon, "微信"))
    list.add(ShowItem(wechat_circle, R.drawable.share_friends_icon, "朋友圈"))
    list.addSelfIf(isWoodApp(), ShowItem(weibo, R.drawable.share_weibo_icon, "微博"))
    return list
}