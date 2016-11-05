
import React, { Component, PropTypes } from 'react';
import {
    StyleSheet,
    Text,
    View,
} from 'react-native';

class Counter extends Component {

    render() {
        const { value, para, oper, onChange } = this.props;
        return (
            <View style={{ flexDirection: 'row' }} >
                <Text style={{ fontSize: 20, marginRight: 20 }} >计数器：{value}</Text>
                <Text style={{ fontSize: 20 }} onPress={(data) => onChange(para) } >点击 {oper} {para}</Text>
            </View>
        );
    }
}

Counter.propTypes = {
    value: PropTypes.number.isRequired,
    para: PropTypes.number.isRequired,
    oper: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
};

export default Counter;