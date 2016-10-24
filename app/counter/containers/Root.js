/**
 * Redux Demo 01
 * 应用中所有的 state 都以一个对象树的形式储存在一个单一的 store 中。
 * 惟一改变 state 的办法是触发 action，一个描述发生什么的对象。
 * 为了描述 action 如何改变 state 树，你需要编写 reducers。
 */
import React, { Component } from 'react';
import { View, Text } from 'react-native';
import { connect } from 'react-redux';

/**
 * 加载应用所需角色
*/
import Counter01 from '../componets/Counter01';
import Counter02 from '../componets/Counter02';

/**
 * 根容器组件，负责控制木偶组件
 * @class Root
 * @extends {Component}
 */
class Root extends Component {

    render() {
        const { dispatch, value } = this.props;
        return (
            (this.props.getState) ?
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <Text>加载Store...</Text>
                </View>
                :
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <Counter01 value={value} dispatch={dispatch} />
                    <Counter01 value={value} dispatch={dispatch} />
                    <Counter02 value={value} dispatch={dispatch} />
                </View>
        );
    }
}


//配置Map映射表，拿到自己关心的数据
const mapStateToProps = state => {
    //state.xxx必须与reducer同名
    const { calculate } = state;
    const { value } = calculate;
    return {
        value
    };
};


//连接Redux
export default connect(mapStateToProps)(Root);