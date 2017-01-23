package com.lecloud.valley.utils;

import android.content.Context;
import android.os.Bundle;

import com.lecloud.sdk.api.md.IVodMediaData;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.download.control.BaseDownloadCenter;
import com.lecloud.sdk.download.control.LeDownloadService;
import com.lecloud.sdk.download.info.LeDownloadInfo;

/**
 * Created by raojia on 2017/1/23.
 */

public class DownloadSaasCenter extends BaseDownloadCenter {
    private static DownloadSaasCenter sInstance;

    private DownloadSaasCenter(Context context) {
        mContext = context;
        mDownloadMgr = LeDownloadService.getDownloadManager(context);
        allowShowMsg(true);
    }

    public static synchronized DownloadSaasCenter getInstances(Context context) {
        if (sInstance == null) {
            sInstance = new DownloadSaasCenter(context);
        }
        return sInstance;
    }

    @Override
    protected void dispatchWorker(LeDownloadInfo info) {
        super.dispatchWorker(info);

        Bundle bundle = new Bundle();
        bundle.putString(PlayerParams.KEY_PLAY_UUID, info.getUu());
        bundle.putString(PlayerParams.KEY_PLAY_VUID, info.getVu());
        bundle.putString(PlayerParams.KEY_PLAY_USERKEY, info.getUserKey());
        bundle.putString(PlayerParams.KEY_PLAY_CHECK_CODE, info.getCheckCode());
        bundle.putString(PlayerParams.KEY_PLAY_PAYNAME, info.getPayerName());

        IVodMediaData mediaData = new CPVodMediaData(mContext);
        mediaData.setMediaDataParams(bundle);
        mediaData.setMediaDataListener(new BaseDownloadCallback(info));
        mediaData.requestVod();

    }
}
