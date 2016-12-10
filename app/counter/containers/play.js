/*************************************************************************
 * Description: SDK组件示例
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2016-10-30
 ************************************************************************/
'use strict';

import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
    StatusBar,
    Dimensions,
    Platform,
} from 'react-native';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as playActions from '../actions/playAction';

import Orientation from '../componets/RCTOrientation';
import Video from '../componets/RCTLeVideo';

//取得屏幕宽高
const {height: SCREEN_HEIGHT, width: SCREEN_WIDTH} = Dimensions.get('window');
const STATUS_BAR_HEIGHT = (Platform.OS === 'ios' ? 20 : 0);

class play extends Component {
    constructor(props) {
        super(props);
        this.state = {
            /** 数据源 */
            source: -1,
            /* 视频真实尺寸 */
            width: -1,
            height: -1,
            /* 窗口尺寸 */
            bottom: 200,
            /* 屏幕方向 */
            orientation: -1,
            /* 暂停/播放状态 */
            paused: false,
            /* 跳转 */
            seek: -1,
            /* 播放码率 */
            rate: '',
            /* 云直播机位切换 */
            live: '',
            /* 播放音量 */
            volume: -1,
            /* 屏幕亮度 */
            brightness: -1,
            /* 视频总长度 */
            duration: 0,
            /* 视频当前时间点 */
            currentTime: 0,
            /* 视频已缓冲百分比 */
            buffPercent: 0,
            /* 是否点击广告 */
            clickAd: false,

            /* 播放源信息 */
            sourceInfo: '',
            /* 视频信息 */
            videoInfo: '',
            /* 码率列表信息 */
            ratesInfo: [],
            /* 媒资信息 */
            mediaInfo: '',
            /* 事件信息 */
            eventInfo: '',
            /* 广告信息 */
            advertInfo: '',
            /* 出错信息 */
            errorInfo: '',
        };

        // this.onLoad = this.onLoad.bind(this);
    }


    componentWillMount() {
        //alert(this.props.datasource);
        // var today = new Date();
        // alert("The time is: " + today.toString());
        // setTimeout("showTime()", 5000);

        const { src } = this.props.params;
        const newSource = this.getFormatDatasource(src);
        this.setState({ source: newSource, });
        Orientation.unlockAllOrientations();
    }

    componentWillUnmount() {
        Orientation.lockToPortrait();
    }
    

    /**
     * 获得视频和播放器初始信息
     * @param {any} data
     * @memberOf VideoPlayer
     */
    onLoad(data) {

        let ratesStr = '';
        // alert(this.props.params.source);

        let arr = data.rateList; //获得码率列表
        // alert(this.state.ratesInfo);
        if (arr instanceof Array && arr.length > 0) {
            for (var i = 0; i < arr.length; i++) {
                ratesStr += `{${arr[i].rateKey},${arr[i].rateValue}}`;
            }
        }

        //获得直播信息
        let livesStr = '';
        // alert(this.state.ratesInfo);
        if (data.actionLive !== undefined) {
            livesStr += `{actionState:${data.actionLive.actionState},currentLive:${data.actionLive.currentLive}`;
            // livesStr += `{actionState:${data.actionLive.actionState},coverImgUrl:${data.actionLive.coverImgUrl},playerPageUrl:${data.actionLive.playerPageUrl}`;
            // livesStr += `,beginTime:${data.actionLive.beginTime},startTime:${data.actionLive.startTime}`;
            let liveArr = data.actionLive.lives;
            if (liveArr instanceof Array && liveArr.length > 0) {
                for (var i = 0; i < liveArr.length; i++) {
                    livesStr += `{liveId:${liveArr[i].liveId},machine:${liveArr[i].machine},previewSteamId:${liveArr[i].previewSteamId},liveStatus:${liveArr[i].liveStatus}}`;
                }
            }
            livesStr += '}';
        }

        // 查看使用可以把下面注释去掉 raojia 2016/11/3
        // //logo
        // alert('logo pic:'+data.logo.pic);
        // alert('logo target:'+data.logo.target);
        // alert('logo pos:'+data.logo.pos);
        // //loading
        // alert('loading pic:'+data.loading.pic);
        // alert('loading target:'+data.loading.target);
        // alert('loading pos:'+data.loading.pos);
        // //waterMark
        // alert('水印 pic:'+(data.waterMarks)[0].pic);
        // alert('水印 target:'+(data.waterMarks)[0].target);
        // alert('水印 pos:'+(data.waterMarks)[0].pos);

        // alert('音量：' + data.volume);
        // alert('亮度：' + data.brightness);

        this.setState({
            duration: data.duration,
            width: data.naturalSize.width,
            height: data.naturalSize.height,
            ratesInfo: data.rateList,
            eventInfo: 'Player准备完毕',
            videoInfo: `片名：${data.title} 长度：${data.duration} 宽高:${data.naturalSize.width}，${data.naturalSize.height} \n`
            + `码率：${ratesStr} 默认：${data.defaultRate} \n音量：${data.volume} 亮度:${data.brightness} \n ${livesStr}`
        });
    }

    /**
     * 渲染码率控件，设置码率
     * @param {any} volume 码率
     * @returns
     * @memberOf VideoPlayer
     */
    renderRateControl(rate) {
        const isSelected = (this.state.rate == rate);
        let rateName = '';
        switch (rate) {
            case '21':
                rateName = ' 标清 ';
                break;
            case '13':
                rateName = ' 高清 ';
                break;
            case '22':
                rateName = ' 超清 ';
                break;
            default:
                break;
        }
        // if( this.state.ratesInfo.length > 0 ) alert(this.state.ratesInfo );
        return (
            <TouchableOpacity onPress={() => { this.setState({ rate: rate }); } }>
                <Text style={[styles.controlOption, { fontWeight: isSelected ? "bold" : "normal" }]}>
                    {rateName}
                </Text>
            </TouchableOpacity>
        );
    }

    /**
   * 渲染机位控件，设置机位
   * @param {any} volume 码率
   * @returns
   * @memberOf VideoPlayer
   */
    renderLiveControl(live) {
        const isSelected = (this.state.live == live);
        let liveName = '机位1';
        // if( this.state.ratesInfo.length > 0 ) alert(this.state.ratesInfo );
        return (
            <TouchableOpacity onPress={() => { this.setState({ live: live }); } }>
                <Text style={[styles.controlOption, { fontWeight: isSelected ? "bold" : "normal" }]}>
                    {liveName}
                </Text>
            </TouchableOpacity>
        );
    }

    renderOrientationControl(orientation) {
        const isSelected = (this.state.orientation == orientation);
        let dispName = '';
        let scrBottom = 0;
        switch (orientation) {
            case 0:
                dispName = ' 正横屏 ';
                scrBottom = 0;
                break;
            case 1:
                dispName = ' 正竖屏 ';
                scrBottom = 200;
                break;
            case 8:
                dispName = ' 反横屏 ';
                scrBottom = 0;
                break;
            case 9:
                dispName = ' 反竖屏 ';
                scrBottom = 200;
                break;
        }

        // if( this.state.ratesInfo.length > 0 ) alert(this.state.ratesInfo );
        return (
            <TouchableOpacity onPress={() => { this.setState({ orientation: orientation, bottom: scrBottom }); } }>
                <Text style={[styles.controlOption, { fontWeight: isSelected ? "bold" : "normal" }]}>
                    {dispName}
                </Text>
            </TouchableOpacity>
        );
    }

    renderVolumeControl(volume) {
        const isSelected = (this.state.volume == volume);
        return (
            <TouchableOpacity onPress={() => { this.setState({ volume: volume }); } }>
                <Text style={[styles.controlOption, { fontWeight: isSelected ? "bold" : "normal" }]}>
                    {volume}%
                </Text>
            </TouchableOpacity>
        );
    }

    renderBrightnessControl(brightness) {
        const isSelected = (this.state.brightness == brightness);
        return (
            <TouchableOpacity onPress={() => { this.setState({ brightness: brightness }); } }>
                <Text style={[styles.controlOption, { fontWeight: isSelected ? "bold" : "normal" }]}>
                    {brightness}%
                </Text>
            </TouchableOpacity>
        );
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

    getFormatDatasource(key) {
        let source;
        switch (key) {
            case '1': //点播 乐视云测试数据
                source = { playMode: 10000, uuid: "847695", vuid: "200323369", businessline: "102", saas: true, pano: false, hasSkin: false };
                break;
            case '2': //点播 川台数据
                source = { playMode: 10000, uuid: "841215", vuid: "300184109", businessline: "102", saas: true, pano: false, hasSkin: false };
                break;
            case '3': //点播  川台数据,艳秋提供带广告
                source = { playMode: 10000, uuid: "819108", vuid: "200644549", businessline: "102", saas: true, pano: false, hasSkin: false };
                break;
            case '4': //点播 Demo示例，有广告
                source = { playMode: 10000, uuid: "838389", vuid: "200271100", businessline: "102", saas: true, pano: false, hasSkin: false };
                break;
            case '5': //活动直播 官方demo
                source = { playMode: 10002, actionId: "A2016062700000gx", usehls: false, customerId: "838389", businessline: "102", cuid: "", utoken: "", pano: false, hasSkin: false };
                break;
            case '6': //活动直播 泸州
                source = { playMode: 10002, actionId: "A2016111100001zn", usehls: false, customerId: "865024", businessline: "102", cuid: "", utoken: "", pano: false, hasSkin: false };
                break;
            case '7': //活动直播 自己推流
                source = { playMode: 10002, actionId: "A2016111200001as", usehls: false, customerId: "", businessline: "", cuid: "", utoken: "", pano: false, hasSkin: false };
                break;
            default: //网络或本地地址
                source = { playMode: 0, uri: "http://cache.utovr.com/201601131107187320.mp4", pano: false, hasSkin: false };
                break;
        }
        // alert(source);
        return source;
    }

    //跳转到播放页
    handleBack = () => {
        const {navigator} = this.props;
        // this.props.actions.play(source);
        navigator.pop();
    }

    getLocalTime = (timestamp) => {
        let d = new Date(timestamp * 1000);    //根据时间戳生成的时间对象
        let date = (d.getMonth() + 1) + "月" +
            (d.getDate()) + "日" +
            (d.getHours()) + ":" +
            (d.getMinutes()) + ":" +
            (d.getSeconds());
        // return new Date(parseInt(timestamp) * 1000).toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
        return date;
    }

    render() {
        const flexCompleted = this.getCurrentTimePercentage() * 100;
        const flexRemaining = (1 - this.getCurrentTimePercentage()) * 100;

        return (
            <View style={styles.container}>
                <StatusBar barStyle='light-content' style={{ height: STATUS_BAR_HEIGHT }} />
                <TouchableOpacity style={[styles.fullScreen, { bottom: this.state.bottom }]} onPress={() => { this.setState({ paused: !this.state.paused }); } }>
                    <Video style={[styles.fullScreen, { bottom: this.state.bottom }]}
                        source={this.state.source}
                        seek={this.state.seek}
                        rate={this.state.rate}
                        orientation={this.state.orientation}
                        volume={this.state.volume}
                        brightness={this.state.brightness}
                        paused={this.state.paused}
                        live={this.state.live}
                        clickAd={this.state.clickAd}
                        onVideoSourceLoad={(data) => { this.setState({ sourceInfo: `视频源: ${data.src}` }); } }
                        onVideoLoad={(data) => this.onLoad(data) }
                        onVideoProgress={(data) => { this.setState({ currentTime: data.currentTime, duration: data.duration, eventInfo: `播放中…… ${data.currentTime}/${data.duration}` }); } }
                        onVideoBufferPercent={(data) => { this.setState({ buffPercent: data.bufferpercent }); } }
                        onBufferStart={() => { this.setState({ eventInfo: '缓冲开始！' }); } }
                        onBufferPercent={(data) => { this.setState({ eventInfo: `${(data.videobuff) ? '缓冲中……' + data.videobuff + '%' : ''}` }); } }
                        onBufferEnd={() => { this.setState({ eventInfo: '缓冲完毕！' }); } }
                        onVideoRendingStart={() => { this.setState({ eventInfo: '渲染第一帧……' }); } }
                        onVideoSeek={(data) => { this.setState({ eventInfo: `跳转到……${data.currentTime}+${data.seekTime}` }); } }
                        onVideoSeekComplete={(data) => { this.setState({ eventInfo: `跳转完毕！` }); } }
                        onVideoPause={(data) => { this.setState({ paused: true, eventInfo: `暂停…… ${(data.beginTime) ? this.getLocalTime(data.beginTime) + '/' + this.getLocalTime(data.currentTime) + '/' + this.getLocalTime(data.serverTime) : data.currentTime + '/' + data.duration}` }); } }
                        onVideoResume={(data) => { this.setState({ paused: false, eventInfo: `恢复播放……  ${(data.beginTime) ? this.getLocalTime(data.beginTime) + '/' + this.getLocalTime(data.currentTime) + '/' + this.getLocalTime(data.serverTime) : data.currentTime + '/' + data.duration}` }); } }
                        onVideoEnd={() => { this.setState({ eventInfo: '播放完毕！' }); } }
                        onAdvertStart={() => { this.setState({ advertInfo: '广告开始！' }); } }
                        onAdvertProgress={(data) => { this.setState({ advertInfo: `广告播放中……倒计时${data.AdTime}` }); } }
                        onAdvertComplete={() => { this.setState({ advertInfo: `广告结束！` }); } }
                        onAdvertClick={() => { this.setState({ advertInfo: `广告点击了！！` }); } }
                        onVideoRateLoad={(data) => { this.setState({ eventInfo: `码率切换:${data.currentRate} 到 ${data.nextRate}` }); } }
                        onActionLiveChange={(data) => { this.setState({ eventInfo: `机位切换:${data.currentLive} 到 ${data.nextLive}` }); } }
                        onActionTimeShift={(data) => { this.setState({ currentTime: data.currentTime, eventInfo: `播放中…… ${this.getLocalTime(data.beginTime)}/${this.getLocalTime(data.currentTime)}/${this.getLocalTime(data.serverTime)}` }); } }
                        onActionStatusChange={(data) => { this.setState({ eventInfo: `活动状态变化…… ${data.actionState}/${data.actionId}/${data.beginTime}/${data.endTime}` }); } }
                        onActionOnlineNumChange={(data) => { this.setState({ eventInfo: `在线人数变化…… ${data.onlineNum}` }); } }
                        onVideoError={(data) => { this.setState({ errorInfo: `出错啦！状态码：${data.statusCode} 错误码：${data.errorCode} 错误：${data.errorMsg} 事件：${data.what}` }); } }
                        />
                </TouchableOpacity>
                {/*onOrientationChange={(data) => { this.setState({ orientation: data.orientation }); }}*/}

                <View style={styles.displays}>
                    <View style={{flex: 1, flexDirection: 'row', justifyContent: 'center', paddingBottom: 20}}>
                        <Text style={styles.controlOption} onPress={this.handleBack} > 返 回 </Text>
                        <Text style={styles.controlOption} onPress={() => { this.setState({ seek: this.state.currentTime + 30 }); } } > + 30s </Text>
                        <Text style={styles.controlOption} onPress={() => { this.setState({ seek: this.state.currentTime - 30 }); } } > - 30s </Text>
                    </View>
                    <View style={styles.infoDisplays}>
                        <View style={styles.bufferDisplay}>
                            <Text style={styles.DisplayOption}>
                                {this.state.sourceInfo}
                            </Text>
                        </View>
                        <View style={styles.bufferDisplay}>
                            <Text style={[styles.DisplayOption, { color: 'yellow' }]}>
                                {this.state.videoInfo}
                            </Text>
                        </View>
                        <View style={styles.bufferDisplay}>
                            <Text style={[styles.DisplayOption, { color: 'cyan' }]}>
                                {this.state.eventInfo}已缓冲{this.state.buffPercent}%
                            </Text>
                        </View>
                        <View style={styles.bufferDisplay}>
                            <TouchableOpacity onPress={() => { this.setState({ clickAd: true }); } }>
                                <Text style={[styles.DisplayOption, { color: 'green' }]}>
                                    {this.state.advertInfo}
                                </Text>
                            </TouchableOpacity>
                        </View>
                        <View style={styles.bufferDisplay}>
                            <Text style={[styles.DisplayOption, { color: 'red' }]}>
                                {this.state.errorInfo}
                            </Text>
                        </View>
                    </View>
                </View>

                <View style={styles.controls}>
                    <View style={styles.volumeControl}>
                        {this.renderOrientationControl(0)}
                        {this.renderOrientationControl(1)}
                        {this.renderOrientationControl(8)}
                        {this.renderOrientationControl(9)}
                    </View>
                    {/*
                        <View style={styles.volumeControl}>
                            {this.renderLiveControl('201611113000002it') }
                        </View>
                    */}
                    <View style={styles.volumeControl}>
                        {this.renderRateControl('21')}
                        {this.renderRateControl('13')}
                        {this.renderRateControl('22')}
                    </View>

                    <View style={styles.volumeControl}>
                        {this.renderVolumeControl(20)}
                        {this.renderVolumeControl(50)}
                        {this.renderVolumeControl(100)}
                    </View>

                    <View style={styles.volumeControl}>
                        {this.renderBrightnessControl(20)}
                        {this.renderBrightnessControl(50)}
                        {this.renderBrightnessControl(100)}
                    </View>
                    <View style={styles.trackingControls}>
                        <View style={styles.progress}>
                            <View style={[styles.innerProgressCompleted, { flex: flexCompleted }]} />
                            <View style={[styles.innerProgressRemaining, { flex: flexRemaining }]} />
                        </View>
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
        top: STATUS_BAR_HEIGHT,
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
        bottom: 100,
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
    innerProgressBuffered: {
        height: 20,
        backgroundColor: '#cfe2f3',
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
        paddingBottom: 20,
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
        paddingBottom: 20,
        lineHeight: 12,
    },
    generalControls: {
        flex: 1,
        flexDirection: 'row',
        borderRadius: 4,
        overflow: 'hidden',
        paddingBottom: 20,
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
        paddingBottom: 20,
    },
    resizeModeControl: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    controlOption: {
        alignSelf: 'center',
        fontSize: 13,
        color: "white",
        paddingLeft: 2,
        paddingRight: 2,
        lineHeight: 13,
    },
});


//配置Map映射表，拿到自己关心的数据
const mapStateToProps = state => ({
    //state.xxx必须与reducer同名
    datasource: state.play.datasource,
});


const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(playActions, dispatch)
});

//连接Redux
export default connect(mapStateToProps, mapDispatchToProps)(play);
