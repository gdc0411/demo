package com.demoproject.leecoSdk;

import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.player.IAdPlayer;

/**
 * Created by raojia on 2016/11/11.
 */

public interface Constants {

    String REACT_CLASS = "RCTLeVideoView";

    /**
     * T播放器模式
     */
    String PROP_PLAY_MODE = PlayerParams.KEY_PLAY_MODE;

    /**
     * URI数据源：本地或者在线  复杂数据源
     */
    String PROP_SRC = "src";
    /**
     * URI地址
     */
    String PROP_URI = "uri";

    //点播模式
    String PROP_SRC_VOD_UUID = PlayerParams.KEY_PLAY_UUID;
    String PROP_SRC_VOD_VUID = PlayerParams.KEY_PLAY_VUID;
    String PROP_SRC_VOD_BUSINESSLINE = PlayerParams.KEY_PLAY_BUSINESSLINE;
    String PROP_SRC_VOD_SAAS = "saas";


    //活动直播模式
    String PROP_SRC_ALIVE_ACTIONID = PlayerParams.KEY_PLAY_ACTIONID;
    String PROP_SRC_ALIVE_CUSTOMERID = PlayerParams.KEY_PLAY_CUSTOMERID;
    String PROP_SRC_ALIVE_BUSINESSLINE = PlayerParams.KEY_PLAY_BUSINESSLINE;
    String PROP_SRC_ALIVE_CUID = PlayerParams.KEY_ACTION_CUID;
    String PROP_SRC_ALIVE_UTIOKEN = PlayerParams.KEY_ACTION_UTOKEN;
    String PROP_SRC_ALIVE_IS_USEHLS = PlayerParams.KEY_PLAY_USEHLS;

    /**
     * 是否全景
     */
    String PROP_SRC_IS_PANO = "pano";
    String PROP_SRC_HAS_SKIN = "hasSkin";

    // 暂停方法
    String PROP_PAUSED = "paused";
    // 快进方法
    String PROP_SEEK = "seek";
    // 切换码率
    String PROP_RATE = "rate";
    // 音量调节
    String PROP_VOLUME = "volume";
    // 屏幕亮度调节
    String PROP_BRIGHTNESS = "brightness";


    //字段名
    String EVENT_PROP_TITLE = "title"; //视频标题
    String EVENT_PROP_DURATION = "duration"; //视频总长
    String EVENT_PROP_PLAYABLE_DURATION = "playableDuration"; //可播放时长
    String EVENT_PROP_PLAY_BUFFERPERCENT = PlayerParams.KEY_PLAY_BUFFERPERCENT; //二级进度长度百分比
    String EVENT_PROP_CURRENT_TIME = "currentTime"; //当前时长
    String EVENT_PROP_SEEK_TIME = "seekTime"; //跳转时间
    String EVENT_PROP_NATURALSIZE = "naturalSize"; //视频原始尺寸
    String EVENT_PROP_VIDEO_BUFF = PlayerParams.KEY_VIDEO_BUFFER;  //缓冲加载进度百分比
    String EVENT_PROP_RATELIST = "rateList";  //可选择的码率
    String EVENT_PROP_CURRENT_RATE = "currentRate"; //当前码率
    String EVENT_PROP_DEFAULT_RATE = "defaultRate"; //默认码率
    String EVENT_PROP_NEXT_RATE = "nextRate"; //下一个码率


    String EVENT_PROP_COVER_IMG =  "coverImgUrl"; //封面URL（直播）
    String EVENT_PROP_PLAYER_URL =  "playerPageUrl"; //直播页地址URL（直播）


    String EVENT_PROP_MMS_STATCODE = "statusCode";  //媒资返回状态码
    String EVENT_PROP_MMS_HTTPCODE = "httpCode"; //媒资返回HTTP状态码

    String EVENT_PROP_ERROR_CODE = "errorCode"; //错误码
    String EVENT_PROP_ERROR_MSG = "errorMsg"; //错误描述

    String EVENT_PROP_WIDTH = "width"; //视频宽度
    String EVENT_PROP_HEIGHT = "height"; //视频高度

    String EVENT_PROP_ORIENTATION = "orientation"; //视频方向
    String EVENT_PROP_AD_TIME = IAdPlayer.AD_TIME; //广告倒计时

    String EVENT_PROP_RATE_KEY = "rateKey";  //码率索引
    String EVENT_PROP_RATE_VALUE = "rateValue";  //码率值

    String EVENT_PROP_LOGO = "logo";  //logo属性
    String EVENT_PROP_LOAD = "loading";  //加载属性
    String EVENT_PROP_WMARKS = "waterMarks";  //水印

    String EVENT_PROP_PIC = "pic";  //图片地址
    String EVENT_PROP_TARGET = "target";  //目标URL
    String EVENT_PROP_POS = "pos";  //位置

    String EVENT_PROP_VOLUME = "volume";  //音量
    String EVENT_PROP_BRIGHTNESS = "brightness";  //亮度

    String EVENT_PROP_ERROR = "error";
    String EVENT_PROP_WHAT = "what";
    String EVENT_PROP_EXTRA = "extra";
    String EVENT_PROP_EVENT = "event";


}
