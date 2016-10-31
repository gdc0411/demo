/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
package com.demoproject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;


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
 * Created by JiaRao on 2016/31/10.
 */
public class LeVideoView extends RelativeLayout implements LifecycleEventListener {

    public enum Events {
        EVENT_LOAD_SOURCE("onSourceLoad"),
        EVENT_LOAD("onVideoLoad"),
        EVENT_ERROR("onVideoError"),
        EVENT_PROGRESS("onVideoProgress"),
        EVENT_PAUSE("onVideoPause"),
        EVENT_RESUME("onVideoResume"),
        EVENT_SEEK("onVideoSeek"),
        EVENT_SEEK_COMPLETE("onVideoSeekComplete"),
        EVENT_END("onVideoEnd"),
        EVENT_BUFFER_START("onBufferStart"),
        EVENT_BUFFER_END("onBufferEnd"),
        EVENT_VIDEO_RENDING_START("onVideoRendingStart"),
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

    public static final String EVENT_PROP_DURATION = "duration";
    public static final String EVENT_PROP_PLAYABLE_DURATION = "playableDuration";
    public static final String EVENT_PROP_CURRENT_TIME = "currentTime";
    public static final String EVENT_PROP_SEEK_TIME = "seekTime";
    public static final String EVENT_PROP_NATURALSIZE = "naturalSize";
    public static final String EVENT_PROP_VIDEO_BUFF = "videobuff";
    public static final String EVENT_PROP_WIDTH = "width";
    public static final String EVENT_PROP_HEIGHT = "height";
    public static final String EVENT_PROP_ORIENTATION = "orientation";
    public static final String EVENT_PROP_ERROR = "error";
    public static final String EVENT_PROP_WHAT = "what";
    public static final String EVENT_PROP_EXTRA = "extra";

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    ///播放器
    private IMediaDataVideoView mLePlayer;
    /// 播放器状态
    private int mPlayMode = PlayerParams.VALUE_PLAYER_VOD;
    private boolean mHasSkin = false; //是否有皮肤
    private boolean mPano = false;  //是否全景
    // 可用状态，prepared, started, paused，completed 时为true
    private boolean mLePlayerValid = false;
    // 暂停状态
    private boolean mPaused = false;

    private long mVideoDuration = 0;
    private int mVideoBufferedDuration = 0;
    // 是否播放完毕
    private boolean isCompleted = false;
    // 是否在缓冲加载状态
    private boolean isSeeking = false;

    /**
     * 播放器回调函数
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

    // 进度更新线程
    private Handler mProgressUpdateHandler = new Handler();
    private Runnable mProgressUpdateRunnable = null;

    /*
    * 构造函数
    */
    public LeVideoView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;

        //创建与RN之间的回调
        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
        mThemedReactContext.addLifecycleEventListener(this);

        //创建播放器及监听
        //initLePlayerIfNeeded();
        //setSurfaceTextureListener(this);

        //创建播放更新进度线程
        mProgressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mLePlayerValid && !mPaused && !isSeeking && !isCompleted) {
                    WritableMap event = Arguments.createMap();
                    event.putDouble(EVENT_PROP_CURRENT_TIME, mLePlayer.getCurrentPosition() / 1000.0);
                    event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                    event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);
                }
                mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, 250);
            }
        };
        mProgressUpdateHandler.post(mProgressUpdateRunnable);
    }

    // 创建播放器及监听
    private void initLePlayerIfNeeded() {
        if (mLePlayer == null) {

            mLePlayerValid = false;
            //创建播放器和播放容器
            View.inflate(mThemedReactContext, R.layout.video_play, this);
            Context ctx = mThemedReactContext.getBaseContext();
            switch (mPlayMode) {
                case PlayerParams.VALUE_PLAYER_LIVE: {
                    mLePlayer = mHasSkin ? (mPano ? new UICPPanoLiveVideoView(ctx) : new UICPLiveVideoView(ctx)) : new CPLiveVideoView(ctx);
                    break;
                }
                case PlayerParams.VALUE_PLAYER_VOD: {
                    mLePlayer = mHasSkin ? (mPano ? new UICPPanoVodVideoView(ctx) : new UICPVodVideoView(ctx)) : new CPVodVideoView(ctx);
                    break;
                }
                case PlayerParams.VALUE_PLAYER_ACTION_LIVE: {
                    mLePlayer = mHasSkin ? (mPano ? new UICPPanoActionLiveVideoView(ctx) : new UICPActionLiveVideoView(ctx)) : new CPActionLiveVideoView(ctx);
                    break;
                }
                default:
                    mLePlayer = new BaseMediaDataVideoView(ctx);
                    break;
            }
            //将播放器放入容器
            RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            videoContainer.addView((View) mLePlayer, VideoLayoutParams.computeContainerSize(mThemedReactContext, 16, 9));

            //设置播放器监听器
            mLePlayer.setVideoViewListener(mVideoViewListener);
            //mLePlayer.setDataSource("http://cache.utovr.com/201601131107187320.mp4");
        }
    }


    /**
     * 销毁播放器，释放资源
     */
    public void cleanupMediaPlayerResources() {
        if (mLePlayer != null) {
            mLePlayerValid = false;
            mLePlayer.stopAndRelease();
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

        mLePlayerValid = false;
        mVideoDuration = 0;
        mVideoBufferedDuration = 0;

        mPlayMode = (bundle.containsKey(LeVideoViewManager.PROP_PLAY_MODE) ? bundle.getInt(LeVideoViewManager.PROP_PLAY_MODE) : -1);
        mPano = (bundle.containsKey(LeVideoViewManager.PROP_SRC_IS_PANO) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_IS_PANO));
        mHasSkin = (bundle.containsKey(LeVideoViewManager.PROP_SRC_HAS_SKIN) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_HAS_SKIN));

        initLePlayerIfNeeded();

        if (mLePlayer != null) {
            mLePlayer.resetPlayer();

            if (bundle.containsKey("path"))
                mLePlayer.setDataSource(bundle.getString("path"));
            else
                mLePlayer.setDataSource(bundle);
            mLePlayer.setVideoViewListener(mVideoViewListener);

            WritableMap event = Arguments.createMap();
            event.putString(LeVideoViewManager.PROP_SRC, bundle.toString());
            mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD_SOURCE.toString(), event);
        }
    }


    /**
     * 处理播放器准备完成事件
     */
    public void processPrepared(int what, Bundle bundle) {

        mLePlayerValid = true;
        mVideoDuration = mLePlayer.getDuration();

        WritableMap naturalSize = Arguments.createMap();
        naturalSize.putInt(EVENT_PROP_WIDTH, mLePlayer.getVideoWidth());
        naturalSize.putInt(EVENT_PROP_HEIGHT, mLePlayer.getVideoHeight());
        //Log.d("视频尺寸", "长度" + mVideoDuration + "宽" + mLePlayer.getVideoWidth() + "高" + mLePlayer.getVideoHeight());
        if (mLePlayer.getVideoWidth() > mLePlayer.getVideoHeight())
            naturalSize.putString(EVENT_PROP_ORIENTATION, "landscape");
        else
            naturalSize.putString(EVENT_PROP_ORIENTATION, "portrait");

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
        event.putDouble(EVENT_PROP_CURRENT_TIME, mLePlayer.getCurrentPosition() / 1000.0);
        event.putMap(EVENT_PROP_NATURALSIZE, naturalSize);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD.toString(), event);

        applyModifiers();

    }

    /**
     * 处理视频信息事件.
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public boolean processPlayerInfo(int what, Bundle bundle) {
        int statusCode = (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_STATUS_CODE)) ?
                bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE) : -1;

        switch (statusCode) {
            case StatusCode.PLAY_INFO_BUFFERING_START://500004
                //缓冲开始
                isSeeking = true;
                mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_START.toString(), null);
                break;
            case StatusCode.PLAY_INFO_BUFFERING_END://500005
                //缓冲结束
                mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_END.toString(), null);
                break;
            case StatusCode.PLAY_INFO_VIDEO_RENDERING_START://500006
                //渲染第一帧完成
                mEventEmitter.receiveEvent(getId(), Events.EVENT_VIDEO_RENDING_START.toString(), null);
                break;
            case StatusCode.PLAY_INFO_VIDEO_BUFFERPERCENT://600006
                //视频缓冲时的进度，开始转圈
                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_VIDEO_BUFF, bundle.containsKey(EVENT_PROP_VIDEO_BUFF) ? bundle.getInt(EVENT_PROP_VIDEO_BUFF) : 0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_PERCENT.toString(), event);
                break;
        }
        return false;
    }

    /**
     * 处理视频开始缓冲事件.
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public boolean processPlayerLoading(int what, Bundle bundle) {
        //视频缓冲时的进度，开始转圈
        isSeeking = true;

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_VIDEO_BUFF, (bundle != null && bundle.containsKey(EVENT_PROP_VIDEO_BUFF)) ? bundle.getInt(EVENT_PROP_VIDEO_BUFF) : 0);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_PERCENT.toString(), event);
        return false;
    }

    /**
     * 处理视频Seek完毕
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public boolean processSeekComplete(int what, Bundle bundle) {
        //视频缓冲时的进度，开始转圈
        mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK_COMPLETE.toString(), null);
        return false;
    }

    /**
     * Process buffering update.
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public void processBufferingUpdate(int what, Bundle bundle) {
        // 正常播放状态
        isSeeking = false;

        mVideoBufferedDuration = bundle.getInt(PlayerParams.KEY_PLAY_BUFFERPERCENT);
        //mVideoBufferedDuration = (int) Math.round((double) (mVideoDuration * percent) / 100.0);
    }

    /**
     * 处理播放完成事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public void processCompletion(int what, Bundle bundle) {
        isCompleted = true;
        mEventEmitter.receiveEvent(getId(), Events.EVENT_END.toString(), null);
    }

    /**
     * 播放器出错
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public boolean processPlayerError(int what, Bundle bundle) {
        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_WHAT, what);
        error.putInt(EVENT_PROP_EXTRA, -1);
        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR, error);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), event);
        return true;
    }

    /**
     * 视频Seek到某一位置
     *
     * @param msec the msec
     */
    public void seekTo(int msec) {

        if (mLePlayerValid) {

            WritableMap event = Arguments.createMap();
            event.putDouble(EVENT_PROP_CURRENT_TIME, mLePlayer.getCurrentPosition() / 1000.0);
            event.putDouble(EVENT_PROP_SEEK_TIME, msec / 1000.0);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK.toString(), event);

            mLePlayer.seekTo(msec);
            if (isCompleted && mVideoDuration != 0 && msec < mVideoDuration) {
                isCompleted = false;
                mLePlayer.retry();
            }
        }
    }

    /**
     * 设置视频暂停和启动
     *
     * @param paused paused
     */
    public void setPausedModifier(final boolean paused) {
        mPaused = paused;
        if (!mLePlayerValid) {
            return;
        }

        if (mPaused) {
            if (mLePlayer.isPlaying()) {
                mLePlayer.onPause();

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, mLePlayer.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PAUSE.toString(), event);
            }
        } else {
            if (!mLePlayer.isPlaying()) {
                mLePlayer.onStart();

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, mLePlayer.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_RESUME.toString(), event);
            }
        }
    }

    /**
     * 根据当前状态设置播放器
     */
    public void applyModifiers() {
        setPausedModifier(mPaused);
    }


    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.PLAY_INIT:
                // 播放器初始化
                handled = true;
                event = "PLAY_INIT";

                //processPlayerInit(PlayerEvent.PLAY_INIT, bundle);
                break;

            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                /**
                 * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
                 * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
                 * 意味着你的surfaceView显示的内容有可能是拉伸的
                 */
                handled = true;
                event = "PLAY_VIDEOSIZE_CHANGED";

                //processVideoSizeChanged(PlayerEvent.PLAY_VIDEOSIZE_CHANGED, bundle);
                break;

            case PlayerEvent.PLAY_PREPARED: //播放器准备完毕
                // 播放器准备完成
                handled = true;
                event = "PLAY_PREPARED";

                processPrepared(PlayerEvent.PLAY_PREPARED, bundle);
                break;

            case PlayerEvent.PLAY_INFO:
                // 获取播放器状态
                handled = true;
                event = "PLAY_INFO";

                processPlayerInfo(PlayerEvent.PLAY_INFO, bundle);
                break;

            case PlayerEvent.PLAY_LOADINGSTART:
                // 开始缓冲视频
                handled = true;
                event = "PLAY_LOADINGSTART";

                processPlayerLoading(PlayerEvent.PLAY_LOADINGSTART, bundle);
                break;

            case PlayerEvent.PLAY_BUFFERING:
                // 获取视频加载值
                handled = true;
                event = "PLAY_BUFFERING";

                processBufferingUpdate(PlayerEvent.PLAY_BUFFERING, bundle);
                break;

            case PlayerEvent.PLAY_SEEK_COMPLETE: //209
                // 用户跳转完毕
                handled = true;
                event = "PLAY_SEEK_COMPLETE";

                processSeekComplete(PlayerEvent.PLAY_SEEK_COMPLETE, bundle);
                break;


            case PlayerEvent.PLAY_COMPLETION://202
                // 播放完毕
                handled = true;
                event = "PLAY_COMPLETION";

                processCompletion(PlayerEvent.PLAY_COMPLETION, bundle);
                break;


            case PlayerEvent.PLAY_ERROR://205
                //播放出错
                handled = true;
                event = "PLAY_ERROR";

                processPlayerError(PlayerEvent.PLAY_ERROR, bundle);

        }
        if (handled)
            Log.d("播放器事件", "event " + event + " state " + state + " bundle " + bundle);
    }


    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.MEDIADATA_LIVE:
                // 处理媒资返回直播对应数据
                handled = true;
                event = "MEDIADATA_LIVE";

                break;

            case PlayerEvent.MEDIADATA_ACTION:
                // 处理媒资返回活动直播对应数据
                handled = true;
                event = "MEDIADATA_ACTION";

                break;

            case PlayerEvent.MEDIADATA_GET_PLAYURL:
                // 处理调度返回（直播、活动直播）对应数据
                handled = true;
                event = "MEDIADATA_GET_PLAYURL";

                break;

        }
        if (handled)
            Log.d("直播事件", "event " + event + " state " + state + " bundle " + bundle);
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.VIEW_PREPARE_VIDEO_SURFACE:
                // 添加了视频播放器SurfaceView
                handled = true;
                event = "VIEW_PREPARE_VIDEO_SURFACE";

                break;

            case PlayerEvent.VIEW_PREPARE_AD_SURFACE:
                // 添加了广告播放器SurfaceView
                handled = true;
                event = "VIEW_PREPARE_AD_SURFACE";

                break;

            case PlayerEvent.MEDIADATA_GET_PLAYURL:
                // 处理调度返回（直播、活动直播）对应数据
                handled = true;
                event = "MEDIADATA_GET_PLAYURL";

                break;

        }
        if (handled)
            Log.d("视频信息事件", "event " + event + " state " + state + " bundle " + bundle);
    }

    /**
     * 处理广告类事件
     */
    private void handleAdEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.AD_START:
                // 广告开始播放
                handled = true;
                event = "AD_START";

                break;

            case PlayerEvent.AD_COMPLETE:
                // 广告结束播放
                handled = true;
                event = "AD_COMPLETE";

                break;

            case PlayerEvent.AD_PROGRESS:
                // 广告播放进度
                handled = true;
                event = "AD_PROGRESS";

                break;

            case PlayerEvent.AD_ERROR:
                // 广告播放错误
                handled = true;
                event = "AD_ERROR";

                break;

            default:
                break;
        }
        if (handled)
            Log.d("广告事件", "event " + event + " state " + state + " bundle " + bundle);
    }

    @Override
    public void onHostResume() {
        if (mLePlayer != null) {
            mLePlayer.onResume();
        }
    }

    @Override
    public void onHostPause() {
        if (mLePlayer != null) {
            mLePlayer.onPause();
        }
    }

    @Override
    public void onHostDestroy() {
        if (mLePlayer != null) {
            mLePlayer.onDestroy();
        }
    }
}
