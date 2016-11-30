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

#import "RCTBridge.h"
#import "LECVODPlayer.h"
#import "LECActivityPlayer.h"
#import "LECActivityInfoManager.h"
#import "LECActivityLiveItem.h"
#import "LECPlayerOption.h"
#import "LCBaseViewController.h"

#import "RCTLeVideoPlayerViewController.h"


#define LCRect_PlayerHalfFrame    CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 250);

@interface RCTLeVideo ()<LECPlayerDelegate, LCActivityManagerDelegate>
{
  __block BOOL _isPlay;
  __block BOOL _isSeeking;
  BOOL _isFullScreen;
  
}
@property (nonatomic, strong) LECPlayer *lePlayer;
@property (nonatomic, strong) LECPlayerOption *option;

@property(nonatomic) CGFloat brightness NS_AVAILABLE_IOS(5_0);        // 0 .. 1.0, where 1.0 is maximum brightness. Only supported by main screen.


@property (nonatomic, copy) RCTDirectEventBlock onVideoSourceLoad;  // 数据源设置
@property (nonatomic, copy) RCTDirectEventBlock onVideoSizeChange;  // 视频真实宽高
@property (nonatomic, copy) RCTDirectEventBlock onVideoRateLoad; // 视频码率列表
@property (nonatomic, copy) RCTDirectEventBlock onVideoLoad; // 播放器准备完毕
@property (nonatomic, copy) RCTDirectEventBlock onVideoError;  // 播放出错
@property (nonatomic, copy) RCTDirectEventBlock onVideoProgress; // 更新播放视频
@property (nonatomic, copy) RCTDirectEventBlock onVideoBufferPercent;  // 缓存进度
@property (nonatomic, copy) RCTDirectEventBlock onVideoPause; // 播放暂停
@property (nonatomic, copy) RCTDirectEventBlock onVideoResume; // 播放继续
@property (nonatomic, copy) RCTDirectEventBlock onVideoSeek; // 播放跳转中
@property (nonatomic, copy) RCTDirectEventBlock onVideoSeekComplete; // 播放跳转结束
@property (nonatomic, copy) RCTDirectEventBlock onVideoRateChange; //视频码率切换
@property (nonatomic, copy) RCTDirectEventBlock onVideoEnd;  // 播放完毕
@property (nonatomic, copy) RCTDirectEventBlock onBufferStart; // 开始缓冲
@property (nonatomic, copy) RCTDirectEventBlock onBufferEnd;  // 缓冲结束
@property (nonatomic, copy) RCTDirectEventBlock onVideoRendingStart; // 加载第一帧
@property (nonatomic, copy) RCTDirectEventBlock onBufferPercent; // 缓冲加载进度，转圈
@property (nonatomic, copy) RCTDirectEventBlock onAdvertStart; // 广告开始
@property (nonatomic, copy) RCTDirectEventBlock onAdvertProgress;  // 广告播放中
@property (nonatomic, copy) RCTDirectEventBlock onAdvertComplete; // 广告结束
@property (nonatomic, copy) RCTDirectEventBlock onAdvertClick; // 广告点击
@property (nonatomic, copy) RCTDirectEventBlock onAdvertError; // 广告出错
@property (nonatomic, copy) RCTDirectEventBlock onMediaVodLoad; // 获得点播媒资
@property (nonatomic, copy) RCTDirectEventBlock onMediaLiveLoad; // 获得直播媒资
@property (nonatomic, copy) RCTDirectEventBlock onMediaActionLoad; // 获得活动直播媒资
@property (nonatomic, copy) RCTDirectEventBlock onMediaPlayURLLoad; // 获得媒资调度
@property (nonatomic, copy) RCTDirectEventBlock onActionLiveChange; // 云直播切换机位
@property (nonatomic, copy) RCTDirectEventBlock onActionTimeShift; // 云直播进度
@property (nonatomic, copy) RCTDirectEventBlock onActionStatusChange; // 云直播状态回调
@property (nonatomic, copy) RCTDirectEventBlock onActionOnlineNumChange; // 云直播在线人数变化
@property (nonatomic, copy) RCTDirectEventBlock onOrientationChange; //屏幕方向切换
@property (nonatomic, copy) RCTDirectEventBlock onOtherEventInfo;  // 其他事件


@end

@implementation RCTLeVideo
{
  /* Required to connect JS */
  __weak RCTBridge *_bridge;
  
  BOOL _playerItemObserversSet;
  BOOL _playerBufferEmpty;
  
  LCBaseViewController *_playerViewController;
  NSURL *_videoURL;
  
  int _playMode; //当前播放模式
  int _currentOritentation; //当前屏幕方向
  int _width; //当前视频宽度
  int _height; //当前视频高度
  NSString *_currentRate; //当前码率
  long _duration; //视频总厂
  long _lastPosition; //最后位置
  NSArray *_ratesList; //可用码率列表
  
  int _currentBrightness;  //屏幕亮度百分比 0-100

  
  bool _pendingSeek;
  float _pendingSeekTime;
  float _lastSeekTime;
  
  /* For sending videoProgress events */
  Float64 _progressUpdateInterval;
  //  BOOL _controls;
  
  /* Keep track of any modifiers, need to be applied after each play */
  int _volume;
  
  BOOL _paused;
  BOOL _repeat;
  BOOL _playbackStalled;
  BOOL _playInBackground;
  BOOL _playWhenInactive;
  NSString * _resizeMode;
  BOOL _fullscreenPlayerPresented;
}

#pragma mark 初始化
/*实例化桥*/
- (instancetype)initWithBridge:(RCTBridge *)bridge
{
  NSLog(@"初始化桥……");
  if ((self = [super init])) {
    _bridge = bridge;
    
    _isFullScreen = NO;
    _isPlay = NO;
    _isSeeking = NO;
    
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
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleDeviceOrientationDidChange:)
                                                 name:UIDeviceOrientationDidChangeNotification
                                               object:nil];
    
    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications]; //开始生成设备旋转通知

    
  }
  return self;
}

//- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher
//{
//  if ((self = [super init])) {
//    _eventDispatcher = eventDispatcher;
//
//    _isFullScreen = NO;
//    _isPlay = NO;
//    _isSeeking = NO;
//
//    _currentOritentation = 9;
//
//
//    //    _playbackStalled = NO;
//    //    _rate = 1.0;
//    //    _volume = 1.0;
//    //    _resizeMode = @"AVLayerVideoGravityResizeAspectFill";
//    //    _pendingSeek = false;
//    //    _pendingSeekTime = 0.0f;
//    //    _lastSeekTime = 0.0f;
//    //    _progressUpdateInterval = 250;
//    //    _controls = NO;
//    //    _playerBufferEmpty = YES;
//    //    _playInBackground = false;
//    //    _playWhenInactive = false;
//
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(applicationWillResignActive:)
//                                                 name:UIApplicationWillResignActiveNotification
//                                               object:nil];
//
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(applicationDidEnterBackground:)
//                                                 name:UIApplicationDidEnterBackgroundNotification
//                                               object:nil];
//
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(applicationWillEnterForeground:)
//                                                 name:UIApplicationWillEnterForegroundNotification
//                                               object:nil];
//  }
//
//  return self;
//}

/*创建viewController*/
- (LCBaseViewController*)createPlayerViewController:(LECPlayer*)player withPlayerOption:(LECPlayerOption*)playerOption {
  RCTLeVideoPlayerViewController* playerController= [[RCTLeVideoPlayerViewController alloc] init];
  playerController.rctDelegate = self; //实现协议
  playerController.view.frame = self.bounds;
  return playerController;
}

#pragma mark - 设置属性
- (void)setSrc:(NSDictionary *)source
{
  NSLog(@"外部控制——— 传入数据源: %@", source);
  if(source == nil)
    return;
  
  // 销毁原播放器和控制器
  if (_lePlayer) {
    [_lePlayer pause];
    
    [_playerViewController.view removeFromSuperview];
    _playerViewController = nil;
  }
  
  
  //从source里拿到必要参数,用来创建player\option\controller
  [self playerItemForSource:source];
  
//  if (_onVideoSourceLoad) {
//    //    _onVideoSourceLoad(@{@"target": self.reactTag,@"src": [[self class] returnJSONStringWithDictionary:source useSystem:YES]});
//    _onVideoSourceLoad(source);
//  }
  
}


- (void)setPaused:(BOOL)paused
{
  if(_lePlayer == nil)
    return;
  
  if (paused) {
    NSLog(@"外部控制——— 暂停播放 pause ");
    [self pause];
    
  } else {
    NSLog(@"外部控制——— 开始播放 start ");
    [self play];
  }
  _paused = paused;
}


- (void)setSeek:(float)seek
{
  if( seek < 0 || _lePlayer == nil)
    return;
  _isSeeking = YES;
  
  __weak typeof(self) wSelf = self;
  [_lePlayer seekToPosition:seek completion:^{
    _isSeeking = NO;
    if (wSelf.onVideoSeekComplete) {
      wSelf.onVideoSeekComplete(nil);
    }
  }];
  
  if(_onVideoSeek){
    _onVideoSeek(@{@"currentTime": [NSNumber numberWithDouble:_lastPosition], @"seekTime":[NSNumber numberWithDouble:seek]});
  }
  NSLog(@"外部控制——— SEEK TO: %f", seek);
}

- (void)setRate:(NSString*)rate
{
  if( [[self class]isBlankString:rate] || _lePlayer == nil || [_ratesList count]==0)
    return;
  
  for (LECStreamRateItem * lItem in _ratesList){
    if (lItem.isEnabled && [rate isEqualToString:lItem.code] ){
      
      _currentRate = rate;
      __weak typeof(self) wSelf = self;
      [wSelf.lePlayer switchSelectStreamRateItem:lItem completion:^{
        if (wSelf.onVideoRateChange) {
          wSelf.onVideoRateChange(@{@"currentRate":_currentRate,@"nextRate":lItem.code});
        }
      }];
    }
  }
  NSLog(@"外部控制——— 切换码率 current:%@ next:%@", _currentRate, rate);
}

- (void)setLive:(NSString*)liveId
{
  
}

- (void)setClickAd:(BOOL)isClicked
{
  
}

- (void)setVolume:(int)volume
{
  if (volume < 0 || volume > 100) {
    return;
  }
  _volume = volume;
  _lePlayer.volume = volume;
  
  NSLog(@"外部控制——— 音量调节:%d", volume );

}

- (void)setBrightness:(int)brightness
{
  if (brightness < 0 || brightness > 100) {
    return;
  }
  _currentBrightness = brightness;

  [[UIScreen mainScreen] setBrightness: brightness / 100];
  NSLog(@"外部控制——— 调节亮度:%d", brightness );
}

- (void)setOrientation:(int)requestedOrientation
{
  if( requestedOrientation<0 || requestedOrientation == _currentOritentation )
    return;

  _currentOritentation = requestedOrientation;
  
  int orientation = 1;
  switch (requestedOrientation) {
    case 0:
      _isFullScreen = YES;
      orientation = UIInterfaceOrientationLandscapeRight;
      break;
    case 1:
      _isFullScreen = NO;
      orientation = UIDeviceOrientationPortrait;
      break;
    case 8:
      _isFullScreen = YES;
      orientation = UIDeviceOrientationLandscapeLeft;
      break;
    case 9:
      _isFullScreen = NO;
      orientation = UIDeviceOrientationPortraitUpsideDown;
      break;
  }
  [self changeScreenOrientation:[NSNumber numberWithInt:orientation]];
  
  NSLog(@"外部控制——— 设置方向 orientation:%d", requestedOrientation);
}

- (void)setPlayInBackground:(BOOL)playInBackground
{
  
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
  int playMode = [RCTConvert int:[source objectForKey:@"playMode"]];
  
  if(playMode == LCPlayerVod ){ //云点播
    NSLog(@"点播数据源");
    
    _playMode = LCPlayerVod;
    
    // 创建播放器
    _lePlayer = [[LECVODPlayer alloc] init];
    _lePlayer.delegate = self;
    
    self.frame = LCRect_PlayerHalfFrame;
    _lePlayer.videoView.frame = self.bounds;
    _lePlayer.videoView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin| UIViewAutoresizingFlexibleWidth| UIViewAutoresizingFlexibleHeight;
    _lePlayer.videoView.contentMode = UIViewContentModeScaleAspectFit;
    
    [self addSubview:_lePlayer.videoView];
    [self sendSubviewToBack:_lePlayer.videoView];

    
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
    
    __weak typeof(self) wSelf = self;
    [(LECVODPlayer*)_lePlayer registerWithUu:uuid
                                          vu:vuid
                                payCheckCode:nil
                                 payUserName:nil
                                     options:_option
                       onlyLocalVODAvaliable:NO
                  resumeFromLastPlayPosition:NO
                      resumeFromLastRateType:YES
                                  completion:^(BOOL result) {
                                    
                                    if (wSelf.onVideoSourceLoad) {//数据源回显
                                      wSelf.onVideoSourceLoad(@{@"src": [[wSelf class] returnJSONStringWithDictionary:source useSystem:YES]});
                                    }
                                    
                                    if (result){
                                      NSLog(@"播放器注册成功");
                                      [wSelf play];//注册完成后自动播放
                                      
                                    }else{
                                      //[_playerViewController showTips:@"播放器注册失败,请检查UU和VU"];
                                      if (wSelf.onVideoError) {
                                        wSelf.onVideoError(@{@"errorCode":@"-1",@"errorMsg":@"播放器注册失败,请检查UU和VU"});
                                      }
                                    }
                                  }];

    
  }else if( playMode == LCPlayerActionLive) { //活动直播
    NSLog(@"直播数据源");

    _playMode = LCPlayerActionLive;
    
    LECActivityInfoManager * manager = [LECActivityInfoManager sharedManager];
    manager.delegate = self;
    [manager releaseActivity];
    
    //创建活动播放器
    _lePlayer = [[LECActivityPlayer alloc] init];
    _lePlayer.delegate = self;
    
    _lePlayer.videoView.frame = self.bounds;
    _lePlayer.videoView.contentMode = UIViewContentModeScaleAspectFit;
    _lePlayer.videoView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin| UIViewAutoresizingFlexibleWidth| UIViewAutoresizingFlexibleHeight;
    [self addSubview:_lePlayer.videoView];
    [self sendSubviewToBack:_lePlayer.videoView];
    
    
    NSString *activityId = [source objectForKey:@"actionId"];
    NSString *customId = [source objectForKey:@"customerId"];
    NSString *buinessline = [source objectForKey:@"businessline"];
    bool saas = [RCTConvert BOOL:[source objectForKey:@"saas"]];
    
    if (activityId.length != 0 && customId.length != 0 && buinessline.length != 0 ) {
      [self usePlayerViewController]; // 创建controller
      _playerViewController.activityId = activityId;
      _playerViewController.customId = customId;
      _playerViewController.p = buinessline;
      
      _option = [[LECPlayerOption alloc]init]; //创建选项
      _option.p = buinessline;
      _option.customId = customId;
      _option.businessLine = (saas)?LECBusinessLineSaas:LECBusinessLineCloud;
    }
    
    __weak typeof(self) wSelf = self;
    BOOL requestSuccess = [manager registerActivityWithActivityId:activityId option:_option completion:^(BOOL success) {
      
      if (success ) {
        NSLog(@"活动事件Manager注册成功");
//        [weakSelf.titleLabel setText:manager.activityItem.activityName];
        LECActivityItem *aItem = manager.activityItem;
        BOOL isEnable = NO;
        if (aItem.activityLiveItemList.count != 0) {
          for (LECActivityLiveItem * lItem in aItem.activityLiveItemList) {
            
            if (lItem.status == LCActivityLiveStatusUsing) {
              isEnable = [(LECActivityPlayer*)wSelf.lePlayer registerWithLiveId:lItem.liveId completion:^(BOOL result) {
                if (result) {
                  [wSelf play];//自动播放
                  LECStreamRateItem * lItem = wSelf.lePlayer.selectedStreamRateItem;
                  //[weakSelf.playerRateBtn setTitle:lItem.name forState:(UIControlStateNormal)];
                  NSLog(@"活动机位数目:%lu",(unsigned long)aItem.activityLiveItemList.count);
                }
                else {
                  NSString * error = [NSString stringWithFormat:@"%@:%@",
                                      wSelf.lePlayer.errorCode,
                                      wSelf.lePlayer.errorDescription];
                  NSLog(@"%@",error);
                }
              }];
              if (isEnable) {
                NSLog(@"播放机位可用");
                break;
              }
            }
          }
        }
        if (!isEnable) {
          NSLog(@"没有可用的直播机位");
        }
      } else {
        NSLog(@"直播活动注册失败");
      }
    }];
    
    if (!requestSuccess) {
      NSLog(@"活动事件Manager注册失败");
    }

    
  }else{ //普通URL
    
  }
}

/*设置屏幕方向*/
- (void)changeScreenOrientation:(NSNumber*) orientation
{
  if ([[UIDevice currentDevice] respondsToSelector:@selector(setOrientation:)]){
    
    [[UIDevice currentDevice] performSelector:@selector(setOrientation:) withObject:(id)orientation];
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

- (BOOL)shouldAutorotate
{
  return YES;
}


- (void)applyModifiers
{
  //  if (_muted) {
  //    [_lePlayer setVolume:0];
  //    [_lePlayer setMuted:YES];
  //  } else {
  //    [_lePlayer setVolume:_volume];
  //    [_lePlayer setMuted:NO];
  //  }
  
  //  [self setResizeMode:_resizeMode];
  //  [self setRepeat:_repeat];
  [self setPaused:_paused];
  //  [self setControls:_controls];
}



#pragma mark - 播放控制
- (void)play
{
  if (_isPlay) {
    return;
  }
  __weak typeof(self) wSelf = self;
  [_lePlayer playWithCompletion:^{
    if (wSelf.onVideoResume) {
      wSelf.onVideoResume(@{@"duration":[NSNumber numberWithDouble:_lePlayer.duration],
                            @"currentTime":[NSNumber numberWithDouble:_lePlayer.position],});
    }
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

  if (_onVideoPause) {
    _onVideoPause(@{@"duration":[NSNumber numberWithDouble:_lePlayer.duration],
                    @"currentTime":[NSNumber numberWithDouble:_lePlayer.position],});

  [_lePlayer pause];
}
  
  _isPlay = NO;
}

#pragma mark - 事件处理
- (void) processPrepared:(LECPlayer *) player
             playerEvent:(LECPlayerPlayEvent) playerEvent
{
  NSLog(@"Prepared Event!");
  
  if( [player isMemberOfClass: [LECVODPlayer class]] ){ //点播模式
    _playMode = LCPlayerVod;
  }
  
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
  
  if(_playMode == LCPlayerVod){
    [event setValue: ((LECVODPlayer*)player).videoTitle forKey:@"title"];
  }
  [event setValue:naturalSize forKey:@"naturalSize"];
  
  // 视频码率信息
  if(_playMode == LCPlayerVod){
      _ratesList = ((LECVODPlayer*)player).streamRatesList;
  }
  if(_ratesList && [_ratesList count] > 0 ){
    
    NSMutableArray *ratesList = [NSMutableArray arrayWithCapacity: [_ratesList count]];
    for(LECStreamRateItem *element in _ratesList){
      //NSLog(@"%@",element);
      if((NSNull *)element != [NSNull null] && element.isEnabled){
        [ratesList addObject: [NSDictionary dictionaryWithObjectsAndKeys:element.code,@"rateKey",element.name,@"rateValue",nil]];
      }
    }
    [event setValue:ratesList forKey:@"rateList"]; //可用码率
  }
  LECStreamRateItem * lItem = _lePlayer.selectedStreamRateItem;
  if( lItem ){
    _currentRate = lItem.code;
    [event setValue:lItem.code forKey:@"defaultRate"]; //默认码率
    [event setValue:_currentRate forKey:@"currentRate"]; //当前码率
  }
  
  // 视频封面信息: 加载
  if(_playMode == LCPlayerVod){
    if (((LECVODPlayer*)player).loadingIconUrl) {
      [event setValue:[NSDictionary dictionaryWithObjectsAndKeys:((LECVODPlayer*)player).loadingIconUrl,@"pic",nil] forKey:@"loading"];  // LOADING信息
    }
  }
  
  if (_playMode == LCPlayerVod) { //VOD模式下参数
    [event setValue:[NSNumber numberWithLong:(_duration==0)?((LECVODPlayer*)player).duration==0:_duration] forKey:@"duration"]; //视频总长度（VOD）
    [event setValue:[NSNumber numberWithLong:_lastPosition] forKey:@"currentTime"]; //当前播放位置（VOD）
  }
  
  // 设备信息： 音量和亮度
  _volume = player.volume;
//  _volume = [[AVAudioSession sharedInstance] outputVolume];
  
  _brightness = [UIScreen mainScreen].brightness;
  _currentBrightness = _brightness * 100;
  [event setValue:[NSNumber numberWithInt:_volume] forKey:@"volume"]; //声音百分比
  [event setValue:[NSNumber numberWithInt:_currentBrightness] forKey:@"brightness"]; //屏幕亮度
  
  if(_onVideoLoad){
    _onVideoLoad(event);
  }
  
  [self applyModifiers];
  
}


/*播放完成*/
- (void) processCompleted:(LECPlayer *) player
              playerEvent:(LECPlayerPlayEvent) playerEvent
{
  _isPlay = NO;
  if (_onVideoEnd) {
    _onVideoEnd(nil);
  }
}

/*缓冲事件*/
- (void) processPlayerInfo:(LECPlayer *) player
               playerEvent:(LECPlayerPlayEvent) playerEvent
{
  switch (playerEvent) {
    case LECPlayerPlayEventBufferStart:
      NSLog(@"开始缓冲");
      _isSeeking = YES;
      if (_onBufferStart) {
        _onBufferStart(nil);
      }
      break;
    case LECPlayerPlayEventRenderFirstPic:
      NSLog(@"加载第一帧");
      if (_onVideoRendingStart) {
        _onVideoRendingStart(nil);
      }
      break;
    case LECPlayerPlayEventBufferEnd:
      NSLog(@"缓冲结束");
      if (_onBufferEnd) {
        _onBufferEnd(nil);
      }
      break;
      
    default:
      break;
  }
}

/*尺寸变化*/
- (void) processVideoSizeChanged:(LECPlayer *) player
                     playerEvent:(LECPlayerPlayEvent) playerEvent
{
  NSLog(@"视频尺寸变化！");
  _width =  player.actualVideoWidth;
  _height = player.actualVideoHeight;
  
  if (_onVideoSizeChange) {
    _onVideoSizeChange(@{@"width": [NSNumber numberWithInt:_width],@"height": [NSNumber numberWithInt:_height],});
  }
}


/*SEEK完成*/
- (void) processSeekComplete:(LECPlayer *) player
                 playerEvent:(LECPlayerPlayEvent) playerEvent
{
  NSLog(@"完成Seek操作");
  _isSeeking = NO;
  
  if (_onVideoSeekComplete) {
    _onVideoSeekComplete(nil);
  }
}

/*播放出错*/
- (void) processError:(LECPlayer *) player
          playerEvent:(LECPlayerPlayEvent) playerEvent
{
  NSString * error = [NSString stringWithFormat:@"%@:%@",player.errorCode,player.errorDescription];
  NSLog(@"播放器错误:%@",error);
  //[_playerViewController showTips:error]; //弹出提示
  
  if (_onVideoError) {
    _onVideoError([NSDictionary dictionaryWithObjectsAndKeys: player.errorCode ,@"errorCode", player.errorDescription  ,@"errorMsg", nil]);
  }
}

/*播放器状态事件*/
- (void) lecPlayer:(LECPlayer *) player
       playerEvent:(LECPlayerPlayEvent) playerEvent
{
  switch (playerEvent){
    case LECPlayerPlayEventPrepareDone: //准备结束
      [self processPrepared:player playerEvent:playerEvent];
      break;
      
    case LECPlayerPlayEventEOS: //播放完成
      [self processCompleted:player playerEvent:playerEvent];
      break;
      
    case LECPlayerPlayEventGetVideoSize: //视频源Size
      [self processVideoSizeChanged:player playerEvent:playerEvent];
      break;
      
    case LECPlayerPlayEventRenderFirstPic: //缓存相关
    case LECPlayerPlayEventBufferStart:
    case LECPlayerPlayEventBufferEnd:
      [self processPlayerInfo:player playerEvent:playerEvent];
      break;
      
    case LECPlayerPlayEventSeekComplete: //seek完毕
      [self processSeekComplete:player playerEvent:playerEvent];
      break;
      
    case LECPlayerPlayEventNoStream:  //无媒体信息
    case LECPlayerPlayEventPlayError: //播放错误
      [self processError:player playerEvent:playerEvent];
      break;
      
    default:
      break;
  }
}

/* 播放进度 */
- (void) lecPlayer:(LECPlayer *) player
          position:(int64_t) position
     cacheDuration:(int64_t) cacheDuration
          duration:(int64_t) duration
{
  _lastPosition = position;
  _duration = duration;
  
  if(_onVideoProgress){
    _onVideoProgress(@{@"currentTime": [NSNumber numberWithDouble:position],
                       @"duration": [NSNumber numberWithDouble:duration],
                       @"playableDuration": [NSNumber numberWithDouble:cacheDuration],});
  }
  
  if(_onVideoBufferPercent){
    _onVideoBufferPercent(@{@"bufferpercent": [NSNumber numberWithInt: (int) ((((float)position + (float)cacheDuration)/(float)duration) * 100) ]});
  }
//  NSLog(@"播放位置:%lld,缓冲位置:%lld,总时长:%lld",position,cacheDuration,duration);
}


- (void) activityManager:(LECActivityInfoManager *) manager event:(LCActivityEvent) event {
  switch (event) {
    case LCActivityEventActivityConfigUpdate: {
      
      break;
    }
    case LCActivityEventOnlineAudiencesNumberUpdate: {
      NSLog(@"活动在线人数:%ld,",(long)manager.onlineAudiencesNumber);
      break;
    }
    default:
      break;
  }
}

#pragma mark 广告正片切换
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
  NSLog(@"videoPlayerViewControllerWillDismiss消息");
  
  if (_playerViewController == playerViewController && _fullscreenPlayerPresented){
    //    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerWillDismiss" body:@{@"target": self.reactTag}];
  }
}

- (void)videoPlayerViewControllerDidDismiss:(LCBaseViewController *)playerViewController
{
  NSLog(@"videoPlayerViewControllerDidDismiss消息");
  
  if (_playerViewController == playerViewController && _fullscreenPlayerPresented){
    _fullscreenPlayerPresented = false;
    //    _presentingViewController = nil;
    //    [self applyModifiers];
    //    [_eventDispatcher sendInputEventWithName:@"onVideoFullscreenPlayerDidDismiss" body:@{@"target": self.reactTag}];
  }
}

/*转屏处理逻辑*/
-(void)videoPlayerViewShouldRotateToOrientation:(LCBaseViewController *)playerViewController{
  
  UIDeviceOrientation orientation = (UIDeviceOrientation)[UIApplication sharedApplication].statusBarOrientation;
  if (orientation == UIDeviceOrientationPortrait ||orientation == UIDeviceOrientationPortraitUpsideDown) {
    // 竖屏
    _isFullScreen = NO;
    self.frame = LCRect_PlayerHalfFrame;
    
  }else {
    // 横屏
    CGFloat width = [UIScreen mainScreen].bounds.size.width;
    CGFloat height = [UIScreen mainScreen].bounds.size.height;
    
    if (width < height){
      CGFloat tmp = width;
      width = height;
      height = tmp;
    }
    _isFullScreen = YES;
    self.frame = CGRectMake(0, 0, width, height);
  }
}


#pragma mark - React View Management

- (void)insertReactSubview:(UIView *)view atIndex:(NSInteger)atIndex
{
  NSLog(@"insertReactSubview消息");
  view.frame = self.bounds;
  //[_playerViewController.contentOverlayView insertSubview:view atIndex:atIndex];
}

- (void)removeReactSubview:(UIView *)subview
{
  NSLog(@"removeReactSubview消息");

  [subview removeFromSuperview];
}

- (void)layoutSubviews
{
  NSLog(@"layoutSubviews消息");

  [super layoutSubviews];
  _playerViewController.view.frame = self.bounds;
  
  // also adjust all subviews of contentOverlayView
  //    for (UIView* subview in _playerViewController.contentOverlayView.subviews) {
  //      subview.frame = self.bounds;
  //    }
}



#pragma mark - View lifecycle
- (void)removeFromSuperview
{
  NSLog(@"removeFromSuperview消息");
  
  [self setOrientation:1];
  
  //销毁播放器
  [self stop];
  [_lePlayer unregister];
  _lePlayer.delegate = nil;
  _lePlayer = nil;
  
  [_playerViewController.view removeFromSuperview];
  _playerViewController = nil;
  
  if(_playMode == LCPlayerActionLive){
    [[LECActivityInfoManager sharedManager] releaseActivity];
  }
  
  [[NSNotificationCenter defaultCenter] removeObserver:self];
  
  [[UIDevice currentDevice]endGeneratingDeviceOrientationNotifications];
  
  [super removeFromSuperview];
}


#pragma mark - 通知处理

- (void)applicationWillResignActive:(NSNotification *)notification
{
  if (_playInBackground || _playWhenInactive || _paused) return;
  [self pause];
}

- (void)applicationDidEnterBackground:(NSNotification *)notification
{
  if (_playInBackground) {
    // Needed to play sound in background. See https://developer.apple.com/library/ios/qa/qa1668/_index.html
    //[_playerLayer setPlayer:nil];
    [self pause];
  }
}

- (void)applicationWillEnterForeground:(NSNotification *)notification
{
  [self applyModifiers];
  if (_playInBackground) {
    //[_playerLayer setPlayer:_player];
    //[self play];
  }
}

//判断设备的朝向
- (void)handleDeviceOrientationDidChange:(UIInterfaceOrientation)interfaceOrientation
{
  int deviceOrientation = -1;
  int value =[UIDevice currentDevice].orientation;
  
  switch (value) {
    case UIDeviceOrientationLandscapeLeft: //正横屏
      deviceOrientation = 0;
      break;
    case UIDeviceOrientationLandscapeRight: //反横屏
      deviceOrientation = 8;
      break;
    case UIDeviceOrientationPortrait: //正竖屏
      deviceOrientation = 1;
      break;
    case UIDeviceOrientationPortraitUpsideDown: //反竖屏
      deviceOrientation = 9;
      break;
    default:
      break;
  }
  
  if( deviceOrientation!= -1 && _onOrientationChange){
    _onOrientationChange(@{@"orientation": [NSNumber numberWithInt:deviceOrientation]});
  }
  
  NSLog(@"设备方向变化！！——— orientation：%d", deviceOrientation);
}


@end
