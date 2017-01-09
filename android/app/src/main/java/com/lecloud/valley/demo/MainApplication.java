package com.lecloud.valley.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lecloud.valley.handler.CrashHandler;
import com.lecloud.valley.modules.UmengPushModule;
import com.lecloud.valley.utils.LogUtils;
import com.facebook.react.ReactApplication;
import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.lecloud.sdk.listener.OnInitCmfListener;
import com.microsoft.codepush.react.CodePush;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

    public static final String TAG = "LeDemo";

//    private static final RJReactPackage rjPackage = new RJReactPackage();
//    public static RJReactPackage getRjPackage() {
//        return rjPackage;
//    }

    //获取当前进程名字
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, LogUtils.getTraceInfo() + "Application start-------");
        String processName = getProcessName(this, android.os.Process.myPid());

        if (getApplicationInfo().packageName.equals(processName)) {

            //TODO CrashHandler是一个抓取崩溃log的工具类（可选）
            CrashHandler.getInstance(this);
            try {
                Class<?> clazzRuntime = Class.forName("dalvik.system.VMRuntime", false, ClassLoader.getSystemClassLoader());
                Method methodGetRuntime = clazzRuntime.getDeclaredMethod("getRuntime");
                Method methodIs64Bit = clazzRuntime.getDeclaredMethod("is64Bit");
                Log.d(TAG, "VM bits " + methodIs64Bit.invoke(methodGetRuntime.invoke(null)));
            } catch (Exception e) {
                e.printStackTrace();
            }


            //友盟注册
            PushAgent mPushAgent = PushAgent.getInstance(this);
//            //sdk开启通知声音
//            mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
//            // sdk关闭通知声音
//            mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//            // 通知声音由服务端控制
//            mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
//
//            mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//            mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);

            //设置消息和通知的处理
            mPushAgent.setMessageHandler(UmengPushModule.messageHandler);

            //设置通知点击处理者
            mPushAgent.setNotificationClickHandler(UmengPushModule.notificationClickHandler);

            //设置debug状态
            if (BuildConfig.DEBUG) {
                mPushAgent.setDebugMode(true);
            }

            //注册推送服务，每次调用register方法都会回调该接口
            mPushAgent.register(new IUmengRegisterCallback() {
                @Override
                public void onSuccess(String deviceToken) {
                    //注册成功会返回device Token
                    Log.d(TAG, "友盟注册成功：DeviceToken: " + deviceToken);
                }

                @Override
                public void onFailure(String s, String s1) {
                    Log.d(TAG, "友盟注册出错了！s:" + s + ",s1:" + s1);
                }
            });

            //统计应用启动数据
            mPushAgent.onAppStart();

            //设置域名LeCloudPlayerConfig.HOST_DEFAULT代表国内版
            SharedPreferences preferences = getSharedPreferences("host", Context.MODE_PRIVATE);
            int host = preferences.getInt("host", LeCloudPlayerConfig.HOST_DEFAULT);
            try {
                LeCloudPlayerConfig.setHostType(host);
                LeCloudPlayerConfig.init(getApplicationContext());
                LeCloudPlayerConfig.setmInitCmfListener(new OnInitCmfListener() {
                    @Override
                    public void onCdeStartSuccess() {
                        //cde启动成功
                        Log.d(TAG, LogUtils.getTraceInfo() + "onCdeStartSuccess -------");
                    }

                    @Override
                    public void onCdeStartFail() {
                        //cde启动失败
                        //如果使用remote版本则可能是remote下载失败
                        //如果使用普通版本，则可能是so文件加载失败导致
                        Log.d(TAG, LogUtils.getTraceInfo() + "onCdeStartFail -------");
                    }

                    @Override
                    public void onCmfCoreInitSuccess() {
                        //cde启动成功可以开始播放
                        Log.d(TAG, LogUtils.getTraceInfo() + "onCmfCoreInitSuccess -------");

                    }

                    @Override
                    public void onCmfCoreInitFail() {
                        //不包含cde的播放框架需要处理
                        Log.d(TAG, LogUtils.getTraceInfo() + "onCmfCoreInitFail -------");
                    }

                    @Override
                    public void onCmfDisconnected() {
                        //cde服务断开，会导致播放失败，重启一次服务
                        Log.d(TAG, LogUtils.getTraceInfo() + "onCmfDisconnected -------");

                        try {
                            LeCloudPlayerConfig.init(getApplicationContext());
                            Log.d(TAG, LogUtils.getTraceInfo() + "LeCloudPlayerConfig init -------");
                        } catch (Exception e) {
                            Log.e(TAG, LogUtils.getTraceInfo() + "LeCloudPlayerConfig exceptiong -------" + e);
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                // assets目录中必须放三个文件，否则报错
                Log.e(TAG, "cde服务启动报错 exceptiong -------" + e);
                e.printStackTrace();
            }
        }
    }

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {

        @Override
        protected String getJSBundleFile() {
            return CodePush.getJSBundleFile();
        }

        @Override
        protected boolean getUseDeveloperSupport() {
            return com.lecloud.valley.demo.BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new CodePush(com.lecloud.valley.demo.BuildConfig.CODEPUSH_KEY, getApplicationContext(), com.lecloud.valley.demo.BuildConfig.DEBUG),
                    new RJReactPackage()

            );
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }
}
