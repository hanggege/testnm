package com.mei.base.weight.xiaolurefresh;


import android.os.Handler;
import android.os.Looper;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/26.
 */

public class UIHandler extends Handler {
    private volatile static UIHandler instance = null;

    private UIHandler(Looper looper) {
        super(looper);
    }

    public static UIHandler getInstance() {
        if (instance == null) {
            synchronized (UIHandler.class) {
                if (instance == null) {
                    instance = new UIHandler(Looper.getMainLooper());
                }
            }
        }
        return instance;
    }

    public static void postUi(Runnable runnable) {
        getInstance().post(runnable);
    }

    public static void postUiDelay(Runnable runnable, long delay) {
        getInstance().postDelayed(runnable, delay);
    }
}
