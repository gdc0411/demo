package com.letv.android.client.skin.videoview.live;

import com.lecloud.skin.videoview.pano.live.UIPanoActionLiveVideoView;
import com.letv.android.client.cp.sdk.player.live.CPActionLivePlayer;

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
}
