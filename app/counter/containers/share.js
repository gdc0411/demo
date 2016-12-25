
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
import * as QQ from '../componets/RCTQQAPI';

const {width, height} = Dimensions.get('window');

class share extends Component {
    constructor(props) {
        super(props);
        this.state = {
            qqApiVersion: 'waiting...',
            isQQAppInstalled: 'waiting...',
            wxApiVersion: 'waiting...',
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
                qqApiVersion: await QQ.getApiVersion(),
                isQQAppInstalled: await QQ.isQQInstalled(),
                wxApiVersion: await WeChat.getApiVersion(),
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

    //QQ登陆
    loginToQQ = () => {
        QQ.isQQInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    QQ.login('get_simple_userinfo')
                    .catch(error => {
                        console.log(error.message);
                    }).then(resp => {
                        console.log(resp);
                        this.setState({
                            callbackStr: JSON.stringify(resp)
                        });
                    });
                } else {
                    console.log('没有安装QQ，请您安装QQ之后再试');
                }
            });
    }


    //QQ分享给好友
    shareToQQ = () => {
        QQ.isQQInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    QQ.shareToQQ({
                        req_type: 1, //1:图文，2：音乐，5：纯图，6：应用
                        imageUrl: 'http://cdn.huodongxing.com/file/20160426/11E69610D2AC0F75D7EB61C48EDEA840FB/30132422640007503.jpg',                        
                        title: '应用工厂创新应用值得期待', 
                        summary: '应用工厂演示QQ分享实例',
                        targetUrl: 'http://www.lecloud.com/zh-cn/',
                        appName:'应用工厂演示',
                        cflag: 2
                    }).catch((error) => {
                        console.log(error.message);
                    }).then(resp => {
                        console.log(resp);
                        this.setState({
                            callbackStr: JSON.stringify(resp)
                        });
                    });

                } else {
                    console.log('没有安装QQ，请您安装QQ之后再试');
                }
            });

    }

     //QQ分享给QZone
    shareToQzone = () => {
        QQ.isQQInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    QQ.shareToQzone({
                        req_type: 1, //1:图文, 3:说说, 4:视频
                        imageUrl: 'http://cdn.huodongxing.com/file/20160426/11E69610D2AC0F75D7EB61C48EDEA840FB/30132422640007503.jpg',                        
                        title: '应用工厂创新应用值得期待', 
                        summary: '应用工厂演示QQ分享实例',
                        targetUrl: 'http://www.lecloud.com/zh-cn/',
                        appName:'应用工厂演示',
                        cflag: 1
                    }).catch((error) => {
                        console.log(error.message);
                    }).then(resp => {
                        console.log(resp);
                        this.setState({
                            callbackStr: JSON.stringify(resp)
                        });
                    });

                } else {
                    console.log('没有安装QQ，请您安装QQ之后再试');
                }
            });

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

    //微信分享给朋友
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

    //微信分享到朋友圈
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

                <View style={{ top: 20, left: 10, position: 'absolute' }}>
                    <Text>QQapi版本：{this.state.qqApiVersion}</Text>
                    <Text>QQ已安装：{String(this.state.isQQAppInstalled)}</Text>
                    <Text>微信api版本：{this.state.wxApiVersion}</Text>
                    <Text>微信api支持：{String(this.state.isWXAppSupportApi)}</Text>
                    <Text>微信已安装：{String(this.state.isWXAppInstalled)}</Text>
                    {this.state.callbackStr ? <Text>回调结果：{String(this.state.callbackStr)}</Text> : null}
                </View>

                <View style={[styles.qqContainer]}>
                    <TouchableOpacity onPress={this.loginToQQ}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/loginQQ.png')} style={styles.bigcodeimage} />
                            <Text>QQ登录</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.shareToQQ}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/shareQQ.png')} style={styles.bigcodeimage} />
                            <Text>分享到好友</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.shareToQzone}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/shareQzone.png')} style={styles.bigcodeimage} />
                            <Text>分享到空间</Text>
                        </View>
                    </TouchableOpacity>
                </View>
                <View style={[styles.wxContainer]}>
                    <TouchableOpacity onPress={this.loginToWeixin}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/weixindenglu.png')} style={styles.bigcodeimage} />
                            <Text>微信登录</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.shareToFrends}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/shareWeixin.png')} style={styles.bigcodeimage} />
                            <Text>分享到好友</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.shareToPyq}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/sharePyq.png')} style={styles.bigcodeimage} />
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
    qqContainer: {
        borderRadius: 10,
        justifyContent: 'space-around',
        alignItems: 'center',
        width: width / 3 * 2,
        height: height / 5,
        flexDirection: 'row',
        position: 'absolute',
        bottom: 120,
        left: width / 6
    },
    wxContainer: {
        borderRadius: 10,
        justifyContent: 'space-around',
        alignItems: 'center',
        width: width / 3 * 2,
        height: height / 5,
        flexDirection: 'row',
        position: 'absolute',
        bottom: 10,
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