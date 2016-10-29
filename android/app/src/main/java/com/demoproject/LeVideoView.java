package com.demoproject;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.demoproject.utils.VideoLayoutParams;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.letv.android.client.sdk.constant.PlayerEvent;
import com.letv.android.client.sdk.constant.PlayerParams;
import com.letv.android.client.sdk.constant.StatusCode;
import com.letv.android.client.sdk.videoview.IMediaDataVideoView;
import com.letv.android.client.sdk.videoview.VideoViewListener;
import com.letv.android.client.sdk.videoview.base.BaseMediaDataVideoView;
import com.letv.android.client.skin.videoview.live.CPActionLiveVideoView;
import com.letv.android.client.skin.videoview.live.CPLiveVideoView;
import com.letv.android.client.skin.videoview.live.UICPActionLiveVideoView;
import com.letv.android.client.skin.videoview.live.UICPLiveVideoView;
import com.letv.android.client.skin.videoview.pano.live.UICPPanoActionLiveVideoView;
import com.letv.android.client.skin.videoview.pano.live.UICPPanoLiveVideoView;
import com.letv.android.client.skin.videoview.pano.vod.UICPPanoVodVideoView;
import com.letv.android.client.skin.videoview.vod.CPVodVideoView;
import com.letv.android.client.skin.videoview.vod.UICPVodVideoView;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The type Le video view.
 */
public class LeVideoView extends RelativeLayout implements LifecycleEventListener {

    public enum Events {
        EVENT_LOAD_START("onVideoLoadStart"),
        EVENT_LOAD("onVideoLoad"),
        EVENT_ERROR("onVideoError"),
        EVENT_PROGRESS("onVideoProgress"),
        EVENT_SEEK("onVideoSeek"),
        EVENT_END("onVideoEnd"),
        EVENT_STALLED("onPlaybackStalled"),
        EVENT_RESUME("onPlaybackResume"),
        EVENT_READY_FOR_DISPLAY("onReadyForDisplay"),
        EVENT_BUFFER_PERCENT("onBufferPercent");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static final String EVENT_PROP_FAST_FORWARD = "canPlayFastForward";
    public static final String EVENT_PROP_SLOW_FORWARD = "canPlaySlowForward";
    public static final String EVENT_PROP_SLOW_REVERSE = "canPlaySlowReverse";
    public static final String EVENT_PROP_REVERSE = "canPlayReverse";
    public static final String EVENT_PROP_STEP_FORWARD = "canStepForward";
    public static final String EVENT_PROP_STEP_BACKWARD = "canStepBackward";
    public static final String EVENT_PROP_DURATION = "duration";
    public static final String EVENT_PROP_PLAYABLE_DURATION = "playableDuration";
    public static final String EVENT_PROP_CURRENT_TIME = "currentTime";
    public static final String EVENT_PROP_SEEK_TIME = "seekTime";
    public static final String EVENT_PROP_NATURALSIZE = "naturalSize";
    public static final String EVENT_PROP_WIDTH = "width";
    public static final String EVENT_PROP_HEIGHT = "height";
    public static final String EVENT_PROP_ORIENTATION = "orientation";
    public static final String EVENT_PROP_ERROR = "error";
    public static final String EVENT_PROP_WHAT = "what";
    public static final String EVENT_PROP_EXTRA = "extra";

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    private Handler mProgressUpdateHandler = new Handler();
    private Runnable mProgressUpdateRunnable = null;
    private IMediaDataVideoView mMediaPlayer;


    private boolean mPaused = false;
    private float mRate = 1.0f;

    private boolean mMediaPlayerValid = false; // True if mMediaPlayer is in prepared, started, paused or completed state.

    private long mVideoDuration = 0;
    private int mVideoBufferedDuration = 0;
    private boolean isCompleted = false;


    /**
     * The M video view listener.
     */
    VideoViewListener mVideoViewListener = new VideoViewListener() {
        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
            handleAdEvent(event, bundle);//处理广告事件
        }

        @Override
        public String onGetVideoRateList(LinkedHashMap<String, String> map) {
            for (Map.Entry<String, String> rates : map.entrySet()) {
                if (rates.getValue().equals("高清")) {
                    return rates.getKey();
                }
            }
            return "";
        }
    };

    private int mPlayMode = PlayerParams.VALUE_PLAYER_VOD;
    private boolean mHasSkin = false; //是否有皮肤
    private boolean mPano = false;  //是否全景

    public LeVideoView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;

        //创建与RN之间的回调
        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
        mThemedReactContext.addLifecycleEventListener(this);

        //创建播放器及监听
        //initializeMediaPlayerIfNeeded();
        //setSurfaceTextureListener(this);

        //创建播放更新进度线程
        mProgressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayerValid && !isCompleted) {
                    WritableMap event = Arguments.createMap();
                    event.putDouble(EVENT_PROP_CURRENT_TIME, mMediaPlayer.getCurrentPosition() / 1000.0);
                    event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);
                }
                mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, 250);
            }
        };
        mProgressUpdateHandler.post(mProgressUpdateRunnable);
    }

    private void initializeMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {

            mMediaPlayerValid = false;
            //创建播放器和播放容器
            View.inflate(mThemedReactContext, R.layout.video_play, this);
            Context ctx = mThemedReactContext.getBaseContext();
            switch (mPlayMode) {
                case PlayerParams.VALUE_PLAYER_LIVE: {
                    mMediaPlayer = mHasSkin ? (mPano ? new UICPPanoLiveVideoView(ctx) : new UICPLiveVideoView(ctx)) : new CPLiveVideoView(ctx);
                    break;
                }
                case PlayerParams.VALUE_PLAYER_VOD: {
                    mMediaPlayer = mHasSkin ? (mPano ? new UICPPanoVodVideoView(ctx) : new UICPVodVideoView(ctx)) : new CPVodVideoView(ctx);
                    break;
                }
                case PlayerParams.VALUE_PLAYER_ACTION_LIVE: {
                    mMediaPlayer = mHasSkin ? (mPano ? new UICPPanoActionLiveVideoView(ctx) : new UICPActionLiveVideoView(ctx)) : new CPActionLiveVideoView(ctx);
                    break;
                }
                default:
                    mMediaPlayer = new BaseMediaDataVideoView(ctx);
                    break;
            }
            //将播放器放入容器
            RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            videoContainer.addView((View) mMediaPlayer, VideoLayoutParams.computeContainerSize(mThemedReactContext, 16, 9));
            //设置播放器监听器
            mMediaPlayer.setVideoViewListener(mVideoViewListener);
            //mMediaPlayer.setDataSource("http://cache.utovr.com/201601131107187320.mp4");
        }
    }


    /**
     * Cleanup media player resources.
     */
    public void cleanupMediaPlayerResources() {
        if (mMediaPlayer != null) {
            mMediaPlayerValid = false;
            mMediaPlayer.stopAndRelease();
        }
    }


    /**
     * 传入数据源
     *
     * @param bundle 数据源包
     * @return
     */
    public void setDataSource(Bundle bundle) {
        Log.d("setDataSource -------", "Bundle : " + bundle);
        if (bundle == null) return;

        mMediaPlayerValid = false;
        mVideoDuration = 0;
        mVideoBufferedDuration = 0;

        mPlayMode = (bundle.containsKey(LeVideoViewManager.PROP_PLAY_MODE) ? bundle.getInt(LeVideoViewManager.PROP_PLAY_MODE) : -1);
        mPano = (bundle.containsKey(LeVideoViewManager.PROP_SRC_IS_PANO) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_IS_PANO));
        mHasSkin = (bundle.containsKey(LeVideoViewManager.PROP_SRC_HAS_SKIN) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_HAS_SKIN));

        initializeMediaPlayerIfNeeded();

        if (mMediaPlayer != null) {
            mMediaPlayer.resetPlayer();

            if (bundle.containsKey("path"))
                mMediaPlayer.setDataSource(bundle.getString("path"));
            else
                mMediaPlayer.setDataSource(bundle);
            mMediaPlayer.setVideoViewListener(mVideoViewListener);

            WritableMap event = Arguments.createMap();
            event.putString(LeVideoViewManager.PROP_SRC, bundle.toString());
            mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD_START.toString(), event);
        }
    }


    /**
     * Process prepared.
     */
    public void processPrepared() {

        mMediaPlayerValid = true;
        mVideoDuration = mMediaPlayer.getDuration();

        WritableMap naturalSize = Arguments.createMap();
        naturalSize.putInt(EVENT_PROP_WIDTH, mMediaPlayer.getVideoWidth());
        naturalSize.putInt(EVENT_PROP_HEIGHT, mMediaPlayer.getVideoHeight());
        if (mMediaPlayer.getVideoWidth() > mMediaPlayer.getVideoHeight())
            naturalSize.putString(EVENT_PROP_ORIENTATION, "landscape");
        else
            naturalSize.putString(EVENT_PROP_ORIENTATION, "portrait");

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
        event.putDouble(EVENT_PROP_CURRENT_TIME, mMediaPlayer.getCurrentPosition() / 1000.0);
        event.putMap(EVENT_PROP_NATURALSIZE, naturalSize);
        // TODO: Actually check if you can.
        event.putBoolean(EVENT_PROP_FAST_FORWARD, true);
        event.putBoolean(EVENT_PROP_SLOW_FORWARD, true);
        event.putBoolean(EVENT_PROP_SLOW_REVERSE, true);
        event.putBoolean(EVENT_PROP_REVERSE, true);
        event.putBoolean(EVENT_PROP_FAST_FORWARD, true);
        event.putBoolean(EVENT_PROP_STEP_BACKWARD, true);
        event.putBoolean(EVENT_PROP_STEP_FORWARD, true);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD.toString(), event);

        applyModifiers();

    }

    /**
     * Process player info boolean.
     *
     * @param what  the what
     * @param extra the extra
     * @return the boolean
     */
    public boolean processPlayerInfo(int what, int extra) {
        switch (what) {
            case StatusCode.PLAY_INFO_BUFFERING_START:
                //缓冲开始
                mEventEmitter.receiveEvent(getId(), Events.EVENT_STALLED.toString(), Arguments.createMap());
                break;
            case StatusCode.PLAY_INFO_BUFFERING_END:
                //缓冲结束
                mEventEmitter.receiveEvent(getId(), Events.EVENT_RESUME.toString(), Arguments.createMap());
                break;
            case StatusCode.PLAY_INFO_VIDEO_RENDERING_START:
                //渲染第一帧完成
                mEventEmitter.receiveEvent(getId(), Events.EVENT_READY_FOR_DISPLAY.toString(), Arguments.createMap());
                break;
            case StatusCode.PLAY_INFO_VIDEO_BUFFERPERCENT:
                //视频缓冲时的进度，开始转圈
                mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_PERCENT.toString(), Arguments.createMap());
                break;

        }
        return false;
    }

    /**
     * Process buffering update.
     *
     * @param percent the percent
     */
    public void processBufferingUpdate(int percent) {
        mVideoBufferedDuration = percent;
        //mVideoBufferedDuration = (int) Math.round((double) (mVideoDuration * percent) / 100.0);
    }

    /**
     * Process completion.
     */
    public void processCompletion() {
        isCompleted = true;
        mEventEmitter.receiveEvent(getId(), Events.EVENT_END.toString(), null);
    }

    /**
     * Process player error boolean.
     *
     * @param what  the what
     * @param extra the extra
     * @return the boolean
     */
    public boolean processPlayerError(int what, int extra) {
        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_WHAT, what);
        error.putInt(EVENT_PROP_EXTRA, extra);
        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR, error);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), event);
        return true;
    }

    /**
     * Seek to.
     *
     * @param msec the msec
     */
    public void seekTo(int msec) {

        if (mMediaPlayerValid) {
            WritableMap event = Arguments.createMap();
            event.putDouble(EVENT_PROP_CURRENT_TIME, mMediaPlayer.getCurrentPosition() / 1000.0);
            event.putDouble(EVENT_PROP_SEEK_TIME, msec / 1000.0);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK.toString(), event);

            mMediaPlayer.seekTo(msec);
            if (isCompleted && mVideoDuration != 0 && msec < mVideoDuration) {
                isCompleted = false;
            }
        }
    }

    /**
     * Sets paused modifier.
     *
     * @param paused the paused
     */
    public void setPausedModifier(final boolean paused) {
        mPaused = paused;
        if (!mMediaPlayerValid) {
            return;
        }
        if (mPaused) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.onPause();
            }
        } else {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.onStart();
            }
        }
    }

    /**
     * Apply modifiers.
     */
    public void applyModifiers() {
        setPausedModifier(mPaused);
    }


    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        Log.d("handlePlayerEvent", "state " + state + " bundle " + bundle);
        switch (state) {

            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                /**
                 * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
                 * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
                 * 意味着你的surfaceView显示的内容有可能是拉伸的
                 */
                break;

            case PlayerEvent.PLAY_PREPARED: //播放器准备完毕
                // 播放器准备完成
                processPrepared();
                break;

            case PlayerEvent.PLAY_INFO:
                // 获取播放器状态
                processPlayerInfo(bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE), 0);
                break;

            case PlayerEvent.PLAY_LOADINGSTART:
                // 开始缓冲视频
                processPlayerInfo(StatusCode.PLAY_INFO_BUFFERING_START, 0);
                break;

            case PlayerEvent.PLAY_BUFFERING:
                // 获取视频加载值
                processBufferingUpdate(bundle.getInt(PlayerParams.KEY_PLAY_BUFFERPERCENT));
                break;

            case PlayerEvent.PLAY_SEEK_COMPLETE:
                // 用户跳转完毕
                break;


            case PlayerEvent.PLAY_COMPLETION:
                // 播放完毕
                processCompletion();
                break;


            case PlayerEvent.PLAY_ERROR:
                //播放出错
                processPlayerError(PlayerEvent.PLAY_ERROR, 0);

        }
    }


    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
        Log.d("handleLiveEvent", "handleLiveEvent:state " + state + " bundle " + bundle);
        switch (state) {

            case PlayerEvent.MEDIADATA_LIVE:
                // 处理媒资返回直播对应数据
                break;

            case PlayerEvent.MEDIADATA_ACTION:
                // 处理媒资返回活动直播对应数据
                break;

            case PlayerEvent.MEDIADATA_GET_PLAYURL:
                // 处理调度返回（直播、活动直播）对应数据
                break;

        }
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
        Log.d("handleVideoInfoEvent", "handleVideoInfoEvent:state " + state + " bundle " + bundle);
        switch (state) {

            case PlayerEvent.VIEW_PREPARE_VIDEO_SURFACE:
                // 添加了视频播放器SurfaceView
                break;

            case PlayerEvent.MEDIADATA_LIVE:
                // 处理媒资返回直播对应数据
                break;

            case PlayerEvent.MEDIADATA_ACTION:
                // 处理媒资返回活动直播对应数据
                break;

            case PlayerEvent.MEDIADATA_GET_PLAYURL:
                // 处理调度返回（直播、活动直播）对应数据
                break;

        }
    }

    /**
     * 处理广告类事件
     */
    private void handleAdEvent(int state, Bundle bundle) {
        Log.d("handleAdEvent", "handleAdEvent:state " + state + " bundle " + bundle);
        switch (state) {

            case PlayerEvent.VIEW_PREPARE_AD_SURFACE:
                // 添加了广告播放器SurfaceView
                break;

            case PlayerEvent.AD_START:
                // 广告开始播放
                break;

            case PlayerEvent.AD_COMPLETE:
                // 广告结束播放
                break;

            case PlayerEvent.AD_PROGRESS:
                // 广告播放进度
                break;

            case PlayerEvent.AD_ERROR:
                // 广告播放错误
                break;

            default:
                break;
        }
    }

    @Override
    public void onHostResume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.onResume();
        }
    }

    @Override
    public void onHostPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.onPause();
        }
    }

    @Override
    public void onHostDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.onDestroy();
        }
    }
}
