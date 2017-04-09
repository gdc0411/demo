package com.lecloud.valley.modules;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;

import java.util.Map;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2017/2/7.
 */

public class UmengPushModule extends ReactBaseModule {

    public static UmengNotificationClickHandler notificationClickHandler = UmengPushFunc.notificationClickHandler;
    public static UmengMessageHandler messageHandler = UmengPushFunc.messageHandler;

    public UmengPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS_UMENG_PUSH_MODULE;
    }

    @Override
    public void initialize() {
        super.initialize();
        Log.i(TAG, "PUSH模块初始化");
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (func == null) {
            func = new UmengPushFunc(mReactContext, mEventEmitter);
        }
    }

    /**
     * 获得推送状态
     */
    @ReactMethod
    public void isPushEnabled(Promise promise) {
        ((UmengPushFunc) func).isPushEnabled(promise);
    }


    @ReactMethod
    public void switchPush(int state, Promise promise) {
        Log.i(TAG, "操作PUSH状态："+state);
        ((UmengPushFunc) func).switchPush(state, promise);
    }

}
