/**
 * Action
 */
import * as types from './types';

export const plus = number => ({
    type: types.ADD_TODO,
    number
});

export const minus = number => ({
    type: types.MINUS_TODO,
    number
});

export const times = number => ({
    type: types.TIMES_TODO,
    number
});

export const divide = number => ({
    type: types.DIVIDE_TODO,
    number
});



//调用plus(1)
//返回{type:'PLUS',number:1}
//并没有格式的推荐，method也可以