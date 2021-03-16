package com.joker.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.joker.support.listener.TdHandlerListener;
import com.joker.support.listener.TdLifecycleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public class BaseActivity extends Activity implements TdHandlerListener {

    private final List<TdLifecycleListener> lifeLists = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        synchronized (lifeLists) {
            if (lifeLists.size() > 0) {
                for (TdLifecycleListener life : lifeLists) {
                    life.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }


    @Override
    public void registerLifecycle(TdLifecycleListener listener) {
        synchronized (lifeLists) {
            WeixinEvent.getInstance().clear();
            lifeLists.add(listener);
        }
    }

    @Override
    public void unregisterLifecycle(TdLifecycleListener listener) {
        synchronized (lifeLists) {
            lifeLists.remove(listener);
        }
    }

    @Override
    public boolean isRegisted(TdLifecycleListener listener) {
        synchronized (lifeLists) {
            return lifeLists.contains(listener);
        }
    }
}
