# app
-keep class com.mei.wood.App
-keep class com.mei.init.BaseApp
-keep class com.mei.splash.ui.SplashActivity
-keep class com.mei.push.GetuiIntentService
-keep class com.mei.orc.common.CommonConstant
-keep class com.report.ReportAppPage

# tencent IM
-keep class com.tencent.imsdk.** { *; }
# MMKV
-keep class com.tencent.mmkv.** { *; }

-keep class com.app.** { *; }

# AndroidX
-keep class androidx.appcompat.** { *; }
-keep class androidx.core.** { *; }
-keep class Landroidx.localbroadcastmanager.**