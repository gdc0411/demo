'use strict';

import React, {Component} from 'react';
import {
    AppRegistry,
    Alert,
} from 'react-native';

import codePush from "react-native-code-push";

//import App from './app';
//import App from './app/simpleRedux/app';
//import App from './app/simpleRedux2/app';
//import App from './app/simpleRedux3/app';
import App from './app/counter/index';

class DemoProject extends Component {

    codePushStatusDidChange(status) {
        switch (status) {
            case codePush.SyncStatus.CHECKING_FOR_UPDATE:
                console.log("Checking for updates.");
                break;
            case codePush.SyncStatus.DOWNLOADING_PACKAGE:
                console.log("Downloading package.");
                break;
            case codePush.SyncStatus.INSTALLING_UPDATE:
                console.log("Installing update.");
                break;
            case codePush.SyncStatus.UP_TO_DATE:
                console.log("Installing update.");
                break;
            case codePush.SyncStatus.UPDATE_INSTALLED:
                console.log("Update installed.");
                break;
        }
    }

    codePushDownloadDidProgress(progress) {
        console.log(progress.receivedBytes + " of " + progress.totalBytes + " received.");
    }

    componentDidMount() {
        console.log('组件加载后执行');

        //访问慢,不稳定
        codePush.checkForUpdate().then((update) => {
            if (!update) {
                Alert.alert("提示", "已是最新版本--", [
                    {
                        text: "Ok", onPress: () => {
                            console.log("点了OK");
                        }
                    }
                ]);
            }else {
                codePush.sync({ updateDialog: true, installMode: codePush.InstallMode.IMMEDIATE });
            }
        });
    }

    render() {
        return (
            <App />
        );
    }
}

DemoProject = codePush(DemoProject);

AppRegistry.registerComponent('DemoProject', () => DemoProject);
