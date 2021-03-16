package com.joker.support.listener;

import com.joker.support.TdAction;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/6.
 */

public interface TdNetAdapter {

    void requestNet(String url, TdAction<String> callBack);
}
