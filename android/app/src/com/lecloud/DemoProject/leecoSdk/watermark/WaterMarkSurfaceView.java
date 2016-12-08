package com.lecloud.DemoProject.leecoSdk.watermark;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lecloud.sdk.api.md.entity.action.WaterConfig;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by LizaRao on 2016/12/9.
 */
public class WaterMarkSurfaceView extends SurfaceView {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private List<WaterConfig> mWaterMarks;

    public WaterMarkSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public WaterMarkSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public WaterMarkSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        mContext = context;
        mSurfaceHolder = this.getHolder();
    }


    private void loadImage(String url, final int pos) {
        new AsyncTask<String, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                if (params == null || params.length == 0) {
                    return null;
                }
                return getHttpBitmap(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    drawWaterMark(bitmap, pos);
                }
            }
        }.execute(url);
    }


    public void setWaterMarks(List<WaterConfig> marks) {
        if (mWaterMarks != null) {
            mWaterMarks = null;
        }

        mWaterMarks = marks;
        for (WaterConfig waterConfig : marks) {
            int pos = 1;
            try {
                pos = Integer.parseInt(waterConfig.getPos());
            } catch (NumberFormatException e) {
            }
            loadImage(waterConfig.getPicUrl(), pos);
        }
    }



    private void drawWaterMark(Bitmap bmp, int pos) {

        Canvas canvas = mSurfaceHolder.lockCanvas();
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Matrix matrix = new Matrix();
        matrix.setScale(0.15f, 0.15f);
        matrix.postTranslate(100, 0);
        canvas.drawBitmap(bmp, bmp.getWidth(), bmp.getHeight(), new Paint());

        mSurfaceHolder.unlockCanvasAndPost(canvas);

//        //定义画笔
//        Paint mpaint = new Paint();
//        mpaint.setColor(Color.BLUE);
//        // mpaint.setAntiAlias(true);//去锯齿
//        mpaint.setStyle(Paint.Style.STROKE);//空心
//        // 设置paint的外框宽度
//        mpaint.setStrokeWidth(2f);
//
//        Canvas canvas = mSurfaceHolder.lockCanvas();
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清除上一次的画框
//        Rect r = new Rect(0, 0, 100, 100);
//        canvas.drawRect(r, mpaint);
//        mSurfaceHolder.unlockCanvasAndPost(canvas);

        bmp.recycle();
    }

    private static Bitmap getHttpBitmap(String url) {
        Bitmap bitmap = null;
        try {
            URL myBitmapUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myBitmapUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap readBitmap(Resources r, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = r.openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
