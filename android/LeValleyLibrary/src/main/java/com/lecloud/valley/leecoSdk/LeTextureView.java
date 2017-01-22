package com.lecloud.valley.leecoSdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.lecloud.valley.R;
import com.lecloud.sdk.api.linepeople.OnlinePeopleChangeListener;
import com.lecloud.sdk.api.status.ActionStatusListener;
import com.lecloud.sdk.api.timeshift.ItimeShiftListener;
import com.lecloud.sdk.listener.AdPlayerListener;
import com.lecloud.sdk.listener.MediaDataPlayerListener;
import com.lecloud.sdk.listener.OnPlayStateListener;
import com.lecloud.sdk.player.IAdPlayer;
import com.lecloud.sdk.player.IMediaDataActionPlayer;
import com.lecloud.sdk.player.IMediaDataLivePlayer;
import com.lecloud.sdk.player.IMediaDataPlayer;
import com.lecloud.sdk.player.IPlayer;
import com.lecloud.valley.utils.LogUtils;
import com.letvcloud.cmf.MediaPlayer;

import static com.facebook.react.common.ReactConstants.TAG;

/**
 * Created by raojia on 2016/11/10.
 */
public class LeTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    protected IPlayer mMediaPlayer;

    protected Surface mSurface;

    protected ScalableType mScalableType = ScalableType.NONE;

    public LeTextureView(Context context) {
        this(context, null);
    }

    public LeTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.scaleStyle, 0, 0);
        if (a == null) {
            return;
        }

        int scaleType = a.getInt(R.styleable.scaleStyle_scalableType, ScalableType.NONE.ordinal());
        a.recycle();
        mScalableType = ScalableType.values()[scaleType];
    }

    protected void initWaterMarkView(Context context) {
//        addView(waterMarkView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureAvailable..." + surfaceTexture);
        mSurface = new Surface(surfaceTexture);
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(mSurface);
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureSizeChanged..." + surface);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureDestroyed..." + surface);
        //todo 删除surface
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureUpdated..." + surface);

    }

//    @Override
//    protected void onDetachedFromWindow() {
////        Log.i(TAG, LogUtils.getTraceInfo() + "onDetachedFromWindow...");
//
//        super.onDetachedFromWindow();
//        if (mMediaPlayer == null) {
//            return;
//        }
//
//        if (isPlaying()) {
//            stop();
//        }
//        release();
//        mMediaPlayer = null;
//    }


    private void scaleVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
            return;
        }

        Size viewSize = new Size(getWidth(), getHeight());
        Size videoSize = new Size(videoWidth, videoHeight);
        ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
        Matrix matrix = scaleManager.getScaleMatrix(mScalableType);
        if (matrix != null) {
            setTransform(matrix);
        }
    }

    public void setOnMediaDataPlayerListener(MediaDataPlayerListener mediaDataPlayerListener) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataPlayer)
            ((IMediaDataPlayer) mMediaPlayer).setOnMediaDataPlayerListener(mediaDataPlayerListener);
    }

    public void setOnAdPlayerListener(AdPlayerListener adPlayerListener) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataPlayer)
            ((IMediaDataPlayer) mMediaPlayer).setOnAdPlayerListener(adPlayerListener);
    }

    public void setOnPlayStateListener(OnPlayStateListener onPlayStateListener) {
        if (mMediaPlayer != null)
            mMediaPlayer.setOnPlayStateListener(onPlayStateListener);
    }

    public void setOnVideoRotateListener(MediaPlayer.OnVideoRotateListener onVideoRotateListener) {
        if (mMediaPlayer != null)
            mMediaPlayer.setOnVideoRotateListener(onVideoRotateListener);
    }

    public void setDataSource(String path) {
        if (mMediaPlayer != null)
            mMediaPlayer.setDataSource(path);
    }


    public void setDataSource(Bundle bundle) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataPlayer)
            ((IMediaDataPlayer) mMediaPlayer).setDataSourceByMediaData(bundle);
    }

    public void setDataSourceByLiveId(String liveId) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataPlayer)
            ((IMediaDataActionPlayer) this.mMediaPlayer).setDataSourceByLiveId(liveId);
    }

    public void setDataSourceByRate(String rate) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataPlayer)
            ((IMediaDataPlayer) mMediaPlayer).setDataSourceByRate(rate);
    }

    public void clearDataSource() {
        if (mMediaPlayer != null)
            mMediaPlayer.clearDataSource();
    }


    public void setDisplay() {
        if (mSurface != null && mMediaPlayer != null)
            mMediaPlayer.setDisplay(mSurface);
    }

    public void setScalableType(ScalableType scalableType) {
        mScalableType = scalableType;
        scaleVideoSize(getVideoWidth(), getVideoHeight());
    }

    public long getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : -1;
    }

    public long getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : -1;
    }

    public int getVideoHeight() {
        return mMediaPlayer != null ? mMediaPlayer.getVideoHeight() : -1;
    }

    public int getVideoWidth() {
        return mMediaPlayer != null ? mMediaPlayer.getVideoWidth() : -1;
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void retry() {
        if (mMediaPlayer != null)
            mMediaPlayer.retry();
    }

    public void pause() {
        if (mMediaPlayer != null)
            mMediaPlayer.pause();
    }

    public void seekTo(long msec) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekTo(msec);
    }

    public void seekTimeShift(long time) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataLivePlayer)
            ((IMediaDataLivePlayer) mMediaPlayer).seekTimeShift(time);
    }

    public void seekToLastPostion(long msec) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekToLastPostion(msec);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void start() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    public void startTimeShift() {
        if (mMediaPlayer instanceof IMediaDataLivePlayer)
            ((IMediaDataLivePlayer) mMediaPlayer).startTimeShift();
    }

    public void stopTimeShift() {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataLivePlayer)
            ((IMediaDataLivePlayer) mMediaPlayer).stopTimeShift();
    }

    public void stop() {
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
    }

    public void setCacheWatermark(int markHigh, int markLow) {
        if (mMediaPlayer != null)
            mMediaPlayer.setCacheWatermark(markHigh, markLow);
    }

    public void setMaxDelayTime(int delayTime) {
        if (mMediaPlayer != null)
            mMediaPlayer.setMaxDelayTime(delayTime);
    }

    public void setCachePreSize(int cachePreSize) {
        if (mMediaPlayer != null)
            mMediaPlayer.setCachePreSize(cachePreSize);
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        if (mMediaPlayer != null)
            mMediaPlayer.setCachePreSize(cacheMaxSize);
    }

    public void clickAd() {
        if (mMediaPlayer != null && mMediaPlayer instanceof IAdPlayer)
            ((IAdPlayer) mMediaPlayer).clickAd();
    }

    public void setTimeShiftListener(ItimeShiftListener itimeShiftListener) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataLivePlayer)
            ((IMediaDataLivePlayer) mMediaPlayer).setTimeShiftListener(itimeShiftListener);
    }

    public void setActionStatusListener(ActionStatusListener actionStatusListener) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataLivePlayer)
            ((IMediaDataActionPlayer) mMediaPlayer).setActionStatusListener(actionStatusListener);
    }

    public void setOnlinePeopleListener(OnlinePeopleChangeListener onlinePeopleChangeListener) {
        if (mMediaPlayer != null && mMediaPlayer instanceof IMediaDataLivePlayer)
            ((IMediaDataActionPlayer) mMediaPlayer).setOnlinePeopleListener(onlinePeopleChangeListener);
    }


    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }

}
