package com.demoproject.leecoSdk;

import android.content.Context;
import android.os.Bundle;

import com.facebook.react.bridge.LifecycleEventListener;
import com.lecloud.sdk.videoview.VideoViewListener;

import java.util.LinkedHashMap;

/**
 * Created by raojia on 2016/11/10.
 */

public class ReactVideoView extends VideoTextureView implements VideoViewListener, LifecycleEventListener {

    public ReactVideoView(Context context) {
        super(context);
    }


    @Override
    public void onStateResult(int i, Bundle bundle) {

    }

    @Override
    public String onGetVideoRateList(LinkedHashMap<String, String> linkedHashMap) {
        return null;
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }
}
