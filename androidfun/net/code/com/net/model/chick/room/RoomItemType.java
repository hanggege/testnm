package com.net.model.chick.room;

import android.text.TextUtils;

import com.net.model.chick.room.RoomList.BannerItemBean;
import com.net.model.chick.room.RoomList.BroadcastItemBean;
import com.net.model.chick.room.RoomList.FollowedPublisher;
import com.net.model.chick.room.RoomList.FooterLabel;
import com.net.model.chick.room.RoomList.HomePageNavigateBar;
import com.net.model.chick.room.RoomList.NavigateBarApp;
import com.net.model.chick.room.RoomList.ProduceCateItemBean;
import com.net.model.chick.room.RoomList.RecommendPublisher;
import com.net.model.chick.room.RoomList.ScrollingMessage;
import com.net.model.chick.room.RoomList.SeparationTitle;
import com.net.model.chick.room.RoomList.ShortVideoItemBean;
import com.net.model.chick.room.RoomList.ShortVideoTitle;
import com.net.model.chick.room.RoomList.OnlinePublisher;
import com.net.model.chick.room.RoomList.UserGuide;

/**
 * Created by hang on 2020/7/13.
 */
//TOP_BANNER SCHEDULE_BROADCAST(0),PUBLIC_BROADCAST(1),BANNER(3),SPECIAL_BROADCAST(4),PRODUCT_CATE(5),EXCLUSIVE_BROADCAST(6),SHORT_VIDEO(7);
public enum RoomItemType {
    TOP_BANNER(BannerItemBean.class),
    NAVIGATE_BAR(HomePageNavigateBar.class),
    MODULE_SEPARATION(String.class),
    SHORT_VIDEO_WITH_TAG(ShortVideoTitle.class),
    SCHEDULE_BROADCAST(BroadcastItemBean.class),
    PUBLIC_BROADCAST(BroadcastItemBean.class),
    BANNER(BannerItemBean.class),
    BANNER_FILL(BannerItemBean.class),
    BANNER_WITH_HTML(HomePageNavigateBar.class),
    BANNER_WITH_URL(HomePageNavigateBar.class),
    SPECIAL_BROADCAST(BroadcastItemBean.class),
    PRODUCT_CATE(ProduceCateItemBean.class),
    EXCLUSIVE_BROADCAST(BroadcastItemBean.class),
    SHORT_VIDEO(ShortVideoItemBean.class),
    MODULE_SEPARATION_WITH_TITLE(SeparationTitle.class),
    PUBLISHER_RECOMMEND(RecommendPublisher.class),
    PUBLISHER_FOLLOWED(FollowedPublisher.class),
    USER_GUIDE(UserGuide.class),
    ONLINE_PUBLISHER(OnlinePublisher.class),
    SCROLLING_MESSAGE(ScrollingMessage.class),
    NAVIGATE_BAR_APP(NavigateBarApp.class),
    IMAGE_WITH_BOTTOM_TEXT(FooterLabel.class);

    private Class<?> cls;

    RoomItemType(Class<?> cls) {
        this.cls = cls;
    }

    public Class<?> getCls() {
        return cls;
    }

    public static RoomItemType parseValue(String type) {
        RoomItemType result = SCHEDULE_BROADCAST;
        for (RoomItemType value : RoomItemType.values()) {
            if (TextUtils.equals(value.name(), type)) {
                result = value;
                break;
            }
        }
        return result;
    }
}
