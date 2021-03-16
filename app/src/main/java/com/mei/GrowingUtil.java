package com.mei;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.growingio.android.sdk.deeplink.DeeplinkCallback;
import com.mei.base.ui.nav.Nav;
import com.mei.login.LoginKt;
import com.mei.orc.channel.PkgChannelKt;
import com.mei.orc.util.handler.GlobalUIHandler;
import com.mei.provider.AppBuildConfig;
import com.mei.wood.util.AppManager;
import com.net.MeiUser;
import com.net.model.user.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/4.
 */

public class GrowingUtil {

    public static void init(Application application) {
        try {
            GrowingIO.startWithConfiguration(application,
                    new Configuration()
                            .setDebugMode(AppBuildConfig.IS_TEST)
                            .trackAllFragments()
                            .setChannel(PkgChannelKt.getChannelId())
                            .setDeeplinkCallback(((map, status, appAwakePassedTime) -> {
                                // 这里接收您的参数，匹配键值对，跳转指定 APP 内页面
                                // appAwakePassedTime 这个新的参数用来判定 APP 是否已经打开了很久才收到自定义参数，进而判断是否再继续跳转指定页面
                                String actionLink = map.get("action_link");
                                if (status == DeeplinkCallback.SUCCESS && appAwakePassedTime < 1500 && !TextUtils.isEmpty(actionLink)) {
                                    if (LoginKt.checkFirstLogin()) {
                                        GrowingDeepLinkKt.setActionLink(actionLink);
                                    } else {
                                        GlobalUIHandler.postUiDelay(() ->
                                                Nav.toAmanLink(AppManager.getInstance().currentActivity(), actionLink), 2000);
                                    }

                                }
                                GrowingDeepLinkKt.getDeepLinkMap().putAll(map);
                            })));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPageGroup(Activity context, String tag) {
//        GrowingIO.getInstance().setPageGroup(context, tag);
        GrowingIO.getInstance().setPageVariable(context, context.getClass().getSimpleName(), tag);
    }

    public static void ignoreFragment(Activity activity, Fragment fragment) {
        GrowingIO.getInstance().ignoreFragmentX(activity, fragment);
    }

    /**
     * 必须运行与onCreate中
     */
    public static void setPageName(Activity activity, String name) {
        GrowingIO.getInstance().setPageName(activity, name);
    }

    /**
     * 必须运行与onCreate中
     */
    public static void setPageName(Fragment fragment, String name) {
        GrowingIO.getInstance().setPageName(fragment, name);
    }

    public static void setViewContent(View view, String tag) {
        GrowingIO.setViewContent(view, tag);
    }

    /**
     * 上报应用退出
     */
    public static void logout() {
        GrowingIO growingIO = GrowingIO.getInstance();
        growingIO.clearUserId();

        //性别
        growingIO.setPeopleVariable("gender_new", "");
        // 出生日期（上报年份）
        growingIO.setPeopleVariable("birth_year_new", "");

        UserInfo userInfo = MeiUser.getSharedUser().info;
        if (userInfo != null && userInfo.interests != null) {
            for (UserInfo.InterestsBean interest : userInfo.interests) {
                //设置兴趣
                growingIO.setPeopleVariable("category_" + interest.id, "");
            }
        }
    }

    /**
     * 上报登陆信息
     */
    public static void uploadLoginInfo(@NonNull UserInfo userInfo) {
        try {
            GrowingIO growingIO = GrowingIO.getInstance();
            growingIO.setUserId(String.valueOf(userInfo.userId));
            //性别
            growingIO.setPeopleVariable("gender_new", userInfo.gender == 0 ? "女" : "男");
            growingIO.setPeopleVariable("staff_member", userInfo.isPublisher ? "是" : "否");
            //AB 测版本
            growingIO.setPeopleVariable("user_abtest_version", userInfo.abVer);
            // 出生日期（上报年份）
            if (!TextUtils.isEmpty(userInfo.birthYear)) {
                growingIO.setPeopleVariable("birth_year_new", birthYear2Year(userInfo.birthYear));
            }

            if (!TextUtils.isEmpty(userInfo.interestIds)) {
                for (String interest : userInfo.interestIds.split(",")) {
                    //设置兴趣
                    growingIO.setPeopleVariable("category_" + interest, "category_" + interest);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String birthYear2Year(String birthYear) {
        switch (birthYear) {
            case "05后": {
                return "2005";
            }
            case "00后": {
                return "2000";
            }
            case "95后": {
                return "1995";
            }
            case "90后": {
                return "1990";
            }
            case "85后": {
                return "1985";
            }
            case "80后": {
                return "1980";
            }
            case "75后": {
                return "1975";
            }
            case "70后": {
                return "1970";
            }
            case "60后": {
                return "1960";
            }
            default: {
                return "";
            }
        }
    }

    public static void setUserId(int userId) {
        GrowingIO.getInstance().setUserId(String.valueOf(userId));
    }


    /**
     * 埋点上传
     */
    public static void track(String event_key, String... key_value) {
        try {
            if (!TextUtils.isEmpty(event_key) && key_value.length % 2 == 0) {
                GrowingIO growingIO = GrowingIO.getInstance();
                JSONObject json = new JSONObject();
                for (int i = 0; i < key_value.length - 1; i = i + 2) {
                    json.put(key_value[i], key_value[i + 1]);
                }
                if (json.length() > 0) {
                    growingIO.track(event_key, json);
                } else {
                    growingIO.track(event_key);
                }
            } else {
                Log.e(GrowingUtil.class.getSimpleName(), "埋点数据出错");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 埋点上传
     */
    public static void track(String event_key, Map<String, String> map) {
        try {
            if (!TextUtils.isEmpty(event_key)) {
                GrowingIO growingIO = GrowingIO.getInstance();
                JSONObject json = new JSONObject();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    json.put(entry.getKey(), entry.getValue());
                }
                if (json.length() > 0) {
                    growingIO.track(event_key, json);
                } else {
                    growingIO.track(event_key);
                }
            } else {
                Log.e(GrowingUtil.class.getSimpleName(), "埋点数据出错");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void ignoredView(View view) {
        GrowingIO.ignoredView(view);
    }

}
