package com.demoproject.leecoSdk;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.demoproject.R;
import com.lecloud.sdk.listener.MediaDataPlayerListener;
import com.lecloud.sdk.player.IMediaDataPlayer;
import com.lecloud.sdk.player.vod.VodPlayer;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * Created by raojia on 2016/11/10.
 */

public class VideoTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    protected IMediaDataPlayer mMediaPlayer;
    protected ScalableType mScalableType = ScalableType.NONE;

    public VideoTextureView(Context context) {
        this(context, null);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTextureView(Context context, AttributeSet attrs, int defStyle) {
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
        Surface surface = new Surface(surfaceTexture);
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(surface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    protected void onDetachedFromWindow() {
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

//    private void initializeMediaPlayer(Context context) {
//        if (mMediaPlayer == null) {
//            mMediaPlayer = new CPVodPlayer(context);
//
////            mMediaPlayer.setOnMediaDataPlayerListener(this);
//            setSurfaceTextureListener(this);
//        } else {
//            mMediaPlayer.reset();
//        }
//    }


    public void setDataSource(@NonNull Context context, @NonNull String path )  {
//        initializeMediaPlayer(context);
        mMediaPlayer.setDataSource(path);
    }


    public void setDataSource(@NonNull Context context, @NonNull Bundle bundle )  {
//        initializeMediaPlayer(context);
        mMediaPlayer.setDataSourceByMediaData(bundle);
    }


    public void setDataSourceByRate(@NonNull Context context, @NonNull String rate) {
//        initializeMediaPlayer(context);
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
