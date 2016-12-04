
'use strict';

import React, { Component, PropTypes } from 'react';
import {
    StyleSheet,
    Text,
    View,
    Image,
    TouchableOpacity,
} from 'react-native';


export default class InfoItem extends Component {

    static propTypes = {
        onViewInfo: PropTypes.func.isRequired,
    };

    render() {
        const { desc, color, imgUrl, onViewInfo } = this.props;
        return (
            <View style={{ height: 60 }} >
                <TouchableOpacity onPress={() => onViewInfo()}>
                    <Text style={{ fontSize: 18, fontWeight: 'bold', color: `${color}` }}>{desc}</Text>
                    {/** <Image
                        style={{ height: 90, width: 100, }}
                        source={ require('../asserts/images/rmb.jpg') }
                        resizeMode="contain" /> */}

                </TouchableOpacity>
            </View>
        );
    }
}