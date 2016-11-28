package com.lecloud.DemoProject.leecoSdk.A2;

import android.content.Context;

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
