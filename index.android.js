'use strict';

import React, {Component} from 'react';
import {
    AppRegistry,
} from 'react-native';

import App from './app';

class DemoProject extends Component {
    render() {
        return (
            <App></App>
        );
    }
}

AppRegistry.registerComponent('DemoProject', () => DemoProject);
