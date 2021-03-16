package com.mei.live.manager

import androidx.annotation.DrawableRes
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-25
 */

@DrawableRes
fun Int?.genderAvatar(isPublisher: Boolean? = true): Int = when (this) {
    0 -> if (isPublisher == true) R.drawable.personal_img_female_default else R.drawable.default_female_head
    1 -> if (isPublisher == true) R.drawable.personal_img_male_default else R.drawable.default_male_head
    else -> R.drawable.default_avatar_50
}

@DrawableRes
fun Int?.genderPersonalImage(): Int = when (this) {
    0 -> R.drawable.personal_img_female_default
    1 -> R.drawable.personal_img_male_default
    else -> R.drawable.personal_img_male_default
}