package com.lecloud.skin.videoview.pano.vod;


import android.content.Context;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.skin.ui.base.BaseChangeModeBtn;
import com.lecloud.skin.ui.impl.LetvVodUICon;
import com.lecloud.skin.videoview.pano.base.BasePanoSurfaceView;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.letv.pano.ISurfaceListener;
import com.letv.pano.OnPanoViewTapUpListener;

/**
 * Created by heyuekuai on 16/5/27.
 */
public class UIPanoVodVideoView extends UIVodVideoView {
    ISurfaceView surfaceView;
    BasePanoSurfaceView.PanoControllMode panoControllMode;

    public UIPanoVodVideoView(Context context) {
        super(context);
        letvVodUICon.canGesture(false);
    }

    @Override
    protected void prepareVideoSurface() {
        surfaceView = new BasePanoSurfaceView(context);
        if (panoControllMode != null) {
            ((BasePanoSurfaceView) surfaceView).switchControllMode(panoControllMode);
        }
        panoControllMode = ((BasePanoSurfaceView) surfaceView).getPanoMode();
        setVideoView(surfaceView);
        ((BasePanoSurfaceView) surfaceView).registerSurfacelistener(new ISurfaceListener() {
            @Override
            public void setSurface(Surface surface) {
                player.setDisplay(surface);
            }
        });
        ((BasePanoSurfaceView) surfaceView).setTapUpListener(new OnPanoViewTapUpListener() {
            @Override
            public void onSingleTapUp(MotionEvent e) {
                letvVodUICon.performClick();
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


    protected void switchPanoVideoMode(int mode) {
        if (mode == BaseChangeModeBtn.MODE_MOVE) {
            panoControllMode = BasePanoSurfaceView.PanoControllMode.GESTURE_AND_GYRO;
        } else {
            panoControllMode = BasePanoSurfaceView.PanoControllMode.GESTURE;
        }
        ((BasePanoSurfaceView) surfaceView).switchControllMode(panoControllMode);
    }
}
