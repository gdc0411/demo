/**
 * Created by raojia on 16/8/22.
 */
'use strict';

import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    ScrollView,
    View,
} from 'react-native';


class MyList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 1,
            user: null,
        };
    }
    _pressButton() {
        const {navigator}  =  this.props;  //等价于 const navigator = this.props.navigator;
        const self = this;
        if (navigator) {
            navigator.push({
                name: 'Detail',
                component: Detail,
                params: {
                    id: this.state.id,
                    //从详情页获取user
                    getUser: function (user) {
                        self.setState({
                            user: user
                        });
                    }
                }
            });
        }
    }

    render() {
        if (this.state.user) {
            return (
                <View style={styles.list_item}>
                    <Text style={styles.list_item}>用户信息: { JSON.stringify(this.state.user) }</Text>
                </View>);
        } else {
            return (
                <View style={styles.list_item}>
                    <Text style={styles.list_item_font} onPress={this._pressButton.bind(this)}>{this.props.title}</Text>
                </View>
            );
        }
    }
}

const USER_MODELS = {
    1: {name: 'mot', age: 23},
    2: {name: '晴明大大', age: 25}
};

class Detail extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: null
        };
    }
    componentDidMount() {
        //这里获取从FirstPageComponent传递过来的参数: id
        this.setState({
            id: this.props.id
        });
    }
    _pressButton() {
        const {navigator} = this.props;
        if (this.props.getUser) {
            let user = USER_MODELS[this.props.id];
            //从详情页设置state.user的值
            this.props.getUser(user);
        }
        if (navigator) {
            navigator.pop();
        }
    }
    render() {
        return (
            <ScrollView style={styles.flex}>
                <Text style={styles.list_item}>传递过来的用户id是：{this.state.id}</Text>
                <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>点击我返回</Text>
            </ScrollView>
        );
    }
}

const styles = StyleSheet.create({
    flex: {
        flex: 1,
        marginTop: 100,
    },
    list_item: {
        height: 40,
        marginLeft: 10,
        marginRight: 10,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
        justifyContent: 'center',
    },

    list_item_font: {
        fontSize: 16,
    },
});

module.exports = MyList;