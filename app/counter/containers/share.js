
/**
 * 查看Device信息
 */
'use strict';

import React, { Component, PropTypes } from 'react';
import {
    StyleSheet,
    Text,
    View,
    TextInput,
    Dimensions,
    Image,
    TouchableOpacity,
    Modal,
    TouchableHighlight
} from 'react-native';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import * as WeChat from '../componets/RCTWechatAPI';

const {width, height} = Dimensions.get('window');

class share extends Component {
    constructor(props) {
        super(props);
        this.state = {
            apiVersion: 'waiting...',
            isWXAppSupportApi: 'waiting...',
            isWXAppInstalled: 'waiting...',
            callbackStr: '',
        };
    }
    //跳转到上一页
    handleBack = () => {
        const {navigator} = this.props;
        navigator.pop();
    }

    async componentDidMount() {
        try {
            this.setState({
                apiVersion: await WeChat.getApiVersion(),
                isWXAppSupportApi: await WeChat.isWXAppSupportApi(),
                isWXAppInstalled: await WeChat.isWXAppInstalled()
            });
            console.log(this.state);
        } catch (e) {
            console.error(e);
        }
        console.log(WeChat);
        // console.log('getApiVersion', typeof WeChat.getApiVersion);
        // console.log('getWXAppInstallUrl', typeof WeChat.getWXAppInstallUrl);
        // console.log('sendRequest', typeof WeChat.sendRequest);
        // console.log('registerApp', typeof WeChat.registerApp);
        // console.log('sendErrorCommonResponse', typeof WeChat.sendErrorCommonResponse);
        // console.log('sendErrorUserCancelResponse', typeof WeChat.sendErrorUserCancelResponse);
        // console.log('sendAuthRequest', typeof WeChat.sendAuthRequest);
        // console.log('getWXAppInstallUrl', typeof WeChat.getWXAppInstallUrl);
        // console.log('openWXApp', typeof WeChat.openWXApp);
        // console.log('registerAppWithDescription', typeof WeChat.registerAppWithDescription);
        // console.log('isWXAppSupportApi', typeof WeChat.isWXAppSupportApi);
        // console.log('isWXAppInstalled', typeof WeChat.isWXAppInstalled);
    }

    //微信登陆
    loginToWeixin = () => {
        WeChat.isWXAppInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    WeChat.sendAuth({
                        config: { scope: 'snsapi_userinfo', }
                    }).catch(error => {
                        console.log(error.message);
                    }).then(resp => {
                        console.log(resp);
                        if (resp && resp.errCode == 0) {
                            WeChat.getToken(resp)
                                .then(json => {
                                    console.log(json);
                                    this.setState({
                                        callbackStr: JSON.stringify(json)
                                    });
                                });
                        } else {
                            this.setState({
                                callbackStr: JSON.stringify(resp)
                            });
                        }
                    });

                } else {
                    console.log('没有安装微信，请您安装微信之后再试');
                }
            });
    }

    //分享给朋友
    shareToFrends = () => {
        WeChat.isWXAppInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    WeChat.shareToSession({
                        thumbImage: 'http://cdn.huodongxing.com/file/20160426/11E69610D2AC0F75D7EB61C48EDEA840FB/30132422640007503.jpg',
                        type: 'news',
                        title: '应用工厂演示', // WeChat app treat title as file name
                        description: '应用工厂演示微信分享范例',
                        webpageUrl: 'http://www.lecloud.com/zh-cn/',
                    }).catch((error) => {
                        console.log(error.message);
                    }).then(resp => {
                        console.log(resp);
                        this.setState({
                            callbackStr: JSON.stringify(resp)
                        });
                    });

                } else {
                    console.log('没有安装微信，请您安装微信之后再试');
                }
            });

    }

    //分享到朋友圈
    shareToPyq = () => {
        WeChat.isWXAppInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    WeChat.shareToTimeline({
                        title: '应用工厂演示',
                        description: '应用工厂演示微信分享范例',
                        thumbImage: 'http://cdn.huodongxing.com/file/20160426/11E69610D2AC0F75D7EB61C48EDEA840FB/30132422640007503.jpg',
                        type: 'news',
                        webpageUrl: 'http://www.lecloud.com/zh-cn/'
                    }).catch((error) => {
                        console.log(error.message);
                    }).then(resp => {
                        console.log(resp);
                        this.setState({
                            callbackStr: JSON.stringify(resp)
                        });
                    });
                } else {
                    console.log('没有安装微信，请您安装微信之后再试');
                }
            });
    }


    render() {
        const { selectedDevice, posts, isFetching, lastUpdated } = this.props;
        const isEmpty = posts === null;

        let {deviceInfo} = this.props;
        return (
            <View style={[styles.container]}>

                <Text>微信api版本：{this.state.apiVersion}</Text>
                <Text>支持微信api：{String(this.state.isWXAppSupportApi)}</Text>
                <Text>已安装微信：{String(this.state.isWXAppInstalled)}</Text>
                {this.state.callbackStr ? <Text>回调结果：{String(this.state.callbackStr)}</Text> : null}

                <View style={[styles.innerContainer]}>
                    <TouchableOpacity onPress={this.loginToWeixin}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/weixindenglu.png')} style={styles.bigcodeimage} />
                            <Text>微信登录</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.shareToFrends}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/weixinhaoyou.png')} style={styles.bigcodeimage} />
                            <Text>分享到好友</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.shareToPyq}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/weixinpengyouquan.png')} style={styles.bigcodeimage} />
                            <Text>分享到朋友圈</Text>
                        </View>
                    </TouchableOpacity>
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
    },
    innerContainer: {
        borderRadius: 10,
        justifyContent: 'space-around',
        alignItems: 'center',
        width: width / 3 * 2,
        height: height / 5,
        flexDirection: 'row',
        position: 'absolute',
        bottom: height / 6,
        left: width / 6
    },
    innerContainerCancel: {
        marginTop: 6,
        borderRadius: 10,
        justifyContent: 'center',
        alignItems: 'center',
        width: width / 3 * 2,
        height: 30,
        backgroundColor: '#fff',

    },
    bigcodeimage: {
        width: width / 6,
        height: width / 6,
        marginBottom: 6
    },
});



//连接Redux
export default share;