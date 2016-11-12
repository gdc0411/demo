/*************************************************************************
 * Description: SDK组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
'use strict';

import React, {Component, PropTypes} from 'react';
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
 * 封装LeSDK播放器
 * @export
 * @class Video
 * @extends {Component}
 */
export default class Video extends Component {

    /**
     * 设置组件别名
     * @param {any} component 组件名
     * @memberOf Video
     */
    _assignRoot = (component) => {
        this._root = component;
    };

    /**
     * 设置封装属性映射为Native属性
     * @param {any} nativeProps 原生属性
     * @memberOf Video
     */
    setNativeProps(nativeProps) {
        this._root.setNativeProps(nativeProps);
    }

    /**
     * 设置视频seek到某一时间点
     * @param {any} time 时间点
     * @memberOf Video
     */
    seek = (time) => {
        this.setNativeProps({ seek: time });
    };


    /**
     * 设置视频码率切换到某一值
     * @param {any} value 码率值
     * @memberOf Video
     */
    rate = (value) => {
        this.setNativeProps({ rate: value });
    };


    /**
     * 设置视频音量百分比（0-100）
     * @param {any} percent 音量
     * @memberOf Video
     */
    volume = (percent) => {
        this.setNativeProps({ volume: percent });
    };

    /**
     * 设置视频音量百分比（0-100）
     * @param {any} percent 音量
     * @memberOf Video
     */
    brightness = (value) => {
        this.setNativeProps({ brightness: value });
    };

    /**
     * 处理数据源加载完成事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onSourceLoad = (event) => {
        if (this.props.onSourceLoad) {
            this.props.onSourceLoad(event.nativeEvent);
        }
    };

    /**
     * 处理获取播放器尺寸的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onSizeChange = (event) => {
        if (this.props.onSizeChange) {
            this.props.onSizeChange(event.nativeEvent);
        }
    };

    /**
     * 处理获取可选码率的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onRateLoad = (event) => {
        if (this.props.onRateLoad) {
            this.props.onRateLoad(event.nativeEvent);
        }
    };

    /**
     * 处理视频准备完成的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onLoad = (event) => {
        if (this.props.onLoad) {
            this.props.onLoad(event.nativeEvent);
        }
    };

    /**
     * 处理视频出错的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onError = (event) => {
        if (this.props.onError) {
            this.props.onError(event.nativeEvent);
        }
    };

    /**
     * 处理视频播放中的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onProgress = (event) => {
        if (this.props.onProgress) {
            this.props.onProgress(event.nativeEvent);
        }
    };

    /**
     * 处理视频seek的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onSeek = (event) => {
        if (this.props.onSeek) {
            this.props.onSeek(event.nativeEvent);
        }
    };

    /**
     * 处理视频seek完毕的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onSeekComplete = (event) => {
        if (this.props.onSeekComplete) {
            this.props.onSeekComplete(event.nativeEvent);
        }
    };

    /**
     * 处理视频播放完成的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onEnd = (event) => {
        if (this.props.onEnd) {
            this.props.onEnd(event.nativeEvent);
        }
    };

    /**
     * 处理视频暂停的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onPause = (event) => {
        if (this.props.onPause) {
            this.props.onPause(event.nativeEvent);
        }
    };

    /**
     * 处理视频恢复播放的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onResume = (event) => {
        if (this.props.onResume) {
            this.props.onResume(event.nativeEvent);
        }
    };

    /**
     * 处理视频缓冲开始的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onStartBuffer = (event) => {
        if (this.props.onStartBuffer) {
            this.props.onStartBuffer(event.nativeEvent);
        }
    };

    /**
     * 处理视频缓冲结束的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onEndBuffer = (event) => {
        if (this.props.onEndBuffer) {
            this.props.onEndBuffer(event.nativeEvent);
        }
    };

    /**
     * 处理码率切换完成的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onRateChange = (event) => {
        if (this.props.onRateChange) {
            this.props.onRateChange(event.nativeEvent);
        }
    };

    /**
     * 处理视频总体缓存进度的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onPlayablePercent = (event) => {
        if (this.props.onPlayablePercent) {
            this.props.onPlayablePercent(event.nativeEvent);
        }
    };

    /**
     * 处理视频渲染第一帧完成的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onStartRending = (event) => {
        if (this.props.onStartRending) {
            this.props.onStartRending(event.nativeEvent);
        }
    };

    /**
     * 处理视频缓冲进度（百分比）的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onBuffPercent = (event) => {
        if (this.props.onBufferPercent) {
            this.props.onBufferPercent(event.nativeEvent);
        }
    };


    /**
     * 处理广告开始的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onAdStart = (event) => {
        if (this.props.onAdStart) {
            this.props.onAdStart(event.nativeEvent);
        }
    };

    /**
     * 处理广告完成的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onAdComplete = (event) => {
        if (this.props.onAdComplete) {
            this.props.onAdComplete(event.nativeEvent);
        }
    };

    /**
     * 处理广告正在播放的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onAdProgress = (event) => {
        if (this.props.onAdProgress) {
            this.props.onAdProgress(event.nativeEvent);
        }
    };

    /**
     * 处理广告出错的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onAdError = (event) => {
        if (this.props.onAdError) {
            this.props.onAdError(event.nativeEvent);
        }
    };

    /**
     * 处理获取媒资点播数据事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onMMSVodLoad = (event) => {
        if (this.props.onMMSVodLoad) {
            this.props.onMMSVodLoad(event.nativeEvent);
        }
    };

    /**
     * 处理获取媒资直播数据事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onMMSLiveLoad = (event) => {
        if (this.props.onMMSLiveLoad) {
            this.props.onMMSLiveLoad(event.nativeEvent);
        }
    };

    /**
     * 处理获取媒资活动直播数据事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onMMSActionLoad = (event) => {
        if (this.props.onMMSActionLoad) {
            this.props.onMMSActionLoad(event.nativeEvent);
        }
    };

    /**
     * 处理获取调度服务返回的数据（直播、点播等）事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onMMSPlayURLLoad = (event) => {
        if (this.props.onMMSPlayURLLoad) {
            this.props.onMMSPlayURLLoad(event.nativeEvent);
        }
    };

    /**
     * 处理云直播机位切换完成的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onActionLiveChange = (event) => {
        if (this.props.onActionLiveChange) {
            this.props.onActionLiveChange(event.nativeEvent);
        }
    };

    /**
     * 处理其他未定义的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onOtherEvent = (event) => {
        if (this.props.onOtherEvent) {
            this.props.onOtherEvent(event.nativeEvent);
        }
    };

    render() {
        const source = resolveAssetSource(this.props.source) || {};
        let uri = source.uri;
        if (uri && uri.match(/^\//)) {
            uri = `file://${uri}`;
        }
        /* 组件属性赋值 */
        const nativeProps = Object.assign({}, this.props);
        Object.assign(nativeProps, {
            style: [styles.base, nativeProps.style],
            src: {
                playMode: source.playMode,
                uuid: source.uuid,
                vuid: source.vuid,
                businessline: source.businessline,
                saas: source.saas || true,
                actionId: source.actionId,
                usehls: source.usehls || false,
                customerId: source.customerId,
                cuid: source.cuid,
                utoken: source.utoken,
                pano: source.pano || false,
                hasSkin: source.hasSkin || false,
                uri: uri,
            },
            /* 回调函数赋值 */
            onVideoSourceLoad: this._onSourceLoad,
            /*播放相关*/
            onVideoLoad: this._onLoad,
            onVideoSizeChange: this._onSizeChange,
            onVideoRateLoad: this._onRateLoad,
            onVideoError: this._onError,
            onVideoProgress: this._onProgress,
            onVideoSeek: this._onSeek,
            onVideoSeekComplete: this._onSeekComplete,
            onVideoEnd: this._onEnd,
            onVideoPause: this._onPause,
            onVideoResume: this._onResume,
            onVideoRendingStart: this._onStartRending,
            onVideoBufferPercent: this._onPlayablePercent,
            onVideoRateChange: this._onRateChange,
            /*直播相关*/
            onActionLiveChange: this._onActionLiveChange,
            /*媒资相关*/
            onMediaVodLoad: this._onMMSVodLoad,
            onMediaLiveLoad: this._onMMSLiveLoad,
            onMediaActionLoad: this._onMMSActionLoad,
            onMediaPlayURLLoad: this._onMMSPlayURLLoad,
            /*广告相关*/
            onAdvertStart: this._onAdStart,
            onAdvertProgress: this._onAdProgress,
            onAdvertComplete: this._onAdComplete,
            onAdvertError: this._onAdError,
            /*缓冲相关*/
            onBufferStart: this._onStartBuffer,
            onBufferEnd: this._onEndBuffer,
            onBufferPercent: this._onBuffPercent,
            /*其他事件相关*/
            onOtherEventInfo: this._onOtherEvent,
        });

        return (
            <RCTLeVideoView
                ref={this._assignRoot}
                {...nativeProps}
                />
        );
    }
}

Video.propTypes = {
    /* 原生属性 */
    /* 播放源：支持点播、直播和本地或URI */
    src: PropTypes.object.isRequired,
    /* 跳转到时间点 */
    seek: PropTypes.number,
    /* 设置视频码率 */
    rate: PropTypes.string,
    /* 设置机位（直播） */
    live: PropTypes.string,
    /* 设置音量百分比 */
    volume: PropTypes.number,
    /* 设置亮度值0-255 */
    brightness: PropTypes.number,

    /* 组件属性 */
    /* 暂停或播放 */
    paused: PropTypes.bool,

    /* 数据源设置完毕回调 */
    onSourceLoad: PropTypes.func,
    /* 视频尺寸获得回调 */
    onSizeChange: PropTypes.func,
    /* 播放加载完成回调 */
    onLoad: PropTypes.func,
    /* 可选码率列表加载完成回调 */
    onRateLoad: PropTypes.func,

    /* 播放错误回调 */
    onError: PropTypes.func,
    /* 播放进行回调 */
    onProgress: PropTypes.func,
    /* 播放跳转回调 */
    onSeek: PropTypes.func,
    /* 播放跳转完毕回调 */
    onSeekComplete: PropTypes.func,
    /* 播放结束回调 */
    onEnd: PropTypes.func,
    /* 播放暂停回调 */
    onPause: PropTypes.func,
    /* 播放后台恢复回调 */
    onResume: PropTypes.func,
    /* 播放码率设置改变回调 */
    onPlaybackRateChange: PropTypes.func,
    /* 播放总体缓冲进度回调 */
    onPlayablePercent: PropTypes.func,
    /* 播放码率切换的回调 */
    onRateChange: PropTypes.func,

    /* 缓冲开始 */
    onStartBuffer: PropTypes.func,
    /* 缓冲完毕 */
    onEndBuffer: PropTypes.func,
    /* 渲染第一帧完成 */
    onStartRending: PropTypes.func,
    /* 视频缓冲进度，百分比 */
    onBuffPercent: PropTypes.func,

    /*媒资相关*/
    onMMSVodLoad: PropTypes.func,
    onMMSLiveLoad: PropTypes.func,
    onMMSActionLoad: PropTypes.func,
    onMMSPlayURLLoad: PropTypes.func,

    /*广告相关*/
    onAdStart: PropTypes.func,
    onAdProgress: PropTypes.func,
    onAdComplete: PropTypes.func,
    onAdError: PropTypes.func,

    /**直播相关 */
    onActionLiveChange: PropTypes.func,

    /*其他事件*/
    onOtherEvent: PropTypes.func,

    /* Required by react-native */
    scaleX: PropTypes.number,
    scaleY: PropTypes.number,
    translateX: PropTypes.number,
    translateY: PropTypes.number,
    rotation: PropTypes.number,

    ...View.propTypes,
};

const RCTLeVideoView = requireNativeComponent('RCTLeVideoView', Video, {
    nativeOnly: {
        src: true, seek: true, rate: true, live: true, volume: true, brightness: true,
    },
});
