/*************************************************************************
 * Description: 视频下载组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-01-23
 * Modified Time: 2017-01-23
 ************************************************************************/
'use strict';

import React, {
    NativeModules,
    NativeEventEmitter,
    Platform,
    AppState,
} from 'react-native';

const DownloadAPI = NativeModules.DownloadModule;
const myNativeEvt = new NativeEventEmitter(DownloadAPI);

var listeners = {};

var id = 0;
var META_1 = '__download_item_update_listener_id';
var META_2 = '__dowanload_list_update_listener_id';

function getKey(listener, META) {
    if (!listener.hasOwnProperty(META)) {
        if (!Object.isExtensible(listener)) {
            return 'F';
        }
        Object.defineProperty(listener, META, {
            value: 'L' + ++id,
        });
    }
    return listener[META];
};

export const SUCCESS = DownloadAPI.SUCCESS;
export const STOP = DownloadAPI.STOP;
export const START = DownloadAPI.START;
export const FAILED = DownloadAPI.FAILED;
export const CANCEL = DownloadAPI.CANCEL;
export const INIT = DownloadAPI.INIT;
export const WAIT = DownloadAPI.WAIT;
export const RATEINFO = DownloadAPI.RATEINFO;

module.exports = {
    download(src) {
        DownloadAPI.download(src);
    },
    addItemUpdateListener(handler: Function) {
        var key = getKey(handler, META_1);
        listeners[key] = myNativeEvt.addListener(DownloadAPI.EVENT_DOWNLOAD_ITEM_UPDATE, message => {
            //处于后台时，拦截收到的消息
            if (AppState.currentState === 'background') {
                return;
            }
            handler(message);
        });
    },
    removeItemUpdateListener(handler: Function) {
        var key = getKey(handler, META_1);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;
    },
};
