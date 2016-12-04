
/**
 * 查看Device信息
 */
'use strict';

import React, { Component, PropTypes } from 'react';
import {
    StyleSheet,
    Text,
    View,
    Image,
    NativeModules,
    Platform,
} from 'react-native';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import * as deviceActions from '../actions/deviceAction';

import { selectDevice, fetchPostsIfNeeded, invalidateDevice } from '../actions/deviceAction';

class device extends Component {

    static propTypes = {
        selectedDevice: PropTypes.string.isRequired,
        posts: PropTypes.array.isRequired,
        isFetching: PropTypes.bool.isRequired,
        lastUpdated: PropTypes.number,
        dispatch: PropTypes.func.isRequired
    }

    componentDidMount() {
        const { dispatch, selectedDevice } = this.props;
        dispatch(fetchPostsIfNeeded(selectedDevice));
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.selectedDevice !== this.props.selectedDevice) {
            const { dispatch, selectedDevice } = nextProps;
            dispatch(fetchPostsIfNeeded(selectedDevice));
        }
    }

    render() {
        const { selectedDevice, posts, isFetching, lastUpdated } = this.props;
        let {deviceInfo} = this.props;
        return (
            <View>
                {deviceInfo ? <Text >设备信息: {deviceInfo} </Text> : null}
            </View>
        );
    }
}

//配置Map映射表，拿到自己关心的数据
const mapStateToProps = state => {
    const { selectedDevice, postsByDevice } = state;
    const { 
        isFetching, 
        lastUpdated, 
        items: posts 
    } = postsByDevice[selectedDevice] || { 
        isFetching: true, 
        items: [] 
    };
    return {
        selectedDevice,
        posts,
        isFetching,
        lastUpdated
    };
};

const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(deviceActions, dispatch)
});

//连接Redux
// export default connect(mapStateToProps, mapDispatchToProps)(device);
export default connect(mapStateToProps)(device);