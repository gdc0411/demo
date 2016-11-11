package com.demoproject.leecoSdk;

import com.lecloud.app.openappdev.utils.LogUtils;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.player.IAdPlayer;

/**
 * Created by raojia on 2016/11/11.
 */

public interface Constants {

    public static final String REACT_CLASS = "RCTLeVideoView";

    /**
     * T播放器模式
     */
    public static final String PROP_PLAY_MODE = PlayerParams.KEY_PLAY_MODE;

    /**
     * URI数据源：本地或者在线  复杂数据源
     */
    public static final String PROP_SRC = "src";
    /**
     * URI地址
     */
    public static final String PROP_URI = "uri";

    //点播模式
    public static final String PROP_SRC_VOD_UUID = PlayerParams.KEY_PLAY_UUID;
    public static final String PROP_SRC_VOD_VUID = PlayerParams.KEY_PLAY_VUID;
    public static final String PROP_SRC_VOD_BUSINESSLINE = PlayerParams.KEY_PLAY_BUSINESSLINE;
    public static final String PROP_SRC_VOD_SAAS = "saas";


    //活动直播模式
    public static final String PROP_SRC_ALIVE_ACTIONID = PlayerParams.KEY_PLAY_ACTIONID;
    public static final String PROP_SRC_ALIVE_CUSTOMERID = PlayerParams.KEY_PLAY_CUSTOMERID;
    public static final String PROP_SRC_ALIVE_BUSINESSLINE = PlayerParams.KEY_PLAY_BUSINESSLINE;
    public static final String PROP_SRC_ALIVE_CUID = PlayerParams.KEY_ACTION_CUID;
    public static final String PROP_SRC_ALIVE_UTIOKEN = PlayerParams.KEY_ACTION_UTOKEN;
    public static final String PROP_SRC_ALIVE_IS_USEHLS = PlayerParams.KEY_PLAY_USEHLS;

    /**
     * 是否全景
     */
    public static final String PROP_SRC_IS_PANO = "pano";
    public static final String PROP_SRC_HAS_SKIN = "hasSkin";

    // 暂停方法
    public static final String PROP_PAUSED = "paused";
    // 快进方法
    public static final String PROP_SEEK = "seek";
    // 切换码率
    public static final String PROP_RATE = "rate";
    // 音量调节
    public static final String PROP_VOLUME = "volume";
    // 屏幕亮度调节
    public static final String PROP_BRIGHTNESS = "brightness";


    //字段名
    public static final String EVENT_PROP_TITLE = "title"; //视频标题
    public static final String EVENT_PROP_DURATION = "duration"; //视频总长
    public static final String EVENT_PROP_PLAYABLE_DURATION = "playableDuration"; //可播放时长
    public static final String EVENT_PROP_PLAY_BUFFERPERCENT = PlayerParams.KEY_PLAY_BUFFERPERCENT; //二级进度长度百分比
    public static final String EVENT_PROP_CURRENT_TIME = "currentTime"; //当前时长
    public static final String EVENT_PROP_SEEK_TIME = "seekTime"; //跳转时间
    public static final String EVENT_PROP_NATURALSIZE = "naturalSize"; //视频原始尺寸
    public static final String EVENT_PROP_VIDEO_BUFF = PlayerParams.KEY_VIDEO_BUFFER;  //缓冲加载进度百分比
    public static final String EVENT_PROP_RATELIST = "rateList";  //可选择的码率
    public static final String EVENT_PROP_CURRENT_RATE = "currentRate"; //当前码率
    public static final String EVENT_PROP_DEFAULT_RATE = "defaultRate"; //默认码率
    public static final String EVENT_PROP_NEXT_RATE = "nextRate"; //下一个码率

    static final String EVENT_PROP_MMS_STATCODE = "statusCode";  //媒资返回状态码
    static final String EVENT_PROP_MMS_HTTPCODE = "httpCode"; //媒资返回HTTP状态码

    static final String EVENT_PROP_WIDTH = "width"; //视频宽度
     static final String EVENT_PROP_HEIGHT = "height"; //视频高度

     static final String EVENT_PROP_ORIENTATION = "orientation"; //视频方向
     static final String EVENT_PROP_AD_TIME = IAdPlayer.AD_TIME; //广告倒计时

     static final String EVENT_PROP_STAT_CODE = PlayerParams.KEY_RESULT_STATUS_CODE; //媒资状态码
     static final String EVENT_PROP_HTTP_CODE = PlayerParams.KEY_HTTP_CODE; //媒资http请求状态

     static final String EVENT_PROP_RATE_KEY = "rateKey";  //码率索引
     static final String EVENT_PROP_RATE_VALUE = "rateValue";  //码率值

     static final String EVENT_PROP_LOGO = "logo";  //logo属性
     static final String EVENT_PROP_LOAD = "loading";  //加载属性
     static final String EVENT_PROP_WMARKS = "waterMarks";  //水印

     static final String EVENT_PROP_PIC = "pic";  //图片地址
     static final String EVENT_PROP_TARGET = "target";  //目标URL
     static final String EVENT_PROP_POS = "pos";  //位置

     static final String EVENT_PROP_VOLUME = "volume";  //音量
     static final String EVENT_PROP_BRIGHTNESS = "brightness";  //亮度

     static final String EVENT_PROP_ERROR = "error";
     static final String EVENT_PROP_WHAT = "what";
     static final String EVENT_PROP_EXTRA = "extra";
     static final String EVENT_PROP_EVENT = "event";


}
