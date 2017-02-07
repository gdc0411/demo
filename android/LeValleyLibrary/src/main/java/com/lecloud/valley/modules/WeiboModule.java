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
 * Created by raojia on 2016/12/26.
 */
public class WeiboModule extends ReactBaseModule {

    public WeiboModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null) {
            func = new WeiboFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS_WEIBO_MODULE;
    }


    @ReactMethod
    public void getApiVersion(Promise promise) {
        ((WeiboFunc) func).getApiVersion(promise);
    }

    @ReactMethod
    public void isAppInstalled(Promise promise) {
        ((WeiboFunc) func).isAppInstalled(promise);
    }

    @ReactMethod
    public void isAppSupportApi(Promise promise) {
        ((WeiboFunc) func).isAppSupportApi(promise);
    }

    @ReactMethod
    public void login(final ReadableMap config, final Promise promise) {
        ((WeiboFunc) func).login(config, promise);
    }

    @ReactMethod
    public void shareToWeibo(final ReadableMap data, final Promise promise) {
        ((WeiboFunc) func).shareToWeibo(data, promise);
    }

}
