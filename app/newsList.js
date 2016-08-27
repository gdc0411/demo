/**
 * Created by raojia on 16/8/22.
 */
'use strict';

import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    View,
} from 'react-native';


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

const styles = StyleSheet.create({

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
});

module.exports = NewsList;