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
import Push from '../componets/RCTLePush';

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
            pushed: false,
            /* 窗口尺寸 */
            bottom: 0,
            /* 屏幕方向 */
            orientation: -1,
            /* 推流目标 */
            targetInfo: '',
            /* 推流操作 */
            pushState: '开始推流',
            /* 推流计时*/
            time: -1,
        };
    }

    componentWillMount() {
        const { para } = this.props.params;
        let newTarget = '';
        switch (para) {
            case '1': //推流-有地址
                newTarget = { type: 0, url: "rtmp://216.mpush.live.lecloud.com/live/camerView", landscape: false };
                break;
            case '2': //推流-无地址
                break;
            case '3': //推流-乐视云
                break;
        }
        this.setState({ target: newTarget, });

        Orientation.setOrientation(1);
        Orientation.addOnOrientationListener(this.handleOrientation);
    }

    componentWillUnmount() {
        Orientation.setOrientation(1);
        Orientation.removeOnOrientationListener(this.handleOrientation);
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

    render() {
        return (
            <View style={styles.container}>
                <StatusBar barStyle='light-content' style={{ height: STATUS_BAR_HEIGHT }} />
                <Push style={styles.preview}
                    target={this.state.target}
                    pushed={this.state.pushed}
                    onPushTargetLoad={(data) => { this.setState({ targetInfo: `参数: ${data.para}` }); }}
                    onPushOperate={(data) => { this.setState({ pushed: data.pushed, pushState: data.pushed ? '停止推流' : '开始推流' }); }}
                    onPushTimeUpdate={(data) => { this.setState({ time: data.time }); }}
                />
                <View style={styles.infoDisplays}>
                    <View style={styles.bufferDisplay}>
                        <Text style={styles.DisplayOption}>
                            {this.state.targetInfo}
                        </Text>
                    </View>
                    <View style={styles.bufferDisplay}>
                        <Text style={styles.DisplayOption}>
                           计时：{this.state.time}
                        </Text>
                    </View>
                </View>
                <View style={styles.control}>
                    <Text style={styles.controlOption} onPress={() => { this.setState({ pushed: !this.state.pushed }); }} >{this.state.pushState}</Text>
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
        flexDirection: 'column',
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
        color: "blue",
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
