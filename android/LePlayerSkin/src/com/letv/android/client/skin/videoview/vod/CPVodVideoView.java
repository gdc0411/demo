package com.letv.android.client.skin.videoview.vod;

import android.content.Context;

import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;
import com.letv.android.client.sdk.player.IPlayer;
import com.letv.android.client.sdk.videoview.vod.VodVideoView;

/**
 * Created by administror on 2016/6/13 0013.
 */
public class CPVodVideoView extends VodVideoView {
    public CPVodVideoView(Context context) {
        super(context);
    }

    @Override
    protected void initPlayer() {
        player = new CPVodPlayer(context);
    }

    public IPlayer getPlayer() {
        return player;
    }
}
