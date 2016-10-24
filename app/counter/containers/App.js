import React, { Component } from 'react';
import {
    Navigator,
    PropTypes,
    Platform,
    BackAndroid,
} from 'react-native';
import { Provider } from 'react-redux';

import PageContainer from './PageContainer';

const INITIAL_ROUTE = {
    location: '/',
};

class App extends Component {

    configureScene = route => {
        if (route.configure) {
            return route.configure;
        }
        if (Platform.OS === 'ios') {
            return Navigator.SceneConfigs.PushFromRight;
        }
        return Navigator.SceneConfigs.FloatFromBottomAndroid;
    };

    renderScene = (route, navigator) => (
        <PageContainer
            extraProps={route.extraProps}
            location={route.location}
            navigator={navigator}
            rootBackAndroid={this.onBackAndroid}
            />
    );
    render() {
        const { store } = this.props;
        return (
            <Provider store={store} key="provider">
                <Navigator
                    ref="navigator"
                    initialRoute={INITIAL_ROUTE}
                    configureScene={this.configureScene}
                    renderScene={this.renderScene}
                    />
            </Provider>
        );
    }
}


export default App;