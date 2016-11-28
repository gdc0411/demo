//
//  RCTLeVideo.m
//  RCTLeVideo
//
//  Created by RaoJia on 25.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import "RCTConvert.h"
#import "RCTLeVideo.h"
#import "RCTBridgeModule.h"
#import "RCTEventDispatcher.h"
#import "UIView+React.h"

#import "RCTLeVideoPlayerViewController.h"


#define LCRect_PlayerHalfFrame    CGRectMake(0, 50, [UIScreen mainScreen].bounds.size.width, 250);

@interface RCTLeVideo ()<LECPlayerDelegate>
{
  __block BOOL _isPlay;
  __block BOOL _isSeeking;
  BOOL _isFullScreen;
  
}
@property (nonatomic, strong) LECVODPlayer *lePlayer;
@property (nonatomic, strong) LECPlayerOption *option;

@end

@implementation RCTLeVideo
{
  BOOL _playerItemObserversSet;
  BOOL _playerBufferEmpty;
  
  LCBaseViewController *_playerViewController;
  NSURL *_videoURL;
  
  /* Required to publish events */
  RCTEventDispatcher *_eventDispatcher;
  
  int _playMode; //当前播放模式
  int _currentOritentation; //当前屏幕方向
  int _width; //当前视频宽度
  int _height; //当前视频高度
  NSString *_currentRate; //当前码率
  long _lastPosition;
  

  bool _pendingSeek;
  float _pendingSeekTime;
  float _lastSeekTime;
  
  /* For sending videoProgress events */
  Float64 _progressUpdateInterval;
  BOOL _controls;
  
  /* Keep track of any modifiers, need to be applied after each play */
  int _volume;
  int _brightness;
  
  BOOL _paused;
  BOOL _repeat;
  BOOL _playbackStalled;
  BOOL _playInBackground;
  BOOL _playWhenInactive;
  NSString * _resizeMode;
  BOOL _fullscreenPlayerPresented;
}

#pragma mark 创建事件分发器
- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher
{
  if ((self = [super init])) {
    _eventDispatcher = eventDispatcher;
    
    _isFullScreen = NO;
    _isPlay = NO;
    _isSeeking = NO;
    
    _currentOritentation = 9;

    
    //    _playbackStalled = NO;
    //    _rate = 1.0;
    //    _volume = 1.0;
    //    _resizeMode = @"AVLayerVideoGravityResizeAspectFill";
    //    _pendingSeek = false;
    //    _pendingSeekTime = 0.0f;
    //    _lastSeekTime = 0.0f;
    //    _progressUpdateInterval = 250;
    //    _controls = NO;
    //    _playerBufferEmpty = YES;
    //    _playInBackground = false;
    //    _playWhenInactive = false;
    
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

#pragma mark 设置viewController
- (LCBaseViewController*)createPlayerViewController:(LECVODPlayer*)player withPlayerOption:(LECPlayerOption*)playerOption {
  RCTLeVideoPlayerViewController* playerController= [[RCTLeVideoPlayerViewController alloc] init];
  playerController.rctDelegate = self; //实现协议
  playerController.view.frame = self.bounds;
  return playerController;
}


#pragma mark - Progress
- (void)dealloc
{
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - App lifecycle handlers

- (void)applicationWillResignActive:(NSNotification *)notification
{
  if (_playInBackground || _playWhenInactive || _paused) return;
  
  [_lePlayer pause];
}

- (void)applicationDidEnterBackground:(NSNotification *)notification
{
  if (_playInBackground) {
    // Needed to play sound in background. See https://developer.apple.com/library/ios/qa/qa1668/_index.html
    //[_playerLayer setPlayer:nil];
  }
}

- (void)applicationWillEnterForeground:(NSNotification *)notification
{
  //  [self applyModifiers];
  if (_playInBackground) {
    //[_playerLayer setPlayer:_player];
  }
}

#pragma mark - 将Dictionary转为Json
+ (NSString *)returnJSONStringWithDictionary:(NSDictionary *)dictionary{
  //系统自带
  NSError * error;
  NSData * jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:kNilOptions error:&error];
  if(error != nil){
    NSLog(@"转换JSON出错:%@", error);
    return nil;
  }
  
  NSString * jsonStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
  //自定义
  //  NSString *jsonStr = @"{";
  //  NSArray * keys = [dictionary allKeys];
  //  for (NSString * key in keys) {
  //    jsonStr = [NSString stringWithFormat:@"%@\"%@\":\"%@\",",jsonStr,key,[dictionary objectForKey:key]];
  //  }
  //  jsonStr = [NSString stringWithFormat:@"%@%@",[jsonStr substringWithRange:NSMakeRange(0, jsonStr.length-1)],@"}"];
  
  return jsonStr;
}


#pragma mark - Player and source
- (void)setSrc:(NSDictionary *)source
{
  NSLog(@"外部控制——— 传入数据源: %@", source);
  if(source == nil){
    return;
  }
  
  // 销毁原播放器和控制器
  if (_lePlayer != nil ) {
    [_lePlayer pause];
    
    [_playerViewController.view removeFromSuperview];
    _playerViewController = nil;
  }
  
  // 创建播放器
  _lePlayer = [[LECVODPlayer alloc] init];
  _lePlayer.delegate = self;
  
  self.frame = LCRect_PlayerHalfFrame;
  _lePlayer.videoView.frame = self.bounds;
  _lePlayer.videoView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin| UIViewAutoresizingFlexibleWidth| UIViewAutoresizingFlexibleHeight;
  _lePlayer.videoView.contentMode = UIViewContentModeScaleAspectFit;
  
  [self addSubview:_lePlayer.videoView];
  [self sendSubviewToBack:_lePlayer.videoView];
  
  //从source里拿到必要参数,用来创建player\option\controller
  [self playerItemForSource:source];
  
  
//  self.onVideoSourceLoad(@{@"target": self.reactTag,@"src": [[self class] returnJSONStringWithDictionary:source]});
  
  [_eventDispatcher sendInputEventWithName:@"onVideoSourceLoad"
                                      body:@{@"src": [[self class] returnJSONStringWithDictionary:source],
                                             @"target": self.reactTag}];
}

- (void)playerItemForSource:(NSDictionary *)source
{
  int playMode = [RCTConvert int:[source objectForKey:@"playMode"]];
  
  if(playMode == LCPlayerVod ){ //云点播
    NSLog(@"设置点播数据源");
    
    _playMode = LCPlayerVod;
    
    NSString *uuid = [source objectForKey:@"uuid"];
    NSString *vuid = [source objectForKey:@"vuid"];
    NSString *buinessline = [source objectForKey:@"businessline"];
    bool saas = [RCTConvert BOOL:[source objectForKey:@"saas"]];
    
    if (uuid.length != 0 && vuid.length != 0 && buinessline.length != 0 ) {
      [self usePlayerViewController]; // 创建controller
      _playerViewController.uu = uuid;
      _playerViewController.vu = vuid;
      _playerViewController.p = buinessline;
      
      _option = [[LECPlayerOption alloc]init]; //创建选项
      _option.p = buinessline;
      _option.businessLine = (saas)?LECBusinessLineSaas:LECBusinessLineCloud;
      
    }
    
  }else if( playMode == LCPlayerActionLive) { //活动直播
    
  }else{ //普通URL
    
  }
  
  __weak typeof(self) wSelf = self;
  [_lePlayer registerWithUu:_playerViewController.uu
                         vu:_playerViewController.vu
               payCheckCode:nil
                payUserName:nil
                    options:_option
      onlyLocalVODAvaliable:NO
 resumeFromLastPlayPosition:YES
     resumeFromLastRateType:YES
                 completion:^(BOOL result) {
                   if (result){
                     NSLog(@"播放器注册成功");
                     [wSelf play];//注册完成后自动播放
                   }else{
                     [_playerViewController showTips:@"播放器注册失败,请检查UU和VU"];
                     //      [_loadIndicatorView stopAnimating];
                   }
                 }];
  
}



#pragma mark - 播放控制
- (void)play
{
  if (_isPlay)
  {
    return;
  }
  __weak typeof(self) wSelf = self;
  [_lePlayer playWithCompletion:^{
    //    [wSelf.playStateBtn setTitle:@"暂停" forState:(UIControlStateNormal)];
    _isPlay = YES;
  }];
}

- (void)stop
{
  if (!_isPlay){
    return;
  }
  __weak typeof(self) wSelf = self;
  [_lePlayer stopWithCompletion:^{
    //    [wSelf.playStateBtn setTitle:@"播放" forState:(UIControlStateNormal)];
    _isPlay = NO;
  }];
}

- (void)pause
{
  if (!_isPlay){
    return;
  }
  [_lePlayer pause];
  //  [self.playStateBtn setTitle:@"播放" forState:(UIControlStateNormal)];
  _isPlay = NO;
}

#pragma mark - LECPlayerDelegate
#pragma mark 处理prepare事件
- (void) processPrepared:(LECPlayer *) player
             playerEvent:(LECPlayerPlayEvent) playerEvent
{
  NSLog(@"Prepared Event!");
  
  // 当前播放模式, 当前屏幕方向
  NSMutableDictionary *event = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                [NSNumber numberWithInt:_playMode],@"playMode",
                                [NSNumber numberWithInt:_currentOritentation],@"orientation", nil];
  
  // 视频基本信息，长/宽/方向
  _width = player.actualVideoWidth;
  _height = player.actualVideoHeight;
  NSString* videoOrientation = (_width > _height)? @"landscape" : @"portrait";
  
  NSDictionary *naturalSize =  [NSDictionary dictionaryWithObjectsAndKeys:
                                [NSNumber numberWithInt:_width],@"width",
                                [NSNumber numberWithInt:_height],@"height",
                                videoOrientation,@"videoOrientation",nil];
  
  [event setValue:_lePlayer.videoTitle forKey:@"title"];
  [event setValue:naturalSize forKey:@"naturalSize"];
  
  // 视频码率信息
  
  NSArray *aRatesList = _lePlayer.streamRatesList;
  if(aRatesList && [aRatesList count] >0 ){
    
    NSMutableArray *ratesList = [NSMutableArray arrayWithCapacity: [aRatesList count]];
    for(LECStreamRateItem *element in aRatesList){
      //NSLog(@"%@",element);
      if((NSNull *)element != [NSNull null] && element.isEnabled){
        [ratesList addObject: [NSDictionary dictionaryWithObjectsAndKeys:element.code,@"rateKey",element.name,@"rateValue",nil]];
      }
    }
    [event setValue:ratesList forKey:@"rateList"]; //可用码率
  }
  LECStreamRateItem * lItem = _lePlayer.selectedStreamRateItem;
  if( lItem ){
    [event setValue:lItem.code forKey:@"defaultRate"]; //默认码率
    [event setValue:_currentRate forKey:@"currentRate"]; //当前码率
  }
  
  // 视频封面信息: 加载
  if (_lePlayer.loadingIconUrl) {
    [event setValue:[NSDictionary dictionaryWithObjectsAndKeys:_lePlayer.loadingIconUrl,@"pic",nil] forKey:@"loading"];  // LOADING信息
  }
  
  if (_playMode == LCPlayerVod) { //VOD模式下参数
    [event setValue:[NSNumber numberWithLong:_lePlayer.duration ] forKey:@"duration"]; //视频总长度（VOD）
    [event setValue:[NSNumber numberWithLong:_lastPosition] forKey:@"currentTime"]; //当前播放位置（VOD）
  }
  
  // 设备信息： 声音和亮度
  [event setValue:[NSNumber numberWithLong:_volume] forKey:@"volume"]; //声音百分比
  [event setValue:[NSNumber numberWithLong:_brightness] forKey:@"brightness"]; //屏幕亮度
  
  [event setValue:self.reactTag forKey:@"target"];

  [_eventDispatcher sendInputEventWithName:@"onVideoLoad" body:event];
  
}


/*播放器播放状态*/
- (void) lecPlayer:(LECPlayer *) player
       playerEvent:(LECPlayerPlayEvent) playerEvent
{
  switch (playerEvent){
    case LECPlayerPlayEventPrepareDone:
      [self processPrepared:player playerEvent:playerEvent];
      break;
    case LECPlayerPlayEventEOS:
      [_playerViewController showTips:@"播放结束"];
      //      _playerViewController.playSlider.value = 0.0;
      //      _timeInfoLabel.text = @"00:00:00/00:00:00";
      //      [self.playStateBtn setTitle:@"播放" forState:(UIControlStateNormal)];
      _isPlay = NO;
      break;
    case LECPlayerPlayEventGetVideoSize:
      break;
    case LECPlayerPlayEventRenderFirstPic:
      //      [_loadIndicatorView stopAnimating];
      break;
    case LECPlayerPlayEventBufferStart:
      //      _loadIndicatorView.hidden = NO;
      //      [_loadIndicatorView startAnimating];
      NSLog(@"开始缓冲");
      break;
    case LECPlayerPlayEventBufferEnd:
      //      [_loadIndicatorView stopAnimating];
      NSLog(@"缓冲结束");
      break;
      
    case LECPlayerPlayEventSeekComplete:
      NSLog(@"完成Seek操作");
      _isSeeking = NO;
      //      [_loadIndicatorView stopAnimating];
      break;
      
    case LECPlayerPlayEventNoStream:
    {
      NSString * error = [NSString stringWithFormat:@"%@:%@",player.errorCode,player.errorDescription];
      NSLog(@"无媒体信息:%@",error);
      //      [_loadIndicatorView stopAnimating];
      [_playerViewController showTips:error];
    }
      break;
    case LECPlayerPlayEventPlayError:
    {
      NSString * error = [NSString stringWithFormat:@"%@:%@",player.errorCode,player.errorDescription];
      NSLog(@"播放器错误:%@",error);
      //      [_loadIndicatorView stopAnimating];
      [_playerViewController showTips:error];
    }
      break;
      
    default:
      break;
  }
}

/*播放器播放时间回调*/
- (void) lecPlayer:(LECPlayer *) player
          position:(int64_t) position
     cacheDuration:(int64_t) cacheDuration
          duration:(int64_t) duration
{
  if (!_isSeeking)
  {
    float value = (float)position/(float)duration;
    //    [_playSlider setValue:value];
  }
  NSString * playTimeStr = [_playerViewController timeFormate:position];
  NSString * totalTimeStr = [_playerViewController timeFormate:duration];
  //  _timeInfoLabel.text = [NSString stringWithFormat:@"%@/%@",playTimeStr,totalTimeStr];
  NSLog(@"播放位置:%lld,缓冲位置:%lld,总时长:%lld",position,cacheDuration,duration);
}

- (void) lecPlayer:(LECPlayer *) player contentTypeChanged:(LECPlayerContentType) contentType
{
  switch (contentType){
    case LECPlayerContentTypeFeature:
      NSLog(@"正在播放正片");
      break;
    case LECPlayerContentTypeAdv:
      NSLog(@"正在播放广告");
      //      [_loadIndicatorView stopAnimating];
      break;
      
    default:
      break;
  }
}

#pragma mark - 按钮事件
- (IBAction)clickToBack:(id)sender
{
  //销毁播放器
  [self stop];
  [_lePlayer unregister];
  _lePlayer = nil;
}


#pragma mark - 转屏处理逻辑

-(void)shouldRotateToOrientation:(UIDeviceOrientation)orientation {
  
  if (orientation == UIDeviceOrientationPortrait ||orientation == UIDeviceOrientationPortraitUpsideDown) {
    // 竖屏
    _isFullScreen = NO;
    //    _playerView.frame = LCRect_PlayerHalfFrame;
  }
  else {
    // 横屏
    CGFloat width = [UIScreen mainScreen].bounds.size.width;
    CGFloat height = [UIScreen mainScreen].bounds.size.height;
    if (width < height)
    {
      CGFloat tmp = width;
      width = height;
      height = tmp;
    }
    _isFullScreen = YES;
    //    _playerView.frame = CGRectMake(0, 0, width, height);
  }
}

- (void)viewWillLayoutSubviews
{
  [self shouldRotateToOrientation:(UIDeviceOrientation)[UIApplication sharedApplication].statusBarOrientation];
}

- (void)changeScreenAction
{
  if ([[UIDevice currentDevice] respondsToSelector:@selector(setOrientation:)])
  {
    NSNumber *num = [[NSNumber alloc] initWithInt:(_isFullScreen?UIInterfaceOrientationLandscapeRight:UIInterfaceOrientationPortrait)];
    [[UIDevice currentDevice] performSelector:@selector(setOrientation:) withObject:(id)num];
    [UIViewController attemptRotationToDeviceOrientation];
  }
  SEL selector=NSSelectorFromString(@"setOrientation:");
  NSInvocation *invocation =[NSInvocation invocationWithMethodSignature:[UIDevice instanceMethodSignatureForSelector:selector]];
  [invocation setSelector:selector];
  [invocation setTarget:[UIDevice currentDevice]];
  int val = _isFullScreen?UIInterfaceOrientationLandscapeRight:UIInterfaceOrientationPortrait;
  [invocation setArgument:&val atIndex:2];
  [invocation invoke];
}

#pragma mark - 转屏设置相关
- (BOOL)shouldAutorotate
{
  return YES;
}

- (void)playbackStalled:(NSNotification *)notification
{
  [_eventDispatcher sendInputEventWithName:@"onPlaybackStalled" body:@{@"target": self.reactTag}];
  _playbackStalled = YES;
}

- (void)playerItemDidReachEnd:(NSNotification *)notification
{
  [_eventDispatcher sendInputEventWithName:@"onVideoEnd" body:@{@"target": self.reactTag}];
}

#pragma mark - 设置属性
- (void)setPaused:(BOOL)paused
{
  if (paused) {
    [self pause];
  } else {
    [self play];
  }
  _paused = paused;
  
}

- (void)usePlayerViewController
{
  if( _lePlayer ){
    
    _playerViewController = [self createPlayerViewController:_lePlayer withPlayerOption:_option];
    
    // to prevent video from being animated when resizeMode is 'cover'
    // resize mode must be set before subview is added
    [self addSubview:_playerViewController.view];
  }
}


#pragma mark - RCTLeVideoPlayerViewControllerDelegate

- (void)videoPlayerViewControllerWillDismiss:(LCBaseViewController *)playerViewController
{
  if (_playerViewController == playerViewController && _fullscreenPlayerPresented){
    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerWillDismiss" body:@{@"target": self.reactTag}];
  }
}

- (void)videoPlayerViewControllerDidDismiss:(LCBaseViewController *)playerViewController
{
  if (_playerViewController == playerViewController && _fullscreenPlayerPresented){
    _fullscreenPlayerPresented = false;
    //    _presentingViewController = nil;
    //    [self applyModifiers];
    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerDidDismiss" body:@{@"target": self.reactTag}];
  }
}

#pragma mark - React View Management

- (void)insertReactSubview:(UIView *)view atIndex:(NSInteger)atIndex
{
  
  if( _controls ){
    view.frame = self.bounds;
    //[_playerViewController.contentOverlayView insertSubview:view atIndex:atIndex];
  }else {
    RCTLogError(@"video cannot have any subviews");
  }
  return;
}

- (void)removeReactSubview:(UIView *)subview
{
  if( _controls ){
    [subview removeFromSuperview];
  }else  {
    RCTLogError(@"video cannot have any subviews");
  }
  return;
}

- (void)layoutSubviews
{
  [super layoutSubviews];
  if( _controls ){
    _playerViewController.view.frame = self.bounds;
    
    // also adjust all subviews of contentOverlayView
    //    for (UIView* subview in _playerViewController.contentOverlayView.subviews) {
    //      subview.frame = self.bounds;
    //    }
  } else {
    [CATransaction begin];
    [CATransaction setAnimationDuration:0];
    [CATransaction commit];
  }
}

#pragma mark - Lifecycle

- (void)removeFromSuperview
{
  [_lePlayer pause];
  //  if (_playbackRateObserverRegistered) {
  //    [_lePlayer removeObserver:self forKeyPath:playbackRate context:nil];
  //    _playbackRateObserverRegistered = NO;
  //  }
  _lePlayer = nil;
  
  [_playerViewController.view removeFromSuperview];
  _playerViewController = nil;
  
  _eventDispatcher = nil;
  [[NSNotificationCenter defaultCenter] removeObserver:self];
  
  [super removeFromSuperview];
}

@end
