
let events = {};

export const on = (name, handler) => {
    const listeners = events[name];
    if (!listeners) {
        events[name] = [];
    }
    events[name].push(handler);
};


export const trigger = (name, data) => {
    const listeners = events[name];
    for (let i = 0; i < listeners.length; i++) {
        listeners[i](data);
    }
};


export const remove = (name, handler) => {
    if (!events[name]) {
        const listeners = events[name];
        //最终会把listeners里中的handler删掉
        //find是找出不等于handler的所有
        events[name] = listeners.find(
            v => v !== handler
        );
    }
};