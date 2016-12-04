/**
 * Reducer
 */
import * as types from '../actions/types';

const play = (state = {}, action) => {
    switch (action.type) {
        case types.PLAY_TODO:
            return Object.assign({}, state, {
                ...state,
                datasouce: action.datasource,
            });
        default:
            return state;
    }
};

export default play;