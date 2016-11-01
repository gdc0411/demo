package com.letv.android.client.skin.videoview.pano.live;

import com.letv.android.client.cp.sdk.player.live.CPActionLivePlayer;
import com.letv.android.client.sdk.player.IPlayer;

import android.content.Context;

public class UICPPanoActionLiveVideoView extends UIPanoActionLiveVideoView {

	public UICPPanoActionLiveVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void initPlayer() {
        player = new CPActionLivePlayer(context);
    }

    public IPlayer getPlayer() {
        return player;
    }

}
