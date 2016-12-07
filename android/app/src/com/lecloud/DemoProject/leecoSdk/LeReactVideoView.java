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

/**
 * Created by JiaRao on 2016/12/07.
 */
public class LeReactVideoView extends RelativeLayout {

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

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

//        View.inflate(context, R.layout.view_check_item, this);
    }

    /**
     * 设置数据源
     *
     * @param bundle 数据源包
     * @return
     */
    public void setSrc(final Bundle bundle) {

        WaterMarkView waterMarkView = new WaterMarkView(mThemedReactContext);

        mLePlayer = new LeReactPlayer(mThemedReactContext, mEventEmitter, getId(), waterMarkView);
//        mLePlayer.setAlpha(0.1f);

        //将播放器放入容器
//        View.inflate(mThemedReactContext, R.layout.video_play, this);
//        RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
//        videoContainer.addView(mLePlayer, VideoLayoutParams.computeContainerSize(mThemedReactContext, 16, 9));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mLePlayer, params);
        
//        List<WaterConfig> marks = new ArrayList<WaterConfig>();
//        WaterConfig wc = new WaterConfig("http://i1.letvimg.com/lc04_leju/201601/08/15/24/lecloud/watermarking.png","http://i1.letvimg.com/lc04_leju/201601/08/15/24/lecloud/watermarking.png","2");
//        marks.add(wc);
//        waterMarkView.setWaterMarks(marks);
        addView(waterMarkView, params);

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
