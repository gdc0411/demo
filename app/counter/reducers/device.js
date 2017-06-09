/**
 * Reducer
 */
import * as types from '../actions/types';

export const selectedDevice = (state = 'reactjs', action) => {
    switch (action.type) {
        case types.SELECT_DEVICE:
            return action.device;
        default:
            return state;
    }
};

const posts = (state = { isFetching: false, didInvalidate: false, items: {} }, action) => {
    switch (action.type) {
        case types.INVALIDATE_DEVICE:
            return {
                ...state,
                didInvalidate: true
            };
        case types.REQUEST_POSTS:
            return {
                ...state,
                isFetching: true,
                didInvalidate: false
            };
        case types.RECEIVE_POSTS:
            return {
                ...state,
                isFetching: false,
                didInvalidate: false,
                items: action.posts,
                lastUpdated: action.receivedAt
            };
        default:
            return state;
    }
};

export const postsByDevice = (state = {}, action) => {
    switch (action.type) {
        case types.INVALIDATE_DEVICE:
        case types.RECEIVE_POSTS:
        case types.REQUEST_POSTS:
            return {
                ...state,
                [action.device]: posts(state[action.device], action)
            };
        default:
            return state;
    }
};