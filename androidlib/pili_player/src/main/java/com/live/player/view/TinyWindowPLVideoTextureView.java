package com.live.player.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.lang.reflect.Method;

/**
 * 小窗口播放专用视频播放器。
 * 自带的播放器在view被remove掉的时候执行了播放器的销毁操作，使用这个在remove掉的时候避免被销毁。
 * 没有小窗口播放的话还是推荐使用自带播放器
 *
 * @author caowei
 * @blog https://646030315.github.io
 * @email 646030315@qq.com
 * Created on 18-1-12.
 */

public class TinyWindowPLVideoTextureView extends PLVideoTextureView {


    public TinyWindowPLVideoTextureView(Context context) {
        super(context);
    }

    public TinyWindowPLVideoTextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TinyWindowPLVideoTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDetachedFromWindow() {
        try {
            invokeMethod(this, "clearCachedLayoutMode");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Object invokeMethod(Object obj, String methodName) throws Exception {
        Class<?> parentClass = obj.getClass();
        Exception exp = null;
        while (parentClass != null) {
            try {
                Method method = parentClass.getDeclaredMethod(methodName);
                method.setAccessible(true);
                return method.invoke(obj);
            } catch (Exception e) {
                if (exp == null) {
                    exp = e;
                }
            }
            parentClass = parentClass.getSuperclass();
        }
        throw exp;
    }

    public void destroyPlayerView() {
        try {
            onDetachedFromWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getTextureView().getSurfaceTexture().detachFromGLContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            invokeMethod(this, "d");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
