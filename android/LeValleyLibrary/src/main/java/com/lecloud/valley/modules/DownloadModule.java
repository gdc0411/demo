package com.lecloud.valley.modules;

import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.sdk.download.control.DownloadCenter;
import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.lecloud.valley.utils.DownloadSaasCenter;
import com.lecloud.valley.utils.LogUtils;

import java.util.List;

import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_UUID;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_VUID;
import static com.lecloud.valley.common.Constants.REACT_CLASS_DOWNLOAD_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by raojia on 2017/1/23.
 */

public class DownloadModule extends ReactContextBaseJavaModule {

    private RCTNativeAppEventEmitter mEventEmitter;
    private ReactApplicationContext mReactContext;

    private List<LeDownloadInfo> mDownloadInfos;

    private DownloadSaasCenter mDownloadCenter;

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

        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        mDownloadCenter = DownloadSaasCenter.getInstances(mReactContext.getBaseContext().getApplicationContext());

        mDownloadCenter.registerDownloadObserver(new LeDownloadObserver() {
            @Override
            public void onDownloadSuccess(LeDownloadInfo info) {
                Log.e(TAG, "onDownloadSuccess" + info.getFileName());
                notifyData();
            }

            @Override
            public void onDownloadStop(LeDownloadInfo info) {
                Log.e(TAG, "onDownloadStop" + info.getFileName());
                notifyData();
            }

            @Override
            public void onDownloadStart(LeDownloadInfo info) {
                Log.e(TAG, "onDownloadStart" + info.getFileName());
                notifyData();
            }

            @Override
            public void onDownloadProgress(LeDownloadInfo info) {
                Log.e(TAG, "onDownloadProgress" + info.getFileName() + ",progress:" + info.getProgress());
                notifyData();
            }

            @Override
            public void onDownloadFailed(LeDownloadInfo info, String msg) {
                // Toast.makeText(ctx,""+msg,Toast.LENGTH_SHORT).show();
                notifyData();
            }

            @Override
            public void onDownloadCancel(LeDownloadInfo info) {
                notifyData();
            }


            @Override
            public void onDownloadInit(LeDownloadInfo info, String msg) {
                notifyData();
            }

            @Override
            public void onDownloadWait(LeDownloadInfo info) {
                // TODO Auto-generated method stub
                //glh 当请求url成功时，回调这个方法，表示开始等待下载
            }

            @Override
            public void onGetVideoInfoRate(LeDownloadInfo info, List<String> rates) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void notifyData() {
        mDownloadInfos = mDownloadCenter.getDownloadInfoList();
//        mAdapter.setData(mDownloadInfos);
//        mAdapter.notifyDataSetChanged();

        Log.d(TAG, LogUtils.getTraceInfo() + "下载进度——— :" + mDownloadInfos.toString());
    }

    @ReactMethod
    public void download(final ReadableMap src) {

        if (src == null || !src.hasKey(PROP_SRC_VOD_UUID) || !src.hasKey(PROP_SRC_VOD_VUID) ) {
            return;
        }

        if(mDownloadCenter != null ) {
//            mDownloadCenter.downloadVideo("", src.getString(PROP_SRC_VOD_UUID), src.getString(PROP_SRC_VOD_VUID));
            LeDownloadInfo info = new LeDownloadInfo();
            info.setUu(src.getString(PROP_SRC_VOD_UUID)); // 必填,否则不能下载视频
            info.setVu(src.getString(PROP_SRC_VOD_VUID)); // 必填，否则不能下载视频
            mDownloadCenter.downloadVideo(info);
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 下载视频:" + src.toString());
    }

}
