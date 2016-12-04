
import * as types from './types';

export const selectDevice = device => ({
  type: SELECT_DEVICE,
  device
});

export const invalidateDevice = device => ({
  type: INVALIDATE_DEVICE,
  device
});

export const requestPosts = device => ({
  type: REQUEST_POSTS,
  device
});

export const receivePosts = (device, json) => ({
  type: RECEIVE_POSTS,
  device,
  posts: json.data.children.map(child => child.data),
  receivedAt: Date.now()
});

const fetchPosts = device => dispatch => {
  dispatch(requestPosts(device));
  return fetch(`https://www.device.com/r/${device}.json`)
    .then(response => response.json())
    .then(json => dispatch(receivePosts(device, json)));
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
