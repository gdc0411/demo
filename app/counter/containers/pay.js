
/**
 * 调用支付接口
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

import * as LePay from '../componets/RCTLePay';

const { width, height } = Dimensions.get('window');

class social extends Component {
    constructor(props) {
        super(props);
        this.state = {
            wbApiVersion: 'waiting...',
            isWBInstalled: 'waiting...',
            isWBSupportApi: 'waiting...',
            qqApiVersion: 'waiting...',
            isQQInstalled: 'waiting...',
            isQQSupportApi: 'waiting...',
            wxApiVersion: 'waiting...',
            isWXAppSupportApi: 'waiting...',
            isWXAppInstalled: 'waiting...',
            callbackStr: '',
        };
    }


    //QQ分享给好友
    doPay = () => {
        LePay.pay({
            title: '乐视云开放框架介绍',
            thumbImage: 'http://cdn.huodongxing.com/file/20160426/11E69610D2AC0F75D7EB61C48EDEA840FB/30132422640007503.jpg',
            description: '应用工厂演示QQ分享实例，LeValley框架值得期待',
            webpageUrl: 'http://www.lecloud.com/zh-cn/',
            appName: '应用工厂演示',
            cflag: 2
        }, resp => {
            // console.log(resp);
            alert(resp);
            this.setState({
                callbackStr: JSON.stringify(resp)
            });
        }, error => {
            alert(resp);
            console.log(error.message);
        });
    }

    render() {
        const { selectedDevice, posts, isFetching, lastUpdated } = this.props;

        let { deviceInfo } = this.props;
        return (
            <View style={[styles.container]}>
                <View style={{ top: 20, left: 10, right: 10, position: 'absolute' }}></View>
                <View style={[styles.wbContainer]}>
                    <TouchableOpacity onPress={this.doPay}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/loginWeibo.png')} style={styles.bigcodeimage} />
                            <Text>微信支付</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.doPay}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/shareWeibo.png')} style={styles.bigcodeimage} />
                            <Text>支付宝</Text>
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
    wbContainer: {
        borderRadius: 10,
        justifyContent: 'space-around',
        alignItems: 'center',
        width: width / 3 * 2,
        height: height / 5,
        flexDirection: 'row',
        position: 'absolute',
        bottom: 200,
        left: width / 6
    },
    bigcodeimage: {
        width: width / 6,
        height: width / 6,
        marginBottom: 6
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
    buttonContainer: {
        flex: 0,
        flexDirection: 'row',
        justifyContent: 'space-around'
    },
    button: {
        padding: 5,
        margin: 5,
        borderWidth: 1,
        borderColor: 'white',
        borderRadius: 3,
        backgroundColor: 'grey',
    }
});



//连接Redux
export default social;