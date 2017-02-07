package com.lecloud.valley.modules;


import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.CacheUtils;
import com.lecloud.valley.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2017/1/27.
 */

interface ReactBaseFunc {

    void initialize();

    Map<String, Object> getConstants();

    void destroy();

}
