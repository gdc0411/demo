
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
    TouchableHighlight,
    Platform
} from 'react-native';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

// import * as deviceActions from '../actions/deviceAction';

import * as LePay from '../componets/RCTLePay';

const { width, height } = Dimensions.get('window');

class pay extends Component {
    constructor(props) {
        super(props);
        this.state = {
            callbackStr: '',
            payResp: null,
        };
    }

    getBossInfo = () => {
        const { posts } = this.props;
        //url
        // let url = "http://saasapi.lecloud.com/albumOrderCreate?tenantId=400001&userId=219834&albumId=200013247&version=1.0.0";
        let url = "http://saasapi.lecloud.com/albumOrderCreate?tenantId=400001&userId=219834&albumId=200001414&version=1.0.0";
        //header
        let headers = {
            // 'Content-Type': 'application/x-www-form-urlencoded;',
            did: "22595632-6B66-426F-BC93-C8BEBA29584D",
            network: "wifi",
            mobile: Platform.OS === 'ios' ? "iOS" : "Android",
            version: "1.0.0",
            sv: "1.0",
            language: "zh-CN",
            channe: "",
            authtoken: "164f1cb96fc94b3f25a850dfc89c8af8",
            jb: "0",
            packagname: "com.lecloud.valley.demo",
            ip: posts.IPAddress
        };
        // alert(JSON.stringify(headers));
        fetch(url, {
            headers: {
                'Content-type': 'application:/x-www-form-urlencoded;charset=utf-8',
                ...headers
            }
        }).then((response) => response.json())
            .then((resp) => {
                // alert(JSON.stringify(resp));
                if (resp.state == 0) {
                    this.setState({ payInfo: resp.content.data, });
                }
            })
            .catch((error) => {
                err(error);
            });
    }

    componentDidMount() {
        this.getBossInfo();
    }

    //LePay支付
    doPay = (payInfo) => {
        // alert(JSON.stringify(payInfo));
        // return;
        // let tradeInfo = "";
        // for (let key in payInfo) {
        //     if (tradeInfo === "")
        //         tradeInfo = key + "=" + payInfo[key];
        //     else
        //         tradeInfo += "&" + key + "=" + payInfo[key];
        // }
        // alert(tradeInfo);
        LePay.pay(payInfo, resp => {
            // console.log(resp);
            alert(resp);
            this.setState({
                callbackStr: JSON.stringify(resp)
            });
        }, error => {
            alert(resp);
            console.log(error.message);
        });

        // LePay.pay({
        //     version: payInfo.version,
        //     service: payInfo.service,
        //     merchant_business_id: payInfo.merchant_business_id,
        //     user_id: payInfo.user_id,
        //     user_name: payInfo.user_name,
        //     notify_url: payInfo.notify_url,
        //     merchant_no: payInfo.merchant_no,
        //     out_trade_no: payInfo.out_trade_no,
        //     price: String(payInfo.price),
        //     currency: payInfo.currency,
        //     pay_expire: payInfo.pay_expire,
        //     product_id: payInfo.product_id,
        //     product_name: payInfo.product_name,
        //     product_desc: payInfo.product_desc,
        //     product_urls: payInfo.mProductUrls,
        //     timestamp: String(payInfo.timestamp),
        //     key_index: payInfo.key_index,
        //     input_charset: payInfo.input_charset,
        //     ip: payInfo.ip,
        //     sign: payInfo.sign,
        //     sign_type: payInfo.sign_type,
        //     isquick: '0'
        // }, resp => {
        //     // console.log(resp);
        //     // alert(resp);
        //     this.setState({
        //         callbackStr: JSON.stringify(resp)
        //     });
        // }, error => {
        //     alert(resp);
        //     console.log(error.message);
        // });
    }

    render() {
        const { posts, isFetching, lastUpdated } = this.props;
        const { payInfo } = this.state;
        const isEmpty = posts === null;

        return (
            <View style={[styles.container]}>
                <View style={{ top: 20, left: 10, right: 10, position: 'absolute' }}>
                    {isEmpty ?
                        (isFetching ? <Text>加载中...</Text> : <Text>没有数据.</Text>)
                        : <Text >支付信息:{'\r\n'}
                            ======================================{'\r\n'}
                            PayInfo:{JSON.stringify(payInfo)} {'\r\n'}
                            IP：{posts.IPAddress} {'\r\n'}
                            ======================================{'\r\n'}
                        </Text>}
                </View>
                <View style={[styles.wbContainer]}>
                    <TouchableOpacity onPress={(json) => this.doPay(payInfo)}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/wxpay.png')} style={styles.bigcodeimage} />
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={(json) => this.doPay(payInfo)}>
                        <View style={{ alignItems: 'center' }}>
                            <Image source={require('../../img/alipay.png')} style={styles.bigcodeimage} />
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
        alignItems: 'flex-start',
        width: width / 3 * 2,
        height: height / 5,
        flexDirection: 'row',
        position: 'absolute',
        bottom: 80,
        left: width / 6
    },
    bigcodeimage: {
        width: width / 4,
        height: width / 4,
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
    return {
        posts,
        isFetching,
        lastUpdated
    };
};

//连接Redux
export default connect(mapStateToProps)(pay);
