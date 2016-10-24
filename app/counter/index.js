/**
 * Redux Demo 01
 * 应用中所有的 state 都以一个对象树的形式储存在一个单一的 store 中。
 * 惟一改变 state 的办法是触发 action，一个描述发生什么的对象。
 * 为了描述 action 如何改变 state 树，你需要编写 reducers。
 */

import React, { Component } from 'react';

import configureStore from './store/configureStore';
import App from './containers/App';

const store = configureStore();

/**
 * 根组件，负责store和根reducer创建和关联
 * 
 * @class ReduxDemo01
 * @extends {Component}
 */
class ReduxDemo01 extends Component {
    render() {
        return (
            <App store={store} />
        );
    }
}

export default ReduxDemo01;