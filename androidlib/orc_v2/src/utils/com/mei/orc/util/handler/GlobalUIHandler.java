package com.mei.orc.util.handler;


import android.os.Handler;
import android.os.Looper;

import com.mei.orc.Cxt;

/**
 * Created by dt on 25/2/15.
 */
public class GlobalUIHandler extends Handler {
    private volatile static GlobalUIHandler instance = null;

    private GlobalUIHandler(Looper looper) {
        super(looper);
    }

    public static GlobalUIHandler getInstance() {
        if (instance == null) {
            synchronized (GlobalUIHandler.class) {
                if (instance == null) {
                    instance = new GlobalUIHandler(Cxt.get().getMainLooper());
                }
            }
        }
        return instance;
    }

    public static void postUi(Runnable runnable) {
        getInstance().post(runnable);
    }

    public static void remove(Runnable runnable) {
        getInstance().removeCallbacks(runnable);
    }

    public static void postUiDelay(Runnable runnable, long delay) {
        getInstance().postDelayed(runnable, delay);
    }

    public static void schedule(Runnable uiThreadTask, long delay) {
        postUiDelay(uiThreadTask, delay);
    }

}
