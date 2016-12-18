/*************************************************************************
 * Description: 乐视视频活动播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-18
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.valley.utils.LogUtils;

import static com.lecloud.valley.leecoSdk.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2016/12/18.
 */
public class LeReactSubVideoViewManager extends SimpleViewManager<LeReactSubVideoView> {

    private ThemedReactContext mReactContext;

    @Override
    public String getName() {
        return REACT_CLASS_SUB_VIDEO_VIEW;
    }

    @Override
    protected LeReactSubVideoView createViewInstance(ThemedReactContext reactContext) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 createViewInstance 调起！");
        mReactContext = reactContext;
        return new LeReactSubVideoView(mReactContext);
    }

    @Override
    public void onDropViewInstance(LeReactSubVideoView subVideoView) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onDropViewInstance 调起！");
        super.onDropViewInstance(subVideoView);
    }


    /**
     * 设置机位数据源URL
     *
     * @param src 数据源包
     */
    @ReactProp(name = PROP_SRC)
    public void setDataSource(final LeReactSubVideoView subVideoView, final ReadableMap src) {
        if (src == null || !src.hasKey(PROP_SRC_STREAM_URL) || TextUtils.isEmpty(src.getString(PROP_SRC_STREAM_URL)) ) {
            return;
        }
        subVideoView.setDataSource(src.getString(PROP_SRC_STREAM_URL));
    }


}
