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
    String PROP_SRC_VOD_UUID = "uuid";
    String PROP_SRC_VOD_VUID = "vuid";
    String PROP_SRC_VOD_BUSINESSLINE = "businessline";
    String PROP_SRC_VOD_SAAS = "saas";


    //活动直播模式
    String PROP_SRC_ALIVE_ACTIONID = "actionId";
    String PROP_SRC_ALIVE_CUSTOMERID = "customerId";
    String PROP_SRC_ALIVE_BUSINESSLINE = "businessline";
    String PROP_SRC_ALIVE_CUID = "cuid";
    String PROP_SRC_ALIVE_UTIOKEN = "utoken";
    String PROP_SRC_ALIVE_IS_USEHLS = "usehls";

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
    // 切换码率
    String PROP_LIVE = "live";
    // 音量调节
    String PROP_VOLUME = "volume";
    // 左右声道
    String PROP_TRACK = "track";
    // 点击广告
    String PROP_CLICKAD = "clickAd";
    // 屏幕亮度调节
    String PROP_BRIGHTNESS = "brightness";
    // 屏幕方向
    String PROP_ORIENTATION = "orientation";


    String EVENT_PROP_PLAY_MODE = "playMode"; //播放模式（VOD，LIVE）

    //字段名
    String EVENT_PROP_TITLE = "title"; //视频标题（VOD，LIVE）
    String EVENT_PROP_DURATION = "duration"; //视频总长（VOD）
    String EVENT_PROP_PLAYABLE_DURATION = "playableDuration"; //可播放时长（VOD）
    String EVENT_PROP_PLAY_BUFFERPERCENT = "bufferpercent"; //二级进度长度百分比（VOD）
    String EVENT_PROP_CURRENT_TIME = "currentTime"; //当前时长（VOD，LIVE）
    String EVENT_PROP_SEEK_TIME = "seekTime"; //跳转时间（VOD）
    String EVENT_PROP_NATURALSIZE = "naturalSize"; //视频原始尺寸（VOD，LIVE）
    String EVENT_PROP_VIDEO_BUFF = "videobuff";  //缓冲加载进度百分比（VOD）
    String EVENT_PROP_RATELIST = "rateList";  //可选择的码率（VOD，LIVE）
    String EVENT_PROP_CURRENT_RATE = "currentRate"; //当前码率（VOD，LIVE）
    String EVENT_PROP_DEFAULT_RATE = "defaultRate"; //默认码率（VOD，LIVE）
    String EVENT_PROP_NEXT_RATE = "nextRate"; //下一个码率（VOD，LIVE）

    String EVENT_PROP_CURRENT_LIVE = "currentLive"; //当前机位（LIVE）
    String EVENT_PROP_NEXT_LIVE = "nextLive"; //切换后的机位（LIVE）
    String EVENT_PROP_SERVER_TIME = "serverTime"; //服务器时间（LIVE）
    String EVENT_PROP_LIVE_BEGIN = "beginTime"; //直播开始时间（LIVE）
    String EVENT_PROP_LIVE_END = "endTime"; //直播开始时间（LIVE）
    String EVENT_PROP_ACTIONLIVE = "actionLive"; //云直播数据（LIVE）
    String EVENT_PROP_LIVE_ACTION_STATE = "actionState"; //当前直播活动状态（LIVE）

    String EVENT_PROP_LIVE_ACTION_ID = "actionId"; //当前直播活动ID（LIVE）
    String EVENT_PROP_LIVE_COVER_IMG = "coverImgUrl"; //封面URL（LIVE）
    String EVENT_PROP_LIVE_PLAYER_URL = "playerPageUrl"; //直播页地址URL（LIVE）
    String EVENT_PROP_LIVE_BEGIN_TIME = "beginTime"; //直播开始时间（LIVE）
    String EVENT_PROP_LIVE_START_TIME = "startTime"; //直播开始时间（LIVE）

    String EVENT_PROP_LIVES = "lives";  //机位集合（LIVE）

    String EVENT_PROP_LIVE_ID = "liveId"; //机位Id（LIVE）
    String EVENT_PROP_LIVE_MACHINE = "machine"; //机位Machine（LIVE）
    String EVENT_PROP_LIVE_PRV_STEAMID = "previewSteamId"; //流ID（LIVE）
    String EVENT_PROP_LIVE_PRV_STEAMURL = "previewSteamPlayUrl"; //预览流URL（LIVE）
    String EVENT_PROP_LIVE_STATUS = "liveStatus"; //机位状态（LIVE）
    String EVENT_PROP_STREAM_ID = "streamId"; //当前机位流ID（LIVE）

    String EVENT_PROP_ONLINE_NUM = "onlineNum"; //当前在线人数

    String EVENT_PROP_MMS_STATCODE = "statusCode";  //媒资返回状态码
    String EVENT_PROP_MMS_HTTPCODE = "httpCode"; //媒资返回HTTP状态码

    String EVENT_PROP_ERROR_CODE = "errorCode"; //错误码
    String EVENT_PROP_ERROR_MSG = "errorMsg"; //错误描述

    String EVENT_PROP_WIDTH = "width"; //视频宽度
    String EVENT_PROP_HEIGHT = "height"; //视频高度

    String EVENT_PROP_ORIENTATION = "orientation"; //屏幕方向
    String EVENT_PROP_VIDEO_ORIENTATION = "videoOrientation"; //视频方向
    String EVENT_PROP_AD_TIME = "AdTime"; //广告倒计时

    String EVENT_PROP_RATE_KEY = "rateKey";  //码率索引
    String EVENT_PROP_RATE_VALUE = "rateValue";  //码率值

    String EVENT_PROP_LOGO = "logo";  //logo属性集合
    String EVENT_PROP_LOAD = "loading";  //加载属性集合
    String EVENT_PROP_WMARKS = "waterMarks";  //水印集合

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
