//
//  BrightnessView.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/18.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BrightnessView : UIView

/** 当前方向*/
@property (nonatomic, assign) UIInterfaceOrientation   orientation;


+ (instancetype)sharedBrightnessView;

@end
