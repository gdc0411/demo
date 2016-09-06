package com.demoproject;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

/**
 * Created by raojia on 16/9/5.
 */
public class LePlayerViewManager extends SimpleViewManager<LePlayerView> {

    private ThemedReactContext mContext;
//    private ReactApplicationContext mContext;


    @Override
    public String getName() {
        return "LePlayerView";
    }


    @Override
    protected LePlayerView createViewInstance(ThemedReactContext reactContext) {
        mContext = reactContext;
        LePlayerView lePlayerView = new LePlayerView(mContext);
        return lePlayerView;
    }



}
