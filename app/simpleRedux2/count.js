//M model mvi里面的
//store  Redux
//内部变量 外部是不能访问的  只能通过特定的方式
//store state model 同一个概念  代表的都是数据的存储
//存储用来数据存储的全局变量

let c = 0;

import { trigger } from './event';
//action:{type:'plus1'}
//类似于Redux里面的纯函数 没有依赖的
export const _setVale = (c, action) => {
    return c + 1;
};

export const setValue = (value) => {
    trigger('counter-changed', value);
    c = value;
};

export const getValue = () => {
    return c;
};


