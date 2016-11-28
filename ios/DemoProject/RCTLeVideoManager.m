#import "RCTLeVideoManager.h"
#import "RCTLeVideo.h"
#import "RCTBridge.h"
#import <AVFoundation/AVFoundation.h>

@implementation RCTLeVideoManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view
{
  return [[RCTLeVideo alloc] initWithEventDispatcher:self.bridge.eventDispatcher];
}

/* Should support: onLoadStart, onLoad, and onError to stay consistent with Image */

- (NSArray *)customDirectEventTypes
{
  return @[
    @"onVideoSourceLoad",  // 传入数据源
    @"onVideoSizeChange",  // 视频真实宽高
    @"onVideoRateLoad", // 视频码率列表
    @"onVideoLoad", // 播放器准备完毕
    @"onVideoError",  // 播放出错
    @"onVideoProgress", // 正在播放视频
    @"onVideoBufferPercent",  // 缓存进度
    @"onVideoPause", // 播放暂停
    @"onVideoResume", // 播放继续
    @"onVideoSeek", // 播放跳转中
    @"onVideoSeekComplete", // 播放跳转结束
    @"onVideoRateChange", //视频码率切换
    @"onVideoEnd",  // 播放完毕
    @"onBufferStart", // 开始缓冲
    @"onBufferEnd",  // 缓冲结束
    @"onVideoRendingStart", // 加载第一帧
    @"onBufferPercent", // 缓冲加载进度，转圈
    @"onAdvertStart", // 广告开始
    @"onAdvertProgress",  // 广告播放中
    @"onAdvertComplete", // 广告结束
    @"onAdvertClick", // 广告点击
    @"onAdvertError", // 广告出错
    @"onMediaVodLoad", // 获得点播媒资
    @"onMediaLiveLoad", // 获得直播媒资
    @"onMediaActionLoad", // 获得活动直播媒资
    @"onMediaPlayURLLoad", // 获得媒资调度
    @"onActionLiveChange", // 云直播切换机位
    @"onActionTimeShift", // 云直播进度
    @"onActionStatusChange", // 云直播状态回调
    @"onActionOnlineNumChange", // 云直播在线人数变化
    @"onOrientationChange", //屏幕方向切换
    @"onOtherEventInfo" // 其他事件
  ];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(src, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(paused, BOOL);

- (NSDictionary *)constantsToExport
{
  return @{
    @"ScaleNone": AVLayerVideoGravityResizeAspect,
    @"ScaleToFill": AVLayerVideoGravityResize,
    @"ScaleAspectFit": AVLayerVideoGravityResizeAspect,
    @"ScaleAspectFill": AVLayerVideoGravityResizeAspectFill
  };
}

@end
