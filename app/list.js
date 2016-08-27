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

class List extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 1,
            user: null,
        };
    }

    _pressButton() {
        //为什么这里可以取得 props.navigator?请看上文: //<Component {...route.params} navigator={navigator} />
        // 这里传递了navigator作为props
        const {navigator} = this.props;
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
                <View style={styles.flex}>
                    <Text style={styles.list_item}>用户信息: { JSON.stringify(this.state.user) }</Text>
                </View>
            );
        } else {
            return (
                <ScrollView style={styles.flex}>
                    <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>☆ 豪华邮轮济州岛3日游</Text>
                    <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>☆ 豪华邮轮台湾3日游</Text>
                    <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>☆ 豪华邮轮地中海8日游</Text>
                </ScrollView>
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
            this.props.getUser(user);
        }
        if (navigator) {
            //很熟悉吧，入栈出栈~ 把当前的页面pop掉，这里就返回到了上一个页面:List了
            navigator.pop();
        }
    }

    render() {
        return (
            <ScrollView style={styles.flex}>
                <Text style={styles.list_item}>传递过来的用户id是：{this.state.id}</Text>
                <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>点击我可以跳回去</Text>
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
        fontSize: 20,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
        justifyContent: 'center',
    },
});

module.exports = List;