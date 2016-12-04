/**
 * Reducer
 */
import * as types from '../actions/types';

/**
 * state store model
 */
const calculate = (state = { value: 0 }, action) => {
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
};

export default calculate;