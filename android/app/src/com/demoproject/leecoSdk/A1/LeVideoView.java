/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
package com.demoproject.leecoSdk.A1;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.demoproject.R;
import com.demoproject.leecoSdk.A2.UICPVodVideoView;
import com.demoproject.leecoSdk.Events;
import com.demoproject.utils.LogUtils;
import com.demoproject.utils.ScreenBrightnessManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import com.lecloud.sdk.api.md.entity.action.CoverConfig;
import com.lecloud.sdk.api.md.entity.action.WaterConfig;
import com.lecloud.sdk.api.md.entity.vod.VideoHolder;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.IMediaDataPlayer;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.letv.android.client.cp.sdk.videoview.live.CPActionLiveVideoView;
import com.letv.android.client.cp.sdk.videoview.live.CPLiveVideoView;
import com.letv.android.client.cp.sdk.videoview.vod.CPVodVideoView;
import com.letv.android.client.skin.videoview.live.UICPActionLiveVideoView;
import com.letv.android.client.skin.videoview.live.UICPLiveVideoView;
import com.letv.android.client.skin.videoview.live.UICPPanoActionLiveVideoView;
import com.letv.android.client.skin.videoview.live.UICPPanoLiveVideoView;
import com.letv.android.client.skin.videoview.vod.UICPPanoVodVideoView;

import static com.demoproject.leecoSdk.Constants.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by JiaRao on 2016/31/10.
 */
public class LeVideoView extends RelativeLayout implements LifecycleEventListener {

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    /// 播放器
    private IMediaDataVideoView mLeVideoView;
    private IMediaDataPlayer mLePlayer;

    // 设备信息
    private final AudioManager mAudioManager;
    private final int mCurrentBrightness;


    /// 播放器设置
    private int mPlayMode = PlayerParams.VALUE_PLAYER_VOD;
    private boolean mHasSkin = false; //是否有皮肤
    private boolean mPano = false;  //是否全景

    private boolean mLePlayerValid = false;  // 可用状态，prepared, started, paused，completed 时为true

    /*
    * VOD媒资信息
    */
    private String mVideoTitle; // 点播视频标题
    private LinkedHashMap<String, String> mRateList;  // 当前支持的码率
    private String mDefaultRate;  // 当前视频默认码率
    private long mVideoDuration = 0;  //当前视频总长
    private int mVideoWidth;  //视频实际宽度
    private int mVideoHeight;  //视频实际高度
    private CoverConfig mCoverConfig;  //LOGO，加载图，水印等信息


    /*
    * VOD视频状态信息
    */
    private String mCurrentRate;  // 当前视频码率
    private boolean mPaused = false;  // 暂停状态
    private long mLastPosition;  //上次播放位置
    private int mVideoBufferedDuration = 0; //当前已缓冲长度
    private int mMediaStatusCode = 0; //媒资返回状态
    private int mMediaHttpCode = 0; //媒资返回值


    private boolean isCompleted = false;   // 是否播放完毕
    private boolean isSeeking = false;  // 是否在缓冲加载状态

    /**
     * 播放器回调函数
     */
    VideoViewListener mVideoViewListener = new VideoViewListener() {
        @Override
        public void onStateResult(int event, Bundle bundle) {
            handlePlayerEvent(event, bundle);// 处理播放器类事件
            handleVideoInfoEvent(event, bundle);// 处理媒资类事件
            handleAdEvent(event, bundle);//处理广告类事件
            handleOtherEvent(event, bundle);//处理其他事件
        }

        @Override
        public String onGetVideoRateList(LinkedHashMap<String, String> map) {
            return null;
        }
    };

    // 进度更新线程
    private Handler mProgressUpdateHandler = new Handler();
    private Runnable mProgressUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            if (mLePlayerValid && !mPaused && !isSeeking && !isCompleted) {
                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);
            }
            mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, 250);
        }
    };


    /*============================= 播放器外部接口 ===================================*/

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
//        initLePlayerIfNeeded();
        //setSurfaceTextureListener(this);

        //创建播放更新进度线程
//        mProgressUpdateRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                if (mLePlayerValid && !mPaused && !isSeeking && !isCompleted) {
//                    WritableMap event = Arguments.createMap();
//                    event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
//                    event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
//                    event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
//                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);
//                }
//                mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, 250);
//            }
//        };
//        mProgressUpdateHandler.post(mProgressUpdateRunnable);


        //设置声音管理器
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);

        // 获得当前屏幕亮度 取值0-255
        mCurrentBrightness = ScreenBrightnessManager.getScreenBrightness(context.getBaseContext());

    }

    // 创建播放器及监听
    private void initLePlayerIfNeeded() {
        if (mLeVideoView == null) {
            mLePlayerValid = false;

            Context ctx = mThemedReactContext.getBaseContext();

            ((Activity) ctx).getWindow().setFormat(PixelFormat.TRANSLUCENT);
            ((Activity) ctx).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            switch (mPlayMode) {
                case PlayerParams.VALUE_PLAYER_LIVE:
                    mLeVideoView = mHasSkin ? (mPano ? new UICPPanoLiveVideoView(ctx)
                            : new UICPLiveVideoView(ctx))
                            : new CPLiveVideoView(ctx);

//                    mLePlayer = mHasSkin ? (mPano ? (IMediaDataPlayer) ((UICPPanoLiveVideoView) mLeVideoView).getPlayer()
//                            : (IMediaDataPlayer) ((UICPLiveVideoView) mLeVideoView).getPlayer())
//                            : (IMediaDataPlayer) ((CPLiveVideoView) mLeVideoView).getPlayer();
                    break;

                case PlayerParams.VALUE_PLAYER_VOD:
                    mLeVideoView = mHasSkin ? (mPano ? new UICPPanoVodVideoView(ctx)
                            : new UICPVodVideoView(ctx))
                            : new CPVodVideoView(ctx);

//                    mLePlayer = mHasSkin ? (mPano ? (IMediaDataPlayer) ((UICPPanoVodVideoView) mLeVideoView).getPlayer()
//                            : (IMediaDataPlayer) ((UICPVodVideoView) mLeVideoView).getPlayer())
//                            : (IMediaDataPlayer) ((CPVodVideoView) mLeVideoView).getPlayer();

                    break;

                case PlayerParams.VALUE_PLAYER_ACTION_LIVE:
                    mLeVideoView = mHasSkin ? (mPano ? new UICPPanoActionLiveVideoView(ctx)
                            : new UICPActionLiveVideoView(ctx))
                            : new CPActionLiveVideoView(ctx);

//                    mLePlayer = mHasSkin ? (mPano ? (IMediaDataPlayer) ((UICPPanoActionLiveVideoView) mLeVideoView).getPlayer()
//                            : (IMediaDataPlayer) ((UICPActionLiveVideoView) mLeVideoView).getPlayer())
//                            : (IMediaDataPlayer) ((CPActionLiveVideoView) mLeVideoView).getPlayer();
                    break;

                default:
                    mLeVideoView = new LeBaseMediaDataVideoView(ctx);
                    mLePlayer = (IMediaDataPlayer) ((LeBaseMediaDataVideoView) mLePlayer).getPlayer();
                    break;
            }

            //将播放器放入容器
            View.inflate(mThemedReactContext, R.layout.video_play, this);
            RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            videoContainer.addView((View) mLeVideoView, VideoLayoutParams.computeContainerSize(mThemedReactContext, 16, 9));
//            videoContainer.bringToFront();

            //设置播放器监听器
            mLeVideoView.setVideoViewListener(mVideoViewListener);

            //mLeVideoView.setDataSource("http://cache.utovr.com/201601131107187320.mp4");
        }
    }

    /**
     * 设置数据源
     *
     * @param bundle 数据源包
     * @return
     */
    public void setDataSource(Bundle bundle) {

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 传入数据源 bundle:" + bundle);

        if (bundle == null) return;

        mLePlayerValid = false;
        mVideoDuration = 0;
        mVideoBufferedDuration = 0;

        mPlayMode = (bundle.containsKey(PROP_PLAY_MODE) ? bundle.getInt(PROP_PLAY_MODE) : -1);
        mPano = (bundle.containsKey(PROP_SRC_IS_PANO) && bundle.getBoolean(PROP_SRC_IS_PANO));
        mHasSkin = (bundle.containsKey(PROP_SRC_HAS_SKIN) && bundle.getBoolean(PROP_SRC_HAS_SKIN));

        initLePlayerIfNeeded();

        if (mLeVideoView != null) {
            mLeVideoView.resetPlayer();
//            mLeVideoView.stopAndRelease();

            mLastPosition = 0;
            mRateList = null;
            isCompleted = false;

            if (bundle.containsKey("path"))
                mLeVideoView.setDataSource(bundle.getString("path"));
            else
                mLeVideoView.setDataSource(bundle);

            mLeVideoView.setVideoViewListener(mVideoViewListener);

            WritableMap event = Arguments.createMap();
            event.putString(PROP_SRC, bundle.toString());
            mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD_SOURCE.toString(), event);

        }
    }

    /**
     * 视频Seek到某一位置
     *
     * @param msec the msec
     */
    public void seekTo(float msec) {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— SEEK TO：" + msec);
        if (mLePlayerValid) {
            if (msec <= 0 || msec >= mVideoDuration) {
                return;
            }

            WritableMap event = Arguments.createMap();
            event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
            event.putDouble(EVENT_PROP_SEEK_TIME, msec / 1000.0);
//            mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK.toString(), event);

            mLeVideoView.seekTo(Math.round(msec * 1000.0f));

            mLastPosition = 0; // 上一位置不再可用?

            if (isCompleted && mVideoDuration != 0 && msec < mVideoDuration) {
                isCompleted = false;
                mLeVideoView.retry();
            }
        }
    }


    /**
     * 视频切换码率
     *
     * @param rate 码率值
     */
    public void setRate(String rate) {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 切换码率 current:" + mCurrentRate + " next:" + rate);
        if (TextUtils.isEmpty(rate)) {
            return;
        }

        // 检查码率是否可用
        if (mLePlayerValid && mRateList != null && mRateList.containsKey(rate)) {
            // 保存当前位置
            saveLastPostion();
            //mCurrentRate = mLePlayer.getLastRate();

            //切换码率
//            ((UICPVodVideoView)mLeVideoView).setDefination(rate);
            mLePlayer.setDataSourceByRate(rate);


            WritableMap event = Arguments.createMap();
            event.putString(EVENT_PROP_CURRENT_RATE, mCurrentRate);
            event.putString(EVENT_PROP_NEXT_RATE, rate);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_RATE_CHANG.toString(), event);
        }
    }


    /**
     * 音量控制 0-100
     *
     * @param percentage 音量百分比
     */
    public void setVolume(int percentage) {
        if (null == mAudioManager) {
            return;
        }
        if (percentage < 0 || percentage > 100) {
            return;
        }
        int maxValue = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentage * maxValue / 100, 0);
    }

    /**
     * 设置亮度
     *
     * @param paramInt 取值0-255
     */
    public void setScreenBrightness(Activity activity, int paramInt) {
        ScreenBrightnessManager.setScreenBrightness(activity, paramInt);
    }

    /**
     * 保存上次播放位置
     */
    private void saveLastPostion() {
        if (mLeVideoView == null || mLeVideoView.getCurrentPosition() == 0) {
            return;
        }
        mLastPosition = mLeVideoView.getCurrentPosition();
    }

    /**
     * 设置屏幕方向
     *
     * @param requestedOrientation 码率值
     */
    public void setRequestedOrientation(int requestedOrientation) {
        if (mThemedReactContext.getBaseContext() instanceof Activity) {
            ((Activity) mThemedReactContext.getBaseContext()).setRequestedOrientation(requestedOrientation);
        }
    }


    /**
     * 设置视频暂停和启动
     *
     * @param paused paused
     */
    public void setPausedModifier(final boolean paused) {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 是否暂停 :" + paused);

        mPaused = paused;
        if (!mLePlayerValid) {
            return;
        }

        if (mPaused) {
            if (mLeVideoView.isPlaying()) {//播放中

                mLeVideoView.onPause();

                //暂停更新进度
                mProgressUpdateHandler.removeCallbacks(mProgressUpdateRunnable);

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PAUSE.toString(), event);
                Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 暂停播放 onPause ");

            }
        } else {
            if (!mLeVideoView.isPlaying()) {//播放中

                mLeVideoView.onStart();

                //启动更新进度
                mProgressUpdateHandler.post(mProgressUpdateRunnable);

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_RESUME.toString(), event);
                Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 开始播放 onStart ");

            }
        }
    }


    /**
     * 设置回到上次播放的地址
     *
     * @param lastPosition lastPosition
     */
    public void setLastPosModifier(final long lastPosition) {
        mLastPosition = lastPosition;

        if (!mLePlayerValid) {
            return;
        }
        //回到上次播放位置
        if (mLePlayer != null && mLastPosition != 0) {
            mLePlayer.seekToLastPostion(lastPosition);
        }

    }

    /**
     * 根据当前状态设置播放器
     */
    private void applyModifiers() {
        setPausedModifier(mPaused);
        setLastPosModifier(mLastPosition);
    }


    /**
     * 销毁播放器，释放资源
     */
    public void cleanupMediaPlayerResources() {
        if (mLeVideoView != null) {
            Log.d(TAG, LogUtils.getTraceInfo() + "控件清理 cleanupMediaPlayerResources 调起！");
            mLePlayerValid = false;
            if (mLeVideoView.isPlaying()) mLeVideoView.stopAndRelease();
            mLeVideoView = null;

        }
    }


    /*============================= 事件回调处理 ===================================*/


    /**
     * 处理播放器准备完成事件
     */
    public void processPrepared(int what, Bundle bundle) {
        mLePlayerValid = true;

        //开始封装回调事件参数
        WritableMap event = Arguments.createMap();

        mVideoDuration = mLeVideoView.getDuration();

        // 视频基本信息，长/宽/方向
        WritableMap naturalSize = Arguments.createMap();
        naturalSize.putInt(EVENT_PROP_WIDTH, mVideoWidth);
        naturalSize.putInt(EVENT_PROP_HEIGHT, mVideoHeight);
        if (mVideoWidth > mVideoHeight)
            naturalSize.putString(EVENT_PROP_ORIENTATION, "landscape");
        else
            naturalSize.putString(EVENT_PROP_ORIENTATION, "portrait");

        // 视频基本信息
        event.putString(EVENT_PROP_TITLE, mVideoTitle); //视频标题
        event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);  //视频总长度
        event.putDouble(EVENT_PROP_CURRENT_TIME, mLastPosition);  //当前播放位置
        event.putMap(EVENT_PROP_NATURALSIZE, naturalSize);  //原始尺寸


        // 媒资信息
        boolean hasRateInfo = (mRateList != null && mRateList.size() > 0);
        boolean hasLogo = (mCoverConfig != null && mCoverConfig.getLogoConfig() != null && mCoverConfig.getLogoConfig().getPicUrl() != null);
        boolean hasLoading = (mCoverConfig != null && mCoverConfig.getLoadingConfig() != null && mCoverConfig.getLoadingConfig().getPicUrl() != null);
        boolean hasWater = (mCoverConfig != null && mCoverConfig.getWaterMarks() != null && mCoverConfig.getWaterMarks().size() > 0);

        // 视频码率信息
        if (hasRateInfo) {
            WritableArray ratesList = Arguments.createArray();
            for (Map.Entry<String, String> rate : mRateList.entrySet()) {
                WritableMap map = Arguments.createMap();
                map.putString(EVENT_PROP_RATE_KEY, rate.getKey());
                map.putString(EVENT_PROP_RATE_VALUE, rate.getValue());
                ratesList.pushMap(map);
            }
            event.putArray(EVENT_PROP_RATELIST, ratesList);  //可用码率
            event.putString(EVENT_PROP_DEFAULT_RATE, mDefaultRate);  //默认码率
        }

        // 视频封面信息: LOGO
        if (hasLogo) {
            WritableMap logoConfig = Arguments.createMap();
            logoConfig.putString(EVENT_PROP_PIC, mCoverConfig.getLogoConfig().getPicUrl());
            logoConfig.putString(EVENT_PROP_TARGET, mCoverConfig.getLogoConfig().getTargetUrl());
            logoConfig.putString(EVENT_PROP_POS, mCoverConfig.getLogoConfig().getPos());

            event.putMap(EVENT_PROP_LOGO, logoConfig);  // LOGO信息
        }
        // 视频封面信息: 加载
        if (hasLoading) {
            WritableMap LoadingConfig = Arguments.createMap();
            LoadingConfig.putString(EVENT_PROP_PIC, mCoverConfig.getLoadingConfig().getPicUrl());
            LoadingConfig.putString(EVENT_PROP_TARGET, mCoverConfig.getLoadingConfig().getTargetUrl());
            LoadingConfig.putString(EVENT_PROP_POS, mCoverConfig.getLoadingConfig().getPos());

            event.putMap(EVENT_PROP_LOAD, LoadingConfig);  // LOADING信息
        }
        // 视频封面信息: 水印
        if (hasWater) {
            WritableArray waterMarkList = Arguments.createArray();
            for (int i = 0; i < mCoverConfig.getWaterMarks().size(); i++) {
                WritableMap map = Arguments.createMap();
                WaterConfig waterConfig = mCoverConfig.getWaterMarks().get(i);
                map.putString(EVENT_PROP_PIC, waterConfig.getPicUrl());
                map.putString(EVENT_PROP_TARGET, waterConfig.getTargetUrl());
                map.putString(EVENT_PROP_POS, waterConfig.getPos());
                waterMarkList.pushMap(map);
            }
            event.putArray(EVENT_PROP_WMARKS, waterMarkList);  // 水印信息
        }

        event.putInt(EVENT_PROP_MMS_STATCODE, mMediaStatusCode); //媒资状态码
        event.putInt(EVENT_PROP_MMS_HTTPCODE, mMediaHttpCode); //媒资状态码


        // 设备信息： 声音和亮度
        int volume = 0;
        if (null != mAudioManager) {
            volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxValue = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = volume * 100 / maxValue; //获得声音百分比
        }

        // 设备信息
        event.putInt(EVENT_PROP_VOLUME, volume); //声音百分比
        event.putInt(EVENT_PROP_BRIGHTNESS, mCurrentBrightness); //屏幕亮度

        mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD.toString(), event);

        // 执行播放器控制
        applyModifiers();

    }


    /**
     * 处理视频信息事件
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
        event.putInt(EVENT_PROP_VIDEO_BUFF, (bundle != null && bundle.containsKey(PlayerParams.KEY_VIDEO_BUFFER)) ?
                bundle.getInt(PlayerParams.KEY_VIDEO_BUFFER) : 0);
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
        //视频缓冲转圈加载完成
        saveLastPostion(); //TODO 待驗證
        mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK_COMPLETE.toString(), null);
        return false;
    }

    /**
     * 处理获得视频真实尺寸的回调
     *
     * @param what   PLAY_VIDEOSIZE_CHANGE
     * @param bundle width,height
     * @return boolean
     */
    public boolean processVideoSizeChanged(int what, Bundle bundle) {

        mVideoWidth = (bundle != null && bundle.containsKey(PlayerParams.KEY_WIDTH)) ? bundle.getInt(PlayerParams.KEY_WIDTH) : -1;
        mVideoHeight = (bundle != null && bundle.containsKey(PlayerParams.KEY_HEIGHT)) ? bundle.getInt(PlayerParams.KEY_HEIGHT) : -1;

        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_WIDTH, mVideoWidth);
        event.putInt(EVENT_PROP_HEIGHT, mVideoHeight);
//        mEventEmitter.receiveEvent(getId(), Events.EVENT_CHANGESIZE.toString(), event);
        return true;
    }


    /**
     * 处理播放器缓冲事件
     *
     * @param what   PLAY_BUFFERING
     * @param bundle Bundle[{bufferpercent=xx}]
     * @return boolean
     */
    public boolean processBufferingUpdate(int what, Bundle bundle) {
        // 正常播放状态
        isSeeking = false;

        int percent = (bundle != null && bundle.containsKey(PlayerParams.KEY_PLAY_BUFFERPERCENT)) ? bundle.getInt(PlayerParams.KEY_PLAY_BUFFERPERCENT) : 0;
        mVideoBufferedDuration = (int) Math.round((double) (mVideoDuration * percent) / 100.0);

        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_PLAY_BUFFERPERCENT, percent);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_PLAYABLE_PERCENT.toString(), event);
        return true;
    }


    /**
     * 处理下载完成的事件
     *
     * @param what   PLAY_DOWNLOAD_FINISHED
     * @param bundle null
     * @return boolean
     */
    public boolean processDownloadFinish(int what, Bundle bundle) {
        int percent = 100;
        mVideoBufferedDuration = (int) mVideoDuration;

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_PLAY_BUFFERPERCENT, percent);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_PLAYABLE_PERCENT.toString(), event);
        return true;
    }


    /**
     * 处理播放完成事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return boolean
     */
    public boolean processCompletion(int what, Bundle bundle) {
        isCompleted = true;
        mEventEmitter.receiveEvent(getId(), Events.EVENT_END.toString(), null);
        return true;
    }

    /**
     * 处理播放器出错事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return boolean
     */
    public boolean processPlayerError(int what, Bundle bundle) {
        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_WHAT, what);
        error.putString(EVENT_PROP_EXTRA, (bundle != null) ? bundle.toString() : "");
        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR, error);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), event);
        return true;
    }


    /**
     * 处理媒资点播数据获取的的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    public boolean processMediaVodLoad(int what, Bundle bundle) {

        VideoHolder videoHolder = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
        if (videoHolder == null) return false;

        mMediaStatusCode = bundle.getInt(EVENT_PROP_STAT_CODE);
        mMediaHttpCode = bundle.getInt(EVENT_PROP_HTTP_CODE);

        //获得视频标题
        String title = videoHolder.getTitle();
        if (!TextUtils.isEmpty(title)) {
            mVideoTitle = title;
        }
        //获得视频长度
        mVideoDuration = Long.parseLong(videoHolder.getVideoDuration());

        //获得默认码率和码率列表
        mRateList = videoHolder.getVtypes();
        mCurrentRate = mDefaultRate = videoHolder.getDefaultVtype();

        //获得加载和水印图
        mCoverConfig = videoHolder.getCoverConfig();

        Log.d(TAG, LogUtils.getTraceInfo() + "媒资数据事件——— event:" + what + " bundle:" + bundle.toString());
        return true;
    }

    /**
     * 处理媒资直播数据获取的的事件
     *
     * @param what
     * @param bundle null
     * @return boolean
     */
    public boolean processMediaLiveLoad(int what, Bundle bundle) {
        //todo 直播信息获取
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_STAT_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_STAT_CODE)) ? bundle.getInt(EVENT_PROP_STAT_CODE) : -1);
//        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_HTTP_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_HTTP_CODE)) ? bundle.getInt(EVENT_PROP_HTTP_CODE) : -1);
//        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_LIVE.toString(), event);
        return true;
    }

    /**
     * 处理媒资活动直播数据获取的的事件
     *
     * @param what   MEDIADATA_ACTION
     * @param bundle null
     * @return boolean
     */
    public boolean processMediaActionLoad(int what, Bundle bundle) {
        // TODO 活动直播信息获取
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_STAT_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_STAT_CODE)) ? bundle.getInt(EVENT_PROP_STAT_CODE) : -1);
//        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_HTTP_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_HTTP_CODE)) ? bundle.getInt(EVENT_PROP_HTTP_CODE) : -1);
//        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_ACTION.toString(), event);
        return true;
    }

    /**
     * 处理媒资调度数据获取的的事件
     *
     * @param what   MEDIADATA_GET_PLAYURL
     * @param bundle null
     * @return boolean
     */
    public boolean processMediaPlayURLLoad(int what, Bundle bundle) {
        // todo 调度信息获取
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_STAT_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_STAT_CODE)) ? bundle.getInt(EVENT_PROP_STAT_CODE) : -1);
//        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_HTTP_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_HTTP_CODE)) ? bundle.getInt(EVENT_PROP_HTTP_CODE) : -1);
//        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_PLAYURL.toString(), event);
        return true;
    }

    /**
     * 处理广告开始的事件
     *
     * @param what   AD_START
     * @param bundle null
     * @return boolean
     */
    public boolean processAdvertStart(int what, Bundle bundle) {
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_START.toString(), null);
        return true;
    }

    /**
     * 处理广告结束的事件
     *
     * @param what   AD_COMPLETE
     * @param bundle null
     * @return boolean
     */
    public boolean processAdvertComplete(int what, Bundle bundle) {
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_COMPLETE.toString(), null);
        return true;
    }

    /**
     * 处理广告结束的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    public boolean processAdvertProgress(int what, Bundle bundle) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_AD_TIME, (bundle != null && bundle.containsKey(EVENT_PROP_AD_TIME)) ? bundle.getInt(EVENT_PROP_AD_TIME) : 0);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_PROGRESS.toString(), event);
        return true;
    }

    /**
     * 处理广告出错的事件
     *
     * @param what   AD_ERROR
     * @param bundle null
     * @return boolean
     */
    public boolean processAdvertError(int what, Bundle bundle) {
//        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_ERROR.toString(), null);
        return true;
    }

    /**
     * 处理其他事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return boolean
     */
    public boolean processOtherEvent(int what, Bundle bundle) {
        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_WHAT, what);
        error.putString(EVENT_PROP_EXTRA, (bundle != null) ? bundle.toString() : "");
        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_EVENT, error);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_OTHER_EVENT.toString(), event);
        return true;
    }

    /*============================= 各类事件回调 ===================================*/

    /**
     * 处理播放器事件，具体事件参见IPlayer类
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.PLAY_INIT: //200
                // 播放器初始化
                handled = true;
                event = "PLAY_INIT";
                processOtherEvent(state, bundle);
                break;

            case PlayerEvent.PLAY_BUFFERING: // 获取视频缓冲加载状态 201
                handled = true;
                event = "PLAY_BUFFERING";
                processBufferingUpdate(state, bundle);
                break;

            case PlayerEvent.PLAY_COMPLETION:  // 播放完毕  202
                handled = true;
                event = "PLAY_COMPLETION";
                processCompletion(state, bundle);
                break;


            case PlayerEvent.PLAY_DECODER_CHANGED: // 解码方式切换 203
                handled = true;
                event = "PLAY_DECODER_CHANGED";

                break;

            case PlayerEvent.PLAY_DOWNLOAD_FINISHED: // 视频下载完成 204
                handled = true;
                event = "PLAY_DOWNLOAD_FINISHED";
                processDownloadFinish(state, bundle);
                break;

            case PlayerEvent.PLAY_ERROR: //播放出错 205
                handled = true;
                event = "PLAY_ERROR";
                processPlayerError(state, bundle);
                break;

            case PlayerEvent.PLAY_INFO: //206
                // 获取播放器状态
                handled = true;
                event = "PLAY_INFO";
                processPlayerInfo(state, bundle);
                break;

            case PlayerEvent.PLAY_LOADINGSTART: // 207
                // 开始缓冲视频
                handled = true;
                event = "PLAY_LOADINGSTART";
                processPlayerLoading(state, bundle);
                break;

            case PlayerEvent.PLAY_PREPARED: //播放器准备完毕 208
                handled = true;
                event = "PLAY_PREPARED";
                processPrepared(state, bundle);
                break;

            case PlayerEvent.PLAY_SEEK_COMPLETE: // 用户跳转完毕 209
                handled = true;
                event = "PLAY_SEEK_COMPLETE";
                processSeekComplete(state, bundle);
                break;

            /**
             * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
             */
            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED: // 视频宽高发生变化时 210
                handled = true;
                event = "PLAY_VIDEOSIZE_CHANGED";
                processVideoSizeChanged(PlayerEvent.PLAY_VIDEOSIZE_CHANGED, bundle);
                break;


            case PlayerEvent.PLAY_OVERLOAD_PROTECTED: // 视频过载保护 211
                handled = true;
                event = "PLAY_OVERLOAD_PROTECTED";
                break;


            case PlayerEvent.VIEW_PREPARE_VIDEO_SURFACE: // 添加了视频播放器SurfaceView 8001
                handled = true;
                event = "VIEW_PREPARE_VIDEO_SURFACE";
                break;


            case PlayerEvent.VIEW_PREPARE_AD_SURFACE:  // 添加了广告播放器SurfaceView 8002
                handled = true;
                event = "VIEW_PREPARE_AD_SURFACE";
                break;


        }
        if (handled)
            Log.d(TAG, LogUtils.getTraceInfo() + "播放器事件——— event " + event + " state " + state + " bundle " + bundle);
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.MEDIADATA_VOD:  // 处理媒资返回点播对应数据  6000
                handled = true;
                event = "MEDIADATA_VOD";
                processMediaVodLoad(state, bundle);
                break;

            case PlayerEvent.MEDIADATA_LIVE:  // 处理媒资返回直播对应数据  6001
                handled = true;
                event = "MEDIADATA_LIVE";
                processMediaLiveLoad(state, bundle);
                break;

            case PlayerEvent.MEDIADATA_GET_PLAYURL:  // 处理调度返回（直播、活动直播）对应数据  6002
                handled = true;
                event = "MEDIADATA_GET_PLAYURL";
                processMediaPlayURLLoad(state, bundle);
                break;

            case PlayerEvent.MEDIADATA_ACTION:  // 处理媒资返回活动直播对应数据  6003
                handled = true;
                event = "MEDIADATA_ACTION";
                processMediaActionLoad(state, bundle);
                break;

        }
        if (handled)
            Log.d(TAG, LogUtils.getTraceInfo() + "视频信息事件——— event " + event + " state " + state + " bundle " + bundle);
    }

    /**
     * 处理广告类事件
     */
    private void handleAdEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.AD_TIME:  // 广告？？？？  7004
                handled = true;
                event = "AD_TIME";
                break;

            case PlayerEvent.AD_START:  // 广告开始播放 7005
                handled = true;
                event = "AD_START";
                processAdvertStart(state, bundle);
                break;

            case PlayerEvent.AD_COMPLETE:  // 广告结束播放  7006
                handled = true;
                event = "AD_COMPLETE";
                processAdvertComplete(state, bundle);
                break;

            case PlayerEvent.AD_PROGRESS: // 广告播放进度  7007
                handled = true;
                event = "AD_PROGRESS";
                processAdvertProgress(state, bundle);
                break;

            case PlayerEvent.AD_ERROR: // 广告播放错误  7008
                handled = true;
                event = "AD_ERROR";
                processAdvertError(state, bundle);
                break;

        }
        if (handled)
            Log.d(TAG, LogUtils.getTraceInfo() + "广告播放事件——— event " + event + " state " + state + " bundle " + bundle);
    }

    /**
     * 处理其他类事件
     */
    private void handleOtherEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.CDEEVENT_EVENT_CDE_LINK_SHELL_ERROR: // CDE连接命令出错 1
                handled = true;
                event = "CDEEVENT_EVENT_CDE_LINK_SHELL_ERROR";
                processOtherEvent(state, bundle);
                break;

            case PlayerEvent.CDEEVENT_EVENT_CDE_READY: // CDE 准备完毕 2
                handled = true;
                event = "CDEEVENT_EVENT_CDE_READY";
                processOtherEvent(state, bundle);
                break;

        }

        if (handled)
            Log.d(TAG, LogUtils.getTraceInfo() + "其他类事件——— event " + event + " state " + state + " bundle " + bundle);
    }

    /*============================= 容器生命周期方法 ===================================*/

    @Override
    public void onHostResume() {
        if (mLeVideoView != null) {
            Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostResume 调起！");
            mLeVideoView.onResume();
        }
    }

    @Override
    public void onHostPause() {
        if (mLeVideoView != null) {
            Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostPause 调起！");
            saveLastPostion();
//            mLeVideoView.stopAndRelease();
            mLeVideoView.onPause();
        }
    }


    @Override
    public void onHostDestroy() {
        if (mLeVideoView != null) {
            Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostDestroy 调起！");
            mLeVideoView.onDestroy();
            cleanupMediaPlayerResources();
            //mLeVideoView.onDestroy();
        }
    }
}
