package com.demoproject.leecoSdk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;


import com.demoproject.utils.LogUtils;
import com.demoproject.utils.OrientationSensorUtils;
import com.demoproject.utils.ScreenBrightnessManager;
import com.demoproject.utils.ScreenUtils;
import com.demoproject.utils.TimeUtils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.lecloud.sdk.api.linepeople.OnlinePeopleChangeListener;
import com.lecloud.sdk.api.md.entity.action.ActionInfo;
import com.lecloud.sdk.api.md.entity.action.CoverConfig;
import com.lecloud.sdk.api.md.entity.action.LiveInfo;
import com.lecloud.sdk.api.md.entity.action.WaterConfig;
import com.lecloud.sdk.api.md.entity.vod.VideoHolder;
import com.lecloud.sdk.api.status.ActionStatus;
import com.lecloud.sdk.api.status.ActionStatusListener;
import com.lecloud.sdk.api.timeshift.ItimeShiftListener;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.listener.AdPlayerListener;
import com.lecloud.sdk.listener.MediaDataPlayerListener;
import com.lecloud.sdk.listener.OnPlayStateListener;
import com.lecloud.sdk.player.IMediaDataActionPlayer;
import com.lecloud.sdk.player.IMediaDataLivePlayer;
import com.letv.android.client.cp.sdk.player.live.CPActionLivePlayer;
import com.letv.android.client.cp.sdk.player.live.CPLivePlayer;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;
import com.letvcloud.cmf.MediaPlayer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.demoproject.leecoSdk.Constants.*;

/**
 * Created by raojia on 2016/11/10.
 */

public class LeReactPlayer extends LeTextureView implements LifecycleEventListener, MediaDataPlayerListener,
        OnPlayStateListener, AdPlayerListener, MediaPlayer.OnVideoRotateListener {

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    /*
    * 设备控制
    */
    // 设备信息
    private final AudioManager mAudioManager;
    private final int mCurrentBrightness;
    private OrientationSensorUtils mOrientationSensorUtils; //方向控制

    private int mCurrentOritentation; //当前屏幕方向
    private boolean isLockFlag = false;
    private Handler mOrientationChangeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int orient = -1;
            switch (msg.what) {
                case OrientationSensorUtils.ORIENTATION_8:// 反横屏
                    orient = 8;
                    break;
                case OrientationSensorUtils.ORIENTATION_9:// 反竖屏
                    orient = 9;
                    break;
                case OrientationSensorUtils.ORIENTATION_0:// 正横屏
                    orient = 0;
                    break;
                case OrientationSensorUtils.ORIENTATION_1:// 正竖屏
                    orient = 1;
                    break;
            }
            WritableMap event = Arguments.createMap();
            event.putInt(EVENT_PROP_ORIENTATION, orient);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_ORIENTATION_CHANG.toString(), event);

            Log.d(TAG, LogUtils.getTraceInfo() + "设备转屏事件——— orientation："+ orient);

            super.handleMessage(msg);
        }

    };

    /// 播放器设置
    private int mPlayMode = -1;

    private boolean mHasSkin = false; //是否有皮肤
    private boolean mPano = false;  //是否全景
    // 播放器可用状态，prepared, started, paused，completed 时为true
    private boolean mLePlayerValid = false;


    /*
    * 视频媒资信息
    */
    private String mVideoTitle; // 点播视频标题
    private LinkedHashMap<String, String> mRateList;  // 当前支持的码率
    private String mDefaultRate;  // 当前视频默认码率
    private int mVideoWidth;  //视频实际宽度
    private int mVideoHeight;  //视频实际高度
    private CoverConfig mCoverConfig;  //LOGO，加载图，水印等信息
    /*
    * 视频状态信息
    */
    private String mCurrentRate;  // 当前视频码率

    private boolean mPaused = false;  // 暂停状态
    private boolean isCompleted = false;   // 是否播放完毕
    private boolean isSeeking = false;  // 是否在缓冲加载状态
    /*
     * == 云点播状态 ==============
    */
    private long mVideoDuration = 0;  //当前视频总长


    private long mLastPosition;  //上次播放位置
    private int mVideoBufferedDuration = 0; //当前已缓冲长度
    // VOD进度更新线程
    private Handler mProgressUpdateHandler = new Handler();
    private Runnable mProgressUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            if (mLePlayerValid && !mPaused && !isSeeking && !isCompleted) {
                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_CURRENT_TIME, getCurrentPosition() / 1000.0);
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);
            }
            mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, 250);
        }
    };
    /*
     * == 云直播状态变量 =====================
    */
    private ActionInfo mActionInfo;

    private com.lecloud.sdk.api.md.entity.live.LiveInfo mCurrentLiveInfo;
    private String mCurrentLiveId; //当前机位
    private int mWaterMarkHight = 800;  //高位
    private int mWaterMarkLow = 200; //低位
    private int mMaxDelayTime = 1000; // 最大延时
    private int mCachePreSize = 500; // 起播缓冲值
    private int mCacheMaxSize = 10000; //最大缓冲值
    private ItimeShiftListener mTimeShiftListener;
    private OnlinePeopleChangeListener mOnlinePeopleChangeListener;
    private ActionStatusListener mActionStatusListener;


    /*============================= 播放器构造 ===================================*/

    public LeReactPlayer(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;

        //创建与RN之间的回调
        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
        mThemedReactContext.addLifecycleEventListener(this);

        setSurfaceTextureListener(this);

        //设置声音管理器
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);

        // 获得当前屏幕亮度 取值0-255
        mCurrentBrightness = ScreenBrightnessManager.getScreenBrightness(context.getBaseContext());

        // 屏幕方向
        mCurrentOritentation = ScreenUtils.getOrientation(context.getBaseContext());
    }

    private void initActionLiveListener() {

        if (mTimeShiftListener == null) {
            mTimeShiftListener = new ItimeShiftListener() {
                /**
                 * 直播时移监听 (Live)
                 * 用于更新直播时间和进度条显示
                 *
                 */
                @Override
                public void onChange(long serverTime, long currentTime, long begin) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "直播时移事件——— serverTime：" + TimeUtils.timet(serverTime)
                            + "，currentTime：" + TimeUtils.timet(currentTime) + "，beginTime：" + TimeUtils.timet(begin));

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_SERVER_TIME, (int) serverTime / 1000);
                    event.putInt(EVENT_PROP_CURRENT_TIME, (int) currentTime / 1000);
                    event.putInt(EVENT_PROP_LIVE_BEGIN, (int) begin / 1000);
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_ACTION_TIME_SHIFT.toString(), event);
                }
            };
            ((IMediaDataLivePlayer) mMediaPlayer).setTimeShiftListener(mTimeShiftListener);
        }

        if (mActionStatusListener == null) {
            mActionStatusListener = new ActionStatusListener() {

                @Override
                public void onChange(ActionStatus actionStatus) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "直播状态变化事件——— actionStatus：" + actionStatus.toString());

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_LIVE_ACTION_STATE, actionStatus.getStatus());
                    event.putString(EVENT_PROP_LIVE_ACTION_ID, actionStatus.getActivityId());
                    event.putInt(EVENT_PROP_LIVE_BEGIN, (int) actionStatus.getBeginTime() / 1000);
                    event.putInt(EVENT_PROP_LIVE_END, (int) actionStatus.getEndTime() / 1000);
                    event.putString(EVENT_PROP_LIVE_ID, actionStatus.getLiveId());
                    event.putString(EVENT_PROP_STREAM_ID, actionStatus.getStreamId());

                    event.putString(EVENT_PROP_ERROR_CODE, actionStatus.getErrCode());
                    event.putString(EVENT_PROP_ERROR_MSG, actionStatus.getErrMsg());

                    mEventEmitter.receiveEvent(getId(), Events.EVENT_ACTION_STATUS_CHANGE.toString(), event);
                }
            };

            ((IMediaDataActionPlayer) mMediaPlayer).setActionStatusListener(mActionStatusListener);
        }

        if (mOnlinePeopleChangeListener == null) {
            mOnlinePeopleChangeListener = new OnlinePeopleChangeListener() {

                @Override
                public void onChange(String s) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "在线人数变化事件——— onlineNum：" + s);

                    WritableMap event = Arguments.createMap();
                    event.putString(EVENT_PROP_ONLINE_NUM, s);
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_ONLINE_NUM_CHANGE.toString(), event);
                }
            };

            ((IMediaDataActionPlayer) mMediaPlayer).setOnlinePeopleListener(mOnlinePeopleChangeListener);
        }

    }

    // 创建播放器及监听
    private void initLePlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mLePlayerValid = false;

            Context ctx = mThemedReactContext.getBaseContext();

            ((Activity) ctx).getWindow().setFormat(PixelFormat.TRANSLUCENT);
            ((Activity) ctx).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            switch (mPlayMode) {
                case PlayerParams.VALUE_PLAYER_LIVE: //直播机位
                    mMediaPlayer = new CPLivePlayer(ctx);
                    break;

                case PlayerParams.VALUE_PLAYER_VOD: //云点播
                    mMediaPlayer = new CPVodPlayer(ctx);
                    break;

                case PlayerParams.VALUE_PLAYER_ACTION_LIVE: //云直播
                    mMediaPlayer = new CPActionLivePlayer(ctx);
                    break;

                case PlayerParams.VALUE_PLAYER_MOBILE_LIVE: //移动直播
                    /* 暂不支持 */
                    break;

            }

            //设置播放器监听器
            mMediaPlayer.setOnMediaDataPlayerListener(this);

            mMediaPlayer.setOnAdPlayerListener(this);

            mMediaPlayer.setOnPlayStateListener(this);

            mMediaPlayer.setOnVideoRotateListener(this);

            if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE)
                initActionLiveListener();
        }
    }


    /*============================= 播放器外部接口 ===================================*/

    /**
     * 设置数据源，必填（VOD、LIVE）
     *
     * @param bundle 数据源包
     */
    public void setSrc(Bundle bundle) {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 传入数据源 bundle:" + bundle);
        if (bundle == null) return;

        // 播放模式切换，重新创建Player
        int newPlayMode = (bundle.containsKey(PROP_PLAY_MODE) ? bundle.getInt(PROP_PLAY_MODE) : -1);
        if (mPlayMode != -1 && newPlayMode != mPlayMode) {
            cleanupMediaPlayerResources();
        }

        mPlayMode = newPlayMode;
        mPano = (bundle.containsKey(PROP_SRC_IS_PANO) && bundle.getBoolean(PROP_SRC_IS_PANO));
        mHasSkin = (bundle.containsKey(PROP_SRC_HAS_SKIN) && bundle.getBoolean(PROP_SRC_HAS_SKIN));

        //创建播放器
        initLePlayerIfNeeded();

        if (mMediaPlayer != null) {
            mMediaPlayer.clearDataSource();

            //初始化状态变量
            initFieldParaStates();

            if (bundle.containsKey("path"))
                setDataSource(bundle.getString("path"));
            else
                setDataSource(bundle);


            WritableMap event = Arguments.createMap();
            event.putString(PROP_SRC, bundle.toString());
            mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD_SOURCE.toString(), event);
        }
    }

    private void initFieldParaStates() {
        mLePlayerValid = false;
        isCompleted = false;
        isSeeking = false;
        mVideoDuration = 0;
        mVideoBufferedDuration = 0;
        mLastPosition = 0;
        mCurrentRate = "";

        mRateList = null;
        mCoverConfig = null;
        mActionInfo = null;
    }

    /**
     * 视频Seek到某一位置（VOD）
     * 直播Seek到某一时间（LIVE）
     *
     * @param msec the msec
     */
    public void setSeekTo(float msec) {
        if (msec < 0 || !mLePlayerValid) {
            return;
        }

        if (mPlayMode == PlayerParams.VALUE_PLAYER_VOD && msec <= mVideoDuration) { //点播
            WritableMap event = Arguments.createMap();
            event.putDouble(EVENT_PROP_CURRENT_TIME, getCurrentPosition() / 1000.0);
            event.putDouble(EVENT_PROP_SEEK_TIME, msec / 1000.0);
//            mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK.toString(), event);

            seekTo(Math.round(msec * 1000.0f));

            mLastPosition = 0; // 上一位置不再可用?

            if (isCompleted && mVideoDuration != 0 && msec < mVideoDuration) {
                isCompleted = false;
                mMediaPlayer.retry();
            }
            Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— SEEK TO：" + msec);

        } else if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE) { //直播

            mMediaPlayer.setCacheWatermark(mWaterMarkHight, mWaterMarkLow);
            mMediaPlayer.setMaxDelayTime(mMaxDelayTime);
            mMediaPlayer.setCachePreSize(mCachePreSize);
            mMediaPlayer.setCacheMaxSize(mCacheMaxSize);

            ((IMediaDataLivePlayer) mMediaPlayer).seekTimeShift(Math.round(msec * 1000.0f));

            Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— SEEK TIMESHIFT：" + msec);
        }


    }

    /**
     * 视频切换码率（VOD、LIVE）
     *
     * @param rate 码率值
     */
    public void setRate(String rate) {
        if (TextUtils.isEmpty(rate)) {
            return;
        }

        // 检查码率是否可用
        if (mLePlayerValid && mRateList != null && mRateList.containsKey(rate)) {
            // 保存当前位置
            saveLastPostion();

            //切换码率
            setDataSourceByRate(rate);
            mCurrentRate = rate;

            WritableMap event = Arguments.createMap();
            event.putString(EVENT_PROP_CURRENT_RATE, mCurrentRate);
            event.putString(EVENT_PROP_NEXT_RATE, rate);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_RATE_CHANG.toString(), event);
        }
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 切换码率 current:" + mCurrentRate + " next:" + rate);
    }

    /**
     * 云直播切换机位（LIVE）
     *
     * @param liveId 机位ID
     */
    public void setLive(String liveId) {
        if (TextUtils.isEmpty(liveId)) {
            return;
        }

        //检查是否是云直播播放器，相关参数是否正确
        if (!mLePlayerValid || mPlayMode != PlayerParams.VALUE_PLAYER_ACTION_LIVE || mActionInfo == null)
            return;

        // 检查码率是否可用
        if (mActionInfo.getLiveInfos() != null && mActionInfo.getLiveInfos().contains(liveId)) {
            //切换机位
            setDataSourceByLiveId(liveId);
            mCurrentLiveId = liveId;

            WritableMap event = Arguments.createMap();
            event.putString(EVENT_PROP_CURRENT_LIVE, mCurrentLiveId);
            event.putString(EVENT_PROP_NEXT_LIVE, liveId);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_ACTION_LIVE_CHANGE.toString(), event);
        }
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 切换机位 current:" + mCurrentLiveId + " next:" + liveId);
    }


    /**
     * 设置左右声道（VOD、LIVE）
     *
     * @param leftVolume  左声道
     * @param rightVolume 右声道
     */
    public void setLeftAndRightTrack(float leftVolume, float rightVolume) {
        if (null == mAudioManager) {
            return;
        }
        if (leftVolume < 0 || rightVolume < 0) {
            return;
        }
        setVolume(leftVolume, rightVolume);
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 设置左右声道:" + leftVolume + "," + rightVolume);
    }

    /**
     * 音量控制 0-100（VOD、LIVE）
     *
     * @param percentage 音量百分比
     */
    public void setVolumePercent(int percentage) {
        if (null == mAudioManager) {
            return;
        }
        if (percentage < 0 || percentage > 100) {
            return;
        }
        int maxValue = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentage * maxValue / 100, 0);
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 调节音量:" + percentage);
    }


    /**
     * 设置亮度（VOD、LIVE）
     *
     * @param paramInt 取值0-255
     */
    public void setScreenBrightness(int paramInt) {
        if (paramInt < 0 || paramInt > 255) {
            return;
        }
        ScreenBrightnessManager.setScreenBrightness((Activity) (mThemedReactContext.getBaseContext()), paramInt);
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 调节亮度:" + paramInt);
    }

    /**
     * 保存上次播放位置
     */
    private void saveLastPostion() {
        if (mMediaPlayer == null || getCurrentPosition() == 0) {
            return;
        }
        mLastPosition = getCurrentPosition();
    }

    /**
     * 设置屏幕方向（VOD、LIVE）
     *
     * @param requestedOrientation 设置屏幕方向
     */
    public void setOrientation(int requestedOrientation) {
        if (requestedOrientation < 0 || mCurrentOritentation == requestedOrientation) return;

        if (mThemedReactContext.getBaseContext() instanceof Activity) {
            Activity activity = (Activity) mThemedReactContext.getBaseContext();

            switch (requestedOrientation) {
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE: //正横屏 0
                    ScreenUtils.showFullScreen(activity, true);
                    activity.setRequestedOrientation(requestedOrientation);
                    mCurrentOritentation = requestedOrientation;
                    break;

                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT: //正竖屏 1
                    ScreenUtils.showFullScreen(activity, false);
                    activity.setRequestedOrientation(requestedOrientation);
                    mCurrentOritentation = requestedOrientation;
                    break;

                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE: //反横屏 8
                    ScreenUtils.showFullScreen(activity, true);
                    activity.setRequestedOrientation(requestedOrientation);
                    mCurrentOritentation = requestedOrientation;
                    break;

                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT: //反竖屏 9
                    ScreenUtils.showFullScreen(activity, false);
                    activity.setRequestedOrientation(requestedOrientation);
                    mCurrentOritentation = requestedOrientation;
                    break;
            }
            //ScreenUtils.getOrientation(activity);
        }

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_ORIENTATION, mCurrentOritentation);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ORIENTATION_CHANG.toString(), event);

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 设置方向 orientation:" + requestedOrientation);

    }

    /**
     * 设置视频暂停和启动（VOD、LIVE）
     *
     * @param paused paused
     */
    public void setPausedModifier(final boolean paused) {
        mPaused = paused;

        if (!mLePlayerValid) {
            return;
        }

        if (mPaused) {
            if (isPlaying()) {
                pause();

                //暂停更新进度
                if (mPlayMode == PlayerParams.VALUE_PLAYER_VOD)
                    mProgressUpdateHandler.removeCallbacks(mProgressUpdateRunnable);

//                if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE)
//                    stopTimeShiftListener();

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PAUSE.toString(), event);
                Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 暂停播放 pause ");
            }
        } else {
            if (!isPlaying()) {
                start();

                //启动更新进度
                if (mPlayMode == PlayerParams.VALUE_PLAYER_VOD)
                    mProgressUpdateHandler.post(mProgressUpdateRunnable);

                if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE)
                    if (mTimeShiftListener != null) {
                        ((IMediaDataLivePlayer) mMediaPlayer).startTimeShift();
                    }

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_RESUME.toString(), event);
                Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 开始播放 start ");
            }
        }
    }

    /**
     * 设置回到上次播放的地址（VOD）
     *
     * @param lastPosition lastPosition
     */
    private void setLastPosModifier(final long lastPosition) {
        mLastPosition = lastPosition;

        if (!mLePlayerValid) {
            return;
        }
        //回到上次播放位置
        if (mMediaPlayer != null && mLastPosition != 0) {
            seekToLastPostion(lastPosition);
            Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 恢复位置 seekToLastPostion ");
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
     * 销毁播放器，释放资源（VOD、LIVE）
     */
    public void cleanupMediaPlayerResources() {
        Log.d(TAG, LogUtils.getTraceInfo() + "控件清理 cleanupMediaPlayerResources 调起！");

        if (mCurrentOritentation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (mMediaPlayer != null) {
            mLePlayerValid = false;

            if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE && mTimeShiftListener != null)
                ((IMediaDataLivePlayer) mMediaPlayer).stopTimeShift();

            if (isPlaying()) stop();
            release();

            mTimeShiftListener = null;
            mActionStatusListener = null;
            mOnlinePeopleChangeListener = null;

            mMediaPlayer = null;
        }
    }


/*============================= 事件回调处理 ===================================*/

    /**
     * 处理播放器准备完成事件
     */
    private void processPrepared(int what, Bundle bundle) {
        mLePlayerValid = true;

        //开始封装回调事件参数
        WritableMap event = Arguments.createMap();

        mVideoDuration = getDuration();

        // 当前播放模式
        event.putInt(EVENT_PROP_PLAY_MODE, mPlayMode);
        // 当前屏幕方向
        event.putInt(EVENT_PROP_ORIENTATION, mCurrentOritentation);


        // 视频基本信息，长/宽/方向
        WritableMap naturalSize = Arguments.createMap();
        naturalSize.putInt(EVENT_PROP_WIDTH, mVideoWidth);
        naturalSize.putInt(EVENT_PROP_HEIGHT, mVideoHeight);
        if (mVideoWidth > mVideoHeight)
            naturalSize.putString(EVENT_PROP_VIDEO_ORIENTATION, "landscape");
        else
            naturalSize.putString(EVENT_PROP_VIDEO_ORIENTATION, "portrait");

        // 视频基本信息
        event.putString(EVENT_PROP_TITLE, mVideoTitle); //视频标题
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
            event.putString(EVENT_PROP_CURRENT_RATE, mCurrentRate);  //当前码率
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

        if (mPlayMode == PlayerParams.VALUE_PLAYER_VOD) { //VOD模式下参数
            event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);  //视频总长度（VOD）
            event.putDouble(EVENT_PROP_CURRENT_TIME, mLastPosition);  //当前播放位置（VOD）
        }

        if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE) { //LIVE模式参数
            WritableMap actionLive = Arguments.createMap();
            if (mActionInfo != null) {
                actionLive.putString(EVENT_PROP_LIVE_COVER_IMG, mActionInfo.getCoverImgUrl()); //直播封面图
                actionLive.putString(EVENT_PROP_LIVE_PLAYER_URL, mActionInfo.getPlayerPageUrl()); //直播页面URL
                actionLive.putInt(EVENT_PROP_LIVE_ACTION_STATE, mActionInfo.getActivityState()); //直播页面URL
                actionLive.putString(EVENT_PROP_CURRENT_LIVE, mCurrentLiveId); //当前机位

                if (mCurrentLiveInfo != null) {
                    actionLive.putString(EVENT_PROP_LIVE_BEGIN_TIME, mCurrentLiveInfo.getLiveBeginTime()); //当前机位开始时间URL
                    actionLive.putString(EVENT_PROP_LIVE_START_TIME, mCurrentLiveInfo.getLiveStartTime()); //当前机位开始时间URL
                }

                final List<LiveInfo> liveInfos = mActionInfo.getLiveInfos();
                if (liveInfos != null && liveInfos.size() > 0) {
                    WritableArray liveList = Arguments.createArray();
                    for (int i = 0; i < liveInfos.size(); i++) {
                        WritableMap map = Arguments.createMap();
                        LiveInfo liveInfo = liveInfos.get(i);
                        map.putString(EVENT_PROP_LIVE_ID, liveInfo.getLiveId());
                        map.putInt(EVENT_PROP_LIVE_MACHINE, liveInfo.getMachine());
                        map.putString(EVENT_PROP_LIVE_PRV_STEAMID, liveInfo.getPreviewStreamId());
                        map.putString(EVENT_PROP_LIVE_PRV_STEAMURL, liveInfo.getPreviewStreamPlayUrl());
                        map.putInt(EVENT_PROP_LIVE_STATUS, liveInfo.getStatus());
                        liveList.pushMap(map);
                    }
                    actionLive.putArray(EVENT_PROP_LIVES, liveList);  // 机位信息
                }
            }
            event.putMap(EVENT_PROP_ACTIONLIVE, actionLive); //云直播数据
        }

//        event.putInt(EVENT_PROP_MMS_STATCODE, mMediaStatusCode); //媒资状态码
//        event.putInt(EVENT_PROP_MMS_HTTPCODE, mMediaHttpCode); //媒资状态码mMediaStatusCode

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
    private boolean processPlayerInfo(int what, Bundle bundle) {
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
            default:
                if (mPlayMode != PlayerParams.VALUE_PLAYER_VOD)
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_START.toString(), null);

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
    private boolean processPlayerLoading(int what, Bundle bundle) {
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
    private boolean processSeekComplete(int what, Bundle bundle) {
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
    private boolean processVideoSizeChanged(int what, Bundle bundle) {

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
    private boolean processBufferingUpdate(int what, Bundle bundle) {
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
    private boolean processDownloadFinish(int what, Bundle bundle) {
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
    private boolean processCompletion(int what, Bundle bundle) {
        isCompleted = true;
        mEventEmitter.receiveEvent(getId(), Events.EVENT_END.toString(), null);
        return true;
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
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), error);
        return true;
    }


    /**
     * 处理媒资点播数据获取的的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    private boolean processMediaVodLoad(int what, Bundle bundle) {
        int mediaStatusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);

        switch (mediaStatusCode) {

            case StatusCode.MEDIADATA_SUCCESS: //OK
                VideoHolder videoHolder = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
                if (videoHolder == null) return false;

                //获得视频标题
                String title = videoHolder.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    mVideoTitle = title;
                }
                //获得视频长度
                mVideoDuration = Long.parseLong(videoHolder.getVideoDuration());

                //获得默认码率和码率列表
                if (mRateList != null) mRateList.clear();
                mRateList = videoHolder.getVtypes();
                mCurrentRate = mDefaultRate = videoHolder.getDefaultVtype();

                //获得加载和水印图
                mCoverConfig = videoHolder.getCoverConfig();

                //设置当前码率为默认
                mMediaPlayer.setDataSourceByRate(mCurrentRate);

                break;

            default: //处理错误
                processError(what, bundle);
                break;
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "媒资数据事件——— 点播事件 event:" + what + " bundle:" + bundle.toString());
        return true;
    }


    /**
     * 处理云直播返回的活动信息（1个活动最多包含4个机位，在后台配置）
     *
     * @param what   MEDIADATA_ACTION
     * @param bundle null
     * @return boolean
     */
    private boolean processMediaActionLoad(int what, Bundle bundle) {
        int mediaStatusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);

        switch (mediaStatusCode) {

            case StatusCode.MEDIADATA_SUCCESS: //OK
                ActionInfo actionInfo = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
                if (actionInfo == null) return false;

                //获得视频标题
                String title = actionInfo.getActivityName();
                if (!TextUtils.isEmpty(title)) {
                    mVideoTitle = title;
                }

                // 获得封面图和网页播放地址
                mActionInfo = actionInfo;

                //获得加载和水印图
                mCoverConfig = actionInfo.getCoverConfig();

                // 获得活动状态
                int actionStatus = actionInfo.getActivityState();
                if (actionStatus == ActionStatus.STATUS_LIVE_ING) {
                    LiveInfo liveInfo = null;
                    //获取当前第一个直播节目
                    List<LiveInfo> liveInfoList = actionInfo.getLiveInfos();
                    for (LiveInfo entity : liveInfoList) {
                        liveInfo = entity;
                        if (liveInfo.getStatus() == LiveInfo.STATUS_ON_USE) break;
                    }
                    if (liveInfo != null && liveInfo.getStatus() == LiveInfo.STATUS_ON_USE) { //开始直播
                        setDataSourceByLiveId(liveInfo.getLiveId());
                        mCurrentLiveId = liveInfo.getLiveId();
                    } else {
                        int liveStatus = (liveInfo == null) ? -1 : liveInfo.getStatus();
                        processLiveStatus(liveStatus, bundle);
                    }
                } else {
                    processActionStatus(actionStatus, bundle);
                }

                break;

            default: //处理错误
                processError(what, bundle);
                break;
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "媒资数据事件——— 直播活动数据事件 event:" + what + " bundle:" + bundle.toString());
        return true;
    }


    /**
     * 处理云直播状态反馈
     *
     * @param status
     * @param bundle the extra
     * @return boolean
     */
    private boolean processActionStatus(int status, Bundle bundle) {
        int statusCode = (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_STATUS_CODE)) ? bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE) : -1;

        String errorCode = String.valueOf(status);
        String errorMsg = null;
        switch (status) {
            case ActionStatus.SATUS_NOT_START:
                errorMsg = "直播未开始，请稍后……";
                break;
            case ActionStatus.STATUS_END:
                errorMsg = "直播已结束";
                break;
            case ActionStatus.STATUS_LIVE_ING:
                errorMsg = "直播信号已恢复";
                break;
            case ActionStatus.STATUS_INTERRUPTED:
                errorMsg = "直播信号中断，请稍后……";
                break;
            default:
                errorMsg = "暂无直播信号，请稍后……";
        }

        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_MMS_STATCODE, statusCode);
        error.putString(EVENT_PROP_ERROR_CODE, errorCode);
        error.putString(EVENT_PROP_ERROR_MSG, errorMsg);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), error);
        return true;
    }

    /**
     * 云直播返回的机位信息事件
     *
     * @param what
     * @param bundle null
     * @return boolean
     */
    private boolean processMediaLiveLoad(int what, Bundle bundle) {
        int mediaStatusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);

        switch (mediaStatusCode) {

            case StatusCode.MEDIADATA_SUCCESS: //OK
                com.lecloud.sdk.api.md.entity.live.LiveInfo liveInfo = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
                if (liveInfo == null) return false;

                mCurrentLiveInfo = liveInfo;

                //获得默认码率和码率列表
                if (mRateList != null) mRateList.clear();
                mRateList = liveInfo.getVtypes();

                mCurrentRate = mDefaultRate = liveInfo.getDefaultVtype();

                int liveStatus = liveInfo.getActivityState();
                if (liveStatus == LiveInfo.STATUS_ON_USE) {
                    setDataSourceByRate(mCurrentRate);
                } else {
                    processLiveStatus(liveStatus, bundle);
                }
                break;

            default: //处理错误
                processError(what, bundle);
                break;
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "媒资数据事件——— 直播机位事件 event:" + what + " bundle:" + bundle.toString());
        return true;
    }


    /**
     * 处理云直播机位信息的状态反馈
     *
     * @param status
     * @param bundle the extra
     * @return boolean
     */
    private boolean processLiveStatus(int status, Bundle bundle) {
        int statusCode = (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_STATUS_CODE)) ? bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE) : -1;

        String errorCode = String.valueOf(status);
        String errorMsg = null;
        switch (status) {
            case LiveInfo.STATUS_NOT_USE:
                errorMsg = "直播未开始，请稍后……";
                break;
            case LiveInfo.STATUS_END:
                errorMsg = "直播已结束";
                break;
            case LiveInfo.STATUS_ON_USE:
                errorMsg = "直播信号已恢复";
                break;
            default:
                errorMsg = "暂无直播信号，请稍后……";
        }

        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_MMS_STATCODE, statusCode);
        error.putString(EVENT_PROP_ERROR_CODE, errorCode);
        error.putString(EVENT_PROP_ERROR_MSG, errorMsg);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), error);
        return true;
    }

    /**
     * 处理媒资调度数据获取的的事件
     *
     * @param what   MEDIADATA_GET_PLAYURL
     * @param bundle null
     * @return boolean
     */
    private boolean processMediaPlayURLLoad(int what, Bundle bundle) {
        // todo 调度信息获取
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_MMS_STATCODE, (bundle != null && bundle.containsKey(PlayerParams.KEY_RESULT_STATUS_CODE)) ? bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE) : -1);
//        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_MMS_HTTPCODE, (bundle != null && bundle.containsKey(PlayerParams.KEY_HTTP_CODE)) ? bundle.getInt(PlayerParams.KEY_HTTP_CODE) : -1);
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
    private boolean processAdvertStart(int what, Bundle bundle) {
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
    private boolean processAdvertComplete(int what, Bundle bundle) {
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
    private boolean processAdvertProgress(int what, Bundle bundle) {
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
    private boolean processAdvertError(int what, Bundle bundle) {
        processError(what, bundle);
        return true;
    }

    /**
     * 处理其他事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return boolean
     */
    private boolean processOtherEvent(int what, Bundle bundle) {
        WritableMap other = Arguments.createMap();
        other.putInt(EVENT_PROP_WHAT, what);
        other.putString(EVENT_PROP_EXTRA, (bundle != null) ? bundle.toString() : "");
        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_EVENT, other);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_OTHER_EVENT.toString(), event);
        return true;
    }

    /*============================= 各类事件回调 ===================================*/

    /**
     * 处理播放器事件，具体事件参见IPlayer类
     */
    @Override
    public void videoState(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.PLAY_INIT: //200
                // 播放器初始化
                handled = true;
                event = "PLAY_INIT";
//                processOtherEvent(state, bundle);
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
                processError(state, bundle);
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


    @Override
    public void onVideoRotate(int i) {

    }


    /**
     * 处理视频信息类事件
     */
    @Override
    public void onMediaDataPlayerEvent(int state, Bundle bundle) {
        boolean handled = false;
        String event = "";
        switch (state) {

            case PlayerEvent.MEDIADATA_VOD:  // 云点播返回媒资信息  6000
                handled = true;
                event = "MEDIADATA_VOD";
                processMediaVodLoad(state, bundle);
                break;

            case PlayerEvent.MEDIADATA_LIVE:  // 云直播返回的机位信息  6001
                handled = true;
                event = "MEDIADATA_LIVE";
                processMediaLiveLoad(state, bundle);
                break;

            case PlayerEvent.MEDIADATA_GET_PLAYURL:  // 云直播请求调度服务器回来的结果（rtmp直播）  6002
                handled = true;
                event = "MEDIADATA_GET_PLAYURL";
                processMediaPlayURLLoad(state, bundle);
                break;

            case PlayerEvent.MEDIADATA_ACTION:  // 云直播返回的活动信息（1个活动最多4个机位，在后台配置）6003
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
    @Override
    public void onAdPlayerEvent(int state, Bundle bundle) {
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
            Log.d(TAG, LogUtils.getTraceInfo() + "广告播放事件——— event：" + event + "，state：" + state + "，bundle：" + bundle);

    }


    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onAttachedToWindow 调起！");
        if (mOrientationSensorUtils == null) {
            mOrientationSensorUtils = new OrientationSensorUtils((Activity) mThemedReactContext.getBaseContext(), mOrientationChangeHandler);
        }
//        if(!mUseGravitySensor){
//            return;
//        }
        mOrientationSensorUtils.onResume();

        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onDetachedFromWindow 调起！");
        super.onDetachedFromWindow();

        if (mOrientationSensorUtils != null) {
            mOrientationSensorUtils.onPause();
        }
        mOrientationChangeHandler.removeCallbacksAndMessages(null);

        if (mMediaPlayer != null) {
            cleanupMediaPlayerResources();
        }
    }

    /*============================= 容器生命周期方法 ===================================*/


    @Override
    public void onHostResume() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostResume 调起！");

        if (mMediaPlayer != null) {
            mMediaPlayer.retry();
            if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE && mTimeShiftListener != null)
                ((IMediaDataLivePlayer) mMediaPlayer).startTimeShift();
        }
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostPause 调起！");

        if (mMediaPlayer != null) {
            if (mPlayMode == PlayerParams.VALUE_PLAYER_VOD) saveLastPostion();
            if (mPlayMode == PlayerParams.VALUE_PLAYER_ACTION_LIVE && mTimeShiftListener != null)
                ((IMediaDataLivePlayer) mMediaPlayer).stopTimeShift();
//            mLeVideoView.stopAndRelease();
            pause();
        }
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostDestroy 调起！");

        if (mMediaPlayer != null) {
            cleanupMediaPlayerResources();
        }
    }

}
