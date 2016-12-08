/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-07
 ************************************************************************/
package com.lecloud.DemoProject.leecoSdk;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.lecloud.DemoProject.leecoSdk.watermark.WaterMarkSurfaceView;
import com.lecloud.DemoProject.utils.LogUtils;

/**
 * Created by JiaRao on 2016/12/07.
 */
public class LeReactVideoView extends RelativeLayout {

    //定义日志
    public static final String TAG = LogUtils.TAG;

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    /// 播放器
    private LeReactPlayer mLePlayer;
    private WaterMarkSurfaceView mWaterMark;

    /*
    * 构造函数
    */
    public LeReactVideoView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;
        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
        mWaterMark = new WaterMarkSurfaceView(mThemedReactContext);

        ((Activity) context.getBaseContext()).getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ((Activity) context.getBaseContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 设置数据源
     *
     * @param bundle 数据源包
     * @return
     */
    public void setSrc(final Bundle bundle) {
        mLePlayer = new LeReactPlayer(mThemedReactContext, mEventEmitter, getId(), mWaterMark);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mLePlayer, params);
        addView(mWaterMark, params);

        mWaterMark.setZOrderOnTop(true);//处于顶层
        mWaterMark.getHolder().setFormat(PixelFormat.TRANSPARENT);//设置surface为透明

        mLePlayer.setSrc(bundle);
    }


    /**
     * 视频Seek到某一位置
     *
     * @param sec the msec
     */
    public void setSeekTo(final int sec) {
        if (mLePlayer != null)
            mLePlayer.setSeekTo(sec);
    }

    /**
     * 视频切换码率（VOD、LIVE）
     *
     * @param rate 码率值
     */
    public void setRate(String rate) {
        if (mLePlayer != null)
            mLePlayer.setRate(rate);
    }

    /**
     * 云直播切换机位（LIVE）
     *
     * @param liveId 机位ID
     */
    public void setLive(final String liveId) {
        if (mLePlayer != null)
            mLePlayer.setLive(liveId);
    }

    /**
     * 设置视频暂停和启动（VOD、LIVE）
     *
     * @param paused paused
     */
    public void setPaused(final boolean paused) {
        if (mLePlayer != null)
            mLePlayer.setPaused(paused);
    }

    public void setPlayInBackground(final boolean playInBackground) {
        if (mLePlayer != null)
            mLePlayer.setPlayInBackground(playInBackground);
    }

    /**
     * 设置视频暂停和启动（VOD、LIVE）
     *
     * @param clicked 是否点击
     */
    public void setClickAd(final boolean clicked) {
        if (mLePlayer != null)
            mLePlayer.setClickAd(clicked);
    }

    /**
     * 设置左右声道（VOD、LIVE）
     *
     * @param leftVolume  左声道
     * @param rightVolume 右声道
     */
    public void setLeftAndRightTrack(final float leftVolume, final float rightVolume) {
        if (mLePlayer != null)
            mLePlayer.setLeftAndRightTrack(leftVolume, rightVolume);
    }

    /**
     * 音量控制 0-100（VOD、LIVE）
     *
     * @param percentage 音量百分比
     */
    public void setVolumePercent(final int percentage) {
        if (mLePlayer != null)
            mLePlayer.setVolumePercent(percentage);
    }

    /**
     * 设置亮度百分比（VOD、LIVE）
     *
     * @param brightness 取值0-1
     */
    public void setScreenBrightness(final int brightness) {
        if (mLePlayer != null)
            mLePlayer.setScreenBrightness(brightness);
    }

    /**
     * 设置屏幕方向（VOD、LIVE）
     *
     * @param requestedOrientation 设置屏幕方向
     */
    public void setOrientation(int requestedOrientation) {
        if (mLePlayer != null)
            mLePlayer.setOrientation(requestedOrientation);
    }


}
