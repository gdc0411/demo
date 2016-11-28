package com.lecloud.DemoProject.leecoSdk.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by raojia on 16/11/2.
 */
public class LeTimer extends Timer {
    private IChange listener;
    private long delaymillts;

    public LeTimer(IChange listener, long delaymillts) {
        this.listener = listener;
        this.delaymillts = delaymillts;
    }

    public void start() {
        schedule(new TimerTask() {
            @Override
            public void run() {
                listener.onChange();
            }
        }, delaymillts, delaymillts);
    }
}
