/*************************************************************************
 * Description: 下载列表DEMO
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-01-26
 ************************************************************************/
'use strict';

import React, { Component } from 'react';

import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
} from 'react-native';
import * as Progress from 'react-native-progress';

import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import Orientation from '../componets/RCTOrientation';
import Download from '../componets/RCTDownload';
import Cache from '../componets/RCTCache';

class download extends Component {
  constructor(props) {
    super(props);

    this.state = {
      progress: 0,
      indeterminate: true,
      downloadList: [],
    };
  }

  componentWillMount() {
    Download.addListUpdateListener(this.handleDownloadList);
    Cache.addCacheUpdateListener(this.handleCacheUpdate);
  }

  componentDidMount() {
    // this.animate();
    Download.list();
    Cache.calc();
  }

  componentWillUnmount() {
    Download.removeListUpdateListener(this.handleDownloadList);
    Cache.removeCacheUpdateListener(this.handleCacheUpdate);
  }

  handleDownloadList = (message) => {
    console.log("ListUpdate:", message);
    //alert('ListUpdate' + JSON.stringify(message));
    this.setState({ downloadList: message });
  }

  handleCacheUpdate = (message) => {
    console.log("CacheUpdate:", message);
    alert('CacheUpdate' + JSON.stringify(message));
  }

  animate() {
    let progress = 0;
    this.setState({ progress });
    setTimeout(() => {
      this.setState({ indeterminate: false });
      setInterval(() => {
        progress += Math.random() / 5;
        if (progress > 1) {
          progress = 1;
        }
        this.setState({ progress });
      }, 500);
    }, 1500);
  }

  render() {

    let downloadList = this.state.downloadList;
    let pages = [];

    for (let i = 0; i < downloadList.length; i++) {

      let downloadState = downloadList[i].downloadState;

      let showProgress = false;
      let showPause = false;
      let showResume = false;
      let showRetry = false;
      let showPlay = false;
      let showDelete = true;
      let indeterminate = true;
      let showTips = '';

      switch (downloadState) {
        case Download.DOWLOAD_STATE_WAITING:
          indeterminate = true;
          showTips = '等待中';
          break;
        case Download.DOWLOAD_STATE_DOWNLOADING:
          indeterminate = false;
          showTips = '下载中';
          showPause = true;
          break;
        case Download.DOWLOAD_STATE_STOP:
          indeterminate = false;
          showTips = '下载暂停';
          showResume = true;
          break;
        case Download.DOWLOAD_STATE_SUCCESS:
          indeterminate = false;
          showTips = '下载成功';
          showPlay = true;
          break;
        case Download.DOWLOAD_STATE_FAILED:
          indeterminate = false;
          showTips = '下载失败';
          showRetry = true;
          break;
        case Download.DOWLOAD_STATE_NO_DISPATCH:
          showTips = '排队中';
          showRetry = true;
          break;
        case Download.DOWLOAD_STATE_NO_PERMISSION:
          indeterminate = false;
          showTips = '没有权限下载';
          showRetry = true;
          break;
        case Download.DOWLOAD_STATE_URL_REQUEST_FAILED:
          indeterminate = false;
          showTips = '视频url请求失败';
          showRetry = true;
          break;
        case Download.DOWLOAD_STATE_DISPATCHING:
          showTips = '正在调度中';
          break;
        default:
          break;
      }

      let progress = downloadList[i].progress / downloadList[i].fileLength;

      pages.push(
        <View>
          <Text key={i}>{downloadList[i].id + `|` + downloadList[i].fileName}</Text>
          <Progress.Bar style={styles.progress} progress={progress} indeterminate={indeterminate} />
          <View style={{ flexDirection: 'row', justifyContent: 'center' }} >
            <Text style={styles.instructions}> {showTips}     </Text>
            {showPause ?
              <TouchableOpacity onPress={() => Download.pause({ id: downloadList[i].id, vuid: downloadList[i].vuid })}>
                <Text style={styles.instructions}> 暂停 </Text>
              </TouchableOpacity> : null
            }
            {showResume ?
              <TouchableOpacity onPress={() => Download.resume({ id: downloadList[i].id, vuid: downloadList[i].vuid })}>
                <Text style={styles.instructions}> 恢复 </Text>
              </TouchableOpacity> : null
            }
            {showRetry ?
              <TouchableOpacity onPress={() => Download.retry({ id: downloadList[i].id, vuid: downloadList[i].vuid })}>
                <Text style={styles.instructions}> 重试 </Text>
              </TouchableOpacity> : null
            }
            {showPlay ?
              <TouchableOpacity onPress={() => this.props.navigator.push({ location: '/play/' + encodeURIComponent(downloadList[i].fileSavePath), })}>
                <Text style={styles.instructions}> 播放 </Text>
              </TouchableOpacity> : null
            }
            {showDelete ?
              <TouchableOpacity onPress={() => Download.delete({ id: downloadList[i].id, vuid: downloadList[i].vuid })}>
                <Text style={styles.instructions}> 删除 </Text>
              </TouchableOpacity> : null
            }
          </View>
        </View>
      );
    }

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>下载视频列表</Text>
        <View>
          {pages.map((elem, index) => { return elem; })}
        </View>
        <Text style={styles.welcome}>缓存大小</Text>
        <TouchableOpacity onPress={(para) => Cache.clear() } style={styles.button}>
          <Text style={styles.instructions}>
            清 除 缓 存
            </Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={(para) => this.props.navigator.pop()} style={styles.button}>
          <Text style={styles.instructions}>
            返 回
            </Text>
        </TouchableOpacity>
      </View>
    );
  }
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
    paddingVertical: 20,
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  circles: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  progress: {
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  buttonContainer: {
    flex: 0,
    flexDirection: 'row',
    justifyContent: 'space-around'
  },
  button: {
    padding: 5,
    margin: 5,
    borderWidth: 1,
    borderColor: 'white',
    borderRadius: 3,
    backgroundColor: 'grey',
  }
});


//配置Map映射表，拿到自己关心的数据
const mapStateToProps = state => ({
  //state.xxx必须与reducer同名
  // value: state.calculate.value,
});


const mapDispatchToProps = dispatch => ({
  // actions: bindActionCreators(calcActions, dispatch)
});

//连接Redux
export default connect(mapStateToProps, mapDispatchToProps)(download);