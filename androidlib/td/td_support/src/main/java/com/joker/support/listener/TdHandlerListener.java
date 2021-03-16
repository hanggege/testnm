package com.joker.support.listener;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/28.
 */

public interface TdHandlerListener {

    void registerLifecycle(TdLifecycleListener listener);

    void unregisterLifecycle(TdLifecycleListener listener);

    boolean isRegisted(TdLifecycleListener listener);
}
