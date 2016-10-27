package com.letv.android.client.skin.videoview.pano.vod;

import android.content.Context;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.letv.android.client.sdk.surfaceview.ISurfaceView;
import com.letv.android.client.skin.videoview.vod.UICPVodVideoView;
import com.letv.android.client.skin.ui.impl.LetvVodUICon;
import com.letv.android.client.skin.videoview.pano.base.BasePanoSurfaceView;
import com.letv.pano.IPanoListener;


public class UICPPanoVodVideoView extends UICPVodVideoView {
    ISurfaceView surfaceView;

    protected int interactiveMode = -1;
    protected int displayMode = -1;

    public UICPPanoVodVideoView(Context context) {
        super(context);
        letvVodUICon.canGesture(false);
    }

    @Override
    protected void prepareVideoSurface() {
        surfaceView = new BasePanoSurfaceView(context);
        interactiveMode = ((BasePanoSurfaceView) surfaceView).switchControllMode(interactiveMode);
        displayMode = ((BasePanoSurfaceView) surfaceView).switchDisplayMode(displayMode);
        setVideoView(surfaceView);

        ((BasePanoSurfaceView) surfaceView).registerPanolistener(new IPanoListener() {
            @Override
            public void setSurface(Surface surface) {
                player.setDisplay(surface);
            }
            @Override
            public void onSingleTapUp(MotionEvent e) {
                letvVodUICon.performClick();
            }

            @Override
            public void onNotSupport(int mode) {
                Toast.makeText(context, "not support current mode " + mode, Toast.LENGTH_LONG).show();
            }
        });

        ((LetvVodUICon) letvVodUICon).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((BasePanoSurfaceView) surfaceView).onPanoTouch(v, event);
                return true;
            }
        });
        letvVodUICon.isPano(true);
    }

    @Override
    protected int switchControllMode(int mode) {
        return ((BasePanoSurfaceView) surfaceView).switchControllMode(mode);
    }

    @Override
    protected int switchDisplayMode(int displayMode) {
        return ((BasePanoSurfaceView) surfaceView).switchDisplayMode(displayMode);
    }
}
