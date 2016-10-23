'use strict';

import React, {Component} from 'react';
import {
    AppRegistry,
} from 'react-native';

//import App from './app';
//import App from './app/simpleRedux/app';
//import App from './app/simpleRedux2/app';
//import App from './app/simpleRedux3/app';
import App from './app/counter/index';

class DemoProject extends Component {
    render() {
        return (
            <App></App>
        );
    }
}

AppRegistry.registerComponent('DemoProject', () => DemoProject);
