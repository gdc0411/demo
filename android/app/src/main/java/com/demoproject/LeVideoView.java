package com.demoproject;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.demoproject.utils.VideoLayoutParams;
import com.facebook.react.uimanager.ThemedReactContext;
import com.letv.android.client.sdk.constant.PlayerEvent;
import com.letv.android.client.sdk.constant.PlayerParams;
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
 * Created by raojia on 16/10/28.
 */
public class LeVideoView extends RelativeLayout {

    private IMediaDataVideoView videoView;

    VideoViewListener mVideoViewListener = new VideoViewListener() {
        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
            //onInterceptAdEvent(event, bundle);//处理广告事件的
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

    private String mPlayUrl;
    private int mPlayMode = PlayerParams.VALUE_PLAYER_VOD;
    private boolean mHasSkin = false; //是否有皮肤
    private boolean mPano = false;  //是否全景

    //初始化RelativeLayout
    private void initView(ThemedReactContext context) {
        View.inflate(context, R.layout.video_play, this);
        Context ctx = context.getBaseContext();
        switch (mPlayMode) {
            case PlayerParams.VALUE_PLAYER_LIVE: {
                videoView = mHasSkin ? (mPano ? new UICPPanoLiveVideoView(ctx) : new UICPLiveVideoView(ctx)) : new CPLiveVideoView(ctx);
                break;
            }
            case PlayerParams.VALUE_PLAYER_VOD: {
                videoView = mHasSkin ? (mPano ? new UICPPanoVodVideoView(ctx) : new UICPVodVideoView(ctx)) : new CPVodVideoView(ctx);
                break;
            }
            case PlayerParams.VALUE_PLAYER_ACTION_LIVE: {
                videoView = mHasSkin ? (mPano ? new UICPPanoActionLiveVideoView(ctx) : new UICPActionLiveVideoView(ctx)) : new CPActionLiveVideoView(ctx);
                break;
            }
            default:
                videoView = new BaseMediaDataVideoView(ctx);
                break;
        }
        videoView.setVideoViewListener(mVideoViewListener);

        RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
        videoContainer.addView((View) videoView, VideoLayoutParams.computeContainerSize(context, 16, 9));

        videoView.setDataSource(mPlayUrl);

    }

    public LeVideoView(ThemedReactContext context) {
        super(context);
        initView(context);
    }

    public LeVideoView(ThemedReactContext context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LeVideoView(ThemedReactContext context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    /**
     * 传入数据源
     * @return
     */
    public void setDataSource( String playUrl) {
        Log.d("setDataSource -------", "setDataSource:playUrl : " + playUrl );
        if (videoView != null && !TextUtils.isEmpty(playUrl)) {
            videoView.setDataSource(playUrl);
            videoView.setVideoViewListener(mVideoViewListener);
        }
    }




    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        Log.d("handlePlayerEvent", "handlePlayerEvent:state " + state + " bundle " + bundle);
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
//                    try {
//                        Thread.sleep(50000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
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
        Log.d("handleLiveEvent", "handleLiveEvent:state " + state + " bundle " + bundle);
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
        Log.d("handleVideoInfoEvent", "handleVideoInfoEvent:state " + state + " bundle " + bundle);
    }

}
