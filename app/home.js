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

import Header from './header';
import MyList from './myList';
import NewsList from './newsList';

class Home extends Component {

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

});

module.exports = Home;
