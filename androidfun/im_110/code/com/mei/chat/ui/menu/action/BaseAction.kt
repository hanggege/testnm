package com.mei.chat.ui.menu.action

import com.mei.chat.ui.menu.ImMenuFragment

/**
 * Created by zzw on 2019/3/18
 * Des:菜单基类  如果使用startActivityForResult必须传递fragment并且重写fragment的onActivityResult函数调用onActivityResult
 */
open class BaseAction(val iconRes: Int, val title: String) {

    var expand: Any? = null
    var fragment: ImMenuFragment? = null

    /**
     * index用于生成requestCode
     */
    var index: Int = 0



    open fun onClick() {

    }

}
