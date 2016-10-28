package com.demoproject;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

/**
 * Created by LizaRao on 2016/21/10.
 */
public class LeVideoViewManager extends SimpleViewManager<LeVideoView> {

    private static final String REACT_CLASS = "LeVideoView";

    private ThemedReactContext mContext;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected LeVideoView createViewInstance(ThemedReactContext reactContext) {
        mContext = reactContext;
        LeVideoView leVideoView = new LeVideoView(mContext);
        return leVideoView;
    }

    @ReactProp(name = "dataSource")
    public void setDataSource(LeVideoView view, String playUrl) {
        view.setDataSource(playUrl);
    }

}
