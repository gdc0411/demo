/**
 * Reducer
 */
import { combineReducers } from 'redux';

const initialState = { value: 0 };

/**
 * state store model
 */
function calculate(state = initialState, action) {
    switch (action.type) {
        case 'PLUS':
            return Object.assign({}, state, {
                value: state.value + action.number
            });
        default:
            return state;
    }
}

/**
 * 生成root reducer
 */
const rootReducer = combineReducers({
    calculate
});

export default rootReducer;