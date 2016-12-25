package com.lecloud.valley.modules;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.SystemUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.lecloud.valley.common.Constants.MSG_INVALID_ARGUMENT;
import static com.lecloud.valley.common.Constants.MSG_NULL_ACTIVITY;
import static com.lecloud.valley.common.Constants.REACT_CLASS_QQ_MODULE;
import static com.lecloud.valley.common.Constants.MSG_NOT_REGISTERED;
import static com.lecloud.valley.common.Constants.MSG_INVOKE_FAILED;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2016/12/24.
 */

public class QQModule extends ReactContextBaseJavaModule implements IUiListener, ActivityEventListener {

    private final ReactApplicationContext context;
    private RCTNativeAppEventEmitter mEventEmitter;

    //    private static final String RCTQQShareTypeNews = "news";
//    private static final String RCTQQShareTypeImage = "image";
//    private static final String RCTQQShareTypeText = "text";
//    private static final String RCTQQShareTypeVideo = "video";
//    private static final String RCTQQShareTypeAudio = "audio";
//
    private static final String RCTQQShareType = "type";
//    private static final String RCTQQShareText = "text";
//    private static final String RCTQQShareTitle = "title";
//    private static final String RCTQQShareDescription = "description";
//    private static final String RCTQQShareWebpageUrl = "webpageUrl";
//    private static final String RCTQQShareImageUrl = "imageUrl";

    private static final int SHARE_RESULT_CODE_SUCCESSFUL = 0;
    private static final int SHARE_RESULT_CODE_FAILED = 1;
    private static final int SHARE_RESULT_CODE_CANCEL = 2;

    private static String appId = null;
    private static String secret = null;

    private Tencent api;

    private boolean isLoginOperation;

    public QQModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;

        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new Error(e);
        }
        if (!appInfo.metaData.containsKey("QQ_APPID")) {
            throw new Error("meta-data QQ_APPID not found in AndroidManifest.xml");
        }
        appId = appInfo.metaData.get("QQ_APPID").toString();
        if (!appInfo.metaData.containsKey("QQ_APPKEY")) {
            throw new Error("meta-data QQ_APPKEY not found in AndroidManifest.xml");
        }
        secret = appInfo.metaData.get("QQ_APPKEY").toString();

    }

    @Override
    public String getName() {
        return REACT_CLASS_QQ_MODULE;
    }

    @Override
    public void initialize() {
        super.initialize();
        getReactApplicationContext().addActivityEventListener(this);
        mEventEmitter = context.getJSModule(RCTNativeAppEventEmitter.class);
        if (api == null) {
            api = Tencent.createInstance(appId, getReactApplicationContext().getApplicationContext());
        }
    }

    @Override
    public void onCatalystInstanceDestroy() {
        if (api != null) {
            api = null;
        }
        mEventEmitter = null;
        getReactApplicationContext().removeActivityEventListener(this);
        super.onCatalystInstanceDestroy();
    }

    @ReactMethod
    public void getApiVersion(Promise promise) {
        if (api == null) {
            promise.reject("-1", MSG_NOT_REGISTERED);
            return;
        }
        promise.resolve(Constants.SDK_VERSION_STRING);
    }

    @ReactMethod
    public void isQQInstalled(Promise promise) {
        if (api == null) {
            promise.reject("-1", MSG_NOT_REGISTERED);
            return;
        } else if (context.getCurrentActivity() == null) {
            promise.reject("-2", MSG_NULL_ACTIVITY);
            return;
        }
        promise.resolve( SystemUtils.getAppVersionName(context.getCurrentActivity(), "com.tencent.mobileqq") != null);
    }

    @ReactMethod
    public void isQQAppSupportApi(Promise promise) {
        if (api == null) {
            promise.reject("-1", MSG_NOT_REGISTERED);
            return;
        } else if (context.getCurrentActivity() == null) {
            promise.reject("-2", MSG_NULL_ACTIVITY);
            return;
        }
        promise.resolve(api.isSupportSSOLogin(context.getCurrentActivity()));

    }

    @ReactMethod
    public void login(String scopes, Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "QQ登录 ——— scopes：" + scopes);

        isLoginOperation = true;

        if (api == null) {
            promise.reject("-1", MSG_NOT_REGISTERED);
            return;
        } else if (context.getCurrentActivity() == null) {
            promise.reject("-2", MSG_NULL_ACTIVITY);
            return;
        }
        if (!api.isSessionValid()) {
            api.login(context.getCurrentActivity(), scopes == null ? "get_simple_userinfo" : scopes, this);
            promise.resolve(null);
        } else {
            promise.reject("-3", MSG_INVOKE_FAILED);
        }
    }

    @ReactMethod
    public void shareToQQ(ReadableMap data, Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "QQ分享到好友 ——— data：" + data.toString());

        isLoginOperation = false;

        if (api == null) {
            promise.reject("-1", MSG_NOT_REGISTERED);
            return;
        } else if (context.getCurrentActivity() == null) {
            promise.reject("-2", MSG_NULL_ACTIVITY);
            return;
        }

        Bundle param = _makeQQShare(data);
        if (param == null) {
            promise.reject("-3", MSG_INVALID_ARGUMENT);
            return;
        }
        api.shareToQQ(context.getCurrentActivity(), param, this);

        promise.resolve(null);
    }

    @ReactMethod
    public void shareToQzone(ReadableMap data, Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "QQ分享到QZong ——— data：" + data.toString());

        isLoginOperation = false;

        if (api == null) {
            promise.reject("-1", MSG_NOT_REGISTERED);
            return;
        } else if (context.getCurrentActivity() == null) {
            promise.reject("-2", MSG_NULL_ACTIVITY);
            return;
        }

        Bundle param = _makeQzoneShare(data);
        if (param == null) {
            promise.reject("-3", MSG_INVALID_ARGUMENT);
            return;
        }
        api.shareToQzone(context.getCurrentActivity(), param, this);

        promise.resolve(null);
    }


    private Bundle _makeQQShare(ReadableMap data) {
        Bundle bundle = null;
        int type = data.hasKey(QQShare.SHARE_TO_QQ_KEY_TYPE) ? data.getInt(QQShare.SHARE_TO_QQ_KEY_TYPE) : QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
        switch (type) {
            case QQShare.SHARE_TO_QQ_TYPE_DEFAULT: //图文分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, data.hasKey(QQShare.SHARE_TO_QQ_TARGET_URL) ?
                        data.getString(QQShare.SHARE_TO_QQ_TARGET_URL) : "http://www.lecloud.com");
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, data.hasKey(QQShare.SHARE_TO_QQ_TITLE) ?
                        data.getString(QQShare.SHARE_TO_QQ_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(QQShare.SHARE_TO_QQ_SUMMARY))
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, data.getString(QQShare.SHARE_TO_QQ_SUMMARY));
                //分享图片的 URL或本地路径
                if (data.hasKey(QQShare.SHARE_TO_QQ_IMAGE_URL))
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, data.getString(QQShare.SHARE_TO_QQ_IMAGE_URL));
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(QQShare.SHARE_TO_QQ_APP_NAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(QQShare.SHARE_TO_QQ_APP_NAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey(QQShare.SHARE_TO_QQ_EXT_INT))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt(QQShare.SHARE_TO_QQ_EXT_INT));
                break;

            case QQShare.SHARE_TO_QQ_TYPE_IMAGE: //纯图片分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, data.hasKey(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL) ?
                        data.getString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL) : "http://www.lecloud.com");
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(QQShare.SHARE_TO_QQ_APP_NAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(QQShare.SHARE_TO_QQ_APP_NAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey(QQShare.SHARE_TO_QQ_EXT_INT))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt(QQShare.SHARE_TO_QQ_EXT_INT));
                break;

            case QQShare.SHARE_TO_QQ_TYPE_AUDIO: //音乐分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, data.hasKey(QQShare.SHARE_TO_QQ_TARGET_URL) ?
                        data.getString(QQShare.SHARE_TO_QQ_TARGET_URL) : "http://www.lecloud.com");
                bundle.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, data.hasKey(QQShare.SHARE_TO_QQ_AUDIO_URL) ?
                        data.getString(QQShare.SHARE_TO_QQ_AUDIO_URL) : "");
                //分享的标题摘要，最长 30 个字符
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, data.hasKey(QQShare.SHARE_TO_QQ_TITLE) ?
                        data.getString(QQShare.SHARE_TO_QQ_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(QQShare.SHARE_TO_QQ_SUMMARY))
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, data.getString(QQShare.SHARE_TO_QQ_SUMMARY));
                //分享图片的 URL或本地路径
                if (data.hasKey(QQShare.SHARE_TO_QQ_IMAGE_URL))
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, data.getString(QQShare.SHARE_TO_QQ_IMAGE_URL));
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(QQShare.SHARE_TO_QQ_APP_NAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(QQShare.SHARE_TO_QQ_APP_NAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey(QQShare.SHARE_TO_QQ_EXT_INT))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt(QQShare.SHARE_TO_QQ_EXT_INT));
                break;

            case QQShare.SHARE_TO_QQ_TYPE_APP: //应用分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
                //分享的标题摘要，最长 30 个字符
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, data.hasKey(QQShare.SHARE_TO_QQ_TITLE) ?
                        data.getString(QQShare.SHARE_TO_QQ_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(QQShare.SHARE_TO_QQ_SUMMARY))
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, data.getString(QQShare.SHARE_TO_QQ_SUMMARY));
                //分享图片的 URL或本地路径
                if (data.hasKey(QQShare.SHARE_TO_QQ_IMAGE_URL))
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, data.getString(QQShare.SHARE_TO_QQ_IMAGE_URL));
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(QQShare.SHARE_TO_QQ_APP_NAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(QQShare.SHARE_TO_QQ_APP_NAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey(QQShare.SHARE_TO_QQ_EXT_INT))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt(QQShare.SHARE_TO_QQ_EXT_INT));
                break;
        }

        return bundle;
    }


    private Bundle _makeQzoneShare(ReadableMap data) {
        Bundle bundle = null;
        int type = data.hasKey(QzoneShare.SHARE_TO_QZONE_KEY_TYPE) ? data.getInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE) : QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE;
        switch (type) {
            case QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT: //图文分享
                bundle = new Bundle();
                bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, data.hasKey(QzoneShare.SHARE_TO_QQ_TITLE) ?
                        data.getString(QzoneShare.SHARE_TO_QQ_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(QzoneShare.SHARE_TO_QQ_SUMMARY))
                    bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, data.getString(QzoneShare.SHARE_TO_QQ_SUMMARY));
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, data.hasKey(QzoneShare.SHARE_TO_QQ_TARGET_URL) ?
                        data.getString(QzoneShare.SHARE_TO_QQ_TARGET_URL) : "http://www.lecloud.com");
                //分享图片的 URL或本地路径
                if (data.hasKey(QzoneShare.SHARE_TO_QQ_IMAGE_URL)) {
                    ArrayList<String> al = new ArrayList();
                    al.add(data.getString(QzoneShare.SHARE_TO_QQ_IMAGE_URL));
                    bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, al);
                }
                break;

            case QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD: //发表说说、或上传图片
            case QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO: //上传视频

                bundle = new Bundle();
                bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(QzoneShare.SHARE_TO_QQ_SUMMARY))
                    bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, data.getString(QzoneShare.SHARE_TO_QQ_SUMMARY));
                // 本地图片 todo 变成arrayList
                if (data.hasKey(QzoneShare.SHARE_TO_QQ_IMAGE_URL)) {
                    ArrayList<String> al = new ArrayList();
                    al.add(data.getString(QzoneShare.SHARE_TO_QQ_IMAGE_URL));
                    bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, al);
                }
                //发表的视频，只支持本地地址，发表视频时必填
                if (data.hasKey(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH))
                    bundle.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, data.getString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH));

                break;
        }

        return bundle;
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }

    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onComplete(Object o) {

        WritableMap resultMap = Arguments.createMap();

        if (isLoginOperation) {
            resultMap.putString("type", "QQAuthorizeResponse");
            try {
                JSONObject obj = (JSONObject) (o);
                if (obj.has("ret")) resultMap.putInt("errCode", obj.getInt("ret"));
                if (obj.has("openid"))
                    resultMap.putString("openid", obj.getString(Constants.PARAM_OPEN_ID));
                if (obj.has("access_token"))
                    resultMap.putString("access_token", obj.getString(Constants.PARAM_ACCESS_TOKEN));
                if (obj.has("pay_token"))
                    resultMap.putString("pay_token", obj.getString("pay_token"));
                if (obj.has("expires_in"))
                    resultMap.putInt("expires_in", (int) obj.getLong(Constants.PARAM_EXPIRES_IN));
                if (obj.has("pf")) resultMap.putString("pf", obj.getString("pf"));
                if (obj.has("pfkey")) resultMap.putString("pfkey", obj.getString("pfkey"));
                if (obj.has("msg")) resultMap.putString("msg", obj.getString("msg"));
                if (obj.has("login_cost")) resultMap.putInt("login_cost", obj.getInt("login_cost"));
                if (obj.has("query_authority_cost"))
                    resultMap.putInt("query_authority_cost", obj.getInt("query_authority_cost"));
                if (obj.has("authority_cost"))
                    resultMap.putInt("authority_cost", obj.getInt("authority_cost"));
                resultMap.putString("appid", appId);
                resultMap.putString("secret", secret);

            } catch (Exception e) {
                WritableMap map = Arguments.createMap();
                map.putInt("errCode", Constants.ERROR_UNKNOWN);
                map.putString("errStr", e.getLocalizedMessage());

                if (mEventEmitter != null)
                    mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), map);
            }
        } else {
            resultMap.putString("type", "QQShareResponse");
            resultMap.putInt("errCode", SHARE_RESULT_CODE_SUCCESSFUL);
            resultMap.putString("errStr", "Share successfully.");
        }

        if (mEventEmitter != null)
            mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), resultMap);
    }

    @Override
    public void onError(UiError uiError) {
        WritableMap resultMap = Arguments.createMap();

        if (isLoginOperation) {
            resultMap.putString("type", "QQAuthorizeResponse");
            resultMap.putInt("errCode", SHARE_RESULT_CODE_FAILED);
            resultMap.putString("errStr", "login failed." + uiError.errorDetail);
        } else {
            resultMap.putString("type", "QQShareResponse");
            resultMap.putInt("errCode", SHARE_RESULT_CODE_FAILED);
            resultMap.putString("errStr", "Share failed." + uiError.errorDetail);
        }

        if (mEventEmitter != null)
            mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), resultMap);
    }

    @Override
    public void onCancel() {
        WritableMap resultMap = Arguments.createMap();

        if (isLoginOperation) {
            resultMap.putString("type", "QQAuthorizeResponse");
            resultMap.putInt("errCode", SHARE_RESULT_CODE_CANCEL);
            resultMap.putString("errStr", "login canceled.");
        } else {
            resultMap.putString("type", "QQShareResponse");
            resultMap.putInt("errCode", SHARE_RESULT_CODE_CANCEL);
            resultMap.putString("errStr", "Share canceled.");
        }

        if (mEventEmitter != null)
            mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), resultMap);
    }

//    private void resolvePromise(ReadableMap resultMap) {
//        getReactApplicationContext()
//                .getJSModule(RCTNativeAppEventEmitter.class)
//                .emit("QQ_Resp", resultMap);
//
//    }
}