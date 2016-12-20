
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
    //跳转到播放页
    handleBack = () => {
        const {navigator} = this.props;
        navigator.pop();
    }

    login4WX = () => {
        WeChat.isWXAppInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    WeChat.login({
                        // 登录参数
                        config: {
                            scope: 'snsapi_userinfo',
                        }
                    })
                    .catch((error) => {
                        console.log(error.message);
                    })
                    .then(json => {console.log(json); } );
                } else {
                    console.log('没有安装微信，请您安装微信之后再试');
                }
            });

    }

    sharetoFrends = () => {
        WeChat.isWXAppInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    WeChat.shareToSession({
                        // thumbImage: 'http://mta.zttit.com:8080/images/ZTT_1404756641470_image.jpg',
                        type: 'news',
                        title: '应用工厂演示', // WeChat app treat title as file name
                        description: '应用工厂演示微信分享范例',
                        webpageUrl: 'http://blog.csdn.net/liu__520/article/details/52801139',
                    }).catch((error) => {
                        console.log(error.message);
                    });
                } else {
                    console.log('没有安装微信软件，请您安装微信之后再试');
                }
            });

    }

    sharetoPyq = () => {
        WeChat.isWXAppInstalled()
            .then((isInstalled) => {
                if (isInstalled) {
                    WeChat.shareToTimeline({
                        title: '应用工厂演示',
                        description: '应用工厂演示微信分享范例',
                        // thumbImage: 'http://mta.zttit.com:8080/images/ZTT_1404756641470_image.jpg',
                        type: 'news',
                        webpageUrl: 'http://blog.csdn.net/liu__520/article/details/52801139'
                    }).catch((error) => {
                        console.log(error.message);
                    });
                } else {
                    console.log('没有安装微信软件，请您安装微信之后再试');
                }
            });
    }


    render() {
        const { selectedDevice, posts, isFetching, lastUpdated } = this.props;
        const isEmpty = posts === null;

        let {deviceInfo} = this.props;
        return (
            <View style={[styles.container]}>
                <View style={[styles.innerContainer]}>
                    <TouchableOpacity onPress={this.login4WX}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/weixinhaoyou.png')} style={styles.bigcodeimage} />
                            <Text>微信登录</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.sharetoFrends}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/weixinhaoyou.png')} style={styles.bigcodeimage} />
                            <Text>分享到好友</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.sharetoPyq}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/weixinpengyouquan.png')} style={styles.bigcodeimage} />
                            <Text>分享到朋友圈</Text>
                        </View>
                    </TouchableOpacity>
                </View>
                <TouchableHighlight
                    onPress={() => { this.setModalVisible(!this.state.modalVisible); } }
                    style={{ position: 'absolute', bottom: height / 10, left: width / 6 }}>
                    <View style={styles.innerContainerCancel}>
                        <Text style={{ color: 'blue' }}>取消</Text>
                    </View>
                </TouchableHighlight>
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