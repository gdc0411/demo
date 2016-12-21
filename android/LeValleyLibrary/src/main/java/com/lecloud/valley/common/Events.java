package com.lecloud.valley.common;

/**
 * Created by LizaRao on 2016/11/6.
 */
public enum Events {

    //乐视播放器事件
    EVENT_LOAD_SOURCE("onVideoSourceLoad"), // 传入数据源
    EVENT_CHANGESIZE("onVideoSizeChange"), // 视频真实宽高
    EVENT_LOAD_RATE("onVideoRateLoad"), // 视频码率列表
    EVENT_LOAD("onVideoLoad"), // 播放器准备完毕
    EVENT_ERROR("onVideoError"), // 播放出错
    EVENT_PROGRESS("onVideoProgress"), // 正在播放视频
    EVENT_PLAYABLE_PERCENT("onVideoBufferPercent"), // 缓存进度
    EVENT_PAUSE("onVideoPause"), // 播放暂停
    EVENT_RESUME("onVideoResume"), // 播放继续
    EVENT_SEEK("onVideoSeek"), // 播放跳转中
    EVENT_SEEK_COMPLETE("onVideoSeekComplete"), // 播放跳转结束
    EVENT_RATE_CHANG("onVideoRateChange"), //视频码率切换
    EVENT_END("onVideoEnd"),  // 播放完毕
    EVENT_BUFFER_START("onBufferStart"),  // 开始缓冲
    EVENT_BUFFER_END("onBufferEnd"), // 缓冲结束
    EVENT_RENDING_START("onVideoRendingStart"), // 加载第一帧
    EVENT_BUFFER_PERCENT("onBufferPercent"),  // 缓冲加载进度，转圈
    EVENT_AD_START("onAdvertStart"),  // 广告开始
    EVENT_AD_PROGRESS("onAdvertProgress"),  // 广告播放中
    EVENT_AD_COMPLETE("onAdvertComplete"),  // 广告结束
    EVENT_AD_CLICK("onAdvertClick"),  // 广告点击
    EVENT_AD_ERROR("onAdvertError"),  // 广告出错
    EVENT_MEDIA_VOD("onMediaVodLoad"),  // 获得点播媒资
    EVENT_MEDIA_LIVE("onMediaLiveLoad"),  // 获得直播媒资
    EVENT_MEDIA_ACTION("onMediaActionLoad"),  // 获得活动直播媒资
    EVENT_MEDIA_PLAYURL("onMediaPlayURLLoad"),  // 获得媒资调度
    EVENT_ACTION_LIVE_CHANGE("onActionLiveChange"), // 云直播切换机位
    EVENT_ACTION_TIME_SHIFT("onActionTimeShift"), // 云直播进度
    EVENT_ACTION_STATUS_CHANGE("onActionStatusChange"), // 云直播状态回调
    EVENT_ONLINE_NUM_CHANGE("onActionOnlineNumChange"), // 云直播在线人数变化

    //乐视机位播放器事件
    EVENT_SUB_LOAD_SOURCE("onSubVideoSourceLoad"), // 传入机位数据源
    EVENT_SUB_CHANGESIZE("onSubVideoSizeChange"), // 机位视频真实宽高
    EVENT_SUB_LOAD("onSubVideoLoad"), // 机位播放器准备完毕
    EVENT_SUB_ERROR("onSubVideoError"), // 机位播放出错
    EVENT_SUB_BUFFER_START("onSubBufferStart"),  // 机位播放开始缓冲
    EVENT_SUB_BUFFER_END("onSubBufferEnd"), // 机位播放缓冲结束
    EVENT_SUB_RENDING_START("onVideoRendingStart"), // 加载第一帧
    EVENT_SUB_PROGRESS("onSubVideoProgress"), // 正在播放机位视频
    EVENT_SUB_PAUSE("onSubVideoPause"), // 机位播放暂停
    EVENT_SUB_RESUME("onSubVideoResume"), // 机位播放继续

    //翻转模块事件
    EVENT_ORIENTATION_CHANG("onOrientationDidChange"), //屏幕方向切换

    // 未命名的事件
    EVENT_OTHER_EVENT("onOtherEventInfo");  // 未知事件


    private final String mName;

    Events(final String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}
