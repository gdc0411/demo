/**
 * 路由表
*/
import React, { Component } from 'react';
import { Route } from 'react-router';
import home from '../containers/home';
import play from '../containers/play';
import device from '../containers/device';
import social from '../containers/social';
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
            path: '/social',
            component: social
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
