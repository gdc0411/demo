package com.demoproject.leecoSdk.vod;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.demoproject.leecoSdk.LeVideoViewManager;

import com.demoproject.leecoSdk.timer.IChange;
import com.demoproject.leecoSdk.timer.LeTimerManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import com.letv.android.client.sdk.api.md.entity.vod.VideoHolder;
import com.letv.android.client.sdk.constant.PlayerEvent;
import com.letv.android.client.sdk.constant.PlayerParams;
import com.letv.android.client.sdk.constant.StatusCode;
import com.letv.android.client.sdk.player.IAdPlayer;
import com.letv.android.client.sdk.player.IMediaDataPlayer;
import com.letv.android.client.sdk.player.IPlayer;
import com.letv.android.client.skin.videoview.vod.CPVodVideoView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by raojia on 2016/11/2.
 */
public class ReactVodVideoView extends CPVodVideoView {

    public static final String TAG = "ReactVodVideoView";

    public enum Events {
        EVENT_LOAD_SOURCE("onSourceLoad"), // 传入数据源
        EVENT_CHANGESIZE("onVideoSizeChange"), // 视频真实宽高
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

    public static final String EVENT_PROP_TITLE = "title"; //视频标题
    public static final String EVENT_PROP_DURATION = "duration"; //视频总长
    public static final String EVENT_PROP_DEFAULT_RATE = "defaultRate"; //默认码率
    public static final String EVENT_PROP_RATELIST = "rateList";  //可选择的码率

    public static final String EVENT_PROP_WIDTH = "width"; //视频宽度
    public static final String EVENT_PROP_HEIGHT = "height"; //视频高度

    public static final String EVENT_PROP_PLAYABLE_DURATION = "playableDuration"; //可播放时长
    public static final String EVENT_PROP_PLAY_BUFFERPERCENT = PlayerParams.KEY_PLAY_BUFFERPERCENT; //二级进度长度百分比
    public static final String EVENT_PROP_CURRENT_TIME = "currentTime"; //当前时长
    public static final String EVENT_PROP_SEEK_TIME = "seekTime"; //跳转时间
    public static final String EVENT_PROP_NATURALSIZE = "naturalSize"; //视频原始尺寸
    public static final String EVENT_PROP_VIDEO_BUFF = PlayerParams.KEY_VIDEO_BUFFER;  //缓冲加载进度百分比
    public static final String EVENT_PROP_CURRENT_RATE = "currentRate"; //默认码率
    public static final String EVENT_PROP_NEXT_RATE = "nextRate"; //下一个码率


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

    // 传入上下文
    private ThemedReactContext mThemedReactContext;
    // 事件回调
    private RCTEventEmitter mEventEmitter;

    //播放进度更新
    private LeTimerManager mLeTimerManager;

//    private WaterMarkView mWaterMarkView; // 水印显示
//    private VideoNoticeView mNoticeView;  // 提示信息显示
//    private VideoLoading mVideoLoading;  //视频加载

    /// 播放器设置
    private boolean mHasSkin = false; //是否有皮肤
    private boolean mPano = false;  //是否全景

    private boolean mPaused = false;  // 视频是否在暂停状态
    private long mLastPosition;  //上次播放位置


    private String mVideoTitle = ""; //视频标题
    private long mVideoDuration = 0; //视频总长度
    private long mVideoBufferedPercent = 0;    //当前已Buffer的百分比

    private String mCurrentRate;  // 当前视频码率
    private LinkedHashMap<String, String> mRateList;  // 当前视频支持的码率
    private boolean isSeeking = false;  // 是否在缓冲加载状态


    public ReactVodVideoView(ThemedReactContext context) {
        super(context.getBaseContext());

        mThemedReactContext = context;

        //设置屏幕常亮
        ((Activity) context.getBaseContext()).getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ((Activity) context.getBaseContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //创建与RN之间的回调
        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);

//        //初始化加载显示
//        mVideoLoading = new VideoLoading(context);
//        addView(mVideoLoading, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//
//        //初始化水印显示
//        mWaterMarkView = new WaterMarkView(context);
//        addView(mWaterMarkView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//
//        //初始化提示信息
//        mNoticeView = new VideoNoticeView(context);
//        mNoticeView.setIsLive(false);
//        addView(mNoticeView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        mNoticeView.setVisibility(View.GONE);

    }

    public LeTimerManager getLeTimerManager(long delaymillts) {
        if (mLeTimerManager == null) {
            mLeTimerManager = new LeTimerManager(new IChange() {

                @Override
                public void onChange() {
                    if (player != null) {
                        post(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(TAG, "isSeeking" + isSeeking);

                                long currentPostion = player.getCurrentPosition();
                                mVideoDuration = player.getDuration();

                                if (!isSeeking && currentPostion <= mVideoDuration) {
                                    mVideoBufferedPercent = player.getBufferPercentage();

                                    WritableMap event = Arguments.createMap();
                                    event.putDouble(EVENT_PROP_CURRENT_TIME, currentPostion / 1000.0);
                                    event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                                    event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedPercent / 1000.0);
                                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);
                                }
//                                letvVodUICon.setPlayState(player.isPlaying());
                            }
                        });
                    }
                }
            }, delaymillts);
        }
        return mLeTimerManager;
    }


    private void stopTimer() {
        if (mLeTimerManager != null) {
            mLeTimerManager.stop();
            mLeTimerManager = null;
        }
    }

    private void startTimer() {
        if (mLeTimerManager == null) {
            getLeTimerManager(500);
        }
        if (mLeTimerManager != null) {
            mLeTimerManager.start();
        }
    }

    /**
     * 显示加载和显示水印
     */
//    private void showLoadingAndWaterMark(CoverConfig coverConfig) {
//        if (coverConfig != null && coverConfig.getLoadingConfig() != null && coverConfig.getLoadingConfig().getPicUrl() != null) {
//            mVideoLoading.setLoadingUrl(coverConfig.getLoadingConfig().getPicUrl());
//            mVideoLoading.showLoadingAnimation();
//        }
//        if (coverConfig != null && coverConfig.getWaterMarks() != null && coverConfig.getWaterMarks().size() > 0) {
//            mWaterMarkView.setWaterMarks(coverConfig.getWaterMarks());
//            mWaterMarkView.show();
//        }
//    }


    /*============================= 播放器外部接口 ===================================*/

    /**
     * 设置数据源
     *
     * @param bundle 数据源包
     * @return
     */
    @Override
    public void setDataSource(Bundle bundle) {
        Log.d(TAG, "外部控制—— 传入数据源 bundle:" + bundle);

        if (bundle == null) return;

        if (player != null) {

            mPano = (bundle.containsKey(LeVideoViewManager.PROP_SRC_IS_PANO) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_IS_PANO));
            mHasSkin = (bundle.containsKey(LeVideoViewManager.PROP_SRC_HAS_SKIN) && bundle.getBoolean(LeVideoViewManager.PROP_SRC_HAS_SKIN));

            resetPlayer();

            if (bundle.containsKey("path"))
                player.setDataSource(bundle.getString("path"));
            else
                ((IMediaDataPlayer) player).setDataSourceByMediaData(bundle);
//            setDataSource(bundle);

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
        Log.d(TAG, "外部控制—— 跳转视频到：" + msec);
        if (msec < 0 || msec > mVideoDuration) {
            return;
        }

        if (player != null) {
            player.seekTo(Math.round(msec * 1000.0f));
            if (isComplete()) {
                player.retry();
            } else if (!player.isPlaying()) {
                player.start();
            }

            WritableMap event = Arguments.createMap();
            event.putDouble(EVENT_PROP_CURRENT_TIME, getCurrentPosition() / 1000.0);
            event.putDouble(EVENT_PROP_SEEK_TIME, msec / 1000.0);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK.toString(), event);
        }
    }


    /**
     * 视频切换码率
     *
     * @param rate 码率值
     */
    public void setRate(String rate) {
        Log.d(TAG, "外部控制—— 切换码率 current:" + mCurrentRate + " next:" + rate);
        if (TextUtils.isEmpty(rate)) {
            return;
        }

        // 检查码率是否可用
        if (mRateList != null && mRateList.size() > 0 && mRateList.containsKey(rate)) {
            stopTimer(); //停止进度
            setLastPostion(); //保存上次位置
//            mVideoLoading.showLoadingProgress(); //显示加载

            ((IMediaDataPlayer) player).setDataSourceByRate(mRateList.get(rate));

            WritableMap event = Arguments.createMap();
            event.putString(EVENT_PROP_CURRENT_RATE, mCurrentRate);
            event.putString(EVENT_PROP_NEXT_RATE, rate);
            mEventEmitter.receiveEvent(getId(), Events.EVENT_RATE_CHANG.toString(), event);
        }
    }


    /**
     *
     **/
    public Bundle getReportParams() {
        return ((IMediaDataPlayer) player).getReportParams();
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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        if (ScreenUtils.getOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
//            letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT, ReactVodVideoView.this);
//        } else {
//            letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_LANDSCAPE, ReactVodVideoView.this);
//        }
        super.onConfigurationChanged(newConfig);
    }


//    @Override
//    protected void onInterceptVodMediaDataSuccess(int event, Bundle bundle) {
//        Log.d(TAG, "媒资信息事件：event " + event + " bundle " + bundle);
//        super.onInterceptVodMediaDataSuccess(event, bundle);
//
//        processVodMediaDataSuccess(event, bundle);
//    }


    @Override
    protected void onInterceptMediaDataError(int event, Bundle bundle) {
        Log.d(TAG, "媒资信息事件：event " + event + " bundle " + bundle);
        super.onInterceptMediaDataError(event, bundle);
//
//        mVideoLoading.hide();
//        mWaterMarkView.hide();
//        mNoticeView.processPlayerState(event, bundle);
    }

    @Override
    protected void notifyPlayerEvent(int event, Bundle bundle) {
        super.notifyPlayerEvent(event, bundle);
        //处理弹出信息
//        mNoticeView.processPlayerState(event, bundle);

        String event_name = "";
        switch (event) {

            case PlayerEvent.PLAY_COMPLETION: // 播放完毕  202
                event_name = "PLAY_COMPLETION";
                processCompletion(event, bundle);
                break;

            case PlayerEvent.PLAY_INFO:  // 获取播放器状态 206
                event_name = "PLAY_INFO";
                processPlayerInfo(event, bundle);
                break;

            case PlayerEvent.PLAY_PREPARED: { //播放器准备完毕 208
                event_name = "PLAY_PREPARED";
                processPrepared(event, bundle);
                break;
            }
            case PlayerEvent.PLAY_SEEK_COMPLETE: {// 用户跳转完毕 209
                event_name = "PLAY_SEEK_COMPLETE";
                processSeekComplete(event, bundle);
                break;
            }
            case PlayerEvent.PLAY_ERROR: //播放出错 205
                event_name = "PLAY_ERROR";
                processPlayerError(event, bundle);
                break;

            default:
                break;
        }
        Log.d(TAG, "播放器事件: what:" + event + " event:" + event_name + " bundle:" + bundle);
    }

    @Override
    protected void onInterceptAdEvent(int event, Bundle bundle) {

        String event_name = "";
        switch (event) {
            case PlayerEvent.AD_START:
                event_name = "AD_START";
                processAdvertStart(event, bundle);
                break;

            case PlayerEvent.AD_ERROR:
                event_name = "AD_ERROR";
                processAdvertError(event, bundle);
                break;

            case IAdPlayer.AD_PLAY_ERROR:
            case PlayerEvent.AD_COMPLETE:
                event_name = "AD_COMPLETE";
                processAdvertComplete(event, bundle);
                break;

            case PlayerEvent.AD_PROGRESS:
                event_name = "AD_PROGRESS";
                processAdvertProgress(event, bundle);
                break;

            default:
                break;
        }
        super.onInterceptAdEvent(event, bundle);
        Log.d(TAG, "播放器事件: what:" + event + " event:" + event_name + " bundle:" + bundle);
    }


    /*============================= 事件回调处理 ===================================*/


    private void processVodMediaDataSuccess(int what, Bundle bundle) {

        VideoHolder videoHolder = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);

        //获得视频标题
        String title = videoHolder.getTitle();
        if (!TextUtils.isEmpty(title)) {
            mVideoTitle = title;
        }
        //获得视频长度
        mVideoDuration = Long.parseLong(videoHolder.getVideoDuration());

        //获得默认码率和码率列表
        mRateList = videoHolder.getVtypes();
        mCurrentRate = videoHolder.getDefaultVtype();

        WritableArray rateList = Arguments.createArray();
        String rateStr = "";
        WritableMap rate;
        for (Map.Entry<String, String> rates : mRateList.entrySet()) {
            rate = Arguments.createMap();
            rate.putString(EVENT_PROP_KEY, rates.getKey());
            rate.putString(EVENT_PROP_VALUE, rates.getValue());
            rateList.pushMap(rate);

            rateStr += rates.getKey() + rates.getValue();
        }

        //事件装箱操作
        WritableMap event = Arguments.createMap();
        event.putString(EVENT_PROP_TITLE, mVideoTitle);
        event.putDouble(EVENT_PROP_DURATION, mVideoDuration);
        event.putString(EVENT_PROP_DEFAULT_RATE, videoHolder.getDefaultVtype());
        event.putArray(EVENT_PROP_RATELIST, rateList);

        mEventEmitter.receiveEvent(getId(), Events.EVENT_MEDIA_VOD.toString(), event);
        Log.d(TAG, "媒资数据事件——— event " + Events.EVENT_MEDIA_VOD.toString() + " " + rateStr);

        // todo 暂时是切换到默认码率，以后交给RN来控制，有可能从历史选择里进行
//        mCurrentRate = mRateList.get(onInterceptSelectDefiniton(mRateList, videoHolder.getDefaultVtype()));

        // 显示视频加载和显示水印
//        showLoadingAndWaterMark(videoHolder.getCoverConfig());
    }


    /**
     * 处理播放器准备完成事件
     */
    public void processPrepared(int what, Bundle bundle) {

        mVideoDuration = player.getDuration();
        //启动视频加载
//        mVideoLoading.showLoadingProgress();

        //返回事件
        WritableMap naturalSize = Arguments.createMap();
        naturalSize.putInt(EVENT_PROP_WIDTH, player.getVideoWidth());
        naturalSize.putInt(EVENT_PROP_HEIGHT, player.getVideoHeight());

        if (player.getVideoWidth() > player.getVideoHeight())
            naturalSize.putString(EVENT_PROP_ORIENTATION, "landscape");
        else
            naturalSize.putString(EVENT_PROP_ORIENTATION, "portrait");

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
        event.putDouble(EVENT_PROP_CURRENT_TIME, player.getCurrentPosition() / 1000.0);
        event.putMap(EVENT_PROP_NATURALSIZE, naturalSize);
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
            case StatusCode.PLAY_INFO_BUFFERING_START://500004 缓冲开始
                isSeeking = true;
//                mVideoLoading.showLoadingProgress();
                mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_START.toString(), null);
                break;

            case StatusCode.PLAY_INFO_BUFFERING_END://500005  缓冲结束
//                mVideoLoading.hide(); //隐藏加载
                mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_END.toString(), null);
                break;

            case StatusCode.PLAY_INFO_VIDEO_RENDERING_START://500006 渲染第一帧完成
                startTimer(); //开启加载

//                mWaterMarkView.show(); //显示水印
//                mVideoLoading.hide(); //隐藏加载
                mEventEmitter.receiveEvent(getId(), Events.EVENT_VIDEO_RENDING_START.toString(), null);
                break;

            case StatusCode.PLAY_INFO_VIDEO_BUFFERPERCENT: // 600006 视频缓冲时的进度，开始转圈
                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_VIDEO_BUFF, bundle.containsKey(EVENT_PROP_VIDEO_BUFF) ?
                        bundle.getInt(EVENT_PROP_VIDEO_BUFF) : 0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_BUFFER_PERCENT.toString(), event);
                break;
        }
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
        setLastPostion();  //保存上次位置
        isSeeking = false;
        mEventEmitter.receiveEvent(getId(), Events.EVENT_SEEK_COMPLETE.toString(), null);
        return true;
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
     * 处理下载完成的事件
     *
     * @param what   PLAY_DOWNLOAD_FINISHED
     * @param bundle null
     * @return the boolean
     */
    public void processDownloadFinish(int what, Bundle bundle) {
        int percent = 100;
        mVideoBufferedPercent = (int) mVideoDuration;

        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_PLAY_BUFFERPERCENT, percent);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_PLAYABLE_PERCENT.toString(), event);
    }


    /**
     * 处理播放器出错事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public boolean processPlayerError(int what, Bundle bundle) {

//        mWaterMarkView.hide(); //隐藏水印

        WritableMap error = Arguments.createMap();
        error.putInt(EVENT_PROP_WHAT, what);
        error.putString(EVENT_PROP_EXTRA, (bundle != null) ? bundle.toString() : "");
        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR, error);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_ERROR.toString(), event);
        return false;
    }


    /**
     * 处理广告开始的事件
     *
     * @param what   AD_START
     * @param bundle null
     * @return boolean
     */
    public void processAdvertStart(int what, Bundle bundle) {
//        mVideoLoading.hide();
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
     * 处理广告进行中的事件
     *
     * @param what   AD_PROGRESS
     * @param bundle null
     * @return boolean
     */
    public void processAdvertProgress(int what, Bundle bundle) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_AD_TIME, (bundle != null && bundle.containsKey(IAdPlayer.AD_TIME)) ? bundle.getInt(IAdPlayer.AD_TIME) : 0);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_AD_PROGRESS.toString(), event);
    }

    /**
     * 处理广告出错的事件
     *
     * @param what   AD_ERROR
     * @param bundle null
     * @return boolean
     */
    public void processAdvertError(int what, Bundle bundle) {
//        mNoticeView.processPlayerState(what, bundle);
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


    /**
     * 设置视频暂停和启动
     *
     * @param paused paused
     */
    public void setPausedModifier(final boolean paused) {
        Log.d(TAG, "外部控制—— 暂停或恢复播放 :" + paused);

        mPaused = paused;
        if (player == null) {
            return;
        }

        if (mPaused) {
            if (player.isPlaying()) {//播放中

                player.pause();

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, player.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PAUSE.toString(), event);
            }
        } else {
            if (!player.isPlaying()) {//播放中

                player.start();

                WritableMap event = Arguments.createMap();
                event.putDouble(EVENT_PROP_DURATION, mVideoDuration / 1000.0);
                event.putDouble(EVENT_PROP_CURRENT_TIME, player.getCurrentPosition() / 1000.0);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_RESUME.toString(), event);
            }
        }
    }


    /**
     * 销毁播放器，释放资源
     */
    public void cleanupMediaPlayerResources() {
        stopAndRelease();
    }


    /**
     * 设置回到某一播放的地址
     */
    public void setPostionModifier(final long lastPosition) {
        mLastPosition = lastPosition;

        //回到上次播放位置
        if (player != null && mLastPosition != 0) {
            player.seekToLastPostion(lastPosition);
        }

    }

    /**
     * 根据当前状态设置播放器
     */
    private void applyModifiers() {
        setPausedModifier(mPaused);
        setPostionModifier(mLastPosition);
    }


    /**
     * 处理播放完成事件
     *
     * @param what   the what
     * @param bundle the extra
     * @return the boolean
     */
    public void processCompletion(int what, Bundle bundle) {
        if (player != null) {
            mVideoDuration = player.getDuration();
            setLastPostion();
        }
        mLastPosition = 0;
        stopTimer();

        mEventEmitter.receiveEvent(getId(), Events.EVENT_END.toString(), null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        setLastPostion();
    }

    @Override
    public void onResume() {
        //横屏设置ILetvVodUICon.SCREEN_LANDSCAPE,竖屏设置ILetvVodUICon.SCREEN_PORTRAIT
        if (isFirstPlay) {
//            letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT, this);
            isFirstPlay = false;
        }
        super.onResume();
    }

    public boolean isComplete() {
        //TODO
        return player != null && player.getStatus() == IPlayer.PLAYER_STATUS_EOS;
    }


    private void setLastPostion() {
        if (player == null || player.getCurrentPosition() == 0) {
            return;
        }
        mLastPosition = player.getCurrentPosition();
    }

    @Override
    public void resetPlayer() {
        super.resetPlayer();
        stopTimer();

        mLastPosition = 0;
        mRateList = null;
        isSeeking = false;
    }

}
