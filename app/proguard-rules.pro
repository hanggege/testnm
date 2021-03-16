# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# growing io
-keep class com.growingio.** {
    *;
}
-dontwarn com.growingio.**
-keepnames class * extends android.view.View
-keepnames class * extends android.app.Fragment
-keepnames class * extends android.support.v4.app.Fragment
-keepnames class * extends androidx.fragment.app.Fragment
-keep class android.support.v4.view.ViewPager{
  *;
}
-keep class android.support.v4.view.ViewPager$**{
  *;
}
-keep class androidx.viewpager.widget.ViewPager{
  *;
}
-keep class androidx.viewpager.widget.ViewPager$**{
  *;
}


##############################################################################
####  getui
 -dontwarn com.igexin.**
 -keep class com.igexin.** { *; }
 -keep class org.json.** { *; }

##############################################################################
####  tencent IM
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**
-keep class tencent.**{*;}
-dontwarn tencent.**
-keep class qalsdk.**{*;}
-dontwarn qalsdk.**
##############################################################################
####  native

-keepattributes SourceFile,LineNumberTable


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends java.lang.annotation.Annotation { *; }
-keepclassmembers class * implements javax.net.ssl.SSLSocketFactory { *; }
-keep class java.lang.reflect.** { *; }
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

-keep class android.support.v13.app.** { *; }
-keep interface android.support.v13.app.** { *; }


##############################################################################
# AndroidX
-keep class androidx.** { *; }

##############################################################################
# Workaround for Samsung Android 4.2 bug
# https://code.google.com/p/android/issues/detail?id=78377
# https://code.google.com/p/android/issues/detail?id=78377#c188
-keepattributes **
-keep class !android.support.v7.internal.view.menu.**,** {*;}
-dontpreverify
-dontoptimize
-dontshrink
-dontwarn **
-dontnote **


##############################################################################
#### gson
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
 -keep class com.net.model.** { *; }
 -keep class com.mei.orc.john.model.** { *; }

##############################################################################
#### OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-keep class okio.**{*;}
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
##############################################################################
####  retrofit
#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#-dontwarn com.squareup.okhttp.**
#-dontwarn com.squareup.okhttp3.**
#-keep class com.squareup.okhttp3.** { *;}
#-keep interface com.squareup.okhttp3.** { *; }
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#-dontwarn rx.**
#-dontwarn retrofit.**
#-dontwarn okio.**
#-keep class retrofit.** { *; }
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}

##############################################################################
####  robospice
# For RoboSpice
#Results classes that only extend a generic should be preserved as they will be pruned by Proguard
#as they are "empty", others are kept
-keep class com.net.model.**{
    public void set*(***);
    public *** get*();
    public *** is*();
}

#RoboSpice requests should be preserved in most cases
-keepclassmembers class com.net.network.** {
  public void set*(***);
  public *** get*();
  public *** is*();
}

-keep public class * extends com.octo.android.robospice.retrofit.RetrofitJackson2SpiceService
-keep public class * extends com.octo.android.robospice.retrofit.RetrofitGsonSpiceService

##############################################################################
##  Bugly
-keep class * extends android.app.Activity{*;}
-keep class * extends android.app.Service{*;}

#Bugly接口
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


##############################################################################
## 友盟统计
-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep public class com.mei.dove.R$*{
    public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

##############################################################################


##########webview视屏
-keepclassmembers class name.cpr.VideoEnabledWebView$JavascriptInterface { public *; }
##########支付宝
#-libraryjars libs/alipaySDK-20150602.jar

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-dontwarn com.alipay.**
##############################################################################
####  native
-keep class java.lang.reflect.** { *; }
-keep class android.support.v7.** { *; }
-keep class retrofit.RequestInterceptor.RequestFacade.** { *; }


-keep class cn.trinea.android.** { *; }
-keepclassmembers class cn.trinea.android.** { *; }
-dontwarn cn.trinea.android.**


### GifImageView
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}

##############################################################################
####  robospice
# For RoboSpice
#Results classes that only extend a generic should be preserved as they will be pruned by Proguard
#as they are "empty", others are kept
-keep class com.mei.orc.john.model.**{
    public void set*(***);
    public *** get*();
    public *** is*();
}

#RoboSpice requests should be preserved in most cases
-keepclassmembers class com.mei.orc.john.network.** {
  public void set*(***);
  public *** get*();
  public *** is*();
}

-keepclassmembers class com.mei.orc.http.** {
  public void set*(***);
  public *** get*();
  public *** is*();
}

#jackson json models cases
-keepclassmembers class * {
  public void set*(***);
  public *** get*();
  public *** is*();
}

#七牛media
-keep class com.pili.pldroid.player.** { *; }
-keep class tv.danmaku.ijk.media.player.** {*;}
-keep class cn.tee3.** {*;}
-keep class org.webrtc.** {*;}
-keep class com.qiniu.android.dns.** {*;}
-keep class com.qiniu.pili.droid.streaming.** {*;}
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-keep class com.pili.pldroid.player.** { *; }
-keep class com.qiniu.qplayer.mediaEngine.MediaPlayer{*;}
-ignorewarnings

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
    }
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl


-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule


#Log日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
}


# ProGuard configurations for Bugtags
-keepattributes LineNumberTable,SourceFile

-keep class com.bugtags.library.** {*;}
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.bugtags.library.**
# End Bugtags

#QQ
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
#weixin
-keep class com.tencent.mm.sdk.** {*;}
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}


#腾讯.云通讯.x5webview
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**
#recyclerviewpager
-keep class com.lsjwzh.widget.recyclerviewpager.**
-dontwarn com.lsjwzh.widget.recyclerviewpager.**

-keepnames class * extends android.view.View
-keep class Landroidx.localbroadcastmanager.**

-keep class * extends android.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class androidx.fragment.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class * extends androidx.fragment.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}


#kotlin
-dontwarn kotlin.**
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
#BaseRecyclerViewAdapterHelper
-keep class com.chad.library.adapter.** {
   *;
}
#webview上传文件支持
-keepclassmembers class * extends android.webkit.WebChromeClient{
       public void openFileChooser(...);
}

-keep class com.sina.weibo.sdk.** { *; }

-keepattributes InnerClasses
-dontoptimize

#实人认证
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
-keep class com.alibaba.security.rp.**{*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.alibaba.security.biometrics.**{*;}
-keep class android.taobao.windvane.**{*;}


# 视频直播
-keep class io.agora.**{*;}

#svga
-keep class com.squareup.wire.** { *; }
-keep class com.opensource.svgaplayer.proto.** { *; }

#闪验
-dontwarn com.cmic.sso.sdk.**
-dontwarn com.sdk.**
-keep class com.cmic.sso.sdk.**{*;}
-keep class com.sdk.** { *;}
-keep class cn.com.chinatelecom.account.api.**{*;}

# SmartRefreshLayout
-keep class com.scwang.smart.refresh.classics.* {*;}