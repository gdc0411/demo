package com.lecloud.valley.modules;

import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.lecloud.valley.utils.DowanloadValleyCenter;
import com.lecloud.valley.utils.LogUtils;

import java.util.List;

import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_BUSINESSLINE;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_UUID;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_VUID;
import static com.lecloud.valley.common.Constants.REACT_CLASS_DOWNLOAD_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by raojia on 2017/1/23.
 */

public class DownloadModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static int maxThreadCount = 5;

    private RCTNativeAppEventEmitter mEventEmitter;
    private ReactApplicationContext mReactContext;

    private DowanloadValleyCenter mDownloadCenter;
    private LeDownloadObserver mDownloadObserver;
    private List<LeDownloadInfo> mDownloadInfos;

    public DownloadModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS_DOWNLOAD_MODULE;
    }

    @Override
    public void initialize() {
        super.initialize();
        Log.i(TAG, "DOWNLOAD模块初始化");

        mReactContext.addLifecycleEventListener(this);
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (mDownloadCenter == null) {
            mDownloadCenter = DowanloadValleyCenter.getInstances(mReactContext.getBaseContext().getApplicationContext());

            mDownloadCenter.allowShowMsg(false);
            mDownloadCenter.setDownloadSavePath("/sdcard/Android/data/" + mReactContext.getPackageName() + "/levideo/");
            mDownloadCenter.setMaxDownloadThread(maxThreadCount);

            mDownloadObserver = new LeDownloadObserver() {
                @Override
                public void onDownloadSuccess(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载完成" + info.getFileName());
                    notifyData();
//                    mDownloadCenter.cancelDownload(info,true);
                }

                @Override
                public void onDownloadStop(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载暂停" + info.getFileName());
                    notifyData();
                }

                @Override
                public void onDownloadStart(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "开始下载" + info.getFileName());
                    notifyData();
                }

                @Override
                public void onDownloadProgress(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载进度更新" + info.getFileName() + ",进度:" + info.getProgress());
                    notifyData();
                }

                @Override
                public void onDownloadFailed(LeDownloadInfo info, String msg) {
                    // Toast.makeText(ctx,""+msg,Toast.LENGTH_SHORT).show();
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载失败" + info.getFileName() + ",错误:" + msg);
                    notifyData();
                }

                @Override
                public void onDownloadCancel(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "取消下载" + info.getFileName());
                    notifyData();
                }

                @Override
                public void onDownloadInit(LeDownloadInfo info, String msg) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载初始化" + info.getFileName() + ",消息:" + msg);
                    notifyData();
                }

                @Override
                public void onDownloadWait(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "等待下载" + info.getFileName());
                    //glh 当请求url成功时，回调这个方法，表示开始等待下载
                }

                @Override
                public void onGetVideoInfoRate(LeDownloadInfo info, List<String> rates) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "获取你当前下载视频的码率(比如标清、高清、原画等等)" + info.getFileName());
                }
            };
            mDownloadCenter.registerDownloadObserver(mDownloadObserver);
        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (mDownloadInfos != null) {
            mDownloadCenter.unregisterDownloadObserver(mDownloadObserver);
            mDownloadInfos = null;
        }
        mEventEmitter = null;
        mReactContext.removeLifecycleEventListener(this);
        super.onCatalystInstanceDestroy();
    }

    private void notifyData() {
        mDownloadInfos = mDownloadCenter.getDownloadInfoList();
//        mAdapter.setData(mDownloadInfos);
//        mAdapter.notifyDataSetChanged();

        Log.d(TAG, LogUtils.getTraceInfo() + "下载消息——— :" + mDownloadInfos.toString());
    }

    @ReactMethod
    public void download(final ReadableMap src) {

        if (src == null || !src.hasKey(PROP_SRC_VOD_UUID) || !src.hasKey(PROP_SRC_VOD_VUID) || !src.hasKey(PROP_SRC_VOD_BUSINESSLINE)) {
            return;
        }
        if (mDownloadCenter != null) {

//            mDownloadCenter.downloadVideo("", src.getString(PROP_SRC_VOD_UUID), src.getString(PROP_SRC_VOD_VUID));
            LeDownloadInfo info = new LeDownloadInfo();
            info.setUu(src.getString(PROP_SRC_VOD_UUID)); // 必填,否则不能下载视频
            info.setVu(src.getString(PROP_SRC_VOD_VUID)); // 必填，否则不能下载视频
            info.setP(src.getString(PROP_SRC_VOD_BUSINESSLINE)); // 必填，否则不能下载视频

            mDownloadCenter.downloadVideo(info);
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 下载视频:" + src.toString());
    }


    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }
}
