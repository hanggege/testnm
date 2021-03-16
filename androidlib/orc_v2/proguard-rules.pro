# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/dt/Developer/lib/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

##############################################################################
####  native

-keepattributes SourceFile,LineNumberTable


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

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



-keep class cn.trinea.android.** { *; }
-keepclassmembers class cn.trinea.android.** { *; }
-dontwarn cn.trinea.android.**



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
#-dontoptimize
#-dontshrink
-dontwarn **
-dontnote **


##############################################################################
####  jackson json
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
 -dontwarn com.fasterxml.jackson.databind.**
 -keep class org.codehaus.** { *; }
 -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class com.mei.orc.john.network.** {
  public void set*(***);
  public *** get*();
}

##############################################################################
####  retrofit
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp3.** { *;}
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**

-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn okio.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class retrofit.RequestInterceptor.RequestFacade.** { *; }

### Jackson SERIALIZER SETTINGS
-keepclassmembers,allowobfuscation class * {
    @org.codehaus.jackson.annotate.* <fields>;
    @org.codehaus.jackson.annotate.* <init>(...);
}

### Using Retrofit
-keep class com.octo.android.robospice.retrofit.** { *; }

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
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

-keep public class * extends com.octo.android.robospice.retrofit.RetrofitJackson2SpiceService
-keep public class * extends com.octo.android.robospice.retrofit.RetrofitGsonSpiceService

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

#唯一id
-keep class com.bun.miitmdid.core.** {*;}