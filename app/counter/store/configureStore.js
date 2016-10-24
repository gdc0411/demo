
import {
    createStore,
    combineReducers,
    applyMiddleware
} from 'redux';
import thunk from 'redux-thunk';
import createLogger from 'redux-logger';

//导入所有的reducer
import rootReducer from '../reducers/reducer';

/**
 * 让你可以发起一个函数来替代 action。
 * 这个函数接收 `dispatch` 和 `getState` 作为参数。
 *
 * 对于（根据 `getState()` 的情况）提前退出，或者异步控制流（ `dispatch()` 一些其他东西）来说，这非常有用。
 * `dispatch` 会返回被发起函数的返回值。
 */
// const thunk = store => next => action => {
//     typeof action === 'function' ? action(store.dispatch, store.getState) : next(action);
// };

/**
 * 记录所有被发起的 action 以及产生的新的 state。
 */
const logger = store => next => action => {
    //console.group(action.type);
    console.log('dispatching', action);
    let result = next(action);
    console.log('next state', store.getState());
    //console.groupEnd(action.type);
    return result;
};

/**
 * 在 state 更新完成和 listener 被通知之后发送崩溃报告。
 */
const crashReporter = store => next => action => {
    try {
        return next(action);
    } catch (err) {
        console.error('Caught an exception!', err);
        Raven.captureException(err, {
            extra: {
                action,
                state: store.getState(),
            }
        });
        throw err;
    }
};


const middleware = [thunk];
if (process.env.NODE_ENV !== 'production') {
    middleware.push(createLogger());
}

/**
 * 创建store状态树，并关联reducer，加入middleware中间件
 * 
 * @param {any} preloadedState
 */
const configureStore = preloadedState => createStore(
    rootReducer,
    preloadedState,
    applyMiddleware(
        ...middleware
    )
);

export default configureStore;






