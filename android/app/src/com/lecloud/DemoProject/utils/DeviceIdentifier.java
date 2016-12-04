package com.lecloud.DemoProject.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;


/**
 * Created by admin on 2016/10/26.
 */

public class DeviceIdentifier extends ReactContextBaseJavaModule {
    private static ReactContext mReactContext;

    public DeviceIdentifier(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "DeviceIdentifier";
    }


    /**
     * 获取设备唯一ID
     * @param promise promose对象
     */
    @ReactMethod
    public void getDeviceIdentifier(Promise promise) {
        if (mReactContext != null){
            TelephonyManager tm = (TelephonyManager) mReactContext.getSystemService(Context.TELEPHONY_SERVICE);

            WritableMap map = Arguments.createMap();
            map.putString("DeviceId", tm.getDeviceId());      //设备唯一标识
            map.putString("DeviceSoftwareVersion", tm.getDeviceSoftwareVersion());  //设备的软件版本号
            map.putInt("PhoneType", tm.getPhoneType());                             //手机类型： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号  PHONE_TYPE_CDMA  CDMA信号
            map.putString("DeviceModel", Build.MODEL);              // 设备型号
            map.putString("DeviceManufacture", Build.MANUFACTURER); //获取手机厂商：
            map.putString("VersionSdk", Build.VERSION.SDK);         // 设备SDK版本
            map.putString("VersionRelease", Build.VERSION.RELEASE); // 设备的系统版本
            map.putString("PackageName",getAppInfo(mReactContext));            //包名

            promise.resolve(map);
        }else{
            promise.reject("-1", "无法创建原生桥会话，获取设备ID失败！");
        }
    }


    private String getAppInfo(ReactContext mReactContext) {
        try {
            String pkName = mReactContext.getPackageName();
//            String versionName = mReactContext.getPackageManager().getPackageInfo(pkName, 0).versionName;
//            int versionCode = mReactContext.getPackageManager().getPackageInfo(pkName, 0).versionCode;
            return pkName;
        } catch (Exception e) {
        }
        return null;
    }
}
