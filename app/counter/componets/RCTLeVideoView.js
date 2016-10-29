
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

export default class Video extends Component {

    setNativeProps(nativeProps) {
        this._root.setNativeProps(nativeProps);
    }

    seek = (time) => {
        this.setNativeProps({ seek: time });
    };

    presentFullscreenPlayer = () => {
        this.setNativeProps({ fullscreen: true });
    };

    dismissFullscreenPlayer = () => {
        this.setNativeProps({ fullscreen: false });
    };

    _assignRoot = (component) => {
        this._root = component;
    };

    _onLoadStart = (event) => {
        if (this.props.onLoadStart) {
            this.props.onLoadStart(event.nativeEvent);
        }
    };

    _onLoad = (event) => {
        if (this.props.onLoad) {
            this.props.onLoad(event.nativeEvent);
        }
    };

    _onError = (event) => {
        if (this.props.onError) {
            this.props.onError(event.nativeEvent);
        }
    };

    _onProgress = (event) => {
        if (this.props.onProgress) {
            this.props.onProgress(event.nativeEvent);
        }
    };

    _onSeek = (event) => {
        if (this.props.onSeek) {
            this.props.onSeek(event.nativeEvent);
        }
    };

    _onEnd = (event) => {
        if (this.props.onEnd) {
            this.props.onEnd(event.nativeEvent);
        }
    };

    _onFullscreenPlayerWillPresent = (event) => {
        if (this.props.onFullscreenPlayerWillPresent) {
            this.props.onFullscreenPlayerWillPresent(event.nativeEvent);
        }
    };

    _onFullscreenPlayerDidPresent = (event) => {
        if (this.props.onFullscreenPlayerDidPresent) {
            this.props.onFullscreenPlayerDidPresent(event.nativeEvent);
        }
    };

    _onFullscreenPlayerWillDismiss = (event) => {
        if (this.props.onFullscreenPlayerWillDismiss) {
            this.props.onFullscreenPlayerWillDismiss(event.nativeEvent);
        }
    };

    _onFullscreenPlayerDidDismiss = (event) => {
        if (this.props.onFullscreenPlayerDidDismiss) {
            this.props.onFullscreenPlayerDidDismiss(event.nativeEvent);
        }
    };

    _onReadyForDisplay = (event) => {
        if (this.props.onReadyForDisplay) {
            this.props.onReadyForDisplay(event.nativeEvent);
        }
    };

    _onPlaybackStalled = (event) => {
        if (this.props.onPlaybackStalled) {
            this.props.onPlaybackStalled(event.nativeEvent);
        }
    };

    _onPlaybackResume = (event) => {
        if (this.props.onPlaybackResume) {
            this.props.onPlaybackResume(event.nativeEvent);
        }
    };

    _onPlaybackRateChange = (event) => {
        if (this.props.onPlaybackRateChange) {
            this.props.onPlaybackRateChange(event.nativeEvent);
        }
    };

    render() {
        const source = resolveAssetSource(this.props.source) || {};

        let uri = source.uri;
        if (uri && uri.match(/^\//)) {
            uri = `file://${uri}`;
        }

        const nativeProps = Object.assign({}, this.props);
        Object.assign(nativeProps, {
            style: [styles.base, nativeProps.style],
            uri: uri,
            src: {
                playMode: source.playMode || 0,
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
            },
            onVideoLoadStart: this._onLoadStart,
            onVideoLoad: this._onLoad,
            onVideoError: this._onError,
            onVideoProgress: this._onProgress,
            onVideoSeek: this._onSeek,
            onVideoEnd: this._onEnd,
            onVideoFullscreenPlayerWillPresent: this._onFullscreenPlayerWillPresent,
            onVideoFullscreenPlayerDidPresent: this._onFullscreenPlayerDidPresent,
            onVideoFullscreenPlayerWillDismiss: this._onFullscreenPlayerWillDismiss,
            onVideoFullscreenPlayerDidDismiss: this._onFullscreenPlayerDidDismiss,
            onReadyForDisplay: this._onReadyForDisplay,
            onPlaybackStalled: this._onPlaybackStalled,
            onPlaybackResume: this._onPlaybackResume,
            onPlaybackRateChange: this._onPlaybackRateChange,
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
    /* Native only */
    src: PropTypes.object,
    seek: PropTypes.number,

    uri: PropTypes.string,

    paused: PropTypes.bool,

    onLoadStart: PropTypes.func,
    onLoad: PropTypes.func,
    onError: PropTypes.func,
    onProgress: PropTypes.func,
    onSeek: PropTypes.func,
    onEnd: PropTypes.func,
    onFullscreenPlayerWillPresent: PropTypes.func,
    onFullscreenPlayerDidPresent: PropTypes.func,
    onFullscreenPlayerWillDismiss: PropTypes.func,
    onFullscreenPlayerDidDismiss: PropTypes.func,
    onReadyForDisplay: PropTypes.func,
    onPlaybackStalled: PropTypes.func,
    onPlaybackResume: PropTypes.func,
    onPlaybackRateChange: PropTypes.func,

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
        src: true,
        seek: true,
        fullscreen: true,
    },
});
