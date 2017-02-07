package com.lecloud.valley.modules;


import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.utils.LogUtils;

import java.util.Map;

import static com.lecloud.valley.common.Constants.REACT_CLASS_CACHE_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2017/1/27.
 */

public class CacheModule extends ReactBaseModule {

    public CacheModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS_CACHE_MODULE;
    }


    @Override
    public void initialize() {
        super.initialize();
        Log.d(TAG, LogUtils.getTraceInfo() + "Func模块初始化");
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null) {
            func = new CacheFunc(mReactContext, mEventEmitter);
        }
    }


    /**
     * 计算缓存
     */
    @ReactMethod
    public void calc() {
        ((CacheFunc) func).calc();
    }

    /**
     * 清除缓存
     */
    @ReactMethod
    public void clear() {
        ((CacheFunc) func).clear();
    }

}
