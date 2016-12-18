//
//  RCTLeSubVideoManager.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/18.
//  Copyright © 2016年 leCloud. All rights reserved.
//

#import "RCTLeSubVideoManager.h"
#import "RCTLeSubVideo.h"

#import <AVFoundation/AVFoundation.h>

@implementation RCLLeSubVideoManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view
{
    return [[RCTLeSubVideo alloc] initWithBridge:self.bridge];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(src, NSDictionary); // 数据源

RCT_EXPORT_VIEW_PROPERTY(onSubVideoSourceLoad, RCTDirectEventBlock);  // 数据源事件
RCT_EXPORT_VIEW_PROPERTY(onSubVideoSizeChange, RCTDirectEventBlock);  // 视频真实宽高
RCT_EXPORT_VIEW_PROPERTY(onSubVideoRateLoad, RCTDirectEventBlock); // 视频码率列表
RCT_EXPORT_VIEW_PROPERTY(onSubVideoLoad, RCTDirectEventBlock); // 播放器准备完毕
RCT_EXPORT_VIEW_PROPERTY(onSubVideoError, RCTDirectEventBlock);  // 播放出错
RCT_EXPORT_VIEW_PROPERTY(onSubVideoPause, RCTDirectEventBlock); // 播放暂停
RCT_EXPORT_VIEW_PROPERTY(onSubVideoResume, RCTDirectEventBlock); // 播放继续
RCT_EXPORT_VIEW_PROPERTY(onSubBufferStart, RCTDirectEventBlock); // 开始缓冲
RCT_EXPORT_VIEW_PROPERTY(onSubBufferEnd, RCTDirectEventBlock);  // 缓冲结束
RCT_EXPORT_VIEW_PROPERTY(onSubVideoRendingStart, RCTDirectEventBlock); // 加载第一帧



@end
