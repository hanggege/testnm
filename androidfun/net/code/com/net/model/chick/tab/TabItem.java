package com.net.model.chick.tab;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/7/27
 */
public class TabItem implements Parcelable {
    public int needLoadFirst = 0;
    public String id;
    public String name;
    public String link;
    public String linkTitle;
    @Nullable
    public String icon0;
    @Nullable
    public String icon1;
    @Nullable
    public String animation;

    public TabItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TabItem(String id, String name, String link, String linkTitle) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.linkTitle = linkTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.link);
        dest.writeString(this.linkTitle);
        dest.writeInt(this.needLoadFirst);
    }

    private TabItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.link = in.readString();
        this.linkTitle = in.readString();
        this.needLoadFirst = in.readInt();
    }

    public static final Parcelable.Creator<TabItem> CREATOR = new Parcelable.Creator<TabItem>() {
        @Override
        public TabItem createFromParcel(Parcel source) {
            return new TabItem(source);
        }

        @Override
        public TabItem[] newArray(int size) {
            return new TabItem[size];
        }
    };
}
