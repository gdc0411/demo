package com.lecloud.valley.modules;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import static com.lecloud.valley.common.Constants.REACT_CLASS_IMAGE_PICKER_MODULE;

/**
 * Created by RaoJia on 2017/2/18.
 */

public class ImagePickerModule extends ReactBaseModule {

    public ImagePickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        super.initialize();
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);
        if (func == null) {
            func = new ImagePickerFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS_IMAGE_PICKER_MODULE;
    }

    @ReactMethod
    public void showImagePicker(final ReadableMap options, final Callback callback) {
        ((ImagePickerFunc) func).showImagePicker(options, callback);
    }

    @ReactMethod
    public void launchCamera(final ReadableMap options, final Callback callback) {
        ((ImagePickerFunc) func).launchCamera(options, callback);
    }

    @ReactMethod
    public void launchImageLibrary(final ReadableMap options, final Callback callback) {
        ((ImagePickerFunc) func).launchImageLibrary(options, callback);
    }

}
