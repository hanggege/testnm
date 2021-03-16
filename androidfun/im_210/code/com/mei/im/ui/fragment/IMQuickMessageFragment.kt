package com.mei.im.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.im.ADD_WORDS_KEY
import com.mei.im.MANAGER_WORDS_KEY
import com.mei.im.ui.IMAddCommonWordsActivityLauncher
import com.mei.im.ui.IMMangeComWordsActivityLauncher
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import kotlinx.android.synthetic.main.im_quick_msg_layout.*
import launcher.Boom

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/20
 */
class IMQuickMessageFragment : CustomSupportFragment() {

    @Boom
    var imChatId: String = ""
    var selectSendMsg: (String) -> Unit = { _ -> }

    val msgList = arrayListOf<String>()
    val msgAdapter by lazy {
        IMQuickMessageAdapter(msgList).apply {
            addHeaderView(headerView)
            setOnItemClickListener { _, _, position ->
                msgList.getIndexOrNull(position)?.let(selectSendMsg)
            }
        }
    }
    val headerView by lazy {
        im_quick_recycler.layoutInflaterKt(R.layout.im_quick_msg_header_layout).apply {
            findViewById<View>(R.id.msg_add_layout).setOnClickListener {
                activity?.startFragmentForResult(IMAddCommonWordsActivityLauncher.getIntentFrom(context), 116) { requestCode, data ->
                    if (requestCode == 116) {
                        val resultList: List<String> = data?.getStringArrayListExtra(ADD_WORDS_KEY).orEmpty()
                        if (resultList.isNotEmpty()) {
                            refreshMsg(resultList)
                        }
                    }
                }
            }
            findViewById<View>(R.id.msg_manager_layout).setOnClickListener {
                activity?.startFragmentForResult(IMMangeComWordsActivityLauncher.getIntentFrom(context, msgList), 100) { requestCode, data ->
                    if (requestCode == 100) {
                        val resultList: List<String> = data?.getStringArrayListExtra(MANAGER_WORDS_KEY).orEmpty()
                        refreshMsg(resultList)
                    }
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.im_quick_msg_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        im_quick_recycler.adapter = msgAdapter
    }

    fun refreshMsg(list: List<String>) {
        msgList.clear()
        msgList.addAll(list)
        if (isAdded) msgAdapter.notifyDataSetChanged()
    }
}


class IMQuickMessageAdapter(val list: MutableList<String>)
    : BaseQuickAdapter<String, BaseViewHolder>(R.layout.im_quick_adapter_item, list) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.quick_msg_tv, item)
    }

}