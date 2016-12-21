/*************************************************************************
 * Description: 乐视直播机位播放组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-12-18
 * Modified Time: 2016-12-18
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
 * 封装LeSDK机位播放器
 * @export
 * @class SubVideo
 * @extends {Component}
 */
export default class SubVideo extends Component {
    /**
     * 设置组件别名
     * @param {any} component 组件名
     * @memberOf SubVideo
     */
    _assignRoot = (component) => {
        this._root = component;
    };

    /**
     * 设置封装属性映射为Native属性
     * @param {any} nativeProps 原生属性
     * @memberOf SubVideo
     */
    setNativeProps(nativeProps) {
        this._root.setNativeProps(nativeProps);
    }

    static propTypes = {
        /* 原生属性 */
        //src: PropTypes.object,

        /* 组件属性 */
        /* 播放源：URL */
        source: PropTypes.oneOfType([
            //uri
            PropTypes.shape({
                liveId: PropTypes.string,
                streamId: PropTypes.string,
                streamUrl: PropTypes.string,
                usehls: PropTypes.bool,
            }),
        ]).isRequired,

        /* 数据源相关 */
        onSubVideoSourceLoad: PropTypes.func,
        onSubVideoSizeChange: PropTypes.func,
        onSubVideoLoad: PropTypes.func,
        onSubVideoError: PropTypes.func,
        onSubVideoPause: PropTypes.func,
        onSubVideoResume: PropTypes.func,
        onSubBufferStart: PropTypes.func,
        onSubBufferEnd: PropTypes.func,
        onSubVideoRendingStart: PropTypes.func,

        /* Required by react-native */
        ...View.propTypes,
    };

    render() {
        const source = resolveAssetSource(this.props.source) || {};
        /* 组件属性赋值 */
        const nativeProps = Object.assign({}, this.props);
        Object.assign(nativeProps, {
            style: [styles.base, nativeProps.style],
            src: {
                liveId: source.liveId,
                streamId: source.streamId,
                streamUrl: source.streamUrl,
                usehls: source.usehls,
            },
            /*回调函数属性赋值*/
            onSubVideoSourceLoad: (event) => { if (this.props.onSubVideoSourceLoad) this.props.onSubVideoSourceLoad(event.nativeEvent); },
            onSubVideoSizeChange: (event) => { if (this.props.onSubVideoSizeChange) this.props.onSubVideoSizeChange(event.nativeEvent); },
            onSubVideoLoad: (event) => { if (this.props.onSubVideoLoad) this.props.onSubVideoLoad(event.nativeEvent); },
            onSubVideoError: (event) => { if (this.props.onSubVideoError) this.props.onSubVideoError(event.nativeEvent); },
            onSubVideoPause: (event) => { if (this.props.onSubVideoPause) this.props.onSubVideoPause(event.nativeEvent); },
            onSubVideoResume: (event) => { if (this.props.onSubVideoResume) this.props.onSubVideoResume(event.nativeEvent); },
            onSubBufferStart: (event) => { if (this.props.onSubBufferStart) this.props.onSubBufferStart(event.nativeEvent); },
            onSubBufferEnd: (event) => { if (this.props.onSubBufferEnd) this.props.onSubBufferEnd(event.nativeEvent); },
            onSubVideoRendingStart: (event) => { if (this.props.onSubVideoRendingStart) this.props.onSubVideoRendingStart(event.nativeEvent); },
        });

        // console.log(nativeProps);
        return (
            <RCTLeSubVideo
                ref={this._assignRoot}
                {...nativeProps}
                />
        );
    }
}

const RCTLeSubVideo = requireNativeComponent('RCTLeSubVideo', SubVideo, {
    nativeOnly: { src: true },
});
