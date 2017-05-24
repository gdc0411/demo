package com.lecloud.valley.modules;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import static com.lecloud.valley.common.Constants.REACT_CLASS_QQ_MODULE;

/**
 * Created by RaoJia on 2017/05/24.
 */

public class LePayModule extends ReactBaseModule {

    public LePayModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();

        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null) {
            func = new LePayFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS_QQ_MODULE;
    }


    @ReactMethod
    public void pay(ReadableMap payData, Promise promise) {
        ((LePayFunc) func).doPay(payData, promise);
    }

}