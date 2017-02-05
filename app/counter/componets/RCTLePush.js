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
    }

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
        ]).isRequired,

        /* 开始/停止推流 */
        pushed: PropTypes.bool,

        /* 推流端事件相关 */
        onPushTargetLoad: PropTypes.func,
        onPushOperate: PropTypes.func,
        onPushTimeUpdate: PropTypes.func,

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
                url: target.url,
                landscape: target.landscape,
            },
            /*回调函数属性赋值*/
            onPushTargetLoad: (event) => { this.props.onPushTargetLoad && this.props.onPushTargetLoad(event.nativeEvent); },
            /* 推流操作 */
            onPushOperate: (event) => { this.props.onPushOperate && this.props.onPushOperate(event.nativeEvent); },
            onPushTimeUpdate: (event) => { this.props.onPushTimeUpdate && this.props.onPushTimeUpdate(event.nativeEvent); },

        });

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
