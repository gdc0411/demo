/**
 * Created by raojia on 16/8/22.
 */
'use strict'

import React, {Component} from 'react';
import {
    Navigator,
} from 'react-native';

import Home from './app/home';

class App extends Component {
    render() {
        let defaultName = 'Home';
        let defaultComponent = Home;
        return (
            <Navigator
                initialRoute={{name: defaultName, component: defaultComponent}}
                //配置场景
                configureScene={
                    (route) => {
                        return Navigator.SceneConfigs.VerticalUpSwipeJump;
                    }
                }
                renderScene={
                    (route, navigator) => {
                        let Component = route.component;
                        return <Component {...route.params} navigator={navigator}/>;
                    }
                }
            />
        );
    }
}

module.exports = App;
