/** 
 * Reducers 
*/

import { combineReducers } from 'redux';
import { ADD_TODO, TOGGLE_TODO, SET_VISIBILITY_FILER, VisibilityFilters } from './actions';
const {SHOW_ALL} = VisibilityFilters;


/**
 * 过滤处理reducer 
 * 
 * @param {any} [state=SHOW_ALL]
 * @param {any} action
 * @returns
 */
function visibilityFilter(state = SHOW_ALL, action) {
    switch (action.type) {
        case SET_VISIBILITY_FILER:
            return action.filter;
        default:
            return state;
    }
}

/**
 * 创建和切换操作reducer
 * 
 * @param {any} [state=[]]
 * @param {any} action
 * @returns
 */
function todos(state = [], action) {
    switch (action.type) {
        case ADD_TODO:
            return [
                ...state,
                {
                    text: action.text,
                    completed: false
                }
            ];
        case TOGGLE_TODO:
            return state.map((todo, index) => {
                if (index === action.index) {
                    return Object.assign({}, todo, {
                        completed: !todo.completed
                    });
                }
                return todo;
            });
        default:
            return state;
    }
}

/**
 * 生成root reducer
 */
const todoApp = combineReducers({
    visibilityFilter,
    todos
});