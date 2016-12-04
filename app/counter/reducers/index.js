/**
 * Reducer
 */
import { combineReducers } from 'redux';
import calculate  from './calc';
import device from './device';
import play from './play';

/**
 * 生成root reducer
 */
const rootReducer = combineReducers({
    calculate,
    play,
    device
});

export default rootReducer;