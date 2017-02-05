/*************************************************************************
 * Description: 乐视推流组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-02-05
 * Modified Time: 2017-02-05
 ************************************************************************/
'use strict';

import React, { Component, PropTypes } from 'react';
import {
    StyleSheet,
    requireNativeComponent,
    NativeModules,
    View
} from 'react-native';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';

const styles = StyleSheet.create({
    base: {
        overflow: 'hidden',
    },
});


/**
 * 推流类型
 */
export const PUSH_TYPE_MOBILE_URI = NativeModules.UIManager.RCTLePush.Constants.PUSH_TYPE_MOBILE_URI;
export const PUSH_TYPE_MOBILE = NativeModules.UIManager.RCTLePush.Constants.PUSH_TYPE_MOBILE;
export const PUSH_TYPE_LECLOUD = NativeModules.UIManager.RCTLePush.Constants.PUSH_TYPE_LECLOUD;
export const PUSH_TYPE_NONE = NativeModules.UIManager.RCTLePush.Constants.PUSH_TYPE_NONE;
// alert(NativeModules.UIManager.RCTLePush.Constants.PUSH_TYPE_MOBILE);

/**
 * 封装LeSDK推流组件
 * @export
 * @class Push
 * @extends {Component}
 */
export default class Push extends Component {
    /**
     * 设置组件别名
     * @param {any} component 组件名
     * @memberOf Push
     */
    _assignRoot = (component) => {
        this._root = component;
    };

    /**
     * 设置封装属性映射为Native属性
     * @param {any} nativeProps 原生属性
     * @memberOf Push
     */
    setNativeProps(nativeProps) {
        this._root.setNativeProps(nativeProps);
    };

    static propTypes = {
        /* 原生属性 */
        //para: PropTypes.object,

        /* 组件属性 */
        /* 推流参数：支持移动直播（有地址）、移动直播（无地址）、乐视直播 */
        target: PropTypes.oneOfType([
            //移动直播（有地址）
            PropTypes.shape({
                type: PropTypes.number.isRequired,
                url: PropTypes.string,
                landscape: PropTypes.bool,
            }),
            //移动直播（无地址）
            PropTypes.shape({
                type: PropTypes.number.isRequired,
                streamName: PropTypes.string,
                domainName: PropTypes.string,
                appkey: PropTypes.string,
                landscape: PropTypes.bool,
            }),
            //乐视云直播
            PropTypes.shape({
                type: PropTypes.number.isRequired,
                activityId: PropTypes.string,
                userId: PropTypes.string,
                secretKey: PropTypes.string,
                landscape: PropTypes.bool,
            }),
        ]).isRequired,

        /* 开始/停止推流 */
        push: PropTypes.bool,
        /* 切换摄像头 */
        camera: PropTypes.number,
        /* 开始/停止闪光灯 */
        flash: PropTypes.bool,

        /* 推流端事件相关 */
        onPushTargetLoad: PropTypes.func,
        onPushStateUpdate: PropTypes.func,
        onPushTimeUpdate: PropTypes.func,
        onPushCameraUpdate: PropTypes.func,
        onPushFlashUpdate: PropTypes.func,

        ...View.propTypes,
    };

    render() {
        const target = resolveAssetSource(this.props.target) || {};
        /* 组件属性赋值 */
        const nativeProps = Object.assign({}, this.props);
        Object.assign(nativeProps, {
            style: [styles.base, nativeProps.style],
            para: {
                type: target.type,
                url: target.url || null,
                streamName: target.streamName || null,
                domainName: target.domainName || null,
                appkey: target.appkey || null,                
                activityId: target.activityId || null,
                userId: target.userId || null,
                secretKey: target.secretKey || null,
                landscape: target.landscape,
            },
            /*回调函数属性赋值*/
            onPushTargetLoad: (event) => { this.props.onPushTargetLoad && this.props.onPushTargetLoad(event.nativeEvent); },
            /* 推流操作 */
            onPushStateUpdate: (event) => { this.props.onPushStateUpdate && this.props.onPushStateUpdate(event.nativeEvent); },
            onPushTimeUpdate: (event) => { this.props.onPushTimeUpdate && this.props.onPushTimeUpdate(event.nativeEvent); },
            onPushCameraUpdate: (event) => { this.props.onPushCameraUpdate && this.props.onPushCameraUpdate(event.nativeEvent); },
            onPushFlashUpdate: (event) => { this.props.onPushFlashUpdate && this.props.onPushFlashUpdate(event.nativeEvent); },

        });

        // alert(NativeModules.UIManager.RCTLePush.Constants.PUSH_TYPE_MOBILE);

        // console.log(nativeProps);
        return (
            <RCTLePush
                ref={this._assignRoot}
                {...nativeProps}
            />
        );
    }
}

const RCTLePush = requireNativeComponent('RCTLePush', Push, {
    nativeOnly: { para: true },
});
