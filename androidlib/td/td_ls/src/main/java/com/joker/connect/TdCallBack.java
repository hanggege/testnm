package com.joker.connect;


import androidx.annotation.NonNull;

import com.joker.model.BackResult;
import com.joker.model.ErrResult;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public interface TdCallBack {
    void onSuccess(@NonNull BackResult success);

    void onFailure(@NonNull ErrResult errResult);
}
