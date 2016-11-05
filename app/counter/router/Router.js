import React, { Component } from 'react';
import {
    Navigator,
    Platform,
    BackAndroid,
    ToastAndroid,
} from 'react-native';
import { Provider } from 'react-redux';

import PageContainer from './PageContainer';

const INITIAL_ROUTE = {
    location: '/',
};

/**
 * 路由组件
 * @class router
 * @extends {Component}
 */
class Router extends Component {

    componentWillMount() {
        if (Platform.OS === 'android') {
            BackAndroid.addEventListener('hardwareBackPress', this.onBackAndroid);
        }
    }
    componentWillUnmount() {
        if (Platform.OS === 'android') {
            BackAndroid.removeEventListener('hardwareBackPress', this.onBackAndroid);
        }
    }

    /**
     * Android BACK物理按键操作
     */
    onBackAndroid = () => {
        const navigator = this.refs.navigator;
        const routers = navigator.getCurrentRoutes();
        console.log('当前路由长度：' + routers.length);
        if (routers.length > 1) {
            const top = routers[routers.length - 1];
            // if (top.ignoreBack || top.component.ignoreBack) {
            //     // 路由或组件上决定这个界面忽略back键
            //     return true;
            // }
            // const handleBack = top.handleBack || top.component.handleBack;
            // if (handleBack) {
            //     // 路由或组件上决定这个界面自行处理back键
            //     return handleBack();
            // }
            // if (top.ref && top.ref.handleBack) {
            //     // 路由或组件上决定这个界面自行处理back键
            //     // TODO： 处理Back键路由栈返回卡顿明显
            //     return top.ref.handleBack();
            // }

            navigator.pop();
            return true;//应用来接管默认行为
        }

        //抵达路由栈底，处理再按两下退出
        if (this.lastBackPressed && this.lastBackPressed + 2000 >= Date.now()) {
            //最近2秒内按过back键，可以退出应用。
            return false; //系统来接管
        }
        this.lastBackPressed = Date.now();
        ToastAndroid.show('再按一次退出应用', ToastAndroid.SHORT);
        return true;
        // return false;//默认行为
    };


    configureScene = route => {
        if (route.configure) {
            return route.configure;
        }
        if (Platform.OS === 'ios') {
            return Navigator.SceneConfigs.PushFromRight;
        }
        return Navigator.SceneConfigs.FloatFromBottomAndroid;
    };

    renderScene = (route, navigator) => (
        <PageContainer
            extraProps={route.extraProps}
            location={route.location}
            navigator={navigator}
            rootBackAndroid={this.onBackAndroid}
            />
    );

    render() {
        const { store } = this.props;
        return (
            <Provider store={store} key="provider" >
                <Navigator
                    ref="navigator"
                    initialRoute={INITIAL_ROUTE}
                    configureScene={this.configureScene}
                    renderScene={this.renderScene}
                    />
            </Provider>
        );
    }
}


export default Router;