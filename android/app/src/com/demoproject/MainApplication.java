package com.demoproject;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.demoproject.handler.CrashHandler;
import com.demoproject.leecoSdk.watermark.Constant;
import com.demoproject.utils.LogUtils;
import com.facebook.react.ReactApplication;
import com.letv.android.client.sdk.config.LeCloudPlayerConfig;
import com.letv.android.client.sdk.listener.OnInitCmfListener;
import com.microsoft.codepush.react.CodePush;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

    public static final String TAG = Constant.TAG;

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
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

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
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new CodePush(BuildConfig.CODEPUSH_KEY, getApplicationContext(), BuildConfig.DEBUG),
                    new RJReactPackage()

            );
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }
}
