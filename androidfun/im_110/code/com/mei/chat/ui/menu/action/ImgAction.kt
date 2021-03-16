package com.mei.chat.ui.menu.action

import com.mei.picker.pickerImage
import com.mei.wood.R

/**
 *  Created by zzw on 2019/3/18
 *  Des:图片
 */


abstract class ImgAction : BaseAction(R.drawable.im_menu_album,
        "图片") {

    abstract fun selectImg(images: List<String>)


    override fun onClick() {
        fragment?.activity?.pickerImage(1) {
            selectImg(it)
        }
    }

}