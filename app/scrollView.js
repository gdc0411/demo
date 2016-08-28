/**
 * Created by raojia on 16/8/25.
 * 模拟异步网络通信
 */

'use strict';

import React, {Component} from 'react';
import {
    View,
    StyleSheet,
    Image,
    Text,
    ScrollView,
} from 'react-native';


const REQUEST_URL = 'https://raw.githubusercontent.com/facebook/react-native/master/docs/MoviesExample.json';

export default class ListView03 extends Component {
    constructor(props) {
        super(props);
        this.state = {
            movies: null,
        };
    }

    componentDidMount() {
        fetch(REQUEST_URL)
            .then((response)=>response.json())
            .then((responseData)=>this.setState({
                movies: responseData.movies
            }))
            .done();//调用了done() —— 这样可以抛出异常而不是简单忽略
    }

    render() {
        if (!this.state.movies) {
            //如果movies==null的情况 初始情况  渲染加载视图
            return (
                <View style={styles.container}>
                    <Text>从网络中加载影片数据中...</Text>
                </View>
            );
        }
        
        //movies已有数据
        var {movies} = this.state;
        var movieData = [];
        for (var i = 0; i < movies.length; i++) {
            movieData.push(
                <View key={i} style={styles.container}>
                    <Image
                        source={{uri: movies[i].posters.thumbnail}}
                        style={styles.thumbnail}
                    />
                    <View style={styles.rightContainer}>
                        <Text style={styles.title}>标题：{movies[i].title}</Text>
                        <Text style={styles.year}>{movies[i].year}年</Text>
                    </View>
                </View>
            );
        }
        return (
            <ScrollView>{movieData}</ScrollView>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    thumbnail: {
        width: 53,
        height: 81,

    },
    //让rightContainer在父容器中占据Image之外剩下的全部空间。
    rightContainer: {
        flex: 1,
    },
    title: {
        fontSize: 16,
        marginBottom: 8,
        textAlign: 'center',
    },
    year: {
        textAlign: 'center',
    },
});