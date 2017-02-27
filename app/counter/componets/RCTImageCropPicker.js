/*************************************************************************
 * Description: 图片选择截取组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-02-27
 * Modified Time: 2017-02-27
 ************************************************************************/
'use strict';

import React, { PropTypes } from 'react';
import { NativeModules } from 'react-native';

const ImagePickerAPI = NativeModules.ImageCropPickerModule;

/**
 *
 * 打开图片剪辑器
 *
 * @export
 * @param {Object} options 提取参数
 * @param {Function} success 成功回调
 * @param {Function} error 失败回调
 * @returns
 */
export function openCropper(options, success: Function, error: Function) {
    return ImagePickerAPI.openCropper(options).then((resp) => success && success(resp)).catch(e => error && error(e));
}

/**
 * 打开相册提取
 *
 * @export
 * @param {Object} options 提取参数
 * @param {Function} success 成功回调
 * @param {Function} error 失败回调
 * @returns
 */
export function openPicker(options, success: Function, error: Function) {
    return ImagePickerAPI.openPicker(options).then((resp) => success && success(resp)).catch(e => error && error(e));
}

/**
 * 打开摄像头提取照片
 *
 * @export
 * @param {Object} options 提取参数
 * @param {Function} success 成功回调
 * @param {Function} error 失败回调
 * @returns
 */
export function openCamera(options, success: Function, error: Function) {
    return ImagePickerAPI.openCamera(options).then((resp) => success && success(resp)).catch(e => error && error(e));
}

/**
 * 清除指定临时文件
 *
 * @export
 * @param {string} pathToDelete  需要删除的文件路径
 * @param {Function} success 成功回调
 * @param {Function} error 失败回调
 * @returns
 */
export function cleanSingle(pathToDelete, success: Function, error: Function) {
    return ImagePickerAPI.cleanSingle(pathToDelete).then(() => success && success()).catch(e => error && error(e));
}

/**
 * 清除全部临时文件
 *
 * @export
 * @param {Function} success 成功回调
 * @param {Function} error 失败回调
 * @returns
 */
export function clean(success: Function, error: Function) {
    return ImagePickerAPI.clean().then(() => success && success()).catch(e => error && error(e));;
}



export default NativeModules.ImageCropPickerModule;