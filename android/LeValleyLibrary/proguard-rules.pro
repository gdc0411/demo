# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\android-sdk/tools/proguard/proguard-android.txt
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

# LePlaySDK
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepattributes *Annotation*,EnclosingMethod
-keepattributes JavascriptInterface
-keepattributes Signature
-ignorewarnings

-dontwarn android.support.**
-keep class android.support.** { *;}

-dontwarn org.apache.commons.codec.**
-keep class org.apache.commons.codec.** { *;}

-dontwarn  android.content.pm.**
-keep class android.content.pm.** { *;}

-keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

#-assumenosideeffects class android.util.Log {
#
#   public static *** d(...);
#   public static *** v(...);
#}

#-assumenosideeffects class android.util.Log {
#    public static *** e(...);
#    public static *** v(...);
#}

#-assumenosideeffects class android.util.Log {
#    public static *** i(...);
#    public static *** v(...);
#}

#-assumenosideeffects class android.util.Log {
#    public static *** w(...);
#    public static *** v(...);
#}


-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
	public static int v(...);
	public static int i(...);
	public static int w(...);
	public static int d(...);
	public static int e(...);
}

-keep class android.support.v4.app.NotificationCompat**{
    public *;
}

-keep class com.lecloud.sdk.http.engine.** { *;}

-keep class com.lecloud.sdk.http.entity.** { *;}

-keep class com.lecloud.sdk.http.request.** { *;}

-dontwarn com.lecloud.sdk.api.ad.entity.AdElementInfo
-keep class com.lecloud.sdk.api.ad.entity.AdElementInfo { *;}

#-dontwarn com.lecloud.sdk.api.ad.impl.LeTvAd
#-keep class com.lecloud.sdk.api.ad.impl.LeTvAd { *;}

-dontwarn com.lecloud.sdk.player.IPlayer
-keep class com.lecloud.sdk.player.IPlayer { *;}

-dontwarn com.lecloud.sdk.api.md.entity.action.**
-keep class com.lecloud.sdk.api.md.entity.action.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.live.**
-keep class com.lecloud.sdk.api.md.entity.live.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.vod.cloud.**
-keep class com.lecloud.sdk.api.md.entity.vod.cloud.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.vod.saas.**
-keep class com.lecloud.sdk.api.md.entity.vod.saas.** { *;}

-dontwarn com.lecloud.sdk.api.md.entity.vod.VideoHolder
-keep class com.lecloud.sdk.api.md.entity.vod.VideoHolder { *;}

-dontwarn com.lecloud.sdk.api.md.IActionMediaData
-keep class com.lecloud.sdk.api.md.IActionMediaData { *;}

-dontwarn com.lecloud.sdk.api.md.ILiveMediaData
-keep class com.lecloud.sdk.api.md.ILiveMediaData { *;}

-dontwarn com.lecloud.sdk.api.md.IMediaData
-keep class com.lecloud.sdk.api.md.IMediaData { *;}

-dontwarn com.lecloud.sdk.api.md.IVodMediaData
-keep class com.lecloud.sdk.api.md.IVodMediaData { *;}

-dontwarn com.lecloud.sdk.utils.LeLog
-keep class com.lecloud.sdk.utils.LeLog{ *;}

-dontwarn com.lecloud.sdk.utils.LeLog.LeLogMode
-keep class com.lecloud.sdk.utils.LeLog.LeLogMode { *;}

-dontwarn com.lecloud.sdk.videoview.**
-keep class com.lecloud.sdk.videoview.** { *;}

-dontwarn com.lecloud.sdk.player.live.**
-keep class com.lecloud.sdk.player.live.** { *;}

-dontwarn com.lecloud.sdk.player.vod.**
-keep class com.lecloud.sdk.player.vod.** { *;}

-dontwarn com.lecloud.sdk.listener.**
-keep class com.lecloud.sdk.listener.** { *;}

-dontwarn com.lecloud.sdk.api.ad.entity.**
-keep class com.lecloud.sdk.api.ad.entity.** { *;}

-dontwarn com.lecloud.sdk.api.ad.IAd
-keep class com.lecloud.sdk.api.ad.IAd { *;}

-dontwarn com.lecloud.sdk.api.ad.IAdContext
-keep class com.lecloud.sdk.api.ad.IAdContext { *;}

-dontwarn com.lecloud.sdk.api.ad.ILeTvAd
-keep class com.lecloud.sdk.api.ad.ILeTvAd { *;}

-dontwarn com.lecloud.sdk.api.ad.ILeTvAdContext
-keep class com.lecloud.sdk.api.ad.ILeTvAdContext { *;}

-dontwarn com.lecloud.sdk.api.stats.IPlayAction
-keep class com.lecloud.sdk.api.stats.IPlayAction { *;}

-dontwarn com.lecloud.sdk.api.stats.IStats
-keep class com.lecloud.sdk.api.stats.IStats { *;}

-dontwarn com.lecloud.sdk.api.stats.IStatsContext
-keep class com.lecloud.sdk.api.stats.IStatsContext { *;}

-dontwarn com.lecloud.sdk.api.cde.**
-keep class com.lecloud.sdk.api.cde.** { *;}

-dontwarn com.lecloud.sdk.api.feedback.**
-keep class com.lecloud.sdk.api.feedback.** { *;}

-dontwarn com.lecloud.sdk.api.linepeople.OnlinePeopleChangeListener
-keep class com.lecloud.sdk.api.linepeople.OnlinePeopleChangeListener { *;}


-dontwarn com.lecloud.sdk.api.timeshift.ItimeShiftListener
-keep class com.lecloud.sdk.api.timeshift.ItimeShiftListener { *;}

-dontwarn com.lecloud.sdk.api.status.ActionStatus
-keep class com.lecloud.sdk.api.status.ActionStatus { *;}

-dontwarn com.lecloud.sdk.api.status.ActionStatusListener
-keep class com.lecloud.sdk.api.status.ActionStatusListener { *;}

-dontwarn com.lecloud.sdk.constant.**
-keep class com.lecloud.sdk.constant.** { *;}

-dontwarn com.lecloud.sdk.download.control.**
-keep class com.lecloud.sdk.download.control.** { *;}

-dontwarn com.lecloud.sdk.download.info.LeDownloadInfo
-keep class com.lecloud.sdk.download.info.LeDownloadInfo { *;}

-dontwarn com.lecloud.sdk.download.observer.LeDownloadObserver
-keep class com.lecloud.sdk.download.observer.LeDownloadObserver { *;}

-dontwarn com.lecloud.sdk.config.LeCloudPlayerConfig
-keep class com.lecloud.sdk.config.LeCloudPlayerConfig { *;}

-dontwarn com.lecloud.sdk.download.plugin.**
-keep class com.lecloud.sdk.download.plugin.** { *;}

-dontwarn com.lecloud.sdk.surfaceview.**
-keep class com.lecloud.sdk.surfaceview.** { *;}

-dontwarn com.lecloud.sdk.download.control.DownloadCenter
-keep class com.lecloud.sdk.download.control.DownloadCenter { *;}

-dontwarn com.lecloud.sdk.download.control.BaseDownloadCenter
-keep class com.lecloud.sdk.download.control.BaseDownloadCenter { *;}


-dontwarn com.letv.android.client.cp.sdk.api.md.entity.**
-keep class com.letv.android.client.cp.sdk.api.md.entity.** { *;}

-dontwarn com.letv.android.client.cp.sdk.videoview.**
-keep class com.letv.android.client.cp.sdk.videoview.** { *;}

-dontwarn com.lecloud.sdk.utils.NetworkUtils
-keep class com.lecloud.sdk.utils.NetworkUtils { *;}

-dontwarn com.lecloud.sdk.utils.AppUtils
-keep class com.lecloud.sdk.utils.AppUtils { *;}

-keepattributes Exceptions,InnerClasses

-keep public class com.letv.ads.**{
 *;
}
-keep public class com.letv.plugin.pluginloader.**{
 *;
}
-dontwarn com.letv.ads.**
-dontwarn com.letv.plugin.pluginloader.**

-keep class cn.mmachina.** { *; }
-keep class com.letv.adlib.** { *; }
-keep class com.letvcloud.cmf.** { *; }
-keep class android.net.** { *; }
-keep class com.android.internal.http.multipart.** { *; }
-keep class org.apache.commons.** { *; }
-keep class com.lecloud.xutils.** { *; }

-keep public class * extends android.app.Fragment

-keep class * implements android.os.Parcelable { *; }

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements android.os.Parcelable {
 public <fields>;
 private <fields>;
}

-keep class * implements java.io.Serializable { *; }

-keep class android.app.IServiceConnection { *;}

-keep class * implements android.os.IInterface { *;}

-keep class android.util.Singleton { *;}

-keep class android.os.SystemProperties
-keepclassmembers class android.os.SystemProperties{
  public <fields>;
  public <methods>;
}

# PUSHSTREAM 使⽤用2.1及以上版本请⽤用以下混淆规则
-keep class com.letv.recorder.** { *; }
-keep class com.le.utils.gles.** { *; }
-keep class com.le.filter.gles.**{ *; }
-keep class com.le.utils.common.**{ *;}
-keep class com.le.utils.format.**{*;}
-keep class com.le.share.streaming.**{*;}
-dontwarn com.le.utils.format.**

# QQ
-keep class com.tencent.mm.sdk.** {*;}


# Weibo
-dontwarn com.weibo.sdk.android.WeiboDialog
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
-keep public class android.net.http.SslError{*;}
-keep public class android.webkit.WebViewClient{*;}
-keep public class android.webkit.WebChromeClient{*;}
-keep public interface android.webkit.WebChromeClient$CustomViewCallback {*;}
-keep public interface android.webkit.ValueCallback {*;}
-keep class * implements android.webkit.WebChromeClient {*;}

# 微信
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class android.net.http.** {*; }
-keep class android.webkit.** {*; }
-keep class com.weibo.net.** {*; }

-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*; }
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*; }


# 友盟PUSH
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn org.apache.thrift.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class com.umeng.message.example.example.R$*{
   public static final int *;
}

-keep public class **.R$*{
   public static final int *;
}



# LEVALLEY
-keep class com.lecloud.valley.handler.** {*;}
-keep class com.lecloud.valley.modules.** {*;}
-keep class com.lecloud.valley.utils.LogUtils {*;}
-keep class com.lecloud.valley.leecoSdk.LeReactPushViewManager {*;}
-keep class com.lecloud.valley.leecoSdk.LeReactSubVideoViewManager {*;}
-keep class com.lecloud.valley.leecoSdk.LeReactVideoViewManager {*;}
-keep class com.lecloud.valley.views.LinearGradientManager {*;}