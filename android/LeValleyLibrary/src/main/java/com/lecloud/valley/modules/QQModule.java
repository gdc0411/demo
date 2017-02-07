package com.lecloud.valley.modules;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.SystemUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2016/12/24.
 */

public class QQModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;
    private RCTNativeAppEventEmitter mEventEmitter;
    private QQFunc mQQFunc;

    public QQModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        return mQQFunc.getConstants();
    }

    @Override
    public void initialize() {
        super.initialize();

        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (mQQFunc == null) {
            mQQFunc = new QQFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (mQQFunc != null) {
            mQQFunc.destroy();
            mQQFunc = null;
        }
        mEventEmitter = null;
        super.onCatalystInstanceDestroy();
    }

    @Override
    public String getName() {
        return REACT_CLASS_QQ_MODULE;
    }

    @ReactMethod
    public void getApiVersion(Promise promise) {
        mQQFunc.getApiVersion(promise);
    }

    @ReactMethod
    public void isAppInstalled(Promise promise) {
        mQQFunc.isAppInstalled(promise);
    }

    @ReactMethod
    public void isAppSupportApi(Promise promise) {
        mQQFunc.isAppSupportApi(promise);
    }

    @ReactMethod
    public void login(String scopes, Promise promise) {
        mQQFunc.login(scopes, promise);
    }

    @ReactMethod
    public void shareToQQ(ReadableMap shareToQQ, Promise promise) {
        mQQFunc.shareToQQ(shareToQQ, promise);
    }

    @ReactMethod
    public void shareToQzone(ReadableMap data, Promise promise) {
        mQQFunc.shareToQzone(data, promise);
    }

    @ReactMethod
    public void logout(Promise promise) {
        mQQFunc.logout(promise);
    }

}