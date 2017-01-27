/*************************************************************************
 * Description: 缓存管理组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-01-27
 * Modified Time: 2017-01-27
 ************************************************************************/
'use strict';

import React, {
    NativeModules,
    NativeEventEmitter,
    Platform,
    AppState,
} from 'react-native';

const CacheAPI = NativeModules.CacheModule;
const myNativeEvt = new NativeEventEmitter(CacheAPI);

var listeners = {};

var id = 0;
var META = '__cache_update_listener_id';

function getKey(listener) {
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
    EVENT_CALC_PROGRESS: CacheAPI.EVENT_CALC_PROGRESS,
    EVENT_CALC_SUCCESS: CacheAPI.EVENT_CALC_SUCCESS,
    EVENT_CALC_FAILED: CacheAPI.EVENT_CALC_FAILED,
    EVENT_CLEAR_PROGRESS: CacheAPI.EVENT_CLEAR_PROGRESS,
    EVENT_CLEAR_SUCCESS: CacheAPI.EVENT_CLEAR_SUCCESS,
    EVENT_CLEAR_FAILED: CacheAPI.EVENT_CLEAR_FAILED,
    calc() {
        CacheAPI.calc();
    },
    clear(src) {
        CacheAPI.clear();
    },
    addCacheUpdateListener(handler: Function) {
        var key = getKey(handler, META);
        listeners[key] = myNativeEvt.addListener(CacheAPI.EVENT_CACHE_UPDATE_MESSAGE, message => {
            //处于后台时，拦截收到的消息
            if (AppState.currentState === 'background') {
                return;
            }
            handler(message);
        });
    },

    removeCacheUpdateListener(handler: Function) {
        var key = getKey(handler, META);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;
    },
};
