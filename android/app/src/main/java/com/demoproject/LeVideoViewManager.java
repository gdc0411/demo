package com.demoproject;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.demoproject.utils.VideoLayoutParams;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
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
 * Created by raojia on 16/9/6.
 */
public class LeVideoViewManager extends SimpleViewManager<CPVodVideoView> {

    private ThemedReactContext mContext;

    private CPVodVideoView videoView;
    VideoViewListener mVideoViewListener = new VideoViewListener() {
        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调

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
    private Bundle mBundle;
    private int mPlayMode;
    private boolean mHasSkin;
    private boolean mPano;

    @Override
    public String getName() {
        return "LeVideoView";
    }

    @Override
    protected CPVodVideoView createViewInstance(ThemedReactContext reactContext) {
        mContext = reactContext;

        mPlayUrl = "http://cache.utovr.com/201601131107187320.mp4";
        mPlayMode = PlayerParams.VALUE_PLAYER_VOD;
        mHasSkin = false;
        mPano = false;

        videoView = new CPVodVideoView(mContext.getBaseContext());

        //videoView.setVideoViewListener(mVideoViewListener);

        LayoutInflater inflater = LayoutInflater.from(mContext.getBaseContext());
        RelativeLayout videoContainer = (RelativeLayout) inflater.inflate(R.layout.view_play_video, null);

        videoView.setLayoutParams(VideoLayoutParams.computeContainerSize(mContext, 16, 9));

        videoView.setDataSource(mPlayUrl);

        return videoView;
    }


    @ReactProp(name = "dataSource")
    public void setDataSource(CPVodVideoView view, String playUrl) {
        if (!TextUtils.isEmpty(playUrl)) {
            view.setDataSource(playUrl);
            view.setVideoViewListener(mVideoViewListener);
        }
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
