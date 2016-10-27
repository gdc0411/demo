package com.letv.android.client.skin.videoview.pano.live;

import com.letv.android.client.cp.sdk.player.live.CPLivePlayer;

import android.content.Context;

public class UICPPanoLiveVideoView extends UIPanoLiveVideoView {

	public UICPPanoLiveVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void initPlayer() {
        player = new CPLivePlayer(context);
    }
}
