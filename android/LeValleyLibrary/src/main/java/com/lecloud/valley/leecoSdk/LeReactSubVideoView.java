/*************************************************************************
 * Description: 乐视视频活动机位播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-18
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.live.ActionLiveSubPlayer;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.base.BaseVideoView;
import com.lecloud.valley.utils.LogUtils;

import java.util.LinkedHashMap;

import static com.lecloud.valley.leecoSdk.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2016/12/18.
 */

public class LeReactSubVideoView extends BaseVideoView implements LifecycleEventListener, VideoViewListener {

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    private BaseSurfaceView mBaseSurfaceView;

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopAndRelease();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder.getSurface());
//            onResume();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }
    };
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mLePlayerValid;

    public LeReactSubVideoView(ThemedReactContext context) {
        super(context);
        this.context = context;
        mThemedReactContext = context;
        mThemedReactContext.addLifecycleEventListener(this);

        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
    }

    protected void initPlayer() {
        mLePlayerValid = false;
        player = new ActionLiveSubPlayer(context);
    }

    @Override
    public void setDataSource(String playUrl) {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 传入机位流地址:" + playUrl);
        super.setDataSource(playUrl);
        setVideoViewListener(this);
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        WritableMap event = Arguments.createMap();
        event.putString(PROP_SRC, playUrl);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_LOAD_SOURCE.toString(), event);
    }


    @Override
    protected void prepareVideoSurface() {
        mBaseSurfaceView = new BaseSurfaceView(context);
        mBaseSurfaceView.getHolder().addCallback(surfaceCallback);
        mBaseSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mBaseSurfaceView.setZOrderOnTop(true);
        setVideoView(mBaseSurfaceView);
    }


    @Override
    protected void notifyPlayerEvent(int state, Bundle bundle) {
        super.notifyPlayerEvent(state, bundle);
        switch (state) {
            case PlayerEvent.PLAY_INIT:
                player.setVolume(0, 0);
                break;
            case PlayerEvent.PLAY_PREPARED:

                onStart();
                break;

            default:
                break;
        }
    }

    /**
     * 处理播放器准备完成事件
     */
    private void processPrepared(int what, Bundle bundle) {
        mLePlayerValid = true;

        //开始封装回调事件参数
        WritableMap event = Arguments.createMap();

        // 视频基本信息，长/宽/方向
        WritableMap naturalSize = Arguments.createMap();
        naturalSize.putInt(EVENT_PROP_WIDTH, mVideoWidth);
        naturalSize.putInt(EVENT_PROP_HEIGHT, mVideoHeight);
        if (mVideoWidth > mVideoHeight)
            naturalSize.putString(EVENT_PROP_VIDEO_ORIENTATION, "landscape");
        else
            naturalSize.putString(EVENT_PROP_VIDEO_ORIENTATION, "portrait");

        if (mEventEmitter != null)
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_LOAD.toString(), event);

        // 执行播放器控制
        onStart();
    }


    /**
     * 处理获得视频真实尺寸的回调
     *
     * @param what   PLAY_VIDEOSIZE_CHANGE
     * @param bundle width,height
     * @return boolean
     */
    private boolean processVideoSizeChanged(int what, Bundle bundle) {
        mVideoWidth = (bundle != null && bundle.containsKey(PlayerParams.KEY_WIDTH)) ? bundle.getInt(PlayerParams.KEY_WIDTH) : -1;
        mVideoHeight = (bundle != null && bundle.containsKey(PlayerParams.KEY_HEIGHT)) ? bundle.getInt(PlayerParams.KEY_HEIGHT) : -1;

        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_WIDTH, mVideoWidth);
        event.putInt(EVENT_PROP_HEIGHT, mVideoHeight);
        if (mEventEmitter != null)
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_CHANGESIZE.toString(), event);
        return true;
    }

    /**
     * 处理视频信息事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    private boolean processPlayerInfo(int what, Bundle bundle) {
        int statusCode = (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_STATUS_CODE)) ? bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE) : -1;

        switch (statusCode) {
            case StatusCode.PLAY_INFO_BUFFERING_START://500004
                //缓冲开始
                if (mEventEmitter != null)
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_BUFFER_START.toString(), null);
                break;
            case StatusCode.PLAY_INFO_BUFFERING_END://500005
                //缓冲结束
                if (mEventEmitter != null)
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_BUFFER_END.toString(), null);
                break;
            case StatusCode.PLAY_INFO_VIDEO_RENDERING_START://500006
                //渲染第一帧完成
                if (mEventEmitter != null)
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_RENDING_START.toString(), null);
                break;

        }
        return false;
    }

    /**
     * 处理出错事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return boolean
     */
    private boolean processError(int what, Bundle bundle) {
        int statusCode = (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_STATUS_CODE)) ? bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE) : -1;
        String errorCode = (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_ERROR_CODE)) ? bundle.getString(PlayerParams.KEY_RESULT_ERROR_CODE) : "";
        String errorMsg = (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_ERROR_MSG)) ? bundle.getString(PlayerParams.KEY_RESULT_ERROR_MSG) : "";

        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_WHAT, what);
        error.putInt(EVENT_PROP_MMS_STATCODE, statusCode);
        error.putString(EVENT_PROP_ERROR_CODE, errorCode);
        error.putString(EVENT_PROP_ERROR_MSG, errorMsg);
        if (mEventEmitter != null)
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_ERROR.toString(), error);
        return true;
    }

    @Override
    public void onStateResult(int event, Bundle bundle) {
        switch (event) {
            case PlayerEvent.PLAY_PREPARED:
                // 播放器准备完成，此刻调用start()就可以进行播放了
                processPrepared(event, bundle);
                break;

            case PlayerEvent.PLAY_ERROR:
                processError(event, bundle);
                break;

            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                processVideoSizeChanged(event, bundle);
                break;

            case PlayerEvent.PLAY_INFO:
                processPlayerInfo(event, bundle);
                break;

            default:
                break;
        }
    }

    @Override
    public String onGetVideoRateList(LinkedHashMap<String, String> linkedHashMap) {
        return null;
    }

    @Override
    public void onResume() {
        mBaseSurfaceView.setVisibility(View.VISIBLE);
        super.onResume();
        if (mEventEmitter != null)
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_RESUME.toString(), null);
    }

    @Override
    public void onDestroy() {
        mBaseSurfaceView.setVisibility(View.GONE);
        super.onDestroy();
        if (mEventEmitter != null)
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SUB_PAUSE.toString(), null);
    }

    public void cleanupMediaPlayerResources() {
        Log.d(TAG, LogUtils.getTraceInfo() + "控件清理 cleanupMediaPlayerResources 调起！");
        if (mLePlayerValid) {
            onDestroy();
        }
    }


    @Override
    public void onHostResume() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostResume 调起！");
        if (mLePlayerValid)
            onResume();
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostPause 调起！");
        if (mLePlayerValid)
            onDestroy();
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostDestroy 调起！");

        if (mLePlayerValid)
            cleanupMediaPlayerResources();
    }
}
