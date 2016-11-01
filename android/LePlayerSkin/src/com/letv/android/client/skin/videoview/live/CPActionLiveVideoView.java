package com.letv.android.client.skin.videoview.live;

import com.letv.android.client.cp.sdk.player.live.CPActionLivePlayer;
import com.letv.android.client.sdk.player.IPlayer;
import com.letv.android.client.sdk.videoview.live.ActionLiveVideoView;

import android.content.Context;

public class CPActionLiveVideoView extends ActionLiveVideoView{

	public CPActionLiveVideoView(Context context) {
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
