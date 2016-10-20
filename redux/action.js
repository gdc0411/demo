/**
 * Action
 */
'use strict';

/**
* Action Creator
* @param {any} number
*/
export const plus = (number) => ({
    type: 'PLUS',
    number: number,
});


//调用plus(1)
//返回{type:'PLUS',number:1}
//并没有格式的推荐，method也可以