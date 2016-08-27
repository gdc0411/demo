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
    View,
    TouchableOpacity,
} from 'react-native';


import DrawerLayout from './drawlayout';

var onePT = 1 / PixelRatio.get();

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

    hide(text) {
        this.setState({
            on: false,
            value: text,
        });
    }

    render() {
        return (
            <View style={[styles.flex, styles.topStatus]}>
                <View style={styles.flexDirection}>
                    <View style={[styles.flex, styles.input]}>
                        <TextInput keyboardType='web-search' returnKeyType='search'
                                   placeholder='请输入搜索内容' numberOfLines={1}
                                   onChangeText={this.show.bind(this)}>
                            {this.state.value}
                        </TextInput>
                    </View>
                    <TouchableOpacity onPress={this.hide.bind(this, this.state.value)}>
                        <View style={styles.btn}>
                            <Text style={styles.search}>搜索</Text>
                        </View>
                    </TouchableOpacity>
                </View>
                <DrawerLayout/>
                {this.state.on ?
                    <View style={styles.result}>
                        <Text style={styles.item}
                              onPress={this.hide.bind(this, this.state.value + '匹配1')}>{this.state.value}匹配1</Text>
                        <Text style={styles.item}
                              onPress={this.hide.bind(this, this.state.value + '哈哈哈')}>{this.state.value}哈哈哈</Text>
                        <Text style={styles.item}
                              onPress={this.hide.bind(this, this.state.value + '原来')}>{this.state.value}原来</Text>
                    </View>
                    : null
                }
            </View>
        );
    }
}

const styles = StyleSheet.create({
    item: {
        fontSize: 16,
        paddingTop: 5,
        paddingBottom: 10,
    },
    result: {
        marginTop: onePT,
        marginLeft: 18,
        marginRight: 5,
        height: 200,
    },
    flex: {
        flex: 1,
    },
    flexDirection: {
        flexDirection: 'row',
    },
    topStatus: {
        marginTop: 25,
    },
    input: {
        height: 50,
        borderColor: 'red',
        borderWidth: 1,
        marginLeft: 10,
        paddingLeft: 5,
        borderRadius: 5,
    },
    btn: {
        width: 45,
        marginLeft: -5,
        marginRight: 5,
        backgroundColor: '#23BEFF',
        height: 50,
        justifyContent: 'center',
        alignItems: 'center',
    },
    search: {
        color: '#fff',
        fontSize: 15,
        fontWeight: 'bold',
    },
});

module.exports = Search;
