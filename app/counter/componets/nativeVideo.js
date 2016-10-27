import React, {PropTypes, Component} from "react";
import {requireNativeComponent, View, UIManager, findNodeHandle} from "react-native";

var RCT_VIDEO_REF = 'VideoView';
var NativeVodVideoView = requireNativeComponent('LeVideoView', VideoView, { nativeOnly: { onChange: true } });

export default class VideoView extends Component {
    constructor(props) {
        super(props);
    }
    resultStr;
    render() {
        //return <RCTVideoView {...this.props} onChange={this._onChange.bind(this)}/>;
        return <NativeVodVideoView
            {...this.props}
            ref = {RCT_VIDEO_REF}
            //onPrepared={this._onPrepared.bind(this)}
            // onError={this._onError.bind(this)}
            // onBufferUpdate={this._onBufferUpdate.bind(this)}
            // onProgress={this._onProgress.bind(this)}
            />;
    };
}