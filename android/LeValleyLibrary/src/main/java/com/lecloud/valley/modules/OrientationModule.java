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
import com.lecloud.valley.leecoSdk.Events;
import com.lecloud.valley.utils.LogUtils;
import com.lecloud.valley.utils.OrientationSensorUtils;
import com.lecloud.valley.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.valley.leecoSdk.Constants.EVENT_PROP_ORIENTATION;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by LizaRao on 2016/12/11.
 */

public class OrientationModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private OrientationSensorUtils mOrientationSensorUtils;
    private int mCurrentOritentation;

    private Handler mOrientationChangeHandler;

    @Override
    public String getName() {
        return "OrientationModule";
    }

    public OrientationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        final ReactApplicationContext ctx = reactContext;

        mOrientationChangeHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int orient = -1;
                switch (msg.what) {
                    case OrientationSensorUtils.ORIENTATION_0:// 正横屏
                        orient = 0;
                        break;
                    case OrientationSensorUtils.ORIENTATION_1:// 正竖屏
                        orient = 1;
                        break;
                    case OrientationSensorUtils.ORIENTATION_8:// 反横屏
                        orient = 8;
                        break;
                    case OrientationSensorUtils.ORIENTATION_9:// 反竖屏
                        orient = 9;
                        break;
                }

                if (orient == -1 || orient == mCurrentOritentation) {
                    return;
                }

                WritableMap event = Arguments.createMap();
                event.putInt(EVENT_PROP_ORIENTATION, orient);
                if(ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)!=null)
                    ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(Events.EVENT_ORIENTATION_CHANG.toString(), event);

                Log.d(TAG, LogUtils.getTraceInfo() + "设备转屏事件——— orientation：" + orient);

                super.handleMessage(msg);
            }

        };
        ctx.addLifecycleEventListener(this);
    }

    @ReactMethod
    public void getOrientation(Callback callback) {
        final Activity activity = getCurrentActivity();
        mCurrentOritentation = ScreenUtils.getOrientation(activity);

        String orientation = this.getOrientationString(mCurrentOritentation);

        if (orientation.equals("null")) {
            callback.invoke(mCurrentOritentation, null);
        } else {
            callback.invoke(null, orientation);
        }
    }

    @ReactMethod
    public void setOrientation(int requestedOrientation) {
        if (mCurrentOritentation == requestedOrientation) return;

        final Activity activity = getCurrentActivity();
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

    @Override
    public
    @Nullable
    Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();
        final Activity activity = getCurrentActivity();
        int orientationInt = getReactApplicationContext().getResources().getConfiguration().orientation;
//        int orientationInt = ScreenUtils.getOrientation(activity);

        String orientation = this.getOrientationString(orientationInt);
        if (orientation.equals("null")) {
            constants.put("initialOrientation", null);
        } else {
            constants.put("initialOrientation", orientation);
        }

        return constants;
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
        final Activity activity = getCurrentActivity();
        assert activity != null;
        if (mOrientationSensorUtils == null) {
            mOrientationSensorUtils = new OrientationSensorUtils(activity, mOrientationChangeHandler);
        }
        mOrientationSensorUtils.onResume();
    }

    @Override
    public void onHostPause() {
        final Activity activity = getCurrentActivity();
        if (activity == null)
            return;

        if (mOrientationSensorUtils != null) {
            mOrientationSensorUtils.onPause();
        }

    }

    @Override
    public void onHostDestroy() {
        final Activity activity = getCurrentActivity();
        if (activity == null)
            return;

        if (mOrientationSensorUtils != null) {
            mOrientationChangeHandler.removeCallbacksAndMessages(null);
        }
    }
}
