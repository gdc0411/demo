/*************************************************************************
 * Description: 友盟PUSH组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-01-06
 * Modified Time: 2017-01-10
 ************************************************************************/
'use strict';

import React, {
    NativeModules,
    NativeEventEmitter,
    Platform,
    AppState,
} from 'react-native';

const UmengPush = NativeModules.UmengPushModule;
const myNativeEvt = new NativeEventEmitter(UmengPush);  //创建自定义事件接口

var receiveMessageSubscript, openMessageSubscription;

var listeners = {};

var id = 0;
var META_1 = '__recv_notify_listener_id';
var META_2 = '__open_notify_listener_id';

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

    addUmengReceiveMessageListener(handler: Function) {
        var key = getKey(handler, META_1);
        listeners[key] = myNativeEvt.addListener(UmengPush.EVENT_UMENG_RECV_MESSAGE, message => {
            //处于后台时，拦截收到的消息
            if (AppState.currentState === 'background') {
                return;
            }
            handler(message);
        });
    },

    removeUmengReceiveMessageListener(handler: Function) {
        var key = getKey(handler, META_1);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;
    },

    addUmengOpenMessageListener(handler: Function) {
        var key = getKey(handler, META_2);
        listeners[key] = myNativeEvt.addListener(UmengPush.EVENT_UMENG_OPEN_MESSAGE,
            message => {
                handler(message);
            });
        // openMessageSubscription = myNativeEvt.addListener(UmengPush.EVENT_UMENG_OPEN_MESSAGE, handler);
    },

    removeUmengOpenMessageListener(handler: Function) {
        var key = getKey(handler, META_2);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;
    },

    // addEventListener(eventName: string, handler: Function) {
    //     if (Platform.OS === 'android') {
    //         return DeviceEventEmitter.addListener(eventName, (event) => {
    //             handler(event);
    //         });
    //     }
    //     else {
    //         return NativeAppEventEmitter.addListener(
    //             eventName, (userInfo) => {
    //                 handler(userInfo);
    //             });
    //     }
    // },

};
