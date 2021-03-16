package com.mei.me.utils

import com.joker.model.BackResult
import com.joker.model.ErrResult

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/5.
 */

interface BindBack {
    fun bindFailure(result: ErrResult)

    fun bindSuccess(success: BackResult)
}
