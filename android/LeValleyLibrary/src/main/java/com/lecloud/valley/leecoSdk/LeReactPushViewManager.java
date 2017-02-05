/*************************************************************************
 * Description: 乐视直播推流组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-02-05
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;

import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.valley.common.Constants.PROP_PAUSED;
import static com.lecloud.valley.common.Constants.PROP_PUSHED;
import static com.lecloud.valley.common.Constants.PROP_PUSH_PARA;
import static com.lecloud.valley.common.Constants.PROP_PUSH_TYPE;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_LECLOUD;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_MOBILE;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_MOBILE_URI;
import static com.lecloud.valley.common.Constants.REACT_CLASS_PUSH_VIEW;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by JiaRao on 2017/02/05.
 */
public class LeReactPushViewManager extends SimpleViewManager<LeReactPushView> {

    private ThemedReactContext mReactContext;

    @Override
    public String getName() {
        return REACT_CLASS_PUSH_VIEW;
    }

    @Override
    protected LeReactPushView createViewInstance(ThemedReactContext reactContext) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 createViewInstance 调起！");
        mReactContext = reactContext;
        return new LeReactPushView(mReactContext);
    }

    @Override
    public void onDropViewInstance(LeReactPushView videoView) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onDropViewInstance 调起！");
        super.onDropViewInstance(videoView);
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    @Override
    @Nullable
    public Map getExportedViewConstants() {
        return MapBuilder.of(
                "PUSH_TYPE_MOBILE_URI", PUSH_TYPE_MOBILE_URI,
                "PUSH_TYPE_MOBILE", PUSH_TYPE_MOBILE,
                "PUSH_TYPE_LECLOUD", PUSH_TYPE_LECLOUD
        );
    }

    /**
     * 设置推流类型和参数
     *
     * @param para 数据源包
     */
    @ReactProp(name = PROP_PUSH_PARA)
    public void setPushPara(final LeReactPushView pushView, final ReadableMap para) {
        if (para == null || !para.hasKey(PROP_PUSH_TYPE) || para.getInt(PROP_PUSH_TYPE) == -1) {
            return;
        }
        int pushType = para.getInt(PROP_PUSH_TYPE);
        Bundle bundle;
        switch (pushType) {
            case PUSH_TYPE_MOBILE_URI:
                bundle = new Bundle();
                bundle.putInt(PROP_PUSH_TYPE, PUSH_TYPE_MOBILE_URI);
                bundle.putString("url", para.hasKey("url") ? para.getString("url") : "");
                bundle.putBoolean("landscape", para.hasKey("landscape") && para.getBoolean("landscape"));
                pushView.setTarget(bundle);
                break;

            case PUSH_TYPE_MOBILE:
                bundle = new Bundle();
                bundle.putInt(PROP_PUSH_TYPE, PUSH_TYPE_MOBILE);
                break;

            case PUSH_TYPE_LECLOUD:
                bundle = new Bundle();
                bundle.putInt(PROP_PUSH_TYPE, PUSH_TYPE_LECLOUD);
                break;

        }

    }


    @ReactProp(name = PROP_PUSHED, defaultBoolean = false)
    public void setPushed(final LeReactPushView pushView, final boolean pushed) {
        pushView.setPushed(pushed);
    }

}
