package com.lecloud.valley.modules;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.OrientedDrawable;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.AccessTokenKeeper;
import com.lecloud.valley.utils.LogUtils;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;


/**
 * Created by raojia on 2016/12/26.
 */
public class WeiboModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;
    private RCTNativeAppEventEmitter mEventEmitter;

    private WeiboFunc mWeiboFunc;

    public WeiboModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public void initialize() {
        super.initialize();

        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (mWeiboFunc == null) {
            mWeiboFunc = new WeiboFunc(mReactContext, mEventEmitter);
        }
    }


    @Override
    public void onCatalystInstanceDestroy() {
        if (mWeiboFunc != null) {
            mWeiboFunc.destroy();
            mWeiboFunc = null;
        }
        mEventEmitter = null;
        super.onCatalystInstanceDestroy();
    }


    @Override
    public Map<String, Object> getConstants() {
        return mWeiboFunc.getConstants();
    }

    @Override
    public String getName() {
        return REACT_CLASS_WEIBO_MODULE;
    }


    @ReactMethod
    public void getApiVersion(Promise promise) {
        mWeiboFunc.getApiVersion(promise);
    }

    @ReactMethod
    public void isAppInstalled(Promise promise) {
        mWeiboFunc.isAppInstalled(promise);
    }

    @ReactMethod
    public void isAppSupportApi(Promise promise) {
        mWeiboFunc.isAppSupportApi(promise);
    }

    @ReactMethod
    public void login(final ReadableMap config, final Promise promise) {
        mWeiboFunc.login(config, promise);
    }

    @ReactMethod
    public void shareToWeibo(final ReadableMap data, final Promise promise) {
        mWeiboFunc.shareToWeibo(data, promise);
    }

}
