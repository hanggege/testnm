package com.joker.support;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.util.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/28.
 */

public class WeixinEvent extends Observable {

    private volatile static WeixinEvent instance;

    private WeixinEvent() {
    }

    public static WeixinEvent getInstance() {
        if (instance == null) {
            synchronized (WeixinEvent.class) {
                if (instance == null) {
                    instance = new WeixinEvent();
                }
            }
        }
        return instance;
    }

    public void postAction(BaseResp resp) {
        setChanged();
        notifyObservers(resp);
    }

    public void clear() {
        instance = null;
    }
}
