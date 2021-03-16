package com.net.model.newhome;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by LingYun on 2017/7/31.
 * 安卓首屏推荐页拆分为四个版本，此为试验二、三版本的网络请求返回数据
 */

public class Tab_content_test {

    public static class EliteCate implements Parcelable {
        public String name;
        public int cate_id;
        public String type;

        public boolean isWeixinType() {
            return TextUtils.equals(type, "wechat");
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeInt(this.cate_id);
            dest.writeString(this.type);
        }

        public EliteCate() {
        }

        protected EliteCate(Parcel in) {
            this.name = in.readString();
            this.cate_id = in.readInt();
            this.type = in.readString();
        }

        public static final Creator<EliteCate> CREATOR = new Creator<EliteCate>() {
            @Override
            public EliteCate createFromParcel(Parcel source) {
                return new EliteCate(source);
            }

            @Override
            public EliteCate[] newArray(int size) {
                return new EliteCate[size];
            }
        };
    }


    public static class BannerBean {

        public int banner_id;
        public String photo;
        public String url;
        public String title;
        public Object ios_version;
        public Object andriod_version;
        public int location;
        public boolean is_ebook;
    }

    public static class EntranceBean {

        public int entrance_id;
        public String photo;
        public String title;
        public String url;
        public String url_b;
        public int new_icon;
    }

    public static class BroadcastListBean {

        public int broadcast_id;
        public String room_id;
        public String subject;
        public int mentor_user_id;
        public String mentor_user_name;
        public String mentor_user_avatar;
        public String begin_time;
        public String end_time;
        public int total;
        public String url;
        public int broadcast_state;
        public int begin_time_left_sec;
        public int end_time_left_sec;
        public String big_cover_url;
        public String small_cover_url;
        public String pro_cate_name;
        public String subject_keyword;
        public boolean hasSpace = false;//当有多个数据的时候的分割线
        public boolean hasBottom = false;//当没有【更多直播】时，需要优化UI下边距离
        public String cate_color; // 直播分类颜色
        public String cate_name; //分类名称
        public boolean isRoom = false;//判断当前直播是否是room直播间
        public int pro_id;
        public int pro_sub_id;
        public String broadcast_cover_url;
        public List<Tab_content_v4.User> recent_in_watch_user_list;
    }
}
