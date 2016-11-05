package com.demoproject.leecoSdk.vod;

import android.content.Context;

import com.facebook.react.uimanager.ThemedReactContext;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;



/**
 * Created by gaolinhua on 16/6/15.
 */
public class ReactCPVodVideoView extends ReactVodVideoView {
    public ReactCPVodVideoView(ThemedReactContext context) {
        super(context);
    }

    @Override
    protected void initPlayer() {
        player = new CPVodPlayer(context);
    }

}
