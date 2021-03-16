package com.mei.intimate.adapter

import android.annotation.SuppressLint
import android.util.SparseIntArray
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.growingio.android.sdk.collection.GrowingIO
import com.growingio.android.sdk.collection.ImpressionMark
import com.mei.intimate.adapter.IntimateHomeItemType.*
import com.mei.intimate.view.*
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.date.formatTimeToHMS
import com.mei.orc.util.string.getLong
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.chick.room.RoomItemType
import com.net.model.chick.room.RoomList.*
import org.json.JSONObject

/**
 * Created by hang on 2020-02-13.
 */
// 增加类型后需要在IntimateHomeAdapter以及IntimateHomeDecoration进行添加修改
enum class IntimateHomeItemType {
    ROOM_ITEM_TOP_BANNER,                       //顶部测试webView
    ROOM_ITEM_TOP_WEB_BANNER,                   //顶部webView
    ROOM_ITEM_PUBLIC_BROADCAST,                 //直播条目
    ROOM_ITEM_COMMON_BANNER,                    //banner
    ROOM_ITEM_FILL_BANNER,                      //填补直播条目空缺的banner---ui有要求
    ROOM_ITEM_SPECIAL_BROADCAST,                //连线直播条目
    ROOM_ITEM_MODULE_SEPARATION,                //普通title样式
    ROOM_ITEM_PRODUCT_CATE,                     //产品功能选择
    ROOM_ITEM_COMMON_WEB_BANNER,                //普通webView
    ROOM_ITEM_SEPARATION_TITLE,                 //带更多和子标题的title
    ROOM_ITEM_RECOMMEND_PUBLISHER,              //推荐关注
    ROOM_ITEM_FOLLOWED_PUBLISHER,               //关注直播，和'推荐关注'是一个UI
    ROOM_ITEM_USER_GUIDE,                       //使用手册
    ROOM_ITEM_BOTTOM_LABEL,                     //底部标签
    ROOM_ITEM_ONLINE_PUBLISHER,                 //在线主播
    ROOM_ITEM_ROLLING_MSG,                      //滚动条目
    ROOM_ITEM_NAVIGATE_BAR_APP,                 //金刚区
    SPACE
}

class IntimateHomeAdapter(list: MutableList<BaseItem<*>>) :
        MeiMultiQuickAdapter<BaseItem<*>, BaseViewHolder>(list),
        LoadMoreModule {

    private val intimateTypeArr by lazy { IntimateHomeItemType.values() }

    init {
        setGridSpanSizeLookup { _, viewType, _ ->
            when (intimateTypeArr[viewType]) {
                ROOM_ITEM_TOP_BANNER,
                ROOM_ITEM_MODULE_SEPARATION,
                ROOM_ITEM_TOP_WEB_BANNER,
                ROOM_ITEM_RECOMMEND_PUBLISHER,
                ROOM_ITEM_FOLLOWED_PUBLISHER,
                ROOM_ITEM_USER_GUIDE,
                ROOM_ITEM_BOTTOM_LABEL,
                ROOM_ITEM_ROLLING_MSG,
                ROOM_ITEM_PRODUCT_CATE,
                ROOM_ITEM_NAVIGATE_BAR_APP,
                ROOM_ITEM_SEPARATION_TITLE -> 2
                else -> 1
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return when (val content = getItemOrNull(position)?.content) {
            is HomePageNavigateBar -> content.seqId.toLong()
            is BannerItemBean -> content.seqId.toLong()
            is ProduceCateItemBean -> content.seqId.toLong()
            is ShortVideoItemBean -> content.seqId.getLong()
            is BroadcastItemBean -> content.createUserId.toLong()
            else -> super.getItemId(position)
        }
    }

    override fun getDefItemViewType(position: Int): Int {
        return when (getItemOrNull(position)?.type) {
            RoomItemType.TOP_BANNER -> ROOM_ITEM_TOP_BANNER
            RoomItemType.NAVIGATE_BAR -> ROOM_ITEM_TOP_WEB_BANNER
            RoomItemType.BANNER_WITH_HTML,
            RoomItemType.BANNER_WITH_URL -> ROOM_ITEM_COMMON_WEB_BANNER
            RoomItemType.PUBLIC_BROADCAST,
            RoomItemType.SCHEDULE_BROADCAST -> ROOM_ITEM_PUBLIC_BROADCAST
            RoomItemType.BANNER -> ROOM_ITEM_COMMON_BANNER
            RoomItemType.BANNER_FILL -> ROOM_ITEM_FILL_BANNER
            RoomItemType.EXCLUSIVE_BROADCAST,
            RoomItemType.SPECIAL_BROADCAST -> ROOM_ITEM_SPECIAL_BROADCAST
            RoomItemType.MODULE_SEPARATION -> ROOM_ITEM_MODULE_SEPARATION
            RoomItemType.PRODUCT_CATE -> ROOM_ITEM_PRODUCT_CATE
            RoomItemType.MODULE_SEPARATION_WITH_TITLE -> ROOM_ITEM_SEPARATION_TITLE
            RoomItemType.PUBLISHER_RECOMMEND -> ROOM_ITEM_RECOMMEND_PUBLISHER
            RoomItemType.PUBLISHER_FOLLOWED -> ROOM_ITEM_FOLLOWED_PUBLISHER
            RoomItemType.USER_GUIDE -> ROOM_ITEM_USER_GUIDE
            RoomItemType.IMAGE_WITH_BOTTOM_TEXT -> ROOM_ITEM_BOTTOM_LABEL
            RoomItemType.ONLINE_PUBLISHER -> ROOM_ITEM_ONLINE_PUBLISHER
            RoomItemType.SCROLLING_MESSAGE -> ROOM_ITEM_ROLLING_MSG
            RoomItemType.NAVIGATE_BAR_APP -> ROOM_ITEM_NAVIGATE_BAR_APP
            else -> SPACE
        }.ordinal
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = when (intimateTypeArr[viewType]) {
            ROOM_ITEM_TOP_WEB_BANNER, ROOM_ITEM_COMMON_WEB_BANNER -> IntimateHeadView(parent.context)
            ROOM_ITEM_TOP_BANNER -> IntimateHeadBannerView(parent.context)
            ROOM_ITEM_PUBLIC_BROADCAST -> IntimateCommonLiveView(parent.context)
            ROOM_ITEM_FILL_BANNER -> IntimateBannerItemView(parent.context)
            ROOM_ITEM_COMMON_BANNER -> IntimateBannerItemView(parent.context)
            ROOM_ITEM_SPECIAL_BROADCAST -> IntimateSpecialLiveView(parent.context)
            ROOM_ITEM_MODULE_SEPARATION -> parent.layoutInflaterKt(R.layout.item_intimate_title)
            ROOM_ITEM_PRODUCT_CATE -> IntimateSelectCateView(parent.context)
            ROOM_ITEM_SEPARATION_TITLE -> IntimateSeparationTitleView(parent.context)
            ROOM_ITEM_RECOMMEND_PUBLISHER,
            ROOM_ITEM_FOLLOWED_PUBLISHER -> IntimateFollowListView(parent.context)
            ROOM_ITEM_USER_GUIDE -> IntimateGuidanceView(parent.context)
            ROOM_ITEM_BOTTOM_LABEL -> IntimateFooterView(parent.context)
            ROOM_ITEM_ONLINE_PUBLISHER -> IntimateConsultOnlineView(parent.context)
            ROOM_ITEM_ROLLING_MSG -> IntimateShufflingNoticeView(parent.context)
            ROOM_ITEM_NAVIGATE_BAR_APP -> IntimateNavigateBarAppView(parent.context)
            else -> parent.layoutInflaterKt(R.layout.item_recommend_space)
        }
        return BaseViewHolder(itemView)
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(holder: BaseViewHolder, item: BaseItem<*>) {
        val viewType = intimateTypeArr[holder.itemViewType]

        //埋点相关参数
        val globalId = holder.bindingAdapterPosition.toString()
        val visibleScale = 0.5f
        try {
            if (viewType != ROOM_ITEM_ROLLING_MSG
                    && viewType != ROOM_ITEM_TOP_WEB_BANNER
                    && viewType != ROOM_ITEM_SEPARATION_TITLE
                    && viewType != ROOM_ITEM_BOTTOM_LABEL && JohnUser.getSharedUser().hasLogin()) { //滚动条目需要在IntimateShufflingNoticeView处理
                GrowingIO.getInstance().markViewImpression(ImpressionMark(holder.itemView, "main_gn_entrance_view")
                        .setGlobalId(globalId)
                        .setVisibleScale(visibleScale)
                        .setVariable(JSONObject().apply {
                            put("screen_name", "首页")
                            put("main_app_gn_pro", "")
                            getGrowingJsonData(viewType, item.content)
                        }))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        when (viewType) {
            ROOM_ITEM_TOP_WEB_BANNER, ROOM_ITEM_COMMON_WEB_BANNER -> (holder.itemView as? IntimateHeadView)?.convertData(item.content as? HomePageNavigateBar)
            ROOM_ITEM_TOP_BANNER -> (holder.itemView as? IntimateHeadBannerView)?.notifyData((item.content as? List<BannerItemBean>).orEmpty())
            ROOM_ITEM_PUBLIC_BROADCAST -> (holder.itemView as? IntimateCommonLiveView)?.convertData(item)
            ROOM_ITEM_COMMON_BANNER,
            ROOM_ITEM_FILL_BANNER -> (holder.itemView as? IntimateBannerItemView)?.convertData(item.content as? BannerItemBean, item.type)
            ROOM_ITEM_SPECIAL_BROADCAST -> (holder.itemView as? IntimateSpecialLiveView)?.convertData(item)
            ROOM_ITEM_MODULE_SEPARATION -> {
                (item.content as? String)?.let {
                    holder.itemView.findViewById<TextView>(R.id.title).apply {
                        paint.isFakeBoldText = true
                        text = it
                    }
                }
            }
            ROOM_ITEM_PRODUCT_CATE -> (holder.itemView as? IntimateSelectCateView)?.convertData(item.content as? ProduceCateItemBean)
            ROOM_ITEM_SEPARATION_TITLE -> (holder.itemView as? IntimateSeparationTitleView)?.convertData(item.content as? SeparationTitle)
            ROOM_ITEM_RECOMMEND_PUBLISHER -> (holder.itemView as? IntimateFollowListView)?.convertData(item.content as? RecommendPublisher)
            ROOM_ITEM_FOLLOWED_PUBLISHER -> (holder.itemView as? IntimateFollowListView)?.convertData(item.content as? FollowedPublisher)
            ROOM_ITEM_USER_GUIDE -> (holder.itemView as? IntimateGuidanceView)?.convertData(item.content as? UserGuide)
            ROOM_ITEM_BOTTOM_LABEL -> (holder.itemView as? IntimateFooterView)?.convertData(item.content as? FooterLabel)
            ROOM_ITEM_ONLINE_PUBLISHER -> (holder.itemView as? IntimateConsultOnlineView)?.convertData(item.content as? OnlinePublisher)
            ROOM_ITEM_ROLLING_MSG -> (holder.itemView as? IntimateShufflingNoticeView)?.convertData((item.content as? ScrollingMessage)?.apply {
                this.globalId = globalId
                this.visibleScale = visibleScale
            })
            ROOM_ITEM_NAVIGATE_BAR_APP -> (holder.itemView as? IntimateNavigateBarAppView)?.convertData(item.content as? NavigateBarApp)
            else -> {
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: BaseItem<*>, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        if (payloads.isNotEmpty() && intimateTypeArr[holder.itemViewType] == ROOM_ITEM_SPECIAL_BROADCAST) {
            val duration = (payloads[0] as? SparseIntArray)?.get(holder.layoutPosition) ?: 0
            if (duration > 0) {
                (holder.itemView as? IntimateSpecialLiveView)?.findViewById<TextView>(R.id.upstream_time)?.text = "已连时长${duration.formatTimeToHMS()}"
            }
        }
    }

    private fun <T> JSONObject.getGrowingJsonData(viewType: IntimateHomeItemType, content: T): JSONObject {
        if (content is BannerItemBean) {
            put("main_app_gn_type", "瀑布流banner").put("main_app_gn_label", content.action)
            return this
        }
        when (viewType) {
            ROOM_ITEM_NAVIGATE_BAR_APP -> put("main_app_gn_type", "金刚区")
            ROOM_ITEM_ONLINE_PUBLISHER -> put("main_app_gn_type", "知心达人在线")
            ROOM_ITEM_PUBLIC_BROADCAST -> put("main_app_gn_type", "公开直播")
            ROOM_ITEM_FOLLOWED_PUBLISHER,
            ROOM_ITEM_RECOMMEND_PUBLISHER -> put("main_app_gn_type", "推荐关注")
            ROOM_ITEM_PRODUCT_CATE -> put("main_app_gn_type", "解决烦恼")
            ROOM_ITEM_SPECIAL_BROADCAST -> put("main_app_gn_type", "连线直播")
            ROOM_ITEM_USER_GUIDE -> put("main_app_gn_type", "使用手册")
            else -> null
        }?.apply { put("main_app_gn_label", "") }
        return this
    }

}

