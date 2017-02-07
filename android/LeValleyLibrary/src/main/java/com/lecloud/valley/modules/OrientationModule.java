package com.lecloud.valley.modules;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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
import com.lecloud.valley.utils.OrientationSensorUtils;
import com.lecloud.valley.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.valley.common.Constants.EVENT_PROP_ORIENTATION;
import static com.lecloud.valley.common.Constants.REACT_CLASS_ORIENTATION_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by LizaRao on 2016/12/11.
 */

public class OrientationModule extends ReactBaseModule {

    public OrientationModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();
        Log.d(TAG, LogUtils.getTraceInfo() + "DOWNLOAD模块初始化");
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null)
            func = new OrientationFunc(mReactContext, mEventEmitter);
    }

    @Override
    public String getName() {
        return REACT_CLASS_ORIENTATION_MODULE;
    }


    @ReactMethod
    public void getOrientation(Callback callback) {
        ((OrientationFunc) func).getOrientation(callback);
    }

    @ReactMethod
    public void setOrientation(int requestedOrientation) {
        ((OrientationFunc) func).setOrientation(requestedOrientation);
    }
}
