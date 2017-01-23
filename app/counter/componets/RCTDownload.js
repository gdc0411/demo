/*************************************************************************
 * Description: 视频下载组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-01-23
 * Modified Time: 2017-01-23
 ************************************************************************/
'use strict';

import { NativeModules, NativeEventEmitter } from 'react-native';

const Download = NativeModules.DownloadModule;
const myNativeEvt = new NativeEventEmitter(Download);

var listeners = {};

var id = 0;
var META = '__download_listener_id';

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
    download(src) {
        Download.download(src);
    },
};
