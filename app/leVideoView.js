
'use strict';

import React, {Component, PropTypes} from 'react';
import {
    requireNativeComponent,
    View,
} from 'react-native';

var face = {
    name: 'LeVideoView',
    propTypes: {
        dataSource: PropTypes.string.isRequired,
        ...View.propTypes,
    }
};

export default requireNativeComponent('LeVideoView', face);
