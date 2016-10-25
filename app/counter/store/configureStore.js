//导入框架
import { Platform } from 'react-native';
import {createStore, combineReducers, applyMiddleware, compose} from 'redux';
import thunk from 'redux-thunk';
import createLogger from 'redux-logger';  //日志工具

//导入所有的reducer
import rootReducer from '../reducers/reducer';


const middleware = [thunk];
if (process.env.NODE_ENV !== 'production') {
    middleware.push(createLogger());
}

const enhancer = compose(
    applyMiddleware(
        ...middleware
        // other store enhancers if any
    ),
    global.reduxNativeDevTools ?
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
  if (global.reduxNativeDevTools) {
    global.reduxNativeDevTools.updateStore(configureStore);
  }

export default configureStore;