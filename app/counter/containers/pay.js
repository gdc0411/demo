
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


    //乐视支付
    doPay = () => {
        LePay.pay({
            version: '2.0',
            service: 'lepay.tv.api.show.cashier',
            merchant_business_id: '78',
            user_id: '178769661',
            user_name: 'Union',
            notify_url: 'http://trade.letv.com/',
            merchant_no: '1311313131',
            out_trade_no: '261836519',
            price: '0.01',
            currency: 'RMB',
            pay_expire: '21600',
            product_id: '8888',
            product_name: 'LeTV',
            product_desc: 'TV60',
            product_urls: 'http://f.hiphotos.baidu.com/image/pic/item/91ef76c6a7efce1b687b6bc2ad51f3deb48f6562.jpg',
            timestamp: '2017-06-06 14:05:47',
            key_index: '1',
            input_charset: 'UTF-8',
            ip: '10.72.108.52',
            sign: '03ddfd352b57d5748270afe5850c7e1c',
            sign_type: 'MD5',
            isquick:'0'
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