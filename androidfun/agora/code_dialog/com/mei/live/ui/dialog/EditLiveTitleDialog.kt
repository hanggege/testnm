package com.mei.live.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundTextView
import com.mei.live.ui.LIVE_TITLE_PATTERN
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.me.ext.filterPattern
import com.mei.orc.Cxt
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.addTextChangedListener
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.room.ProductCategory.ProductCategoryBean
import com.net.network.room.ProductCategoryRequest
import com.net.network.room.RoomInfoModifyRequest
import kotlinx.android.synthetic.main.dialog_edit_live_title.*

/**
 * Created by hang on 2020/6/12.
 * 编辑直播标题弹窗
 */
fun MeiCustomBarActivity.showEditLiveTitleDialog(category: String, title: String, back: (String, String) -> Unit = { _, _ -> }) {
    showLoading(true)
    apiSpiceMgr.executeToastKt(ProductCategoryRequest(), success = {
        if (it.isSuccess) {
            it.data?.list.orEmpty().let { list ->
                EditLiveTitleDialog().apply {
                    mTitle = title
                    mSelectedIndex = list.indexOfFirst { item -> item.pro_cate_name == category }
                    mData.addAll(list)
                    mBack = back
                }.show(supportFragmentManager, EditLiveTitleDialog::class.java.simpleName)
            }
        }
    }, finish = {
        showLoading(false)
    })

}

class EditLiveTitleDialog : BottomDialogFragment() {

    var mTitle: String = ""

    var mBack: (String, String) -> Unit = { _, _ -> }

    var mSelectedIndex = -1

    val mData = arrayListOf<ProductCategoryBean>()

    private val mCateNameAdapter by lazy {
        CateNameAdapter(mData).apply {
            setOnItemClickListener { _, _, position ->
                mSelectedIndex = position
                notifyDataSetChanged()
            }
        }
    }

    override fun onSetInflaterLayout(): Int = R.layout.dialog_edit_live_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()

        confirm_edit.setOnClickNotDoubleListener {
            confirmEdit()
        }
        edit_live_title.filterPattern(LIVE_TITLE_PATTERN, "名字不能包含特殊字符", 16)

        edit_live_title.addTextChangedListener {
            confirm_edit.apply {
                isEnabled = it.isNotEmpty()
                if (isEnabled) {
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                } else {
                    setTextColor(ContextCompat.getColor(context, R.color.color_c8c8c8))
                }
            }
            confirm_edit.delegate.applyViewDelegate {
                if (it.isNotEmpty()) {
                    radius = 22.5f.dp2px
                    startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                    endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
                } else {
                    radius = 20.dp
                    startColor = ContextCompat.getColor(context, R.color.color_f3f3f3)
                }
            }
        }
    }

    private fun initView() {
        cate_recycler.layoutManager = GridLayoutManager(activity, 4)
        cate_recycler.adapter = mCateNameAdapter
        edit_live_title.setText(mTitle)
    }

    private fun confirmEdit() {
        val liveTitle = edit_live_title.text.toString()
        (activity as? VideoLiveRoomActivity)?.run {
            showLoading(true)
            apiSpiceMgr.executeToastKt(RoomInfoModifyRequest().apply {
                roomId = roomInfo.roomId
                title = liveTitle
                proCateId = mData.getOrNull(mSelectedIndex)?.pro_cate_id ?: 0
            }, success = {
                dismissAllowingStateLoss()
                UIToast.toast("修改成功")
            }, finish = {
                showLoading(false)
            })
        }
    }

    inner class CateNameAdapter(list: MutableList<ProductCategoryBean>) : BaseQuickAdapter<ProductCategoryBean, BaseViewHolder>(R.layout.item_select_cate, list) {
        override fun convert(holder: BaseViewHolder, item: ProductCategoryBean) {
            holder.setText(R.id.cate_name, item.pro_cate_name)
            holder.getView<RoundTextView>(R.id.cate_name).apply {
                isSelected = holder.layoutPosition == mSelectedIndex
                delegate.backgroundColor = if (isSelected) Color.parseColor("#19ff8200") else Cxt.getColor(R.color.color_FAFAFA)
                setTextColor(ContextCompat.getColor(context, if (isSelected) R.color.color_ff8200 else R.color.color_c8c8c8))
            }
        }
    }
}