package com.lecloud.valley.modules;

import android.app.Activity;
import android.os.Bundle;

import com.lecloud.valley.modules.WeChatModule;

/**
 * Created by raojia on 2016/12/20.
 */
public class WXEntryActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeChatModule.handleIntent(getIntent());
        finish();
    }
}
