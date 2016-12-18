/*************************************************************************
 * Description: 乐视视频活动播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-18
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.valley.utils.LogUtils;

import static com.lecloud.valley.leecoSdk.Constants.PROP_PLAY_MODE;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_ALIVE_ACTIONID;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_ALIVE_BUSINESSLINE;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_ALIVE_CUID;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_ALIVE_CUSTOMERID;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_ALIVE_IS_USEHLS;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_ALIVE_UTIOKEN;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_HAS_SKIN;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_IS_PANO;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_VOD_BUSINESSLINE;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_VOD_SAAS;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_VOD_UUID;
import static com.lecloud.valley.leecoSdk.Constants.PROP_SRC_VOD_VUID;
import static com.lecloud.valley.leecoSdk.Constants.PROP_URI;
import static com.lecloud.valley.leecoSdk.Constants.REACT_CLASS;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2016/12/18.
 */
public class LeReactSubVideoViewManager extends SimpleViewManager<LeReactSubVideoView> {

    private ThemedReactContext mReactContext;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected LeReactSubVideoView createViewInstance(ThemedReactContext reactContext) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 createViewInstance 调起！");
        mReactContext = reactContext;
        return new LeReactSubVideoView(mReactContext);
    }

    @Override
    public void onDropViewInstance(LeReactSubVideoView subVideoView) {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onDropViewInstance 调起！");
        super.onDropViewInstance(videoView);
    }



    /**
     * 设置数据源，必填（VOD、LIVE）
     *
     * @param src 数据源包
     */
    @ReactProp(name = PROP_SRC)
    public void setDataSource(final LeReactSubVideoView videoView, final ReadableMap src) {
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


}
