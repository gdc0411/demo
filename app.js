/**
 * Created by raojia on 16/8/22.
 */
'use strict';

import React, {Component} from 'react';
import {
    Navigator,
    Platform,
    BackAndroid,
    ToastAndroid,

} from 'react-native';

// import Home from './app/home';
// import Home from './app/search';
import Home from './app/asyncStore';

class App extends Component {

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
        const navigator = this.refs.navstack;
        const routers = navigator.getCurrentRoutes();
        console.log('当前路由长度：' + routers.length);
        if (routers.length > 1) {
            const top = routers[routers.length - 1];
            if (top.ignoreBack || top.component.ignoreBack) {
                // 路由或组件上决定这个界面忽略back键
                return true;
            }
            const handleBack = top.handleBack || top.component.handleBack;
            if (handleBack) {
                // 路由或组件上决定这个界面自行处理back键
                return handleBack();
            }
            if (top.ref && top.ref.handleBack) {
                // 路由或组件上决定这个界面自行处理back键
                //return top.ref.handleBack();
            }

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


    render() {
        let defaultName = 'Home';
        let defaultComponent = Home;
        return (

            <Navigator
                ref="navstack"
                initialRoute={{ name: defaultName, component: defaultComponent }}
                configureScene={
                    //配置场景
                    (route) => {
                        return Navigator.SceneConfigs.FloatFromLeft;
                    }
                }
                renderScene={
                    (route, navigator) => {
                        let Component = route.component;
                        return <Component ref={r => route.ref = r}  {...route.params} navigator={navigator}/>;
                    }
                }
                />
        );
    }
}

module.exports = App;
