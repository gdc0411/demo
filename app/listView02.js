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

export default class listView02 extends Component {

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
                <Text>正在从网络中获取数据……</Text>
            </View>);
    }

    _renderRow(rowData, sectionID, rowID) {
        return (<Text>{rowData}+{sectionID}+{rowID}</Text>);
    }

    _renderMovies(movie) {
        return (
            <View style={styles.container}>
                <Image
                    source={{ uri: movie.posters.thumbnail }}
                    style={styles.thumbnail}
                    />
                <Text style={styles.year} >年份{movie.year}</Text>
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
                contentContainerStyle={styles.list}
                />
        );
    }

}


const styles = StyleSheet.create({
    list: {
        justifyContent: 'flex-start',
        flexDirection: 'row',
        flexWrap: 'wrap'
    },
    container: {
        width: 100,
        height: 100,
        backgroundColor: '#F5FCFF',
        margin: 5,
        alignItems: 'center',
    },
    listView: {
        paddingTop: 20,
        backgroundColor: '#F5FCFF',
    },
    thumbnail: {
        width: 80,
        height: 80,
        borderRadius: 16,
    },
    //让rightContainer在父容器中占据Image之外剩下的全部空间。
    container1: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    title: {
        fontSize: 14,
        marginBottom: 8,
    },
    year: {
        fontSize: 14,
    },
});
