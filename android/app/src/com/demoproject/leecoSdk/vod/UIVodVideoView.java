package com.demoproject.leecoSdk.vod;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.demoproject.utils.ScreenUtils;
import com.lecloud.skin.R;
import com.letv.android.client.sdk.api.md.entity.vod.VideoHolder;
import com.letv.android.client.sdk.constant.PlayerEvent;
import com.letv.android.client.sdk.constant.PlayerParams;
import com.letv.android.client.sdk.constant.StatusCode;
import com.letv.android.client.sdk.player.IAdPlayer;
import com.letv.android.client.sdk.player.IMediaDataPlayer;
import com.letv.android.client.sdk.player.IPlayer;
import com.letv.android.client.sdk.videoview.vod.VodVideoView;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UIVodVideoView extends VodVideoView {
    public static final String TAG = "UIVodVideoView";

    private long lastPosition;
    private LinkedHashMap<String, String> vtypeList;
    //是否正在seeking
    private boolean isSeeking = false;

    public UIVodVideoView(Context context) {
        super(context);
    }


    public Bundle getReportParams() {
        return ((IMediaDataPlayer) player).getReportParams();
    }

    public void rePlay() {
        setLastPostion();
        player.retry();
    }

    public void setRequestedOrientation(int requestedOrientation) {
        if (context instanceof Activity) {
            ((Activity) context).setRequestedOrientation(requestedOrientation);
        }
    }

    public void setDefination(String rate) {
//                UIVodVideoView.super.onInterceptMediaDataSuccess(event, bundle);
        setLastPostion();
        ((IMediaDataPlayer) player).setDataSourceByRate(rate);

    }

    public void onSeekTo(float sec) {
        Log.d("meng", "onSeekTo" + isSeeking);
        long msec = (long) Math.floor((sec * player.getDuration()));
        if (player != null) {
            player.seekTo(msec);
            if (isComplete()) {
                player.retry();
            } else if (!player.isPlaying()) {
                player.start();
            }
        }
    }

    public void onClickPlay() {
        if (player.isPlaying()) {
            player.pause();
        } else if (isComplete()) {
            player.seekTo(0);
            player.retry();
        } else {
            player.start();
        }
    }


    protected int switchControllMode(int interactiveMode) {
        return -1;
    }

    protected int switchDisplayMode(int displayMode) {
        return -1;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (ScreenUtils.getOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
        } else {
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onInterceptVodMediaDataSuccess(int event, Bundle bundle) {
        super.onInterceptVodMediaDataSuccess(event, bundle);
        VideoHolder videoHolder = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
        vtypeList = videoHolder.getVtypes();
        String title = videoHolder.getTitle();
        if (!TextUtils.isEmpty(title)) {
        }
        String currentDefiniton = vtypeList.get(onInterceptSelectDefiniton(vtypeList, videoHolder.getDefaultVtype()));
        List<String> ratetypes = new ArrayList<String>(videoHolder.getVtypes().values());

    }

    @Override
    protected void onInterceptMediaDataError(int event, Bundle bundle) {
        super.onInterceptMediaDataError(event, bundle);
    }


    @Override
    protected void notifyPlayerEvent(int event, Bundle bundle) {
        super.notifyPlayerEvent(event, bundle);
        switch (event) {
            case PlayerEvent.PLAY_COMPLETION://202
                lastPosition = 0;
                break;
            case PlayerEvent.PLAY_INFO:
                int code = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
                if (code == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
                }
                break;
            case PlayerEvent.PLAY_PREPARED: {
                if (lastPosition > 0) {
                    player.seekToLastPostion(lastPosition);
                }
                break;
            }
            case PlayerEvent.PLAY_SEEK_COMPLETE: {//209
                setLastPostion();
                isSeeking = false;
//                Log.e("meng", "PlayerEvent.PLAY_SEEK_COMPLETE: " + lastPosition);
                break;
            }
            case PlayerEvent.PLAY_ERROR://205
                break;
            default:
                break;
        }
    }

    @Override
    protected void onInterceptAdEvent(int code, Bundle bundle) {
        super.onInterceptAdEvent(code, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        lastPosition = player.getCurrentPosition();
    }

    @Override
    public void resetPlayer() {
        super.resetPlayer();
        lastPosition = 0;
        vtypeList = null;
        isSeeking = false;
    }
}
