/**
 * Redux Demo 01
 * 应用中所有的 state 都以一个对象树的形式储存在一个单一的 store 中。
 * 惟一改变 state 的办法是触发 action，一个描述发生什么的对象。
 * 为了描述 action 如何改变 state 树，你需要编写 reducers。
 */
import React, { Component } from 'react';
import {
    View,
    Text,
    Image,
} from 'react-native';
import { connect } from 'react-redux';

/**
 * 加载应用所需角色
*/
import Counter01 from '../componets/Counter01';
import Counter02 from '../componets/Counter02';
import LeVideoView from '../componets/LeVideoView';
import CheckItemView from '../componets/nativeView02';

//import LeVideoView from '../componets/NativeVideo';

/**
 * 首页的根容器组件，负责控制首页内的木偶组件
 * @class Root
 * @extends {Component}
 */
class home extends Component {

    render() {
        const { value } = this.props;
        return (
            (this.props.getState) ?
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <Text>加载Store...</Text>
                </View>
                :
                <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} >
                    <LeVideoView style={{ height: 180, width: 320, backgroundColor: '#eeffee'  }} dataSource='http://cache.utovr.com/201601131107187320.mp4' />
                    <Image  style={{ height: 180, width: 200, }} source={require('../asserts/images/lecloud.png') } resizeMode="contain" />
                    <Counter01 value={value} />
                    <Counter01 value={value} />
                    <Counter02 value={value} />
                </View>
        );
        //
        //                        <CheckItemView style={{ width: - 20, height: 68, marginTop: 10 }} desc={'描述'} title={'标题'} isChecked={true}/>

    }
}


//配置Map映射表，拿到自己关心的数据
const mapStateToProps = state => {
    //state.xxx必须与reducer同名
    const { calculate } = state;
    const { value } = calculate;
    return {
        value
    };
};


//连接Redux
export default connect(mapStateToProps)(home);