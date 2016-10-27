package com.letv.android.client.skin.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.letv.android.client.skin.ui.LetvUIListener;

/**
 * 播放按钮
 */
public abstract class BaseBtn extends ImageView {
	protected LetvUIListener mLetvUIListener;
	
	public BaseBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseBtn(Context context) {
        super(context);
    }
    
    public void setLetvUIListener(LetvUIListener mLetvUIListener) {
		this.mLetvUIListener = mLetvUIListener;
	}
    
    
}
