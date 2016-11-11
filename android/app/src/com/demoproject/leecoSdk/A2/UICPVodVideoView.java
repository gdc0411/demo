package com.demoproject.leecoSdk.A2;

import android.content.Context;

import com.lecloud.sdk.player.IPlayer;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;

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
