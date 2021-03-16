package com.joker.support.listener;

import android.content.Intent;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public interface TdLifecycleListener {

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
