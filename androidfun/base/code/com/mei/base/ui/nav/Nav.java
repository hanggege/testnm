package com.mei.base.ui.nav;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.mei.GrowingUtil;
import com.mei.browser.BrowserInfo;
import com.mei.browser.ImageBrowserKt;
import com.mei.intimate.TouristsActivity;
import com.mei.live.ui.VideoLiveRoomActivity;
import com.mei.mentor_home_page.ui.MentorHomePageActivity;
import com.mei.mentor_home_page.ui.MentorHomePageActivityLauncher;
import com.mei.orc.john.model.JohnUser;
import com.mei.wood.constant.Preference;
import com.mei.wood.ui.TabMainActivity;
import com.mei.wood.util.AppManager;
import com.mei.wood.util.MeiLogKt;
import com.mei.wood.web.MeiWebActivityLauncher;
import com.mei.wood.web.MeiWebData;
import com.mei.wood.web.MelkorWebViewClient;
import com.net.MeiUser;

import java.util.List;


/**
 * Created by guoyufeng on 21/5/15.
 */
public class Nav {

    private static final String TAG = Nav.class.getSimpleName();

    public static void toUriActivity(Context from, String innerURL) {
        Uri uri = Uri.parse(innerURL);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(innerURL));
        if (!(from instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (isIntentAvailable(from, intent)) {
            from.startActivity(intent);
        } else if (uri.getHost() != null && uri.getHost().startsWith("com.mei.")) {
            //自己内部App跳转不成功，则去应用市场下载
            Uri marketUri = Uri.parse("market://details?id=" + uri.getHost());
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            from.startActivity(marketIntent);
        }
    }


    public static void logout(final Activity from, boolean login) {
        Log.d(TAG, "logout, from:" + from);
        clearInfo();
        exitToMain();
    }

    public static void exitToMain() {
        Log.d(TAG, "exitToMain");
        AppManager.getInstance().finishOtherAllActivity(TabMainActivity.class);
    }

    public static void clearInfo() {
        Log.d(TAG, "clearInfo");

        JohnUser.getSharedUser().setLoginInfo(null);

        //注意: 这里面需要清除兴趣的key，根据MeiUser的缓存信息，所以必须在MeiUser.reset()之前
        GrowingUtil.logout();
        MeiUser.reset();
    }

    public static void toWebActivity(Context from, String url) {
        toWebActivity(from, url, "");
    }

    public static void toWebActivity(Context from, String url, String title) {
        toWebActivity(from, url, title, true);
    }

    /**
     * @param from
     * @param url
     * @param title
     * @param canRefresh 是否带右边的刷新按钮控制
     */
    public static void toWebActivity(Context from, String url, String title, boolean canRefresh) {
        Log.d(TAG, "to web activity, url: " + url);
        MeiWebActivityLauncher.startActivity(from, new MeiWebData(url, title, canRefresh));
    }


    public static void toMain(Context from) {
        from.startActivity(new Intent(from, TabMainActivity.class));
    }

    public static void toTourists(Context from) {
        from.startActivity(new Intent(from, TouristsActivity.class));
    }

    public static void toPersonalPage(Context from, int userId) {
        if (AppManager.getInstance().hasActivitys(MentorHomePageActivity.class, VideoLiveRoomActivity.class).size() == 2) {
            AppManager.getInstance().finishActivity(MentorHomePageActivity.class);
        }
        MentorHomePageActivityLauncher.startActivity(from, userId);
    }

    public static void toViewImages(Context from, BrowserInfo imageInfo) {
        if (imageInfo != null && !imageInfo.getImages().isEmpty() && imageInfo.getIndex() < imageInfo.getImages().size()) {
            BrowserInfo info = new BrowserInfo();
            info.setImages(imageInfo.getImages());
            info.setIndex(imageInfo.getIndex());
            info.setDownloadable(imageInfo.getDownloadable());
            ImageBrowserKt.showImageBrowser(from, info);
        }
    }

    /**
     * 通过URL跳转内链统一用该方法
     */
    public static boolean toAmanLink(Context context, String url) {
        return toAmanLink(context, url, "", "", "");
    }

    public static boolean toAmanLink(Context context, String url, String title) {
        return toAmanLink(context, url, title, "", "");
    }

    public static boolean toAmanLink2(Context context, String url, String fromID, String survey_msg_id) {
        return toAmanLink(context, url, "", fromID, survey_msg_id);
    }

    public static boolean toAmanLink(final Context context, String url, String title, String fromID, String survey_msg_id) {
        try {
            url = MelkorWebViewClient.changeScheme2(url)
                    .replaceAll("\n", "")
                    .replaceAll("\t", "");
            MeiLogKt.logDebug("amanLink", "toAmanLink: activity  " + context.getClass().getSimpleName() + "   uri = " + url);
            if (!TextUtils.isEmpty(url) && MelkorWebViewClient.isAppLink(url)) {
                if (NavLinkHandler.handLink(context, url)) {

                } else {
                    Intent intent = new Intent().setData(Uri.parse(url));
                    if (isIntentAvailable(context, intent)) {
                        intent.putExtra(Preference.aman_from_id, fromID);
                        intent.putExtra(Preference.aman_survey_msg_id, survey_msg_id);
                        if (!(context instanceof Activity)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        context.startActivity(intent);
                    } else {
                        String msg = "no activity to handle intent with url: " + url;
                        Log.e(TAG, msg);
                        return false;
                    }
                }
            } else if (URLUtil.isNetworkUrl(url)) {
                Nav.toWebActivity(context, url, title);
            } else {
                Nav.toUriActivity(context, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }


    /**
     * 检查Intent是否可用
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() > 0;
    }


}
