package com.lecloud.valley.modules;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;


import static com.lecloud.valley.common.Constants.REACT_CLASS_IMAGE_CROP_PICKER_MODULE;

/**
 * Created by RaoJia on 2017/2/27.
 */

public class CropPickerModule extends ReactBaseModule {

    public CropPickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS_IMAGE_CROP_PICKER_MODULE;
    }

    @Override
    public void initialize() {
        super.initialize();
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null) {
            func = new CropPickerFunc(mReactContext, mEventEmitter);
        }
    }


    @ReactMethod
    public void clean(final Promise promise) {
        ((CropPickerFunc)func).clean(promise);
    }

    @ReactMethod
    public void cleanSingle(final String pathToDelete, final Promise promise) {
        ((CropPickerFunc)func).cleanSingle(pathToDelete, promise);
    }


    @ReactMethod
    public void openCamera(final ReadableMap options, final Promise promise) {
        ((CropPickerFunc)func).openCamera(options, promise);
    }


    @ReactMethod
    public void openPicker(final ReadableMap options, final Promise promise) {
        ((CropPickerFunc)func).openPicker(options, promise);
    }

    @ReactMethod
    public void openCropper(final ReadableMap options, final Promise promise) {
        ((CropPickerFunc)func).openCropper(options, promise);
    }

}
