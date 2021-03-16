package com.joker.support;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.joker.support.data.MetaKey;
import com.tencent.mm.opensdk.modelbiz.OpenWebview;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * w
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public class TdSdk {

    /**
     * 返回weibo appkey
     */
    public static String WbAppKey(Context context) {
        return getAppMetaData(context, MetaKey.weibo_app_key);
    }

    public static String WbRedUrl(Context context) {
        return getAppMetaData(context, MetaKey.weibo_redirect_url);
    }

    public static String WbScope(Context context) {
        return getAppMetaData(context, MetaKey.weibo_scope);
    }

    /**
     * 返回weixin Appid
     */
    public static String WxAppid(Context context) {
        return getAppMetaData(context, MetaKey.weixin_app_id);
    }

    /**
     * 返回weixinSecret
     */
    public static String WxSecret(Context context) {
        return getAppMetaData(context, MetaKey.weixin_app_secret);
    }

    /**
     * 返回qqAppid
     */
    public static String QQAppid(Context context) {
        return getAppMetaData(context, MetaKey.qq_app_id);
    }

    /**
     * 返回qqAppkey
     */
    public static String QQAppKey(Context context) {
        return getAppMetaData(context, MetaKey.qq_app_key);
    }

    public static String PaypalProduction(Context context) {
        return getAppMetaData(context, MetaKey.paypal_environment_production);
    }

    public static IWXAPI getWeixinAPI(Context context, String appID) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, appID);
        iwxapi.registerApp(appID);
        return iwxapi;
    }

    /**
     * 打开微信
     *
     * @param context
     * @return
     */
    public static boolean openWeixin(Context context) {
        IWXAPI iwxapi = getWeixinAPI(context, TdSdk.WxAppid(context));
        boolean canUse = canUseWXLogin(context, iwxapi);
        if (canUse) {
            return iwxapi.openWXApp();
        }
        return false;
    }

    /**
     * 拉起微信小程序
     *
     * @param context
     * @param mini_name
     * @param mini_path
     * @return
     */
    public static boolean openMiniProgram(Context context, String mini_name, String mini_path) {
        IWXAPI iwxapi = getWeixinAPI(context, TdSdk.WxAppid(context));
        boolean canUse = canUseWXLogin(context, iwxapi);
        if (canUse && !TextUtils.isEmpty(mini_name)) {//未安装微信与小程序ID为空时不支持跳转
            WXLaunchMiniProgram.Req miniReq = new WXLaunchMiniProgram.Req();
            miniReq.userName = mini_name;// 填小程序原始id
            miniReq.path = mini_path;//拉起小程序页面的可带参路径，不填默认拉起小程序首页
            miniReq.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
            iwxapi.sendReq(miniReq);
        }
        return false;
    }

    /**
     * 打开微信,且去打开网页
     * 暂时不可用，打开微信并且能跳转到webviewUI 但微信报错，跳转失败
     *
     * @param context
     * @return
     */
    public static boolean openWeixinGoWebView(Context context, @NonNull String url) {
        IWXAPI iwxapi = getWeixinAPI(context, TdSdk.WxAppid(context));
        boolean canUse = canUseWXLogin(context, iwxapi);
        if (canUse) {
            OpenWebview.Req req = new OpenWebview.Req();
            req.url = url;
            return iwxapi.sendReq(req);
        }
        return false;
    }


    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值, 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            Log.e("TdSdk", "请检查build.gradle是否配置" + key);
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = String.valueOf(applicationInfo.metaData.get(key));
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(resultData)) {
            Log.e("TdSdk", "请检查build.gradle是否配置" + key);
        }

        return resultData;
    }

    public static boolean canUseWXLogin(Context context, @NonNull IWXAPI api) {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "没有安装微信，请先安装微信", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
