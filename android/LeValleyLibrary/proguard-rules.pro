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

-dontwarn cn.mmachina.**
-keep class cn.mmachina.** {*;}

-dontwarn com.letv.adlib.**
-keep class com.letv.adlib.** {*;}

-dontwarn com.letv.controller.tracker.**
-keep class com.letv.controller.tracker.** {*;}

-dontwarn com.letv.pano.**
-keep class com.letv.pano.** {*;}

-dontwarn com.lecloud.sdk.**
-keep class com.lecloud.sdk.** {*;}

-dontwarn com.lecloud.xutils.**
-keep class com.lecloud.xutils.** {*;}

-dontwarn com.lecloud.cmf.**
-keep class com.lecloud.cmf.** {*;}

-dontwarn com.lecloud.skin.**
-keep class com.lecloud.skin.** {*;}

-dontwarn cn.com.iresearch.mvideotracker.**
-keep class cn.com.iresearch.mvideotracker.** {*;}

-dontwarn android.os.**
-keep class android.os.** {*;}

-dontwarn android.net.compatibility.**
-keep class android.net.compatibility.** {*;}

-dontwarn android.net.http.**
-keep class android.net.http.** {*;}

-dontwarn com.android.internal.http.multipart.**
-keep class com.android.internal.http.multipart.** { *; }

-dontwarn org.apache.commons.**
-keep class org.apache.commons.** { *; }

-dontwarn org.apache.http.**
-keep class org.apache.http.** { *; }


# QQ

-keep class com.tencent.mm.sdk.** {*;}