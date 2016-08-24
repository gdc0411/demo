/**
 * Created by raojia on 16/8/24.
 */
'use strict';

import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    PixelRatio,
    TextInput,
    ScrollView,
    View,
} from 'react-native';

const minPT = 1 / PixelRatio.get();

class Search extends Component {

    constructor(props) {
        super(props);
        this.state = {
            on: false,
            value: null,
        };
    }

    show(val) {
        this.setState({
            on: true,
            value: val,
        });
    }

    hide(val) {
        this.setState({
            on: false,
            value: val,
        });
    }

    render() {
        return (
            <View>
                <View>

                </View>
                (this.state.on)?
                <View>

                </View>:null
            </View>
        );
    }

}
