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

module.exports = {
    EVENT_TYPE_SUCCESS: DownloadAPI.EVENT_TYPE_SUCCESS,
    EVENT_TYPE_START: DownloadAPI.EVENT_TYPE_START,
    EVENT_TYPE_FAILED: DownloadAPI.EVENT_TYPE_FAILED,
    EVENT_TYPE_EXIST: DownloadAPI.EVENT_TYPE_EXIST,
    DOWLOAD_STATE_WAITING: DownloadAPI.DOWLOAD_STATE_WAITING,
    DOWLOAD_STATE_DOWNLOADING: DownloadAPI.DOWLOAD_STATE_DOWNLOADING,
    DOWLOAD_STATE_STOP: DownloadAPI.DOWLOAD_STATE_STOP,
    DOWLOAD_STATE_SUCCESS: DownloadAPI.DOWLOAD_STATE_SUCCESS,
    DOWLOAD_STATE_FAILED: DownloadAPI.DOWLOAD_STATE_FAILED,
    DOWLOAD_STATE_NO_DISPATCH: DownloadAPI.DOWLOAD_STATE_NO_DISPATCH,
    DOWLOAD_STATE_NO_PERMISSION: DownloadAPI.DOWLOAD_STATE_NO_PERMISSION,
    DOWLOAD_STATE_URL_REQUEST_FAILED: DownloadAPI.DOWLOAD_STATE_URL_REQUEST_FAILED,
    DOWLOAD_STATE_DISPATCHING: DownloadAPI.DOWLOAD_STATE_DISPATCHING,

    download(src) {
        DownloadAPI.download(src);
    },
    list(src) {
        DownloadAPI.list();
    },
    pause(src) {
        DownloadAPI.pause(src);
    },
    resume(src) {
        DownloadAPI.resume(src);
    },
    retry(src) {
        DownloadAPI.retry(src);
    },
    delete(src) {
        DownloadAPI.delete(src);
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

    addListUpdateListener(handler: Function) {
        var key = getKey(handler, META_2);
        listeners[key] = myNativeEvt.addListener(DownloadAPI.EVENT_DOWNLOAD_LIST_UPDATE, message => {
            //处于后台时，拦截收到的消息
            if (AppState.currentState === 'background') {
                return;
            }
            handler(message);
        });
    },
    removeListUpdateListener(handler: Function) {
        var key = getKey(handler, META_2);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;
    },
};
