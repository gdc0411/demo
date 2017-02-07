package com.lecloud.valley.demo;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.umeng.message.PushAgent;

//import static anetwork.channel.http.NetworkSdkSetting.context;

public class MainActivity extends ReactActivity implements DefaultHardwareBackBtnHandler {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "LeDemo";
    }

    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PushAgent.getInstance(this).onAppStart();

        //动态申请权限
//        checkSelfPermission();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
    }

    /**
     * 检查权限,获取所有需要的权限
     * 当targetSdkVersion大于23并且打算在6.0手机上运行时,请动态申请SDK所需要的权限
     */
    public void checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        Intent intent = new Intent("onConfigurationChanged");
//        intent.putExtra("newConfig", newConfig);
//        this.sendBroadcast(intent);
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mReactRootView = new ReactRootView(this);
//        mReactInstanceManager = ReactInstanceManager.builder()
//                .setApplication(getApplication())
//                .setBundleAssetName("index.android.bundle")
//                .setJSMainModuleName("index.android")
//                .addPackage(new MainReactPackage())
//                .addPackage(new RJReactPackage()) // <--- add fab package here
//                .setUseDeveloperSupport(BuildConfig.DEBUG)
//                .setInitialLifecycleState(LifecycleState.RESUMED)
//                .build();
//
//        mReactRootView.startReactApplication(mReactInstanceManager, "LeDemo", null);
//        setContentView(mReactRootView);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (1 != requestCode || RESULT_OK != resultCode) return;
//
//        Uri contactData = data.getData();
//
//        Cursor cursor = managedQuery(contactData, null, null, null, null);
//        cursor.moveToFirst();
//
//        String num = getContactPhone(cursor);
//
//        //Log.i("饶佳的测试",num);
//
//        //把num发给RN侧
//        //MainApplication.getRjPackage().getRjNativeModule().sendMsgToRN(num);
//
//    }

}
