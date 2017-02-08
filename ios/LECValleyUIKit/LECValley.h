//
//  LECValley.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/18.
//  Copyright © 2016年 RaoJia. All rights reserved.
//

#ifndef LECValley_h
#define LECValley_h

#define iPhone4s ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(640, 960), [[UIScreen mainScreen] currentMode].size) : NO)

#define REACT_JS_EVENT_WAIT               1

// 监听TableView的contentOffset
#define kPlayerViewContentOffset          @"contentOffset"
// player的单例
#define PlayerShared                      [BrightnessView sharedBrightnessView]
// 屏幕的宽
#define ScreenWidth                       [[UIScreen mainScreen] bounds].size.width
// 屏幕的高
#define ScreenHeight                      [[UIScreen mainScreen] bounds].size.height
// 颜色值RGB
#define RGBA(r,g,b,a)                     [UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:a]
// 图片路径
#define PlayerSrcName(file)               [@"LECValleyUIResource.bundle" stringByAppendingPathComponent:file]

#define PlayerFrameworkSrcName(file)      [@"Frameworks/Player.framework/LECValleyUIResource.bundle" stringByAppendingPathComponent:file]

#define PlayerImage(file)                 [UIImage imageNamed:PlayerSrcName(file)] ? :[UIImage imageNamed:PlayerFrameworkSrcName(file)]


#define LCRect_PlayerFullFrame            CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height)

#define PlayerViewTag                     8888
#define SubPlayerViewTag                  8866

#define PushViewTag                       6666

#endif /* LECValley_h */
