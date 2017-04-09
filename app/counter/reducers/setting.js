/**
 * Setting Reducer
 */
import * as types from '../actions/types';

const setting = (state = {}, action) => {
    switch (action.type) {
        case types.SET_CONFIG:
            return Object.assign({}, state, {
                ...state,                
                ...action.data
            });
        default:
            return state;
    }
};

export default setting;