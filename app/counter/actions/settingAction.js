/**
 * Setting Action
 */
'use strict';

import * as types from './types';
import { AsyncStorage } from "react-native";

function setSetting(data) {
    return {
        type: types.SET_CONFIG,
        data: data
    };
};

export const loadConfig = (key) => dispatch => {
    return AsyncStorage.getItem(key)
        .then((resp) => {
            if (resp) {
                // alert(resp);
                const json = JSON.parse(resp);
                dispatch(setSetting(json));
            } else {
                let defData = {
                    skipHeadTail: true,
                    noWifiCache: true,
                    acceptPush: true
                };
                dispatch(setSetting(defData));
                // setConfig(key, JSON.stringify(defData));
                // alert(JSON.stringify(defData));
            }
        })
        .catch((error) => {
            alert(JSON.stringify(error));
        })
        .done();
};


export const setConfig = (key, val) => dispatch => {

    dispatch(setSetting(val));

    return AsyncStorage.setItem(key, JSON.stringify(val))
        .then((resp) => {
            // alert(resp);
            // dispatch(setSetting(val));
        })
        .catch((error) => {
            alert(JSON.stringify(error));
        })
        .done();
};