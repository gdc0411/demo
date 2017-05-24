/*************************************************************************
 * Description: LePayAPI组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-05-25
 * Modified Time: 2017-05-25
 ************************************************************************/
'use strict';

import { NativeModules, NativeAppEventEmitter } from 'react-native';

const LePayAPI = NativeModules.LePayModule;

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


export function pay(data = {}) {
    return LePayAPI.pay(data).then((resp) => success && success(resp)).catch(e => error && error(e));
}

