/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.lecloud.sdk.constant.PlayerParams;

import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by JiaRao on 2016/07/12.
 */
public class LeReactVideoViewManager extends SimpleViewManager<LeReactVideoView> {

    private ThemedReactContext mReactContext;

    @Override
    public String getName() {
        return REACT_CLASS_VIDEO_VIEW;
    }

    @Override
    protected LeReactVideoView createViewInstance(ThemedReactContext reactContext) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 createViewInstance 调起！");
        mReactContext = reactContext;
        return new LeReactVideoView(mReactContext);
    }

    @Override
    public void onDropViewInstance(LeReactVideoView videoView) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onDropViewInstance 调起！");
//        videoView.cleanupMediaPlayerResources();
        super.onDropViewInstance(videoView);
    }



    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    @Override
    @Nullable
    public Map getExportedViewConstants() {
        return MapBuilder.of(
                "ScaleNone", Integer.toString(ScalableType.LEFT_TOP.ordinal()),
                "ScaleToFill", Integer.toString(ScalableType.FIT_XY.ordinal()),
                "ScaleAspectFit", Integer.toString(ScalableType.FIT_CENTER.ordinal()),
                "ScaleAspectFill", Integer.toString(ScalableType.CENTER_CROP.ordinal())
        );
    }

    /**
     * 设置数据源，必填（VOD、LIVE）
     *
     * @param src 数据源包
     */
    @ReactProp(name = PROP_SRC)
    public void setDataSource(final LeReactVideoView videoView, final ReadableMap src) {
        if (src == null || !src.hasKey(PROP_SRC_PLAY_MODE) || src.getInt(PROP_SRC_PLAY_MODE) == -1 ) {
            return;
        }
        int playMode = src.getInt(PROP_SRC_PLAY_MODE);
        Bundle bundle;
        switch (playMode) {
            case PlayerParams.VALUE_PLAYER_VOD:
                bundle = new Bundle();
                bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
                bundle.putString(PlayerParams.KEY_PLAY_UUID, src.hasKey(PROP_SRC_VOD_UUID) ? src.getString(PROP_SRC_VOD_UUID) : "");
                bundle.putString(PlayerParams.KEY_PLAY_VUID, src.hasKey(PROP_SRC_VOD_UUID) ? src.getString(PROP_SRC_VOD_VUID) : "");
                bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, src.hasKey(PROP_SRC_VOD_UUID) ? src.getString(PROP_SRC_VOD_BUSINESSLINE) : "");
                bundle.putBoolean("saas", !src.hasKey(PROP_SRC_VOD_SAAS) || src.getBoolean(PROP_SRC_VOD_SAAS));
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
//                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                bundle.putBoolean("repeat", src.hasKey(PROP_SRC_IS_REPEAT) && src.getBoolean(PROP_SRC_IS_REPEAT));
                videoView.setSrc(bundle);
                break;

            case PlayerParams.VALUE_PLAYER_LIVE:
                break;

            case PlayerParams.VALUE_PLAYER_ACTION_LIVE:
                bundle = new Bundle();
                bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
                bundle.putString(PlayerParams.KEY_PLAY_ACTIONID, src.hasKey(PROP_SRC_ALIVE_ACTIONID) ? src.getString(PROP_SRC_ALIVE_ACTIONID) : "");
                bundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, src.hasKey(PROP_SRC_ALIVE_IS_USEHLS) && src.getBoolean(PROP_SRC_ALIVE_IS_USEHLS));
                bundle.putString(PlayerParams.KEY_PLAY_CUSTOMERID, src.hasKey(PROP_SRC_ALIVE_CUSTOMERID) ? src.getString(PROP_SRC_ALIVE_CUSTOMERID) : "");
                bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, src.hasKey(PROP_SRC_ALIVE_BUSINESSLINE) ? src.getString(PROP_SRC_ALIVE_BUSINESSLINE) : "");
                bundle.putString(PlayerParams.KEY_ACTION_CUID, src.hasKey(PROP_SRC_ALIVE_CUID) ? src.getString(PROP_SRC_ALIVE_CUID) : "");
                bundle.putString(PlayerParams.KEY_ACTION_UTOKEN, src.hasKey(PROP_SRC_ALIVE_UTIOKEN) ? src.getString(PROP_SRC_ALIVE_UTIOKEN) : "");
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
//                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                videoView.setSrc(bundle);
                break;

            case PlayerParams.VALUE_PLAYER_MOBILE_LIVE:
                break;

            default:
                //未知播放类型则为URI
                bundle = new Bundle();
                bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
                bundle.putString("path", src.hasKey(PROP_SRC_URI) ? src.getString(PROP_SRC_URI) :"");
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
//                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                bundle.putBoolean("repeat", src.hasKey(PROP_SRC_IS_REPEAT) && src.getBoolean(PROP_SRC_IS_REPEAT));
                videoView.setSrc(bundle);
                break;
        }

    }


    @ReactProp(name = PROP_PAUSED, defaultBoolean = false)
    public void setPaused(final LeReactVideoView videoView, final boolean paused) {
        videoView.setPaused(paused);
    }


    @ReactProp(name = PROP_REPEAT)
    public void setRepeat(final LeReactVideoView videoView, final int repeat) {
        videoView.setRepeat(repeat);
    }

    /**
     * 视频Seek到某一位置（VOD）
     * 直播Seek到某一时间（LIVE）
     *
     * @param seek the msec
     */
    @ReactProp(name = PROP_SEEK)
    public void setSeek(final LeReactVideoView videoView, final int seek) {
        videoView.setSeekTo(seek);
    }

    /**
     * 视频切换码率（VOD、LIVE）
     *
     * @param rate 码率值
     */
    @ReactProp(name = PROP_RATE)
    public void setRate(final LeReactVideoView videoView, final String rate) {
        videoView.setRate(rate);
    }

    /**
     * 云直播切换机位（LIVE）
     *
     * @param liveId 机位ID
     */
    @ReactProp(name = PROP_LIVE)
    public void setLive(final LeReactVideoView videoView, final String liveId) {
        videoView.setLive(liveId);
    }


    @ReactProp(name = PROP_CLICKAD, defaultBoolean = false)
    public void setClickAd(final LeReactVideoView videoView, final boolean isClicked) {
        videoView.setClickAd(isClicked);
    }

    /**
     * 设置左右声道（VOD、LIVE）
     *
     */
//    @ReactProp(name = PROP_TRACK)
//    public void setLeftAndRightTrack(final LeReactPlayer videoView, final float leftVolume, final float rightVolume) {
//        videoView.setLeftAndRightTrack(leftVolume, rightVolume);
//    }


    /**
     * 音量控制（VOD、LIVE）
     *
     * @param volume  音量控制 0-100
     */
    @ReactProp(name = PROP_VOLUME)
    public void setVolume(final LeReactVideoView videoView, final int volume) {
        videoView.setVolumePercent(volume);
    }

    /**
     /**
     * 设置亮度（VOD、LIVE）
     *
     * @param brightness 取值0-1
     */
    @ReactProp(name = PROP_BRIGHTNESS)
    public void setBrightness(final LeReactVideoView videoView, final int brightness) {
        videoView.setScreenBrightness( brightness);
    }

    /**
//     /**
//     * 设置转屏方向（VOD、LIVE）
//     *
//     * @param orientation 0-横屏，1-竖屏
//     */
//    @ReactProp(name = PROP_ORIENTATION)
//    public void setOrientation(final LeReactVideoView videoView, final int orientation) {
//        videoView.setOrientation(orientation);
//    }


    /**
     /**
     * 设置是否后台播放（VOD、LIVE）
     *
     * @param playInBackground 是否支持
     */
    @ReactProp(name = PROP_PLAY_IN_BACKGROUND, defaultBoolean = false)
    public void setPlayInBackground(final LeReactVideoView videoView, final boolean playInBackground) {
        videoView.setPlayInBackground(playInBackground);
    }

}
