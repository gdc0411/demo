/**
 * Sample React Native App
 * @flow
 */

import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    View
} from 'react-native';

import { setValue, getValue } from './count';
import { on, remove, trigger } from './event';
import { connector } from './connector';

class SimpleReduxApp02 extends Component {
    render() {
        return (
            <View style={styles.container}>
                <Counter1 />
                <Counter1 />
                <Counter2 />
            </View>
        );
    }
}


class __Counter1 extends Component {

    render() {
        return (
            <View style={{ flexDirection: 'row' }}>
                <Text style={{ fontSize: 20, marginRight: 20 }}>计数器：{this.props.data}</Text>
                <Text style={{ fontSize: 20 }} onPress={this.addCounter.bind(this)}>点击我</Text>
            </View>
        );
    }

    addCounter() {
        //系统事件 点击变成一个意图
        //action: +1 {type:'plus1'}
        setValue(getValue() + 1);
    }
}

class __Counter2 extends Component {
    render() {
        return (
            <View style={{ flexDirection: 'row' }}>
                <Text style={{ fontSize: 20, marginRight: 20 }}>计数器：{this.props.data}</Text>
                <Text style={{ fontSize: 20 }} onPress={this.addCounter.bind(this)}>点击我</Text>
            </View>
        );
    }

    addCounter() {
        setValue(getValue() + 1);
    }
}

//redux是一个完整数据模型，并没有connect方法
//是放在react-redux库里面有连接的方法connect()
//数据与组件连接起来
let Counter1 = connector('counter-changed', __Counter1);

let Counter2 = connector('counter-changed', __Counter2);

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});


export default SimpleReduxApp02;