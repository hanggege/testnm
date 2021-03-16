package com.mei.im.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.mei.wood.R
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.im.ui.adapter
 * @ClassName:      TagLabelAdapter
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/28 17:54
 * @UpdateUser:
 * @UpdateDate:     2020/6/28 17:54
 * @UpdateRemark:
 * @Version:
 */
class TagLabelAdapter(var context: Context, datas: ArrayList<String>) : TagAdapter<String>(datas) {
    override fun getView(parent: FlowLayout?, position: Int, item: String?): View {
        val iv: RelativeLayout = LayoutInflater.from(context).inflate(R.layout.chat_c2c_lable_item, parent, false) as RelativeLayout
        val txtItem: TextView = iv.findViewById(R.id.c2c_chat_info_text_label)
        txtItem.text = item
        return iv
    }
}