package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.login.checkLogin
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.unit.TimeUnit
import com.mei.wood.R
import com.mei.wood.extensions.notifyDiff
import com.mei.wood.util.logDebug
import com.net.model.chick.room.RoomList.NavigateBarApp
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.net.URL

/**
 * IntimateNavigateBarAppView
 *
 * 跳转导航栏
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-06
 */
class IntimateNavigateBarAppView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RecyclerView(context, attrs, defStyleAttr) {

    private val navigateList = arrayListOf<NavigateBarApp.Info>()
    private val navigateAdapter by lazy {
        IntimateNavigateBarAppAdapter(navigateList).apply {
            setOnItemClickListener { _, _, position ->
                if (context.checkLogin()) {
                    GrowingUtil.track("main_app_gn_use_data",
                            "screen_name", "首页",
                            "main_app_gn_type", "金刚区",
                            "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                            "main_app_gn_label", navigateList[position].name,
                            "position", "",
                            "main_app_gn_mentor_id", "",
                            "main_app_gn_content", "",
                            "main_app_gn_id", "",
                            "main_app_gn_cate", "",
                            "main_app_gn_gender", "",
                            "main_app_gn_pro", "")
                    Nav.toAmanLink(context, navigateList[position].action)
                }
            }
        }
    }

    init {
        isNestedScrollingEnabled = false
        layoutManager = GridLayoutManager(context, 4)
        adapter = navigateAdapter
        setPadding(dip(5), dip(10), dip(5), 0)
    }

    fun convertData(navigateData: NavigateBarApp?) {
        val oldList = navigateList.toList()
        navigateData?.apply {
            navigateList.clear()
            navigateList.addAll(list)
            (layoutManager as? GridLayoutManager)?.apply {
                if (list.size < 4) spanCount = list.size
            }
        }
        // 如果数据里有svga图，则需要刷新整个View，因为目前SVGAImageView无法在嵌套的recyclerview中复用
        if (navigateList.any { it.tipsIcon.isNullOrEmpty() }) {
            navigateAdapter.notifyDataSetChanged()
        } else {
            val changed: (NavigateBarApp.Info, NavigateBarApp.Info) -> Boolean = { old, new -> old.name != new.name }
            this.notifyDiff(oldList, navigateList, itemChange = changed, contentChange = changed)
        }
    }

    class IntimateNavigateBarAppAdapter(list: MutableList<NavigateBarApp.Info>) :
            BaseQuickAdapter<NavigateBarApp.Info, BaseViewHolder>(R.layout.item_intimate_navigate_bar_app, list) {
        override fun convert(holder: BaseViewHolder, item: NavigateBarApp.Info) {
            holder.getView<ImageView>(R.id.item_intimate_banner_img).glideDisplay(item.icon.orEmpty())
            holder.setText(R.id.item_intimate_banner_text, item.name.orEmpty())
            holder.getView<SVGAImageView>(R.id.item_intimate_banner_RT_img).apply {
                clearsAfterDetached = false
                isGone = item.tipsIcon.isNullOrEmpty()
                item.tipsIcon?.let { setSvgaImage(it) }
            }
        }

        private fun SVGAImageView.setSvgaImage(svgaUrl: String) {
            var url: URL? = null
            try {
                url = URL(svgaUrl)
            } catch (e: Exception) {
                logDebug(e.toString())
            }
            url?.let {
                SVGAParser.shareParser().decodeFromURL(it, object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        val svgaDrawable = SVGADrawable(videoItem)
                        setImageDrawable(svgaDrawable)
                        startAnimation()
                    }

                    override fun onError() {}
                })
            }
        }
    }

}