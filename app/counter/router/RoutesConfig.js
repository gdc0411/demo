/**
 * 路由表
*/
import React, { Component } from 'react';
import { Route } from 'react-router';
import home from '../containers/home';
import play from '../containers/play';
import device from '../containers/device';

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
            path: '/play/:src',
            component: play
        },
    ],
};
