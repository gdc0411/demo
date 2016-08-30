/**
 * Created by raojia on 16/8/24.
 */
'use strict';

import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    View,
    Picker,
    DrawerLayoutAndroid,
} from 'react-native';


export default class DrawerLayout extends Component {
    constructor(props) {
        super(props);
        this.state = {
            lang: null,
        };
    }


    render() {
        return (
            <View style={{flex: 1, marginTop: 45}}>
                <Picker selectedValue={this.state.lang}
                        onValueChange={lang=>this.setState({lang: lang})}
                        mode="dialog" >
                    <Picker.Item label='java' value='java'/>
                    <Picker.Item label='C++' value='C++'/>
                    <Picker.Item label='PHP' value='PHP'/>
                </Picker>
                <Text>{this.state.lang}</Text>
            </View>
        );
    }
}

