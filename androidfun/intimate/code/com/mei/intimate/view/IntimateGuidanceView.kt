package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.login.checkLogin
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.unit.TimeUnit
import com.mei.wood.R
import com.mei.wood.extensions.notifyDiff
import com.net.model.chick.room.RoomList.UserGuide

/**
 * IntimateGridView
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-03
 */
class IntimateGuidanceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RecyclerView(context, attrs, defStyleAttr) {

    private var data = UserGuide()

    private val gridLayoutManager by lazy {
        GridLayoutManager(context, 3)
    }

    private val list = arrayListOf<UserGuide.Info>()
    private val guidanceAdapter by lazy {
        IntimateGuidanceAdapter(list).apply {
            setOnItemClickListener { _, _, position ->
                if (context.checkLogin()) {
                    GrowingUtil.track("main_app_gn_use_data",
                            "screen_name", "首页",
                            "main_app_gn_type", "使用手册",
                            "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                            "main_app_gn_label", "",
                            "position", "",
                            "main_app_gn_mentor_id", "",
                            "main_app_gn_content", "",
                            "main_app_gn_id", "",
                            "main_app_gn_cate", "",
                            "main_app_gn_gender", "",
                            "main_app_gn_pro", "")
                    Nav.toAmanLink(context, list[position].action)
                }
            }
        }
    }

    init {
        isNestedScrollingEnabled = false
        layoutManager = gridLayoutManager
        adapter = guidanceAdapter
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateLayoutParams<MarginLayoutParams> {
            topMargin = 10.dp.toInt()
            marginStart = 11.25f.dp2px.toInt()
            marginEnd = 11.25f.dp2px.toInt()
        }
    }

    fun convertData(userGuide: UserGuide?) {
        val oldList = list.toList()
        userGuide?.let {
            data = it
            list.clear()
            list.addAll(it.list)
            guidanceAdapter.notifyDataSetChanged()
        }
        val changed: (UserGuide.Info, UserGuide.Info) -> Boolean = { old, new -> old.title != new.title }
        this.notifyDiff(oldList, list, itemChange = changed, contentChange = changed)
    }

    class IntimateGuidanceAdapter(list: MutableList<UserGuide.Info>) :
            BaseQuickAdapter<UserGuide.Info, BaseViewHolder>(R.layout.item_intimate_guidance, list) {
        override fun convert(holder: BaseViewHolder, item: UserGuide.Info) {
            holder.setText(R.id.item_intimate_guidance_text, item.title.orEmpty())
                    .getView<ImageView>(R.id.item_intimate_guidance_img).glideDisplay(item.icon)
        }
    }

}