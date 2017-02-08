//
//  RCTLePush.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/6.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import "RCTLePush.h"
#import "LECValley.h"

#import <React/RCTConvert.h>
#import <React/UIView+React.h>

#import "RCTLePushViewController.h"
#import <LCStreamingManager.h>

@interface RCTLePush ()
{

}

@property (nonatomic, copy) RCTDirectEventBlock onPushTargetLoad;  // 传入推流设置参数
@property (nonatomic, copy) RCTDirectEventBlock onPushStateUpdate;  // 推流操作状态
@property (nonatomic, copy) RCTDirectEventBlock onPushTimeUpdate; // 推流时间更新
@property (nonatomic, copy) RCTDirectEventBlock onPushCameraUpdate; // 切换摄像头操作回调
@property (nonatomic, copy) RCTDirectEventBlock onPushFlashUpdate;  // 闪光灯操作回调
@property (nonatomic, copy) RCTDirectEventBlock onPushFilterUpdate; // 滤镜操作回调
@property (nonatomic, copy) RCTDirectEventBlock onPushVolumeUpdate;  // 音量操作回调

@end

@implementation RCTLePush
{
    __weak RCTBridge *_bridge;
    
    RCTLePushViewController *_pushViewController;

    int _pushType; //当前推流类型
    int _currentOritentation; //当前屏幕方向

}

#pragma mark 初始化
/*实例化桥*/
- (instancetype)initWithBridge:(RCTBridge *)bridge
{
    NSLog(@"初始化桥……");
    if ((self = [super init])) {
        _bridge = bridge;
        
        _currentOritentation = 1;
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(applicationWillResignActive:)
                                                     name:UIApplicationWillResignActiveNotification
                                                   object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(applicationDidEnterBackground:)
                                                     name:UIApplicationDidEnterBackgroundNotification
                                                   object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(applicationWillEnterForeground:)
                                                     name:UIApplicationWillEnterForegroundNotification
                                                   object:nil];
        
    }
    return self;
}


#pragma mark 销毁
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

RCT_NOT_IMPLEMENTED(- (instancetype)init)


#pragma mark 创建viewController
- (RCTLePushViewController*)createPlayerViewController {
    //    self.frame                       = LCRect_PlayerFullFrame;
    RCTLePushViewController* pushController= [[RCTLePushViewController alloc] init];
    pushController.viewControllerDelegate  = self; //实现协议
    pushController.view                    = self;//player.videoView;
    pushController.view.tag                = PushViewTag;
    pushController.view.frame              = self.bounds; //CGRectMake(0, 0, 0, 0);
    //    playerController.view.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleWidth;
    pushController.view.contentMode      = UIViewContentModeScaleAspectFit;
    
    [self addSubview:pushController.view];
    [self sendSubviewToBack:pushController.view];
    
    return pushController;
}

#pragma mark - 设置属性
- (void)setPara:(NSDictionary *)bundle
{
    NSLog(@"外部控制——— 推流参数: %@", bundle);
    if(bundle == nil)
        return;
    
//    //重置播放器
//    [self resetPlayerAndController];
//    
//    //重置所有状态
//    [self initFieldParaStates];
//    
//    //根据必要参数创建播放器
//    [self playerItemForSource:source];
    
}


- (void)setPush:(BOOL)push
{
    NSLog(@"外部控制——— 开始/停止推流: %@", push?@"YES":@"NO");
}


- (void)setCamera:(int)times
{
    NSLog(@"外部控制——— 切换摄像头方向");
}


- (void)setFlash:(BOOL)flash
{
    NSLog(@"外部控制——— 开始/关闭闪光灯:", flash?@"YES":@"NO");
}

- (void)setFilter:(int)filter
{
    NSLog(@"外部控制——— 设置滤镜: %d", filter);
}


- (void)setVolume:(int)volume
{
    NSLog(@"外部控制——— 设置音量: %d", volume);
}



@end
