package com.lecloud.DemoProject.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecloud.DemoProject.R;

/**
 * Created by LizaRao on 2016/9/5.
 */
public class CheckItemView extends RelativeLayout {

    private CheckBox cb_status;
    private TextView tv_desc;
    private TextView tv_title;

    private void initView(Context context) {
        View.inflate(context, R.layout.view_check_item, this);
        cb_status = (CheckBox) this.findViewById(R.id.cb_status);
        tv_desc = (TextView) this.findViewById(R.id.tv_desc);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
    }

    public CheckItemView(Context context) {
        super(context);
        initView(context);
    }

    public CheckItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CheckItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    /**
     * 校验组合控件是否选中
     * @return
     */
    public boolean isChecked() {
        return cb_status.isChecked();
    }

    /**
     * 设置组合控件的状态
     * @param checked
     */
    public void setChecked(boolean checked) {
        cb_status.setChecked(checked);
    }


    /**
     * 设置组合控件的描述信息
     * @param text
     */
    public void setDesc(String text) {
        tv_desc.setText(text);
    }


    /**
     * 设置组合控件的标题
     * @param title
     */
    public void setTitle(String title) {
        tv_title.setText(title);
    }


}
