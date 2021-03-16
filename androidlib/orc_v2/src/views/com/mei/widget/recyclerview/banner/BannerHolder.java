package com.mei.widget.recyclerview.banner;

import android.content.Context;
import android.view.View;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/9
 */
public interface BannerHolder<T> {

    View createView(Context context);

    void updateUI(View item, int position, T data);

    default void updateCertainUI(View view, int position, T data){ }

}
