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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import android.widget.RelativeLayout;

import com.demoproject.utils.VideoLayoutParams;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.letv.android.client.sdk.api.md.entity.live.Stream;
import com.letv.android.client.sdk.constant.PlayerEvent;
import com.letv.android.client.sdk.constant.PlayerParams;
import com.letv.android.client.sdk.constant.StatusCode;
import com.letv.android.client.sdk.player.IMediaDataPlayer;
import com.letv.android.client.sdk.videoview.IMediaDataVideoView;
import com.letv.android.client.sdk.videoview.VideoViewListener;
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
        EVENT_LOAD_SOURCE("onSourceLoad"), // 传入数据源
        EVENT_CHANGESIZE("onVideoSizeChange"), // 视频真实宽高
        EVENT_LOAD_RATE("onVideoRateLoad"), // 视频码率列表
        EVENT_LOAD("onVideoLoad"), // 播放器准备完毕
        EVENT_ERROR("onVideoError"), // 播放出错
        EVENT_PROGRESS("onVideoProgress"), // 正在播放视频
        EVENT_PLAYABLE_PERCENT("onVideoBufferPercent"), // 缓存进度
        EVENT_PAUSE("onVideoPause"), // 播放暂停
        EVENT_RESUME("onVideoResume"), // 播放继续
        EVENT_SEEK("onVideoSeek"), // 播放跳转中
        EVENT_SEEK_COMPLETE("onVideoSeekComplete"), // 播放跳转结束
        EVENT_RATE_CHANG("onVideoRateChange"), //视频码率切换
        EVENT_END("onVideoEnd"),  // 播放完毕
        EVENT_BUFFER_START("onBufferStart"),  // 开始缓冲
        EVENT_BUFFER_END("onBufferEnd"), // 缓冲结束
        EVENT_VIDEO_RENDING_START("onVideoRendingStart"), // 加载第一帧
        EVENT_BUFFER_PERCENT("onBufferPercent"),  // 缓冲加载进度，转圈
        EVENT_AD_START("onAdvertStart"),  // 广告开始
        EVENT_AD_PROGRESS("onAdvertProgress"),  // 广告播放中
        EVENT_AD_COMPLETE("onAdvertComplete"),  // 广告结束
        EVENT_AD_ERROR("onAdvertError"),  // 广告出错
        EVENT_MEDIA_VOD("onMediaVodLoad"),  // 获得点播媒资
        EVENT_MEDIA_LIVE("onMediaLiveLoad"),  // 获得直播媒资
        EVENT_MEDIA_ACTION("onMediaActionLoad"),  // 获得活动直播媒资
        EVENT_MEDIA_PLAYURL("onMediaPlayURLLoad"),  // 获得媒资调度
        EVENT_OTHER_EVENT("onOtherEventInfo");  //其他事件

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static final String EVENT_PROP_DURATION = "duration"; //视频总长
    public static final String EVENT_PROP_PLAYABLE_DURATION = "playableDuration"; //可播放时长
    public static final String EVENT_PROP_PLAY_BUFFERPERCENT = PlayerParams.KEY_PLAY_BUFFERPERCENT; //二级进度长度百分比
    public static final String EVENT_PROP_CURRENT_TIME = "currentTime"; //当前时长
    public static final String EVENT_PROP_SEEK_TIME = "seekTime"; //跳转时间
    public static final String EVENT_PROP_NATURALSIZE = "naturalSize"; //视频原始尺寸
    public static final String EVENT_PROP_VIDEO_BUFF = PlayerParams.KEY_VIDEO_BUFFER;  //缓冲加载进度百分比
    public static final String EVENT_PROP_RATELIST = "rateList";  //可选择的码率
    public static final String EVENT_PROP_CURRENT_RATE = "currentRate"; //当前码率
    public static final String EVENT_PROP_NEXT_RATE = "nextRate"; //下一个码率

    public static final String EVENT_PROP_WIDTH = "width"; //视频宽度
    public static final String EVENT_PROP_HEIGHT = "height"; //视频高度

    public static final String EVENT_PROP_ORIENTATION = "orientation"; //视频方向
    public static final String EVENT_PROP_AD_TIME = "AdTime"; //广告倒计时
    public static final String EVENT_PROP_STAT_CODE = PlayerParams.KEY_RESULT_STATUS_CODE; //媒资状态码
    public static final String EVENT_PROP_RET_DATA = PlayerParams.KEY_RESULT_DATA; //媒资结果数据

    public static final String EVENT_PROP_HTTP_CODE = PlayerParams.KEY_HTTP_CODE; //媒资http请求状态

    public static final String EVENT_PROP_KEY = "key";
    public static final String EVENT_PROP_VALUE = "value";


    public static final String EVENT_PROP_ERROR = "error";
    public static final String EVENT_PROP_WHAT = "what";
    public static final String EVENT_PROP_EXTRA = "extra";
    public static final String EVENT_PROP_EVENT = "event";

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    /// 播放器
    private IMediaDataVideoView mLeVideoView;
    private IMediaDataPlayer mLePlayer;

    /// 播放器设置
    private int mPlayMode = PlayerParams.VALUE_PLAYER_VOD;
    private boolean mHasSkin = false; //是否有皮肤
    private boolean mPano = false;  //是否全景

    private LinkedHashMap<String, String> mRateList;  // 当前支持的码率
    private String mCurrentRate;  // 当前视频码率

    private boolean mLePlayerValid = false;  // 可用状态，prepared, started, paused，completed 时为true
    private boolean mPaused = false;  // 暂停状态
    private long mLastPosition;  //上次播放位置


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
            handlePlayerEvent(event, bundle);// 处理播放器类事件
            handleVideoInfoEvent(event, bundle);// 处理媒资类事件
            handleAdEvent(event, bundle);//处理广告类事件
            handleOtherEvent(event, bundle);//处理其他事件
        }

        @Override
        public String onGetVideoRateList(LinkedHashMap<String, String> map) {
            if (mRateList == null) mRateList = new LinkedHashMap<>();

            WritableArray rateList = Arguments.createArray();
            String rateStr = "";
            WritableMap rate;
            for (Map.Entry<String, String> rates : map.entrySet()) {
                rate = Arguments.createMap();
                rate.putString(EVENT_PROP_KEY, rates.getKey());
                rate.putString(EVENT_PROP_VALUE, rates.getValue());
                rateList.pushMap(rate);

                mRateList.put(rates.getKey(), rates.getValue());

                rateStr += rates.getKey() + rates.getValue();
            }

            WritableMap event = Arguments.createMap();
            event.putArray(EVENT_PROP_RATELIST, rateList);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD_RATE.toString(), event);
            Log.d("视频码率", "event " + Events.EVENT_LOAD_RATE.toString() + " " + rateStr);

            return "";
        }
    };

    // 进度更新线程
    private Handler mProgressUpdateHandler = new Handler();
    private Runnable mProgressUpdateRunnable = null;

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
        //initLePlayerIfNeeded();
        //setSurfaceTextureListener(this);

        //创建播放更新进度线程
        mProgressUpdateRunnable = new Runnable() {
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
        mProgressUpdateHandler.post(mProgressUpdateRunnable);
    }

    // 创建播放器及监听
    private void initLePlayerIfNeeded() {
        if (mLeVideoView == null) {

            mLePlayerValid = false;
            //创建播放器和播放容器
            View.inflate(mThemedReactContext, R.layout.video_play, this);
            Context ctx = mThemedReactContext.getBaseContext();
            switch (mPlayMode) {
                case PlayerParams.VALUE_PLAYER_LIVE: {
                    mLeVideoView = mHasSkin ? (mPano ? new UICPPanoLiveVideoView(ctx)
                            : new UICPLiveVideoView(ctx))
                            : new CPLiveVideoView(ctx);

                    mLePlayer = mHasSkin ? (mPano ? (IMediaDataPlayer) ((UICPPanoLiveVideoView) mLeVideoView).getPlayer()
                            : (IMediaDataPlayer) ((UICPLiveVideoView) mLeVideoView).getPlayer())
                            : (IMediaDataPlayer) ((CPLiveVideoView) mLeVideoView).getPlayer();
                    break;
                }
                case PlayerParams.VALUE_PLAYER_VOD: {
                    mLeVideoView = mHasSkin ? (mPano ? new UICPPanoVodVideoView(ctx)
                            : new UICPVodVideoView(ctx))
                            : new CPVodVideoView(ctx);

                    mLePlayer = mHasSkin ? (mPano ? (IMediaDataPlayer) ((UICPPanoVodVideoView) mLeVideoView).getPlayer()
                            : (IMediaDataPlayer) ((UICPVodVideoView) mLeVideoView).getPlayer())
                            : (IMediaDataPlayer) ((CPVodVideoView) mLeVideoView).getPlayer();

                    break;
                }
                case PlayerParams.VALUE_PLAYER_ACTION_LIVE: {
                    mLeVideoView = mHasSkin ? (mPano ? new UICPPanoActionLiveVideoView(ctx)
                            : new UICPActionLiveVideoView(ctx))
                            : new CPActionLiveVideoView(ctx);

                    mLePlayer = mHasSkin ? (mPano ? (IMediaDataPlayer) ((UICPPanoActionLiveVideoView) mLeVideoView).getPlayer()
                            : (IMediaDataPlayer) ((UICPActionLiveVideoView) mLeVideoView).getPlayer())
                            : (IMediaDataPlayer) ((CPActionLiveVideoView) mLeVideoView).getPlayer();
                    break;
                }
                default:
                    mLeVideoView = new LeBaseMediaDataVideoView(ctx);
                    mLePlayer = (IMediaDataPlayer) ((LeBaseMediaDataVideoView) mLePlayer).getPlayer();
                    break;
            }

            //将播放器放入容器
            RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            videoContainer.addView((View) mLeVideoView, VideoLayoutParams.computeContainerSize(mThemedReactContext, 16, 9));

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
        Log.d("外部控制", "传入数据源 bundle:" + bundle);

        if (bundle == null) return;

        mLePlayerValid = false;
        mVideoDuration = 0;
        mVideoBufferedDuration = 0;

        mPlayMode = (bundle.containsKey(LeVideoViewManager.PROP_PLAY_MODE) ? bundle.getInt(LeVideoViewManager.PROP_PLAY_MODE) : -1);
        mPano = (bundle.containsKey(LeVideoViewManager.PROP_SRC_IS_PANO) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_IS_PANO));
        mHasSkin = (bundle.containsKey(LeVideoViewManager.PROP_SRC_HAS_SKIN) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_HAS_SKIN));

        initLePlayerIfNeeded();

        if (mLeVideoView != null) {
            mLeVideoView.resetPlayer();
            mLastPosition = 0;
            mRateList = null;
            isCompleted = false;

            if (bundle.containsKey("path"))
                mLeVideoView.setDataSource(bundle.getString("path"));
            else
                mLeVideoView.setDataSource(bundle);
            mLeVideoView.setVideoViewListener(mVideoViewListener);

            WritableMap event = Arguments.createMap();
            event.putString(LeVideoViewManager.PROP_SRC, bundle.toString());
            mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD_SOURCE.toString(), event);
        }
    }

    /**
     * 视频Seek到某一位置
     *
     * @param msec the msec
     */
    public void seekTo(float msec) {
        Log.d("外部控制", "跳转视频到：" + msec);
        if (mLePlayerValid) {
            if (msec < 0 || msec > mVideoDuration) {
                return;
            }

            WritableMap event = Arguments.createMap();
            event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
            event.putDouble(EVENT_PROP_SEEK_TIME, msec / 1000.0);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK.toString(), event);

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
        Log.d("外部控制", "切换码率 current:" + mCurrentRate + " next:" + rate);
        if (TextUtils.isEmpty(rate)) {
            return;
        }

        // 检查码率是否可用
        if (mLePlayerValid && mRateList != null && mRateList.containsKey(rate)) {
            // 保存当前位置
            saveLastPostion();
            mCurrentRate = mLePlayer.getLastRate();

            //切换码率
            mLePlayer.setDataSourceByRate(rate);

            WritableMap event = Arguments.createMap();
            event.putString(EVENT_PROP_CURRENT_RATE, mCurrentRate);
            event.putString(EVENT_PROP_NEXT_RATE, rate);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_RATE_CHANG.toString(), event);

        }
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
     * 设置视频暂停和启动
     *
     * @param paused paused
     */
    public void setPausedModifier(final boolean paused) {
        Log.d("外部控制", "暂停或恢复播放 :" + paused);

        mPaused = paused;
        if (!mLePlayerValid) {
            return;
        }

        if (mPaused) {
            if (mLeVideoView.isPlaying()) {//播放中

                mLeVideoView.onPause();

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PAUSE.toString(), event);
            }
        } else {
            if (!mLeVideoView.isPlaying()) {//播放中

                if(isSeeking)
                    mLeVideoView.retry();
                else
                    mLeVideoView.onStart();

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_RESUME.toString(), event);
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
            mLePlayerValid = false;
            mLeVideoView.stopAndRelease();
        }
    }

    /**
     * 处理播放器准备完成事件
     */
    public void processPrepared(int what, Bundle bundle) {

        mLePlayerValid = true;
        mVideoDuration = mLeVideoView.getDuration();

        WritableMap naturalSize = Arguments.createMap();
        naturalSize.putInt(EVENT_PROP_WIDTH, mLeVideoView.getVideoWidth());
        naturalSize.putInt(EVENT_PROP_HEIGHT, mLeVideoView.getVideoHeight());
        //Log.d("视频尺寸", "长度" + mVideoDuration + "宽" + mLeVideoView.getVideoWidth() + "高" + mLeVideoView.getVideoHeight());
        if (mLeVideoView.getVideoWidth() > mLeVideoView.getVideoHeight())
            naturalSize.putString(EVENT_PROP_ORIENTATION, "landscape");
        else
            naturalSize.putString(EVENT_PROP_ORIENTATION, "portrait");

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
        event.putDouble(EVENT_PROP_CURRENT_TIME, mLeVideoView.getCurrentPosition() / 1000.0);
        event.putMap(EVENT_PROP_NATURALSIZE, naturalSize);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_LOAD.toString(), event);

        applyModifiers();

    }


    /*============================= 事件回调处理 ===================================*/

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
    public void processVideoSizeChanged(int what, Bundle bundle) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_WIDTH, (bundle != null && bundle.containsKey(PlayerParams.KEY_WIDTH)) ? bundle.getInt(PlayerParams.KEY_WIDTH) : -1);
        event.putInt(EVENT_PROP_HEIGHT, (bundle != null && bundle.containsKey(PlayerParams.KEY_HEIGHT)) ? bundle.getInt(PlayerParams.KEY_HEIGHT) : -1);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_CHANGESIZE.toString(), event);
    }


    /**
     * 处理播放器缓冲事件
     *
     * @param what   PLAY_BUFFERING
     * @param bundle Bundle[{bufferpercent=xx}]
     * @return boolean
     */
    public void processBufferingUpdate(int what, Bundle bundle) {
        // 正常播放状态
        isSeeking = false;

        int percent = (bundle != null && bundle.containsKey(PlayerParams.KEY_PLAY_BUFFERPERCENT)) ? bundle.getInt(PlayerParams.KEY_PLAY_BUFFERPERCENT) : 0;
        mVideoBufferedDuration = (int) Math.round((double) (mVideoDuration * percent) / 100.0);

        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_PLAY_BUFFERPERCENT, percent);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_PLAYABLE_PERCENT.toString(), event);
    }


    /**
     * 处理下载完成的事件
     *
     * @param what   PLAY_DOWNLOAD_FINISHED
     * @param bundle null
     * @return the boolean
     */
    public void processDownloadFinish(int what, Bundle bundle) {
        int percent = 100;
        mVideoBufferedDuration = (int) mVideoDuration;

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_PLAY_BUFFERPERCENT, percent);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_PLAYABLE_PERCENT.toString(), event);
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
     * 处理播放器出错事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
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
    public void processMediaVodLoad(int what, Bundle bundle) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_STAT_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_STAT_CODE)) ? bundle.getInt(EVENT_PROP_STAT_CODE) : -1);
        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_HTTP_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_HTTP_CODE)) ? bundle.getInt(EVENT_PROP_HTTP_CODE) : -1);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_VOD.toString(), event);
    }

    /**
     * 处理媒资直播数据获取的的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    public void processMediaLiveLoad(int what, Bundle bundle) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_STAT_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_STAT_CODE)) ? bundle.getInt(EVENT_PROP_STAT_CODE) : -1);
        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_HTTP_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_HTTP_CODE)) ? bundle.getInt(EVENT_PROP_HTTP_CODE) : -1);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_LIVE.toString(), event);
    }

    /**
     * 处理媒资活动直播数据获取的的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    public void processMediaActionLoad(int what, Bundle bundle) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_STAT_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_STAT_CODE)) ? bundle.getInt(EVENT_PROP_STAT_CODE) : -1);
        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_HTTP_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_HTTP_CODE)) ? bundle.getInt(EVENT_PROP_HTTP_CODE) : -1);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_ACTION.toString(), event);
    }

    /**
     * 处理媒资调度数据获取的的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    public void processMediaPlayURLLoad(int what, Bundle bundle) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_STAT_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_STAT_CODE)) ? bundle.getInt(EVENT_PROP_STAT_CODE) : -1);
        event.putString(EVENT_PROP_RET_DATA, (bundle != null && bundle.containsKey(EVENT_PROP_RET_DATA)) ? bundle.getString(EVENT_PROP_RET_DATA) : "");
        event.putInt(EVENT_PROP_HTTP_CODE, (bundle != null && bundle.containsKey(EVENT_PROP_HTTP_CODE)) ? bundle.getInt(EVENT_PROP_HTTP_CODE) : -1);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_PLAYURL.toString(), event);
    }

    /**
     * 处理广告开始的事件
     *
     * @param what   AD_START
     * @param bundle null
     * @return boolean
     */
    public void processAdvertStart(int what, Bundle bundle) {
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_START.toString(), null);
    }

    /**
     * 处理广告结束的事件
     *
     * @param what   AD_COMPLETE
     * @param bundle null
     * @return boolean
     */
    public void processAdvertComplete(int what, Bundle bundle) {
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_COMPLETE.toString(), null);
    }

    /**
     * 处理广告结束的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    public void processAdvertProgress(int what, Bundle bundle) {

        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_AD_TIME, (bundle != null && bundle.containsKey(EVENT_PROP_AD_TIME)) ? bundle.getInt(EVENT_PROP_AD_TIME) : 0);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_PROGRESS.toString(), event);
    }

    /**
     * 处理广告出错的事件
     *
     * @param what   AD_COMPLETE
     * @param bundle null
     * @return boolean
     */
    public void processAdvertError(int what, Bundle bundle) {
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_ERROR.toString(), null);
    }

    /**
     * 处理其他事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
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
            Log.d("播放器事件", "event " + event + " state " + state + " bundle " + bundle);
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
            Log.d("视频信息事件", "event " + event + " state " + state + " bundle " + bundle);
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
            Log.d("广告事件", "event " + event + " state " + state + " bundle " + bundle);
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
            Log.d("其他类事件", "event " + event + " state " + state + " bundle " + bundle);
    }

    /*============================= 容器生命周期方法 ===================================*/

    @Override
    public void onHostResume() {
        if (mLeVideoView != null) {
            mLeVideoView.onResume();
        }
    }

    @Override
    public void onHostPause() {
        if (mLeVideoView != null) {
            saveLastPostion();
            mLeVideoView.onPause();
        }
    }

    @Override
    public void onHostDestroy() {
        if (mLeVideoView != null) {
            cleanupMediaPlayerResources();
            //cleanupLayoutState(this);
        }
    }
}