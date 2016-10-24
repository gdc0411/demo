/**
 * 路由表
*/
import React, { Component } from 'react';
import { Route } from 'react-router';

import Index from './containers/Root';


export default module.exports = {
    path: '/',
    indexRoute: {
        component: Index,
    },
    // childRoutes: [
    //     {
    //         path: '/article/:id',
    //         component: Article
    //     },
    // ],
};