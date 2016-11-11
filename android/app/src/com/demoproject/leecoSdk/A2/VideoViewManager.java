/*************************************************************************
 * Description: 乐视视频播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
package com.demoproject.leecoSdk.A2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


import com.demoproject.leecoSdk.A2.ReactCPVodVideoView;
import com.demoproject.leecoSdk.Events;
import com.demoproject.leecoSdk.ScalableType;
import com.demoproject.utils.LogUtils;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.sdk.constant.PlayerParams;

import static com.demoproject.leecoSdk.Constants.*;
import static com.demoproject.utils.LogUtils.TAG;


import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by JiaRao on 2016/31/10.
 */
public class VideoViewManager extends SimpleViewManager<ReactCPVodVideoView> {


    private ThemedReactContext mReactContext;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactCPVodVideoView createViewInstance(ThemedReactContext reactContext) {
        mReactContext = reactContext;
        return new ReactCPVodVideoView(mReactContext);
    }

    @Override
    public void onDropViewInstance(ReactCPVodVideoView videoView) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onDropViewInstance 调起！");
        super.onDropViewInstance(videoView);
        videoView.cleanupMediaPlayerResources();
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


    @ReactProp(name = PROP_SRC)
    public void setDataSource(final ReactCPVodVideoView videoView, @Nullable ReadableMap src) {
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
                bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, src.hasKey(PROP_SRC_VOD_UUID) ? src.getString(PROP_SRC_VOD_BUSINESSLINE) : "102");
                bundle.putBoolean("saas", !src.hasKey(PROP_SRC_VOD_SAAS) || src.getBoolean(PROP_SRC_VOD_SAAS));
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                videoView.setDataSource(bundle);
                break;

            case PlayerParams.VALUE_PLAYER_LIVE:
                break;

            case PlayerParams.VALUE_PLAYER_ACTION_LIVE:
                bundle = new Bundle();
                bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
                bundle.putString(PlayerParams.KEY_PLAY_ACTIONID, src.hasKey(PROP_SRC_VOD_UUID) ? src.getString(PROP_SRC_ALIVE_ACTIONID) : "A2016062700000gx");
                bundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, src.hasKey(PROP_SRC_ALIVE_IS_USEHLS) && src.getBoolean(PROP_SRC_ALIVE_IS_USEHLS));
                bundle.putString(PlayerParams.KEY_PLAY_CUSTOMERID, src.hasKey(PROP_SRC_ALIVE_CUSTOMERID) ? src.getString(PROP_SRC_ALIVE_CUSTOMERID) : "838389");
                bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, src.hasKey(PROP_SRC_ALIVE_BUSINESSLINE) ? src.getString(PROP_SRC_ALIVE_BUSINESSLINE) : "102");
                bundle.putString(PlayerParams.KEY_ACTION_CUID, src.hasKey(PROP_SRC_ALIVE_CUID) ? src.getString(PROP_SRC_ALIVE_CUID) : "");
                bundle.putString(PlayerParams.KEY_ACTION_UTOKEN, src.hasKey(PROP_SRC_ALIVE_UTIOKEN) ? src.getString(PROP_SRC_ALIVE_UTIOKEN) : "");
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                videoView.setDataSource(bundle);
                break;

            case PlayerParams.VALUE_PLAYER_MOBILE_LIVE:
                break;

            default:
                //未知播放类型则为URI
                bundle = new Bundle();
                bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
                bundle.putString("path", src.hasKey(PROP_URI) ? src.getString(PROP_URI) :"http://cache.utovr.com/201601131107187320.mp4");
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));
                videoView.setDataSource(bundle);
                break;
        }

    }


    @ReactProp(name = PROP_PAUSED, defaultBoolean = false)
    public void setPaused(final ReactCPVodVideoView videoView, final boolean paused) {
        videoView.setPausedModifier(paused);
    }


    @ReactProp(name = PROP_SEEK)
    public void setSeek(final ReactCPVodVideoView videoView, final float seek) {
        videoView.seekTo(seek);
    }

    @ReactProp(name = PROP_RATE)
    public void setRate(final ReactCPVodVideoView videoView, final String rate) {
        videoView.setRate(rate);
    }

    /**
     * 调节音量
     *
     * @param videoView the video view
     * @param volume    the volume
     */
    @ReactProp(name = PROP_VOLUME)
    public void setVolume(final ReactCPVodVideoView videoView, final int volume) {
        videoView.setVolume(volume);
    }

    /**
     * 调节亮度
     *
     * @param videoView  the video view
     * @param brightness the brightness
     */
    @ReactProp(name = PROP_BRIGHTNESS)
    public void setBrightness(final ReactCPVodVideoView videoView, final int brightness) {
        videoView.setScreenBrightness((Activity) mReactContext.getBaseContext(), brightness);
    }

}
