package com.demoproject.utils;

/**
 * Created by LizaRao on 2016/11/6.
 */

public class LogUtils {

    public static final String TAG = "DemoProject";

    public static String getTraceInfo() {
        StringBuilder sb = new StringBuilder();
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        sb.append(stacks[level].getClassName()).append(".").append(stacks[level].getMethodName()).append("(): ").append(stacks[level].getLineNumber()).append(" ");
        //sb.append("class: " ).append(stacks[level].getClassName()).append("; method: ").append(stacks[level].getMethodName()).append("; number: ").append(stacks[level].getLineNumber());

        return sb.toString();
    }
}
