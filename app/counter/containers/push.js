/*************************************************************************
 * Description: 推流端示例
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-02-05
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

import Orientation from '../componets/RCTOrientation';
import Push, {
    PUSH_TYPE_MOBILE_URI,
    PUSH_TYPE_MOBILE,
    PUSH_TYPE_LECLOUD,
    PUSH_TYPE_NONE,
} from '../componets/RCTLePush';

//取得屏幕宽高
const {height: SCREEN_HEIGHT, width: SCREEN_WIDTH} = Dimensions.get('window');
const STATUS_BAR_HEIGHT = (Platform.OS === 'ios' ? 20 : 0);

class push extends Component {
    constructor(props) {
        super(props);
        this.state = {
            /** 推流参数 */
            target: -1,
            /* 开始/停止推流 */
            push: false,
            /* 窗口尺寸 */
            bottom: 0,
            /* 屏幕方向 */
            orientation: -1,
            /* 推流目标 */
            targetInfo: '',
            /* 推流操作 */
            state: 0,
            /* 推流计时*/
            time: -1,
            /* 推流计时标识*/
            timeFlag: false,
            /* 摄像头操作 */
            camera: 0,
            /* 摄像头切换标识*/
            cameraFlag: false,
            /* 摄像头方向：0为后置，1为前置*/
            cameraDirection: 1,
            /* 闪光灯操作 */
            flash: false,
            /* 闪光灯打开标识*/
            flashFlag: false,
            /* 滤镜操作 */
            filter: '滤镜',
            /* 摄像头操作 */
            volume: '音量',
        };
    }

    componentWillMount() {
        const { para } = this.props.params;
        let newTarget = '';
        switch (para) {
            case '1': //推流-有地址
                newTarget = { type: PUSH_TYPE_MOBILE_URI, url: "rtmp://216.mpush.live.lecloud.com/live/camerView", landscape: false };
                break;
            case '2': //推流-无地址
                newTarget = { type: PUSH_TYPE_MOBILE, domainName: "216.mpush.live.lecloud.com", streamName: '358239059415259', appkey: 'KIVK8X67PSPU9518B1WA', landscape: false };
                break;
            case '3': //推流-乐视云
                newTarget = { type: PUSH_TYPE_LECLOUD, activityId: "A2016120500000gx", userId: '800053', secretKey: '60ca65970dc1a15ad421d46f524b99b7', landscape: false };
                break;
            default:
                newTarget = { type: PUSH_TYPE_NONE };
        }
        this.setState({ target: newTarget, });

        Orientation.setOrientation(1);
        // Orientation.addOnOrientationListener(this.handleOrientation);
    }

    componentWillUnmount() {
        Orientation.setOrientation(1);
        // Orientation.removeOnOrientationListener(this.handleOrientation);
    }

    handleOrientation = (orientation) => {
        let bottom = 0;
        let needUpdate = false;
        switch (orientation) {
            case 0:
                bottom = 0;
                needUpdate = true;
                break;
            case 1:
                bottom = 0;
                needUpdate = true;
                break;
            case 8:
                bottom = 0;
                needUpdate = true;
                break;
            case 9:
                // bottom = 200;
                // needUpdate = true;
                break;
        }
        if (needUpdate) {
            this.setState({ orientation, bottom });
            Orientation.setOrientation(orientation);
        }
    }

    //跳转到播放页
    handleBack = () => {
        const {navigator} = this.props;
        navigator.pop();

        Orientation.setOrientation(1);
        Orientation.removeOnOrientationListener(this.handleOrientation);
    }


    displayPushState = () => {
        let {state} = this.state;
        console.log("displayPushState:", state);
        let dispControl;
        switch (state) {
            case 0: //CLOSED
                dispControl = '开始推流';
                break;
            case 1: //CONNECTING
                dispControl = '连接中…';
                break;
            case 2: //CONNECTED
                dispControl = '已连接';
                break;
            case 3: //OPENED
                dispControl = '关闭推流';
                break;
            case 4: //DISCONNECTING
                dispControl = '断开中…';
                break;
            case 5: //ERROR
                dispControl = '推流出错';
                break;
            case 6: //WARNING
                dispControl = '关闭推流';
                break;
        }
        return dispControl;
    }

    render() {
        return (
            <View style={styles.container}>
                <StatusBar barStyle='light-content' style={{ height: STATUS_BAR_HEIGHT }} />
                <Push style={styles.preview}
                    target={this.state.target}
                    push={this.state.push}
                    camera={this.state.camera}
                    flash={this.state.flash}
                    onPushTargetLoad={(data) => { this.setState({ targetInfo: `参数: ${data.para}\r\n推流地址: ${data.pushUrl}\r\n播放地址: ${data.playUrl}` }); }}
                    onPushStateUpdate={(data) => { this.setState({ state: data.state, timeFlag: data.timeFlag }); }}
                    onPushTimeUpdate={(data) => { this.setState({ time: data.time, timeFlag: data.timeFlag }); }}
                    onPushCameraUpdate={(data) => { this.setState({ cameraFlag: data.cameraFlag, cameraDirection: data.cameraDirection }); }}
                    onPushFlashUpdate={(data) => { this.setState({ flashFlag: data.flashFlag }); }}
                />
                <View style={styles.infoDisplays}>
                    <View style={styles.bufferDisplay}>
                        <Text style={styles.DisplayOption}>
                            {this.state.targetInfo}
                        </Text>
                    </View>
                    {this.state.timeFlag ?
                        <View style={styles.bufferDisplay}>
                            <Text style={[styles.DisplayOption, { fontSize: 15, }]}>
                                计时：{this.state.time}
                            </Text>
                        </View> : null}
                </View>
                <View style={styles.control}>
                    <TouchableOpacity disabled={this.state.state === 0 || this.state.state === 3 ? false : true} onPress={() => { this.setState({ push: !this.state.push }); }}>
                        <Text style={[styles.controlOption, { color: this.state.state === 0 ? 'green' : this.state.state === 3 ? 'red' : 'gray' }]} >{this.displayPushState()}</Text>
                    </TouchableOpacity>
                    <TouchableOpacity disabled={this.state.cameraFlag} onPress={() => { this.setState({ camera: this.state.camera + 1 }); }}>
                        <Text style={styles.controlOption} >{this.state.cameraFlag ? '请稍后' : '切换镜头'}</Text>
                    </TouchableOpacity>
                    <TouchableOpacity disabled={this.state.cameraDirection === 1 ? true : false} onPress={() => { this.setState({ flash: !this.state.flash }); }}>
                        <Text style={styles.controlOption} >{this.state.cameraDirection === 1 ? '无闪光' : this.state.flashFlag ? '关闪光' : '开闪光'}</Text>
                    </TouchableOpacity>
                    <Text style={styles.controlOption} >{this.state.filter}</Text>
                    <Text style={styles.controlOption} >{this.state.volume}</Text>
                </View>
            </View >
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'blue',
    },
    preview: {
        position: 'absolute',
        top: STATUS_BAR_HEIGHT,
        left: 0,
        bottom: 0,
        right: 0,
        backgroundColor: 'red',
    },
    control: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        backgroundColor: "transparent",
        borderRadius: 5,
        position: 'absolute',
        bottom: 50,
        left: 20,
        right: 20,
    },
    controlOption: {
        alignSelf: 'center',
        fontSize: 15,
        color: "white",
        paddingLeft: 2,
        paddingRight: 2,
        lineHeight: 15,
    },
    infoDisplays: {
        borderRadius: 4,
        overflow: 'hidden',
        position: 'absolute',
        top: STATUS_BAR_HEIGHT,
        left: 5,
        right: 5,
    },
    bufferDisplay: {
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
});


//配置Map映射表，拿到自己关心的数据
const mapStateToProps = state => ({
    //state.xxx必须与reducer同名
    // target: state.push.target,
});


const mapDispatchToProps = dispatch => ({
    // actions: bindActionCreators(playActions, dispatch)
});

//连接Redux
export default connect(mapStateToProps, mapDispatchToProps)(push);
