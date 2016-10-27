package com.letv.android.client.skin.videoview.pano.vod;

import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;

import android.content.Context;

public class CPPanoVodVideoView extends PanoVodVideoView {

	public CPPanoVodVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void initPlayer() {
        player = new CPVodPlayer(context);
    }
}
