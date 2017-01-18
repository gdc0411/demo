/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-07
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by JiaRao on 2016/12/07.
 */
public class LeReactVideoView extends RelativeLayout {

    private ThemedReactContext mThemedReactContext;
    private LeReactPlayer mLePlayer;
    private LeWaterMarkView mWaterMark;

    public LeReactVideoView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;

        mWaterMark = new LeWaterMarkView(mThemedReactContext);
        mWaterMark.setZOrderOnTop(true);
        mWaterMark.getHolder().setFormat(PixelFormat.TRANSPARENT);

        mLePlayer = new LeReactPlayer(mThemedReactContext);
        mLePlayer.setEventEmitter(mThemedReactContext.getJSModule(RCTEventEmitter.class));
        mLePlayer.setWaterMarkSurface(mWaterMark);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mLePlayer, params);
        addView(mWaterMark, params);

    }

    /**
     * 设置数据源
     *
     * @param bundle 数据源包
     * @return
     */
    public void setSrc(final Bundle bundle) {
        if (mLePlayer != null) {
            mLePlayer.setViewId(getId());
            mLePlayer.setSrc(bundle);
        }
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


    /**
     * 设置视频暂停和启动（VOD、LIVE）
     *
     * @param repeat 操作id
     */
    public void setRepeat(final int repeat) {
        if (mLePlayer != null && mLePlayer.isCompleted )
            setSeekTo(0);
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

//    /**
//     * 设置屏幕方向（VOD、LIVE）
//     *
//     * @param requestedOrientation 设置屏幕方向
//     */
//    public void setOrientation(int requestedOrientation) {
//        if (mLePlayer != null)
//            mLePlayer.setOrientation(requestedOrientation);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);// 得到模式
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);// 得到尺寸
//
//        int width = 0;
//        switch (widthMode) {
//            /**
//             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
//             * MeasureSpec.AT_MOST。
//             *
//             *
//             * MeasureSpec.EXACTLY是精确尺寸，
//             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
//             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
//             *
//             *
//             * MeasureSpec.AT_MOST是最大尺寸，
//             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
//             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
//             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
//             *
//             *
//             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
//             * 通过measure方法传入的模式。
//             */
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.EXACTLY:
//                width = widthSize;
//                break;
//        }
//
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);// 得到模式
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);// 得到尺寸
//
//        int height = 0;
//        switch (heightMode) {
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.EXACTLY:
//                height = heightSize;
//                break;
//        }
//
//        if (mWaterMark != null) {
//            mWaterMark.setContainerWidth(width);
//            mWaterMark.setContainerHeight(height);
//            mWaterMark.showWaterMarks();
//        }
//        Log.d(TAG, "onMeasure: "+ width + "," + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.d(TAG, "onSizeChanged: " + w + ',' + h + ',' + oldw + ',' + oldh);
        if (mWaterMark != null) {
            mWaterMark.setContainerWidth(w);
            mWaterMark.setContainerHeight(h);
            mWaterMark.showWaterMarks();
        }
    }
}
