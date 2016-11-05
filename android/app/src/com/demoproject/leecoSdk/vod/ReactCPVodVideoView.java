package com.demoproject.leecoSdk.vod;

import android.content.Context;

import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;
import com.letv.android.client.skin.videoview.vod.UIVodVideoView;

/**
 * Created by gaolinhua on 16/6/15.
 */
public class ReactCPVodVideoView extends UIVodVideoView {
    public ReactCPVodVideoView(Context context) {
        super(context);
    }

    @Override
    protected void initPlayer() {
        player = new CPVodPlayer(context);
    }
}
