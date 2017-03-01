'use strict';

import React, { Component } from "react";
import {
  AppRegistry,
  Dimensions,
  Image,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";

import CodePush from "react-native-code-push";


//import App from './app';
//import App from './app/simpleRedux/app';
//import App from './app/simpleRedux2/app';
//import App from './app/simpleRedux3/app';
import App from './counter/index';

const enableHotUpdate = !__DEV__;

class LeDemo extends Component {
  constructor() {
    super();
    this.state = { restartAllowed: true };
  }

  codePushStatusDidChange(syncStatus) {
    switch (syncStatus) {
      case CodePush.SyncStatus.CHECKING_FOR_UPDATE:
        this.setState({ syncMessage: "检查更新." });
        break;
      case CodePush.SyncStatus.DOWNLOADING_PACKAGE:
        this.setState({ syncMessage: "下载更新包." });
        break;
      case CodePush.SyncStatus.AWAITING_USER_ACTION:
        this.setState({ syncMessage: "等待用户操作." });
        break;
      case CodePush.SyncStatus.INSTALLING_UPDATE:
        this.setState({ syncMessage: "安装更新." });
        break;
      case CodePush.SyncStatus.UP_TO_DATE:
        this.setState({ syncMessage: "最新版本.", progress: false });
        break;
      case CodePush.SyncStatus.UPDATE_IGNORED:
        this.setState({ syncMessage: "用户取消更新.", progress: false });
        break;
      case CodePush.SyncStatus.UPDATE_INSTALLED:
        this.setState({ syncMessage: "重启后安装更新.", progress: false });
        break;
      case CodePush.SyncStatus.UNKNOWN_ERROR:
        this.setState({ syncMessage: "更新出错.", progress: false });
        break;
    }
  }

  codePushDownloadDidProgress(progress) {
    this.setState({ progress });
  }

  toggleAllowRestart() {
    this.state.restartAllowed
      ? CodePush.disallowRestart()
      : CodePush.allowRestart();

    this.setState({ restartAllowed: !this.state.restartAllowed });
  }

  getUpdateMetadata() {
    CodePush.getUpdateMetadata(CodePush.UpdateState.RUNNING)
      .then((metadata: LocalPackage) => {
        this.setState({ syncMessage: metadata ? JSON.stringify(metadata) : "Running binary version", progress: false });
      }, (error: any) => {
        this.setState({ syncMessage: "Error: " + error, progress: false });
      });
  }

  /** Update is downloaded silently, and applied on restart (recommended) */
  sync() {
    CodePush.sync(
      {},
      this.codePushStatusDidChange.bind(this),
      this.codePushDownloadDidProgress.bind(this)
    );
  }

  /** Update pops a confirmation dialog, and then immediately reboots the app */
  syncImmediate() {
    CodePush.sync(
      {
        updateDialog: {
          appendReleaseDescription: true,
          descriptionPrefix: "\n\n更新日志:\n",
          title: "更新提示",
          optionalIgnoreButtonLabel: "忽略",
          optionalInstallButtonLabel: "安装",
          optionalUpdateMessage: "新版本已发布，是否安装？",
          mandatoryContinueButtonLabel: "继续",
          mandatoryUpdateMessage: "请安装必要的更新",
        },
        installMode: CodePush.InstallMode.IMMEDIATE,
      },
      this.codePushStatusDidChange.bind(this),
      this.codePushDownloadDidProgress.bind(this),
    );
  }


  componentDidMount() {
    if (enableHotUpdate) {
      alert('开启热更新');
      CodePush.sync(
        {
          updateDialog: {
            appendReleaseDescription: true,
            descriptionPrefix: "\n\n更新日志:\n",
            title: "更新提示",
            optionalInstallButtonLabel: "立即安装",
            optionalIgnoreButtonLabel: "忽略",
            optionalUpdateMessage: "新版本已发布，是否安装？",
            mandatoryContinueButtonLabel: "继续",
            mandatoryUpdateMessage: "请安装必要的更新",
          },
          installMode: CodePush.InstallMode.IMMEDIATE,

        },
      );
    }
  }

  render() {
    let progressView;
    if (this.state.progress) {
      progressView = (
        <Text style={styles.messages}>{this.state.progress.receivedBytes} of {this.state.progress.totalBytes} bytes received</Text>
      );
    }

    return (
      /*<View style={styles.container}>
        <Text style={styles.welcome}>
          热更新测试(双平台)
        </Text>
        <TouchableOpacity onPress={this.sync.bind(this)}>
          <Text style={styles.syncButton}>点击后台更新</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={this.syncImmediate.bind(this)}>
          <Text style={styles.syncButton}>点击对话框更新</Text>
        </TouchableOpacity>
        {progressView}
        <Image style={styles.image} resizeMode={Image.resizeMode.contain} source={require("./img/laptop_phone_howitworks.png")} />
        <TouchableOpacity onPress={this.toggleAllowRestart.bind(this)}>
          <Text style={styles.restartToggleButton}>重启 {this.state.restartAllowed ? "允许" : "禁止"}</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={this.getUpdateMetadata.bind(this)}>
          <Text style={styles.syncButton}>点击更新Metadata，哈哈</Text>
        </TouchableOpacity>
        <Text style={styles.messages}>{this.state.syncMessage || ""}</Text>
      </View>*/
      <App />
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    backgroundColor: "#F5FCFF",
    paddingTop: 50
  },
  image: {
    margin: 30,
    width: Dimensions.get("window").width - 100,
    height: 365 * (Dimensions.get("window").width - 100) / 651,
  },
  messages: {
    marginTop: 30,
    textAlign: "center",
  },
  restartToggleButton: {
    color: "blue",
    fontSize: 17
  },
  syncButton: {
    color: "green",
    fontSize: 17
  },
  welcome: {
    fontSize: 20,
    textAlign: "center",
    margin: 20
  },
});


if (enableHotUpdate) {
  /**
   * Configured with a MANUAL check frequency for easy testing. For production apps, it is recommended to configure a
   * different check frequency, such as ON_APP_START, for a 'hands-off' approach where CodePush.sync() does not
   * need to be explicitly called. All options of CodePush.sync() are also available in this decorator.
  */
  let codePushOptions = { checkFrequency: CodePush.CheckFrequency.MANUAL };
  LeDemo = CodePush(codePushOptions)(LeDemo);
}

AppRegistry.registerComponent("LeDemo", () => LeDemo);