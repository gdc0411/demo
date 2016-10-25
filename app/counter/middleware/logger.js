/**
 * 记录所有被发起的 action 以及产生的新的 state。
 */
export const logger = store => next => action => {
    //console.group(action.type);
    console.log('dispatching', action);
    let result = next(action);
    console.log('next state', store.getState());
    //console.groupEnd(action.type);
    return result;
};