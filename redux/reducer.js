/**
 * Reducer
 */

'use strict';

const initialState = { value: 0 };

/**
 * state store model
 */
export const calculate = (state = initialState, action) => {
    switch (action.type) {
        case 'PLUS':
            state = { value: state.value + action.number };
            break;

        default:
            break;
    }

    return state;
};