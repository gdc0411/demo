
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
} from 'react-native';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import * as deviceActions from '../actions/deviceAction';

class device extends Component {

    static propTypes = {
        selectedDevice: PropTypes.string.isRequired,
        posts: PropTypes.shape({
            DeviceId: PropTypes.string,
            DeviceSoftwareVersion: PropTypes.string,
            PhoneType: PropTypes.number,
            DeviceModel: PropTypes.string,
            DeviceManufacture: PropTypes.string,
            VersionSdk: PropTypes.string,
            VersionRelease: PropTypes.string,
            PackageName: PropTypes.string,
            Language: PropTypes.string,
            Country: PropTypes.string,
        }).isRequired,
        isFetching: PropTypes.bool.isRequired,
        lastUpdated: PropTypes.number,
        dispatch: PropTypes.func.isRequired
    }

    componentDidMount() {
        const { dispatch, selectedDevice } = this.props;
        dispatch(deviceActions.fetchPostsIfNeeded(selectedDevice));
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.selectedDevice !== this.props.selectedDevice) {
            const { dispatch, selectedDevice } = nextProps;
            dispatch(deviceActions.fetchPostsIfNeeded(selectedDevice));
        }
    }

    render() {
        const { selectedDevice, posts, isFetching, lastUpdated } = this.props;
        const isEmpty = posts === null;

        let {deviceInfo} = this.props;
        return (
            <View style={{ backgroundColor: "transparent", borderRadius: 5, position: 'absolute', bottom: 50, left: 20, right: 20 }} >
                {isEmpty ?
                    (isFetching ? <Text>加载中...</Text> : <Text>没有数据.</Text>)
                    : <Text >设备信息:{'\r\n'}
                    ======================================{'\r\n'}
                    设备ID：{posts.DeviceId} {'\r\n'}
                    网络制式：{posts.PhoneType} {'\r\n'}
                    设备型号：{posts.DeviceModel} {'\r\n'}
                    生产厂商：{posts.DeviceManufacture} {'\r\n'}
                    OS版本：{posts.DeviceSoftwareVersion} {'\r\n'}
                    SDK版本：{posts.VersionSdk} {'\r\n'}
                    软件版本：{posts.VersionRelease} {'\r\n'}
                    包名：{posts.PackageName} {'\r\n'}
                    语言：{posts.Language} {'\r\n'}
                    国家：{posts.Country} {'\r\n'}
                    ======================================={'\r\n'}
                    </Text>}
                {lastUpdated &&
                    <Text>
                        上次更新时间： {new Date(lastUpdated).toLocaleTimeString()}.
                    </Text>
                }
                <Text>{'\r\n\r\n'}</Text>
                <Text style={{ alignSelf: 'center', fontSize: 14, color: "black", paddingLeft: 2, paddingRight: 2, lineHeight: 14 }}
                    onPress={() => {this.props.navigator.pop();}} > 返 回 </Text>
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
        items: {},
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