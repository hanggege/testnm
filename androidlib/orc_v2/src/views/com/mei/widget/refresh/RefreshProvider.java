package com.mei.widget.refresh;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/6/20.
 */

public interface RefreshProvider {

    /**
     * 触发距离
     */
    int provideOffsetDistance();

    /**
     * 是否置顶不动
     */
    boolean needStickTop();


}
