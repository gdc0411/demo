//
//  BrightnessView.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/18.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BrightnessView : UIView

/** 调用单例记录播放状态是否锁定屏幕方向*/
@property (nonatomic, assign) BOOL     isLockScreen;
/** 是否允许横屏,来控制只有竖屏的状态*/
@property (nonatomic, assign) BOOL     isAllowLandscape;

+ (instancetype)sharedBrightnessView;

@end
