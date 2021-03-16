package com.mei.mentor_home_page.adapter

import android.view.ViewGroup
import android.widget.Space
import com.joker.im.custom.chick.CourseInfo
import com.joker.im.custom.chick.ServiceInfo
import com.mei.live.views.ExpandableTextView.ExpandableStatusFix
import com.mei.live.views.ExpandableTextView.StatusType
import com.mei.mentor_home_page.adapter.item.*
import com.mei.mentor_home_page.model.CourseTitle
import com.mei.mentor_home_page.model.MentorLiveData
import com.mei.mentor_home_page.model.ServiceTitle
import com.mei.mentor_home_page.model.WorkTitle
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.wood.R
import com.net.model.chick.friends.ProductBean
import com.net.model.chick.friends.UserHomePagerResult

/**
 *
 * @author Created by zyh on 2020/5/20
 */
/**
 * 导师主页基本信息item
 */
const val ITEM_TYPE_USER_INFO = 1

/**
 * 导师主页导师作品item（图片or视频）
 */
const val ITEM_TYPE_WORKS = 2

/**
 *  导师主页导师收到的礼物item
 */
const val ITEM_TYPE_RECEIVE_GIFT = 3

/**
 * 导师作品为null的情况展示的item
 */
const val ITEM_TYPE_WORK_EMPTY = 4

/**
 * 导师作品有更多的时候显示的展开item
 */
const val ITEM_TYPE_WORK_UNFOLD = 5

/**
 * 导师直播的一些信息item
 */
const val ITEM_TYPE_LIVE_DATA = 6

/**
 * 列表尾部item
 */
const val ITEM_TYPE_FOOTER = 7

/**
 * 导师作品title item
 */
const val ITEM_TYPE_WORK_TITLE = 8

/**
 * 导师服务tile item
 */
const val ITEM_TYPE_SERVICE_TITLE = 9

/**
 * 服务列表item
 */
const val ITEM_TYPE_SERVICE_LIST = 11

/**
 * 导师专属服务有更多时候展示展开item
 */
const val ITEM_TYPE_SERVICE_UNFOLD = 12

/**
 * 知识付费课程item
 */
const val ITEM_TYPE_COURSE_SERVICE = 13

/**
 * 课程title
 */
const val ITEM_TYPE_COURSE_TITLE = 14

/**
 * 展开课程更多
 */
const val ITEM_TYPE_COURSE_UNFOLD = 15

class MentorPageAdapter(list: MutableList<Any>) : MeiMultiQuickAdapter<Any, MentorBaseHolder>(list) {
    var statusType = StatusType.STATUS_EXPAND

    init {
        setGridSpanSizeLookup { _, viewType, _ ->
            when (viewType) {
                ITEM_TYPE_WORKS -> 3
                ITEM_TYPE_RECEIVE_GIFT -> 2
                else -> 6
            }
        }
    }

    override fun getDefItemViewType(position: Int): Int {

        return when (val data = getItemOrNull(position)) {
            is UserHomePagerResult -> ITEM_TYPE_USER_INFO
            is ProductBean -> ITEM_TYPE_WORKS
            is MentorLiveData -> ITEM_TYPE_LIVE_DATA
            is UserHomePagerResult.ReceiveGiftBean -> ITEM_TYPE_RECEIVE_GIFT
            is WorkTitle -> ITEM_TYPE_WORK_TITLE
            is ServiceTitle -> ITEM_TYPE_SERVICE_TITLE
            is CourseTitle -> ITEM_TYPE_COURSE_TITLE
            is ServiceInfo -> ITEM_TYPE_SERVICE_LIST
            is CourseInfo -> ITEM_TYPE_COURSE_SERVICE
            is Int -> data
            else -> 0
        }
    }

    //保存展开状态
    private val expandableStatus = object : ExpandableStatusFix {
        override fun getStatus(): StatusType {
            return statusType
        }

        override fun setStatus(status: StatusType) {
            statusType = status
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): MentorBaseHolder {
        return when (viewType) {
            ITEM_TYPE_USER_INFO -> {
                val viewHolder = UserBaseInfoViewHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_info))
                viewHolder.etv?.bind(expandableStatus)
                return viewHolder
            }
            ITEM_TYPE_WORKS -> UserWorksViewHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_product_show))
            ITEM_TYPE_WORK_EMPTY -> MentorBaseHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_empty_data))
            ITEM_TYPE_LIVE_DATA -> MentorHomePageLiveDataHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_living_statistics))
            ITEM_TYPE_RECEIVE_GIFT -> MentorHomePageGiftHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_gift_show))
            ITEM_TYPE_WORK_TITLE -> MentorHomePageWorkTitleHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_work_title))

            ITEM_TYPE_COURSE_UNFOLD,
            ITEM_TYPE_WORK_UNFOLD,
            ITEM_TYPE_SERVICE_UNFOLD -> MentorHomePageUnfoldDataHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_works_look_more))
            ITEM_TYPE_SERVICE_TITLE -> MentorBaseHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_service_title))
            ITEM_TYPE_COURSE_TITLE -> MentorHomePageCourseTitleHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_service_title))
            //课程与服务
            ITEM_TYPE_COURSE_SERVICE,
            ITEM_TYPE_SERVICE_LIST -> MentorHomePageServiceListHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_service_list))
            ITEM_TYPE_FOOTER -> MentorBaseHolder(parent.layoutInflaterKtToParent(R.layout.item_mentor_home_page_footer))
            else -> MentorBaseHolder(Space(parent.context))

        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun convert(holder: MentorBaseHolder, item: Any) {
        holder.refresh(item)
    }

    /**
     * 刷新视频上传进度 ，"payloads"不为null就不走一般的bindViewHolder
     */
    override fun convert(holder: MentorBaseHolder, item: Any, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        if (holder is UserWorksViewHolder) {
            holder.refresh(item, payloads)
        }
    }

}