package com.demoproject;

import android.content.Context;

import com.letv.android.client.sdk.player.IPlayer;
import com.letv.android.client.sdk.videoview.base.BaseMediaDataVideoView;

/**
 * Created by raojia on 2016/11/1.
 */

public class LeBaseMediaDataVideoView extends BaseMediaDataVideoView {
    public LeBaseMediaDataVideoView(Context context) {
        super(context);
    }

    public IPlayer getPlayer() {
        return player;
    }

}
