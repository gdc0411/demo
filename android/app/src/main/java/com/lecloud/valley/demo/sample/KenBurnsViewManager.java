package com.lecloud.valley.demo.sample;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.lecloud.demo.R;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.flaviofaria.kenburnsview.KenBurnsView;

/**
 * Created by LizaRao on 2016/9/4.
 */
public class KenBurnsViewManager extends SimpleViewManager<KenBurnsView> {

    private ThemedReactContext mContext;

    @Override
    public String getName() {
        return "KenBurnsView";
    }

    @Override
    protected KenBurnsView createViewInstance(ThemedReactContext reactContext) {
        mContext = reactContext;
        KenBurnsView kenBurnsView = new KenBurnsView(mContext);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.pic01);
        kenBurnsView.setImageDrawable(drawable);
        return kenBurnsView;
    }

    @ReactProp(name = "picName")
    public void setPic(KenBurnsView view, String picName) {
        //根据名字获取资源ID
        Resources res = mContext.getResources();
        int picId = res.getIdentifier(picName, "drawable", mContext.getPackageName());
        if (picId != 0) {
            Drawable drawable = mContext.getResources().getDrawable(picId);
            view.setImageDrawable(drawable);
        }
    }
}
