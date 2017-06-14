package com.lecloud.valley.modules;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.utils.LogUtils;
import com.letv.lepaysdk.Constants;
import com.letv.lepaysdk.ELePayState;
import com.letv.lepaysdk.LePay;
import com.letv.lepaysdk.LePayApi;
import com.letv.lepaysdk.LePayConfig;
import com.letv.lepaysdk.alipay.AliPay;
import com.letv.lepaysdk.alipay.AliPayCallback;
import com.letv.lepaysdk.alipay.AliPayResult;
import com.letv.lepaysdk.utils.LOG;
import com.letv.lepaysdk.utils.ToastUtils;
import com.letv.lepaysdk.wxpay.WXPay;
import com.letv.lepaysdk.wxpay.WXPayCallback;
import com.tencent.mm.sdk.modelbase.BaseResp;

import java.util.HashMap;
import java.util.Map;

import static com.lecloud.valley.common.Constants.CODE_INVALID_ARGUMENT;
import static com.lecloud.valley.common.Constants.CODE_NOT_REGISTERED;
import static com.lecloud.valley.common.Constants.CODE_NULL_ACTIVITY;
import static com.lecloud.valley.common.Constants.MSG_INVALID_ARGUMENT;
import static com.lecloud.valley.common.Constants.MSG_NOT_REGISTERED;
import static com.lecloud.valley.common.Constants.MSG_NULL_ACTIVITY;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by raojia on 2017/5/24.
 */

public class LePayFunc implements ReactBaseFunc, ActivityEventListener {

    private final ReactApplicationContext mReactContext;
    private final RCTNativeAppEventEmitter mEventEmitter;

    private WXPay mWxPay;
    private AliPay mAliPay;


    LePayConfig lePayConfig;

    LePayFunc(ReactApplicationContext reactContext, RCTNativeAppEventEmitter eventEmitter) {
        mReactContext = reactContext;
        mEventEmitter = eventEmitter;

        initialize();
    }

    @Override
    public void initialize() {

        if (lePayConfig == null) {
            lePayConfig = new LePayConfig();//参数配置
            lePayConfig.hasShowTimer = true;//设置支付时间是否显示
            lePayConfig.hasShowOrderInfo = true;//设置订单详情是否显示
            lePayConfig.mWatingTime = 20;//设置订单轮询时长
            //lePayConfig.hasShowPaySuccess = false;//是否显示成功提示
            lePayConfig.hasHalfPay = false;  //半屏支付， true（半屏）,false（全屏，默认）
//            lePayConfig.lepaymentCenterTitle = "leeco";
            lePayConfig.hasShowPaySuccess = true;//是否显示成功提示

            LePayApi.initConfig(mReactContext.getCurrentActivity(), lePayConfig);
        }
    }

    @Override
    public Map<String, Object> getConstants() {
        return null;
    }

    @Override
    public void destroy() {
        if (lePayConfig != null) {
            lePayConfig = null;
        }
        LePayApi.destory(mReactContext.getCurrentActivity());
    }

    void doPay(ReadableMap data, final Promise promise) {
        Log.d(TAG, LogUtils.getTraceInfo() + "LePay支付 ——— data：" + data.toString());

        if (lePayConfig == null) {
            promise.reject(CODE_NOT_REGISTERED, MSG_NOT_REGISTERED);
            return;
        } else if (mReactContext.getCurrentActivity() == null) {
            promise.reject(CODE_NULL_ACTIVITY, MSG_NULL_ACTIVITY);
            return;
        }

        final String tradeInfo = _makeLePayInfo(data);
        if (tradeInfo == null) {
            promise.reject(CODE_INVALID_ARGUMENT, MSG_INVALID_ARGUMENT);
            return;
        }

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                LePayApi.doPay(mReactContext.getCurrentActivity(), tradeInfo, new LePay.ILePayCallback() {
                    @Override
                    public void payResult(ELePayState status, String message) {
//                        Toast.makeText(mReactContext, status.toString(), Toast.LENGTH_SHORT).show();
                        if (ELePayState.CANCEL == status) {
                            //支付取消
                        } else if (ELePayState.FAILT == status) {
                            //支付失败
                        } else if (ELePayState.OK == status) {
                            //支付成功
                        } else if (ELePayState.PAYED == status) {
                            //已支付
                        } else if (ELePayState.WAITTING == status) {
                            //支付中
                        } else if (ELePayState.NONETWORK == status) {
                            //网络异常
                        } else {
                        }
                        promise.resolve(status.toString());
                    }
                });
            }
        }.execute();

//        promise.resolve(null);
    }


    private String _makeLePayInfo(ReadableMap map) {
        String strRequest = "";

        ReadableMapKeySetIterator iterator = map.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            if (key.equals("mProductUrls") || key.equals("mpid") || key.equals("mdeptid") || key.equals("letv_user_id"))
                continue;
            if (ReadableType.Number.equals(map.getType(key))) {
                double val = map.getDouble(key);
                if ((long) val == val)
                    strRequest += "&" + key + "=" + (long) map.getDouble(key);
                else
                    strRequest += "&" + key + "=" + map.getDouble(key);
            } else if (ReadableType.String.equals(map.getType(key))) {
                strRequest += "&" + key + "=" + map.getString(key);
            } else if (ReadableType.Boolean.equals(map.getType(key))) {
                strRequest += "&" + key + "=" + map.getBoolean(key);
            }
        }
        strRequest = !strRequest.equals("") ? strRequest.substring(1) : "";

        Log.d(TAG, LogUtils.getTraceInfo() + "LePay支付 ——— url：" + strRequest);
//        String str = "version=2.0&service=lepay.tv.api.show.cashier&merchant_business_id=78&user_id=178769661&user_name=Union&notify_url=http://trade.letv.com/&merchant_no=1311313131&out_trade_no=261836519&price=0.01&currency=RMB&pay_expire=21600&product_id=8888&product_name=LeTV&product_desc=TV60&product_urls=http://f.hiphotos.baidu.com/image/pic/item/91ef76c6a7efce1b687b6bc2ad51f3deb48f6562.jpg&timestamp=2016-06-06 14:05:47&key_index=1&input_charset=UTF-8&ip=10.72.108.52&sign=03ddfd352b57d5748270afe5850c7e1c&sign_type=MD5&d_ram=57090805760&d_terminal=PHONE&d_app_version=2.2.0&d_os_version=23&d_net=WIFI&d_wifi_mac=02%3A00%3A00%3A00%3A00%3A00&d_imei=868918020071944&d_display=1440*2560&d_package_version=2&d_sdk_version=2.2.0&d_imsi=unknown&d_model=Letv+X910&d_package_name=2.0";

        return strRequest;
    }


    private void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        LOG.logD("onActivityResult");
        if (requestCode == 0x21) {
            if (data != null) {
                ELePayState eLePayState = (ELePayState) data.getSerializableExtra(Constants.LePayApiResult.LEPAY_EPAYSTATUS);
                String content = data.getStringExtra(Constants.LePayApiResult.LEPAY_CONTENT);
                ToastUtils.makeText(mReactContext.getCurrentActivity(), "eLePayState: " + eLePayState + "|content:" + content);
                if (ELePayState.OK.equals(eLePayState)) {
                } else if (ELePayState.FAILT.equals(eLePayState)) {
                } else if (ELePayState.CANCEL.equals(eLePayState)) {
                } else if (ELePayState.NONETWORK.equals(eLePayState)) {
                }
            }
        }
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        try {
//            LOG.logE("onRestoreInstanceState");
//            super.onRestoreInstanceState(savedInstanceState);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
