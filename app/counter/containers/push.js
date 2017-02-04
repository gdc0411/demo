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
            /* 窗口尺寸 */
            bottom: 0,
            /* 屏幕方向 */
            orientation: -1,
        };
    }

    componentWillMount() {
        const { para } = this.props.params;
        let newTarget = '';
        switch (para) {
            case '1': //推流-有地址
                newTarget = { type: 0, url: "rtmp://216.mpush.live.lecloud.com/live/camerView" };
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
                <Push style={[styles.fullScreen, { bottom: this.state.bottom, backgroundColor: 'red' }]}
                    target={this.state.target}
                />
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
