/*************************************************************************
 * Description: 微信API组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-25
 * Modified Time: 2016-12-25
 ************************************************************************/
'use strict';

import { NativeModules, NativeAppEventEmitter } from 'react-native';

const QQAPI = NativeModules.QQModule;

function translateError(err, result) {
    if (!err) {
        return this.resolve(result);
    }
    if (typeof err === 'object') {
        if (err instanceof Error) {
            return this.reject(ret);
        }
        return this.reject(Object.assign(new Error(err.message), { errCode: err.errCode }));
    } else if (typeof err === 'string') {
        return this.reject(new Error(err));
    }
    this.reject(Object.assign(new Error(), { origin: err }));
}

export const getApiVersion = QQAPI.getApiVersion;
export const isInstalled = QQAPI.isInstalled;
export const isSupportApi = QQAPI.isSupportApi;

// export const isQQSupportApi = QQAPI.isQQSupportApi;

// Save callback and wait for future event.
let savedCallback = undefined;
function waitForResponse(type) {
    return new Promise((resolve, reject) => {
        if (savedCallback) {
            savedCallback('User canceled.');
        }
        savedCallback = result => {
            if (result.type !== type) {
                return;
            }
            savedCallback = undefined;
            // if (result.errCode !== 0) {
            //     const err = new Error(result.errMsg);
            //     err.errCode = result.errCode;
            //     reject(err);
            // } else {
            //     const {type, ...r} = result;
            //     resolve(r);
            // }
            // const {type, ...r} = result;
            resolve(result);
        };
    });
}

NativeAppEventEmitter.addListener('QQ_Resp', resp => {
    const callback = savedCallback;
    savedCallback = undefined;
    callback && callback(resp);
});

export function login(scopes) {
    return QQAPI.login(scopes)
        .then(() => waitForResponse("QQAuthorizeResponse"));
}

/**
 * 分享类型：图文、新闻（QQ，QZONE）
 */
export const SHARE_TYPE_NEWS = QQAPI.SHARE_TYPE_NEWS;
/**
 * 分享类型：纯图片（QQ、QZONE）
 */
export const SHARE_TYPE_IMAGE = QQAPI.SHARE_TYPE_IMAGE;
/**
 * 分享类型：纯文本(仅支持iOS版QQ)
 */
export const SHARE_TYPE_TEXT = QQAPI.SHARE_TYPE_TEXT;
/**
 * 分享类型：视频（仅支持iOS版QQ）
 */
export const SHARE_TYPE_VIDEO = QQAPI.SHARE_TYPE_VIDEO;
/**
 * 分享类型：音乐（QQ、QZone）
 */
export const SHARE_TYPE_AUDIO = QQAPI.SHARE_TYPE_AUDIO;
/**
 * 分享类型：应用推广（仅Android）
 */
export const SHARE_TYPE_APP = QQAPI.SHARE_TYPE_APP;

export function shareToQQ(data = {}) {
    return QQAPI.shareToQQ(data)
        .then(() => waitForResponse("QQShareResponse"));
}

export function shareToQzone(data = {}) {
    return QQAPI.shareToQzone(data)
        .then(() => waitForResponse("QQShareResponse"));
}

export function logout() {
    QQAPI.logout();
}