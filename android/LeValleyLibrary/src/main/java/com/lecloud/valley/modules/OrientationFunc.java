package com.lecloud.valley.modules;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.lecloud.valley.utils.OrientationSensorUtils;
import com.lecloud.valley.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.valley.common.Constants.EVENT_PROP_ORIENTATION;
import static com.lecloud.valley.common.Constants.REACT_CLASS_ORIENTATION_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by LizaRao on 2016/12/11.
 */

class OrientationFunc implements LifecycleEventListener {

    private final ReactApplicationContext mReactContext;
    private final DeviceEventManagerModule.RCTDeviceEventEmitter mEventEmitter;

    private Handler mOrientationChangeHandler;

    private OrientationSensorUtils mOrientationSensorUtils;
    private int mCurrentOritentation;

    OrientationFunc(ReactApplicationContext reactContext, DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter) {
        mReactContext = reactContext;
        mEventEmitter = eventEmitter;

        initialize();
    }

    private void initialize() {
        Log.d(TAG, LogUtils.getTraceInfo() + "OrientationFunc初始化");

        mReactContext.addLifecycleEventListener(this);

        mOrientationChangeHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int orient = -1;
                switch (msg.what) {
                    case OrientationSensorUtils.ORIENTATION_0:// 正横屏
                        orient = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                    case OrientationSensorUtils.ORIENTATION_1:// 正竖屏
                        orient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                    case OrientationSensorUtils.ORIENTATION_8:// 反横屏
                        orient = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        break;
                    case OrientationSensorUtils.ORIENTATION_9:// 反竖屏
                        orient = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                        break;
                }

                if (orient == -1 || orient == mCurrentOritentation) {
                    return;
                }

                WritableMap event = Arguments.createMap();
                event.putInt(EVENT_PROP_ORIENTATION, orient);
                if (mEventEmitter != null)
                    mEventEmitter.emit(Events.EVENT_ORIENTATION_CHANG.toString(), event);

                Log.d(TAG, LogUtils.getTraceInfo() + "设备转屏事件——— orientation：" + orient + " 设定方向：" + mCurrentOritentation);

                super.handleMessage(msg);
            }

        };
    }

    void destroy() {
        if (mOrientationSensorUtils != null) {
            mOrientationChangeHandler.removeCallbacksAndMessages(null);
        }
    }

    Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();
        final Activity activity = mReactContext.getCurrentActivity();
        int orientationInt = mReactContext.getResources().getConfiguration().orientation;
//        int orientationInt = ScreenUtils.getOrientation(activity);

        String orientation = this.getOrientationString(orientationInt);
        if (orientation.equals("null")) {
            constants.put("initialOrientation", null);
        } else {
            constants.put("initialOrientation", orientation);
        }

        constants.put("EVENT_ORIENTATION_CHANG", Events.EVENT_ORIENTATION_CHANG.toString());
        constants.put("ORIENTATION_LANDSCAPE", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        constants.put("ORIENTATION_PORTRAIT", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        constants.put("ORIENTATION_REVERSE_LANDSCAPE", ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        constants.put("ORIENTATION_REVERSE_PORTRAIT", ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        constants.put("ORIENTATION_UNSPECIFIED", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);


        return constants;
    }


    void getOrientation(Callback callback) {
        final Activity activity = mReactContext.getCurrentActivity();
        mCurrentOritentation = ScreenUtils.getOrientation(activity);

        String orientation = this.getOrientationString(mCurrentOritentation);

        if (orientation.equals("null")) {
            callback.invoke(mCurrentOritentation, null);
        } else {
            callback.invoke(null, orientation);
        }
    }

    void setOrientation(int requestedOrientation) {
        if (mCurrentOritentation == requestedOrientation) return;

        final Activity activity = mReactContext.getCurrentActivity();
        if (activity == null) {
            return;
        }

        switch (requestedOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                activity.setRequestedOrientation(requestedOrientation);
                mCurrentOritentation = requestedOrientation;
                break;

            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                activity.setRequestedOrientation(requestedOrientation);
                mCurrentOritentation = requestedOrientation;
                break;

            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                activity.setRequestedOrientation(requestedOrientation);
                mCurrentOritentation = requestedOrientation;
                break;

            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                activity.setRequestedOrientation(requestedOrientation);
                mCurrentOritentation = requestedOrientation;
                break;

            default:
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
                activity.setRequestedOrientation(requestedOrientation);
                mCurrentOritentation = -1;
                break;
        }
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 设置方向 orientation:" + requestedOrientation);
    }


    private String getOrientationString(int orientation) {
        if (orientation == OrientationSensorUtils.ORIENTATION_0) {
            return "LANDSCAPE-LEFT";
        } else if (orientation == OrientationSensorUtils.ORIENTATION_1) {
            return "PORTRAIT";
        } else if (orientation == OrientationSensorUtils.ORIENTATION_8) {
            return "LANDSCAPE-RIGHT";
        } else if (orientation == OrientationSensorUtils.ORIENTATION_9) {
            return "PORTRAITUPSIDEDOWN";
        } else {
            return "null";
        }
    }

    @Override
    public void onHostResume() {
        final Activity activity = mReactContext.getCurrentActivity();
        assert activity != null;
        if (mOrientationSensorUtils == null) {
            mOrientationSensorUtils = new OrientationSensorUtils(activity, mOrientationChangeHandler);
        }
        mOrientationSensorUtils.onResume();
    }

    @Override
    public void onHostPause() {
        final Activity activity = mReactContext.getCurrentActivity();
        if (activity == null)
            return;

        if (mOrientationSensorUtils != null) {
            mOrientationSensorUtils.onPause();
        }

    }

    @Override
    public void onHostDestroy() {
        final Activity activity = mReactContext.getCurrentActivity();
        if (activity == null)
            return;

        if (mOrientationSensorUtils != null) {
            mOrientationChangeHandler.removeCallbacksAndMessages(null);
        }
    }
}
