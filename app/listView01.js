/**
 * ListView 绑定数据源显示
 * 多个电影数据的情况
 */
'use strict';

import React, {Component} from 'react';
import {
    View,
    StyleSheet,
    ListView,
    Image,
    Text,
} from 'react-native';


var REQUEST_URL = 'https://raw.githubusercontent.com/facebook/react-native/master/docs/MoviesExample.json';

export default class listView01 extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loaded: false,
            dataSource: new ListView.DataSource({
                rowHasChanged: (row1, row2) => row1 !== row2,
            })
        };
    }

    componentDidMount() {
        //在React的工作机制下，setState实际上会触发一次重新渲染的流程，此时render函数被触发，发现this.state.movies不再是null
        fetch(REQUEST_URL)
            .then((response) => response.json())
            .then((responseData) => {
                this.setState({
                    loaded: true,
                    dataSource: this.state.dataSource.cloneWithRows(responseData.movies),
                });
            }).done(); //调用了done() —— 这样可以抛出异常而不是简单忽略
    }

    _renderLoadingView() {
        return (
            <View style={styles.container} >
                <Text>从网络中加载影片数据中...</Text>
            </View>);
    }

    _renderRow(rowData, sectionID, rowID) {
        return (<Text>{rowData}+{sectionID}+{rowID}</Text>);
    }

    _renderMovies(movie) {
        return (
            <View style={styles.container} >
                <Image source={{ uri: movie.posters.thumbnail }} style={styles.thumbnail} />
                <View style={styles.rightContainer} >
                    <Text style={styles.title}>{movie.title}</Text>
                    <Text style={styles.year}>{movie.year} </Text>
                </View>
            </View>
        );
    }

    render() {
        //初始化获取数据
        if (!this.state.loaded) {
            return this._renderLoadingView();
        }

        return (
            <ListView
                dataSource={this.state.dataSource}
                renderRow={this._renderMovies}
                style={styles.listView}
                />
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
    listView: {
        paddingTop: 20,
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
        fontSize: 20,
        marginBottom: 8,
        textAlign: 'center',
    },
    year: {
        textAlign: 'center',
    },



});


