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
import android.net.rtp.AudioCodec;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.sina.weibo.sdk.utils.Utility;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.lecloud.valley.common.Constants.*;
import static com.lecloud.valley.utils.LogUtils.TAG;


/**
 * Created by raojia on 2016/12/26.
 */
public class WeiboModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private final ReactApplicationContext context;
    private RCTNativeAppEventEmitter mEventEmitter;

    private SsoHandler mWeiboSsoHandler;
    private IWeiboShareAPI mWeiboShareAPI;
    private String appId;

    private static WeiboModule gModule = null;

    public WeiboModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;

        ApplicationInfo appInfo;
        try {
            appInfo = reactContext.getPackageManager().getApplicationInfo(reactContext.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new Error(e);
        }
        if (!appInfo.metaData.containsKey("WB_APPKEY")) {
            throw new Error("meta-data WB_APPKEY not found in AndroidManifest.xml");
        }
        appId = appInfo.metaData.get("WB_APPKEY").toString();
        appId = appId.substring(2);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("SHARE_TYPE_NEWS", SHARE_TYPE_NEWS);
        constants.put("SHARE_TYPE_IMAGE", SHARE_TYPE_IMAGE);
        constants.put("SHARE_TYPE_TEXT", SHARE_TYPE_TEXT);
        constants.put("SHARE_TYPE_VIDEO", SHARE_TYPE_VIDEO);
        constants.put("SHARE_TYPE_AUDIO", SHARE_TYPE_AUDIO);
        constants.put("SHARE_TYPE_VOICE", SHARE_TYPE_VOICE);
        return constants;
    }

    @Override
    public void initialize() {
        super.initialize();
        gModule = this;

        context.addActivityEventListener(this);
        mEventEmitter = context.getJSModule(RCTNativeAppEventEmitter.class);

        if (mWeiboShareAPI == null) {
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, appId);
            mWeiboShareAPI.registerApp();
        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        gModule = null;
        mEventEmitter = null;
        mWeiboShareAPI = null;
        mWeiboSsoHandler = null;
        context.removeActivityEventListener(this);
        super.onCatalystInstanceDestroy();
    }

    @Override
    public String getName() {
        return REACT_CLASS_WEIBO_MODULE;
    }


    @ReactMethod
    public void getApiVersion(Promise promise) {
        if (mWeiboShareAPI == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        }
        promise.resolve(mWeiboShareAPI.getWeiboAppSupportAPI());
    }

    @ReactMethod
    public void isAppInstalled(Promise promise) {
        if (mWeiboShareAPI == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        }
        promise.resolve(mWeiboShareAPI.isWeiboAppInstalled());
    }

    @ReactMethod
    public void isAppSupportApi(Promise promise) {
        if (mWeiboShareAPI == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        }
        promise.resolve(mWeiboShareAPI.isWeiboAppSupportAPI());
    }

    @ReactMethod
    public void login(final ReadableMap config, final Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "微博登陆——— config：" + config.toString());

        if (context.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }
        if (mWeiboSsoHandler == null) {
            AuthInfo sinaAuthInfo = _genAuthInfo(config);
            mWeiboSsoHandler = new SsoHandler(context.getCurrentActivity(), sinaAuthInfo);
            mWeiboSsoHandler.authorize(new WeiboAuthListener() {
                @Override
                public void onComplete(Bundle bundle) {

                    final Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(context.getApplicationContext(), token);
                    WritableMap event = Arguments.createMap();
                    if (token.isSessionValid()) {
                        event.putString("access_token", token.getToken());
                        event.putDouble("expires_in", token.getExpiresTime());
                        event.putString("openid", token.getUid());
                        event.putString("refresh_token", token.getRefreshToken());
                        event.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_SUCCESSFUL);
                    } else {
//                    String code = bundle.getString("code", "");
                        event.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_CANCEL);
                        event.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_CANCEL);
                    }
                    event.putString(EVENT_PROP_SOCIAL_TYPE, "WBAuthorizeResponse");

                    if (mEventEmitter != null)
                        mEventEmitter.emit(Events.EVENT_WEIBO_RESP.toString(), event);
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    WritableMap event = Arguments.createMap();
                    event.putString(EVENT_PROP_SOCIAL_TYPE, "WBAuthorizeResponse");
                    event.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_FAILED);
                    event.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_FAILED + "：" + e.getMessage());
                    if (mEventEmitter != null)
                        mEventEmitter.emit(Events.EVENT_WEIBO_RESP.toString(), event);
                }

                @Override
                public void onCancel() {
                    WritableMap event = Arguments.createMap();
                    event.putString(EVENT_PROP_SOCIAL_TYPE, "WBAuthorizeResponse");
                    event.putInt(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_CODE_CANCEL);
                    event.putString(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_MSG_CANCEL);
                    if (mEventEmitter != null)
                        mEventEmitter.emit(Events.EVENT_WEIBO_RESP.toString(), event);
                }
            });
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void shareToWeibo(final ReadableMap data, final Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "分享到新浪微博 ——— data：" + data.toString());

        if (mWeiboShareAPI == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (context.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }

        if (data.hasKey(SHARE_PROP_THUMB_IMAGE)) {
            String imageUrl = data.getString(SHARE_PROP_THUMB_IMAGE);
            DataSubscriber<CloseableReference<CloseableImage>> dataSubscriber =
                    new BaseDataSubscriber<CloseableReference<CloseableImage>>() {

                        @Override
                        public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                            // isFinished must be obtained before image, otherwise we might set intermediate result
                            // as final image.
                            boolean isFinished = dataSource.isFinished();
//                        float progress = dataSource.getProgress();
                            CloseableReference<CloseableImage> image = dataSource.getResult();
                            if (image != null) {
                                Drawable drawable = _createDrawable(image);
                                Bitmap bitmap = _drawable2Bitmap(drawable);
                                _share(data, bitmap);
                            } else if (isFinished) {
                                _share(data, null);
                            }
                            dataSource.close();
                        }

                        @Override
                        public void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                            dataSource.close();
                            _share(data, null);
                        }

                        @Override
                        public void onProgressUpdate(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        }
                    };

            ResizeOptions resizeOptions = null;
            if (!data.hasKey("type") || !data.getString("type").equals(SHARE_TYPE_IMAGE)) {
                resizeOptions = new ResizeOptions(80, 80);
            }
            _downloadImage(imageUrl, resizeOptions, dataSubscriber);

        } else {
            _share(data, null);
        }

        promise.resolve(null);
    }

    private void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mWeiboSsoHandler != null) {
            mWeiboSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            mWeiboSsoHandler = null;
        }
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    public void onNewIntent(Intent intent) {

    }


    private void _share(ReadableMap data, Bitmap bitmap) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        String type = data.hasKey(SHARE_PROP_TYPE) ? data.getString(SHARE_PROP_TYPE) : SHARE_TYPE_NEWS;

        if (data.hasKey(SHARE_PROP_TEXT)) {
            //创建文本消息对象
            TextObject textObject = new TextObject();
            textObject.text = data.getString(SHARE_PROP_TEXT);
            weiboMessage.textObject = textObject;
        }

        if (bitmap != null) {
            //创建图片消息对象。
            ImageObject imageObject = new ImageObject();
            //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            imageObject.setImageObject(bitmap);
            weiboMessage.imageObject = imageObject;
        }

        switch (type) {
            case SHARE_TYPE_NEWS:
                WebpageObject webpageObject = new WebpageObject();
                if (data.hasKey(SHARE_PROP_TARGET)) {
                    webpageObject.actionUrl = data.getString(SHARE_PROP_TARGET);
                }
                weiboMessage.mediaObject = webpageObject;
                break;

            case SHARE_TYPE_VIDEO:
                VideoObject videoObject = new VideoObject();
                if (data.hasKey(SHARE_PROP_VIDEO)) {
                    videoObject.dataUrl = data.getString(SHARE_PROP_VIDEO);
                }
                weiboMessage.mediaObject = videoObject;
                break;

            case SHARE_TYPE_AUDIO:
                MusicObject musicObject = new MusicObject();
                if (data.hasKey(SHARE_PROP_AUDIO)) {
                    musicObject.dataUrl = data.getString(SHARE_PROP_AUDIO);
                }
                weiboMessage.mediaObject = musicObject;
                break;

            case SHARE_TYPE_VOICE:
                VoiceObject voiceObject = new VoiceObject();
                if (data.hasKey(SHARE_PROP_AUDIO)) {
                    voiceObject.dataUrl = data.getString(SHARE_PROP_AUDIO);
                }
                weiboMessage.mediaObject = voiceObject;
                break;
        }

        if (data.hasKey(SHARE_PROP_DESP)) {
            weiboMessage.mediaObject.description = data.getString(SHARE_PROP_DESP);
        }
        if (data.hasKey(SHARE_PROP_TITLE)) {
            weiboMessage.mediaObject.title = data.getString(SHARE_PROP_TITLE);
        }
        if (bitmap != null) {
            weiboMessage.mediaObject.setThumbImage(bitmap);
        }
        weiboMessage.mediaObject.identify = new Date().toString();


        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

//        String accessToken = null;
//        if (data.hasKey("accessToken")) {
//            accessToken = data.getString("accessToken");
//        }
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context.getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }

        boolean success = false;
//        if(TextUtils.isEmpty(token)){
//            success = mWeiboShareAPI.sendRequest(getCurrentActivity(), request);
//        }else{
//            success = mWeiboShareAPI.sendRequest(getCurrentActivity(), request, null, token, null);
//        }
        success = mWeiboShareAPI.sendRequest(getCurrentActivity(), request);

        if (!success) {
            WritableMap event = Arguments.createMap();
            event.putString(EVENT_PROP_SOCIAL_TYPE, "WBSendMessageToWeiboResponse");
            event.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_FAILED);
            event.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_FAILED + ":WeiBo API invoke returns false.");
            if (mEventEmitter != null)
                mEventEmitter.emit(Events.EVENT_WEIBO_RESP.toString(), event);
        }
    }

    private static boolean handleWeiboResponse(Intent intent, IWeiboHandler.Response response) {
        return gModule.mWeiboShareAPI.handleWeiboResponse(intent, response);
    }

    private static void onShareResponse(BaseResponse baseResponse) {
        WritableMap map = Arguments.createMap();
        map.putInt("wbCode", baseResponse.errCode);
        map.putString("wbMsg", baseResponse.errMsg);

        if (baseResponse.errCode == 0) {
            map.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_SUCCESSFUL);
            map.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_SUCCESSFUL);
        } else if (baseResponse.errCode == 1) {
            map.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_CANCEL);
            map.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_CANCEL);
        } else {
            map.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_FAILED);
            map.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_FAILED);
        }
        map.putString(EVENT_PROP_SOCIAL_TYPE, "WBSendMessageToWeiboResponse");

        if (gModule.mEventEmitter != null)
            gModule.mEventEmitter.emit(Events.EVENT_WEIBO_RESP.toString(), map);
    }

    static public class SinaEntryActivity extends Activity implements IWeiboHandler.Response {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            WeiboModule.handleWeiboResponse(getIntent(), this);
        }

        @Override
        public void onResponse(BaseResponse baseResponse) {
            WeiboModule.onShareResponse(baseResponse);
            this.finish();
        }
    }

    private AuthInfo _genAuthInfo(ReadableMap config) {
        String redirectURI = "https://api.weibo.com/oauth2/default.html";
        if (config != null && config.hasKey("redirectURI")) {
            redirectURI = config.getString("redirectURI");
        }
        String scope = "all";
        if (config != null && config.hasKey("scope")) {
            scope = config.getString("scope");
        }
        return new AuthInfo(context, appId, redirectURI, scope);
    }

    private void _downloadImage(String imageUrl, ResizeOptions resizeOptions, DataSubscriber<CloseableReference<CloseableImage>> dataSubscriber) {
        Uri uri = null;
        try {
            uri = Uri.parse(imageUrl);
            // Verify scheme is set, so that relative uri (used by static resources) are not handled.
            if (uri.getScheme() == null) {
                uri = null;
            }
        } catch (Exception e) {
            // ignore malformed uri, then attempt to extract resource ID.
        }
        if (uri == null) {
            uri = _getResourceDrawableUri(context, imageUrl);
        } else {
        }

        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (resizeOptions != null) {
            builder.setResizeOptions(resizeOptions);
        }
        ImageRequest imageRequest = builder.build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(dataSubscriber, UiThreadImmediateExecutorService.getInstance());
    }

    private static
    @Nullable
    Uri _getResourceDrawableUri(Context context, @Nullable String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        name = name.toLowerCase().replace("-", "_");
        int resId = context.getResources().getIdentifier(
                name,
                "drawable",
                context.getPackageName());
        return new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
    }

    private Drawable _createDrawable(CloseableReference<CloseableImage> image) {
        Preconditions.checkState(CloseableReference.isValid(image));
        CloseableImage closeableImage = image.get();
        if (closeableImage instanceof CloseableStaticBitmap) {
            CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) closeableImage;
            BitmapDrawable bitmapDrawable = new BitmapDrawable(
                    context.getResources(),
                    closeableStaticBitmap.getUnderlyingBitmap());
            if (closeableStaticBitmap.getRotationAngle() == 0 ||
                    closeableStaticBitmap.getRotationAngle() == EncodedImage.UNKNOWN_ROTATION_ANGLE) {
                return bitmapDrawable;
            } else {
                return new OrientedDrawable(bitmapDrawable, closeableStaticBitmap.getRotationAngle());
            }
        } else {
            throw new UnsupportedOperationException("Unrecognized image class: " + closeableImage);
        }
    }

    private Bitmap _drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }
}
