package com.lecloud.valley.modules;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2017/1/7.
 */

public class UmengPushModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private RCTNativeAppEventEmitter mEventEmitter;
    private ReactApplicationContext mReactContext;

    private static UmengPushModule gModule = null;

    private static UMessage tmpMessage;

    public static UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void launchApp(Context context, UMessage msg) {
            super.launchApp(context, msg);
            Log.i(TAG, "由消息启动APP");
            clickHandlerSendEvent(msg);
        }

        @Override
        public void openUrl(Context context, UMessage msg) {
            super.openUrl(context, msg);
            clickHandlerSendEvent(msg);
        }

        @Override
        public void openActivity(Context context, UMessage msg) {
            super.openActivity(context, msg);
            clickHandlerSendEvent(msg);
        }

        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            super.dealWithCustomAction(context, msg);
            clickHandlerSendEvent(msg);
        }
    };

    public static UmengMessageHandler messageHandler = new UmengMessageHandler() {
        @Override
        public Notification getNotification(Context context, UMessage msg) {
            messageHandlerSendEvent(msg);
            Log.i(TAG, msg.toString());
            return super.getNotification(context, msg);
        }

        @Override
        public void dealWithCustomMessage(Context context, UMessage msg) {
            super.dealWithCustomMessage(context, msg);
            messageHandlerSendEvent(msg);
        }
    };

    @Override
    public String getName() {
        return REACT_CLASS_UMENG_PUSH_MODULE;
    }


    public UmengPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("EVENT_UMENG_RECV_MESSAGE", Events.EVENT_UMENG_RECV_MESSAGE.toString());
        constants.put("EVENT_UMENG_OPEN_MESSAGE", Events.EVENT_UMENG_OPEN_MESSAGE.toString());
        return constants;
    }

    @Override
    public void initialize() {
        super.initialize();
        Log.i(TAG, "PUSH模块初始化");
        gModule = this;
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        //添加监听
        mReactContext.addLifecycleEventListener(this);

//        if (tmpMessage != null) {
//            gModule.sendEvent(Events.EVENT_UMENG_OPEN_MESSAGE.toString(), tmpMessage);
//            tmpMessage = null;
//        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        gModule = null;
        mEventEmitter = null;
        mReactContext.removeLifecycleEventListener(this);
        super.onCatalystInstanceDestroy();
    }

    /**
     * 点击推送通知触发的事件
     *
     * @param msg
     */
    private static void clickHandlerSendEvent(final UMessage msg) {
        if (gModule == null) {
            //应用退出时，打开推送通知时临时保存的消息
            tmpMessage = msg;
            return;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                gModule.sendEvent(Events.EVENT_UMENG_OPEN_MESSAGE.toString(), msg);
            }
        }, 500); //延时500毫秒发送推送，否则可能收不到
    }

    /**
     * 消息处理触发的事件
     *
     * @param msg
     */
    private static void messageHandlerSendEvent(UMessage msg) {
        if (gModule == null) {
            return;
        }
        gModule.sendEvent(Events.EVENT_UMENG_RECV_MESSAGE.toString(), msg);
    }

    private static WritableMap convertToWriteMap(UMessage msg) {
        WritableMap map = Arguments.createMap();
        //遍历Json
        JSONObject jsonObject = msg.getRaw();
        Iterator<String> keys = jsonObject.keys();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            try {
                map.putString(key, jsonObject.get(key).toString());
            } catch (Exception e) {
                Log.e(TAG, "putString fail");
            }
        }
        return map;
    }


    private void sendEvent(String eventName, UMessage msg) {
        if (msg == null) return;

//        WritableMap params = convertToWriteMap(msg);
        WritableMap event = Arguments.createMap();

        Map<String,String> map = msg.extra;
        if(map!=null && map.size()>0){
            WritableMap para = Arguments.createMap();
            for(Map.Entry<String, String> entry:map.entrySet()){
//                System.out.println(entry.getKey()+"--->"+entry.getValue());
                para.putString(entry.getKey(),entry.getValue());
            }
            event.putMap("extra", para);
        }

        //此处需要添加hasActiveCatalystInstance，否则可能造成崩溃
        //问题解决参考: https://github.com/walmartreact/react-native-orientation-listener/issues/8
        if (mReactContext.hasActiveCatalystInstance()) {
            Log.i(TAG, "hasActiveCatalystInstance");
            if (mEventEmitter != null)
                mEventEmitter.emit(eventName, event);
        } else {
            Log.i(TAG, "not hasActiveCatalystInstance");
        }
    }


    @Override
    public void onHostResume() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostResume 调起！");
//        gModule = this;
//        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostPause 调起！");
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostDestroy 调起！");
//        gModule = null;
//        mEventEmitter = null;
    }

}
