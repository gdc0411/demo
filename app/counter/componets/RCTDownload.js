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

const Download = NativeModules.DownloadModule;
const myNativeEvt = new NativeEventEmitter(Download);

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
    download(src) {
        Download.download(src);
    },

    addItemUpdateListener(handler: Function) {
        var key = getKey(handler, META_1);
        listeners[key] = myNativeEvt.addListener(Download.EVENT_DOWNLOAD_ITEM_UPDATE, message => {
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
