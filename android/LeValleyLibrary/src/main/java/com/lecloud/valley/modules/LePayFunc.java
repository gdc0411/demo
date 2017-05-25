package com.lecloud.valley.modules;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.utils.LogUtils;
import com.letv.lepaysdk.ELePayState;
import com.letv.lepaysdk.LePay;
import com.letv.lepaysdk.LePayApi;
import com.letv.lepaysdk.LePayConfig;

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

public class LePayFunc implements ReactBaseFunc {

    private final ReactApplicationContext mReactContext;
    private final RCTNativeAppEventEmitter mEventEmitter;

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
        }

        LePayApi.initConfig(mReactContext.getCurrentActivity(), lePayConfig);
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

        String tradeInfo = _makeLePayInfo(data);
        if (tradeInfo == null) {
            promise.reject(CODE_INVALID_ARGUMENT, MSG_INVALID_ARGUMENT);
            return;
        }

        String componentName = mReactContext.getCurrentActivity().getComponentName().toString();
        promise.resolve(componentName);

//        LePayApi.doPay(mReactContext.getCurrentActivity(), tradeInfo, new LePay.ILePayCallback() {
//            @Override
//            public void payResult(ELePayState status, String message) {
//                if (ELePayState.CANCEL == status) {
//                    //支付取消
//                } else if (ELePayState.FAILT == status) {
//                    //支付失败
//                } else if (ELePayState.OK == status) {
//                    //支付成功
//                } else if (ELePayState.WAITTING == status) {
//                    //支付中
//                } else if (ELePayState.NONETWORK == status) {
//                    //网络异常
//                }else{
//                }
//                promise.resolve(status);
//            }
//        });

//        promise.resolve(null);
    }


    private String _makeLePayInfo(ReadableMap data){
        String str = "version=2.0&service=lepay.tv.api.show.cashier&merchant_business_id=78&user_id=178769661&user_name=Union&notify_url=http://trade.letv.com/&merchant_no=1311313131&out_trade_no=261836519&price=0.01&currency=RMB&pay_expire=21600&product_id=8888&product_name=LeTV&product_desc=TV60&product_urls=http://f.hiphotos.baidu.com/image/pic/item/91ef76c6a7efce1b687b6bc2ad51f3deb48f6562.jpg&timestamp=2016-06-06 14:05:47&key_index=1&input_charset=UTF-8&ip=10.72.108.52&sign=03ddfd352b57d5748270afe5850c7e1c&sign_type=MD5&d_ram=57090805760&d_terminal=PHONE&d_app_version=2.2.0&d_os_version=23&d_net=WIFI&d_wifi_mac=02%3A00%3A00%3A00%3A00%3A00&d_imei=868918020071944&d_display=1440*2560&d_package_version=2&d_sdk_version=2.2.0&d_imsi=unknown&d_model=Letv+X910&d_package_name=2.0";
        return str;
    }

}
