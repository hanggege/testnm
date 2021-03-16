package com.mei.message.wiget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData
import kotlinx.android.synthetic.main.include_conversation_list_menu_header.view.*

/**
 *
 * @author Created by lenna on 2020/6/15
 */
class MentorMessageHeaderView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : LinearLayout(context, attributeSet, def) {
    init {
        layoutInflaterKtToParentAttach(R.layout.include_conversation_list_menu_header)
        my_fans_itv.setOnClickListener {

//            if (!JohnUser.getSharedUser().hasLogin()) {
//                (context as? FragmentActivity)?.toLogin()
//            } else {
//                context.startActivity(Intent(context, MyFansListActivity::class.java))
//            }

            toWeb("fans")
        }
        connect_user_itv.setOnClickListener {

            toWeb("connection")

        }
        service_itv.setOnClickListener {
            toWeb("exclusive")
        }
        isVisible = false
    }

    private fun toWeb(type: String) {
        MeiWebActivityLauncher.startActivity(context, MeiWebData(MeiUtil.appendParamsUrl(AmanLink.NetUrl.user_record, "type" to type), 0).apply {
            need_reload_web = 1
        })
    }

    fun loadState(isPublisher: Boolean?) {
        isVisible = isPublisher == true
    }


}