package com.lecloud.valley.modules;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import com.lecloud.valley.utils.LogUtils;

import java.util.Map;

import static com.lecloud.valley.common.Constants.REACT_CLASS_DOWNLOAD_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by raojia on 2017/2/8.
 */

public class DownloadModule extends ReactBaseModule {

    public DownloadModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS_DOWNLOAD_MODULE;
    }


    @Override
    public void initialize() {
        super.initialize();
        Log.d(TAG, LogUtils.getTraceInfo() + "DOWNLOAD模块初始化");
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (func == null)
            func = new DownloadFunc(mReactContext, mEventEmitter);
    }

    @ReactMethod
    public void download(final ReadableMap src) {
        ((DownloadFunc) func).download(src);
    }

    @ReactMethod
    public void list() {
        ((DownloadFunc) func).list();
    }

    @ReactMethod
    public void pause(final ReadableMap src) {
        ((DownloadFunc) func).pause(src);
    }

    @ReactMethod
    public void resume(final ReadableMap src) {
        ((DownloadFunc) func).resume(src);
    }


    @ReactMethod
    public void retry(final ReadableMap src) {
        ((DownloadFunc) func).retry(src);
    }

    @ReactMethod
    public void delete(final ReadableMap src) {
        ((DownloadFunc) func).delete(src);
    }

    @ReactMethod
    public void clear() {
        ((DownloadFunc) func).clear();
    }

}
