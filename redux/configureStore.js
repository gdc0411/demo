
'use strict';

import {
    createStore,
    combineReducers,
    applyMiddleware
} from 'redux';

//导入所有的reducer
import {calculate} from './reducer';

//创建根Reducer
const rootReducer = combineReducers({
    calculate,
});

let store = createStore(rootReducer);

export const getStore = () => {
    return store;
};





