package com.mei.mentor_home_page.adapter.item

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.CourseInfo
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.custom.chick.SplitText
import com.mei.GrowingUtil
import com.mei.base.util.shadow.setListShadowDefault
import com.mei.chat.toImChat
import com.mei.course_service.ui.MeiWebCourseServiceActivityLauncher
import com.mei.live.LiveEnterType
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.orc.Cxt
import com.mei.orc.ext.dp
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.json.toJson
import com.mei.widget.gradient.GradientLinearLayout
import com.mei.widget.gradient.applyViewDelegate
import com.mei.widget.holder.activity
import com.mei.widget.holder.setVisibleAndGone
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.recommend.BatRoomStatusRequest

/**
 *
 * @author Created by lenna on 2020/5/21
 * 服务课程item
 */
class MentorHomePageServiceListHolder(var view: View) : MentorBaseHolder(view) {
    var itemData: Any? = null
    override fun refresh(item: Any) {
        itemData = item
        view.apply { setListShadowDefault(this) }
        val askView = getView<GradientLinearLayout>(R.id.mentor_home_page_ask_rll)
        if (item is ServiceInfo) {
            item.apply {
                setText(R.id.mentor_home_page_service_name_tv, serviceName)

                setVisibleAndGone(R.id.history_service_cl, priceText?.isNotEmpty() != true)
                setVisibleAndGone(R.id.service_detail, priceText?.isNotEmpty() == true)
                if (priceText?.isNotEmpty() != true) {
                    setText(R.id.mentor_home_page_service_price_tv, "$serviceCost")

                    val hint = String.format(view.resources.getString(R.string.specification_text), serviceMinutes)
                    val count = String.format(view.resources.getString(R.string.service_count_text), serviceMin)
                    setText(R.id.mentor_home_page_specification_tv, hint)
                    setText(R.id.mentor_home_page_service_count_tv, count)
                    setVisibleAndGone(R.id.mentor_home_page_service_count_tv, serviceMin > 1)
                } else {
                    setText(R.id.service_detail, priceText.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333)))

                }
                askView.apply {
                    //主播自己和非主播才能看见询问按钮
                    //主播端自己看自己是没有推荐的，因为个人主页入口很多，如果主播没在直播中就根本不知道推荐给谁，鑫姐说的
                    isVisible = publisherId != JohnUser.getSharedUser().userID && MeiUser.getSharedUser().info?.isPublisher == false
                    setOnClickNotDoubleListener {
                        askClick(publisherId)
                    }
                    delegate.applyViewDelegate {
                        startColor = ContextCompat.getColor(context, R.color.color_FF8484)
                        endColor = ContextCompat.getColor(context, R.color.color_FF4030)
                        radius = 20.dp
                    }
                }
                setTextColor(R.id.mentor_home_page_options_tv, Color.WHITE)
            }
        } else if (item is CourseInfo) {
            item.apply {
                val hint = String.format(view.resources.getString(R.string.course_service_text), audioCount)
                setText(R.id.mentor_home_page_service_name_tv, courseName)
                setText(R.id.mentor_home_page_service_price_tv, cost.toString())
                setText(R.id.mentor_home_page_specification_tv, hint)
                setGone(R.id.mentor_home_page_service_count_tv, true)
                //主播自己和非主播才能看见询问按钮
                //主播端自己看自己是没有推荐的，因为个人主页入口很多，如果主播没在直播中就根本不知道推荐给谁，鑫姐说的
                askView.isVisible = userId != JohnUser.getSharedUser().userID
                askView.setOnClickNotDoubleListener {
                    askClick(userId)
                }
                val originalPrice = if (crossedPrice != 0) "$crossedPrice" else ""
                setVisible(R.id.mentor_home_page_original_price, crossedPrice != 0)
                if (originalPrice.isNotEmpty()) {
                    val ss = SpannableString(originalPrice)
                    ss.setSpan(StrikethroughSpan(), 0, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    getView<TextView>(R.id.mentor_home_page_original_price).text = ss
                }
                //免费领取的和已购买的按钮都是立即去听
                if (hasBuy || hasReceive) {
                    setText(R.id.mentor_home_page_options_tv, "立即去听")
                    getView<ImageView>(R.id.mentor_home_page_options_iv).setImageResource(R.drawable.icon_start_paly)
                    askView.delegate.applyViewDelegate {
                        strokeWidth = 1.dp
                        strokeStartColor = ContextCompat.getColor(context, R.color.color_FE3F3F)
                        startColor = ContextCompat.getColor(context, R.color.colorWhite)
                        radius = 20.dp
                    }
                    setTextColorRes(R.id.mentor_home_page_options_tv, R.color.color_FE3F3F)
                } else {
                    setText(R.id.mentor_home_page_options_tv, "询问")
                    getView<ImageView>(R.id.mentor_home_page_options_iv).setImageResource(R.drawable.icon_chat_tip)
                    askView.delegate.applyViewDelegate {
                        startColor = ContextCompat.getColor(context, R.color.color_FF8484)
                        endColor = ContextCompat.getColor(context, R.color.color_FF4030)
                        radius = 20.dp
                    }
                    setTextColor(R.id.mentor_home_page_options_tv, Color.WHITE)
                }
            }
        }
        view.setOnClickListener {
            if (item is ServiceInfo) {
                //服务列表，不管什么身份都跳转到服务详情
                MeiWebActivityLauncher.startActivity(activity,
                        MeiWebData(MeiUtil.appendParamsUrl(AmanLink.NetUrl.exclusive_service_details,
                                "service_id" to "${item.specialServiceId}",
                                "from" to "self_page"),
                                0).apply {
                            need_reload_web = 1
                        })
            } else if (item is CourseInfo) {
                MeiWebCourseServiceActivityLauncher.startActivity(activity,
                        MeiWebData(MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_introduce,
                                "status_bar_height" to "${getStatusBarHeight()}",
                                "course_id" to "${item.courseId}",
                                "from" to "self_page"),
                                0))
            }
        }

    }

    private fun askClick(userId: Int) {
        showLoading(true)
        // 普通用户点击询问，判断是否在直播中
        //主播端自己看自己是没有推荐的，因为个人主页入口很多，如果主播没在直播中就根本不知道推荐给谁，鑫姐说的
        apiSpiceMgr.executeKt(BatRoomStatusRequest(arrayListOf(userId)), success = {
            gotoPushIM(it.data["$userId"]?.roomId.orEmpty())
        }, failure = {
            gotoPushIM("")
        }, finish = {
            showLoading(false)
        })
    }

    private fun gotoPushIM(roomId: String) {
        val data = itemData
        if (data is ServiceInfo) {
            if (roomId.isNotEmpty()) {
                //直播中 （跳转到直播间）
                VideoLiveRoomActivityLauncher.startActivity(activity, roomId, LiveEnterType.user_home_page, ChickCustomData.Result(CustomType.special_service_card, ChickCustomData().apply {
                    this.serviceInfo = data
                    this.msgState = 2
                    this.content = arrayListOf(SplitText(text = "${MeiUser.getSharedUser().info?.nickname ?: ""}想要询问【专属服务】", color = "#A3E2FB", action = AmanLink.URL.exclusive_service_ask_dialog))
                }).toJson())
            } else {
                apiSpiceMgr.requestImUserInfo(arrayOf(data.publisherId), needRefresh = true, back = {
                    val info = it.firstOrNull()
                    when {
                        info?.isSelfToBlackList == true -> {
                            UIToast.toast(tabbarConfig.publisherBlacklistUser)
                        }
                        info?.isTheOtherToBlackList == true -> {
                            UIToast.toast(tabbarConfig.hasBlacklistTips)
                        }
                        else -> {
                            //未直播  (跳转到im页面发一条消息）
                            activity?.toImChat("${data.publisherId}", false, ChickCustomData.Result(CustomType.special_service_card, ChickCustomData().apply {
                                serviceInfo = data
                                content = arrayListOf(SplitText(text = "${MeiUser.getSharedUser().info?.nickname ?: ""}想要询问【专属服务】", color = "#A3E2FB", action = AmanLink.URL.exclusive_service_ask_dialog))
                            }))
                        }
                    }
                })

            }
        } else if (data is CourseInfo) {
            GrowingUtil.track("function_click",
                    "function_name", "课程", "function_page", "个人主页",
                    "click_type", if (!data.hasBuy || !data.hasReceive) "询问" else "开始学习",
                    "status", if (!data.hasBuy || !data.hasReceive) "未购买" else {
                if (data.hasReceive) "已领取" else "已购买"
            })
            when {
                data.hasBuy || data.hasReceive -> {
                    MeiWebCourseServiceActivityLauncher.startActivity(activity, MeiWebData(MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_introduce, "status_bar_height" to "${getStatusBarHeight()}", "course_id" to "${data.courseId}", "from" to "self_page"), 0))
                }
                roomId.isNotEmpty() -> {
                    //直播中 （跳转到直播间）
                    VideoLiveRoomActivityLauncher.startActivity(activity, roomId, LiveEnterType.user_home_page, ChickCustomData.Result(CustomType.course_card, ChickCustomData().apply {
                        this.courseInfo = data
                        this.msgState = 2
                        this.content = arrayListOf(SplitText(text = "${MeiUser.getSharedUser().info?.nickname ?: ""}想要询问【课程】", color = "#A3E2FB", action = AmanLink.URL.exclusive_service_ask_dialog))

                    }).toJson())
                }
                else -> {

                    apiSpiceMgr.requestImUserInfo(arrayOf(data.userId), needRefresh = true, back = {
                        val info = it.firstOrNull()
                        when {
                            info?.isSelfToBlackList == true -> {
                                UIToast.toast(tabbarConfig.publisherBlacklistUser)
                            }
                            info?.isTheOtherToBlackList == true -> {
                                UIToast.toast(tabbarConfig.hasBlacklistTips)
                            }
                            else -> {
                                //未直播  (跳转到im页面发一条消息）
                                activity?.toImChat("${data.userId}", false, ChickCustomData.Result(CustomType.course_card, ChickCustomData().apply {
                                    courseInfo = data
                                    content = arrayListOf(SplitText(text = "${MeiUser.getSharedUser().info?.nickname ?: ""}想要询问【课程】", color = "#A3E2FB", action = AmanLink.URL.exclusive_service_ask_dialog))
                                }))
                            }
                        }
                    })

                }
            }
        }
    }


}