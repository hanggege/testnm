package com.mei.widget.refresh;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/6/20.
 */

public interface FooterRefreshProvider extends RefreshProvider {

    /**
     * 拖动到的位置和头距离顶部margin
     *
     * @param percent              离触发的百分比
     * @param currDragPosition     当前移动的位置
     * @param marginBottomDistance headView距离顶部的位置
     * @param allDistance          触发位置
     */
    void positionChange(float percent, int currDragPosition, int marginBottomDistance, int allDistance);

    void setLoadMore(boolean loadMore);


}
