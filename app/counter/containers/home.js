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
    Dimensions,
} from 'react-native';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import Orientation from '../componets/RCTOrientation';
import UmengPush from '../componets/RCTUmengPush';

import * as calcActions from '../actions/calcAction';

import Counter from '../componets/Counter';
import PlayItem from '../componets/PlayItem';
import InfoItem from '../componets/InfoItem';

//取得屏幕宽高
const {height: SCREEN_HEIGHT, width: SCREEN_WIDTH} = Dimensions.get('window');

/**
 * 首页的根容器组件，负责控制首页内的木偶组件
 * @class Root
 * @extends {Component}
 */
class home extends Component {

    componentWillMount() {
        Orientation.setOrientation(Orientation.ORIENTATION_PORTRAIT);
        UmengPush.addReceiveMessageListener(this.handleRecvMessage);
        UmengPush.addOpenMessageListener(this.handleOpenMessage);
    }

    componentWillUnmount() {
        Orientation.setOrientation(Orientation.ORIENTATION_UNSPECIFIED);
        UmengPush.removeReceiveMessageListener(this.handleRecvMessage);
        UmengPush.removeOpenMessageListener(this.handleOpenMessage);
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

    //跳转到播放页
    skipToPlayer = (source) => {
        const {navigator} = this.props;
        // this.props.actions.play(source);
        this.props.navigator.push({ location: '/play/' + source, });
    }

    //跳转到推流页面
    skipToPush = (source) => {
        const {navigator} = this.props;
        // this.props.actions.play(source);
        this.props.navigator.push({ location: '/push/' + source, });
    }

    //加
    operatePlus = (data) => {
        this.props.actions.plus(data);
    }
    //减
    operateMinus = (data) => {
        this.props.actions.minus(data);
    }
    //乘
    operateTimes = (data) => {
        this.props.actions.times(data);
    }
    //除
    operateDivide = (data) => {
        this.props.actions.divide(data);
    }

    render() {
        const { value, navigator } = this.props;

        const img1 = "../asserts/images/lecloud.png";
        const img2 = "../asserts/images/rmb.jpg";
        const img3 = "../asserts/images/rmb.jpg";

        let plusPara = 1;
        let minusPara = 2;
        let timesPara = 2;
        let dividePara = 2;

        return (
            (this.props.getState) ?
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <Text>加载Store...</Text>
                </View>
                :
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <View style={{ flexDirection: 'column', width: SCREEN_WIDTH }} >
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <InfoItem imgUrl={img1} desc={'设备信息'} color={'green'} onViewInfo={() => navigator.push({ location: '/device' })} />
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `orange` }} onPress={() => navigator.push({ location: '/orient' })} >转屏</Text>
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `orange` }} onPress={() => navigator.push({ location: '/social' })} >分享</Text>
                            <Text style={{ fontSize: 18, fontWeight: 'bold', color: `black` }} onPress={() => navigator.push({ location: '/download' })} >下载</Text>
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
                            <PlayItem source={7} imgUrl={img2} desc={'云直播-流'} color={'red'} onPlay={this.skipToPlayer} />
                            <PlayItem source={3} imgUrl={img2} desc={'云点播-可下载'} color={'blue'} onPlay={this.skipToPlayer} />
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={1} imgUrl={img2} desc={'移动推流-地址'} color={'green'} onPlay={this.skipToPush} />
                            <PlayItem source={2} imgUrl={img2} desc={'移动推流-账号'} color={'green'} onPlay={this.skipToPush} />
                        </View>
                        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }} >
                            <PlayItem source={3} imgUrl={img2} desc={'云直播-推流'} color={'green'} onPlay={this.skipToPush} />
                        </View>
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
const mapStateToProps = state => ({
    //state.xxx必须与reducer同名
    value: state.calculate.value,
});


const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(calcActions, dispatch)
});

//连接Redux
export default connect(mapStateToProps, mapDispatchToProps)(home);