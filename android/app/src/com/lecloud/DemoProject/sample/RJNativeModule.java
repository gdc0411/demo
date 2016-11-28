package com.lecloud.DemoProject.sample;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by raojia on 16/9/2.
 */
public class RJNativeModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mContext;

    public RJNativeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    /**
     * 通过名字来获得该类的引用
     *
     * @return 原生模块名
     */
    @Override
    public String getName() {
        return "RJNativeModule";
    }

    /**
     * 函数不能有返回值，因为被调用的方法是异步的，
     * 原生代码执行结束之后，通过回调或发送消息给RN
     *
     * @param msg 弹出消息
     */
    @ReactMethod
    public void callNative(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext, VodSDKActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);

    }

    /**
     * 需要通过处理后回调该函数
     *
     * @param msg
     */
    @ReactMethod
    public void callNativeWithResult(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        Bundle bundle = new Bundle();
        mContext.startActivityForResult(intent, 1, bundle);
        //Log.i("消息机制", "成功调起");

    }

    /**
     * 发送RN消息
     *
     * @param msg
     */
    public void sendMsgToRN(String msg) {
        //将消息msg发送给RN侧
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("AndroidToRNMessage", msg);
    }

}
