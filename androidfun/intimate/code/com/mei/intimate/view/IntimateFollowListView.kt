package com.mei.intimate.view

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.shape.*
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.login.checkLogin
import com.mei.orc.ext.*
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.extensions.notifyDiff
import com.net.model.chick.room.PublisherOnlineStatusEnum
import com.net.model.chick.room.RoomList.FollowedPublisher
import com.net.model.chick.room.RoomList.RecommendPublisher
import kotlinx.android.synthetic.main.item_intimate_follow_list.view.*
import kotlinx.android.synthetic.main.view_intimate_follow_publisher.view.*

/**
 * IntimateFollowListView
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-02
 */
class IntimateFollowListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var recommendPublisher = RecommendPublisher()
    private var followedPublisher = FollowedPublisher()

    private val leftBubbleDrawable by lazy {
        val shapePathModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 8.dp)
                .setLeftEdge(object : TriangleEdgeTreatment(6.dp, false) {
                    override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                        super.getEdgePath(length, length - 10.dp, interpolation, shapePath)
                    }
                })
                .build()
        MaterialShapeDrawable(shapePathModel).apply {
            paintStyle = Paint.Style.STROKE
            strokeWidth = 0.25f.dp2px
            setStrokeTint(ContextCompat.getColor(this@IntimateFollowListView.context, R.color.color_FF8254))
        }
    }

    private val followList = arrayListOf<RecommendPublisher.Info>()
    private val followListAdapter by lazy {
        FollowListAdapter(followList).apply {
            setOnItemClickListener { _, _, position ->
                if (context.checkLogin()) {
                    GrowingUtil.track("main_app_gn_use_data",
                            "screen_name", "首页",
                            "main_app_gn_type", "推荐关注",
                            "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                            "main_app_gn_label", "",
                            "position", "",
                            "main_app_gn_mentor_id", "",
                            "main_app_gn_content", "",
                            "main_app_gn_id", "",
                            "main_app_gn_cate", "",
                            "main_app_gn_gender", "",
                            "main_app_gn_pro", "")
                    Nav.toAmanLink(context, followList[position].action)
                }
            }
        }
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_follow_publisher)

        intimate_follow_publisher_title.paint.isFakeBoldText = true
        intimate_follow_publisher_my_follow_text.paint.isFakeBoldText = true
        intimate_follow_publisher_recommend_description.paint.isFakeBoldText = true
        intimate_follow_publisher_rv.isNestedScrollingEnabled = false
        intimate_follow_publisher_rv.adapter = followListAdapter
        intimate_follow_publisher_rv.addItemDecoration(Decoration())

        intimate_follow_publisher_more_click.setOnClickNotDoubleListener(tag = "intimate_follow_more") {
            Nav.toAmanLink(context, followedPublisher.moreAction)
        }
    }

    inner class Decoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.right = dip(10)
        }
    }

    fun convertData(data: Any?) {
        (data as? RecommendPublisher)?.let {
            showFollowListOrNot(false)
            recommendPublisher = it
            loadRecommendInfo()
        }
        (data as? FollowedPublisher)?.let {
            showFollowListOrNot(true)
            followedPublisher = it
            loadFollowInfo()
        }
    }

    private fun loadRecommendInfo() {
        intimate_follow_publisher_title.text = recommendPublisher.text.orEmpty()
        intimate_follow_publisher_my_follow_text.text = recommendPublisher.title.orEmpty()
        intimate_follow_publisher_recommend_description.text = recommendPublisher.subTitle.orEmpty()
        intimate_follow_publisher_recommend_description.background = leftBubbleDrawable
        recommendPublisher.recommend?.apply {
            item_intimate_follow_avatar.glideDisplay(avatar, R.drawable.default_avatar_50)
            item_intimate_follow_state_online.isVisible = (status == PublisherOnlineStatusEnum.ONLINE.name)
            item_intimate_follow_state.isVisible = (status == PublisherOnlineStatusEnum.BROADCASTING.name)
        }
        setOnClickNotDoubleListener(tag = "intimate_follow_whole") {
            if (context.checkLogin()) {
                GrowingUtil.track("main_app_gn_use_data",
                        "screen_name", "首页",
                        "main_app_gn_type", "推荐关注",
                        "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                        "main_app_gn_label", "",
                        "position", "",
                        "main_app_gn_mentor_id", "",
                        "main_app_gn_content", "",
                        "main_app_gn_id", "",
                        "main_app_gn_cate", "",
                        "main_app_gn_gender", "",
                        "main_app_gn_pro", "")
                Nav.toAmanLink(context, recommendPublisher.action)
            }
        }
    }

    private fun loadFollowInfo() {
        intimate_follow_publisher_title.text = followedPublisher.text.orEmpty()
        intimate_follow_publisher_my_follow_text.text = followedPublisher.title.orEmpty()
        intimate_follow_publisher_sub_text.text = followedPublisher.subTitle.orEmpty()
        val oldList = followList.toList()
        followedPublisher.myFollowList?.apply {
            followList.clear()
            followList.addAll(subListByIndex(4))
        }
        val changed: (RecommendPublisher.Info, RecommendPublisher.Info) -> Boolean = { old, new ->
            old.publisherUserId != new.publisherUserId || old.status != new.status
        }
        intimate_follow_publisher_rv.notifyDiff(oldList, followList, itemChange = changed, contentChange = changed)
        showMoreViewOrNot(followedPublisher.moreTitle.isNullOrEmpty().not())
        intimate_follow_publisher_more_text.text = followedPublisher.moreTitle.orEmpty()
        setOnClickNotDoubleListener(tag = "intimate_follow_whole") {}
    }

    private fun showFollowListOrNot(visible: Boolean) {
        val showFollowListView = if (visible) VISIBLE else GONE
        val showRecommendView = if (visible) GONE else VISIBLE
        intimate_follow_publisher_sub_text.visibility = showFollowListView
        intimate_follow_publisher_rv.visibility = showFollowListView
        intimate_follow_publisher_more_text.visibility = showFollowListView
        intimate_follow_publisher_more_click.visibility = showFollowListView
        intimate_follow_publisher_group_recommend.visibility = showRecommendView
    }

    private fun showMoreViewOrNot(visible: Boolean) {
        val showMore = if (visible) VISIBLE else GONE
        intimate_follow_publisher_more_text.visibility = showMore
        intimate_follow_publisher_more_click.visibility = showMore
    }

    class FollowListAdapter(list: MutableList<RecommendPublisher.Info>) :
            BaseQuickAdapter<RecommendPublisher.Info, BaseViewHolder>(R.layout.item_intimate_follow_list, list) {
        override fun convert(holder: BaseViewHolder, item: RecommendPublisher.Info) {
            holder.getView<ImageView>(R.id.item_intimate_follow_avatar).glideDisplay(item.avatar, R.drawable.default_avatar_50)
            holder.getView<ImageView>(R.id.item_intimate_follow_state).isVisible = item.status == PublisherOnlineStatusEnum.BROADCASTING.name
            holder.getView<View>(R.id.item_intimate_follow_state_online).isVisible = item.status == PublisherOnlineStatusEnum.ONLINE.name
        }
    }

}