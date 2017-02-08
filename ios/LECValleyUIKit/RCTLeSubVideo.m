//
//  RCTLeSubVideo.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/18.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "RCTLeSubVideo.h"
#import "LECValley.h"

#import <React/RCTConvert.h>
#import <React/UIView+React.h>


#import "LECActivityPlayer.h"
#import "LECActivityInfoManager.h"
#import "LECActivityLiveItem.h"
#import "LECPlayerOption.h"
#import "LCBaseViewController.h"

#import "RCTLeVideoPlayerViewController.h"

#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVFoundation.h>

//#define LCRect_PlayerHalfFrame    CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height);

@interface RCTLeSubVideo ()<LECPlayerDelegate, LCActivityManagerDelegate>
{
    __block BOOL _isPlaying;
}

@property (nonatomic, strong) LECPlayer *lePlayer;
@property (nonatomic, strong) LECPlayerOption *option;


@property (nonatomic, copy) RCTDirectEventBlock onSubVideoSourceLoad;  // 数据源设置
@property (nonatomic, copy) RCTDirectEventBlock onSubVideoSizeChange;  // 视频真实宽高
@property (nonatomic, copy) RCTDirectEventBlock onSubVideoLoad; // 播放器准备完毕
@property (nonatomic, copy) RCTDirectEventBlock onSubVideoError;  // 播放出错
@property (nonatomic, copy) RCTDirectEventBlock onSubVideoPause; // 播放暂停
@property (nonatomic, copy) RCTDirectEventBlock onSubVideoResume; // 播放继续
@property (nonatomic, copy) RCTDirectEventBlock onSubBufferStart; // 开始缓冲
@property (nonatomic, copy) RCTDirectEventBlock onSubBufferEnd;  // 缓冲结束
@property (nonatomic, copy) RCTDirectEventBlock onSubVideoRendingStart; // 加载第一帧

@end

@implementation RCTLeSubVideo
{
    /* Required to connect JS */
    __weak RCTBridge *_bridge;
    
    LCBaseViewController *_playerViewController;
    NSURL *_videoURL;
    
    int _width; //视频宽度
    int _height; //视频高度
    
    BOOL _paused;
}

#pragma mark 初始化
/*实例化桥*/
- (instancetype)initWithBridge:(RCTBridge *)bridge
{
    NSLog(@"初始化桥……");
    if ((self = [super init])) {
        _bridge = bridge;
        
        _isPlaying = NO;
        
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


- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

RCT_NOT_IMPLEMENTED(- (instancetype)init)


/*创建viewController*/
- (LCBaseViewController*)createPlayerViewController:(LECPlayer*)player {
    //    self.frame                             = LCRect_PlayerFullFrame;
    RCTLeVideoPlayerViewController* playerController= [[RCTLeVideoPlayerViewController alloc] init];
    
    playerController.rctDelegate           = self; //实现协议
    playerController.view                  = player.videoView;
    playerController.view.tag              = SubPlayerViewTag;
    playerController.view.frame            = self.bounds; //CGRectMake(0, 0, 0, 0);
    //    playerController.view.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleWidth;
    playerController.view.contentMode      = UIViewContentModeScaleAspectFit;
    
    [self addSubview:playerController.view];
    [self sendSubviewToBack:playerController.view];
    
    return playerController;
}


#pragma mark - 设置属性
- (void)setSrc:(NSDictionary *)source
{
    NSLog(@"外部控制——— 传入数据源: %@", source);
    if(source == nil)
        return;
    
    //重置播放器
    [self resetPlayerAndController];
    
    //重置所有状态
    [self initFieldParaStates];
    
    //根据必要参数创建播放器
    [self playerItemForSource:source];
    
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


/* 根据数据源包创建播放 */
- (void)playerItemForSource:(NSDictionary *)source
{
    NSLog(@"机位播放数据源");
    
    NSString *liveId    = [source objectForKey:@"liveId"];
    NSString *streamId  = [source objectForKey:@"streamId"];
    NSString *streamUrl = [source objectForKey:@"streamUrl"];
    bool usehls         = [RCTConvert BOOL:[source objectForKey:@"usehls"]];
    
    if (streamUrl.length != 0 ) {        
        _lePlayer          = [[LECPlayer alloc] init];
        _lePlayer.delegate = self;
        
        [self usePlayerViewController:_lePlayer]; // 创建controller
        _playerViewController.url = streamUrl;
        
        __weak typeof(self) wSelf = self;
        [_lePlayer registerWithURLString:streamUrl completion:^(BOOL result) {
            //数据源回显
            wSelf.onSubVideoSourceLoad?wSelf.onSubVideoSourceLoad(@{@"src": [[wSelf class] returnJSONStringWithDictionary:source useSystem:YES]}):nil;
            
            if (result){
                NSLog(@"播放器注册成功");
                [wSelf play];//注册完成后自动播放
                [wSelf.lePlayer setVolume:0];
                
            }else{
                wSelf.onSubVideoError? wSelf.onSubVideoError(@{@"errorCode":@"-1",@"errorMsg":@"播放器注册失败,请检查URL"}):nil;
            }
        }];
        
//        [(LECActivityPlayer*)_lePlayer registerWithLiveId: liveId
//                                              isLetvMedia:YES
//                                                mediaType:LECPlayerMediaTypeRTMP
//                                                  options:nil
//                                               completion:^(BOOL result) {
//                                                   
//                                                   //数据源回显
//                                                   wSelf.onSubVideoSourceLoad?
//                                                   wSelf.onSubVideoSourceLoad(@{@"src": [[wSelf class] returnJSONStringWithDictionary:source useSystem:YES]}):nil;
//                                                   
//                                                   if (result){
//                                                       NSLog(@"播放器注册成功");
//                                                       [wSelf play];//注册完成后自动播放
//                                                       [wSelf.lePlayer setVolume:0];
//                                                       
//                                                   }else{
//                                                       //[_playerViewController showTips:@"播放器注册失败,请检查UU和VU"];
//                                                       wSelf.onSubVideoError?wSelf.onSubVideoError(@{@"errorCode":@"-1",@"errorMsg":@"播放器注册失败,请检查LiveId"}):nil;
//                                                   }
//                                               }];
        
    }else{
        NSLog(@"直播活动注册失败");
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        dispatch_async(queue, ^{
            sleep(REACT_JS_EVENT_WAIT);
            self.onSubVideoError? self.onSubVideoError(@{@"errorCode":@"-1",@"errorMsg":@"播放器注册失败,请检查StreamURL"}):nil;
        });
    }
    
}

- (BOOL)shouldAutorotate
{
    return YES;
}


/*重置播放器*/
- (void) resetPlayerAndController
{
    if (_lePlayer) {
        [self stop];
        
        UIView *subview = [self viewWithTag:SubPlayerViewTag];
        subview?[subview removeFromSuperview]:nil;
        
        [_lePlayer unregister];
        
        _lePlayer.delegate = nil;
        _lePlayer = nil;
        
        _option = nil;
        
        _playerViewController?_playerViewController = nil:nil;
        
    }
}


/*重置状态量*/
- (void) initFieldParaStates
{
    _width = 0;
    _height = 0;
}


#pragma mark - 播放控制
- (void)play
{
    if (_isPlaying || _lePlayer == nil ) {
        return;
    }
    __weak typeof(self) wSelf = self;
    [_lePlayer playWithCompletion:^{
        wSelf.onSubVideoResume?wSelf.onSubVideoResume(nil):nil;
        
        _paused = NO;
        _isPlaying = YES;
    }];
}

- (void)resume
{
    if (_isPlaying || _lePlayer == nil) {
        return;
    }
    [_lePlayer resume];
    _onSubVideoResume?_onSubVideoResume(nil):nil;
    
    _paused = NO;
    _isPlaying = YES;
}

- (void)stop
{
    if (!_isPlaying || _lePlayer == nil){
        return;
    }
    __weak typeof(self) wSelf = self;
    [_lePlayer stopWithCompletion:^{ _isPlaying = NO; }];
}

- (void)pause
{
    if (!_isPlaying || _lePlayer == nil){
        return;
    }
    [_lePlayer pause];
    
    _onSubVideoPause?_onSubVideoPause(nil):nil;
    
    _paused = YES;
    _isPlaying = NO;
}

#pragma mark - 事件处理
- (void) processPrepared:(LECPlayer *) player
             playerEvent:(LECPlayerPlayEvent) playerEvent
{
    NSLog(@"Prepared Event!");
    
    //当前播放模式, 当前屏幕方向
    NSMutableDictionary *event = [NSMutableDictionary new];
    
    //视频基本信息，长/宽/方向
    _width = player.actualVideoWidth;
    _height = player.actualVideoHeight;
    [event setValue:@{@"width":[NSNumber numberWithInt:_width],
                      @"height":[NSNumber numberWithInt:_height],
                      @"videoOrientation":(_width>_height)? @"landscape" : @"portrait"}
             forKey:@"naturalSize"];
    
    _onSubVideoLoad?_onSubVideoLoad(event):nil;
    
}


/*缓冲事件*/
- (void) processPlayerInfo:(LECPlayer *) player
               playerEvent:(LECPlayerPlayEvent) playerEvent
{
    switch (playerEvent) {
        case LECPlayerPlayEventBufferStart:
            _onSubBufferStart?_onSubBufferStart(nil):nil;
            break;
        case LECPlayerPlayEventRenderFirstPic:
            _onSubVideoRendingStart?_onSubVideoRendingStart(nil):nil;
            break;
        case LECPlayerPlayEventBufferEnd:
            _onSubBufferEnd?_onSubBufferEnd(nil):nil;
            break;
        default:
            break;
    }
}

/*尺寸变化*/
- (void) processVideoSizeChanged:(LECPlayer *) player
                     playerEvent:(LECPlayerPlayEvent) playerEvent
{
    _width =  player.actualVideoWidth;
    _height = player.actualVideoHeight;
    _onSubVideoSizeChange?_onSubVideoSizeChange(@{@"width": [NSNumber numberWithInt:_width],@"height": [NSNumber numberWithInt:_height],}):nil;
    
}

/*播放出错*/
- (void) processError:(LECPlayer *) player
          playerEvent:(LECPlayerPlayEvent) playerEvent
{
    NSString * error = [NSString stringWithFormat:@"%@:%@",player.errorCode,player.errorDescription];
    NSLog(@"播放器错误:%@",error);
    //[_playerViewController showTips:error]; //弹出提示
    _onSubVideoError?_onSubVideoError(@{@"errorCode": player.errorCode,@"errorMsg": player.errorDescription}):nil;
    
}

/*播放器状态事件*/
- (void) lecPlayer:(LECPlayer *) player
       playerEvent:(LECPlayerPlayEvent) playerEvent
{
    switch (playerEvent){
        case LECPlayerPlayEventPrepareDone: //准备结束
            [self processPrepared:player playerEvent:playerEvent];
            break;
            
        case LECPlayerPlayEventGetVideoSize: //视频源Size
            [self processVideoSizeChanged:player playerEvent:playerEvent];
            break;
            
        case LECPlayerPlayEventRenderFirstPic: //缓存相关
        case LECPlayerPlayEventBufferStart:
        case LECPlayerPlayEventBufferEnd:
            [self processPlayerInfo:player playerEvent:playerEvent];
            break;
            
            
            //        case LECPlayerPlayEventNoStream:  //无媒体信息
        case LECPlayerPlayEventPlayError: //播放错误
            [self processError:player playerEvent:playerEvent];
            break;
            
        default:
            break;
    }
}


- (void)usePlayerViewController:(LECPlayer*)player
{
    if( _lePlayer ){
        _playerViewController = [self createPlayerViewController:player];
        
        // to prevent video from being animated when resizeMode is 'cover'
        // resize mode must be set before subview is added
        //    [self addSubview:_playerViewController.view];
    }
}


#pragma mark - RCTLeVideoPlayerViewControllerDelegate

- (void)videoPlayerViewControllerWillDismiss:(LCBaseViewController *)playerViewController
{
    NSLog(@"videoPlayerViewControllerWillDismiss消息");
    
    if (_playerViewController == playerViewController){
        //    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerWillDismiss" body:@{@"target": self.reactTag}];
    }
}

- (void)videoPlayerViewControllerDidDismiss:(LCBaseViewController *)playerViewController
{
    NSLog(@"videoPlayerViewControllerDidDismiss消息");
    
    if (_playerViewController == playerViewController ){
        //    _presentingViewController = nil;
        //    [self applyModifiers];
        //    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerDidDismiss" body:@{@"target": self.reactTag}];
    }
}

-(void)videoPlayerViewShouldRotateToOrientation:(LCBaseViewController *)playerViewController{
    
    //    UIDeviceOrientation orientation = (UIDeviceOrientation)[UIApplication sharedApplication].statusBarOrientation;
    //    if (orientation == UIDeviceOrientationPortrait ||orientation == UIDeviceOrientationPortraitUpsideDown) {
    //        // 竖屏
    //
    //    }else {
    //        // 横屏
    //        CGFloat width = [UIScreen mainScreen].bounds.size.width;
    //        CGFloat height = [UIScreen mainScreen].bounds.size.height;
    //
    //        if (width < height){
    //            CGFloat tmp = width;
    //            width = height;
    //            height = tmp;
    //        }
    //        self.frame = CGRectMake(0, 0, width, height);
    //    }
}


#pragma mark - React View Management

- (void)insertReactSubview:(UIView *)view atIndex:(NSInteger)atIndex
{
    NSLog(@"insertReactSubview消息");
    [super insertReactSubview:view atIndex:atIndex];
    view.frame = self.bounds;
    //[_playerViewController.contentOverlayView insertSubview:view atIndex:atIndex];
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
    _playerViewController.view.frame = self.bounds;
    
    // also adjust all subviews of contentOverlayView
    //      for (UIView* subview in _playerViewController.contentOverlayView.subviews) {
    //        subview.frame = self.bounds;
    //      }
}

- (void)removeFromSuperview
{
    NSLog(@"removeFromSuperview消息");
    
    [self resetPlayerAndController];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    [super removeFromSuperview];
}


#pragma mark - 通知处理

- (void)applicationWillResignActive:(NSNotification *)notification
{
    if (_paused) return;
    [self pause];
}

- (void)applicationDidEnterBackground:(NSNotification *)notification
{
}

- (void)applicationWillEnterForeground:(NSNotification *)notification
{
    //  [self applyModifiers];
    [self resume];
}


@end
