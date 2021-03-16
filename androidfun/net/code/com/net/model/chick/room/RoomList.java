package com.net.model.chick.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mei.orc.http.response.BaseResponse;
import com.mei.orc.util.json.JsonExtKt;
import com.net.model.room.RoomInfo;
import com.net.model.user.GroupInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zzw on 2019-12-11
 * Des:
 */
public class RoomList {
    public static class Response extends BaseResponse<RoomList> {

    }

    public boolean hasMore;
    private List<Map<String, Object>> list;
    public int nextPageNo;
    public String topSlogan;
    @Nullable
    public TestReport report;

    public List<BaseItem<Object>> getList() {
        List<BaseItem<Object>> itemList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            RoomItemType type = RoomItemType.parseValue((String) map.get("type"));
            String json = JsonExtKt.toJson(map.get("content"));
            Object ob;
            if (type == RoomItemType.TOP_BANNER) {
                ob = JsonExtKt.jsonToList(json == null ? "" : json, type.getCls());
            } else {
                ob = JsonExtKt.json2Obj(json == null ? "" : json, type.getCls());
            }
            if (ob != null) {
                BaseItem<Object> baseItem = new BaseItem<>();
                baseItem.type = type;
                baseItem.content = ob;
                itemList.add(baseItem);
            }
        }
        return itemList;
    }

    public static class BaseItem<T> {
        public RoomItemType type;
        @NonNull
        public T content;

        public RoomItemGridType gridType;
    }

    public static class BannerItemBean {
        public int seqId;
        public String action;
        public String image;
        public String title;
        public String remark; //目前只有快速咨询有，"新用户"/"老用户"
    }

    public static class HomePageNavigateBar {
        public String html;
        public String url;
        public int width;
        public int height;
        public int version;
        public int seqId;
    }

    public static class BroadcastItemBean {
        public String background;
        public long begin;
        public long broadcastId;
        public String channelId;
        public RoomInfo.CreateUser createUser;
        public int createUserId;
        public int duration;
        public String groupId;
        public String image;
        public int mode;
        public int onLineCount;
        @Nullable
        public GroupInfo groupInfo;
        public int publisherId;
        public String roomId;
        public String roomType;
        public String tag;
        public String title;
        public RoomInfo.CreateUser userInfo;
        public int userId;
        public int version;
        public String exclusivePriceTips;
        public long upstreamBegin;
    }

    public static class ProduceCateItemBean {
        public List<ProduceCateBeanItem> items;
        public String title;
        public int seqId;
    }

    public static class ProduceCateBeanItem {
        public int createUser;
        public String createTime;
        public int inUse;
        public int partitionId;
        public String partitionImage;
        public String partitionName;
        public int priority;
        public String updateTime;
        public int updateUser;
    }

    public static class ShortVideoItemBean {
        public String seqId;
        public String title;
        public int userId;
        public List<VideoTag> tags;
        public RoomInfo.CreateUser user;
        public String videoCoverUrl;
        public String videoGifUrl;
        public String videoUrl;
        public int videoWidth;
        public int videoHeight;

        public static class VideoTag {
            public String action;
            public String text;
            public String name;
            public int seqId;
        }
    }

    public static class ShortVideoTitle {
        public ShortVideoItemBean.VideoTag more;
        public String title;
        public List<ShortVideoItemBean.VideoTag> tags;
    }

    public static class SeparationTitle {
        public String title; //"知心达人·公开直播中"
        public String subTitle; //"连线300分钟，聊聊你的困惑，找到同频的知心达人",
        public String moreTitle; //"更多"
        public String moreAction; //"dove://edit_personal_info"
    }

    public static class RecommendPublisher {
        public String text; //"繁忙的生活节奏，让你陷入了吵架？矛盾？情绪失控？暴躁？为什么这么累？"
        public String title; //"推荐关注"
        public String subTitle; //"和知心达人连线聊聊\n让你的情绪在这里释放"
        public String action; //"dove://edit_personal_info"
        public Info recommend;

        public static class Info {
            public int publisherUserId; //29245690
            public String name; //"向不暖"
            public String avatar; //"https://img.meidongli.net/2020/04/09/b5d36b7f2268f0ab50b9a9463476cd62.jpg"
            public String action; //"dove://edit_personal_info"
            public String status; // BROADCASTING, 对应PublisherOnlineStatusEnum
        }
    }

    public static class FollowedPublisher {
        public String text; //"繁忙的生活节奏，让你陷入了吵架？矛盾？情绪失控？暴躁？为什么这么累？"
        public String title; //"推荐关注"
        public String subTitle; //"和知心达人连线聊聊\n让你的情绪在这里释放"
        public String moreTitle; //"查看更多>>"
        public String moreAction; //"dove://edit_personal_info"
        public List<RecommendPublisher.Info> myFollowList;
    }

    public static class UserGuide {
        public List<Info> list;

        public static class Info {
            public String title; //"如何咨询
            public String action; //"dove://edit_personal_info"
            public String icon;
            public String color; // #000000
            public boolean disappearAfterClick;
        }
    }

    public static class FooterLabel {
        public String imageUrl; //"https://res.meidongli.net/zhixin-workroom/images/android-index-logo@2x.png"
        public String text; //"知心已服务xxx用户，共计xxxx分钟"
        public String action; //"dove://edit_personal_info"
    }

    public static class OnlinePublisher {
        public int publisherUserId; //29245690
        public String name; //向不暖1
        public String avatar; //"https://img.meidongli.net/2020/04/09/b5d36b7f2268f0ab50b9a9463476cd62.jpg"
        public List<String> tagList;

        public String image; // 形象照
        public String background; // 背景图

        public String action; //dove://jump_personal_page_info?userId=29245690
    }

    public static class ScrollingMessage {
        public int durationSec; //5
        public List<UserGuide.Info> list;

        // 埋点相关
        public String globalId;
        public Float visibleScale;
    }

    public static class NavigateBarApp {
        public List<Info> list;

        public static class Info {
            public String name; //聊天神器
            public String icon; //"https://valar.ixiaolu.com/h5/zhixinli/tool/xingzuoyunshi.png?ver=2009091838"
            public String tipsIcon;
            public String action;// "dove://new_json_webview/{\"can_bounce\":0,\"can_refresh\":0,\"url\":\"https://zxl.meidongli.net/zhixin2/constellation.html\",\"appear_top_bar\":1,\"go_back_android\":0,\"show_title\":1,\"title\":\"星座运势\"}"
        }
    }

    public static class TestReport {
        public String title; //你的测评报告已生成
        public String text; //抑郁程度测评，测试你的抑郁程度
        public String image; //https://neptune-app.zhixinli.vip/pages/test/test.jpg
        public String buttonText; //登录查看
        public String action; //dove://user_login_check
    }
}
