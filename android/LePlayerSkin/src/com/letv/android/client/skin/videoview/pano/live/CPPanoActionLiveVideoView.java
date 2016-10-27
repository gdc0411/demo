package com.letv.android.client.skin.videoview.pano.live;

import com.letv.android.client.cp.sdk.player.live.CPActionLivePlayer;

import android.content.Context;

public class CPPanoActionLiveVideoView extends PanoActionLiveVideoView {

	public CPPanoActionLiveVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void initPlayer() {
        player = new CPActionLivePlayer(context);
    }
}
