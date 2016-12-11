//
//  RCTLeVideoManager.m
//  RCTLeVideo
//
//  Created by RaoJia on 28.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import "RCTLeVideoManager.h"
#import "RCTLeVideo.h"

#import <AVFoundation/AVFoundation.h>

@implementation RCTLeVideoManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view
{
  return [[RCTLeVideo alloc] initWithBridge:self.bridge];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(src, NSDictionary); // 数据源
RCT_EXPORT_VIEW_PROPERTY(paused, BOOL);  // 开始或暂停
RCT_EXPORT_VIEW_PROPERTY(seek, int);  // seek位置
RCT_EXPORT_VIEW_PROPERTY(rate, NSString*);  // 切换码率
RCT_EXPORT_VIEW_PROPERTY(live, NSString*);  // 切换机位
RCT_EXPORT_VIEW_PROPERTY(volume, int);  // 音量调节
//RCT_EXPORT_VIEW_PROPERTY(track, float);  // 左右声道
RCT_EXPORT_VIEW_PROPERTY(clickAd, BOOL);  // 点击广告
RCT_EXPORT_VIEW_PROPERTY(brightness, int);  // 屏幕亮度调节
RCT_EXPORT_VIEW_PROPERTY(orientation, int);  // 屏幕方向
RCT_EXPORT_VIEW_PROPERTY(playInBackground, BOOL);  // 是否后台播放

RCT_EXPORT_VIEW_PROPERTY(onVideoSourceLoad, RCTDirectEventBlock);  // 数据源事件
RCT_EXPORT_VIEW_PROPERTY(onVideoSizeChange, RCTDirectEventBlock);  // 视频真实宽高
RCT_EXPORT_VIEW_PROPERTY(onVideoRateLoad, RCTDirectEventBlock); // 视频码率列表
RCT_EXPORT_VIEW_PROPERTY(onVideoLoad, RCTDirectEventBlock); // 播放器准备完毕
RCT_EXPORT_VIEW_PROPERTY(onVideoError, RCTDirectEventBlock);  // 播放出错
RCT_EXPORT_VIEW_PROPERTY(onVideoProgress, RCTDirectEventBlock); // 正在播放视频
RCT_EXPORT_VIEW_PROPERTY(onVideoBufferPercent, RCTDirectEventBlock);  // 缓存进度
RCT_EXPORT_VIEW_PROPERTY(onVideoPause, RCTDirectEventBlock); // 播放暂停
RCT_EXPORT_VIEW_PROPERTY(onVideoResume, RCTDirectEventBlock); // 播放继续
RCT_EXPORT_VIEW_PROPERTY(onVideoSeek, RCTDirectEventBlock); // 播放跳转中
RCT_EXPORT_VIEW_PROPERTY(onVideoSeekComplete, RCTDirectEventBlock); // 播放跳转结束
RCT_EXPORT_VIEW_PROPERTY(onVideoRateChange, RCTDirectEventBlock); //视频码率切换
RCT_EXPORT_VIEW_PROPERTY(onVideoEnd, RCTDirectEventBlock);  // 播放完毕
RCT_EXPORT_VIEW_PROPERTY(onBufferStart, RCTDirectEventBlock); // 开始缓冲
RCT_EXPORT_VIEW_PROPERTY(onBufferEnd, RCTDirectEventBlock);  // 缓冲结束
RCT_EXPORT_VIEW_PROPERTY(onVideoRendingStart, RCTDirectEventBlock); // 加载第一帧
RCT_EXPORT_VIEW_PROPERTY(onBufferPercent, RCTDirectEventBlock); // 缓冲加载进度，转圈
RCT_EXPORT_VIEW_PROPERTY(onAdvertStart, RCTDirectEventBlock); // 广告开始
RCT_EXPORT_VIEW_PROPERTY(onAdvertProgress, RCTDirectEventBlock);  // 广告播放中
RCT_EXPORT_VIEW_PROPERTY(onAdvertComplete, RCTDirectEventBlock); // 广告结束
RCT_EXPORT_VIEW_PROPERTY(onAdvertClick, RCTDirectEventBlock); // 广告点击
RCT_EXPORT_VIEW_PROPERTY(onAdvertError, RCTDirectEventBlock); // 广告出错
RCT_EXPORT_VIEW_PROPERTY(onMediaVodLoad, RCTDirectEventBlock); // 获得点播媒资
RCT_EXPORT_VIEW_PROPERTY(onMediaLiveLoad, RCTDirectEventBlock); // 获得直播媒资
RCT_EXPORT_VIEW_PROPERTY(onMediaActionLoad, RCTDirectEventBlock); // 获得活动直播媒资
RCT_EXPORT_VIEW_PROPERTY(onMediaPlayURLLoad, RCTDirectEventBlock); // 获得媒资调度
RCT_EXPORT_VIEW_PROPERTY(onActionLiveChange, RCTDirectEventBlock); // 云直播切换机位
RCT_EXPORT_VIEW_PROPERTY(onActionTimeShift, RCTDirectEventBlock); // 云直播进度
RCT_EXPORT_VIEW_PROPERTY(onActionStatusChange, RCTDirectEventBlock); // 云直播状态回调
RCT_EXPORT_VIEW_PROPERTY(onActionOnlineNumChange, RCTDirectEventBlock); // 云直播在线人数变化
RCT_EXPORT_VIEW_PROPERTY(onOrientationChange, RCTDirectEventBlock); //屏幕方向切换
RCT_EXPORT_VIEW_PROPERTY(onOtherEventInfo, RCTDirectEventBlock); // 其他事件


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
