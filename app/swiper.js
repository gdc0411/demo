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
    NativeModules,
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

    //调用原生方法
    _handlePress = (para) => {
        NativeModules.RJNativeModule.callNative(para);
    }

    render() {
        return (
            <View style={{ flex: 1 }} >
                <Swiper style={styles.wrapper} height={240} autoplay={false}>
                    <View style={styles.slide}>
                        <TouchableOpacity style={{ flex: 1 }} onPress={(para) => this._handlePress('调用乐视原生播放SDK！') } >
                            <Image resizeMode='contain' style={styles.img} source={{ uri: 'http://c.hiphotos.baidu.com/image/w%3D310/sign=0dff10a81c30e924cfa49a307c096e66/7acb0a46f21fbe096194ceb468600c338644ad43.jpg' }} >
                                <Text numberOfLines={1} style={styles.item_text}>测试一下</Text>
                            </Image>
                        </TouchableOpacity>
                    </View>
                    <View style={styles.slide2}>
                        <Text style={styles.text}>Beautiful</Text>
                    </View>
                    <View style={styles.slide3}>
                        <Text style={styles.text}>And simple</Text>
                    </View>
                </Swiper>

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