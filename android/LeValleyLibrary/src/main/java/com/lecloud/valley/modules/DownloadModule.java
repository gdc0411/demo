package com.lecloud.valley.modules;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.DowanloadCenterUtils;
import com.lecloud.valley.utils.LogUtils;
import com.lecloud.valley.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lecloud.valley.common.Constants.PROP_RATE;
import static com.lecloud.valley.common.Constants.PROP_SRC_IS_PANO;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_BUSINESSLINE;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_EXTRA;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_UUID;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_VUID;
import static com.lecloud.valley.common.Constants.REACT_CLASS_DOWNLOAD_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by raojia on 2017/1/23.
 */

public class DownloadModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;
    private RCTNativeAppEventEmitter mEventEmitter;
    private DownloadFunc mDownloadFunc;

    public DownloadModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS_DOWNLOAD_MODULE;
    }

    @Override
    public Map<String, Object> getConstants() {
        return mDownloadFunc.getConstants();
    }

    @Override
    public void initialize() {
        super.initialize();
        Log.d(TAG, LogUtils.getTraceInfo() + "DOWNLOAD模块初始化");
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (mDownloadFunc == null)
            mDownloadFunc = new DownloadFunc(mReactContext, mEventEmitter);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (mDownloadFunc != null) {
            mDownloadFunc.destroy();
            mDownloadFunc = null;
        }
        mEventEmitter = null;
        super.onCatalystInstanceDestroy();
    }

    @ReactMethod
    public void download(final ReadableMap src) {
        mDownloadFunc.download(src);
    }

    @ReactMethod
    public void list() {
        mDownloadFunc.list();
    }

    @ReactMethod
    public void pause(final ReadableMap src) {
        mDownloadFunc.pause(src);
    }

    @ReactMethod
    public void resume(final ReadableMap src) {
        mDownloadFunc.resume(src);
    }


    @ReactMethod
    public void retry(final ReadableMap src) {
        mDownloadFunc.retry(src);
    }

    @ReactMethod
    public void delete(final ReadableMap src) {
        mDownloadFunc.delete(src);
    }

    @ReactMethod
    public void clear() {
        mDownloadFunc.clear();
    }

}
