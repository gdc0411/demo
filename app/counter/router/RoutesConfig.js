/**
 * 路由表
*/
import React, { Component } from 'react';
import { Route } from 'react-router';
import home from '../containers/home';
import play from '../containers/play';
import push from '../containers/push';
import device from '../containers/device';
import social from '../containers/social';
import orient from '../containers/orient';
import download from '../containers/download';

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
            path: '/social',
            component: social
        },
        {
            path: '/orient',
            component: orient
        },
        {
            path: '/download',
            component: download
        },
        {
            path: '/play/:src',
            component: play
        },
        {
            path: '/push/:para',
            component: push
        },
    ],
};
