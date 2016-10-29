package com.demoproject;

import android.os.Bundle;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.letv.android.client.sdk.constant.PlayerParams;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by LizaRao on 2016/21/10.
 */
public class LeVideoViewManager extends SimpleViewManager<LeVideoView> {

    // 组件名
    public static final String REACT_CLASS = "RCTLeVideoView";


    //播放器模式
    public static final String PROP_PLAY_MODE = PlayerParams.KEY_PLAY_MODE;

    //URI数据源：本地或者在线
    public static final String PROP_URI = "uri";

    //复杂数据源
    public static final String PROP_SRC = "src";

    //点播模式
    public static final String PROP_SRC_VOD_UUID = PlayerParams.KEY_PLAY_UUID;
    public static final String PROP_SRC_VOD_VUID = PlayerParams.KEY_PLAY_VUID;
    public static final String PROP_SRC_VOD_BUSINESSLINE = PlayerParams.KEY_PLAY_BUSINESSLINE;
    public static final String PROP_SRC_VOD_SAAS = "saas";

    //活动直播模式
    public static final String PROP_SRC_ALIVE_ACTIONID = PlayerParams.KEY_PLAY_ACTIONID;
    public static final String PROP_SRC_ALIVE_CUSTOMERID = PlayerParams.KEY_PLAY_CUSTOMERID;
    public static final String PROP_SRC_ALIVE_BUSINESSLINE = PlayerParams.KEY_PLAY_BUSINESSLINE;
    public static final String PROP_SRC_ALIVE_CUID = PlayerParams.KEY_ACTION_CUID;
    public static final String PROP_SRC_ALIVE_UTIOKEN = PlayerParams.KEY_ACTION_UTOKEN;
    public static final String PROP_SRC_ALIVE_IS_USEHLS = PlayerParams.KEY_PLAY_USEHLS;
    //是否全景
    public static final String PROP_SRC_IS_PANO = "isPano";
    //是否有皮肤
    public static final String PROP_SRC_HAS_SKIN = "hasSkin";

    // 暂停方法
    public static final String PROP_PAUSED = "paused";
    // 快进方法
    public static final String PROP_SEEK = "seek";


    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected LeVideoView createViewInstance(ThemedReactContext reactContext) {
        return new LeVideoView(reactContext);
    }

    @Override
    public void onDropViewInstance(LeVideoView videoView) {
        super.onDropViewInstance(videoView);
        videoView.cleanupMediaPlayerResources();
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (LeVideoView.Events event : LeVideoView.Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

//    @Override
//    @Nullable
//    public Map getExportedViewConstants() {
//        return MapBuilder.of(
//                "ScaleNone", Integer.toString(ScalableType.LEFT_TOP.ordinal()),
//                "ScaleToFill", Integer.toString(ScalableType.FIT_XY.ordinal()),
//                "ScaleAspectFit", Integer.toString(ScalableType.FIT_CENTER.ordinal()),
//                "ScaleAspectFill", Integer.toString(ScalableType.CENTER_CROP.ordinal())
//        );
//    }

//    @ReactProp(name = PROP_PLAY_MODE)
//    public void setDataPlayMode(final LeVideoView videoView, int playMode) {
//        videoView.setPlayMode(playMode);
//    }


    @ReactProp(name = PROP_URI)
    public void setDataSource(final LeVideoView videoView, @Nullable String uri) {
        if (uri != null && !uri.trim().equals(""))
            videoView.setDataSource(uri);
    }

    @ReactProp(name = PROP_SRC)
    public void setDataSource(final LeVideoView videoView, @Nullable ReadableMap src) {
        if (src == null) {
            return;
        }
        int playMode = src.hasKey(PROP_PLAY_MODE) ? src.getInt(PROP_PLAY_MODE) : -1;
        Bundle bundle;
        switch (playMode) {
            case PlayerParams.VALUE_PLAYER_VOD:
                bundle = new Bundle();
                bundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
                bundle.putString(PlayerParams.KEY_PLAY_UUID, src.hasKey(PROP_SRC_VOD_UUID) ? src.getString(PROP_SRC_VOD_UUID):"");
                bundle.putString(PlayerParams.KEY_PLAY_VUID, src.hasKey(PROP_SRC_VOD_UUID) ?src.getString(PROP_SRC_VOD_VUID):"");
                bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, src.hasKey(PROP_SRC_VOD_UUID) ?src.getString(PROP_SRC_VOD_BUSINESSLINE):"102");
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
                bundle.putString(PlayerParams.KEY_PLAY_ACTIONID,  src.hasKey(PROP_SRC_VOD_UUID) ?src.getString(PROP_SRC_ALIVE_ACTIONID):"A2016062700000gx");
                bundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, src.hasKey(PROP_SRC_ALIVE_IS_USEHLS) && src.getBoolean(PROP_SRC_ALIVE_IS_USEHLS));
                bundle.putString(PlayerParams.KEY_PLAY_CUSTOMERID,  src.hasKey(PROP_SRC_ALIVE_CUSTOMERID) ?src.getString(PROP_SRC_ALIVE_CUSTOMERID):"838389");
                bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE,  src.hasKey(PROP_SRC_ALIVE_BUSINESSLINE) ?src.getString(PROP_SRC_ALIVE_BUSINESSLINE):"102");
                bundle.putString(PlayerParams.KEY_ACTION_CUID,  src.hasKey(PROP_SRC_ALIVE_CUID) ?src.getString(PROP_SRC_ALIVE_CUID):"");
                bundle.putString(PlayerParams.KEY_ACTION_UTOKEN,  src.hasKey(PROP_SRC_ALIVE_UTIOKEN) ?src.getString(PROP_SRC_ALIVE_UTIOKEN):"");
                bundle.putBoolean("pano", src.hasKey(PROP_SRC_IS_PANO) && src.getBoolean(PROP_SRC_IS_PANO));
                bundle.putBoolean("hasSkin", src.hasKey(PROP_SRC_HAS_SKIN) && src.getBoolean(PROP_SRC_HAS_SKIN));

                videoView.setDataSource(bundle);
                break;

            case PlayerParams.VALUE_PLAYER_MOBILE_LIVE:
                break;
            default:
                //未知播放类型则为URL
                break;
        }

    }

    @ReactProp(name = PROP_PAUSED, defaultBoolean = false)
    public void setPaused(final LeVideoView videoView, final boolean paused) {
        videoView.setPausedModifier(paused);
    }

    @ReactProp(name = PROP_SEEK)
    public void setSeek(final LeVideoView videoView, final float seek) {
        videoView.seekTo(Math.round(seek * 1000.0f));
    }

}
