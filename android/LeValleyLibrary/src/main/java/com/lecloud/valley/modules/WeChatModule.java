package com.lecloud.valley.modules;

import android.content.Intent;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import java.util.Map;

import static com.lecloud.valley.common.Constants.*;

/**
 * Created by raojia on 2016/12/20.
 */
public class WeChatModule extends ReactBaseModule {

    public WeChatModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null) {
            func = new WeChatFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS_WECHAT_MODULE;
    }

    /**
     * 微信是否安装
     */
    @ReactMethod
    public void isAppInstalled(Promise promise) {
        ((WeChatFunc)func).isAppInstalled(promise);
    }

    /**
     * 微信版本是否支持API
     */
    @ReactMethod
    public void isAppSupportApi(Promise promise) {
        ((WeChatFunc)func).isAppSupportApi(promise);
    }

    /**
     * 获得微信版本
     */
    @ReactMethod
    public void getApiVersion(Promise promise) {
        ((WeChatFunc)func).getApiVersion(promise);
    }

    /**
     * 调起微信APP
     */
    @ReactMethod
    public void openApp(Promise promise) {
        ((WeChatFunc)func).openApp(promise);
    }

    /**
     * 微信登陆
     */
    @ReactMethod
    public void sendAuth(ReadableMap config, Promise promise) {
        ((WeChatFunc)func).sendAuth(config, promise);
    }


    /**
     * 微信分享朋友圈
     */
    @ReactMethod
    public void shareToTimeline(ReadableMap data, Promise promise) {
        ((WeChatFunc)func).shareToTimeline(data, promise);
    }

    /**
     * 微信分享好友
     */
    @ReactMethod
    public void shareToSession(ReadableMap data, Promise promise) {
        ((WeChatFunc)func).shareToSession(data, promise);
    }

    /**
     * 微信支付
     */
    @ReactMethod
    public void pay(ReadableMap data, Promise promise) {
        ((WeChatFunc)func).pay(data, promise);
    }


    public static void handleIntent(Intent intent) {
        WeChatFunc.handleIntent(intent);
    }

}
