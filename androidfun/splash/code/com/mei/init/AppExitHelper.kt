package com.mei.init

import android.app.Activity
import com.mei.orc.event.clearAllAction
import com.mei.wood.dialog.clearDialogDialog

/**
 * Created by joker on 16/12/5.
 */


/**
 * 退出app需要释放资源
 */
fun Activity.exitApp() {
    //目前用不到这个玩意
//    EliteDAO.getInstance(this).destroyDB()
    clearAllAction()
    clearDialogDialog()
}
