'use strict';

import React, {Component, PropTypes} from 'react';
import {
    requireNativeComponent,
    View,
} from 'react-native';

var face = {
    name: 'CheckItemView',
    propTypes: {
        title: PropTypes.string.isRequired,
        desc: PropTypes.string.isRequired,        
        isChecked: PropTypes.bool.isRequired,
        ...View.propTypes,
    }
};

export default requireNativeComponent('CheckItemView', face);