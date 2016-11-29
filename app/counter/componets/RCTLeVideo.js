/*************************************************************************
 * Description: SDK组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
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

    // /**
    //  * 设置视频seek到某一时间点
    //  * @param {any} time 时间点
    //  * @memberOf Video
    //  */
    // seek = (time) => {
    //     this.setNativeProps({ seek: time });
    // };


    // /**
    //  * 设置视频码率切换到某一值
    //  * @param {any} value 码率值
    //  * @memberOf Video
    //  */
    // rate = (value) => {
    //     this.setNativeProps({ rate: value });
    // };


    // /**
    //  * 设置视频音量百分比（0-100）
    //  * @param {any} percent 音量
    //  * @memberOf Video
    //  */
    // volume = (percent) => {
    //     this.setNativeProps({ volume: percent });
    // };

    // /**
    //  * 设置屏幕亮度（0-255）
    //  * @param {any} value 亮度
    //  * @memberOf Video
    //  */
    // brightness = (value) => {
    //     this.setNativeProps({ brightness: value });
    // };


    // /**
    //  * 设置屏幕方向 0-正横屏，1-正竖屏，8-反横屏，9-反竖屏
    //  * @param {any} orientation 方向
    //  * @memberOf Video
    //  */
    // orientation = (value) => {
    //     this.setNativeProps({ orientation: value });
    // };

    // /**
    //  * 处理数据源加载完成事件
    //  * @param {any} event 原生回调句柄
    //  * @memberOf Video
    //  */
    // _onSourceLoad = (event) => {
    //     if (this.props.onSourceLoad) {
    //         this.props.onSourceLoad(event.nativeEvent);
    //     }
    // };

    // /**
    //  * 处理屏幕方向设置完成事件
    //  * @param {any} event 原生回调句柄
    //  * @memberOf Video
    //  */
    // _onOrientationChange = (event) => {
    //     if (this.props.onOrientationChange) {
    //         this.props.onOrientationChange(event.nativeEvent);
    //     }
    // };

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

    // /**
    //  * 处理视频准备完成的事件
    //  * @param {any} event 原生回调句柄
    //  * @memberOf Video
    //  */
    // _onLoad = (event) => {
    //     if (this.props.onLoad) {
    //         this.props.onLoad(event.nativeEvent);
    //     }
    // };

    // /**
    //  * 处理视频出错的事件
    //  * @param {any} event 原生回调句柄
    //  * @memberOf Video
    //  */
    // _onError = (event) => {
    //     if (this.props.onError) {
    //         this.props.onError(event.nativeEvent);
    //     }
    // };

    /**
     * 处理VOD视频播放中的事件
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

    // /**
    //  * 处理视频播放完成的事件
    //  * @param {any} event 原生回调句柄
    //  * @memberOf Video
    //  */
    // _onEnd = (event) => {
    //     if (this.props.onEnd) {
    //         this.props.onEnd(event.nativeEvent);
    //     }
    // };

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
     * 处理广告点击的事件
     * @param {any} event 原生回调句柄
     * @memberOf Video
     */
    _onAdClick = (event) => {
        if (this.props.onAdClick) {
            this.props.onAdClick(event.nativeEvent);
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
    * 处理LIVE视频播放中的事件
    * @param {any} event 原生回调句柄
    * @memberOf Video
    */
    _onActionTimeShift = (event) => {
        if (this.props.onActionTimeShift) {
            this.props.onActionTimeShift(event.nativeEvent);
        }
    };

    /**
    * 处理LIVE活动直播中的状态回调
    * @param {any} event 原生回调句柄
    * @memberOf Video
    */
    _onActionStatusChange = (event) => {
        if (this.props.onActionStatusChange) {
            this.props.onActionStatusChange(event.nativeEvent);
        }
    };

    /**
    * 处理LIVE活动直播中的在线人数变化回调
    * @param {any} event 原生回调句柄
    * @memberOf Video
    */
    _onActionOnlineNumChange = (event) => {
        if (this.props.onActionOnlineNumChange) {
            this.props.onActionOnlineNumChange(event.nativeEvent);
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
            /*回调函数属性赋值*/
            onVideoSourceLoad: (event) => { if (this.props.onVideoSourceLoad) this.props.onVideoSourceLoad(event.nativeEvent); },
            /*设备相关*/
            onOrientationChange: (event) => { if (this.props.onOrientationChange) this.props.onOrientationChange(event.nativeEvent); },
            /*播放相关*/
            onVideoLoad: (event) => { if (this.props.onVideoLoad) this.props.onVideoLoad(event.nativeEvent); },
            onVideoSizeChange: (event) => { if (this.props.onVideoSizeChange) this.props.onVideoSizeChange(event.nativeEvent); },
            onVideoRateLoad: (event) => { if (this.props.onVideoRateLoad) this.props.onVideoRateLoad(event.nativeEvent); },
            onVideoError: (event) => { if (this.props.onVideoError) this.props.onVideoError(event.nativeEvent); },
            onVideoProgress: (event) => { if (this.props.onVideoProgress) this.props.onVideoProgress(event.nativeEvent); },
            onVideoSeek: (event) => { if (this.props.onVideoSeek) this.props.onVideoSeek(event.nativeEvent); },
            onVideoSeekComplete: (event) => { if (this.props.onVideoSeekComplete) this.props.onVideoSeekComplete(event.nativeEvent); },
            onVideoEnd: (event) => { if (this.props.onVideoEnd) this.props.onVideoEnd(event.nativeEvent); },
            onVideoPause: (event) => { if (this.props.onVideoPause) this.props.onVideoPause(event.nativeEvent); },
            onVideoResume: (event) => { if (this.props.onVideoResume) this.props.onVideoResume(event.nativeEvent); },
            onVideoRendingStart: (event) => { if (this.props.onVideoRendingStart) this.props.onVideoRendingStart(event.nativeEvent); },
            onVideoBufferPercent: (event) => { if (this.props.onVideoBufferPercent) this.props.onVideoBufferPercent(event.nativeEvent); },
            onVideoRateChange: (event) => { if (this.props.onVideoRateChange) this.props.onVideoRateChange(event.nativeEvent); },
            /*直播相关*/
            onActionLiveChange: (event) => { if (this.props.onActionLiveChange) this.props.onActionLiveChange(event.nativeEvent); },
            onActionTimeShift: (event) => { if (this.props.onActionTimeShift) this.props.onActionTimeShift(event.nativeEvent); },
            onActionStatusChange: (event) => { if (this.props.onActionStatusChange) this.props.onActionStatusChange(event.nativeEvent); },
            onActionOnlineNumChange: (event) => { if (this.props.onActionOnlineNumChange) this.props.onActionOnlineNumChange(event.nativeEvent); },
            /*媒资相关*/
            onMediaVodLoad: (event) => { if (this.props.onMediaVodLoad) this.props.onMediaVodLoad(event.nativeEvent); },
            onMediaLiveLoad: (event) => { if (this.props.onMediaLiveLoad) this.props.onMediaLiveLoad(event.nativeEvent); },
            onMediaActionLoad: (event) => { if (this.props.onMediaActionLoad) this.props.onMediaActionLoad(event.nativeEvent); },
            onMediaPlayURLLoad: (event) => { if (this.props.onMediaPlayURLLoad) this.props.onMediaPlayURLLoad(event.nativeEvent); },
            /*广告相关*/
            onAdvertStart: (event) => { if (this.props.onAdvertStart) this.props.onAdvertStart(event.nativeEvent); },
            onAdvertProgress: (event) => { if (this.props.onAdvertProgress) this.props.onAdvertProgress(event.nativeEvent); },
            onAdvertComplete: (event) => { if (this.props.onAdvertComplete) this.props.onAdvertComplete(event.nativeEvent); },
            onAdvertClick: (event) => { if (this.props.onAdvertClick) this.props.onAdvertClick(event.nativeEvent); },
            onAdvertError: (event) => { if (this.props.onAdvertError) this.props.onAdvertError(event.nativeEvent); },
            /*缓冲相关*/
            onBufferStart: (event) => { if (this.props.onBufferStart) this.props.onBufferStart(event.nativeEvent); },
            onBufferEnd: (event) => { if (this.props.onBufferEnd) this.props.onBufferEnd(event.nativeEvent); },
            onBufferPercent: (event) => { if (this.props.onBufferPercent) this.props.onBufferPercent(event.nativeEvent); },
            /*其他事件相关*/
            onOtherEventInfo: (event) => { if (this.props.onOtherEventInfo) this.props.onOtherEventInfo(event.nativeEvent); },
        });

        console.log(nativeProps);

        return (
            <RCTLeVideo
                ref={this._assignRoot}
                {...nativeProps}
                />
        );
    }
}

Video.propTypes = {
    /* 原生属性 */
    //src: PropTypes.object,

    /* 组件属性 */
    /* 播放源：支持点播、直播和本地或URI */
    source: PropTypes.oneOfType([
        //点播
        PropTypes.shape({
            playMode: PropTypes.number,
            uuid: PropTypes.string,
            vuid: PropTypes.string,
            businessline: PropTypes.string,
            saas: PropTypes.bool,
            pano: PropTypes.bool,
            hasSkin: PropTypes.bool,
        }),
        //直播
        PropTypes.shape({
            playMode: PropTypes.number,
            actionId: PropTypes.string,
            usehls: PropTypes.bool,
            customerId: PropTypes.string,
            businessline: PropTypes.string,
            cuid: PropTypes.string,
            utoken: PropTypes.string,
            pano: PropTypes.bool,
            hasSkin: PropTypes.bool,
        }),
        //uri
        PropTypes.shape({
            playMode: PropTypes.number,
            uri: PropTypes.string,
            pano: PropTypes.bool,
            hasSkin: PropTypes.bool,
        }),
    ]).isRequired,
    /* 跳转到时间点 */
    seek: PropTypes.number,
    /* 设置视频码率 */
    rate: PropTypes.string,
    /* 设置音量百分比 */
    volume: PropTypes.number,
    /* 设置亮度值0-255 */
    brightness: PropTypes.number,
    /* 设置屏幕方向 */
    orientation: PropTypes.number,
    /* 暂停或播放 */
    paused: PropTypes.bool,
    /* 点击广告 */
    clickAd: PropTypes.bool,
    /* 设置机位（直播） */
    live: PropTypes.string,
    /* 设置后台播放 */
    playInBackground: PropTypes.bool,
    /* 设置？？？ */
    playWhenInactive: PropTypes.bool,
    /* 设置进度条更新频率 */
    progressUpdateInterval: PropTypes.number,

    /* 数据源相关 */
    onVideoSourceLoad: PropTypes.func,
    /*设备相关*/
    onOrientationChange: PropTypes.func,
    /*播放相关*/
    onVideoSizeChange: PropTypes.func,
    onVideoLoad: PropTypes.func,
    onVideoRateLoad: PropTypes.func,
    onVideoProgress: PropTypes.func,
    onVideoSeek: PropTypes.func,
    onVideoSeekComplete: PropTypes.func,
    onVideoEnd: PropTypes.func,
    onVideoPause: PropTypes.func,
    onVideoResume: PropTypes.func,
    onVideoBufferPercent: PropTypes.func,
    onVideoRateChange: PropTypes.func,
    onVideoError: PropTypes.func,
    /* 缓冲相关 */
    onBufferStart: PropTypes.func,
    onBufferEnd: PropTypes.func,
    onVideoRendingStart: PropTypes.func,
    onBufferPercent: PropTypes.func,
    /*媒资相关*/
    onMediaVodLoad: PropTypes.func,
    onMediaLiveLoad: PropTypes.func,
    onMediaActionLoad: PropTypes.func,
    onMediaPlayURLLoad: PropTypes.func,
    /*广告相关*/
    onAdvertStart: PropTypes.func,
    onAdvertProgress: PropTypes.func,
    onAdvertComplete: PropTypes.func,
    onAdvertClick: PropTypes.func,
    onAdvertError: PropTypes.func,
    /**直播相关 */
    onActionLiveChange: PropTypes.func,
    onActionTimeShift: PropTypes.func,
    onActionStatusChange: PropTypes.func,
    onActionOnlineNumChange: PropTypes.func,
    /*其他事件*/
    onOtherEventInfo: PropTypes.func,

    /* Required by react-native */
    scaleX: PropTypes.number,
    scaleY: PropTypes.number,
    translateX: PropTypes.number,
    translateY: PropTypes.number,
    rotation: PropTypes.number,

    ...View.propTypes,
};

const RCTLeVideo = requireNativeComponent('RCTLeVideo', Video, {
    nativeOnly: { src: true },
});