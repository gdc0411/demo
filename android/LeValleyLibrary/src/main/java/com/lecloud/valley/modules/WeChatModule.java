package com.lecloud.valley.modules;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXFileObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by raojia on 2016/12/20.
 */
public class WeChatModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;
    private RCTNativeAppEventEmitter mEventEmitter;

    private WeChatFunc mWeChatFunc;

    public WeChatModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public void initialize() {
        super.initialize();
        mEventEmitter = mReactContext.getJSModule(RCTNativeAppEventEmitter.class);

        if (mWeChatFunc == null) {
            mWeChatFunc = new WeChatFunc(mReactContext, mEventEmitter);
        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (mWeChatFunc != null) {
            mWeChatFunc.destroy();
            mWeChatFunc = null;
        }

        mEventEmitter = null;
        super.onCatalystInstanceDestroy();
    }

    @Override
    public String getName() {
        return REACT_CLASS_WECHAT_MODULE;
    }

    @Override
    public Map<String, Object> getConstants() {
        return mWeChatFunc.getConstants();
    }

    /**
     * 微信是否安装
     */
    @ReactMethod
    public void isAppInstalled(Promise promise) {
        mWeChatFunc.isAppInstalled(promise);
    }

    /**
     * 微信版本是否支持API
     */
    @ReactMethod
    public void isAppSupportApi(Promise promise) {
        mWeChatFunc.isAppSupportApi(promise);
    }

    /**
     * 获得微信版本
     */
    @ReactMethod
    public void getApiVersion(Promise promise) {
        mWeChatFunc.getApiVersion(promise);
    }

    /**
     * 调起微信APP
     */
    @ReactMethod
    public void openApp(Promise promise) {
        mWeChatFunc.openApp(promise);
    }

    /**
     * 微信登陆
     */
    @ReactMethod
    public void sendAuth(ReadableMap config, Promise promise) {
        mWeChatFunc.sendAuth(config, promise);
    }


    /**
     * 微信分享朋友圈
     */
    @ReactMethod
    public void shareToTimeline(ReadableMap data, Promise promise) {
        mWeChatFunc.shareToTimeline(data, promise);
    }

    /**
     * 微信分享好友
     */
    @ReactMethod
    public void shareToSession(ReadableMap data, Promise promise) {
        mWeChatFunc.shareToSession(data, promise);
    }

    /**
     * 微信支付
     */
    @ReactMethod
    public void pay(ReadableMap data, Promise promise) {
        mWeChatFunc.pay(data, promise);
    }


    public static void handleIntent(Intent intent) {
        WeChatFunc.handleIntent(intent);
    }

}
