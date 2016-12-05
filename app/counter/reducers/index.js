/**
 * Reducer
 */
import { combineReducers } from 'redux';
import calculate  from './calc';
import { selectedDevice, postsByDevice } from './device';
import play from './play';

/**
 * 生成root reducer
 */
const rootReducer = combineReducers({
    calculate,
    play,
    selectedDevice,
    postsByDevice
});

export default rootReducer;