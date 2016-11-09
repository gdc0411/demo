package com.demoproject.leecoSdk;

import android.content.Context;

import com.lecloud.sdk.player.IPlayer;
import com.lecloud.sdk.videoview.base.BaseMediaDataVideoView;


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
