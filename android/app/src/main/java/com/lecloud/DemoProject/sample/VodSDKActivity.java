package com.lecloud.DemoProject.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lecloud.DemoProject.R;

public class VodSDKActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_sdk);
    }

    public void onBack(View v) {
        finish();
    }


}
