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
import { on } from './event';

class SimpleReduxApp extends Component {
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

class Counter1 extends Component {
    constructor(props) {
        super(props);//这一句不能省略，照抄即可
        this.state = {
            counter: getValue(),
        };
    }


    /**
     * 注册事件监听
     * @memberOf Counter1
     */
    componentDidMount() {
        on('counter-changed', ((value) => {
            this.setState({
                counter: value
            });
        }).bind(this));
    }


    render() {
        return (
            <View style={{ flexDirection: 'row' }}>
                <Text style={{ fontSize: 20, marginRight: 20 }}>计数器：{this.state.counter}</Text>
                <Text style={{ fontSize: 20 }} onPress={this.addCounter.bind(this)}>点击我</Text>
            </View>
        );
    }

    addCounter() {
        setValue(getValue() + 1);
        this.setState({
            counter: getValue()
        });
    }
}

class Counter2 extends Component {
    constructor(props) {
        super(props);//这一句不能省略，照抄即可
        this.state = {
            counter: getValue(),
        };
    }

    componentDidMount() {
        on('counter-changed', ((value) => {
            this.setState({
                counter: value
            });
        }).bind(this));
    }

    render() {
        return (
            <View style={{ flexDirection: 'row' }}>
                <Text style={{ fontSize: 20, marginRight: 20 }}>计数器：{this.state.counter}</Text>
                <Text style={{ fontSize: 20 }} onPress={this.addCounter.bind(this)}>点击我</Text>
            </View>
        );
    }

    addCounter() {
        setValue(getValue() + 1);
        this.setState({
            counter: getValue()
        });
    }
}

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


export default SimpleReduxApp;