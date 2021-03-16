package com.mei.orc.imageload.cache;

import android.text.TextUtils;

/**
 * Created by joker on 16/7/6.
 */
public class QiniuImageInfo {


    /**
     * format : jpeg
     * width : 200
     * height : 200
     * colorModel : ycbcr
     */

    public String format;
    public int width;
    public int height;
    public String colorModel;
    public String netUrl;

    public boolean checkSameUrl(String fromUrl) {
        return TextUtils.equals(netUrl, fromUrl);
    }

    public static boolean checkWH(QiniuImageInfo info) {
        return info != null && info.width > 0 && info.height > 0;
    }
}
