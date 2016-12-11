/*************************************************************************
 * Description: 方向组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-11
 * Modified Time: 2016-12-11
 ************************************************************************/
'use strict';

import {
    NativeModules,
    DeviceEventEmitter,
    NativeEventEmitter
} from 'react-native';

const Orientation = NativeModules.OrientationModule;
const myNativeEvt = new NativeEventEmitter(Orientation);  //创建自定义事件接口

var listeners = {};
const onOrientationDidChangeEvent = "onOrientationDidChange";
// const orientationDidChangeEvent = "orientationDidChange";
// const specificOrientationDidChangeEvent = "specificOrientationDidChange";

var id = 0;
var META = '__listener_id';

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
    getOrientation(cb) {
        Orientation.getOrientation((error, orientation) => {
            cb(error, orientation);
        });
    },
    setOrientation(orientation) {
        Orientation.setOrientation(orientation);
    },
    // getSpecificOrientation(cb) {
    //     Orientation.getSpecificOrientation((error, orientation) => {
    //         cb(error, orientation);
    //     });
    // },
    // lockToPortrait() {
    //     Orientation.lockToPortrait();
    // },
    // lockToLandscape() {
    //     Orientation.lockToLandscape();
    // },
    // lockToLandscapeRight() {
    //     Orientation.lockToLandscapeRight();
    // },
    // lockToLandscapeLeft() {
    //     Orientation.lockToLandscapeLeft();
    // },
    // unlockAllOrientations() {
    //     Orientation.unlockAllOrientations();
    // },
    addOnOrientationListener(cb) {
        var key = getKey(cb);
        listeners[key] = myNativeEvt.addListener(onOrientationDidChangeEvent,
            (body) => {
                cb(body.orientation);
            });
    },
    removeOnOrientationListener(cb) {
        var key = getKey(cb);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;
    },
    // addOrientationListener(cb) {
    //     var key = getKey(cb);
    //     listeners[key] = myNativeEvt.addListener(orientationDidChangeEvent,
    //         (body) => {
    //             cb(body.orientation);
    //         });
    // },
    // removeOrientationListener(cb) {
    //     var key = getKey(cb);
    //     if (!listeners[key]) {
    //         return;
    //     }
    //     listeners[key].remove();
    //     listeners[key] = null;
    // },
    // addSpecificOrientationListener(cb) {
    //     var key = getKey(cb);
    //     listeners[key] = myNativeEvt.addListener(specificOrientationDidChangeEvent,
    //         (body) => {
    //             cb(body.specificOrientation);
    //         });
    // },
    // removeSpecificOrientationListener(cb) {
    //     var key = getKey(cb);
    //     if (!listeners[key]) {
    //         return;
    //     }
    //     listeners[key].remove();
    //     listeners[key] = null;
    // },
    getInitialOrientation() {
        return Orientation.initialOrientation;
    }
};
