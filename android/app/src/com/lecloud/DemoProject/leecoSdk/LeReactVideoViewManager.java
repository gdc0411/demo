/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
package com.lecloud.DemoProject.leecoSdk;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.DemoProject.utils.LogUtils;
import com.lecloud.sdk.constant.PlayerParams;

import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_BRIGHTNESS;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_CLICKAD;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_LIVE;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_ORIENTATION;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_PAUSED;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_PLAY_IN_BACKGROUND;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_PLAY_MODE;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_RATE;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SEEK;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_ALIVE_ACTIONID;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_ALIVE_BUSINESSLINE;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_ALIVE_CUID;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_ALIVE_CUSTOMERID;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_ALIVE_IS_USEHLS;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_ALIVE_UTIOKEN;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_HAS_SKIN;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_IS_PANO;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_VOD_BUSINESSLINE;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_VOD_SAAS;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_VOD_UUID;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_SRC_VOD_VUID;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_URI;
import static com.lecloud.DemoProject.leecoSdk.Constants.PROP_VOLUME;
import static com.lecloud.DemoProject.leecoSdk.Constants.REACT_CLASS;
import static com.lecloud.DemoProject.utils.LogUtils.TAG;

/**
 * Created by JiaRao on 2016/31/10.
 */
public class LeReactVideoViewManager extends SimpleViewManager<LeReactVideoView> {

    private ThemedReactContext mReactContext;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected LeReactVideoView createViewInstance(ThemedReactContext reactContext) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 createViewInstance 调起！");
        mReactContext = reactContext;
        return new LeReactPlayer(mReactContext);
    }

    @Override
    public void onDropViewInstance(LeReactPlayer videoView) {
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
    public void setDataSource(final LeReactPlayer videoView, final ReadableMap src) {
        if (src == null || !src.hasKey(PROP_PLAY_MODE) || src.getInt(PROP_PLAY_MODE) == -1 ) {
            return;
        }
        int playMode = src.getInt(PROP_PLAY_MODE);
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
                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
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
                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                videoView.setSrc(bundle);
                break;

            case PlayerParams.VALUE_PLAYER_MOBILE_LIVE:
                break;

            default:
                //未知播放类型则为URI
                bundle = new Bundle();
                bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
                bundle.putString("path", src.hasKey(PROP_URI) ? src.getString(PROP_URI) :"");
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                videoView.setSrc(bundle);
                break;
        }

    }


    @ReactProp(name = PROP_PAUSED, defaultBoolean = false)
    public void setPaused(final LeReactPlayer videoView, final boolean paused) {
        videoView.setPaused(paused);
    }


    /**
     * 视频Seek到某一位置（VOD）
     * 直播Seek到某一时间（LIVE）
     *
     * @param seek the msec
     */
    @ReactProp(name = PROP_SEEK)
    public void setSeek(final LeReactPlayer videoView, final int seek) {
        videoView.setSeekTo(seek);
    }

    /**
     * 视频切换码率（VOD、LIVE）
     *
     * @param rate 码率值
     */
    @ReactProp(name = PROP_RATE)
    public void setRate(final LeReactPlayer videoView, final String rate) {
        videoView.setRate(rate);
    }

    /**
     * 云直播切换机位（LIVE）
     *
     * @param liveId 机位ID
     */
    @ReactProp(name = PROP_LIVE)
    public void setLive(final LeReactPlayer videoView, final String liveId) {
        videoView.setLive(liveId);
    }


    @ReactProp(name = PROP_CLICKAD, defaultBoolean = false)
    public void setClickAd(final LeReactPlayer videoView, final boolean isClicked) {
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
    public void setVolume(final LeReactPlayer videoView, final int volume) {
        videoView.setVolumePercent(volume);
    }

    /**
     /**
     * 设置亮度（VOD、LIVE）
     *
     * @param brightness 取值0-1
     */
    @ReactProp(name = PROP_BRIGHTNESS)
    public void setBrightness(final LeReactPlayer videoView, final int brightness) {
        videoView.setScreenBrightness( brightness);
    }

    /**
     /**
     * 设置转屏方向（VOD、LIVE）
     *
     * @param orientation 0-横屏，1-竖屏
     */
    @ReactProp(name = PROP_ORIENTATION)
    public void setOrientation(final LeReactPlayer videoView, final int orientation) {
        videoView.setOrientation(orientation);
    }


    /**
     /**
     * 设置是否后台播放（VOD、LIVE）
     *
     * @param playInBackground 是否支持
     */
    @ReactProp(name = PROP_PLAY_IN_BACKGROUND, defaultBoolean = false)
    public void setPlayInBackground(final LeReactPlayer videoView, final boolean playInBackground) {
        videoView.setPlayInBackground(playInBackground);
    }

}
