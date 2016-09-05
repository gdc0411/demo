
'use strict';

import React, {Component, PropTypes} from 'react';
import {
    requireNativeComponent,
    View,
} from 'react-native';

var face = {
    name: 'LePlayerView',
    propTypes: {
        ...View.propTypes,
    }
};

export default requireNativeComponent('LePlayerView', face);
