package com.lecloud.valley.utils;

import android.content.Context;
import android.os.Bundle;

import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.download.control.BaseDownloadCenter;
import com.lecloud.sdk.download.control.LeDownloadService;
import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.letv.android.client.cp.sdk.api.md.impl.CPVodMediaData;

/**
 * Created by raojia on 2017/1/24.
 */

public class DowanloadCenterUtils extends BaseDownloadCenter {

    private static DowanloadCenterUtils sInstance;

    private DowanloadCenterUtils(Context context) {
        this.mContext = context;
        this.mDownloadMgr = LeDownloadService.getDownloadManager(context);
        this.allowShowMsg(true);
    }

    public static synchronized DowanloadCenterUtils getInstances(Context context) {
        if(sInstance == null) {
            sInstance = new DowanloadCenterUtils(context);
        }

        return sInstance;
    }

    protected void dispatchWorker(LeDownloadInfo info) {
        super.dispatchWorker(info);
        Bundle bundle = new Bundle();
        bundle.putString(PlayerParams.KEY_PLAY_UUID, info.getUu());
        bundle.putString(PlayerParams.KEY_PLAY_VUID, info.getVu());
        bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, info.getP());
//        bundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, "102");
//        bundle.putString("userkey", info.getUserKey());
//        bundle.putString("checkCode", info.getCheckCode());
//        bundle.putString("checkCode", info.getCheckCode());
//        bundle.putString("payname", info.getPayerName());
        CPVodMediaData mediaData = new CPVodMediaData(this.mContext);
        mediaData.setMediaDataParams(bundle);
        mediaData.setMediaDataListener(new BaseDownloadCallback(info));
        mediaData.requestVod();
    }

}
