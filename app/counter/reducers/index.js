/**
 * Reducer
 */
import { combineReducers } from 'redux';
import calculate  from './calc';
import { selectedDevice, postsByDevice } from './device';
import setting from './setting';

/**
 * 生成root reducer
 */
const rootReducer = combineReducers({
    calculate,
    setting,
    selectedDevice,
    postsByDevice
});

export default rootReducer;