/*************************************************************************
 * Description: 乐视视频活动机位播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-18
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.live.ActionLiveSubPlayer;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.base.BaseVideoView;

import java.util.LinkedHashMap;

/**
 * Created by RaoJia on 2016/12/18.
 */

public class LeReactSubVideoView extends BaseVideoView implements VideoViewListener {

    BaseSurfaceView mBaseSurfaceView;

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopAndRelease();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder.getSurface());
//            onResume();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }
    };

    public LeReactSubVideoView(Context context) {
        super(context);
        this.context = context;
    }

    protected void initPlayer() {
        player = new ActionLiveSubPlayer(context);
    }

    @Override
    public void setDataSource(String playUrl) {
        super.setDataSource(playUrl);
        setVideoViewListener(this);
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }


    @Override
    protected void prepareVideoSurface() {
        mBaseSurfaceView = new BaseSurfaceView(context);
        mBaseSurfaceView.getHolder().addCallback(surfaceCallback);
        mBaseSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mBaseSurfaceView.setZOrderOnTop(true);
        setVideoView(mBaseSurfaceView);
    }


    @Override
    protected void notifyPlayerEvent(int state, Bundle bundle) {
        super.notifyPlayerEvent(state, bundle);
        switch (state) {
            case PlayerEvent.PLAY_INIT:
                player.setVolume(0, 0);
                break;
            case PlayerEvent.PLAY_PREPARED:
                // 播放器准备完成，此刻调用start()就可以进行播放了
                onStart();
                break;

            default:
                break;
        }
    }

    @Override
    public void onStateResult(int event, Bundle bundle) {
        switch (event) {
            case PlayerEvent.PLAY_PREPARED:
                // 播放器准备完成，此刻调用start()就可以进行播放了
                onStart();
                break;

            case PlayerEvent.PLAY_ERROR:
//                if (mMultLivePlayHolder.no_video_layout != null) {
//                    rootView.removeView(mMultLivePlayHolder.no_video_layout);
//                }
//                View view = View.inflate(context, ReUtils.getLayoutId(context, "letv_skin_v4_large_mult_live_action_no_video_layout"), null);
//                rootView.addView(view, multLiveViewParams);
//                mMultLivePlayHolder.videoState = false;
//                mMultLivePlayHolder.no_video_layout = view;
                break;
            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                // 设置视频比例
//                    videoView.setVideoLayout(VideoViewSizeHelper.VIDEO_LAYOUT_STRETCH, 0);
                break;
            case PlayerEvent.PLAY_INFO:
                int code =bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
                if(code== StatusCode.PLAY_INFO_VIDEO_RENDERING_START){
//                    mMultLivePlayHolder.videoState = true;
                }

                break;

            default:
                break;
        }
    }

    @Override
    public String onGetVideoRateList(LinkedHashMap<String, String> linkedHashMap) {
        return null;
    }

    @Override
    public void onResume() {
        mBaseSurfaceView.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mBaseSurfaceView.setVisibility(View.GONE);
        super.onDestroy();
    }


}
