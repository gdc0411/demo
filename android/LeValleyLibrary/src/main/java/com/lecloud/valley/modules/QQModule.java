package com.lecloud.valley.modules;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import java.util.Map;

import static com.lecloud.valley.common.Constants.*;

/**
 * Created by RaoJia on 2016/12/24.
 */

public class QQModule extends ReactBaseModule {

    public QQModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();

        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null) {
            func = new QQFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS_QQ_MODULE;
    }

    @ReactMethod
    public void getApiVersion(Promise promise) {
        ((QQFunc) func).getApiVersion(promise);
    }

    @ReactMethod
    public void isAppInstalled(Promise promise) {
        ((QQFunc) func).isAppInstalled(promise);
    }

    @ReactMethod
    public void isAppSupportApi(Promise promise) {
        ((QQFunc) func).isAppSupportApi(promise);
    }

    @ReactMethod
    public void login(String scopes, Promise promise) {
        ((QQFunc) func).login(scopes, promise);
    }

    @ReactMethod
    public void shareToQQ(ReadableMap shareToQQ, Promise promise) {
        ((QQFunc) func).shareToQQ(shareToQQ, promise);
    }

    @ReactMethod
    public void shareToQzone(ReadableMap data, Promise promise) {
        ((QQFunc) func).shareToQzone(data, promise);
    }

    @ReactMethod
    public void logout(Promise promise) {
        ((QQFunc) func).logout(promise);
    }

}