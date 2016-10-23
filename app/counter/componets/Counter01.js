
import React, { Component, PropTypes } from 'react';
import {
    StyleSheet,
    Text,
    View,
} from 'react-native';

import { plus } from '../actions/action';

class Counter01 extends Component {

    handleClick = () => {
        this.props.dispatch(plus(1));
    }

    render() {
        const { dispatch, value } = this.props;
        return (
            <View style={{ flexDirection: 'row' }} >
                <Text style={{ fontSize: 20, marginRight: 20 }} >计数器：{this.props.value}</Text>
                <Text style={{ fontSize: 20 }} onPress={() => this.handleClick()} >点击 +1</Text>
            </View>
        );
    }
}


export default Counter01;