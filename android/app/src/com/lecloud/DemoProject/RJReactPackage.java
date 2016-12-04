package com.lecloud.DemoProject;

import com.lecloud.DemoProject.leecoSdk.LeReactPlayerManager;
import com.lecloud.DemoProject.sample.CallbackModule;
import com.lecloud.DemoProject.sample.CheckItemViewManager;
import com.lecloud.DemoProject.sample.ConstModule;
import com.lecloud.DemoProject.sample.EmbedModule;
import com.lecloud.DemoProject.sample.KenBurnsViewManager;
import com.lecloud.DemoProject.sample.PromiseModule;
import com.lecloud.DemoProject.sample.RJNativeModule;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.lecloud.DemoProject.utils.DeviceIdentifier;

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
        modules.add(new DeviceIdentifier(reactContext));
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
//        viewManagers.add(new LeVideoViewManager());
//        viewManagers.add(new VideoViewManager());
        viewManagers.add(new LeReactPlayerManager());

        return viewManagers;
    }
}
