package com.mei.live.ui.dialog

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.animation.addListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.LiveGift
import com.joker.im.custom.chick.SplitText
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.dialog.PayFromType
import com.mei.dialog.myRose
import com.mei.dialog.showPayDialog
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.views.GiftAnimView
import com.mei.live.views.GiftCountDownView
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.AppManager
import com.net.MeiUser
import com.net.model.gift.GiftInfo
import com.net.model.rose.RoseFromSceneEnum
import kotlinx.android.synthetic.main.view_send_gift.view.*

/**
 * Created by hang on 2019-12-02.
 * 赠送礼物模块
 */

class SendGiftView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {

    var roseFromScene: RoseFromSceneEnum = RoseFromSceneEnum.PERSONAL_PAGE
    var fromType: String = "用户主动送礼"

    private val mGiftPageList = arrayListOf<List<GiftInfo>>()
    private val mPageAdapter by lazy { GiftPageAdapter(mGiftPageList) }

    private var toUserId: Int = 0
    private var toUserName = ""
    private var roomId = ""
    private var hideBack: (isHide: Boolean, balance: Double) -> Unit = { _, _ -> }
    private var statuBack: (type: Int, gift: LiveGift?) -> Unit = { _, _ -> }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_send_gift, this)

        val pagerSnapHelper = PagerSnapHelper()
        gift_page_recycler.onFlingListener = null
        pagerSnapHelper.attachToRecyclerView(gift_page_recycler)
        val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        gift_page_recycler.layoutManager = manager
        gift_page_recycler.adapter = mPageAdapter

        gift_page_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = pagerSnapHelper.findSnapView(manager)
                    snapView?.let {
                        val position = manager.getPosition(snapView)
                        gift_indicator.selectIndex(position)
                    }
                }
            }
        })
    }

    /**
     * 数据做分页处理
     */
    fun notifyGiftListData(toUserId: Int, toUserName: String, roomId: String, list: List<GiftInfo>, statuBack: (type: Int, gift: LiveGift?) -> Unit = { _, _ -> }, hideBack: (isHide: Boolean, balance: Double) -> Unit = { _, _ -> }) {
        this.toUserId = toUserId
        this.hideBack = hideBack
        this.statuBack = statuBack
        this.toUserName = toUserName
        this.roomId = roomId

        var pageNum = 8
        val pageList = list.groupBy { gift ->
            if (list.indexOf(gift) + 1 > pageNum)
                pageNum += 8
            pageNum
        }.toList().map { pair -> pair.second }
        mGiftPageList.clear()
        mGiftPageList.addAll(pageList)
        mPageAdapter.notifyDataSetChanged()
        gift_indicator.buildIndicator(mGiftPageList.size, R.drawable.selector_gift_indicators)
    }


    inner class GiftPageAdapter(list: MutableList<List<GiftInfo>>) :
            BaseQuickAdapter<List<GiftInfo>, BaseViewHolder>(R.layout.view_send_gift_page, list) {

        private val itemDecoration by lazy {
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val pos = parent.getChildAdapterPosition(view)
                    outRect.top = if (pos > 3) dip(15) else 0
                }
            }
        }

        override fun convert(holder: BaseViewHolder, item: List<GiftInfo>) {
            val recyclerView = holder.getView<RecyclerView>(R.id.recycler_view)
            recyclerView.layoutManager = GridLayoutManager(context, 4)
            recyclerView.adapter = SendGiftAdapter(item.toMutableList())

            recyclerView.removeItemDecoration(itemDecoration)
            recyclerView.addItemDecoration(itemDecoration)
        }

    }

    /**
     * 保存之前做动画的view
     */
    private var mPreAnimView: GiftAnimView? = null

    private var mPreCountDownView: GiftCountDownView? = null

    inner class SendGiftAdapter(list: MutableList<GiftInfo>) : BaseQuickAdapter<GiftInfo, BaseViewHolder>(R.layout.item_send_gift, list) {

        override fun convert(holder: BaseViewHolder, item: GiftInfo) {
            val giftAnimView = holder.getView<GiftAnimView>(R.id.gift_anim_view)
            val giftImg = holder.getView<ImageView>(R.id.send_gift_img)
            val giftCountDownView = holder.getView<GiftCountDownView>(R.id.count_down_view)
            giftImg.glideDisplay(item.giftImage, GlideDisPlayDefaultID.default_gift)
            holder.setText(R.id.gift_name, item.giftName)
                    .setText(R.id.cost_rose, item.costCoin.toString())
                    .setTextColor(R.id.gift_name, if (roseFromScene == RoseFromSceneEnum.EXCLUSIVE_CHAT) Color.parseColor("#333333") else Color.WHITE)
                    .setTextColor(R.id.cost_rose, if (roseFromScene == RoseFromSceneEnum.EXCLUSIVE_CHAT) Color.parseColor("#999999") else Color.parseColor("#80FFFFFF"))
            //点击礼物送礼
            holder.itemView.setOnClickListener {
                ((context as? MeiCustomActivity)
                        ?: ((context as? ContextWrapper)?.baseContext as? MeiCustomActivity))?.let { activity ->
                    if (toUserId == JohnUser.getSharedUser().userID) {
                        UIToast.toast("不能给自己送礼")
                        return@setOnClickListener
                    }
                    if (MeiUser.getSharedUser().info?.groupRole ?: 0 > 0) {
                        UIToast.toast("工作室成员不能送礼")
                        return@setOnClickListener
                    }

                    /**首先判断当前余额是否充足*/
                    if (MeiUser.getSharedUser().extra?.coinBalance?.compareTo(item.costCoin) ?: 0 < 0) {
                        val payFromType = if (roseFromScene == RoseFromSceneEnum.PERSONAL_PAGE) {
                            PayFromType.USER_PAGE
                        } else {
                            PayFromType.BROADCAST_SEND_GIFT
                        }
                        activity.showPayDialog(payFromType) { success ->
                            if (success) {
                                activity.myRose { balance ->
                                    hideBack(false, balance)
                                }
                            }
                        }
                        UIToast.toast("当前心币数量不足,请充值")
                        liveSendCustomMsg(roomId, CustomType.send_text, applyData = {
                            whitelist.add(toUserId)
                            val name = MeiUser.getSharedUser().info?.nickname.orEmpty()
                            content = arrayListOf<SplitText>().apply {
                                if (name.isNotEmpty()) add(SplitText(name, "#B3FFFFFF"))
                                add(SplitText(" 因余额不足，送礼物不成功"))
                            }
                        })
                    } else {
                        startSendGift(activity, giftAnimView, giftCountDownView, item)
                        if (roseFromScene == RoseFromSceneEnum.EXCLUSIVE_CHAT) {
                            MeiUser.getSharedUser().extra?.coinBalance?.let { it1 -> hideBack(true, it1) }
                            (AppManager.getInstance().currentActivity() as? MeiCustomActivity)?.showLoading(true)
                        }
                    }
                }
            }
        }

        private fun startSendGift(activity: MeiCustomActivity, view: GiftAnimView, countDownView: GiftCountDownView, item: GiftInfo) {
            /**倒计时动画*/
            if (countDownView != mPreCountDownView) {
                mPreCountDownView?.endAnim()
            }
            countDownView.startAnim()
            countDownView.mAnimator.addListener(onEnd = {
                mPreAnimView?.cancelAnim()
            })
            mPreCountDownView = countDownView

            /**加倍动画*/
            if (view != mPreAnimView) {
                mPreAnimView?.cancelAnim()
            }
            view.startAnim(1)
            mPreAnimView = view

            activity.requestSendGift(roomId, roomId, toUserId, toUserName, 1, item, roseFromScene, fromType, hideBack, statuBack)
        }
    }
}

fun VideoLiveRoomActivity.autoStartBannerAnim(giftInfo: GiftInfo, toUserName: String, toUserId: Int, giftCount: Int) {
    liveGiftBannerFragment.addGiftQueue(LiveGift().apply {
        addGift(giftInfo, giftCount, toUserId, toUserName)
    })
}

private fun LiveGift.addGift(giftInfo: GiftInfo, giftCount: Int, toUserId: Int, toUserName: String) {
    giftId = giftInfo.giftId
    atId = toUserId
    giftImg = giftInfo.bannerImage.orEmpty()
    giftName = giftInfo.giftName.orEmpty()
    giftEffect = giftInfo.giftEffect.orEmpty()
    count = giftCount
    giftType = giftInfo.giftType
    atName = toUserName
    nickName = MeiUser.getSharedUser().info?.nickname.orEmpty()
    avatar = MeiUser.getSharedUser().info?.avatar.orEmpty()
    userId = MeiUser.getSharedUser().info?.userId ?: 0
    gender = MeiUser.getSharedUser().info?.gender ?: 0
}



