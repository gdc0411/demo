package com.lecloud.DemoProject.sample;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by LizaRao on 2016/9/4.
 */
public class ConstModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext mContext;

    public ConstModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "ConstModule";
    }


    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("URL","http://www.lecloud.com");
        constants.put("port","8081");
        constants.put("ip","192.168.11.102");
        return constants;
    }
}
