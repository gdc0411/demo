package com.demoproject;

import com.demoproject.leecoSdk.LeVideoViewManager;
import com.demoproject.sample.CallbackModule;
import com.demoproject.sample.CheckItemViewManager;
import com.demoproject.sample.ConstModule;
import com.demoproject.sample.EmbedModule;
import com.demoproject.sample.KenBurnsViewManager;
import com.demoproject.sample.PromiseModule;
import com.demoproject.sample.RJNativeModule;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

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
        viewManagers.add(new LeVideoViewManager());
//        viewManagers.add(new VideoViewManager());

        return viewManagers;
    }
}
