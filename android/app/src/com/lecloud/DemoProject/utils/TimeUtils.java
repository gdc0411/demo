package com.lecloud.DemoProject.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LizaRao on 2016/11/13.
 */

public class TimeUtils {

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16:09"）
     *
     * @param time
     * @return
     */
    public static String timet(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        @SuppressWarnings("unused")
//        long lcc = Long.valueOf(time);
//        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(time));
        return times;
    }
}
