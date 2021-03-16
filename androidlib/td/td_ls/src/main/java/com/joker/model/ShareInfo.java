package com.joker.model;

import androidx.annotation.DrawableRes;

import com.joker.TdType;
import com.joker.flag.ClientFlags;
import com.joker.flag.ShareFlags;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public class ShareInfo {
    public String title;
    public String content;
    public String imgUrl;
    @DrawableRes
    public int resID;//默认出错图片
    public String shareUrl;
    public String linkUrl;//内链
    public String defaultEnd;
    @ShareFlags
    public int shareType;
    @ClientFlags
    public int clientType;

    public MiniAppInfo miniAppInfo;


    /**
     * 分享小程序
     */
    public ShareInfo(@ClientFlags int client, String title, String content, String imgUrl
            , @DrawableRes int resID, String shareUrl, String linkUrl, String defaultEnd, MiniAppInfo miniAppInfo) {

        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.resID = resID;
        this.shareUrl = shareUrl;
        this.linkUrl = linkUrl;
        this.defaultEnd = defaultEnd;
        this.clientType = client;
        this.shareType = TdType.mini_program_share;

        this.miniAppInfo = miniAppInfo;
    }


    /**
     * 仅分享图片
     */
    public ShareInfo(@ClientFlags int client, String imgUrl, @DrawableRes int resID) {
        this(client, TdType.image_share, "", "", imgUrl, resID, "", "", "");
    }

    /**
     * 分享复合消息
     */
    public ShareInfo(@ClientFlags int client, String title, String content, String imgUrl, @DrawableRes int resID, String shareUrl, String linkUrl, String defaultEnd) {
        this(client, TdType.multi_share, title, content, imgUrl, resID, shareUrl, linkUrl, defaultEnd);
    }

    public static class MiniAppInfo {
        public MiniAppInfo(String mini_program_path, String mini_program_name) {
            this.mini_program_name = mini_program_name;
            this.mini_program_path = mini_program_path;
        }

        // 分享小程序参数
        public String mini_program_path;//小程序链接
        public String mini_program_name;//小程序id

    }

    public ShareInfo(@ClientFlags int client, @ShareFlags int type, String title, String content, String imgUrl
            , @DrawableRes int resID, String shareUrl, String linkUrl, String defaultEnd) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.resID = resID;
        this.shareUrl = shareUrl;
        this.linkUrl = linkUrl;
        this.defaultEnd = defaultEnd;
        this.clientType = client;
        this.shareType = type;
    }
}
