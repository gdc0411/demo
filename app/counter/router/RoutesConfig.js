/**
 * 路由表
*/
import React, { Component } from 'react';
import { Route } from 'react-router';
import home from '../containers/home';
import play from '../containers/play';
import playIOS from '../containers/playIOS';

export default module.exports = {
    path: '/',
    indexRoute: {
        component: home,
    },
    childRoutes: [
        {
            path: '/play/:src',
            component: play
        },
        {
            path: '/playIOS/:src',
            component: playIOS
        },
    ],
};
