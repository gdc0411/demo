//
//  RCTLePushManager.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/6.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import "RCTLePushManager.h"
#import "RCTLePush.h"

@implementation RCTLePushManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view
{
    return [[RCTLePush alloc] initWithBridge:self.bridge];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(para, NSDictionary); // 推流目标
RCT_EXPORT_VIEW_PROPERTY(push, BOOL);  // 推流打开/关闭
RCT_EXPORT_VIEW_PROPERTY(camera, int);  // 摄像头切换
RCT_EXPORT_VIEW_PROPERTY(flash, BOOL);  // 闪光灯打开/关闭

RCT_EXPORT_VIEW_PROPERTY(onPushTargetLoad, RCTDirectEventBlock);  // 传入推流设置参数
RCT_EXPORT_VIEW_PROPERTY(onPushStateUpdate, RCTDirectEventBlock);  // 推流操作状态
RCT_EXPORT_VIEW_PROPERTY(onPushTimeUpdate, RCTDirectEventBlock);  // 推流时间更新
RCT_EXPORT_VIEW_PROPERTY(onPushCameraUpdate, RCTDirectEventBlock);  // 切换摄像头操作回调
RCT_EXPORT_VIEW_PROPERTY(onPushFlashUpdate, RCTDirectEventBlock);  // 闪光灯操作回调

- (NSDictionary *)constantsToExport
{
    return @{
             @"PUSH_TYPE_NONE"      : @(PUSH_TYPE_NONE),
             @"PUSH_TYPE_MOBILE_URI": @(PUSH_TYPE_MOBILE_URI),
             @"PUSH_TYPE_MOBILE"    : @(PUSH_TYPE_MOBILE),
             @"PUSH_TYPE_LECLOUD"   : @(PUSH_TYPE_LECLOUD)
             };
}

@end
