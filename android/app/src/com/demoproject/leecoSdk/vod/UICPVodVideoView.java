package com.demoproject.leecoSdk.vod;

import android.content.Context;

import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;
import com.letv.android.client.sdk.player.IPlayer;

/**
 * Created by gaolinhua on 16/6/15.
 */
public class UICPVodVideoView  extends UIVodVideoView {
    public UICPVodVideoView(Context context) {
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
