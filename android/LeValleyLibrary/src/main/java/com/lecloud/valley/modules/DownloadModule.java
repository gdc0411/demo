package com.lecloud.valley.modules;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.DowanloadValleyCenter;
import com.lecloud.valley.utils.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lecloud.valley.common.Constants.PROP_RATE;
import static com.lecloud.valley.common.Constants.PROP_SRC_IS_PANO;
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

    private final static int SUCCESS = 0; //下载完成
    private final static int STOP = 1; //下载停止
    private final static int START = 3; //下载开始
    private final static int PROGRESS = 4; //下载进行中
    private final static int FAILED = 5; //下载失败
    private final static int CANCEL = 6; //下载取消
    private final static int INIT = 7; //初始化完成
    private final static int WAIT = 8; //已获得url，等待下载
    private final static int RATEINFO = 9; //得到码率


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
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("EVENT_DOWNLOAD_ITEM_UPDATE", Events.EVENT_DOWNLOAD_ITEM_UPDATE.toString());
        constants.put("SUCCESS", SUCCESS);
        constants.put("STOP", STOP);
        constants.put("START", START);
        constants.put("PROGRESS", PROGRESS);
        constants.put("FAILED", FAILED);
        constants.put("CANCEL", CANCEL);
        constants.put("INIT", INIT);
        constants.put("WAIT", WAIT);
        constants.put("RATEINFO", RATEINFO);
        constants.put("EVENT_DOWNLOAD_LIST_UPDATE", Events.EVENT_DOWNLOAD_LIST_UPDATE.toString());
        return constants;
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
                    notifyItemEvent(SUCCESS, info, null);
                    notifyData();
//                    mDownloadCenter.cancelDownload(info,true);
                }

                @Override
                public void onDownloadStop(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "停止下载" + info.getFileName());
                    notifyItemEvent(STOP, info, null);
                    notifyData();
                }

                @Override
                public void onDownloadStart(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "开始下载" + info.getFileName());
                    notifyItemEvent(START, info, null);
                    notifyData();
                }

                @Override
                public void onDownloadProgress(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载进度更新" + info.getFileName() + ",进度:" + info.getProgress());
                    notifyItemEvent(PROGRESS, info, null);
                    notifyData();
                }

                @Override
                public void onDownloadFailed(LeDownloadInfo info, String msg) {
                    // Toast.makeText(ctx,""+msg,Toast.LENGTH_SHORT).show();
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载失败" + info.getFileName() + ",错误:" + msg);
                    notifyItemEvent(FAILED, info, msg);
                    notifyData();
                }

                @Override
                public void onDownloadCancel(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "删除下载" + info.getFileName());
                    notifyItemEvent(CANCEL, info, null);
                    notifyData();
                }

                @Override
                public void onDownloadInit(LeDownloadInfo info, String msg) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载初始化" + info.getFileName() + ",消息:" + msg);
                    notifyItemEvent(INIT, info, msg);
                    notifyData();
                }

                @Override
                public void onDownloadWait(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "等待下载" + info.getFileName());
                    //glh 当请求url成功时，回调这个方法，表示开始等待下载
                    notifyItemEvent(WAIT, info, null);
                }

                @Override
                public void onGetVideoInfoRate(LeDownloadInfo info, List<String> rates) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "获取你当前下载视频的码率(比如标清、高清、原画等等)" + info.getFileName());
                    notifyItemEvent(RATEINFO, info, null);
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
            info.setUu(src.getString(PROP_SRC_VOD_UUID));
            info.setVu(src.getString(PROP_SRC_VOD_VUID));
            info.setP(src.getString(PROP_SRC_VOD_BUSINESSLINE));
            if (src.hasKey(src.getString(PROP_RATE)))
                info.setRateText(src.getString(PROP_RATE));
            mDownloadCenter.downloadVideo(info);
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 下载视频:" + src.toString());
    }


    private void notifyItemEvent(int eventType, LeDownloadInfo info, String msg) {
        if (eventType == -1 || info == null) return;

        WritableMap eventPara = Arguments.createMap();
        eventPara.putInt("eventType", eventType);
        eventPara.putInt("id", (int) info.getId());
        eventPara.putString("fileName", info.getFileName());
        eventPara.putString("fileSavePath", info.getFileSavePath());
        eventPara.putDouble("progress", info.getProgress());
        eventPara.putDouble("fileLength", info.getFileLength());
        eventPara.putString("rateText", info.getRateText());
        eventPara.putInt(PROP_SRC_IS_PANO, info.getIsPano());
        eventPara.putInt("downloadState", info.getDownloadState());
        eventPara.putInt("state", info.getState().value());
        eventPara.putString(PROP_SRC_VOD_UUID, info.getUu());
        eventPara.putString(PROP_SRC_VOD_VUID, info.getVu());
        eventPara.putString(PROP_SRC_VOD_BUSINESSLINE, info.getP());
        eventPara.putString("msg", msg);

        //此处需要添加hasActiveCatalystInstance，否则可能造成崩溃
        //问题解决参考: https://github.com/walmartreact/react-native-orientation-listener/issues/8
        if (mReactContext.hasActiveCatalystInstance()) {
            Log.i(TAG, "hasActiveCatalystInstance");
            if (mEventEmitter != null)
                mEventEmitter.emit(Events.EVENT_DOWNLOAD_ITEM_UPDATE.toString(), eventPara);
        } else {
            Log.i(TAG, "not hasActiveCatalystInstance");
        }
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
