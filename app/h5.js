
import React, {Component} from 'react';
import {
    WebView,
    View,
    Text,
    Dimensions,
} from 'react-native';

const M = Dimensions.get('window').width;
const H = Dimensions.get('window').height;


class h5Test extends Component {
    render() {
        return (
            <View style={{ flex: 1 }} >
                <WebView style={{ height: M, width: H }} source={{ uri: 'http://m.baidu.com' }}></WebView>
            </View>
        );
    }
}

export default h5Test;
