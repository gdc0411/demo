package com.lecloud.valley.modules;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.DowanloadValleyCenter;
import com.lecloud.valley.utils.LogUtils;
import com.lecloud.valley.utils.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.lecloud.valley.common.Constants.PROP_RATE;
import static com.lecloud.valley.common.Constants.PROP_SRC_IS_PANO;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_BUSINESSLINE;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_EXTRA;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_UUID;
import static com.lecloud.valley.common.Constants.PROP_SRC_VOD_VUID;
import static com.lecloud.valley.common.Constants.REACT_CLASS_DOWNLOAD_MODULE;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by raojia on 2017/1/23.
 */

public class DownloadModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static int maxThreadCount = 5;

    private final static int EVENT_TYPE_SUCCESS = 0; //下载完成
    private final static int EVENT_TYPE_STOP = 1; //下载停止
    private final static int EVENT_TYPE_START = 3; //下载开始
    private final static int EVENT_TYPE_PROGRESS = 4; //下载进行中
    private final static int EVENT_TYPE_FAILED = 5; //下载失败
    private final static int EVENT_TYPE_CANCEL = 6; //下载取消
    private final static int EVENT_TYPE_INIT = 7; //初始化完成
    private final static int EVENT_TYPE_WAIT = 8; //已获得url，等待下载
    private final static int EVENT_TYPE_RATEINFO = 9; //得到码率


    private RCTNativeAppEventEmitter mEventEmitter;
    private ReactApplicationContext mReactContext;

    private DowanloadValleyCenter mDownloadCenter;
    private LeDownloadObserver mDownloadObserver;
    private List<LeDownloadInfo> mDownloadInfos;

    private static final Object syncObject = new Object();

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
        constants.put("EVENT_TYPE_SUCCESS", EVENT_TYPE_SUCCESS);
        constants.put("EVENT_TYPE_START", EVENT_TYPE_START);
        constants.put("EVENT_TYPE_PROGRESS", EVENT_TYPE_PROGRESS);
        constants.put("EVENT_TYPE_FAILED", EVENT_TYPE_FAILED);

        constants.put("EVENT_DOWNLOAD_LIST_UPDATE", Events.EVENT_DOWNLOAD_LIST_UPDATE.toString());
        constants.put("DOWLOAD_STATE_WAITING", LeDownloadObserver.DOWLOAD_STATE_WAITING);
        constants.put("DOWLOAD_STATE_DOWNLOADING", LeDownloadObserver.DOWLOAD_STATE_DOWNLOADING);
        constants.put("DOWLOAD_STATE_STOP", LeDownloadObserver.DOWLOAD_STATE_STOP);
        constants.put("DOWLOAD_STATE_SUCCESS", LeDownloadObserver.DOWLOAD_STATE_SUCCESS);
        constants.put("DOWLOAD_STATE_FAILED", LeDownloadObserver.DOWLOAD_STATE_FAILED);
        constants.put("DOWLOAD_STATE_NO_DISPATCH", LeDownloadObserver.DOWLOAD_STATE_NO_DISPATCH);
        constants.put("DOWLOAD_STATE_NO_PERMISSION", LeDownloadObserver.DOWLOAD_STATE_NO_PERMISSION);
        constants.put("DOWLOAD_STATE_URL_REQUEST_FAILED", LeDownloadObserver.DOWLOAD_STATE_URL_REQUEST_FAILED);
        constants.put("DOWLOAD_STATE_DISPATCHING", LeDownloadObserver.DOWLOAD_STATE_DISPATCHING);
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
                    notifyItemEvent(EVENT_TYPE_SUCCESS, info, null);
                    notifyListEvent();
//                    mDownloadCenter.cancelDownload(info,true);
                }

                @Override
                public void onDownloadStop(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "停止下载" + info.getFileName());
                    notifyItemEvent(EVENT_TYPE_STOP, info, null);
                    notifyListEvent();
                }

                @Override
                public void onDownloadStart(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "开始下载" + info.getFileName());
                    notifyItemEvent(EVENT_TYPE_START, info, null);
                    notifyListEvent();
                }

                @Override
                public void onDownloadProgress(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载进度更新" + info.getFileName() + ",进度:" + info.getProgress());
                    notifyItemEvent(EVENT_TYPE_PROGRESS, info, null);
                    notifyListEvent();
                }

                @Override
                public void onDownloadFailed(LeDownloadInfo info, String msg) {
                    // Toast.makeText(ctx,""+msg,Toast.LENGTH_SHORT).show();
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载失败" + info.getFileName() + ",错误:" + msg);
                    notifyItemEvent(EVENT_TYPE_FAILED, info, msg);
                    notifyListEvent();
                }

                @Override
                public void onDownloadCancel(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "删除下载" + info.getFileName());
                    notifyItemEvent(EVENT_TYPE_CANCEL, info, null);
                    notifyListEvent();
                }

                @Override
                public void onDownloadInit(LeDownloadInfo info, String msg) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "下载初始化" + info.getFileName() + ",消息:" + msg);
                    notifyItemEvent(EVENT_TYPE_INIT, info, msg);
                    notifyListEvent();
//                    mDownloadCenter.cancelDownload(info,true);
                }

                @Override
                public void onDownloadWait(LeDownloadInfo info) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "等待下载" + info.getFileName());
                    //glh 当请求url成功时，回调这个方法，表示开始等待下载
                    notifyItemEvent(EVENT_TYPE_WAIT, info, null);
                    notifyListEvent();
                }

                @Override
                public void onGetVideoInfoRate(LeDownloadInfo info, List<String> rates) {
                    Log.d(TAG, LogUtils.getTraceInfo() + "获取你当前下载视频的码率(比如标清、高清、原画等等)" + info.getFileName());
                    notifyItemEvent(EVENT_TYPE_RATEINFO, info, null);
                }
            };
            mDownloadCenter.registerDownloadObserver(mDownloadObserver);
        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (mDownloadCenter != null) {
            mDownloadCenter.unregisterDownloadObserver(mDownloadObserver);
            mDownloadObserver = null;
//            mDownloadInfos = null;
        }
        mEventEmitter = null;
        mReactContext.removeLifecycleEventListener(this);
        super.onCatalystInstanceDestroy();
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
            if (src.hasKey(PROP_SRC_VOD_EXTRA))
                info.setString1(StringUtils.convertReadMapToJsonStr(src.getMap(PROP_SRC_VOD_EXTRA)));
            if (src.hasKey(PROP_RATE))
                info.setRateText(src.getString(PROP_RATE));
            mDownloadCenter.downloadVideo(info);
        }
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 下载视频:" + src.toString());
    }

    @ReactMethod
    public void list() {
        if (mDownloadCenter != null)
            notifyListEvent();

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 获取下载列表！");
    }

    @ReactMethod
    public void pause(final ReadableMap src) {
        if (src == null || mDownloadCenter == null || mDownloadInfos == null || !src.hasKey("id"))
            return;

//        LeDownloadInfo[] infoArray = (LeDownloadInfo[]) mDownloadInfos.toArray();
//        synchronized (syncObject) {
        for (LeDownloadInfo info : mDownloadInfos.toArray(new LeDownloadInfo[0]))
            if (info.getId() == src.getInt("id"))
                mDownloadCenter.stopDownload(info);
//        }


        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 暂停下载:" + src.toString());
    }

    @ReactMethod
    public void resume(final ReadableMap src) {
        if (src == null || mDownloadCenter == null || mDownloadInfos == null || !src.hasKey("id"))
            return;

        for (LeDownloadInfo info : mDownloadInfos.toArray(new LeDownloadInfo[0]))
            if (info.getId() == src.getInt("id"))
                mDownloadCenter.resumeDownload(info);

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 恢复下载:" + src.toString());
    }


    @ReactMethod
    public void retry(final ReadableMap src) {
        if (src == null || mDownloadCenter == null || mDownloadInfos == null || !src.hasKey("id"))
            return;

        for (LeDownloadInfo info : mDownloadInfos.toArray(new LeDownloadInfo[0]))
            if (info.getId() == src.getInt("id"))
                mDownloadCenter.retryDownload(info);

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 重试下载:" + src.toString());
    }

    @ReactMethod
    public void delete(final ReadableMap src) {
        if (src == null || mDownloadCenter == null || mDownloadInfos == null || !src.hasKey("id"))
            return;

        for (LeDownloadInfo info : mDownloadInfos.toArray(new LeDownloadInfo[0]))
            if (info.getId() == src.getInt("id"))
                mDownloadCenter.cancelDownload(info, true);

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 删除视频:" + src.toString());
    }

    @ReactMethod
    public void clear() {
        if (mDownloadCenter == null || mDownloadInfos == null)
            return;


        for (LeDownloadInfo info : mDownloadInfos.toArray(new LeDownloadInfo[0]))
            mDownloadCenter.cancelDownload(info, true);

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 删除全部视频:");
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
        eventPara.putInt("state", info.getState() != null ? info.getState().value() : -1);
        eventPara.putString(PROP_SRC_VOD_UUID, info.getUu());
        eventPara.putString(PROP_SRC_VOD_VUID, info.getVu());
        eventPara.putString(PROP_SRC_VOD_BUSINESSLINE, info.getP());
        eventPara.putString("payCheckCode", info.getCheckCode());
        eventPara.putString("payUserName", info.getPayerName());
        eventPara.putMap(PROP_SRC_VOD_EXTRA, StringUtils.convertJsonStrToWriteMap(info.getString1()));
        eventPara.putString("msg", msg);

        if (mReactContext.hasActiveCatalystInstance()) {
            if (mEventEmitter != null)
                mEventEmitter.emit(Events.EVENT_DOWNLOAD_ITEM_UPDATE.toString(), eventPara);
        } else {
            Log.i(TAG, "not hasActiveCatalystInstance");
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "下载事件——— Item更新事件 event:" + eventType + " info:" + info.getFileName());

    }

    private void notifyListEvent() {

        mDownloadInfos = mDownloadCenter.getDownloadInfoList();

        WritableArray eventList = Arguments.createArray();

        if (mDownloadInfos != null) {
            for (LeDownloadInfo info : mDownloadInfos) {
                WritableMap eventPara = Arguments.createMap();
                eventPara.putInt("id", (int) info.getId());
                eventPara.putString("fileName", info.getFileName());
                eventPara.putString("fileSavePath", info.getFileSavePath());
                eventPara.putDouble("progress", info.getProgress());
                eventPara.putDouble("fileLength", info.getFileLength());
                eventPara.putString("rateText", info.getRateText());
                eventPara.putInt(PROP_SRC_IS_PANO, info.getIsPano());
                eventPara.putInt("downloadState", info.getDownloadState());
                eventPara.putInt("state", info.getState() != null ? info.getState().value() : -1);
                eventPara.putString(PROP_SRC_VOD_UUID, info.getUu());
                eventPara.putString(PROP_SRC_VOD_VUID, info.getVu());
                eventPara.putString(PROP_SRC_VOD_BUSINESSLINE, info.getP());
                eventPara.putString("payCheckCode", info.getCheckCode());
                eventPara.putString("payUserName", info.getPayerName());
                eventPara.putMap(PROP_SRC_VOD_EXTRA, StringUtils.convertJsonStrToWriteMap(info.getString1()));

                eventList.pushMap(eventPara);
            }
        }

        if (mReactContext.hasActiveCatalystInstance()) {
            if (mEventEmitter != null)
                mEventEmitter.emit(Events.EVENT_DOWNLOAD_LIST_UPDATE.toString(), eventList);
        } else {
            Log.i(TAG, "not hasActiveCatalystInstance");
        }
        Log.d(TAG, LogUtils.getTraceInfo() + "下载事件——— List更新事件 :" + mDownloadInfos.size());
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
