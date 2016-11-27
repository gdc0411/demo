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
//  LECVODPlayer *_lePlayer;
//  LECPlayerOption *_option;
  BOOL _playerItemObserversSet;
  BOOL _playerBufferEmpty;
  //AVPlayerLayer *_playerLayer;
  LCBaseViewController *_playerViewController;
  NSURL *_videoURL;

  /* Required to publish events */
  RCTEventDispatcher *_eventDispatcher;
  BOOL _playbackRateObserverRegistered;

  bool _pendingSeek;
  float _pendingSeekTime;
  float _lastSeekTime;

  /* For sending videoProgress events */
  Float64 _progressUpdateInterval;
  BOOL _controls;
  id _timeObserver;

  /* Keep track of any modifiers, need to be applied after each play */
  float _volume;
  float _rate;
  BOOL _muted;
  BOOL _paused;
  BOOL _repeat;
  BOOL _playbackStalled;
  BOOL _playInBackground;
  BOOL _playWhenInactive;
  NSString * _resizeMode;
  BOOL _fullscreenPlayerPresented;
  UIViewController * _presentingViewController;
}

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher
{
  if ((self = [super init])) {
    _eventDispatcher = eventDispatcher;
    _isFullScreen = NO;
    _isPlay = NO;
    _isSeeking = NO;
    
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

- (LCBaseViewController*)createPlayerViewController:(LECVODPlayer*)player withPlayerItem:(LECPlayerOption*)playerItem {
    RCTLeVideoPlayerViewController* playerLayer= [[RCTLeVideoPlayerViewController alloc] init];
    //playerLayer.showsPlaybackControls = NO;
    playerLayer.rctDelegate = self;
    playerLayer.view.frame = self.bounds;
    //playerLayer.player = _player;
    playerLayer.view.frame = self.bounds;
    return playerLayer;
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
  [self applyModifiers];
  if (_playInBackground) {
    //[_playerLayer setPlayer:_player];
  }
}



#pragma mark - Player and source

- (void)setSrc:(NSDictionary *)source
{
  // 销毁原播放器
  if (!_lePlayer) {
    [_lePlayer pause];
    [self removePlayerLayer];
  }
  [_playerViewController.view removeFromSuperview];
  _playerViewController = nil;
  
  // 新建播放器
  _lePlayer = [[LECVODPlayer alloc] init];
  _lePlayer.delegate = self;
  
  self.frame = LCRect_PlayerHalfFrame;
  _lePlayer.videoView.frame = self.bounds;
  _lePlayer.videoView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin| UIViewAutoresizingFlexibleWidth| UIViewAutoresizingFlexibleHeight;
  _lePlayer.videoView.contentMode = UIViewContentModeScaleAspectFit;
  
  [self addSubview:_lePlayer.videoView];
  [self sendSubviewToBack:_lePlayer.videoView];
  
  _option = [[LECPlayerOption alloc]init];
  _option.p = _playerViewController.p;
  _option.businessLine = LECBusinessLineSaas;
  

  [self playerItemForSource:source];
  
  [_eventDispatcher sendInputEventWithName:@"onVideoLoadStart"
                                      body:@{@"src": @{
                                             @"uri": [source objectForKey:@"uri"],
                                            @"type": [source objectForKey:@"type"],
                                       @"isNetwork":[NSNumber numberWithBool:(bool)[source objectForKey:@"isNetwork"]]},
                                          @"target": self.reactTag}];
}

- (void)playerItemForSource:(NSDictionary *)source
{
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
    if (result)
    {
      NSLog(@"播放器注册成功");
      [wSelf play];//注册完成后自动播放
//      wSelf.titleLabel.text = wSelf.player.videoTitle;
//      LECStreamRateItem * lItem = wSelf.player.selectedStreamRateItem;
//      [wSelf.playerRateBtn setTitle:lItem.name
//                           forState:(UIControlStateNormal)];
      
//      _timeObserver = [_player addPeriodicTimeObserverForInterval:CMTimeMakeWithSeconds(progressUpdateIntervalMS, NSEC_PER_SEC)
//                                                            queue:NULL
//                                                       usingBlock:^(CMTime time) { [weakSelf sendProgressUpdate]; }
//                       ];
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
  if (!_isPlay)
  {
    return;
  }
  __weak typeof(self) wSelf = self;
  [_lePlayer stopWithCompletion:^{
    wSelf.playSlider.value = 0.0;
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
/*播放器播放状态*/
- (void) lecPlayer:(LECPlayer *) player
         playerEvent:(LECPlayerPlayEvent) playerEvent
{
  switch (playerEvent){
    case LECPlayerPlayEventPrepareDone:
//      _titleLabel.text = _lePlayer.videoTitle;
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
    [_playSlider setValue:value];
  }
  NSString * playTimeStr = [self timeFormate:position];
  NSString * totalTimeStr = [self timeFormate:duration];
  _timeInfoLabel.text = [NSString stringWithFormat:@"%@/%@",playTimeStr,totalTimeStr];
  NSLog(@"播放位置:%lld,缓冲位置:%lld,总时长:%lld",position,cacheDuration,duration);
}

- (void) lecPlayer:(LECPlayer *) player contentTypeChanged:(LECPlayerContentType) contentType
{
  switch (contentType)
  {
    case LECPlayerContentTypeFeature:
      NSLog(@"正在播放正片");
      break;
    case LECPlayerContentTypeAdv:
      NSLog(@"正在播放广告");
      [_loadIndicatorView stopAnimating];
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
  [_vodPlayer unregister];
  _vodPlayer = nil;
  [self.navigationController popViewControllerAnimated:YES];
}

- (IBAction)clickToChangePlayState:(id)sender
{
  if (_isPlay)
  {
    [self pause];
    return;
  }
  [self play];
}

- (IBAction)slideToChangePlayProgress:(id)sender
{
  float value = _playSlider.value;
  [_vodPlayer seekToPosition:_vodPlayer.duration*value completion:^{
    _isSeeking = NO;
  }];
}

- (IBAction)slideToChangeValue:(id)sender
{
  _isSeeking = YES;
}

- (IBAction)clickToChangePlayerRate:(id)sender
{
  UIAlertController * alertController = [UIAlertController alertControllerWithTitle:@"码率"
                                                                            message:@"请选择您要切换的清晰度"
                                                                     preferredStyle:UIAlertControllerStyleActionSheet];
  UIAlertAction * cancelAction =[UIAlertAction actionWithTitle:@"取消"
                                                         style:(UIAlertActionStyleCancel)
                                                       handler:NULL];
  [alertController addAction:cancelAction];
  
  
  NSArray * list = self.vodPlayer.streamRatesList;
  
  for (LECStreamRateItem * lItem in list)
  {
    if (lItem.isEnabled)
    {
      __weak typeof(self) wSelf = self;
      UIAlertAction * action =[UIAlertAction actionWithTitle:lItem.name
                                                       style:(UIAlertActionStyleDefault)
                                                     handler:^(UIAlertAction * _Nonnull action) {
                                                       [wSelf.vodPlayer switchSelectStreamRateItem:lItem
                                                                                        completion:^{
                                                                                          [wSelf.playerRateBtn setTitle:lItem.name forState:(UIControlStateNormal)];
                                                                                        }];
                                                     }];
      [alertController addAction:action];
    }
  }
  
  [self presentViewController:alertController animated:YES completion:NULL];
}

- (IBAction)clickToChangePlayerScreen:(id)sender
{
  _isFullScreen = !_isFullScreen;
  [self changeScreenAction];
}

#pragma mark - 转屏处理逻辑

-(void)shouldRotateToOrientation:(UIDeviceOrientation)orientation {
  
  if (orientation == UIDeviceOrientationPortrait ||orientation == UIDeviceOrientationPortraitUpsideDown) {
    // 竖屏
    _isFullScreen = NO;
    _playerView.frame = LCRect_PlayerHalfFrame;
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
    _playerView.frame = CGRectMake(0, 0, width, height);
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

#pragma mark - Prop setters

- (void)setPaused:(BOOL)paused
{
  if (paused) {
    [_lePlayer pause];
    [_lePlayer setRate:0.0];
  } else {
    [_lePlayer play];
    [_lePlayer setRate:_rate];
  }

  _paused = paused;
}


- (BOOL)getFullscreen
{
    return _fullscreenPlayerPresented;
}

- (void)setFullscreen:(BOOL)fullscreen
{
    if( fullscreen && !_fullscreenPlayerPresented )
    {
        // Ensure player view controller is not null
        if( !_playerViewController )
        {
            [self usePlayerViewController];
        }
        // Set presentation style to fullscreen
        [_playerViewController setModalPresentationStyle:UIModalPresentationFullScreen];

        // Find the nearest view controller
        UIViewController *viewController = [self firstAvailableUIViewController];
        if( !viewController )
        {
            UIWindow *keyWindow = [[UIApplication sharedApplication] keyWindow];
            viewController = keyWindow.rootViewController;
            if( viewController.childViewControllers.count > 0 )
            {
                viewController = viewController.childViewControllers.lastObject;
            }
        }
        if( viewController )
        {
            _presentingViewController = viewController;
            [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerWillPresent" body:@{@"target": self.reactTag}];
            [viewController presentViewController:_playerViewController animated:true completion:^{
                _playerViewController.showsPlaybackControls = YES;
                _fullscreenPlayerPresented = fullscreen;
                [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerDidPresent" body:@{@"target": self.reactTag}];
            }];
        }
    }
    else if ( !fullscreen && _fullscreenPlayerPresented )
    {
        [self videoPlayerViewControllerWillDismiss:_playerViewController];
        [_presentingViewController dismissViewControllerAnimated:true completion:^{
            [self videoPlayerViewControllerDidDismiss:_playerViewController];
        }];
    }
}

- (void)usePlayerViewController
{
    if( _lePlayer )
    {
        _playerViewController = [self createPlayerViewController:_lePlayer withPlayerItem:_option];
        // to prevent video from being animated when resizeMode is 'cover'
        // resize mode must be set before subview is added
        [self setResizeMode:_resizeMode];
        [self addSubview:_playerViewController.view];
    }
}

- (void)usePlayerLayer
{
    if( _lePlayer )
    {
      _playerLayer = [AVPlayerLayer playerLayerWithPlayer:_lePlayer];
      _playerLayer.frame = self.bounds;
      _playerLayer.needsDisplayOnBoundsChange = YES;

      // to prevent video from being animated when resizeMode is 'cover'
      // resize mode must be set before layer is added
      [self setResizeMode:_resizeMode];

      [self.layer addSublayer:_playerLayer];
      self.layer.needsDisplayOnBoundsChange = YES;
    }
}

- (void)setControls:(BOOL)controls
{
    if( _controls != controls || (!_playerLayer && !_playerViewController) )
    {
        _controls = controls;
        if( _controls )
        {
            [self removePlayerLayer];
            [self usePlayerViewController];
        }
        else
        {
            [_playerViewController.view removeFromSuperview];
            _playerViewController = nil;
            [self usePlayerLayer];
        }
    }
}



#pragma mark - RCTLeVideoPlayerViewControllerDelegate

- (void)videoPlayerViewControllerWillDismiss:(LCBaseViewController *)playerViewController
{
    if (_playerViewController == playerViewController && _fullscreenPlayerPresented)
    {
        [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerWillDismiss" body:@{@"target": self.reactTag}];
    }
}

- (void)videoPlayerViewControllerDidDismiss:(LCBaseViewController *)playerViewController
{
    if (_playerViewController == playerViewController && _fullscreenPlayerPresented)
    {
        _fullscreenPlayerPresented = false;
        _presentingViewController = nil;
        [self applyModifiers];
        [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerDidDismiss" body:@{@"target": self.reactTag}];
    }
}

#pragma mark - React View Management

- (void)insertReactSubview:(UIView *)view atIndex:(NSInteger)atIndex
{
  // We are early in the game and somebody wants to set a subview.
  // That can only be in the context of playerViewController.
  if( !_controls && !_playerLayer && !_playerViewController )
  {
    [self setControls:true];
  }

  if( _controls )
  {
     view.frame = self.bounds;
     [_playerViewController.contentOverlayView insertSubview:view atIndex:atIndex];
  }
  else
  {
     RCTLogError(@"video cannot have any subviews");
  }
  return;
}

- (void)removeReactSubview:(UIView *)subview
{
  if( _controls )
  {
      [subview removeFromSuperview];
  }
  else
  {
    RCTLogError(@"video cannot have any subviews");
  }
  return;
}

- (void)layoutSubviews
{
  [super layoutSubviews];
  if( _controls )
  {
    _playerViewController.view.frame = self.bounds;

    // also adjust all subviews of contentOverlayView
    for (UIView* subview in _playerViewController.contentOverlayView.subviews) {
      subview.frame = self.bounds;
    }
  }
  else
  {
      [CATransaction begin];
      [CATransaction setAnimationDuration:0];
      _playerLayer.frame = self.bounds;
      [CATransaction commit];
  }
}

#pragma mark - Lifecycle

- (void)removeFromSuperview
{
  [_lePlayer pause];
  if (_playbackRateObserverRegistered) {
    [_lePlayer removeObserver:self forKeyPath:playbackRate context:nil];
    _playbackRateObserverRegistered = NO;
  }
  _lePlayer = nil;

  [self removePlayerLayer];

  [_playerViewController.view removeFromSuperview];
  _playerViewController = nil;

  [self removePlayerTimeObserver];
  [self removePlayerItemObservers];

  _eventDispatcher = nil;
  [[NSNotificationCenter defaultCenter] removeObserver:self];

  [super removeFromSuperview];
}

@end
