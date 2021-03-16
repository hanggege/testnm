package com.mei.wood.ui.view

import android.graphics.drawable.Drawable
import com.mei.orc.ui.loading.LoadingToggle

/**
 * Created by Ling on 2018/6/4.
 *
 * @描述：
 */
interface XiaoluLoadingToggle : LoadingToggle {

    /**
     * 加载动画是否覆盖底部界面（暂时只给xiaolu用）
     */
    fun showLoadingCover()
}
