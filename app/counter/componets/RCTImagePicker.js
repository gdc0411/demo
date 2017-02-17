/*************************************************************************
 * Description: 相册图片选择组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-02-18
 * Modified Time: 2017-02-18
 ************************************************************************/
'use strict';

const { NativeModules } = require('react-native');
const ImagePickerAPI = NativeModules.ImagePickerModule;

const DEFAULT_OPTIONS = {
  title: '选取图片或视频',
  cancelButtonTitle: '取消',
  takePhotoButtonTitle: '拍摄',
  chooseFromLibraryButtonTitle: '从手机相册选择',
  quality: 1.0,
  allowsEditing: false
};

module.exports = {
  ...ImagePickerAPI,  
  showImagePicker: function showImagePicker(options, callback) {
    if (typeof options === 'function') {
      callback = options;
      options = {};
    }
    return ImagePickerAPI.showImagePicker({...DEFAULT_OPTIONS, ...options}, callback);
  }
};