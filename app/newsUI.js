/**
 * Created by raojia on 16/8/22.
 */
'use strict';

import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    PixelRatio,
    View,
} from 'react-native';


import Search from './search';

class Header extends Component {
    render() {
        return (
            <View refreshing="" style={styles.header_flex}>
                <Text style={styles.header_font }>
                    <Text style={styles.font_1}>乐视</Text>
                    <Text style={styles.font_2}>新闻</Text>
                    <Text >有态度"</Text>
                </Text>
            </View>
        );
    }
}

class NewsList extends Component {
    show(title) {
        alert(title);
    }

    render() {
        var news_data = [];
        for (var i in this.props.news) {
            var text = (                
                <Text onPress={this.show.bind(this, this.props.news[i])} numberOfLines={1} style={styles.news_item}
                      key={i}>
                    {this.props.news[i]}
                    
                </Text>
            );
            news_data.push(text);
        }
        return (
            <View style={styles.flex} >
                <Text style={styles.news_title}>今日要闻</Text>
                {news_data}
            </View>
            
        );
    }
}


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
            <ScrollView style={styles.myList_flex}>
                <Text style={styles.list_item}>传递过来的用户id是：{this.state.id}</Text>
                <Text style={styles.list_item} onPress={this._pressButton.bind(this)}>点击我返回</Text>
            </ScrollView>
        );
    }
}


export default class NewsUI extends Component {
    render() {
        return (
            <View style={styles.flex}>
                <Header></Header>
                <MyList title="一线城市楼市退烧 有房源一夜跌价160万" navigator={this.props.navigator}></MyList>
                <MyList title="上海市民称墓地太贵买不起 买房存骨灰" navigator={this.props.navigator}></MyList>
                <MyList title="朝鲜再发视频:摧毁青瓦台 一切化作灰烬" navigator={this.props.navigator}></MyList>
                <MyList title="生活大爆炸人物原型都好牛逼" navigator={this.props.navigator}></MyList>

                <NewsList news={[
                    '解放军报报社大楼正在拆除 标识已被卸下(图)',
                    '韩国停签东三省52家旅行社 或为阻止朝旅游创汇',
                    '南京大学生发起亲吻陌生人活动 有女生献初吻-南京大学生发起亲吻陌生人活动 有女生献初吻-南京大学生发起亲吻陌生人活动 有女生献初吻',
                    '防总部署长江防汛:以防御98年量级大洪水为目标'
                ]}></NewsList>


                <View style={styles.container}>
                    <View style={[styles.item, styles.center]}>
                        <Text style={styles.font}>直播</Text>
                    </View>
                    <View style={[styles.item, styles.lineLeftRight]}>
                        <View style={[styles.center, styles.flex, styles.lineCenter]}>
                            <Text style={styles.font}>互动直播</Text>
                        </View>
                        <View style={[styles.center, styles.flex]}>
                            <Text style={styles.font}>云直播</Text>
                        </View>
                    </View>
                    <View style={[styles.item]}>
                        <View style={[styles.center, styles.flex, styles.lineCenter]}>
                            <Text style={styles.font}>广告服务</Text>
                        </View>
                        <View style={[styles.center, styles.flex]}>
                            <Text style={styles.font}>CDN服务</Text>
                        </View>
                    </View>
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        marginTop: 20,
        marginLeft: 5,
        marginRight: 5,
        height: 84,
        borderRadius: 5,
        padding: 2,
        backgroundColor: '#FF0067',
        flexDirection: 'row',
    },
    item: {
        flex: 1,
        height: 80,
    },
    center: {
        justifyContent: 'center',
        alignItems: 'center',
    },
    flex: {
        flex: 1,
    },
    font: {
        color: '#fff',
        fontSize: 16,
        fontWeight: 'bold',
    },
    lineLeftRight: {
        borderLeftWidth: 1 / PixelRatio.get(),
        borderRightWidth: 1 / PixelRatio.get(),
        borderColor: '#fff',
    },
    lineCenter: {
        borderBottomWidth: 1 / PixelRatio.get(),
        borderColor: '#fff',
    },
    //header
    header_flex: {
        marginTop: 25,
        height: 50,
        borderBottomWidth: 3 / PixelRatio.get(),
        borderBottomColor: '#EF2D36',
        alignItems: 'center',
    },
    header_font: {
        fontSize: 25,
        fontWeight: 'bold',
        textAlign: 'center',
    },
    font_1: {
        color: '#CD1D1C'
    },
    font_2: {
        color: '#FFF',
        backgroundColor: '#CD1D1C',
    },
    //NewsList
    news_item: {
        marginLeft: 10,
        marginRight: 10,
        fontSize: 15,
        lineHeight: 40,
    },

    news_title: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#CD1D1C',
        marginLeft: 10,
        marginTop: 15
    },

    //
    myList_flex: {
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

