/*************************************************************************
 * Description: 下载组件示例
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-01-25
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

class download extends Component {
  constructor(props) {
    super(props);

    this.state = {
      progress: 0,
      indeterminate: true,
    };
  }

  componentWillMount() {
    Download.addItemUpdateListener(this.handleDownloadList);
  }

  componentDidMount() {
    this.animate();
  }

  componentWillUnmount() {
    Download.removeItemUpdateListener(this.handleDownloadList);
  }

  handleDownloadList(message) {
    console.log("ItemUpdate:", message);
    alert('ItemUpdate' + JSON.stringify(message));
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
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Progress Example</Text>
        <Progress.Bar
          style={styles.progress}
          progress={this.state.progress}
          indeterminate={this.state.indeterminate}
          />
        <View style={styles.circles}>
          <Progress.Circle
            style={styles.progress}
            progress={this.state.progress}
            indeterminate={this.state.indeterminate}
            />
          <Progress.Pie
            style={styles.progress}
            progress={this.state.progress}
            indeterminate={this.state.indeterminate}
            />
          <Progress.Circle
            style={styles.progress}
            progress={this.state.progress}
            indeterminate={this.state.indeterminate}
            direction="counter-clockwise"
            />
        </View>
        <View style={styles.circles}>
          <Progress.CircleSnail
            style={styles.progress}
            />
          <Progress.CircleSnail
            style={styles.progress}
            color={[
              '#F44336',
              '#2196F3',
              '#009688',
            ]}
            />
        </View>
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