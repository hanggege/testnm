package com.joker.support.listener;

import android.content.Context;
import android.graphics.Bitmap;

import com.joker.support.TdAction;

/**
 * 佛祖保佑         永无BUG
 * 图片加载适配器，目前用于分享取图片
 *
 * @author Created by joker on 2017/5/3.
 */

public interface TdImageAdapter {

    void getImgBitmap(Context context, String url, TdAction<Bitmap> callback);

}
