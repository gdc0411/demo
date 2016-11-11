package com.demoproject.leecoSdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;


import com.lecloud.app.openappdev.R;
import com.lecloud.app.openappdev.utils.LogUtils;
import com.lecloud.sdk.player.IMediaDataPlayer;


/**
 * Created by raojia on 2016/11/10.
 */
public class LeTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    //定义日志
    public static final String TAG = LogUtils.TAG;


    protected IMediaDataPlayer mMediaPlayer;
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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
//        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureAvailable..." + surfaceTexture);
        Surface surface = new Surface(surfaceTexture);
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(surface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureSizeChanged..." + surface);

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureAvailable..." + surface);

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        Log.i(TAG, LogUtils.getTraceInfo() + "onSurfaceTextureUpdated..." + surface);

    }

    @Override
    protected void onDetachedFromWindow() {
//        Log.i(TAG, LogUtils.getTraceInfo() + "onDetachedFromWindow...");

        super.onDetachedFromWindow();
        if (mMediaPlayer == null) {
            return;
        }

        if (isPlaying()) {
            stop();
        }
        release();
        mMediaPlayer = null;
    }



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


    public void setDataSource(@NonNull Context context, @NonNull String path )  {
        mMediaPlayer.setDataSource(path);
    }


    public void setDataSource(@NonNull Context context, @NonNull Bundle bundle )  {
        mMediaPlayer.setDataSourceByMediaData(bundle);
    }


    public void setDataSourceByRate(@NonNull Context context, @NonNull String rate) {
        mMediaPlayer.setDataSourceByRate(rate);
    }


    public void setScalableType(ScalableType scalableType) {
        mScalableType = scalableType;
        scaleVideoSize(getVideoWidth(), getVideoHeight());
    }


    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }


    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }


    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void release() {
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

}
