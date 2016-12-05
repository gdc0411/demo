import {NativeModules} from 'react-native';
import * as types from './types';

export const selectDevice = device => ({
  type: types.SELECT_DEVICE,
  device
});

export const invalidateDevice = device => ({
  type: types.INVALIDATE_DEVICE,
  device
});

export const requestPosts = device => ({
  type: types.REQUEST_POSTS,
  device
});

export const receivePosts = (device, json) => ({
  type: types.RECEIVE_POSTS,
  device,
  posts: json,
  receivedAt: Date.now()
});
// posts: json.data.children.map(child => child.data),

const fetchPosts = device => dispatch => {
  dispatch(requestPosts(device));
  // return fetch(`https://www.reddit.com/r/${device}.json`)
  //   .then(response => response.json())
  //   .then(json => dispatch(receivePosts(device, json)));

  return NativeModules.DeviceModule.getDeviceIdentifier()
      .then(json => {dispatch(receivePosts(device, json)); console.log(json); } )
      .catch(err => dispatch(receivePosts(device, err)));
};

const shouldFetchPosts = (state, device) => {
  const posts = state.postsByDevice[device];
  if (!posts) {
    return true;
  }
  if (posts.isFetching) {
    return false;
  }
  return posts.didInvalidate;
};

export const fetchPostsIfNeeded = device => (dispatch, getState) => {
  if (shouldFetchPosts(getState(), device)) {
    return dispatch(fetchPosts(device));
  }
};
