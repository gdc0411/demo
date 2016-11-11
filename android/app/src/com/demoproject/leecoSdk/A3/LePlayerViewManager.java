package com.demoproject.leecoSdk.A3;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.letv.android.client.cp.sdk.videoview.vod.CPVodVideoView;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by raojia on 16/9/6.
 */
public class LePlayerViewManager extends SimpleViewManager<CPVodVideoView> implements View.OnTouchListener {
    private static final String REACT_CLASS = "LePlayerView";

    private ThemedReactContext mContext;
    private CPVodVideoView videoView;

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

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected CPVodVideoView createViewInstance(final ThemedReactContext reactContext) {
        mContext = reactContext;
        videoView = new CPVodVideoView(mContext.getBaseContext());
        videoView.setVideoViewListener(mVideoViewListener);
        videoView.setOnTouchListener(this);

//        RelativeLayout videoContainer = (RelativeLayout) ((Activity)mContext.getBaseContext()).findViewById(R.id.videoContainer);
//        videoContainer.addView((View) videoView, VideoLayoutParams.computeContainerSize(mContext.getBaseContext(), 16, 9));

        return videoView;
    }


    @ReactProp(name = "dataSource")
    public void setDataSource(CPVodVideoView view, String playUrl) {
        Log.d("setDataSource -------", "setDataSource : " + playUrl + " : " + playUrl.equals("\"\""));
        if (!TextUtils.isEmpty(playUrl)) {
            view.setDataSource(playUrl);
            view.setVideoViewListener(mVideoViewListener);
        }
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
        Log.d("handleLiveEvent", "state " + state + " bundle " + bundle);
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
        Log.d("handleVideoInfoEvent", "state " + state + " bundle " + bundle);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
