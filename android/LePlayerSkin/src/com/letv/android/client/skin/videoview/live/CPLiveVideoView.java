package com.letv.android.client.skin.videoview.live;

import com.letv.android.client.cp.sdk.player.live.CPLivePlayer;
import com.letv.android.client.sdk.videoview.live.LiveVideoView;

import android.content.Context;

public class CPLiveVideoView extends LiveVideoView{

	public CPLiveVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void initPlayer() {
        player = new CPLivePlayer(context);
    }
}
