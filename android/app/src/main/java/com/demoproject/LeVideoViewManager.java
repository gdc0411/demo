package com.demoproject;

import android.os.Bundle;
import android.text.TextUtils;

import com.demoproject.utils.VideoLayoutParams;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.base.BaseMediaDataVideoView;

/**
 * Created by raojia on 16/9/6.
 */
public class LeVideoViewManager extends SimpleViewManager<BaseMediaDataVideoView> {

    private ThemedReactContext mContext;
    private BaseMediaDataVideoView videoView;
//    private String mPlayUrl = "http://cache.utovr.com/201601131107187320.mp4";

    VideoViewListener mVideoViewListener = new VideoViewListener() {
        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
        }
    };

    @Override
    public String getName() {
        return "LeVideoView";
    }

    @Override
    protected BaseMediaDataVideoView createViewInstance(ThemedReactContext reactContext) {
        mContext = reactContext;
        videoView = new BaseMediaDataVideoView(mContext);
        videoView.setLayoutParams(VideoLayoutParams.computeContainerSize(mContext, 16, 9));
        videoView.setVideoViewListener(mVideoViewListener);
//        videoView.setDataSource(mPlayUrl);
        return videoView;
    }

    @ReactProp(name = "dataSource")
    public void setDataSource( BaseMediaDataVideoView view, String playUrl ){
        if(!TextUtils.isEmpty(playUrl) ) {
            view.setDataSource(playUrl);
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
