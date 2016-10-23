
import React, { Component, PropTypes } from 'react';
import {
    StyleSheet,
    Text,
    View,
} from 'react-native';

import { plus } from '../actions/action';

class Counter02 extends Component {

    __addCounter = () => {
        this.props.dispatch(plus(-1));
    }

    render() {
        const { dispatch, value } = this.props;
        return (
            <View style={{ flexDirection: 'row' }} >
                <Text style={{ fontSize: 20, marginRight: 20 }} >计数器：{value}</Text>
                <Text style={{ fontSize: 20 }} onPress={() => this.__addCounter()} >点击  -1</Text>
            </View>
        );
    }
}

Counter02.propTypes = {
    value: PropTypes.number.isRequired
};

export default Counter02;