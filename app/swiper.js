/**
 * 开源轮播组件
 */
import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Image,
    Text,
    TouchableOpacity,
    DeviceEventEmitter,
    NativeModules,
    Platform,
} from 'react-native';
import Swiper from 'react-native-swiper';

const renderPagination = (index, total, context) => {
    return (
        <View style={{
            position: 'absolute',
            bottom: -25,
            right: 10
        }}>
            <Text>
                <Text style={{
                    color: '#007aff',
                    fontSize: 20
                }}>{index + 1}</Text>/{total}
            </Text>
        </View>
    );
};


class MySwiper extends Component {

    constructor(props) {
        super(props);
        this.state = {
            phoneNum: null,

            callBackResult: false,
            callBackMsg: null,
            callBackCode: 0,

            promiseResult: null,

        };
    }


    /**
     * 调用原生方法
     */
    _handlePress = (key) => {
        if (Platform.OS === 'android') {
            let _self = this;

            switch (key) {
                case 1: //方案一：消息机制
                    para = '调用Native方法！';
                    //NativeModules.RJNativeModule.callNative(para); //不带回调
                    //NativeModules.RJNativeModule.callNativeWithResult(para);  //带回调
                    NativeModules.EmbedModule.embedCallWithResult('Embed方案调用！'); //带回调
                    break;

                case 2: //方案二：回调函数

                    NativeModules.CallbackModule.callbackTest(
                        (x, y, z) => { _self.setState({ callBackResult: x, callBackMsg: y, callBackCode: z }); },
                        (errMsg) => { _self.setState({ callBackMsg: errMsg, }); }
                    );
                    break;

                case 3: //方案三：Promise机制
                    NativeModules.PromiseModule.promiseTest('Promise方案调用')
                        .then((msg) => {
                            _self.setState({ promiseResult: msg });
                        }).catch((err) => {
                            _self.setState({ promiseResult: err });
                        });
                    break;

                case 4:

                    break;

                default:
                    break;
            }


        }
    }


    componentWillMount() {
        if (Platform.OS === 'android') {
            DeviceEventEmitter.addListener('AndroidToRNMessage', this._handleAndroidMessage);
            DeviceEventEmitter.addListener('EmbedMessage', this._handleAndroidMessage);
        }
    }
    componentWillUnmount() {
        if (Platform.OS === 'android') {
            DeviceEventEmitter.removeListener('AndroidToRNMessage', this._handleAndroidMessage);
            DeviceEventEmitter.removeListener('EmbedMessage', this._handleAndroidMessage);
        }
    }

    //获得Android侧的参数回调
    _handleAndroidMessage = (para) => {
        // alert(para);
        console.log(para);
        this.setState({
            phoneNum: para,
        });
    }


    render() {

        let {phoneNum, callBackResult, promiseResult} = this.state;

        return (
            <View style={{ flex: 1 }} >
                <Swiper style={styles.wrapper} height={240} autoplay={false}>
                    <View style={styles.slide}>
                        <TouchableOpacity style={{ flex: 1 }} onPress={(para) => this._handlePress(1) } >
                            <Image resizeMode='contain' style={styles.img} source={{ uri: 'http://c.hiphotos.baidu.com/image/w%3D310/sign=0dff10a81c30e924cfa49a307c096e66/7acb0a46f21fbe096194ceb468600c338644ad43.jpg' }} >
                                <Text numberOfLines={1} style={styles.item_text}>测试一下</Text>
                            </Image>
                        </TouchableOpacity>
                    </View>
                    <View style={styles.slide2}>
                        <TouchableOpacity style={{ flex: 1 }} onPress={(para) => this._handlePress(2) } >
                            <Text style={styles.text}>CallBack Call</Text>
                        </TouchableOpacity>
                    </View>
                    <View style={styles.slide3}>
                        <TouchableOpacity style={{ flex: 1 }} onPress={(para) => this._handlePress(3) } >
                            <Text style={styles.text}>Prmoise Call</Text>
                        </TouchableOpacity>
                    </View>
                </Swiper>
                <View>
                    <Text >跨语言常量URL: {NativeModules.ConstModule.URL}-port: {NativeModules.ConstModule.port}-ip: {NativeModules.ConstModule.ip} </Text>
                    {phoneNum ? <Text >手机号：{this.state.phoneNum} </Text> : null}
                    {callBackResult ? <Text>CallbackModule: {this.state.callBackMsg}-{this.state.callBackCode} </Text> : null}
                    {promiseResult ? <Text >Promise: {this.state.promiseResult} </Text> : null}
                </View>
            </View >
        );
    }
}

const styles = StyleSheet.create({
    wrapper: {
    },

    slide: {
        flex: 1,
        justifyContent: 'center',
        backgroundColor: 'transparent'
    },

    slide1: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#9DD6EB'
    },

    slide2: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#97CAE5'
    },

    slide3: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#92BBD9'
    },

    text: {
        color: '#fff',
        fontSize: 30,
        fontWeight: 'bold'
    },

    img: {
        flex: 1,
        backgroundColor: 'transparent',
    },
    item_text: {
        backgroundColor: '#000',
        opacity: 0.7,
        color: '#fff',
        height: 35,
        lineHeight: 18,
        textAlign: 'center',
        marginTop: 144,
    },
    item: {
        flex: 1,
        marginLeft: 5,
        borderWidth: 1,
        borderColor: '#ddd',
        marginRight: 5,
        height: 100,
    },

    image: {
        flex: 1,
    }
});


export default MySwiper;