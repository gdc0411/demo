/**
 * 路由表
*/
import React, { Component } from 'react';
import { Route } from 'react-router';
import home from '../containers/play';

export default module.exports = {
    path: '/',
    indexRoute: {
        component: home,
    },
    // childRoutes: [
    //     {
    //         path: '/article/:id',
    //         component: Article
    //     },
    // ],
};
