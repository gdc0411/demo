import React, { Component } from 'react';
import {
    View,
    PropTypes,
} from 'react-native';

import { match } from 'react-router';
//加载路由配置
import RoutesConfig from './RoutesConfig';

/**
 *
 * 路由容器组件，装载具体页面
 * @class PageContainer
 * @extends {Component}
 */
class PageContainer extends Component {
    componentWillMount() {
        this.doMatch(this.props);
    }
    componentWillReceiveProps(newProps) {
        if (newProps.location !== this.props.location) {
            this.doMatch(newProps);
        }
    }
    doMatch(props) {
        const { location } = props;
        match({
            location,
            routes: RoutesConfig,
        }, (err, redirectLocation, renderProps) => {
            this.setState({ routerState: renderProps });
        });
    }
    render() {
        if (this.state.routerState) {
            const {routes, params, } = this.state.routerState;

            return [...routes].reduceRight((children, route) => {
                const Component = route.component;
                return Component ? (
                    <Component
                        route={route}
                        navigator={this.props.navigator}
                        params={params}
                        {...this.props.extraProps}
                        >
                        {children}
                    </Component>
                ) : children;
            }, null);
        }
        return <View />;
    }
}

export default PageContainer;