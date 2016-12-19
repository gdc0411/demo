//
//  BrightnessModule.m
//  LECValleyUIKit
//
//  Created by LizaRao on 2016/12/19.
//  Copyright © 2016年 Facebook. All rights reserved.
//

#import "BrightnessModule.h"

#import "BrightnessView.h"
#import "LECValley.h"

#import <UIKit/UIKit.h>

@interface BrightnessModule()

@property (nonatomic, strong) BrightnessView   *brightnessView;  //亮度View

@end

@implementation BrightnessModule

- (instancetype)init
{
    if ((self = [super init])) {
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(needUpdateBrightnessView:)
                                                     name:@"UIDeviceOrientationDidChangeNotification"
                                                   object:nil];
        
        [self.brightnessView removeFromSuperview];
        [[UIApplication sharedApplication].keyWindow addSubview:self.brightnessView];
        
    }
    return self;
    
}

- (void)dealloc
{
    [self.brightnessView removeFromSuperview];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (BrightnessView *)brightnessView
{
    if (!_brightnessView) {
        _brightnessView = [BrightnessView sharedBrightnessView];
    }
    return _brightnessView;
}


- (void)needUpdateBrightnessView:(NSNotification *)notification
{
    // 获取到当前状态条的方向
    UIInterfaceOrientation currentOrientation = [UIApplication sharedApplication].statusBarOrientation;
    if(self.brightnessView.orientation == currentOrientation) return;
    
    if (currentOrientation == UIInterfaceOrientationPortrait) {
        [self.brightnessView removeFromSuperview];
        self.brightnessView.frame = CGRectMake((ScreenWidth-155)/2, (ScreenHeight-155)/2, 155, 155);
        [[UIApplication sharedApplication].keyWindow addSubview:self.brightnessView];
        self.brightnessView.orientation = currentOrientation;
        
    } else {
        [self.brightnessView removeFromSuperview];
        self.brightnessView.frame = CGRectMake(ScreenWidth/2, ScreenHeight/2, 155, 155);
        [[UIApplication sharedApplication].keyWindow addSubview:self.brightnessView];
        self.brightnessView.orientation = currentOrientation;
    }
    
}


+ (float) getBrightnessValue
{
    return [UIScreen mainScreen].brightness;
}

+ (void) setBrightnessValue:(float)brightness
{
    [[UIScreen mainScreen] setBrightness: brightness];
}



//// 状态条变化通知（在前台播放才去处理）
//- (void)onStatusBarOrientationChange
//{
//    // 获取到当前状态条的方向
//    UIInterfaceOrientation currentOrientation = [UIApplication sharedApplication].statusBarOrientation;
//    if (currentOrientation == UIInterfaceOrientationPortrait) {
//        [self.brightnessView removeFromSuperview];
//        [[UIApplication sharedApplication].keyWindow addSubview:self.brightnessView];
//        [self.brightnessView mas_remakeConstraints:^(MASConstraintMaker *make) {
//            make.width.height.mas_equalTo(155);
//            make.leading.mas_equalTo((ScreenWidth-155)/2);
//            make.top.mas_equalTo((ScreenHeight-155)/2);
//        }];
//
//    } else {
//        if (currentOrientation == UIInterfaceOrientationLandscapeRight) {
//            [self toOrientation:UIInterfaceOrientationLandscapeRight];
//        } else if (currentOrientation == UIDeviceOrientationLandscapeLeft){
//            [self toOrientation:UIInterfaceOrientationLandscapeLeft];
//        }
//        [self.brightnessView removeFromSuperview];
//        [self addSubview:self.brightnessView];
//        [self.brightnessView mas_remakeConstraints:^(MASConstraintMaker *make) {
//            make.center.mas_equalTo(self);
//            make.width.height.mas_equalTo(155);
//        }];
//
//    }
//
//}

@end
