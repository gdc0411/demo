package com.lecloud.valley.modules;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.lecloud.valley.common.Constants.AUTH_RESULT_CODE_CANCEL;
import static com.lecloud.valley.common.Constants.AUTH_RESULT_CODE_FAILED;
import static com.lecloud.valley.common.Constants.AUTH_RESULT_MSG_CANCEL;
import static com.lecloud.valley.common.Constants.AUTH_RESULT_MSG_FAILED;
import static com.lecloud.valley.common.Constants.AUTH_RESULT_MSG_SUCCESSFUL;
import static com.lecloud.valley.common.Constants.CODE_INVALID_ARGUMENT;
import static com.lecloud.valley.common.Constants.CODE_INVOKE_FAILED;
import static com.lecloud.valley.common.Constants.CODE_NOT_REGISTERED;
import static com.lecloud.valley.common.Constants.CODE_NULL_ACTIVITY;
import static com.lecloud.valley.common.Constants.EVENT_PROP_SOCIAL_CODE;
import static com.lecloud.valley.common.Constants.EVENT_PROP_SOCIAL_MSG;
import static com.lecloud.valley.common.Constants.EVENT_PROP_SOCIAL_TYPE;
import static com.lecloud.valley.common.Constants.MSG_INVALID_ARGUMENT;
import static com.lecloud.valley.common.Constants.MSG_INVOKE_FAILED;
import static com.lecloud.valley.common.Constants.MSG_NOT_REGISTERED;
import static com.lecloud.valley.common.Constants.MSG_NULL_ACTIVITY;
import static com.lecloud.valley.common.Constants.REACT_CLASS_QQ_MODULE;
import static com.lecloud.valley.common.Constants.SHARE_PROP_APPNAME;
import static com.lecloud.valley.common.Constants.SHARE_PROP_AUDIO;
import static com.lecloud.valley.common.Constants.SHARE_PROP_DESP;
import static com.lecloud.valley.common.Constants.SHARE_PROP_IMAGE;
import static com.lecloud.valley.common.Constants.SHARE_PROP_TARGET;
import static com.lecloud.valley.common.Constants.SHARE_PROP_THUMB_IMAGE;
import static com.lecloud.valley.common.Constants.SHARE_PROP_TITLE;
import static com.lecloud.valley.common.Constants.SHARE_PROP_TYPE;
import static com.lecloud.valley.common.Constants.SHARE_RESULT_CODE_CANCEL;
import static com.lecloud.valley.common.Constants.SHARE_RESULT_CODE_FAILED;
import static com.lecloud.valley.common.Constants.SHARE_RESULT_CODE_SUCCESSFUL;
import static com.lecloud.valley.common.Constants.SHARE_RESULT_MSG_CANCEL;
import static com.lecloud.valley.common.Constants.SHARE_RESULT_MSG_FAILED;
import static com.lecloud.valley.common.Constants.SHARE_RESULT_MSG_SUCCESSFUL;
import static com.lecloud.valley.common.Constants.SHARE_TYPE_APP;
import static com.lecloud.valley.common.Constants.SHARE_TYPE_AUDIO;
import static com.lecloud.valley.common.Constants.SHARE_TYPE_IMAGE;
import static com.lecloud.valley.common.Constants.SHARE_TYPE_NEWS;
import static com.lecloud.valley.common.Constants.SHARE_TYPE_TEXT;
import static com.lecloud.valley.common.Constants.SHARE_TYPE_VIDEO;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2016/12/24.
 */

class QQFunc implements IUiListener, ActivityEventListener {

    private final ReactApplicationContext mReactContext;
    private final RCTNativeAppEventEmitter mEventEmitter;

    private static String appId = null;
    private static String secret = null;

    private Tencent api;

    private boolean isLoginOperation;

    QQFunc(ReactApplicationContext reactContext, RCTNativeAppEventEmitter eventEmitter) {
        mReactContext = reactContext;
        mEventEmitter = eventEmitter;

        initialize();
    }

    private void initialize() {

        ApplicationInfo appInfo;
        try {
            appInfo = mReactContext.getPackageManager().getApplicationInfo(mReactContext.getPackageName(), PackageManager.GET_META_DATA);
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

        mReactContext.addActivityEventListener(this);

        if (api == null) {
            api = Tencent.createInstance(appId, mReactContext.getApplicationContext());
        }
    }

    void destroy() {
        if (api != null) {
            api = null;
        }
        mReactContext.removeActivityEventListener(this);
    }

    Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("SHARE_TYPE_NEWS", SHARE_TYPE_NEWS);
        constants.put("SHARE_TYPE_IMAGE", SHARE_TYPE_IMAGE);
        constants.put("SHARE_TYPE_TEXT", SHARE_TYPE_TEXT);
        constants.put("SHARE_TYPE_VIDEO", SHARE_TYPE_VIDEO);
        constants.put("SHARE_TYPE_AUDIO", SHARE_TYPE_AUDIO);
        constants.put("SHARE_TYPE_APP", SHARE_TYPE_APP);
        constants.put("EVENT_QQ_RESP", Events.EVENT_QQ_RESP.toString());
        return constants;
    }

    void getApiVersion(Promise promise) {
        if (api == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        }
        promise.resolve(Constants.SDK_VERSION_STRING);
    }

    void isAppInstalled(Promise promise) {
        if (api == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (mReactContext.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }
        promise.resolve(SystemUtils.getAppVersionName(mReactContext.getCurrentActivity(), Constants.PACKAGE_QQ) != null ||
                SystemUtils.getAppVersionName(mReactContext.getCurrentActivity(), Constants.PACKAGE_QQ_PAD) != null ||
                SystemUtils.getAppVersionName(mReactContext.getCurrentActivity(), Constants.PACKAGE_QZONE) != null);
    }

    void isAppSupportApi(Promise promise) {
        if (api == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (mReactContext.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }
        promise.resolve(api.isSupportSSOLogin(mReactContext.getCurrentActivity()));

    }

    void login(String scopes, Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "QQ登录 ——— scopes：" + scopes);

        isLoginOperation = true;

        if (api == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (mReactContext.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }
        if (!api.isSessionValid()) {
            api.login(mReactContext.getCurrentActivity(), scopes == null ? "get_simple_userinfo" : scopes, this);
            promise.resolve(null);
        } else {
            promise.reject(CODE_INVOKE_FAILED, MSG_INVOKE_FAILED);
        }
    }

    void shareToQQ(ReadableMap data, Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "QQ分享到好友 ——— data：" + data.toString());

        isLoginOperation = false;

        if (api == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (mReactContext.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }

        Bundle param = _makeQQShare(data);
        if (param == null) {
            promise.reject(CODE_INVALID_ARGUMENT, MSG_INVALID_ARGUMENT);
            return;
        }
        api.shareToQQ(mReactContext.getCurrentActivity(), param, this);
        promise.resolve(null);

    }

    void shareToQzone(ReadableMap data, Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "QQ分享到QZong ——— data：" + data.toString());

        isLoginOperation = false;

        if (api == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (mReactContext.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }

        Bundle param = _makeQzoneShare(data);
        if (param == null) {
            promise.reject(CODE_INVALID_ARGUMENT, MSG_INVALID_ARGUMENT);
            return;
        }
        api.shareToQzone(mReactContext.getCurrentActivity(), param, this);

        promise.resolve(null);
    }


    void logout(Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "QQ登录注销 ——— ");

        isLoginOperation = true;

        if (api == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (mReactContext.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }
        if (api.isSessionValid()) {
            api.logout(mReactContext.getCurrentActivity());
            promise.resolve(null);
        }
    }

    private Bundle _makeQQShare(ReadableMap data) {
        Bundle bundle = null;
        String type = data.hasKey(SHARE_PROP_TYPE) ? data.getString(SHARE_PROP_TYPE) : SHARE_TYPE_NEWS;

        switch (type) {
            case SHARE_TYPE_NEWS: //图文分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, data.hasKey(SHARE_PROP_TARGET) ? data.getString(SHARE_PROP_TARGET) : "http://www.lecloud.com");
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, data.hasKey(SHARE_PROP_TITLE) ? data.getString(SHARE_PROP_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(SHARE_PROP_DESP))
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, data.getString(SHARE_PROP_DESP));
                //分享图片的 URL或本地路径
                if (data.hasKey(SHARE_PROP_THUMB_IMAGE))
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, data.getString(SHARE_PROP_THUMB_IMAGE));
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(SHARE_PROP_APPNAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(SHARE_PROP_APPNAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey("cflag"))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt("cflag"));
                break;

            case SHARE_TYPE_IMAGE: //纯图片分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, data.hasKey("imageLocalUrl") ? data.getString("imageLocalUrl") : "http://www.lecloud.com");
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(SHARE_PROP_APPNAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(SHARE_PROP_APPNAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey("cflag"))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt("cflag"));
                break;

            case SHARE_TYPE_AUDIO: //音乐分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, data.hasKey(SHARE_PROP_TARGET) ? data.getString(SHARE_PROP_TARGET) : "http://www.lecloud.com");
                bundle.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, data.hasKey(SHARE_PROP_AUDIO) ? data.getString(SHARE_PROP_AUDIO) : "");
                //分享的标题摘要，最长 30 个字符
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, data.hasKey(SHARE_PROP_TITLE) ? data.getString(SHARE_PROP_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(SHARE_PROP_DESP))
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, data.getString(SHARE_PROP_DESP));
                //分享图片的 URL或本地路径
                if (data.hasKey(SHARE_PROP_IMAGE))
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, data.getString(SHARE_TYPE_IMAGE));
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(SHARE_PROP_APPNAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(SHARE_PROP_APPNAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey("cflag"))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt("cflag"));
                break;


            case SHARE_TYPE_APP: //应用分享

                bundle = new Bundle();
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
                //分享的标题摘要，最长 30 个字符
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, data.hasKey(SHARE_PROP_TITLE) ? data.getString(SHARE_PROP_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(SHARE_PROP_DESP))
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, data.getString(SHARE_PROP_DESP));
                //分享图片的 URL或本地路径
                if (data.hasKey(SHARE_PROP_IMAGE))
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, data.getString(SHARE_PROP_IMAGE));
                //手Q客户端顶部 ，替换“返回 ”按钮文字，如果为空用返回代替
                if (data.hasKey(SHARE_PROP_APPNAME))
                    bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, data.getString(SHARE_PROP_APPNAME));
                //分享时是否自动打开分享到QZone的对话框
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1
                // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2
                if (data.hasKey("cflag"))
                    bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, data.getInt("cflag"));
                break;
        }

        return bundle;
    }


    private Bundle _makeQzoneShare(ReadableMap data) {
        Bundle bundle = null;
        String type = data.hasKey(SHARE_PROP_TYPE) ? data.getString(SHARE_PROP_TYPE) : SHARE_TYPE_NEWS;
        switch (type) {
            case SHARE_TYPE_NEWS: //图文分享
                bundle = new Bundle();
                bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, data.hasKey(SHARE_PROP_TITLE) ? data.getString(SHARE_PROP_TITLE) : ""); //分享的标题，最长30个字符
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(SHARE_PROP_DESP))
                    bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, data.getString(SHARE_PROP_DESP));
                //这条分享消息被好友点击后的跳转的URL
                bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, data.hasKey(SHARE_PROP_TARGET) ?
                        data.getString(SHARE_PROP_TARGET) : "http://www.lecloud.com");
                //分享图片的 URL或本地路径
                if (data.hasKey(SHARE_PROP_IMAGE)) {
                    ArrayList<String> al = new ArrayList<>(Arrays.asList(data.getString(SHARE_PROP_IMAGE).split(",")));
                    bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, al);
                }
                break;

            case SHARE_TYPE_IMAGE: //发表说说、或上传图片
            case SHARE_TYPE_VIDEO: //上传视频

                bundle = new Bundle();
                bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                //分享的消息摘要，最长 40 个字符
                if (data.hasKey(SHARE_PROP_DESP))
                    bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, data.getString(SHARE_PROP_DESP));
                // 本地图片 todo 变成arrayList
                if (data.hasKey(SHARE_PROP_IMAGE)) {
                    ArrayList<String> al = new ArrayList<>();
                    al.add(data.getString(SHARE_PROP_IMAGE));
                    bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, al);
                }
                //发表的视频，只支持本地地址，发表视频时必填
                if (data.hasKey("videoPath"))
                    bundle.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, data.getString("videoPath"));

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
            resultMap.putString(EVENT_PROP_SOCIAL_TYPE, "QQAuthorizeResponse");
            try {
                JSONObject obj = (JSONObject) (o);
                if (obj.has("ret")) resultMap.putInt(EVENT_PROP_SOCIAL_CODE, obj.getInt("ret"));
                if (obj.has("openid"))
                    resultMap.putString("openid", obj.getString(Constants.PARAM_OPEN_ID));
                if (obj.has("access_token"))
                    resultMap.putString("access_token", obj.getString(Constants.PARAM_ACCESS_TOKEN));
                if (obj.has("pay_token"))
                    resultMap.putString("pay_token", obj.getString("pay_token"));
                if (obj.has("expires_in"))
                    resultMap.putDouble("expires_in", System.currentTimeMillis() + obj.getLong(Constants.PARAM_EXPIRES_IN));
                if (obj.has("pf")) resultMap.putString("pf", obj.getString("pf"));
                if (obj.has("pfkey")) resultMap.putString("pfkey", obj.getString("pfkey"));
                if (obj.has("msg")) resultMap.putString("msg", obj.getString("msg"));
                if (obj.has("login_cost")) resultMap.putInt("login_cost", obj.getInt("login_cost"));
                if (obj.has("query_authority_cost"))
                    resultMap.putInt("query_authority_cost", obj.getInt("query_authority_cost"));
                if (obj.has("authority_cost"))
                    resultMap.putInt("authority_cost", obj.getInt("authority_cost"));
                resultMap.putString("appid", appId);

                resultMap.putString(EVENT_PROP_SOCIAL_MSG, AUTH_RESULT_MSG_SUCCESSFUL);

            } catch (Exception e) {
                WritableMap map = Arguments.createMap();
                map.putInt(EVENT_PROP_SOCIAL_CODE, Constants.ERROR_UNKNOWN);
                map.putString(EVENT_PROP_SOCIAL_MSG, e.getLocalizedMessage());

                if (mEventEmitter != null)
                    mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), map);
            }
        } else {
            resultMap.putString(EVENT_PROP_SOCIAL_TYPE, "QQShareResponse");
            resultMap.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_SUCCESSFUL);
            resultMap.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_SUCCESSFUL);
        }

        if (mEventEmitter != null)
            mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), resultMap);
    }

    @Override
    public void onError(UiError uiError) {
        WritableMap resultMap = Arguments.createMap();

        if (isLoginOperation) {
            resultMap.putString(EVENT_PROP_SOCIAL_TYPE, "QQAuthorizeResponse");
            resultMap.putInt(EVENT_PROP_SOCIAL_CODE, AUTH_RESULT_CODE_FAILED);
            resultMap.putString(EVENT_PROP_SOCIAL_MSG, AUTH_RESULT_MSG_FAILED + ":" + uiError.errorDetail);
        } else {
            resultMap.putString(EVENT_PROP_SOCIAL_TYPE, "QQShareResponse");
            resultMap.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_FAILED);
            resultMap.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_FAILED +":" + uiError.errorDetail);
        }

        if (mEventEmitter != null)
            mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), resultMap);
    }

    @Override
    public void onCancel() {
        WritableMap resultMap = Arguments.createMap();

        if (isLoginOperation) {
            resultMap.putString(EVENT_PROP_SOCIAL_TYPE, "QQAuthorizeResponse");
            resultMap.putInt(EVENT_PROP_SOCIAL_CODE, AUTH_RESULT_CODE_CANCEL);
            resultMap.putString(EVENT_PROP_SOCIAL_MSG, AUTH_RESULT_MSG_CANCEL);
        } else {
            resultMap.putString(EVENT_PROP_SOCIAL_TYPE, "QQShareResponse");
            resultMap.putInt(EVENT_PROP_SOCIAL_CODE, SHARE_RESULT_CODE_CANCEL);
            resultMap.putString(EVENT_PROP_SOCIAL_MSG, SHARE_RESULT_MSG_CANCEL);
        }
        if (mEventEmitter != null)
            mEventEmitter.emit(Events.EVENT_QQ_RESP.toString(), resultMap);
    }

}