/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
'use strict';

import React, {Component} from 'react';
import {
    AppRegistry,
} from 'react-native';

// import App from './app';
import App from './app/counter/index';


class LeDemo extends Component {
  render() {
    return (
        <App></App>
    );
  }
}

AppRegistry.registerComponent('LeDemo', () => LeDemo);
