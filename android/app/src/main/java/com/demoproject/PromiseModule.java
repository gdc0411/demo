package com.demoproject;

import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by LizaRao on 2016/9/4.
 */
public class PromiseModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mContext;

    public PromiseModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "PromiseModule";
    }


    /**
     * 通过Promise方案调用Native方法
     * @param msg 参数
     * @param promise 必须存在，最后返回的promise对象
     */
    @ReactMethod
    public void promiseTest(String msg, Promise promise) {
        try {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            String componentName = getCurrentActivity().getComponentName().toString();
            promise.resolve(componentName);
        } catch (Exception e) {
            promise.reject("100", e.getMessage());
        }

    }

}
