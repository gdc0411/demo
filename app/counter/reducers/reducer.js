/**
 * Reducer
 */
import { combineReducers } from 'redux';

import * as types from '../actions/types';

/**
 * state store model
 */
function calculate(state = { value: 0 }, action) {
    switch (action.type) {
        case types.ADD_TODO:
            return Object.assign({}, state, {
                ...state,
                value: state.value + action.number
            });
            break;

        case types.MINUS_TODO:
            return Object.assign({}, state, {
                ...state,
                value: state.value - action.number
            });
            break;

        case types.TIMES_TODO:
            return Object.assign({}, state, {
                ...state,
                value: state.value * action.number
            });
            break;

        case types.DIVIDE_TODO:
            if (action.number !== 0){
                return Object.assign({}, state, {
                    ...state,
                    value: state.value / action.number
                });
            }else{
                return Object.assign({}, state, {
                    ...state,
                    value: 0,
                });
            }            
            break;
            
        default:
            return state;
    }
}


function play(state = {}, action) {
    switch (action.type) {
        case types.PLAY_TODO:
            return Object.assign({}, state, {
                ...state,
                datasouce: action.datasource,
            });
        default:
            return state;
    }
}


/**
 * 生成root reducer
 */
const rootReducer = combineReducers({
    calculate,
    play
});

export default rootReducer;