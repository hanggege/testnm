package com.pili;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.live.player.module.ListenInfo;
import com.live.stream.module.StreamInfo;
import com.mei.orc.util.json.JsonExtKt;
import com.net.model.broadcast.PlayType;

/**
 * Created by guoyufeng on 11/9/15.
 */

public class PlayInfo implements StreamInfo, ListenInfo, Cloneable {

    public final static PlayInfo NONE = new PlayInfo().playType(PlayType.NONE);

    public static PlayInfo Builder() {
        return new PlayInfo();
    }

    @PlayType
    public String mPlayType = PlayType.NONE;
    public String mUrl = "";
    public int mMentorUserId = 0;
    public String mMentorAvatar = "";
    public String mTitle = "";
    public String mSubTitle = "";
    public String mBackgroundColor = "";
    public int channelId = 0;
    public int mAudioId = 0;
    public int groupId = 0;
    public int startPosition = 0;//音频开始位置

    private boolean mIsPlaying = false;

    public Object extraData = null;

    public PlayInfo playType(@PlayType String mPlayType) {
        this.mPlayType = mPlayType;
        return this;
    }

    public PlayInfo url(String mUrl) {
        this.mUrl = mUrl;
        return this;
    }

    public PlayInfo mentorAvatar(String mMentorAvatar) {
        this.mMentorAvatar = mMentorAvatar;
        return this;
    }

    public PlayInfo title(String mTitle) {
        this.mTitle = mTitle;
        return this;
    }

    public PlayInfo subTitle(String mSubTitle) {
        this.mSubTitle = mSubTitle;
        return this;
    }

    public PlayInfo mentorUserId(int mMentorUserId) {
        this.mMentorUserId = mMentorUserId;
        return this;
    }

    public PlayInfo audioId(int mAudioId) {
        this.mAudioId = mAudioId;
        return this;
    }

    public PlayInfo channelId(int channelId) {
        this.channelId = channelId;
        return this;
    }

    public PlayInfo groupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    public PlayInfo setStartPosition(int startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    public PlayInfo setPlaying(boolean mPlaying) {
        this.mIsPlaying = mPlaying;
        return this;
    }

    public PlayInfo backgroundColor(String mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        return this;
    }

    public PlayInfo extraData(Object data) {
        this.extraData = data;
        return this;
    }


    @NonNull
    @Override
    public String toString() {
        return JsonExtKt.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayInfo) {
            PlayInfo otherInfo = (PlayInfo) o;
            return !TextUtils.isEmpty(this.mUrl) && TextUtils.equals(this.mUrl, otherInfo.mUrl);
        }
        return false;
    }

    @Override
    public PlayInfo clone() {
        try {
            return (PlayInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return PlayInfo.NONE;
        }
    }

    @Override
    public String url() {
        return mUrl;
    }

    @Override
    public String primaryKey() {
        return String.valueOf(mAudioId);
    }

    @Override
    public boolean isPlaying() {
        return mIsPlaying;
    }

    @Override
    public int startPosition() {
        return startPosition;
    }
}
