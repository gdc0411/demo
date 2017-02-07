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

public class ReactBaseModule extends ReactContextBaseJavaModule {

    protected final ReactApplicationContext mReactContext;
    protected RCTNativeAppEventEmitter mEventEmitter;

    ReactBaseFunc func;

    public ReactBaseModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        return func != null ? func.getConstants() : null;
    }

    @Override
    public String getName() {
        return "NONAME";
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (func != null) {
            func.destroy();
            func = null;
        }

        mEventEmitter = null;
        super.onCatalystInstanceDestroy();
    }
}
