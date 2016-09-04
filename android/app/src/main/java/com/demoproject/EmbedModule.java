package com.demoproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.facebook.react.common.ReactConstants.TAG;

/**
 * Created by LizaRao on 2016/9/4.
 */
public class EmbedModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {

    private final ReactApplicationContext mContext;

    public EmbedModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;

        //在构造函数中注册生命周期事件的监听接口
        mContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "EmbedModule";
    }

    /**
     * 需要通过处理后回调该函数
     *
     * @param msg
     */
    @ReactMethod
    public void embedCallWithResult(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        Bundle bundle = new Bundle();

        mContext.addActivityEventListener(this);//增加监听 让当前类的onActivityResult来处理

        mContext.startActivityForResult(intent, 1, bundle);
        //Log.i("嵌入机制","成功调起");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (1 != requestCode || RESULT_OK != resultCode) return;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);

        sendMsgToRN("来自嵌入式的回调结果！当前时间：" + str);
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    /**
     * 发送RN消息
     *
     * @param msg
     */
    public void sendMsgToRN(String msg) {
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("EmbedMessage", msg);
    }


    @Override
    public void onHostResume() {
        Log.i(TAG, "onActivityResume");
    }

    @Override
    public void onHostPause() {
        Log.i(TAG, "onActivityPause");
    }

    @Override
    public void onHostDestroy() {
        Log.i(TAG, "onActivityDestroy");
    }
}
