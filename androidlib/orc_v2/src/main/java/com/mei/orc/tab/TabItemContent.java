package com.mei.orc.tab;

/**
 * Created by guoyufeng on 21/7/15.
 */
public interface TabItemContent {

    void onSelect();

    void onReSelect();//当前item被再次点击

    void onDeSelect();

}
