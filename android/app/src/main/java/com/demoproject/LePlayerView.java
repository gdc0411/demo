package com.demoproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.demoproject.handler.GetDeviceInfo;
import com.demoproject.utils.VideoLayoutParams;
import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.base.BaseMediaDataVideoView;
import com.lecloud.sdk.videoview.vod.VodVideoView;
import com.lecloud.skin.videoview.pano.vod.PanoVodVideoView;
import com.lecloud.skin.videoview.pano.vod.UIPanoVodVideoView;
import com.lecloud.skin.videoview.vod.UIVodVideoView;

/**
 * Created by raojia on 16/9/5.
 */
public class LePlayerView extends RelativeLayout {

    public final static String DATA = "data";

    private final ThemedReactContext mContext;
//    private final Activity mActivity;

    private IMediaDataVideoView videoView;
    VideoViewListener mVideoViewListener = new VideoViewListener() {

        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
        }
    };

    private String mPlayUrl;
    private Bundle mBundle;
    private int mPlayMode;
    private boolean mHasSkin;
    private boolean mPano;


    /**
     * 创建View
     */
    protected void createView() {

//        mActivity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View.inflate(mContext, R.layout.view_play_video, this);

        initData();

        mHasSkin = true;//mActivity.getIntent().getBundleExtra(DATA).getBoolean("hasSkin");
        mPano = false;//mActivity.getIntent().getBundleExtra(DATA).getBoolean("pano");
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
//        Intent intent = mActivity.getIntent();
//        if (intent != null) {
//            mBundle = intent.getBundleExtra(DATA);
//            if (mBundle == null) {
//                Toast.makeText(mActivity, "no data", Toast.LENGTH_LONG).show();
//                return;
//            } else {
//                mPlayUrl = mBundle.getString("path");
//                mPlayMode = mBundle.getInt(PlayerParams.KEY_PLAY_MODE, -1);
//            }
//        }
        mPlayUrl = "http://cache.utovr.com/201601131107187320.mp4";
        mPlayMode = -1;//PlayerParams.VALUE_PLAYER_VOD;
    }


    /**
     * 初始化视图
     */
    private void initView() {
        switch (mPlayMode) {
//            case PlayerParams.VALUE_PLAYER_LIVE: {
//                videoView = mHasSkin ? (mPano ? new UIPanoLiveVideoView(this) : new UILiveVideoView(this)) : (mPano ? new PanoLiveVideoView(this) : new LiveVideoView(this));
//                break;
//            }
            case PlayerParams.VALUE_PLAYER_VOD: {
                videoView = mHasSkin ? (mPano ? new UIPanoVodVideoView(mContext) : new UIVodVideoView(mContext)) : (mPano ? new PanoVodVideoView(mContext) : new VodVideoView(mContext));
                break;
            }
//            case PlayerParams.VALUE_PLAYER_ACTION_LIVE: {
//                videoView = mHasSkin ? (mPano ? new UIPanoActionLiveVideoView(this) : new UIActionLiveVideoView(this)) : (mPano ? new PanoActionLiveVideoView(this) : new ActionLiveVideoView(this));
//                videoView.setCacheWatermark(800,200);
//                videoView.setMaxDelayTime(1000);
//                videoView.setCachePreSize(500);
//                videoView.setCacheMaxSize(10000);
//                break;
//            }
            default:
                videoView = new BaseMediaDataVideoView(mContext);
                break;
        }

        videoView.setVideoViewListener(mVideoViewListener);

        RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
        videoContainer.addView((View) videoView, VideoLayoutParams.computeContainerSize(mContext, 16, 9));

        if (!TextUtils.isEmpty(mPlayUrl)) {
            videoView.setDataSource(mPlayUrl);
        } else {
            videoView.setDataSource(mBundle);
        }
    }


    public LePlayerView(Context context) {
        super(context);
        mContext = (ThemedReactContext) context;
//        mActivity = mContext.getCurrentActivity();
        createView();
    }

    public LePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (ThemedReactContext) context;
//        mActivity = mContext.getCurrentActivity();
        createView();
    }

    public LePlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = (ThemedReactContext) context;
//        mActivity = mContext.getCurrentActivity();
        createView();
    }

    public LePlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = (ThemedReactContext) context;
//        mActivity = mContext.getCurrentActivity();
        createView();
    }


    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        switch (state) {
            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                /**
                 * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
                 * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
                 * 意味着你的surfaceView显示的内容有可能是拉伸的
                 */
                break;

            case PlayerEvent.PLAY_PREPARED:
                // 播放器准备完成，此刻调用start()就可以进行播放了
                if (videoView != null) {
                    videoView.onStart();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
    }
}
