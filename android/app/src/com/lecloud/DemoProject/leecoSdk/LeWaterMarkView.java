package com.lecloud.DemoProject.leecoSdk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lecloud.DemoProject.utils.LogUtils;
import com.lecloud.DemoProject.utils.PxUtils;
import com.lecloud.sdk.api.md.entity.action.WaterConfig;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by LizaRao on 2016/12/9.
 */
public class LeWaterMarkView extends SurfaceView {

    private static final int FIX_WATER_MARK_WIDTH = 48;
    private static final int FIX_WATER_MARK_HEIGHT = 32;
    private static final int FIX_WATER_MARK_MARGIN = 22;

    private SurfaceHolder mSurfaceHolder;
    private List<WaterConfig> mWaterMarks;

    private int mContainerWidth;
    private int mContainerHeight;
    private int mWaterMarkWidth;
    private int mWaterMarkHeight;
    private int mMargin;

    public LeWaterMarkView(Context context) {
        super(context);
        init(context);
    }

    public LeWaterMarkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LeWaterMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        mSurfaceHolder = this.getHolder();

        mWaterMarkWidth = PxUtils.dip2px(context, FIX_WATER_MARK_WIDTH);
        mWaterMarkHeight = PxUtils.dip2px(context, FIX_WATER_MARK_HEIGHT);
        mMargin = PxUtils.dip2px(context, FIX_WATER_MARK_MARGIN);
    }

    public void setContainerWidth(int mContainerWidth) {
        this.mContainerWidth = mContainerWidth;// PxUtils.dip2px(mContext, mContainerWidth);
    }

    public void setContainerHeight(int mContainerHeight) {
        this.mContainerHeight = mContainerHeight;//PxUtils.dip2px(mContext, mContainerHeight);
    }


    private void loadImage(String url, final int pos) {
        new AsyncTask<String, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                if (params == null || params.length == 0) {
                    return null;
                }
                BitmapFactory.Options options = getHttpBitmapOption(params[0]);
                if (options != null)
                    return getHttpBitmap(params[0], options, mWaterMarkWidth, mWaterMarkHeight);

                return null;
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
            clearWaterMarks();
        }
        mWaterMarks = marks;
    }

    public void showWaterMarks() {
        if (mWaterMarks != null) {
            for (WaterConfig waterConfig : mWaterMarks) {
                int pos = 1;
                try {
                    pos = Integer.parseInt(waterConfig.getPos());
                } catch (NumberFormatException e) {
                }
                loadImage(waterConfig.getPicUrl(), pos);
            }
        }
    }

    private void clearWaterMarks() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void drawWaterMark(Bitmap bmp, int pos) {
        if (!mSurfaceHolder.getSurface().isValid())
            return;

        Canvas canvas = mSurfaceHolder.lockCanvas();

        Matrix matrix = new Matrix();
        float scaleWidth = mWaterMarkWidth / (float) bmp.getWidth();
        float scaleHeight = mWaterMarkHeight / (float) bmp.getHeight();
        float scale = (scaleWidth < scaleHeight) ? scaleWidth : scaleHeight;
        matrix.setScale(scale, scale);

        switch (pos) {
            case 1: //左上角
                matrix.postTranslate(mMargin, mMargin);
                break;
            case 2: //右上角
                matrix.postTranslate(mContainerWidth - mWaterMarkWidth - mMargin, mMargin);
                break;
            case 3: //左下角
                matrix.postTranslate(mMargin, mContainerHeight - mWaterMarkHeight - mMargin);
                break;
            case 4: //右下角
                matrix.postTranslate(mContainerWidth - mWaterMarkWidth - mMargin, mContainerHeight - mWaterMarkHeight - mMargin);
                break;
        }

        canvas.drawBitmap(bmp, matrix, null);

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

    private static synchronized BitmapFactory.Options getHttpBitmapOption(String url) {
        BitmapFactory.Options options = null;
        Bitmap bitmap;
        try {
            URL myBitmapUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myBitmapUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            InputStream is = conn.getInputStream();
            BitmapFactory.decodeStream(is, null, options);
//            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return options;
    }

    private static synchronized Bitmap getHttpBitmap(String url, BitmapFactory.Options originOptions,
                                                     int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        try {
            URL myBitmapUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myBitmapUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
//            bitmap = BitmapFactory.decodeStream(is);
            bitmap = decodeSampledBitmapFromStream(is, originOptions, reqWidth, reqHeight);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    private static synchronized Bitmap decodeSampledBitmapFromStream(InputStream in, BitmapFactory.Options originOptions,
                                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();

        // Calculate inSampleSize

        options.inSampleSize = calculateInSampleSize(originOptions, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(in, null, options);
    }


    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        //先根据宽度进行缩小
        while (width / inSampleSize > reqWidth) {
            inSampleSize++;
        }
        //然后根据高度进行缩小
        while (height / inSampleSize > reqHeight) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private static synchronized Bitmap readBitmap(Resources r, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = r.openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
