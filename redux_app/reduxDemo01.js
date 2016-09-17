/**
 * Redux Demo 01
 * 应用中所有的 state 都以一个对象树的形式储存在一个单一的 store 中。
 * 惟一改变 state 的办法是触发 action，一个描述发生什么的对象。
 * 为了描述 action 如何改变 state 树，你需要编写 reducers。
 */
'use strict';

import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    View,
} from 'react-native';

import {connect, Provider} from 'react-redux';
import {plus} from '../redux/action';
import {getStore} from '../redux/configureStore';


class __Counter01 extends Component {
    __addCounter = () => {
        //生成一个Action 分发，改变State
        this.props.dispatch(plus(1));
    }
    render() {
        return (
            <View style={{ flexDirection: 'row' }} >
                <Text style={{ fontSize: 20, marginRight: 20 }} >计数器：{this.props.calculate.value}</Text>
                <Text style={{ fontSize: 20 }} onPress={() => this.__addCounter() } >点击我</Text>
            </View>
        );
    }
}

class __Counter02 extends Component {
    __addCounter = () => {
        //生成一个Action 分发，改变State
        this.props.dispatch(plus(2));
    }
    render() {
        return (
            <View style={{ flexDirection: 'row' }} >
                <Text style={{ fontSize: 20, marginRight: 20 }} >计数器：{this.props.calculate.value}</Text>
                <Text style={{ fontSize: 20 }} onPress={() => this.__addCounter() } >点击我</Text>
            </View>
        );
    }
}


//配置Map映射表，拿到自己关心的数据
const mapStateToProps = (state) => {
    return {
        //state.xxx必须与reducer同名
        calculate: state.calculate,
    };
};

//连接Redux
let Counter01 = connect(mapStateToProps)(__Counter01);
let Counter02 = connect(mapStateToProps)(__Counter02);


class ReduxDemo01 extends Component {

    constructor(props) {
        super(props);
        this.state = {
            store: null,
        };
    }

    componentDidMount() {
        //获得根Store
        if (!this.state.store) {
            this.setState({
                store: getStore(),
            });
        }
    }

    render() {
        return (
            (!this.state.store) ?
                <View style={styles.container} >
                    <Text>加载Store...</Text>
                </View>
                :
                <Provider store={ this.state.store } >
                    <View style={styles.container} >
                        <Counter01 />
                        <Counter01 />
                        <Counter02 />
                    </View>
                </Provider>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
});

export default ReduxDemo01;