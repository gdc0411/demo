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

public class CacheModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;
    private RCTNativeAppEventEmitter mEventEmitter;
    private CacheFunc mCacheFunc;

    public CacheModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS_CACHE_MODULE;
    }


    @Override
    public void initialize() {
        super.initialize();
        Log.d(TAG, LogUtils.getTraceInfo() + "Cache模块初始化");
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (mCacheFunc == null) {
            mCacheFunc = new CacheFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public Map<String, Object> getConstants() {
        return mCacheFunc.getConstants();
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (mCacheFunc != null)
            mCacheFunc = null;

        mEventEmitter = null;
        super.onCatalystInstanceDestroy();
    }

    /**
     * 计算缓存
     */
    @ReactMethod
    public void calc() {
        mCacheFunc.calc();
    }

    /**
     * 清除缓存
     */
    @ReactMethod
    public void clear() {
        mCacheFunc.clear();
    }

}
