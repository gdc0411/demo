package com.lecloud.skin.ui.base;

import java.util.List;

import com.demoproject.R;
import com.lecloud.skin.ui.utils.ReUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 */
public abstract class BaseChangeModeBtn extends BaseBtn {
    public static final int MODE_TOUCH = 0;
    public static final int MODE_MOVE = 1;
    public int panoMode = MODE_TOUCH;

    public BaseChangeModeBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BaseChangeModeBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseChangeModeBtn(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (checkSensor()) {
                        if (panoMode == MODE_TOUCH) {
                            panoMode =MODE_MOVE;
                        } else {
                            panoMode =MODE_TOUCH;
                        }
                        if (mLetvUIListener != null) {
                            mLetvUIListener.onSwitchPanoVideoMode(panoMode);
                        }
                        reset();
                }else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.your_phone_not_support), Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset();
    }

    /**
     * 恢复初始状态
     */
    protected void reset() {
        String btnResId = panoMode == MODE_TOUCH ? getTouchStyle() : getMoveStyle();
        setImageResource(ReUtils.getDrawableId(getContext(), btnResId));
    }
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private boolean checkSensor() {
        SensorManager sm = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        // 获取全部传感器列表
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        //旋转矢量传感器  判断是否支持陀螺仪
        for (Sensor item : sensors) {
            strLog.append(iIndex + ".");
            strLog.append(" Sensor Type - " + item.getType() + "\r\n");
            strLog.append(" Sensor Name - " + item.getName() + "\r\n");
            strLog.append(" Sensor Version - " + item.getVersion() + "\r\n");
            strLog.append(" Sensor Vendor - " + item.getVendor() + "\r\n");
            strLog.append(" Maximum Range - " + item.getMaximumRange() + "\r\n");
            strLog.append(" Minimum Delay - " + item.getMinDelay() + "\r\n");
            strLog.append(" Power - " + item.getPower() + "\r\n");
            strLog.append(" Resolution - " + item.getResolution() + "\r\n");
            strLog.append("\r\n");
            iIndex++;
            if (item.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                return true;
            }
        }
        return false;
    }
    
    protected abstract String getMoveStyle();

    protected abstract String getTouchStyle();
    
}
