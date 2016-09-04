package com.demoproject;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;

/**
 * Created by LizaRao on 2016/9/4.
 */
public class CallbackModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mContext;

    public CallbackModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "CallbackModule";
    }

    /**
     * 通过CallBack方案实现Native通讯
     *
     * @param successBack 处理成功时，回调RN定义的成功方法
     * @param errorBack   处理失败时的处理
     */
    @ReactMethod
    public void callbackTest(Callback successBack, Callback errorBack) {
        try {
            successBack.invoke(true, "Native回调成功！", 8);
        } catch (IllegalViewOperationException e) {
            errorBack.invoke(e);
        }

    }
}
