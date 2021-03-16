package com.mei.userinfocomplement.step

import android.graphics.Rect
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.me.activity.CHINESE_PATTERN_COPY
import com.mei.me.ext.getPatternStr
import com.mei.orc.ext.addTextChangedListener
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.orc.ext.subListByIndex
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.keyboard.hideKeyBoard
import com.mei.widget.gradient.GradientLinearLayout
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.extensions.appendSpannable
import com.mei.wood.extensions.executeToastKt
import com.net.model.chick.user.NicknameBatchInfo
import com.net.network.chick.user.GetNicknameBatchRequest
import kotlinx.android.synthetic.main.fragment_complement_step_four.*
import kotlinx.android.synthetic.main.include_net_error_layout.*

/**
 * ComplementStepFourFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-16
 */
class ComplementStepFourFragment(private val loginUserId: Int, private val sessionId: String) : ComplementBaseFragment() {

    companion object {
        private const val NO_POSITION = -1
    }

    override val step = 4
    override val title = "选个昵称"
    override val layoutId = R.layout.fragment_complement_step_four

    override fun titleView(): TextView {
        return complement_step_four_title
    }

    var nameBack: (String, View) -> Unit = { _, _ -> }

    // 要记录编辑框聚焦时，之前holder的选择的位置
    private var adapterPrePos = NO_POSITION
    private var initName = ""

    private val nameList = arrayListOf<NameData>()
    private val nameAdapter by lazy {
        NameAdapter(nameList).apply {
            setOnItemClickListener { _, _, position ->
                if (selectedPos == position) {
                    val editText = complement_step_four_edit.text.toString()
                    initName = if (editText.isEmpty()) "" else editText
                    selectedPos = NO_POSITION
                } else {
                    initName = nameList[position].name
                    selectedPos = position
                }
                complement_step_four_edit.clearFocus()
                complement_step_four_edit.hideKeyBoard()
                notifyDataSetChanged()
                refreshNextButtonStyle()
            }
        }
    }

    override fun initView() {
        complement_step_four_next.paint.isFakeBoldText = true

        complement_step_four_sub_title.text = buildSpannedString {
            appendSpannable("知心是一个随时随地倾诉解惑的平台。\n为", arrayListOf(
                    ForegroundColorSpan(ContextCompat.getColor(complement_step_four_sub_title.context, R.color.color_666666)),
                    StyleSpan(Typeface.NORMAL)))
            appendSpannable("保护隐私", arrayListOf(
                    ForegroundColorSpan(ContextCompat.getColor(complement_step_four_sub_title.context, R.color.color_333333)),
                    StyleSpan(Typeface.BOLD)))
            appendSpannable("，给自己选个有趣的昵称吧～", arrayListOf(
                    ForegroundColorSpan(ContextCompat.getColor(complement_step_four_sub_title.context, R.color.color_666666)),
                    StyleSpan(Typeface.NORMAL)))
        }

        complement_step_four_rv.addItemDecoration(Decoration())
        complement_step_four_rv.adapter = nameAdapter
    }

    override fun initListener() {
        net_error_layout.setOnBtnClick { requestData() }
        complement_step_four_refresh.setOnClickListener {
            adapterPrePos = NO_POSITION
            nameAdapter.selectedPos = NO_POSITION
            val editText = complement_step_four_edit.text.toString()
            initName = if (editText.isNotEmpty()) editText else ""
            requestData()
        }

        complement_step_four_edit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val editText = complement_step_four_edit.text.toString()
                if (editText.isNotEmpty()) { //当编辑框有文字时清除推荐昵称焦点
                    adapterPrePos = nameAdapter.selectedPos
                    nameAdapter.selectedPos = NO_POSITION
                    nameAdapter.notifyDataSetChanged()
                    initName = editText
                }
                refreshNextButtonStyle()
            } else {
                v.hideKeyBoard()
            }
        }

        complement_step_four_edit.addTextChangedListener {
            initName = it
            if (it.isNotEmpty()) {
                // 编辑框为空时，聚焦到编辑框不清除推荐昵称焦点
                // 当编辑框有文字输入时再清除推荐昵称焦点
                if (adapterPrePos == NO_POSITION) {
                    adapterPrePos = nameAdapter.selectedPos
                    nameAdapter.selectedPos = NO_POSITION
                    nameAdapter.notifyDataSetChanged()
                }
            } else {
                if (adapterPrePos != NO_POSITION) {
                    initName = nameList[adapterPrePos].name
                }
                nameAdapter.selectedPos = adapterPrePos
                adapterPrePos = NO_POSITION
                nameAdapter.notifyDataSetChanged()
            }
            refreshNextButtonStyle()
        }

        complement_step_four_next.setOnClickListener {
            if (initName.isNotEmpty()) {
                if (initName.length > 8) {
                    UIToast.toast("昵称不能超过8个字")
                    return@setOnClickListener
                }
                if (getPatternStr(CHINESE_PATTERN_COPY, initName) != initName) {
                    UIToast.toast("昵称不能包含特殊字符")
                    return@setOnClickListener
                }
                nameBack(initName, it)
            }
        }
    }

    override fun requestData() {
        apiSpiceMgr.executeToastKt(GetNicknameBatchRequest(loginUserId, sessionId), success = {
            if (it.isSuccess && it.data != null) {
                refreshUiData(it.data)
                net_error_layout.isVisible = false
            } else {
                net_error_layout.isVisible = true
            }
        }, failure = {
            net_error_layout.isVisible = true
        })
    }

    private fun refreshUiData(batchInfo: NicknameBatchInfo) {
        complement_step_four_avatar.glideDisplay(batchInfo.avatar, R.drawable.default_avatar_50)
        nameList.clear()
        batchInfo.nicknameWx?.apply { nameList.add(NameData(this, true)) }
        val subNum = if (batchInfo.nicknameWx == null) 4 else 3
        batchInfo.list.subListByIndex(subNum).forEach {
            nameList.add(NameData(it))
        }
        nameAdapter.notifyDataSetChanged()
        refreshNextButtonStyle()
    }

    private fun refreshNextButtonStyle() {
        complement_step_four_next.setTextColor(ContextCompat.getColor(complement_step_four_next.context,
                if (initName.isEmpty()) R.color.color_c8c8c8 else android.R.color.white
        ))
        complement_step_four_next.delegate.applyViewDelegate {
            radius = 27.5f.dp2px
            if (initName.isEmpty()) {
                startColor = ContextCompat.getColor(context, R.color.color_f6f6f6)
            } else {
                startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
            }
        }
    }

    inner class NameAdapter(list: MutableList<NameData>)
        : BaseQuickAdapter<NameData, BaseViewHolder>(R.layout.item_complement_name, list) {
        var selectedPos = NO_POSITION
        override fun convert(holder: BaseViewHolder, item: NameData) {
            val isSelected = selectedPos == holder.layoutPosition
            holder.getView<GradientLinearLayout>(R.id.complement_name).delegate.applyViewDelegate {
                radius = 19.dp
                if (isSelected) {
                    startColor = ContextCompat.getColor(context, R.color.color_FD955A)
                    endColor = ContextCompat.getColor(context, R.color.color_FD645D)
                } else {
                    strokeWidth = 0.5f.dp2px
                    strokeStartColor = ContextCompat.getColor(context, R.color.color_c8c8c8)
                }
            }
            holder.getView<ImageView>(R.id.complement_name_icon).apply {
                this.isVisible = item.isWx
                if (item.isWx) {
                    glideDisplay(if (isSelected) R.drawable.icon_wechat else R.drawable.icon_wechat_unselected)
                }
            }
            holder.getView<TextView>(R.id.complement_name_text).apply {
                text = item.name
                setTextColor(ContextCompat.getColor(context, if (isSelected) R.color.colorWhite else R.color.color_c8c8c8))
            }
        }
    }

    inner class Decoration : RecyclerView.ItemDecoration() {
        private val margin10dp = 10.dp.toInt()
        private val margin14dp = 14.dp.toInt()
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
            if (position % 2 == 0) {
                outRect.right = margin10dp
            } else {
                outRect.left = margin10dp
            }
            if (position > 1) {
                outRect.top = margin14dp
            }
        }
    }

    class NameData(val name: String, val isWx: Boolean = false)
}