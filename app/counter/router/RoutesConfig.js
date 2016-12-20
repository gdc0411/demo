/**
 * 路由表
*/
import React, { Component } from 'react';
import { Route } from 'react-router';
import home from '../containers/home';
import play from '../containers/play';
import device from '../containers/device';
import share from '../containers/share';
import orient from '../containers/orient';

export default module.exports = {
    path: '/',
    indexRoute: {
        component: home,
    },
    childRoutes: [
        {
            path: '/device',
            component: device
        },
        {
            path: '/share',
            component: share
        },
        {
            path: '/orient',
            component: orient
        },
        {
            path: '/play/:src',
            component: play
        },
    ],
};
