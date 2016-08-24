/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */


'use strict'

import React, {Component} from 'react';
import {
    AppRegistry,
} from 'react-native';

import App from './app'

class DemoProject extends Component {
    render() {
        return (
            <App></App>
        );
    }
}

AppRegistry.registerComponent('DemoProject', () => DemoProject);
