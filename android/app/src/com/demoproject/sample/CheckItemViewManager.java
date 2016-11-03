package com.demoproject.sample;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

/**
 * Created by LizaRao on 2016/9/5.
 */
public class CheckItemViewManager extends SimpleViewManager<CheckItemView> {

    private ThemedReactContext mContext;

    @Override
    public String getName() {
        return "CheckItemView";
    }

    @Override
    protected CheckItemView createViewInstance(ThemedReactContext reactContext) {
        mContext = reactContext;
        CheckItemView checkItemView = new CheckItemView(reactContext);
        checkItemView.setTitle("设置名");
        checkItemView.setDesc("设置值");
        return checkItemView;
    }

    @ReactProp(name = "title")
    public void setTitle(CheckItemView view, String title) {
        view.setTitle(title);
    }

    @ReactProp(name = "desc")
    public void setDesc(CheckItemView view, String desc) {
        view.setDesc(desc);
    }

    @ReactProp(name = "isChecked")
    public void setChecked(CheckItemView view, boolean isChecked) {
        view.setChecked(isChecked);
    }
}
