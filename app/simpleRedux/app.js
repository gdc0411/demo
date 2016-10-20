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

        //创建事件监听handler
        this.handler = ((value) => {
            this.setState({
                counter: value
            });
        }).bind(this);
    }

    /**
     * 注册事件监听
     */
    componentDidMount() {
        on('counter-changed', this.handler);
    }


    /**
     * 删除事件监听
     */
    componentWillUnmount() {
        remove('counter-changed', this.handler);
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
        let value = getValue() + 1;
        setValue(value);
        //触发事件
        trigger('counter-changed', value);

        this.setState({
            counter: value
        });
    }
}

class Counter2 extends Component {
    constructor(props) {
        super(props);
        this.state = {
            counter: getValue(),
        };

        //创建事件监听handler
        this.handler = ((value) => {
            this.setState({
                counter: value
            });
        }).bind(this);
    }

    /**
     * 注册事件监听
     */
    componentDidMount() {
        on('counter-changed', this.handler);
    }


    /**
     * 删除事件监听
     */
    componentWillUnmount() {
        remove('counter-changed', this.handler);
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
        let value = getValue() + 1;
        setValue(value);
        //触发事件
        trigger('counter-changed', value);
        //修改state
        this.setState({
            counter: value
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