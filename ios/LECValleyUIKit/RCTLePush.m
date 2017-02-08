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
#import <CommonCrypto/CommonDigest.h>


// 定义PUSH状态常量
#define PUSH_STATE_CLOSED        0
#define PUSH_STATE_CONNECTING    1
#define PUSH_STATE_CONNECTED     2
#define PUSH_STATE_OPENED        3
#define PUSH_STATE_DISCONNECTING 4
#define PUSH_STATE_ERROR         5
#define PUSH_STATE_WARNING       6

@interface RCTLePush () <LCStreamingManagerDelegate>
{
    __block BOOL _lePushValid; //当前初始化状态
    __block BOOL _isBack; //后台标志,在进入后台之前正在推流设置为true。判断是否在后台回来时继续推流
}

@property (nonatomic, strong) LCStreamingManager *manager;

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
    
    NSDictionary *_pushPara; //推流参数
    NSString *_pushUrl; //推流地址
    NSString *_playUrl; //播放地址
    
    int  _pushTime; //推流计时
    BOOL _pushFlag; //推流计时是否开始
    
    int  _pushState;  //PUSH操作状态：0：closed，1：opening，2：opened，3:closing, 4:error
    
    BOOL _flashFlag;  //是否打开闪光灯
    BOOL _switchFlag; //是否正在切换摄像
    int  _filterModel; //当前滤镜选择
    int  _volume; //音量设置
    
    BOOL _fullscreenPresented; //是否全屏
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
- (RCTLePushViewController*)createPushViewController:(LCStreamingManager*) manager {
    //    self.frame                       = LCRect_PlayerFullFrame;
    RCTLePushViewController* pushController= [[RCTLePushViewController alloc] init];
    pushController.viewControllerDelegate  = self; //实现协议
    pushController.view                    = manager.videoView;
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
    if(bundle == nil) return;
    
    NSLog(@"外部控制——— 推流参数: %@", bundle);
    
    //重置播放器
    [self resetViewAndController];
    
    //重置所有状态
    [self initFieldParaStates];
    
    //根据必要参数创建推流端
    [self pushItemForTarget:bundle];
    
}


/*重置播放器*/
- (void) resetViewAndController
{
    if (_lePushValid) {
        
        UIView *subview = [self viewWithTag:PushViewTag];
        subview?[subview removeFromSuperview]:nil;
        
        _manager.delegate = nil;
        
        if(_pushType == PUSH_TYPE_MOBILE_URI){
            [_manager cleanSession];
        }
        
        _pushViewController?_pushViewController = nil:nil;
    }
}

/*重置状态量*/
- (void) initFieldParaStates
{
    _lePushValid = NO;
    
    _pushTime = 0;
    _pushFlag = NO;
    _pushState = PUSH_STATE_CLOSED;
    
    _flashFlag = NO;
    _switchFlag = NO;
    _filterModel = LCVideoFilterNone;
    _volume = 1;
    
}


/* 将Dictionary转为Json */
+ (NSString *)returnJSONStringWithDictionary:(NSDictionary *)dictionary useSystem:(BOOL)system{
    if(system){
        //系统
        NSError * error;
        NSData * jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:kNilOptions error:&error];
        if(error != nil){
            NSLog(@"转换JSON出错:%@", error);
            return nil;
        }
        return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }else{
        //自定义
        NSString *jsonStr = @"{";
        NSArray * keys = [dictionary allKeys];
        for (NSString * key in keys) {
            jsonStr = [NSString stringWithFormat:@"%@\"%@\":\"%@\",",jsonStr,key,[dictionary objectForKey:key]];
        }
        jsonStr = [NSString stringWithFormat:@"%@%@",[jsonStr substringWithRange:NSMakeRange(0, jsonStr.length-1)],@"}"];
        return jsonStr;
    }
}


/*判断空字串*/
+ (BOOL)isBlankString:(NSString *)string{
    if (string == nil || string == NULL) {
        return YES;
    }
    if ([string isKindOfClass:[NSNull class]]) {
        return YES;
    }
    if ([[string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length]==0) {
        return YES;
    }
    return NO;
}

/* 根据数据源包初始化推流目标 */
- (void)pushItemForTarget:(NSDictionary *)bundle
{
    _pushPara = bundle;

    int playMode = [RCTConvert int:[bundle objectForKey:@"type"]];
    BOOL isLandscape = [RCTConvert BOOL:[bundle objectForKey:@"landscape"]];
    
    switch (playMode) {
        case PUSH_TYPE_MOBILE_URI: //移动直播有地址
            
            if (!_manager) {
                _manager = [[LCStreamingManager alloc] init];
                _manager.delegate = self;
                //        _manager = [LCStreamingManager sharedManager];
            }
            
            //配置推流正方
            _manager.pushOrientation = isLandscape? UIInterfaceOrientationLandscapeRight: UIInterfaceOrientationPortrait;
            
            CGSize size = UIInterfaceOrientationLandscapeRight == _manager.pushOrientation ?
            CGSizeMake([UIScreen mainScreen].bounds.size.height,[UIScreen mainScreen].bounds.size.width) :
            CGSizeMake([UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height);
            
            //配置推流参数
            [_manager configVCSessionWithVideoSize:size
                                         frameRate:24
                                           bitrate:1000000
                           useInterfaceOrientation:YES];
            
            //配置预览视图的frame
            [_manager configVideoViewFrame:[UIScreen mainScreen].bounds];
            [_manager enableManulFocus:YES];
            
            [self usePushViewController:_manager]; // 创建controller
            
            _playUrl;
            _pushUrl;
            break;
            
        case PUSH_TYPE_MOBILE: //移动直播无地址
            _manager = [LCStreamingManager sharedManager];
            
            _playUrl;
            _pushUrl;
            break;
            
        case PUSH_TYPE_LECLOUD: //云直播
            
            break;
            
        default:
            break;
    }
    
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


- (void)usePushViewController:(LCStreamingManager*) manager
{
    if( manager ){
        _pushViewController = [self createPushViewController: manager];
        
        // to prevent video from being animated when resizeMode is 'cover'
        // resize mode must be set before subview is added
        //    [self addSubview:_playerViewController.view];
    }
}


#pragma mark - LCStreamingManagerDelegate

//推流状态变化通知
- (void)connectionStatusChanged:(LCStreamingSessionState)sessionState
{
    switch (sessionState) {
        case LCStreamingSessionStateStarted:
            [_manager setGain:1.3];
//            [self.btnPush setTitle:@"STOP" forState:UIControlStateNormal];
            break;
        case LCStreamingSessionStateStarting:
//            [self.btnPush setTitle:@"STARTING" forState:UIControlStateNormal];
            break;
        case LCStreamingSessionStatePreviewStarted:
        case LCStreamingSessionStateNone:
//            [self.btnPush setTitle:@"PUSH" forState:UIControlStateNormal];
            break;
        case LCStreamingSessionStateEnded:
//            [self.btnPush setTitle:@"PUSH" forState:UIControlStateNormal];
            break;
        default:
//            [self.btnPush setTitle:@"ERROR" forState:UIControlStateNormal];
            break;
    }
//    [self.btnPush.titleLabel sizeToFit];
}

//推流管理器状态通知，主要用于错误信息的通知
- (void)notifyManagerStatus:(LCStreamingManagerStatus)managerStatus withMessage:(NSString *)msg
{
    NSLog(@"错误提示：%@", msg);
    
}

//视频帧处理回调，参数为原始视频帧，需返回处理后的视频帧
//- (CVPixelBufferRef)newPixelBufferFromPixelBuffer:(const CVPixelBufferRef)pixelBuffer
//{
//    
//}

#pragma mark - RCTLePushViewControllerDelegate

- (void)pushViewControllerWillDismiss:(UIViewController *)viewController
{
    NSLog(@"previewViewControllerWillDismiss消息");
    
    if (_pushViewController == viewController && _fullscreenPresented){
        //    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerWillDismiss" body:@{@"target": self.reactTag}];
    }
}

- (void)pushViewControllerDidDismiss:(UIViewController *)viewController
{
    NSLog(@"previewViewControllerDidDismiss消息");
    
    if (_pushViewController == viewController && _fullscreenPresented){
        _fullscreenPresented = NO;
        //    _presentingViewController = nil;
        //    [self applyModifiers];
        //    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerDidDismiss" body:@{@"target": self.reactTag}];
    }
}

/*转屏处理逻辑*/
-(void)pushViewShouldRotateToOrientation:(UIViewController *)viewController{
    
    UIDeviceOrientation orientation = (UIDeviceOrientation)[UIApplication sharedApplication].statusBarOrientation;
    if (orientation == UIDeviceOrientationPortrait ||orientation == UIDeviceOrientationPortraitUpsideDown) {
        // 竖屏
        _fullscreenPresented = NO;
        //self.frame = LCRect_PlayerFullFrame;
        
    }else {
        // 横屏
        CGFloat width = [UIScreen mainScreen].bounds.size.width;
        CGFloat height = [UIScreen mainScreen].bounds.size.height;
        
        if (width < height){
            CGFloat tmp = width;
            width = height;
            height = tmp;
        }
        _fullscreenPresented = YES;
        self.frame = CGRectMake(0, 0, width, height);
        
    }
}

#pragma mark - util methods
/*
 推流url规则：
 rtmp://推流域名/live/流名称?tm=yyyyMMddHHmmss&sign=xxx
 播放sign规则 ：
 sign参数=MD5(流名称+ tm参数 + 安全)
 其中流名称可以是任意数字、字母的组合
 示例rtmp://400438.mpush.live.lecloud.com/live/mytest1?tm=20160406154640&sign=c445f98bed147e4463185efa4a639978
 
 播放url规则：
 rtmp://播放域名/live/流名称?tm=yyyyMMddHHmmss&sign=xxx
 播放sign规则 ：
 sign参数=MD5(流名称+ tm参数 + 安全码 + “lecloud”)
 其中流名称可以是任意数字、字母的组合
 示例：rtmp://400438.mpull.live.lecloud.com/live/mytest1?tm=20160406154640&sign=7922d30aefbe2740c55bc6b032736208
 */
- (NSString *)rtmpAddressWithDomain:(NSString *)domain streamName:(NSString *)stream appKey:(NSString *)appKey {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyyMMddHHmmss"];
    NSString *currentDateStr = [dateFormatter stringFromDate:[NSDate date]];
    
    NSString *sign = [self md5:[NSString stringWithFormat:@"%@%@%@", stream, currentDateStr, appKey]];
    NSString *ret = [NSString stringWithFormat:@"rtmp://%@/live/%@?&tm=%@&sign=%@", domain, stream, currentDateStr, sign];
    
    return ret;
}

- (NSString *)md5:(NSString *)str {
    const char *cStr = [str UTF8String];
    unsigned char result[16];
    CC_MD5(cStr, strlen(cStr), result); // This is the md5 call
    return [NSString stringWithFormat:
            @"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            result[0], result[1], result[2], result[3],
            result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11],
            result[12], result[13], result[14], result[15]
            ];
}

#pragma mark - React View Management

- (void)insertReactSubview:(UIView *)view atIndex:(NSInteger)atIndex
{
    NSLog(@"insertReactSubview消息");
    [super insertReactSubview:view atIndex:atIndex];
    view.frame = self.bounds;
    //[_pushViewController.contentOverlayView insertSubview:view atIndex:atIndex];
}

- (void)removeReactSubview:(UIView *)subview
{
    NSLog(@"removeReactSubview消息");
    
    [subview removeFromSuperview];
    [super removeReactSubview:subview];
}

#pragma mark - View lifecycle

- (void)layoutSubviews
{
    NSLog(@"layoutSubviews消息");
    
    [super layoutSubviews];
//    _pushViewController.view.frame = self.bounds;
    
    // also adjust all subviews of contentOverlayView
    //      for (UIView* subview in _playerViewController.contentOverlayView.subviews) {
    //        subview.frame = self.bounds;
    //      }
}

- (void)removeFromSuperview
{
    NSLog(@"removeFromSuperview消息");
    
    [self resetViewAndController];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    //  [[UIDevice currentDevice]endGeneratingDeviceOrientationNotifications];
    
    [super removeFromSuperview];
}


#pragma mark - 通知处理

- (void)applicationWillResignActive:(NSNotification *)notification
{
//    if (_paused) return;
//    [self pause];
}

- (void)applicationDidEnterBackground:(NSNotification *)notification
{
}

- (void)applicationWillEnterForeground:(NSNotification *)notification
{
    //  [self applyModifiers];
//    [self resume];
}

@end
