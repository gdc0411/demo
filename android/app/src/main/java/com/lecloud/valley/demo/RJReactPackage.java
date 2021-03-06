package com.lecloud.valley.demo;

import com.lecloud.valley.leecoSdk.LeReactPushViewManager;
import com.lecloud.valley.leecoSdk.LeReactSubVideoViewManager;
import com.lecloud.valley.leecoSdk.LeReactVideoViewManager;
import com.lecloud.valley.modules.CacheModule;
import com.lecloud.valley.modules.ImageCropPickerModule;
import com.lecloud.valley.modules.DownloadModule;
import com.lecloud.valley.modules.ImagePickerModule;
import com.lecloud.valley.modules.LePayModule;
import com.lecloud.valley.modules.OrientationModule;
import com.lecloud.valley.demo.sample.CallbackModule;
import com.lecloud.valley.demo.sample.CheckItemViewManager;
import com.lecloud.valley.demo.sample.ConstModule;
import com.lecloud.valley.demo.sample.EmbedModule;
import com.lecloud.valley.demo.sample.KenBurnsViewManager;
import com.lecloud.valley.demo.sample.PromiseModule;
import com.lecloud.valley.demo.sample.RJNativeModule;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.lecloud.valley.modules.DeviceModule;
import com.lecloud.valley.modules.QQModule;
import com.lecloud.valley.modules.UmengPushModule;
import com.lecloud.valley.modules.WeChatModule;
import com.lecloud.valley.modules.WeiboModule;
import com.lecloud.valley.views.LinearGradientManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by raojia on 16/9/2.
 */
public class RJReactPackage implements ReactPackage {

    private RJNativeModule rjNativeModule;

    public RJNativeModule getRjNativeModule() {
        return rjNativeModule;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        rjNativeModule = new RJNativeModule(reactContext);
        modules.add(rjNativeModule);
        modules.add(new CallbackModule(reactContext));
        modules.add(new PromiseModule(reactContext));
        modules.add(new EmbedModule(reactContext));
        modules.add(new ConstModule(reactContext));
        modules.add(new DeviceModule(reactContext));
        modules.add(new OrientationModule(reactContext));
        modules.add(new WeChatModule(reactContext));
        modules.add(new QQModule(reactContext));
        modules.add(new WeiboModule(reactContext));
        modules.add(new UmengPushModule(reactContext));
        modules.add(new DownloadModule(reactContext));
        modules.add(new CacheModule(reactContext));
        modules.add(new ImagePickerModule(reactContext));
        modules.add(new ImageCropPickerModule(reactContext));
        modules.add(new LePayModule(reactContext));
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> viewManagers = new ArrayList<>();
        viewManagers.add(new KenBurnsViewManager());
        viewManagers.add(new CheckItemViewManager());
//        viewManagers.add(new LeReactPlayerManager());
        viewManagers.add(new LeReactVideoViewManager());
        viewManagers.add(new LeReactSubVideoViewManager());
        viewManagers.add(new LeReactPushViewManager());
        viewManagers.add(new LinearGradientManager());

        return viewManagers;
    }
}
