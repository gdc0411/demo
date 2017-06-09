package com.lecloud.valley.modules;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

import static com.lecloud.valley.common.Constants.REACT_CLASS_DEVICE_MODULE;

/**
 * Created by admin on 2016/10/26.
 */

public class DeviceModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;

    public DeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS_DEVICE_MODULE;
    }


    /**
     * 获取设备唯一ID
     *
     * @param promise promose对象
     */
    @ReactMethod
    public void getDeviceIdentifier(Promise promise) {
        if (mReactContext != null) {
            final WritableMap map = Arguments.createMap();
            TelephonyManager tm = (TelephonyManager) mReactContext.getSystemService(Context.TELEPHONY_SERVICE);
            map.putString("DeviceId", tm.getDeviceId());      //设备唯一标识
            map.putString("DeviceSoftwareVersion", "Android " + Build.VERSION.RELEASE);  //软件版本号，如6.0.1
            map.putInt("PhoneType", tm.getPhoneType());     //手机制式： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号  PHONE_TYPE_CDMA  CDMA信号
            map.putString("DeviceModel", Build.MODEL);              // 设备型号，如Nexus
            map.putString("DeviceManufacture", Build.MANUFACTURER); //获取手机厂商：LGE
            map.putString("VersionSdk", "4.3.1");         // 播放器SDK版本
            map.putString("VersionRelease", getPackageInfo(mReactContext).versionName); // 软件版本名
            map.putString("PackageName", mReactContext.getPackageName());         //包名
            map.putString("Language", Locale.getDefault().getLanguage());  //语言
            map.putString("Country", Locale.getDefault().getCountry());  //国家
            map.putString("IPAddress", getHostIp());  //IP

            promise.resolve(map);
//            Log.d(TAG, LogUtils.getTraceInfo() + "获取设备唯一ID——— getDeviceIdentifier：" + map.toString());
        } else {
            promise.reject("-1", "无法创建原生桥Session，获取设备ID失败！");
        }
    }


    /***
     * 获取网关IP地址
     * @return
     */
    private static String getHostIp() {
        String address = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        address = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            address = "an error occurred when obtaining ip address";
        }
        return address;
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
