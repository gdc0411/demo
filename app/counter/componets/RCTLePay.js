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

/**
 *
 * 乐BOSS接口调用
 *
 * @param {*} data 支付数据
 * @param {Function} success 成功回调
 * @param {Function} error 失败回调
 */
export function pay(data = {}, success: Function, error: Function) {
    return LePayAPI.doPay(data).then((resp) => success && success(resp)).catch(e => error && error(e));
}

