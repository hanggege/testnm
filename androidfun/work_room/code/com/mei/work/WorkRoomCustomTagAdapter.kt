package com.mei.work

import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.NormalDialogLauncher
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

/**
 *
 * @author Created by lenna on 2020/7/22
 */
class WorkRoomCustomTagAdapter(var tags: List<String>, var refresh: (count: Int) -> Unit = {}) : TagAdapter<String>(tags) {

    override fun getView(parent: FlowLayout?, position: Int, t: String?): View? {
        val view = parent?.layoutInflaterKt(R.layout.work_room_edit_tag_item)
        view?.findViewById<TextView>(R.id.tag_text)?.text = t
        view?.findViewById<TextView>(R.id.tag_text)?.setOnClickListener { v ->
            t?.let { item ->
                NormalDialogLauncher.newInstance().showDialog((v.context as FragmentActivity), "确定是否删除标签？", back = {
                    when (it) {
                        DISSMISS_DO_OK -> {
                            removeItem(item)
                        }
                    }
                })
            }

        }
        return view
    }

    private fun removeItem(item: String) {
        (tags as ArrayList<String>).remove(item)
        notifyDataChanged()
        refresh(10 - tags.size)
        UIToast.toast("删除成功")
    }

    fun addItem(item: String) {
        (tags as ArrayList<String>).add(item)
        notifyDataChanged()
        refresh(10 - tags.size)
        UIToast.toast("添加成功")
    }


}