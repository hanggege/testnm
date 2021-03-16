package com.mei.wood.web;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/30
 */
public class MeiWebData implements Parcelable {
    public String url;
    public String title;
    public ArrayList<String> cookies;

    /**
     * 以下数据 1是true 0为false
     **/
    public int can_refresh;// 是否能下拉刷新
    public int appear_top_bar;//是否显示顶部
    public int show_refresh_button;//是否显示右上角刷新按钮
    public int go_back_android;//此参数是为了有时返回操作需要web来处理，包括但不限于禁止返回、返回确认
    public int transparent_status_bar; //是否沉浸式状态栏
    /**
     * 其它页面调用{@link com.mei.wood.ext.AmanLink.URL#close_webview}时需要刷新
     **/
    public int need_reload_web = 0;

    public int hide_nav_bottomline = 1;//是否隐藏中间的分割线
    public int show_title = 0;//是否显示Title
    public int not_show_loading = 0;//是否不进行loading

    public MeiWebData() {
    }

    /**
     * 普通webview
     */
    public MeiWebData(String url, String title) {
        this(url, title, null, 0, 1, 1, 0, 1, 0, 0);
    }

    /**
     * 带cookie的 webview
     */
    public MeiWebData(String url, String title, ArrayList<String> cookies) {
        this(url, title, cookies, 0, 1, 1, 0, 1, 0, 0);
    }

    public MeiWebData(String url, int appear_top_bar) {
        this(url, "", null, 0, appear_top_bar, 0, 0, 1, 0, 0);
    }

    public MeiWebData(String url, boolean isTransparentStatusBar) {
        this(url, "", null, 0, 0, 0, 0, 1, 0, isTransparentStatusBar ? 1 : 0);
    }


    /**
     * 带右边的刷新按钮控制 webview
     */
    public MeiWebData(String url, String title, boolean refresh) {
        this(url, title, null, 0, 1, refresh ? 1 : 0, 0, 1, 0, 0);
    }


    public MeiWebData(String url, String title, ArrayList<String> cookies, int can_refresh, int appear_top_bar, int show_refresh_button, int go_back_android, int show_title, int hide_nav_bottomline, int transparent_status_bar) {
        this.url = url;
        this.title = title;
        this.cookies = cookies;
        this.can_refresh = can_refresh;
        this.appear_top_bar = appear_top_bar;
        this.show_refresh_button = show_refresh_button;
        this.go_back_android = go_back_android;
        this.show_title = show_title;
        this.hide_nav_bottomline = hide_nav_bottomline;
        this.transparent_status_bar = transparent_status_bar;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    protected MeiWebData(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.cookies = in.createStringArrayList();
        this.can_refresh = in.readInt();
        this.appear_top_bar = in.readInt();
        this.show_refresh_button = in.readInt();
        this.go_back_android = in.readInt();
        this.transparent_status_bar = in.readInt();
        this.need_reload_web = in.readInt();
        this.hide_nav_bottomline = in.readInt();
        this.show_title = in.readInt();
        this.not_show_loading = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeStringList(this.cookies);
        dest.writeInt(this.can_refresh);
        dest.writeInt(this.appear_top_bar);
        dest.writeInt(this.show_refresh_button);
        dest.writeInt(this.go_back_android);
        dest.writeInt(this.transparent_status_bar);
        dest.writeInt(this.need_reload_web);
        dest.writeInt(this.hide_nav_bottomline);
        dest.writeInt(this.show_title);
        dest.writeInt(this.not_show_loading);
    }

    public static final Creator<MeiWebData> CREATOR = new Creator<MeiWebData>() {
        @Override
        public MeiWebData createFromParcel(Parcel source) {
            return new MeiWebData(source);
        }

        @Override
        public MeiWebData[] newArray(int size) {
            return new MeiWebData[size];
        }
    };
}
