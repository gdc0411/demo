package com.lecloud.valley.modules;


import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.CacheUtils;
import com.lecloud.valley.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

import static com.lecloud.valley.common.Constants.REACT_CLASS_CACHE_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;



/**
 * Created by RaoJia on 2017/1/27.
 */

public class CacheModule extends ReactContextBaseJavaModule {

    private final static int EVENT_CALC_PROGRESS = 0; //计算缓存中
    private final static int EVENT_CALC_SUCCESS = 1; //计算缓存成功
    private final static int EVENT_CALC_FAILED = 2; //计算缓存失败
    private final static int EVENT_CLEAR_PROGRESS = 3; //清除缓存中
    private final static int EVENT_CLEAR_SUCCESS = 4; //清除缓存成功
    private final static int EVENT_CLEAR_FAILED = 5; //清除缓存失败

    private final ReactApplicationContext mReactContext;
    private RCTNativeAppEventEmitter mEventEmitter;

    private Runnable mCalcCacheRunnable = new Runnable() {

        @Override
        public void run() {

            WritableMap event = Arguments.createMap();
            String cacheStr = "无法计算";
            long cacheSize = CacheUtils.getTotalCacheSize(mReactContext);
            if (cacheSize >= 0) {
                cacheStr = CacheUtils.getFormatSize(cacheSize);
                event.putInt("eventType", EVENT_CALC_SUCCESS);
                event.putString("cacheSize", cacheStr);
            } else {
                event.putInt("eventType", EVENT_CALC_FAILED);
                event.putString("cacheSize", cacheStr);
            }
            if (mReactContext.hasActiveCatalystInstance()) {
                if (mEventEmitter != null)
                    mEventEmitter.emit(Events.EVENT_CACHE_UPDATE_MESSAGE.toString(), event);
            } else {
                Log.e(LogUtils.TAG, LogUtils.getTraceInfo() + "not hasActiveCatalystInstance");
            }

        }
    };

    private Runnable mClearCacheRunnable = new Runnable() {

        @Override
        public void run() {

            WritableMap event = Arguments.createMap();
            String cacheStr = "清理缓存失败";
            CacheUtils.clearAllCache(mReactContext);
            long cacheSize = CacheUtils.getTotalCacheSize(mReactContext);
            if (cacheSize == 0) {
                cacheStr = CacheUtils.getFormatSize(cacheSize);
                event.putInt("eventType", EVENT_CLEAR_SUCCESS);
                event.putString("cacheSize", cacheStr);
            } else {
                event.putInt("eventType", EVENT_CLEAR_FAILED);
                event.putString("cacheSize", cacheStr);
            }
            if (mReactContext.hasActiveCatalystInstance()) {
                if (mEventEmitter != null)
                    mEventEmitter.emit(Events.EVENT_CACHE_UPDATE_MESSAGE.toString(), event);
            } else {
                Log.e(LogUtils.TAG, LogUtils.getTraceInfo() + "not hasActiveCatalystInstance");
            }

        }
    };

    public CacheModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS_CACHE_MODULE;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("EVENT_CACHE_UPDATE_MESSAGE", Events.EVENT_CACHE_UPDATE_MESSAGE.toString());
        constants.put("EVENT_CALC_PROGRESS", EVENT_CALC_PROGRESS);
        constants.put("EVENT_CALC_SUCCESS", EVENT_CALC_SUCCESS);
        constants.put("EVENT_CALC_FAILED", EVENT_CALC_FAILED);
        constants.put("EVENT_CLEAR_PROGRESS", EVENT_CLEAR_PROGRESS);
        constants.put("EVENT_CLEAR_SUCCESS", EVENT_CLEAR_SUCCESS);
        constants.put("EVENT_CLEAR_FAILED", EVENT_CLEAR_FAILED);
        return constants;
    }

    @Override
    public void initialize() {
        super.initialize();
        Log.d(TAG, LogUtils.getTraceInfo() + "Cache模块初始化");
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        mEventEmitter = null;
        super.onCatalystInstanceDestroy();
    }

    /**
     * 计算缓存
     */
    @ReactMethod
    public void calc() {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 计算缓存大小！");

        WritableMap event = Arguments.createMap();
        event.putInt("eventType", EVENT_CALC_PROGRESS);
        event.putString("cacheSize", "正在计算");

        if (mReactContext.hasActiveCatalystInstance()) {
            if (mEventEmitter != null)
                mEventEmitter.emit(Events.EVENT_CACHE_UPDATE_MESSAGE.toString(), event);
        } else {
            Log.e(LogUtils.TAG, LogUtils.getTraceInfo() + "not hasActiveCatalystInstance");
        }

        Handler mCalcCacheHandler = new Handler();
        mCalcCacheHandler.post(mCalcCacheRunnable);
    }

    /**
     * 清除缓存
     */
    @ReactMethod
    public void clear() {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 清理缓存！");

        WritableMap event = Arguments.createMap();
        event.putInt("eventType", EVENT_CALC_PROGRESS);
        event.putString("cacheSize", "正在清理缓存");

        if (mReactContext.hasActiveCatalystInstance()) {
            if (mEventEmitter != null)
                mEventEmitter.emit(Events.EVENT_CACHE_UPDATE_MESSAGE.toString(), event);
        } else {
            Log.e(LogUtils.TAG, LogUtils.getTraceInfo() + "not hasActiveCatalystInstance");
        }

        Handler mClearCacheHandler = new Handler();
        mClearCacheHandler.post(mClearCacheRunnable);
    }

}
