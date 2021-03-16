package com.mei.wood

import android.Manifest
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.shape.*
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.custom.chick.SplitText
import com.joker.im.message.CustomMessage
import com.joker.im.newMessages
import com.mei.chat.toImChat
import com.mei.faceunity.activity.FUBeautyActivity
import com.mei.im.ui.popup.CommonPopupWindow
import com.mei.mentor_home_page.ui.dialog.showMentorBrowser
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.permission.PermissionCheck
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.util.CalendarUtil
import com.net.network.chick.friends.WorkListRequest
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.TIMValueCallBack
import kotlinx.android.synthetic.main.for_test_layout.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/6.
 */
class ForTestMethodActivity : MeiCustomBarActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.for_test_layout)
        onClick()
    }


    fun onClick() {
        test_id.setOnClickListener {
            toImChat(edit_id.text.toString(), ob = ServiceInfo().apply {
                userId = 10000000
            })
        }
        test_course_list_activity.setOnClickListener {
            val data = ChickCustomData()
            val list: MutableList<SplitText> = ArrayList()
            list.add(SplitText("28567639 的主页   === (这个会上报服务器) ", "#FF0000", "dove://personal_page_info?userid=28567639", afterClickAction = "哈哈，测试的"))
            list.add(SplitText("   没有内链的文本   ", "", ""))
            list.add(SplitText("   #  去我的个人主页  #  (这个不会上报)", "#00FF00", "dove://personal_page_info?userid=28845632"))
            list.add(SplitText("没有内链的文本", "", ""))
            data.content = list
            val customMessage = CustomMessage(ChickCustomData.Result(CustomType.send_text, data))
            val conversation2 = TIMManager.getInstance().getConversation(TIMConversationType.C2C, edit_id.text.toString())
            conversation2.sendMessage(customMessage.timMessage, object : TIMValueCallBack<TIMMessage> {
                override fun onError(i: Int, s: String) {
                    Log.e("infoxxx", "onError: $i$s")
                }

                override fun onSuccess(timMessage: TIMMessage) {
                    Log.e("infoxxx", "onSuccess: ")
                    newMessages(arrayListOf(timMessage))
                }
            })
        }

        test_channel.setOnClickListener {
            apiSpiceMgr.executeKt(WorkListRequest(JohnUser.getSharedUser().userID, 0, 40), success = { result ->
                val list = result.data?.list
                showMentorBrowser(JohnUser.getSharedUser().userID, list, 0)
            })
        }
        test_im_single.setOnClickListener {
            startActivity(Intent(activity, FUBeautyActivity::class.java))
        }
        test_order.setOnClickListener {
//            val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "crash/"
//            println("path$path")
//            Nav.toAmanLink(this, "https://sssr.meidongli.net/zhixin/quick-consulting")
//            val str = "dsl萨拉肯德基拉刷卡缴费拉克丝发顺丰科技阿拉山口缴费奥斯卡垃圾分类卡视角发口令设计费埃里克森缴费刻录机爱丽丝咖啡机傲世狂妃骄傲隆盛科技"
//            autoSplitText(test_order,str)
        }
        test_work_room_info_edit.setOnClickListener {
//            WorkRoomInfoEditorActivityLauncher.startActivity(this)
//            ComplementBaseFragment.complementStep = 4
//            startActivity(UserInfoComplementActivityLauncher.getIntentFrom(this, 30960916, ""))
//            (activity as? MeiCustomBarActivity)?.showPeopleGiftBagDialog()
//            (activity as? MeiCustomBarActivity)?.showPayExtDialog()

        }

        test_shan_yan_bind_dialog.setOnClickListener {
            if (PermissionCheck.hasPermission(activity, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                    && CalendarUtil.isHasAppointment(activity, 114112)) {
                UIToast.toast("114112已经订阅")
            } else {
                requestMulPermissionZip(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR) {
                    //日历添加事件
                    val calendar = GregorianCalendar.getInstance()
                    val startTime = calendar.timeInMillis
                    calendar.add(Calendar.MINUTE, 30)
                    val endTime = calendar.timeInMillis
                    CalendarUtil.addCalendarEvent(this, "抢券！！！", "01:30:40开始抢券", 114112, 111, startTime, endTime)
                    UIToast.toast(this, "预约成功！开始时为你发送提醒")
                }
            }
        }
    }


}