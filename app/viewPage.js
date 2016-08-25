/**
 * Created by raojia on 16/8/24.
 */
'use strict';

import React, {Component} from 'react';
import {
    View,
    StyleSheet,
    Text,
    Image,
    TouchableOpacity,
    TouchableWithoutFeedback,
    ViewPagerAndroid,
} from 'react-native';



class Button extends Component {
    constructor(props) {
        super(props);
    }
    _handlePress = () => {
        if (this.props.enabled && this.props.onPress) {
            //按钮可以按,没有变灰,调用OnPress方法
            this.props.onPress();
        }
    };
    render() {
        //两个样式,第二个用来覆盖
        return (
            <TouchableWithoutFeedback onPress={this._handlePress}>
                <View style={[styles.button, this.props.enabled ? {} : styles.buttonDisabled]}>
                    <Text style={styles.buttonText}>{this.props.text}</Text>
                </View>
            </TouchableWithoutFeedback>
        );
    }
}

class LikeCount extends Component {
    constructor(props) {
        super(props);
        this.state = {
            likes: 0,
        };
    }

    _handleClick = () => {
        this.setState({ likes: this.state.likes + 1 });
    };
    render() {
        const thunbsUp = '\uD83D\uDC4D';
        return (
            <View style={styles.likeContainer}>
                <TouchableOpacity onPress={this._handleClick} style={styles.likeButton}>
                    <Text style={styles.likesText}>{thunbsUp + 'Like'}</Text>
                </TouchableOpacity>
                <Text style={styles.likesText}>{this.state.likes + '人赞过'}</Text>
            </View>
        );
    }
}


const PAGES = 5;
const BGCOLOR = ['#fdc08e', '#fff6b9', '#99d1b7', '#dde5fe', '#f79273'];
const IMAGE_URIS = [
    'http://apod.nasa.gov/apod/image/1410/20141008tleBaldridge001h990.jpg',
    'http://apod.nasa.gov/apod/image/1409/volcanicpillar_vetter_960.jpg',
    'http://apod.nasa.gov/apod/image/1409/m27_snyder_960.jpg',
    'http://apod.nasa.gov/apod/image/1409/PupAmulti_rot0.jpg',
    'http://static.flowerboys.cn/1453777625.jpg',
];


class ProgressBar extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        //当前位置+偏移量
        var fractionalPostion = (this.props.progress.position + this.props.progress.offset);
        //当前进度
        var progressBarSize = (fractionalPostion / (PAGES - 1)) * this.props.size;
        return (
            <View style={[styles.progressBarContainer, { width: this.props.size }]}>
                <View style={[styles.progressBar, { width: progressBarSize }]}/>
            </View>
        );
    }
}

//引导页 或者 欢迎界面  用viewpager实现
export default class ViewPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pageIdx: 0,
            animationEnabled: true,
            progress: {
                position: 0,
                offset: 0,
            }
        };
    }

    //这个回调会在页面切换完成后（当用户在页面间滑动）调用
    //回调参数中的event.nativeEvent对象
    onPageSelected = (e) => {
        this.setState({ pageIdx: e.nativeEvent.position });
    };

    //当在页间切换时（不论是由于动画还是由于用户在页间滑动/拖拽）执行。回调参数中的event.nativeEvent对象会包含如下数据：
    // position 从左数起第一个当前可见的页面的下标
    // offset 一个在[0,1)（大于等于0，小于1）之间的范围，代表当前页面切换的状态。值x表示现在"position"所表示的页有(1 - x)的部分可见，而下一页有x的部分可见。
    onPageScroll = (e) => {
        this.setState({ progress: e.nativeEvent });
    };

    onClick = () => {
        //alert('点击了');
        const {navigator} = this.props;
        if (navigator) {
            navigator.push({
                name: 'ViewPage',
                component: ViewPage,
            });
        }
    };

    move(delta) {
        var page = this.state.pageIdx + delta;
        this.go(page);
    }

    go(page) {
        if (this.state.animationEnabled) {
            this.viewPager.setPage(page);
        } else {
            this.viewPager.setPageWithoutAnimation(page);
        }
        //刷新
        this.setState({
            pageIdx: page
        });
    }

    render() {
        const thunbsUp = '\uD83D\uDC4D';
        var pages = [];
        for (var i = 0; i < PAGES; i++) {
            var pageStyle = {
                backgroundColor: BGCOLOR[i % BGCOLOR.length],
                alignItems: 'center',
                padding: 20,
            };
            if (i < PAGES - 1) { //前面几个viewpage
                pages.push(
                    <View key={i} style={pageStyle} collapsable={false}>
                        <Image style={styles.image}
                            source={{ uri: IMAGE_URIS[i % IMAGE_URIS.length] }}/>
                        <LikeCount/>
                    </View>
                );
            } else {  //最后一个viewpage,加了一个返回首页
                pages.push(
                    <View key={i} style={pageStyle} collapsable={false}>
                        <Image style={styles.image}
                            source={{ uri: IMAGE_URIS[i % IMAGE_URIS.length] }}/>
                        <LikeCount/>
                    </View>
                    /*<TouchableOpacity onPress={this.onClick} style={styles.startupButton}>
                     <Text style={styles.likesText}>{thunbsUp + '启动首页'}</Text>
                     </TouchableOpacity>*/
                );
            }
        }

        var {pageIdx, animationEnabled} = this.state;

        return (
            <View style={styles.container}>
                <ViewPagerAndroid
                    style={styles.flex}
                    initialPage={0}
                    onPageScroll={this.onPageScroll}
                    onPageSelected={this.onPageSelected}
                    ref={viewPager => {
                        this.viewPager = viewPager;
                    } }>
                    {pages}
                </ViewPagerAndroid>
                <View style={styles.buttons}>
                    {animationEnabled ?
                        <Button text='关闭动画' enabled={true} onPress={() => this.setState({ animationEnabled: false }) }/>
                        :
                        <Button text='打开动画' enabled={true} onPress={() => this.setState({ animationEnabled: true }) }/>
                    }
                </View>
                <View style={styles.buttons}>
                    <Button text='首页' enabled={pageIdx > 0} onPress={() => this.go(0) }/>
                    <Button text='上翻' enabled={pageIdx > 0} onPress={() => this.move(-1) }/>

                    <Text style={styles.buttonText}>页: {pageIdx + 1}/{PAGES}</Text>
                    <ProgressBar size={100} progress={this.state.progress}/>

                    <Button text="下翻" enabled={pageIdx < PAGES - 1} onPress={() => this.move(1) }/>
                    <Button text="末页" enabled={pageIdx < PAGES - 1} onPress={() => this.go(PAGES - 1) }/>

                </View>
            </View>
        );
    }

}

const styles = StyleSheet.create({
    //Button Component
    button: {
        flex: 1,
        width: 0,
        margin: 5,
        borderColor: 'gray',
        borderWidth: 1,
        backgroundColor: 'gray',
    },
    buttonDisabled: {
        backgroundColor: 'black',
        opacity: 0.5,
    },
    buttonText: {
        color: 'white',
    },
    //Like Component
    likeButton: {
        backgroundColor: 'rgba(0, 0, 0, 0.1)',
        borderColor: '#333333',
        borderWidth: 1,
        borderRadius: 5,
        flex: 1,
        margin: 8,
        padding: 8,
    },
    likeContainer: {
        flexDirection: 'row',
    },
    likesText: {
        flex: 1,
        fontSize: 18,
        alignSelf: 'center',
    },
    //All
    buttons: {
        flexDirection: 'row',
        height: 30,
        backgroundColor: 'black',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    container: {
        flex: 1,
        backgroundColor: 'white',
    },
    image: {
        width: 300,
        height: 200,
        padding: 20,
    },
    startupButton: {
        backgroundColor: 'rgba(0, 0, 0, 0.1)',
        borderColor: '#333333',
        borderWidth: 1,
        borderRadius: 5,
        margin: 8,
        padding: 8,
    },
    progressBarContainer: {
        height: 10,
        margin: 10,
        borderColor: '#eeeeee',
        borderWidth: 2,
    },
    progressBar: {
        alignSelf: 'flex-start',
        flex: 1,
        backgroundColor: '#ff0000',
    },
    flex: {
        flex: 1,
    },

});