package com.lecloud.valley.modules;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
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

import static com.lecloud.valley.common.Constants.REACT_CLASS_UMENG_PUSH_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2017/2/7.
 */

class UmengPushFunc implements ReactBaseFunc, LifecycleEventListener {

    private final ReactApplicationContext mReactContext;
    private final RCTNativeAppEventEmitter mEventEmitter;

    private static UmengPushFunc gModule = null;

    private static UMessage tmpMessage;

    UmengPushFunc(ReactApplicationContext reactContext , RCTNativeAppEventEmitter eventEmitter) {
        mReactContext = reactContext;
        mEventEmitter = eventEmitter;

        initialize();
    }

    public void initialize() {
        Log.i(TAG, "PushFunc初始化");
        gModule = this;

        //添加监听
        mReactContext.addLifecycleEventListener(this);

//        if (tmpMessage != null) {
//            gModule.sendEvent(Events.EVENT_UMENG_OPEN_MESSAGE.toString(), tmpMessage);
//            tmpMessage = null;
//        }
    }

    public void destroy() {
        gModule = null;
        mReactContext.removeLifecycleEventListener(this);
    }

    static UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
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

    static UmengMessageHandler messageHandler = new UmengMessageHandler() {
        /**
         * 自定义通知栏样式的回调方法
         * */
        @Override
        public Notification getNotification(final Context context, final UMessage msg) {
//            Notification notification = super.getNotification(context, msg);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    messageHandlerSendEvent(msg);
                }
            });
            Log.i(TAG, msg.toString());
//            return notification;
            switch (msg.builder_id) {
                case 1: //自定义消息展示
                    Notification.Builder builder = new Notification.Builder(context);
//                    RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
//                    myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                    myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                    myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
//                    myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
//                    builder.setContent(myNotificationView)
//                            .setSmallIcon(getSmallIconId(context, msg))
//                            .setTicker(msg.ticker)
//                            .setAutoCancel(true);
                    return builder.getNotification();
                default:
                    //默认为0，若填写的builder_id并不存在，也使用默认。
                    return super.getNotification(context, msg);
            }
        }

        /**
         * 自定义消息的回调方法
         * */
        @Override
        public void dealWithCustomMessage(final Context context, final UMessage msg) {
            super.dealWithCustomMessage(context, msg);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    messageHandlerSendEvent(msg);
                }
            });
        }
    };


//    public static synchronized UmengPushModule getInstance(Context context) {
//        if(gModule == null) {
//            gModule = new UmengPushModule((ReactApplicationContext) context.getApplicationContext());
//        }
//        return gModule;
//    }


    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("EVENT_UMENG_RECV_MESSAGE", Events.EVENT_UMENG_RECV_MESSAGE.toString());
        constants.put("EVENT_UMENG_OPEN_MESSAGE", Events.EVENT_UMENG_OPEN_MESSAGE.toString());
        return constants;
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
