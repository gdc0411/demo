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
-keep class org.xutils.** { *; }
-keepattributes *Annotation*,EnclosingMethod
-keepattributes JavascriptInterface
-keepattributes Signature
-ignorewarnings
-dontwarn android.support.**
-keep class android.support.** { *;}
-dontwarn org.apache.commons.codec.**
-keep class org.apache.commons.codec.** { *;}
-keep class org.apache.** { *; }
-dontwarn android.content.pm.**
-keep class android.content.pm.** { *;}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);}
-keepclasseswithmembernames class * { native <methods>;}
-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static int v(...);
public static int i(...);
public static int w(...);
public static int d(...);
public static int e(...);}
-keep class android.support.v4.app.NotificationCompat**{
public *;}
-keep class com.lecloud.sdk.http.** { *;}
-keep class com.lecloud.sdk.api.ad.** { *;}
-keep class com.lecloud.sdk.player.** { *;}
-keep class com.lecloud.sdk.api.** { *;}
-keep class com.lecloud.sdk.utils.**{ *;}
-keep class com.lecloud.sdk.videoview.** { *;}
-keep class com.lecloud.sdk.listener.** { *;}
-keep class com.lecloud.sdk.download.**{ *;}
-keep class com.lecloud.sdk.config.** { *;}
-keep class com.lecloud.sdk.surfaceview.** { *;}
-keep class com.lecloud.sdk.constant.** { *;}
-keepattributes Exceptions,InnerClasses
-keep public class com.letv.ads.**{ *;}
-keep public class com.letv.plugin.pluginloader.**{ *;}
-dontwarn com.letv.ads.**
-dontwarn com.letv.plugin.pluginloader.**
-keep class cn.mmachina.** { *; }
-keep class com.letv.adlib.** { *; }
-keep class com.letvcloud.cmf.** { *; }
-keep class com.lecloud.uploadservice.** { *; }
-keep class android.net.** { *; }
-keep class com.android.internal.http.multipart.** { *; }
-keep class org.apache.commons.** { *; }
-keep class com.lecloud.xutils.** { *; }
-keep public class * extends android.app.Fragment
-keep class * implements android.os.Parcelable { *; }
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;}
-keepclassmembers class * implements android.os.Parcelable


# QQ
-keep class com.tencent.mm.sdk.** {*;}