package com.mei.im.ui

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.im.ADD_WORDS_KEY
import com.mei.im.MANAGER_WORDS_KEY
import com.mei.im.ui.adapter.IMReplayRecleViewAdapter
import com.mei.im.ui.popup.CommonPopupWindow
import com.mei.im.ui.view.RecycleViewDivider
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.widget.actionbar.defaultRightTextView
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.chat.CommandPhraseModifyRequest
import kotlinx.android.synthetic.main.im_activity_mange_com_words.*
import launcher.Boom
import launcher.MakeResult
import launcher.MulField


/**
 * 常用语言管理界面
 */
@MakeResult(includeStartForResult = true)
class IMMangeComWordsActivity : MeiCustomBarActivity() {
    @MulField
    @Boom
    var mListValue: ArrayList<String> = ArrayList()
    private var mRawData: ArrayList<String> = ArrayList()
    val mAdapter by lazy {
        IMReplayRecleViewAdapter(mListValue)

    }
    private lateinit var linearLM: LinearLayoutManager
    private var rightText = ""
    private val itemDecoration by lazy {
        RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, Cxt.getColor(R.color.color_ffe8e8e8))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IMMangeComWordsActivityLauncher.bind(this)
        setContentView(R.layout.im_activity_mange_com_words)
        initView()
    }

    /**
     * 初始化UI
     */
    private fun initView() {
        showBottomLine(false)
        rightText = getString(R.string.sort)
        mRawData.clear()
        mRawData.addAll(mListValue)
        title = Cxt.getStr(R.string.manager_command_phrase)
        setCustomView(defaultRightTextView(rightText, Cxt.getColor(R.color.color_333333)), 1)
        bt_add_common_words.setOnClickListener {
            activity.startFragmentForResult(IMAddCommonWordsActivityLauncher.getIntentFrom(activity), 101) { requestCode, data ->
                if (requestCode == 101) {
                    val resultList: List<String> = data?.getStringArrayListExtra(ADD_WORDS_KEY).orEmpty()
                    if (resultList.isNotEmpty()) {
                        mListValue.clear()
                        mListValue.addAll(resultList)
                        mRawData.clear()
                        mRawData.addAll(resultList)
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        linearLM = LinearLayoutManager(this)
        common_word_recy.layoutManager = linearLM
        common_word_recy.addItemDecoration(itemDecoration)
        // 拖拽监听
        val listener: OnItemDragListener = object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                val holder = viewHolder as BaseViewHolder

                // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor: Int = Color.WHITE
                val endColor: Int = Color.rgb(245, 245, 245)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
            }

            override fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int) {
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                val holder = viewHolder as BaseViewHolder
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor: Int = Color.rgb(245, 245, 245)
                val endColor: Int = Color.WHITE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
            }
        }
        mAdapter.draggableModule.setOnItemDragListener(listener)
        mAdapter.draggableModule.itemTouchHelperCallback.setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
        common_word_recy.adapter = mAdapter
        meiTitleBar.rightContainer.setOnClickListener {
            if (rightText == getString(R.string.sort)) {
                rightText = getString(R.string.complete)
                mAdapter.draggableModule.isDragEnabled = true
                bt_add_common_words.visibility = View.GONE
                common_word_recy.setPadding(dip(15), 0, dip(15), dip(0))
            } else {
                //不相等就上传
                if (!isListEq()) {
                    requestUpdate()
                } else {
                    UIToast.toast("没有修改内容")
                    setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(MANAGER_WORDS_KEY, mListValue))
                }
                mAdapter.draggableModule.isDragEnabled = false
                rightText = getString(R.string.sort)
                bt_add_common_words.visibility = View.VISIBLE
                common_word_recy.setPadding(dip(15), 0, dip(15), dip(90))

            }
            mAdapter.notifyDataSetChanged()
            setCustomView(defaultRightTextView(rightText, Cxt.getColor(R.color.color_333333)), 1)
        }

        mAdapter.setOnItemChildLongClickListener { _, _, position ->
            if (rightText != getString(R.string.complete)) {
                showDeletePopWindow(position)
            }
            true
        }
        mAdapter.setOnItemChildClickListener { _, _, position ->
            if (rightText != getString(R.string.complete)) {
                //编辑页面
                startFragmentForResult(IMAddCommonWordsActivityLauncher.getIntentFrom(activity, position, mListValue), 100) { requestCode, data ->
                    if (requestCode == 100) {
                        val resultValue = data?.getStringArrayListExtra(ADD_WORDS_KEY).orEmpty()
                        if (resultValue.isNotEmpty()) {
                            mRawData.clear()
                            mListValue.clear()
                            mRawData.addAll(resultValue)
                            mListValue.addAll(resultValue)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

    }

    /**
     * 返回按键
     */
    override fun onClickLeft(view: View) {
        if (isListEq()) {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(MANAGER_WORDS_KEY, mListValue))
            finish()
        } else {
            NormalDialogLauncher.newInstance().showDialog(this, "修改的内容未保存，是否确定退出", back = {
                when (it) {
                    DISSMISS_DO_OK -> {
                        setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(MANAGER_WORDS_KEY, mRawData))
                        finish()
                    }
                }
            })
        }
    }

    /**
     * 更新顺序
     */
    private fun requestUpdate() {
        showLoading(true)
        apiSpiceMgr.executeKt(CommandPhraseModifyRequest(mListValue),
                success = {
                    it?.let {
                        mRawData.clear()
                        mRawData.addAll(it.data.chatPhrases)
                        setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(MANAGER_WORDS_KEY, it.data.chatPhrases))
                    }
                }, failure = {}, finish = {
            showLoading(false)
        })
    }

    override fun onBackPressed() {
        if (isListEq()) {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(MANAGER_WORDS_KEY, mListValue))
            finish()
            super.onBackPressed()
        } else {
            NormalDialogLauncher.newInstance().showDialog(this, "修改的内容未保存，是否确定退出", back = {
                when (it) {
                    DISSMISS_DO_OK -> {
                        setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(MANAGER_WORDS_KEY, mRawData))
                        finish()
                    }
                }
            })
        }


    }

    /**
     * 集合数据相等顺序相等
     */
    private fun isListEq(): Boolean {
        for (index in mRawData.indices) {
            if (mRawData[index] != mListValue[index])
                return false
        }
        return true
    }

    /**
     * popWindow
     * 功能：删除
     */
    private var mPopupWindow: CommonPopupWindow? = null
    private fun showDeletePopWindow(position: Int) {
        mPopupWindow = CommonPopupWindow.Builder(activity).setView(R.layout.im_replay_pop_delete_words)
                .setWidthAndHeight(dip(65), dip(47))
                .setViewOnclickListener { view, _ ->
                    view?.findViewById<RelativeLayout>(R.id.im_replay_pop_delete)?.setOnClickNotDoubleListener {
                        deleteWord(position)
                    }
                }
                .setAnimationStyle(R.style.AnimDown).create()
        val firstView = linearLM.findViewByPosition(position)
        firstView?.let {
            val textV = firstView.findViewById<TextView>(R.id.im_reply_item_txt)
            val txt = textV.text.toString().trim()
            val textPaint = textV?.paint
            var textPaintWidth = textPaint?.measureText(txt)?.toInt()
            var height = textV?.height?.div(2)
            val location = IntArray(2)
            textV.getLocationOnScreen(location)
            if (textPaintWidth != null) {
                if (textPaintWidth > screenWidth) {
                    textPaintWidth = screenWidth / 2
                    height = 0
                }
            }
            val bottomY = bt_add_common_words.y
            val heightY = if (location[1] >= bottomY) {
                location[1]
            } else {
                if ((location[1] + textV.height + 5) > bottomY) {
                    location[1]
                } else {
                    (location[1] + textV.height + 5)
                }
            }
            height?.let {
                textPaintWidth?.div(2)?.let { x ->
                    mPopupWindow?.showAtLocation(textV, Gravity.NO_GRAVITY, x, heightY)
                }
            }

        }
    }

    /**
     * 删除选择消息
     * 成功后更新
     */
    private fun deleteWord(position: Int) {
        mListValue.removeAt(position)
        mRawData.removeAt(position)
        setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(MANAGER_WORDS_KEY, mListValue))
        apiSpiceMgr.executeKt(CommandPhraseModifyRequest(mListValue),
                success = {
                    mAdapter.notifyDataSetChanged()
                    UIToast.toast("删除成功")
                    mPopupWindow?.dismiss()
                }, failure = {
            UIToast.toast("删除失败")
            mPopupWindow?.dismiss()
        })
    }

}
