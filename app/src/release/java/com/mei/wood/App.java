package com.mei.wood;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mei.init.BaseApp;
import com.mei.orc.channel.PkgChannelKt;
import com.mei.orc.john.model.JohnUser;
import com.mei.provider.AppBuildConfig;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class App extends BaseApp {


    @Override
    public void onCreate() {
        super.onCreate();
        //bugly
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this); //App的策略Bean
        strategy.setAppChannel(PkgChannelKt.getChannelId());     //设置渠道
        strategy.setAppVersion(AppBuildConfig.VERSION_NAME);      //App的版本
        strategy.setAppReportDelay(20000);  //设置SDK处理延时，毫秒
        // 设置是否为上报进程
        strategy.setUploadProcess(isAppProcess(getApplicationContext()));
        CrashReport.initCrashReport(this, AppBuildConfig.BUGLY_APP_ID, false);  //初始化SDK
        BuglyLog.e("BuglyLog", JohnUser.getSharedUser().getUserIDAsString());

        //友盟
        //  liuhang 2019-11-22 上线前关闭日志
        UMConfigure.setLogEnabled(false);
        UMConfigure.init(getApplicationContext(), AppBuildConfig.UMENG_APP_ID, PkgChannelKt.getChannelId(), UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                //umeng & talking data
//                MobclickAgent.onResume(activity);
                if (activity != null) {
                    BuglyLog.e("BuglyLog", activity.toString());
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                //umeng & talking data
//                MobclickAgent.onPause(activity);
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });


    }


}
