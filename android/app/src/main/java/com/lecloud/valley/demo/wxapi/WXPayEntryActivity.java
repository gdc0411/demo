package com.lecloud.valley.demo.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.lecloud.valley.modules.WeChatModule;

/**
 * Created by raojia on 2017/6/14.
 */

public class WXPayEntryActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeChatModule.getPayInstance(this);
    }

}
