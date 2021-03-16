package com.mei.intimate.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.GrowingUtil
import com.mei.find.ui.FindCourseActivityLauncher
import com.mei.login.checkLogin
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.unit.TimeUnit
import com.mei.wood.R
import com.mei.wood.extensions.notifyDiff
import com.mei.wood.modle.TAB_ITEM
import com.mei.wood.ui.TabMainActivity
import com.net.model.chick.room.RoomList
import com.net.model.chick.room.RoomList.ProduceCateItemBean
import kotlinx.android.synthetic.main.view_intimate_select_cate.view.*
import kotlinx.android.synthetic.main.view_intimate_separation_title.view.*

/**
 * Created by hang on 2020/7/10.
 */
class IntimateSelectCateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private val listData = arrayListOf<RoomList.ProduceCateBeanItem>()
    private val cateAdapter by lazy {
        CateAdapter(listData).apply {
            setOnItemClickListener { adapter, _, position ->
                /**暂时定位到发现页面*/
                (adapter.getItemOrNull(position) as? RoomList.ProduceCateBeanItem)?.let { item ->
                    context.checkLogin { login ->
                        if (login) {
                            GrowingUtil.track("main_app_gn_use_data",
                                    "screen_name", "首页",
                                    "main_app_gn_type", "解决烦恼",
                                    "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                                    "main_app_gn_label", "",
                                    "position", "",
                                    "main_app_gn_mentor_id", "",
                                    "main_app_gn_content", "",
                                    "main_app_gn_id", "",
                                    "main_app_gn_cate", "",
                                    "main_app_gn_gender", "",
                                    "main_app_gn_pro", "")
                            FindCourseActivityLauncher.startActivity(context, item.partitionId)
                        }
                    }
                }

            }
        }
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_select_cate)

        intimate_separation_main_title.paint.isFakeBoldText = true

        question_recycler.adapter = cateAdapter
        question_recycler.isNestedScrollingEnabled = false
        question_recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {

            private val itemMarginTop = 10.dp.toInt()
            private val itemMarginRowBorder = 5.6f.dp2px.toInt()
            private val itemMarginHorizontal = 2.8f.dp2px.toInt()

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = (view.layoutParams as? RecyclerView.LayoutParams)?.viewLayoutPosition
                        ?: -1
                if (position > 3) {
                    outRect.top = itemMarginTop
                }
                when {
                    position % 4 == 0 -> outRect.right = itemMarginRowBorder
                    (position + 1) % 4 == 0 -> outRect.left = itemMarginRowBorder
                    else -> {
                        outRect.left = itemMarginHorizontal
                        outRect.right = itemMarginHorizontal
                    }
                }
            }
        })

        setOnClickListener {
            (context as? TabMainActivity)?.setCurrentTabItem(TAB_ITEM.DISCOVERY)
        }
    }

    fun convertData(data: ProduceCateItemBean?) {
        val oldList = listData.toList()
        data?.let {
            listData.clear()
            listData.addAll(it.items)
            intimate_separation_main_title.text = it.title
//            cateAdapter.setList(it.items)
        }
        val changed: (RoomList.ProduceCateBeanItem, RoomList.ProduceCateBeanItem) -> Boolean = { old, new ->
            old.partitionName != new.partitionName
        }
        question_recycler.notifyDiff(oldList, listData, itemChange = changed, contentChange = changed)
    }

    class CateAdapter(val list: MutableList<RoomList.ProduceCateBeanItem>) : BaseQuickAdapter<RoomList.ProduceCateBeanItem, BaseViewHolder>(R.layout.item_produce_cate, list) {

        override fun convert(holder: BaseViewHolder, item: RoomList.ProduceCateBeanItem) {
            holder.setText(R.id.product_name, item.partitionName)
        }
    }
}