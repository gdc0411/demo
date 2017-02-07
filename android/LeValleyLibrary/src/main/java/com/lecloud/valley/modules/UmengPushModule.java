package com.lecloud.valley.modules;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2017/1/7.
 */

public class UmengPushModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;
    private RCTNativeAppEventEmitter mEventEmitter;

    public static UmengNotificationClickHandler notificationClickHandler = UmengPushFunc.notificationClickHandler;
    public static UmengMessageHandler messageHandler = UmengPushFunc.messageHandler;

    private UmengPushFunc mUmengPushFunc;

    @Override
    public String getName() {
        return REACT_CLASS_UMENG_PUSH_MODULE;
    }

    public UmengPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        return mUmengPushFunc.getConstants();
    }

    @Override
    public void initialize() {
        super.initialize();
        Log.i(TAG, "PUSH模块初始化");

        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (mUmengPushFunc == null) {
            mUmengPushFunc = new UmengPushFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if(mUmengPushFunc !=null){
            mUmengPushFunc.destroy();
            mUmengPushFunc = null;
        }
        mEventEmitter = null;

        super.onCatalystInstanceDestroy();
    }

}
