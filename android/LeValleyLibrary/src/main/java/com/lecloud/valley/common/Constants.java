package com.lecloud.valley.common;

import com.lecloud.sdk.constant.PlayerParams;

/**
 * Created by raojia on 2016/11/11.
 */
public interface Constants {

//============== REACT_CLASS ======================

    String REACT_CLASS_VIDEO_VIEW = "RCTLeVideo";

    String REACT_CLASS_SUB_VIDEO_VIEW = "RCTLeSubVideo";

    String REACT_CLASS_PUSH_VIEW = "RCTLePush";

    String REACT_CLASS_LINEAR_GRADIENT_VIEW = "RCTLinearGradient";

    String REACT_CLASS_DEVICE_MODULE = "RCTDeviceModule";

    String REACT_CLASS_CACHE_MODULE = "RCTCacheModule";

    String REACT_CLASS_ORIENTATION_MODULE = "RCTOrientationModule";

    String REACT_CLASS_WECHAT_MODULE = "RCTWeChatModule";

    String REACT_CLASS_QQ_MODULE = "RCTQQModule";

    String REACT_CLASS_WEIBO_MODULE = "RCTWeiboModule";

    String REACT_CLASS_UMENG_PUSH_MODULE = "RCTUmengPushModule";

    String REACT_CLASS_DOWNLOAD_MODULE = "RCTDownloadModule";

    String REACT_CLASS_IMAGE_PICKER_MODULE = "RCTImagePickerModule";

//============== REACT_METHOD ======================

    //方法
    String PROP_SRC = "src";  //URI数据源：本地或者在线  复杂数据源
    String PROP_SRC_PLAY_MODE = PlayerParams.KEY_PLAY_MODE;  //播放器模式
    String PROP_SRC_URI = "uri"; //URI地址
    //点播模式参数
    String PROP_SRC_VOD_UUID = "uuid";
    String PROP_SRC_VOD_VUID = "vuid";
    String PROP_SRC_VOD_BUSINESSLINE = "businessline";
    String PROP_SRC_VOD_SAAS = "saas";
    String PROP_SRC_VOD_EXTRA = "videoInfo";
    //活动直播模式参数
    String PROP_SRC_ALIVE_ACTIONID = "actionId";
    String PROP_SRC_ALIVE_CUSTOMERID = "customerId";
    String PROP_SRC_ALIVE_BUSINESSLINE = "businessline";
    String PROP_SRC_ALIVE_CUID = "cuid";
    String PROP_SRC_ALIVE_UTIOKEN = "utoken";
    String PROP_SRC_ALIVE_IS_USEHLS = "usehls";
    //机位播放器参数
    String PROP_SRC_LIVE_ID = "liveId";
    String PROP_SRC_STREAM_ID = "streamId";
    String PROP_SRC_STREAM_URL = "streamUrl";
    String PROP_SRC_IS_PANO = "pano";
    String PROP_SRC_IS_REPEAT = "repeat";

    // 暂停方法
    String PROP_PAUSED = "paused";
    // 重播方法
    String PROP_REPEAT = "repeat";
    // 快进方法
    String PROP_SEEK = "seek";
    // 切换码率
    String PROP_RATE = "rate";
    // 切换机位
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
    // 是否后台播放
    String PROP_PLAY_IN_BACKGROUND = "playInBackground";


//================= REACT_PUSH ====================


    String PROP_PUSH_PARA = "para"; //推流参数，包含移动直播（无/有地址）、乐视云直播
    String PROP_PUSH_TYPE = "type"; //推流类型
    String PROP_PUSH = "push"; //开始/停止推流
    String PROP_CAMERA = "camera"; //切换摄像头
    String PROP_FLASH = "flash"; //打开/关闭闪光灯
    String PROP_FILTER = "filter"; //选取滤镜

    // 定义分享类型常量
    int PUSH_TYPE_MOBILE_URI = 0;
    int PUSH_TYPE_MOBILE = 1;
    int PUSH_TYPE_LECLOUD = 2;
    int PUSH_TYPE_NONE = -1;

    String EVENT_PROP_PUSH_PUSH_URL = "pushUrl";  //推流地址
    String EVENT_PROP_PUSH_PLAY_URL = "playUrl";  //播放地址

    String EVENT_PROP_PUSH_STATE = "state";  //推流操作状态
    String EVENT_PROP_PUSH_TIME = "time"; //推流计时
    String EVENT_PROP_PUSH_TIME_FLAG = "timeFlag"; //推流计时标识,为true正在计时
    String EVENT_PROP_PUSH_CAMERA_FLAG = "cameraFlag";  //摄像头切换标识，为true表示正在切换，不可以切换
    String EVENT_PROP_PUSH_CAMERA_DIRECTION = "cameraDirection";  //摄像头方向，1为前置，0为后置
    String EVENT_PROP_PUSH_FLASH_FLAG = "flashFlag"; //闪光灯标识，为true表示打开
    String EVENT_PROP_PUSH_FILTER = "filter"; //滤镜
    String EVENT_PROP_PUSH_VOLUME = "volume"; //音量

//============== REACT_EVENT_PROP ======================

    String EVENT_PROP_PLAY_MODE = "playMode"; //播放模式（VOD，LIVE）

    //字段名
    String EVENT_PROP_TITLE = "title"; //视频标题（VOD，LIVE）
    String EVENT_PROP_DURATION = "duration"; //视频总长（VOD）
    String EVENT_PROP_PLAYABLE_DURATION = "playableDuration"; //可播放时长（VOD）
    String EVENT_PROP_PLAY_BUFFERPERCENT = "bufferpercent"; //二级进度长度百分比（VOD）
    String EVENT_PROP_CURRENT_TIME = "currentTime"; //当前时长（VOD，LIVE）
    String EVENT_PROP_SEEK_TIME = "seekTime"; //跳转时间（VOD）
    String EVENT_PROP_VOD_IS_DOWNLOAD = "isDownload"; //是否允许下载（VOD）
    String EVENT_PROP_VOD_IS_PANO = "isPano"; //是否全景（VOD）
    String EVENT_PROP_NATURALSIZE = "naturalSize"; //视频原始尺寸（VOD，LIVE）
    String EVENT_PROP_VIDEO_BUFF = "videobuff";  //缓冲加载进度百分比（VOD）
    String EVENT_PROP_RATELIST = "rateList";  //可选择的码率（VOD，LIVE）
    String EVENT_PROP_CURRENT_RATE = "currentRate"; //当前码率（VOD，LIVE）
    String EVENT_PROP_DEFAULT_RATE = "defaultRate"; //默认码率（VOD，LIVE）
    String EVENT_PROP_NEXT_RATE = "nextRate"; //下一个码率（VOD，LIVE）

    String EVENT_PROP_LIVE_NEED_FULLVIEW = "needFullView"; //是否全屏（LIVE）
    String EVENT_PROP_LIVE_NEED_TIMESHIFT = "needTimeShift"; //是否支持时移（LIVE）
    String EVENT_PROP_LIVE_IS_NEED_AD = "isNeedAd"; //是否有广告（LIVE）
    String EVENT_PROP_LIVE_ARK = "ark"; //ARK（LIVE）
    String EVENT_PROP_CURRENT_LIVE = "currentLive"; //当前机位（LIVE）
    String EVENT_PROP_NEXT_LIVE = "nextLive"; //切换后的机位（LIVE）
    String EVENT_PROP_SERVER_TIME = "serverTime"; //服务器时间（LIVE）
    String EVENT_PROP_LIVE_BEGIN_TIME = "beginTime"; //直播开始时间（LIVE）
    String EVENT_PROP_LIVE_END_TIME = "endTime"; //直播结束时间（LIVE）
    String EVENT_PROP_ACTIONLIVE = "actionLive"; //云直播数据（LIVE）
    String EVENT_PROP_LIVE_ACTION_STATE = "actionState"; //当前直播活动状态（LIVE）

    String EVENT_PROP_LIVE_ACTION_ID = "actionId"; //当前直播活动ID（LIVE）
    String EVENT_PROP_LIVE_COVER_IMG = "coverImgUrl"; //封面URL（LIVE）
    String EVENT_PROP_LIVE_PLAYER_URL = "playerPageUrl"; //直播页地址URL（LIVE）

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

//================= REACT_SOCIAL ====================

    // 定义分享类型常量
    String SHARE_TYPE_NEWS = "news";
    String SHARE_TYPE_IMAGE = "image";
    String SHARE_TYPE_IMAGE_FILE = "imageFile";
    String SHARE_TYPE_TEXT = "text";
    String SHARE_TYPE_VIDEO = "video";
    String SHARE_TYPE_AUDIO = "audio";
    String SHARE_TYPE_VOICE = "voice";
    String SHARE_TYPE_FILE = "file";
    String SHARE_TYPE_APP = "app";

    // 定义分享共性字段名
    String SHARE_PROP_TYPE = "type";
    String SHARE_PROP_TITLE = "title";
    String SHARE_PROP_TEXT = "text";
    String SHARE_PROP_DESP = "description";
    String SHARE_PROP_TARGET = "webpageUrl";
    String SHARE_PROP_THUMB_IMAGE = "thumbImage";
    String SHARE_PROP_IMAGE = "imageUrl";
    String SHARE_PROP_VIDEO = "videoUrl";
    String SHARE_PROP_AUDIO = "audioUrl";
    String SHARE_PROP_APPNAME = "appName";

    //事件字段定义
    String EVENT_PROP_SOCIAL_CODE = "errCode";
    String EVENT_PROP_SOCIAL_MSG = "errMsg";
    String EVENT_PROP_SOCIAL_TYPE = "type";

    //授权和分享返回码和消息体
    int AUTH_RESULT_CODE_SUCCESSFUL = 0;
    int AUTH_RESULT_CODE_FAILED = 1;
    int AUTH_RESULT_CODE_CANCEL = 2;

    String AUTH_RESULT_MSG_SUCCESSFUL = "登录成功";
    String AUTH_RESULT_MSG_FAILED = "授权失败，请稍后重试";
    String AUTH_RESULT_MSG_CANCEL = "授权失败，用户取消";

    int SHARE_RESULT_CODE_SUCCESSFUL = 0;
    int SHARE_RESULT_CODE_FAILED = 1;
    int SHARE_RESULT_CODE_CANCEL = 2;


    String SHARE_RESULT_MSG_SUCCESSFUL = "分享成功";
    String SHARE_RESULT_MSG_FAILED = "分享失败，请稍后重试";
    String SHARE_RESULT_MSG_CANCEL = "分享失败，用户取消";

    //事件返回码
    String CODE_NOT_REGISTERED = "-1";
    String CODE_NULL_ACTIVITY = "-2";
    String CODE_INVOKE_FAILED = "-3";
    String CODE_INVALID_ARGUMENT = "-4";

    //事件处理响应
    String MSG_NOT_REGISTERED = "NOT_REGISTERED";
    String MSG_NULL_ACTIVITY = "NULL_ACTIVITY";
    String MSG_INVOKE_FAILED = "INVOKE_FAILED";
    String MSG_INVALID_ARGUMENT = "INVALID_ARGUMENT";

}
