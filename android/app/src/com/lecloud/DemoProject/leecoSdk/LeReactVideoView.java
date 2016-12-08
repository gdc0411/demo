/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-07
 ************************************************************************/
package com.lecloud.DemoProject.leecoSdk;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.lecloud.DemoProject.leecoSdk.watermark.WaterMarkView;
import com.lecloud.sdk.api.md.entity.action.CoverConfig;
import com.lecloud.sdk.api.md.entity.action.WaterConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiaRao on 2016/12/07.
 */
public class LeReactVideoView extends RelativeLayout {

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;
    private WaterMarkView mWaterMarkView;

    /// 播放器
    private LeReactPlayer mLePlayer;
    /*============================= 播放器外部接口 ===================================*/

    /*
    * 构造函数
    */
    public LeReactVideoView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;
        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
        mWaterMarkView = new WaterMarkView(mThemedReactContext);
    }

    /**
     * 设置数据源
     *
     * @param bundle 数据源包
     * @return
     */
    public void setSrc(final Bundle bundle) {

        mLePlayer = new LeReactPlayer(mThemedReactContext, mEventEmitter, getId(), mWaterMarkView);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mLePlayer, params);
        addView(mWaterMarkView, params);

        mLePlayer.setSrc(bundle);

//        List<WaterConfig> marks = new ArrayList<>();
//        WaterConfig wc = new WaterConfig("http://i1.letvimg.com/lc04_leju/201601/08/15/24/lecloud/watermarking.png","http://i1.letvimg.com/lc04_leju/201601/08/15/24/lecloud/watermarking.png","2");
//        marks.add(wc);
//        mWaterMarkView.setWaterMarks(marks);

    }


    public void showWaterMark(CoverConfig coverConfig) {
//        if (coverConfig != null && coverConfig.getLoadingConfig() != null && coverConfig.getLoadingConfig().getPicUrl() != null) {
//            mVideoLoading.setLoadingUrl(coverConfig.getLoadingConfig().getPicUrl());
//            mVideoLoading.showLoadingAnimation();
//        }
//        if (coverConfig != null && coverConfig.getWaterMarks() != null && coverConfig.getWaterMarks().size() > 0) {

//        mWaterMarkView.post(new Runnable() {
//            @Override
//            public void run() {
//                List<WaterConfig> marks = new ArrayList<>();
//                WaterConfig wc = new WaterConfig("http://i1.letvimg.com/lc04_leju/201601/08/15/24/lecloud/watermarking.png", "http://i1.letvimg.com/lc04_leju/201601/08/15/24/lecloud/watermarking.png", "2");
//                marks.add(wc);
//                mWaterMarkView.setWaterMarks(marks);
//            }
//        });


//
////            mWaterMarkView.setWaterMarks(coverConfig.getWaterMarks());
////            mWaterMarkView.show();
//        }
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
