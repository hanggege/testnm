package com.mei.wood.extensions

import androidx.fragment.app.FragmentActivity
import com.mei.orc.callback.Callback
import com.mei.orc.dialog.BottomSelectFragment
import com.mei.orc.dialog.Item
import com.mei.orc.dialog.darkGray

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/10
 */

fun FragmentActivity.showCountryCode(back: Callback<String>) {
    val items = arrayListOf("中国 + 86",
            "中国香港 + 852",
            "台湾 + 886",
            "美国 + 1",
            "马来西亚 + 60",
            "加拿大 + 1",
            "澳大利亚 + 61",
            "日本 + 81",
            "英国 + 44",
            "新加坡 + 65",
            "韩国 + 82",
            "法国 + 33")
    BottomSelectFragment()
            .addItemList<String>(items.toList()) { index, t -> Item(index, t, darkGray) }
            .setItemClickListener(Callback {
                if (it.type == items.size - 1) {
                    back.onCallback("")
                } else {
                    var item = it.display
                    item = item.substring(item.lastIndexOf('+') + 1).trim { it <= ' ' }
                    back.onCallback("+$item")
                }
            })
            .show(supportFragmentManager)


}