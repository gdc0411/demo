/*************************************************************************
 * Description: 微博API组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-26
 * Modified Time: 2016-12-26
 ************************************************************************/
'use strict';

import { NativeModules, NativeAppEventEmitter } from 'react-native';
// import promisify from 'es6-promisify';

const WeiboAPI = NativeModules.WeiboModule;

// Used only with promisify. Transform callback to promise result.
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

// function wrapApi(nativeFunc) {
//     if (!nativeFunc) {
//         return undefined;
//     }
//     const promisified = promisify(nativeFunc, translateError);
//     return (...args) => {
//         return promisified(...args);
//     };
// }

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
                resolve(result);
            // }
        };
    });
}

NativeAppEventEmitter.addListener('Weibo_Resp', resp => {
    const callback = savedCallback;
    savedCallback = undefined;
    callback && callback(resp);
});


const defaultScope = "all";
const defaultRedirectURI = "https://api.weibo.com/oauth2/default.html";

function checkData(data) {
    if (!data.redirectURI) {
        data.redirectURI = defaultRedirectURI;
    }
    if (!data.scope) {
        data.scope = defaultScope;
    }
}

export const getApiVersion = WeiboAPI.getApiVersion;
export const isInstalled = WeiboAPI.isInstalled;
export const isSupportApi = WeiboAPI.isSupportApi;

// const nativeSendAuthRequest = wrapApi(WeiboAPI.login);
// const nativeSendMessageRequest = wrapApi(WeiboAPI.shareToWeibo);

/**
 * 分享类型：图文、新闻
 */
export const SHARE_TYPE_NEWS = WeiboAPI.SHARE_TYPE_NEWS;
/**
 * 分享类型：纯图片（网络地址）
 */
export const SHARE_TYPE_IMAGE = WeiboAPI.SHARE_TYPE_IMAGE;
/**
 * 分享类型：纯文本
 */
export const SHARE_TYPE_TEXT = WeiboAPI.SHARE_TYPE_TEXT;
/**
 * 分享类型：视频
 */
export const SHARE_TYPE_VIDEO = WeiboAPI.SHARE_TYPE_VIDEO;
/**
 * 分享类型：音乐
 */
export const SHARE_TYPE_AUDIO = WeiboAPI.SHARE_TYPE_AUDIO;
/**
 * 分享类型：声音
 */
export const SHARE_TYPE_FILE = WeiboAPI.SHARE_TYPE_VOICE;

export function login(config = {}) {
    checkData(config);
    // return Promise.all([waitForResponse('WBAuthorizeResponse'), nativeSendAuthRequest(config)]).then(v=>v[0]);
    // return Promise.all([waitForResponse('WBAuthorizeResponse'), WeiboAPI.login(config)]).then(v=>v[0]);
    return WeiboAPI.login(config).then(() => waitForResponse("WBAuthorizeResponse"));
}

export function shareToWeibo(data) {
    checkData(data);
    // return Promise.all([waitForResponse('WBSendMessageToWeiboResponse'), WeiboAPI.shareToWeibo(data)]).then(v => v[0]);
    return WeiboAPI.shareToWeibo(data).then(() => waitForResponse("WBSendMessageToWeiboResponse"));

}