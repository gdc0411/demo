package com.lecloud.DemoProject.modules;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.util.Locale;


/**
 * Created by admin on 2016/10/26.
 */

public class DeviceModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mContext;

    public DeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "DeviceModule";
    }


    /**
     * 获取设备唯一ID
     * @param promise promose对象
     */
    @ReactMethod
    public void getDeviceIdentifier(Promise promise) {
        if (mContext != null){
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

            WritableMap map = Arguments.createMap();
            map.putString("DeviceId", tm.getDeviceId());      //设备唯一标识
            map.putString("DeviceSoftwareVersion", "Android " + Build.VERSION.RELEASE);  //软件版本号，如6.0.1
            map.putInt("PhoneType", tm.getPhoneType());     //手机制式： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号  PHONE_TYPE_CDMA  CDMA信号
            map.putString("DeviceModel", Build.MODEL);              // 设备型号，如Nexus
            map.putString("DeviceManufacture", Build.MANUFACTURER); //获取手机厂商：LGE
            map.putString("VersionSdk", "4.3.1");         // 播放器SDK版本
            map.putString("VersionRelease", getPackageInfo(mContext).versionName ); // 软件版本名
            map.putString("PackageName",mContext.getPackageName());         //包名
            map.putString("Language", Locale.getDefault().getLanguage());  //语言
            map.putString("Country", Locale.getDefault().getCountry());  //国家
            promise.resolve(map);
        }else{
            promise.reject("-1", "无法创建原生桥会话，获取设备ID失败！");
        }
    }


    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }
}
