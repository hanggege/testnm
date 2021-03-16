package com.mei.provider;

import android.os.Build;

import com.mei.orc.Cxt;

import static com.mei.orc.util.app.AppUtilKt.getAppVersion;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/11/7
 * 项目之间不同的本地信息
 */
public interface ProjectExt {
    String WEBVIEWUserAgent = " dove_";
    String FullUserAgent = String.format("%s/%s(%s;%s;%s;%s) mdlclient ", "dove", getAppVersion(Cxt.get()), Build.MANUFACTURER, Build.BRAND, Build.MODEL, Build.VERSION.RELEASE);
    String share_from_app = "dove";
    String WEIXIN_ANDROID = "dove_weixin_android";
}
