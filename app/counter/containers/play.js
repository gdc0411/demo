/*************************************************************************
 * Description: SDK组件示例
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
'use strict';

import React, {Component} from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
    Dimensions,
} from 'react-native';

//取得屏幕宽高
const {height: SCREEN_HEIGHT, width: SCREEN_WIDTH} = Dimensions.get('window');

import RCTLeVideoView from '../componets/RCTLeVideoView';

class VideoPlayer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            /* 暂停/播放状态 */
            paused: false,
            /* 跳转 */
            seek: 0,
            /* 播放音量 */
            volume: 1,
            /* 视频总长度 */
            duration: 0.0,
            /* 视频当前时间点 */
            currentTime: 0.0,
            /* 播放源信息 */
            sourceInfo: '',
            /* 事件信息 */
            eventInfo: '',
        };
    }

    /**
     * 获得视频当前播放百分比
     * @returns
     * @memberOf VideoPlayer
     */
    getCurrentTimePercentage() {
        if (this.state.currentTime > 0) {
            return parseFloat(this.state.currentTime) / parseFloat(this.state.duration);
        } else {
            return 0;
        }
    }


    /**
     * 渲染声音，设置声音
     * @param {any} volume 声音
     * @returns
     * @memberOf VideoPlayer
     */
    renderVolumeControl(volume) {
        const isSelected = (this.state.volume == volume);

        return (
            <TouchableOpacity onPress={() => { this.setState({ volume: volume }); } }>
                <Text style={[styles.controlOption, { fontWeight: isSelected ? "bold" : "normal" }]}>
                    {volume * 100}%
                </Text>
            </TouchableOpacity>
        );
    }

    render() {
        const flexCompleted = this.getCurrentTimePercentage() * 100;
        const flexRemaining = (1 - this.getCurrentTimePercentage()) * 100;

        //网络地址
        const uri = { uri: "http://cache.utovr.com/201601131107187320.mp4", pano: false, hasSkin: false };
        //标准点播
        //const vod = { playMode: 10000, uuid: "838389", vuid: "200271100", businessline: "102", saas: true, pano: false, hasSkin: false }; //Demo示例，有广告
        const vod = { playMode: 10000, uuid: "847695", vuid: "200323369", businessline: "102", saas: true, pano: false, hasSkin: false }; //乐视云测试数据
        //const vod = { playMode: 10000, uuid: "841215", vuid: "300184109", businessline: "102", saas: true, pano: false, hasSkin: false };  //川台数据
        //活动直播
        const live = { playMode: 10002, actionId: "A2016062700000gx", usehls: false, customerId: "838389", businessline: "102", cuid: "", utoken: "", pano: false, hasSkin: false };

        return (
            <View style={styles.container}>
                <TouchableOpacity style={styles.fullScreen} onPress={() => { this.setState({ paused: !this.state.paused }); } }>
                    <RCTLeVideoView style={styles.fullScreen}
                        source={vod}
                        paused={this.state.paused}
                        seek={this.state.seek}
                        onLoadSource={(data) => { this.setState({ sourceInfo: `视频源: ${data.src}` }); } }
                        onLoad={(data) => { this.setState({ duration: data.duration, eventInfo: 'Player准备完毕' }); } }
                        onProgress={(data) => { this.setState({ currentTime: data.currentTime, eventInfo: `播放中…… ${data.currentTime}/${data.duration}` }); } }
                        onStartBuffer={() => { this.setState({ eventInfo: '缓冲开始！' }); } }
                        onBufferPercent={(data) => { this.setState({ eventInfo: `缓冲中…… ${data.videobuff}%` }); } }
                        onEndBuffer={() => { this.setState({ eventInfo: '缓冲完毕！' }); } }
                        onStartRending={() => { this.setState({ eventInfo: '渲染第一帧……' }); } }
                        onSeek={(data) => { this.setState({ eventInfo: `跳转到……${data.currentTime}+${data.seekTime}` }); } }
                        onSeekComplete={(data) => { this.setState({ eventInfo: `跳转完毕！` }); } }
                        onPause={(data) => { this.setState({ eventInfo: `暂停…… ${data.currentTime}/${data.duration}` }); } }
                        onEnd={() => { this.setState({ eventInfo: '播放完毕！' }); } }
                        />
                </TouchableOpacity>

                <View style={styles.displays}>
                    <View style={styles.infoDisplays}>
                        <View style={styles.bufferDisplay}>
                            <Text style={styles.DisplayOption}>
                                {this.state.sourceInfo}
                            </Text>
                        </View>
                        <View style={styles.bufferDisplay}>
                            <Text style={[styles.DisplayOption, { color: 'green' }]}>
                                {this.state.eventInfo}
                            </Text>
                        </View>
                    </View>
                </View>

                <View style={styles.controls}>
                    <View style={styles.generalControls}>
                        <View style={styles.volumeControl}>
                            {this.renderVolumeControl(0.5) }
                            {this.renderVolumeControl(1) }
                            {this.renderVolumeControl(1.5) }
                        </View>
                    </View>

                    <View style={styles.trackingControls}>
                        <TouchableOpacity onPress={() => { this.setState({ seek: this.state.currentTime + 50 }); } }>
                            <View style={styles.progress}>
                                <View style={[styles.innerProgressCompleted, { flex: flexCompleted }]} />
                                <View style={[styles.innerProgressRemaining, { flex: flexRemaining }]} />
                            </View>
                        </TouchableOpacity>
                    </View>
                </View>
            </View >
        );
    }
}


const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'black',
    },
    fullScreen: {
        position: 'absolute',
        top: 0,
        left: 0,
        bottom: 0,
        right: 0,
    },
    controls: {
        backgroundColor: "transparent",
        borderRadius: 5,
        position: 'absolute',
        bottom: 20,
        left: 20,
        right: 20,
    },
    displays: {
        backgroundColor: "transparent",
        borderRadius: 5,
        position: 'absolute',
        bottom: 60,
        left: 20,
        right: 20,
    },
    progress: {
        flex: 1,
        flexDirection: 'row',
        borderRadius: 3,
        overflow: 'hidden',
    },
    innerProgressCompleted: {
        height: 20,
        backgroundColor: '#cccccc',
    },
    innerProgressRemaining: {
        height: 20,
        backgroundColor: '#2C2C2C',
    },
    infoDisplays: {
        flex: 1,
        flexDirection: 'column',
        borderRadius: 4,
        overflow: 'hidden',
        paddingBottom: 15,
    },
    bufferDisplay: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'center',
    },
    DisplayOption: {
        alignSelf: 'center',
        fontSize: 11,
        color: "white",
        paddingLeft: 2,
        paddingRight: 2,
        lineHeight: 12,
    },
    generalControls: {
        flex: 1,
        flexDirection: 'row',
        borderRadius: 4,
        overflow: 'hidden',
        paddingBottom: 10,
    },
    rateControl: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'center',
    },
    volumeControl: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'center',
    },
    resizeModeControl: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    controlOption: {
        alignSelf: 'center',
        fontSize: 11,
        color: "white",
        paddingLeft: 2,
        paddingRight: 2,
        lineHeight: 12,
    },
});


export default VideoPlayer;