package com.demoproject.leecoSdk.vod;

import android.content.Context;

import com.facebook.react.uimanager.ThemedReactContext;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;

/**
 * Created by raojia on 16/11/07.
 */
public class ReactCPVodVideoView extends ReactVodVideoView {
    public ReactCPVodVideoView(Context context) {
        super(context);
    }

    @Override
    protected void initPlayer() {
        player = new CPVodPlayer(context);
    }

}
