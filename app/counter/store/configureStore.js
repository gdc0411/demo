//导入框架
import { Platform } from 'react-native';
import {createStore, combineReducers, applyMiddleware, compose} from 'redux';
import thunk from 'redux-thunk';
import createLogger from 'redux-logger';  //日志工具

//导入所有的reducer
import rootReducer from '../reducers/reducer';

const dev_env = (process.env.NODE_ENV !== 'production');
const middleware = [thunk];
// 用log显示Action变化
// if (process.env.NODE_ENV !== 'production') {
//     middleware.push(createLogger());
// }

const enhancer = compose(
    applyMiddleware(
        ...middleware
        // other store enhancers if any
    ),
    //使用RND Tools调试
    (global.reduxNativeDevTools) && dev_env ?
        global.reduxNativeDevTools(/*options*/) :
        noop => noop
);

/**
 * 创建store状态树，并关联reducer，加入middleware中间件
 * @param {any} preloadedState
 */
const configureStore = preloadedState => createStore(
    rootReducer,
    preloadedState,
    enhancer
);


// If you have other enhancers & middlewares
// update the store after creating / changing to allow devTools to use them
//使用RND Tools调试
if (global.reduxNativeDevTools && dev_env) {
    global.reduxNativeDevTools.updateStore(configureStore);
}

export default configureStore;