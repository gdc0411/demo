package com.demoproject;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

/**
 * Created by LizaRao on 2016/9/4.
 */
public class CallbackModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext mContext;

    public CallbackModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "CallbackModule";
    }

    
}
