//
//  BVLinearGradientManager.m
//  DemoProject
//
//  Created by RaoJia on 2016/12/7.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "RCTLinearGradientManager.h"
#import "RCTLinearGradient.h"
#import "RCTBridge.h"

@implementation RCTLinearGradientManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view
{
  return [[RCTLinearGradient alloc] init];
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(colors, NSArray);
RCT_EXPORT_VIEW_PROPERTY(start, CGPoint);
RCT_EXPORT_VIEW_PROPERTY(end, CGPoint);
RCT_EXPORT_VIEW_PROPERTY(locations, NSArray);

@end
