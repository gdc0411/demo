/**
 * Redux Demo 01
 * 应用中所有的 state 都以一个对象树的形式储存在一个单一的 store 中。
 * 惟一改变 state 的办法是触发 action，一个描述发生什么的对象。
 * 为了描述 action 如何改变 state 树，你需要编写 reducers。
 */
'use strict';

import React, { Component, PropTypes } from 'react';
import {
    View,
    Text,
    Image,
    TouchableOpacity,
    Switch,
    Dimensions,
} from 'react-native';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import * as deviceActions from '../actions/deviceAction';

import Orientation from '../componets/RCTOrientation';
import UmengPush from '../componets/RCTUmengPush';

import * as calcActions from '../actions/calcAction';
import * as settingActions from '../actions/settingAction';

import Counter from '../componets/Counter';
import PlayItem from '../componets/PlayItem';
import InfoItem from '../componets/InfoItem';

//取得屏幕宽高
const { height: SCREEN_HEIGHT, width: SCREEN_WIDTH } = Dimensions.get('window');

const img1 = "../asserts/images/lecloud.png";
const img2 = "../asserts/images/rmb.jpg";
const img3 = "../asserts/images/rmb.jpg";
const plusPara = 1;
const minusPara = 2;
const timesPara = 2;
const dividePara = 2;

/**
 * 首页的根容器组件，负责控制首页内的木偶组件
 * @class Root
 * @extends {Component}
 */
class home extends Component {

    constructor(props) {
        super(props);
        this.props.settingActions.loadConfig("LEDEMO_CFG");
    }

    componentWillMount() {
        Orientation.setOrientation(Orientation.ORIENTATION_PORTRAIT);
        UmengPush.addReceiveMessageListener(this.handleRecvMessage);
        UmengPush.addOpenMessageListener(this.handleOpenMessage);
        // this.isPushEnabled();
        // this.switchPush(true);
    }

    componentWillUnmount() {
        Orientation.setOrientation(Orientation.ORIENTATION_UNSPECIFIED);
        UmengPush.removeReceiveMessageListener(this.handleRecvMessage);
        UmengPush.removeOpenMessageListener(this.handleOpenMessage);
    }

    componentDidMount() {
        this.props.deviceActions.fetchPostsIfNeeded(this.props.selectedDevice);
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.selectedDevice !== this.props.selectedDevice) {
            const { selectedDevice } = nextProps;
            this.props.deviceActions.fetchPostsIfNeeded(selectedDevice);
        }
    }

    handleRecvMessage = (message) => {
        console.log("onUmengReceiveMessage:", message);
        alert('onUmengReceiveMessage' + JSON.stringify(message));
    }

    handleOpenMessage = (message) => {
        console.log("onUmengOpenMessage:", message);
        // alert('onUmengOpenMessage' + JSON.stringify(message));
        if (message.extra && message.extra.uri) {
            this.props.navigator.push({ location: message.extra.uri });
        }
    }

    isPushEnabled = () => {
        UmengPush.isPushEnabled()
            .then((isEnabled) => {
                if (isEnabled) {
                    alert('推送开启');
                } else {
                    alert('推送关闭');
                }
            });
    }

    switchPush = (enable) => {

        let config_data = this.props.setting;
        UmengPush.switchPush(enable)
            .then((result) => {
                if (result) {
                    enable ? alert('推送开启成功') : alert('推送关闭成功');
                    config_data.acceptPush = enable ? true : false;
                    // alert(JSON.stringify(config_data));
                    this.props.settingActions.setConfig("LEDEMO_CFG", config_data);
                } else {
                    enable ? alert('推送开启失败') : alert('推送关闭失败');
                }
            }).catch((error) => {
                alert('接口请求失败！');
            });
    }

    //跳转到播放页
    skipToPlayer = (source) => {
        const { navigator } = this.props;
        // this.props.actions.play(source);
        this.props.navigator.push({ location: '/play/' + source, });
    }

    //跳转到推流页面
    skipToPush = (source) => {
        const { navigator } = this.props;
        // this.props.actions.play(source);
        this.props.navigator.push({ location: '/push/' + source, });
    }

    //加
    operatePlus = (data) => {
        this.props.calcActions.plus(data);
    }
    //减
    operateMinus = (data) => {
        this.props.calcActions.minus(data);
    }
    //乘
    operateTimes = (data) => {
        this.props.calcActions.times(data);
    }
    //除
    operateDivide = (data) => {
        this.props.calcActions.divide(data);
    }

    render() {
        const { value, navigator, setting } = this.props;

        return (
            (this.props.getState) ?
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <Text>加载Store...</Text>
                </View>
                :
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <View style={{ flexDirection: 'column', width: SCREEN_WIDTH }} >
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <InfoItem imgUrl={img1} desc={'设备'} color={'green'} onViewInfo={() => navigator.push({ location: '/device' })} />
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `orange` }} onPress={() => navigator.push({ location: '/orient' })} >转屏</Text>
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `orange` }} onPress={() => navigator.push({ location: '/social' })} >分享</Text>
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `black` }} onPress={() => navigator.push({ location: '/download' })} >下载</Text>
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `black` }} onPress={() => navigator.push({ location: '/picker' })} >相册</Text>
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `red` }} onPress={() => navigator.push({ location: '/pay' })} >支付</Text>
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={0} imgUrl={img1} desc={'第三方URL'} color={'black'} onPlay={this.skipToPlayer} />
                            <PlayItem source={1} imgUrl={img2} desc={'云点播-长片'} color={'blue'} onPlay={this.skipToPlayer} />
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={2} imgUrl={img2} desc={'云点播-短片'} color={'blue'} onPlay={this.skipToPlayer} />
                            <PlayItem source={4} imgUrl={img2} desc={'云点播-有广告'} color={'blue'} onPlay={this.skipToPlayer} />
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={5} imgUrl={img2} desc={'云直播-Demo'} color={'red'} onPlay={this.skipToPlayer} />
                            <PlayItem source={6} imgUrl={img2} desc={'云直播-泸州'} color={'red'} onPlay={this.skipToPlayer} />
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={7} imgUrl={img2} desc={'云直播-推流'} color={'red'} onPlay={this.skipToPlayer} />
                            <PlayItem source={3} imgUrl={img2} desc={'云点播-可下载'} color={'blue'} onPlay={this.skipToPlayer} />
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={1} imgUrl={img2} desc={'移动推流-地址'} color={'green'} onPlay={this.skipToPush} />
                            <PlayItem source={8} imgUrl={img2} desc={'移动直播-播放'} color={'green'} onPlay={this.skipToPlayer} />
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={2} imgUrl={img2} desc={'移动推流-账号'} color={'green'} onPlay={this.skipToPush} />
                            <PlayItem source={3} imgUrl={img2} desc={'移动推流-云直播'} color={'green'} onPlay={this.skipToPush} />
                        </View>
                    </View>
                    <View style={{ flexDirection: 'row', alignItems: 'center' }} >
                        <Text>{'是否接收推送'}</Text>
                        <Switch value={setting.acceptPush} onValueChange={(value) => {
                            {/*this.setState({ pushEnable: value });*/ }
                            this.switchPush(value);
                        }} />
                    </View>

                    {/*
                    <Counter value={value} para={plusPara} oper={`加`} onChange={this.operatePlus} />
                    <Counter value={value} para={minusPara} oper={`减`} onChange={this.operateMinus} />
                    <Counter value={value} para={timesPara} oper={`乘`} onChange={this.operateTimes} />
                    <Counter value={value} para={dividePara} oper={`除`} onChange={this.operateDivide} />
                    */}
                </View >
        );
    }
}

//配置Map映射表，拿到自己关心的数据
const mapStateToProps = state => {
    const { selectedDevice, postsByDevice } = state;
    const {
        isFetching,
        lastUpdated,
        items: posts
    } = postsByDevice[selectedDevice] || {
        isFetching: true,
        items: {},
    };
    const value = state.calculate.value;
    const setting = state.setting;
    return {
        selectedDevice,
        posts,
        isFetching,
        lastUpdated,
        value,
        setting,
    };
};

const mapDispatchToProps = dispatch => ({
    deviceActions: bindActionCreators(deviceActions, dispatch),
    calcActions: bindActionCreators(calcActions, dispatch),
    settingActions: bindActionCreators(settingActions, dispatch),
});

//连接Redux
export default connect(mapStateToProps, mapDispatchToProps)(home);