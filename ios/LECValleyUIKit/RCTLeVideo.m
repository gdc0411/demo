//
//  RCTLeVideo.m
//  RCTLeVideo
//
//  Created by RaoJia on 25.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//
#import "RCTLeVideo.h"
#import "LECValley.h"


#import <React/RCTConvert.h>
#import <React/UIView+React.h>

#import "VolumeModule.h"
#import "BrightnessModule.h"

#import "LECVODPlayer.h"
#import "LECActivityPlayer.h"
#import "LECActivityInfoManager.h"
#import "LECActivityLiveItem.h"
#import "LECPlayerOption.h"
#import "LCBaseViewController.h"

#import "RCTLeVideoPlayerViewController.h"


#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVFoundation.h>


#define LCRect_PlayerHalfFrame    CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height);

@interface RCTLeVideo ()<LECPlayerDelegate, LCActivityManagerDelegate>
{
    __block BOOL _isPlaying;
    __block BOOL _isSeeking;
    __block BOOL _isAdPlaying;
    BOOL _isFullScreen;
    
}

@property (nonatomic, strong) LECPlayer         *lePlayer;
@property (nonatomic, strong) LECPlayerOption   *option;
@property (nonatomic, strong) BrightnessModule  *brigtnessModule;

//@property(nonatomic) CGFloat screenBrightness NS_AVAILABLE_IOS(5_0);        // 0 .. 1.0, where 1.0 is maximum brightness. Only supported by main screen.


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
@property (nonatomic, copy) RCTDirectEventBlock onOtherEventInfo;  // 其他事件


@end

@implementation RCTLeVideo
{
    /* Required to connect JS */
    __weak RCTBridge *_bridge;
    
    //  BOOL _playerItemObserversSet;
    //  BOOL _playerBufferEmpty;
    
    __weak LCBaseViewController *_playerViewController;
    NSURL *_videoURL;
    
    int _playMode; //当前播放模式
    int _currentOritentation; //当前屏幕方向
    __block NSString *_title; //视频标题
    
    int _width; //视频宽度
    int _height; //视频高度
    
    long _currentTime; //当前时间
    long _duration; //视频总长度
    long _cacheDuration; //视频缓冲长度
    long _lastPosition; //最后位置
    
    NSArray *_ratesList; //可用码率列表
    NSString *_currentRate; //当前码率
    NSString *_defaultRate; //默认码率
    
    
    LECActivityItem *_activityItem; //直播信息
    LECActivityConfigItem *_activityConfigItem; //直播配置信息
    LECActivityLiveItem *_activityLiveItem; //直播机位信息
    NSString* _currentLive; //当前机位
    
    long _serverTime; //当前服务时间
    long _beginTime; //活动开始时间
    long _endTime; //活动结束时间
    
    bool _pendingSeek;
    float _pendingSeekTime;
    float _lastSeekTime;
    
    
    /* Keep track of any modifiers, need to be applied after each play */
    int _originVolume; //原音量0-100
    int _originBrightness;  //原屏幕亮度百分比 0-100
    
    BOOL _paused; //是否暂停
    BOOL _repeat; //是否重播
    
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
        
        _brigtnessModule = [[BrightnessModule alloc]init];
        
        _isFullScreen = NO;
        _isPlaying = NO;
        _isSeeking = NO;
        _isAdPlaying = NO;
        
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


- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

RCT_NOT_IMPLEMENTED(- (instancetype)init)

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
    
    //重置播放器
    [self resetPlayerAndController];
    
    //重置所有状态
    [self initFieldParaStates];
    
    [_playerViewController.view removeFromSuperview];
    _playerViewController = nil;
    
    //根据必要参数创建播放器
    [self playerItemForSource:source];
    
}


- (void)setPaused:(BOOL)paused
{
    if(_lePlayer == nil || _isAdPlaying || _paused == paused )
        return;
    
    paused? [self pause]: [self play];
    _paused = paused;
    NSLog(@"外部控制——— 播放暂停：%@", paused?@"YES":@"NO");
}

- (void)setRepeat:(int)repeat
{
    if(_lePlayer == nil || _isPlaying || _isAdPlaying )
        return;
    
    [self play];
    
    NSLog(@"外部控制——— 重播：%@", repeat?@"YES":@"NO");
}


- (void)setSeek:(int)seek
{
    if(seek < 0) return;
    _lastPosition = seek;
    
    if(_lePlayer == nil ) return;
    
    if(_playMode == LCPlayerVod && _duration != 0){
        
        _isSeeking = YES;
        
        if( !_isPlaying && !_isAdPlaying ){
            
            __weak typeof(self) wSelf = self;
            [self playAndSeek:^{
                _lastPosition = seek > _duration ? _duration : seek;
                [wSelf setSeek: _lastPosition];
            }];
        }
        else
        {
            __weak typeof(self) wSelf = self;
            _lastPosition = seek > _duration ? _duration : seek;
            [_lePlayer seekToPosition: seek > _duration ? (int)_duration : seek completion:^{
                _isSeeking = NO;
                wSelf.onVideoSeekComplete?wSelf.onVideoSeekComplete(nil):nil;
                
            }];
        }
        
        
        NSLog(@"外部控制——— SEEK TO: %d", seek);
        
    }else if(_playMode == LCPlayerActionLive){
        
        _isSeeking = YES;
        
        __weak typeof(self) wSelf = self;
        if(seek >= _serverTime){
            _lastPosition = _serverTime;
            [(LECActivityPlayer*)_lePlayer backToLiveWithCompletion:^{
                _isSeeking = NO;
                wSelf.onVideoSeekComplete?wSelf.onVideoSeekComplete(nil):nil;
            }];
        }else{
            _lastPosition = seek < _beginTime ? (int)_beginTime : seek;
            [_lePlayer seekToPosition: seek < _beginTime ? (int)_beginTime : seek completion:^{
                _isSeeking = NO;
                wSelf.onVideoSeekComplete?wSelf.onVideoSeekComplete(nil):nil;
            }];
        }
        NSLog(@"外部控制——— SEEK TIMESHIFT: %d", seek);
    }
    
    _onVideoSeek? _onVideoSeek(@{@"currentTime": [NSNumber numberWithLong:_lastPosition], @"seekTime":[NSNumber numberWithInt:seek]}):nil;
    
}

//回到上次播放位置
- (void)setLastPosModifier:(long)lastPosition
{
    if(lastPosition == 0) return;
    
    _lastPosition = lastPosition;
    
    if (_lePlayer == nil || _duration == 0) {
        return;
    }
    
    if (_playMode == LCPlayerVod && _lastPosition != 0) {
        if (_lastPosition < _duration)
            [_lePlayer seekToPosition:_lastPosition];
        else
            [_lePlayer seekToPosition:_duration];
        
        NSLog(@"外部控制——— 恢复位置 seekToLastPostion: %ld", _lastPosition);
    }
}


- (void)setRate:(NSString*)rate
{
    if( [[self class]isBlankString:rate] || _lePlayer == nil || [_ratesList count]==0)
        return;
    
    for (LECStreamRateItem * lItem in _ratesList){
        if (lItem.isEnabled && [rate isEqualToString:lItem.code]){
            
            _currentRate = rate;
            __weak typeof(self) wSelf = self;
            [wSelf.lePlayer switchSelectStreamRateItem:lItem completion:^{
                wSelf.onVideoRateChange?wSelf.onVideoRateChange(@{@"currentRate":_currentRate,@"nextRate":lItem.code}):nil;
            }];
        }
    }
    NSLog(@"外部控制——— 切换码率 current:%@ next:%@", _currentRate, rate);
}

- (void)setLive:(NSString*)liveId
{
    if( [[self class]isBlankString:liveId] || _lePlayer == nil || _activityItem == nil || [_activityItem.activityLiveItemList count]==0)
        return;
    
    [_lePlayer stop];
    [_lePlayer unregister];
    
    __weak typeof(self) wSelf = self;
    [(LECActivityPlayer*)_lePlayer registerWithLiveId:liveId completion:^(BOOL result) {
        wSelf.onActionLiveChange?wSelf.onActionLiveChange(@{@"currentLive":_currentLive,@"nextLive":liveId}):nil;
    }];
    NSLog(@"外部控制——— 切换机位 current:%@ next:%@", _currentLive, liveId);
}

- (void)setClickAd:(BOOL)isClicked
{
    NSLog(@"外部控制——— 点击广告，iOS暂不支持！");
}

- (void)setVolume:(int)volume
{
    if (volume < 0 || volume > 100) {
        return;
    }
    //    _volume = volume;
    //  _lePlayer.volume = volume;
    [VolumeModule setVolumeValue: (float)volume/100 ];
    
    NSLog(@"外部控制——— 音量调节:%d", volume );
    
}

- (void)setBrightness:(int)brightness
{
    if (brightness < 0 || brightness > 100) {
        return;
    }
    //_currentBrightness = brightness;
    
    [BrightnessModule setBrightnessValue: (float)brightness/100 ];
    //    [[UIScreen mainScreen] setBrightness: (float)brightness / 100];
    
    NSLog(@"外部控制——— 调节亮度:%d", brightness );
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
        
        self.frame  = LCRect_PlayerHalfFrame;
        _lePlayer.videoView.frame               = self.bounds;
        _lePlayer.videoView.autoresizingMask    = UIViewAutoresizingFlexibleLeftMargin| UIViewAutoresizingFlexibleWidth| UIViewAutoresizingFlexibleHeight;
        _lePlayer.videoView.contentMode         = UIViewContentModeScaleAspectFit;
        
        [self addSubview:_lePlayer.videoView];
        [self sendSubviewToBack:_lePlayer.videoView];
        
        
        NSString *uuid = [source objectForKey:@"uuid"];
        NSString *vuid = [source objectForKey:@"vuid"];
        NSString *buinessline = [source objectForKey:@"businessline"];
        bool saas  = [RCTConvert BOOL:[source objectForKey:@"saas"]];
        _repeat    = [RCTConvert BOOL:[source objectForKey:@"repeat"]];
        
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
                                          
                                          //数据源回显
                                          wSelf.onVideoSourceLoad?
                                          wSelf.onVideoSourceLoad(@{@"src": [[wSelf class] returnJSONStringWithDictionary:source useSystem:YES]}):nil;
                                          
                                          if (result){
                                              NSLog(@"播放器注册成功");
                                              _title = ((LECVODPlayer*)(wSelf.lePlayer)).videoTitle;
                                              
                                              [wSelf play];//注册完成后自动播放
                                              
                                          }else{
                                              //[_playerViewController showTips:@"播放器注册失败,请检查UU和VU"];
                                              wSelf.onVideoError?wSelf.onVideoError(@{@"errorCode":@"-1",@"errorMsg":@"播放器注册失败,请检查UU和VU"}):nil;
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
        
        self.frame = LCRect_PlayerHalfFrame;
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
                _title = manager.activityItem.activityName;
                LECActivityItem *aItem = manager.activityItem;
                BOOL isEnable = NO;
                if (aItem.activityLiveItemList.count != 0) {
                    for (LECActivityLiveItem * lItem in aItem.activityLiveItemList) {
                        
                        if (lItem.status == LCActivityLiveStatusUsing) {
                            isEnable = [(LECActivityPlayer*)wSelf.lePlayer registerWithLiveId:lItem.liveId completion:^(BOOL result) {
                                if (result) {
                                    [wSelf play];//自动播放
                                    //                  LECStreamRateItem * lItem = wSelf.lePlayer.selectedStreamRateItem;
                                    //[weakSelf.playerRateBtn setTitle:lItem.name forState:(UIControlStateNormal)];
                                    NSLog(@"活动机位数目:%lu",(unsigned long)aItem.activityLiveItemList.count);
                                }
                                else {
                                    NSString * error = [NSString stringWithFormat:@"%@:%@",
                                                        wSelf.lePlayer.errorCode,
                                                        wSelf.lePlayer.errorDescription];
                                    NSLog(@"%@",error);
                                    wSelf.onVideoError?wSelf.onVideoError(@{@"errorCode":wSelf.lePlayer.errorCode,@"errorMsg":wSelf.lePlayer.errorDescription}):nil;
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
                    wSelf.onVideoError?wSelf.onVideoError(@{@"errorCode":@"-1",@"errorMsg":@"没有可用的直播机位"}):nil;
                }
            } else {
                NSLog(@"直播活动注册失败");
                wSelf.onVideoError?wSelf.onVideoError(@{@"errorCode":@"-1",@"errorMsg":@"直播活动注册失败"}):nil;
            }
        }];
        
        if (!requestSuccess) {
            NSLog(@"活动事件Manager注册失败");
            wSelf.onVideoError?wSelf.onVideoError(@{@"errorCode":@"-1",@"errorMsg":@"活动事件Manager注册失败"}):nil;
        }
        
        
    }else{ //普通URL
        
        NSLog(@"URL数据源");
        
        _playMode = LCPlayerVod;
        
        // 创建播放器
        _lePlayer = [[LECPlayer alloc] init];
        _lePlayer.delegate = self;
        
        self.frame = LCRect_PlayerHalfFrame;
        _lePlayer.videoView.frame = self.bounds;
        _lePlayer.videoView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin| UIViewAutoresizingFlexibleWidth| UIViewAutoresizingFlexibleHeight;
        _lePlayer.videoView.contentMode = UIViewContentModeScaleAspectFit;
        
        [self addSubview:_lePlayer.videoView];
        [self sendSubviewToBack:_lePlayer.videoView];
        
        NSString* url = [source objectForKey:@"uri"];
        _repeat  = [RCTConvert BOOL:[source objectForKey:@"repeat"]];
        
        if (url.length != 0 ) {
            [self usePlayerViewController]; // 创建controller
            _playerViewController.url = url;
        }
        
        __weak typeof(self) wSelf = self;
        [_lePlayer registerWithURLString:url completion:^(BOOL result) {
            //数据源回显
            wSelf.onVideoSourceLoad?wSelf.onVideoSourceLoad(@{@"src": [[wSelf class] returnJSONStringWithDictionary:source useSystem:YES]}):nil;
            
            if (result){
                NSLog(@"播放器注册成功");
                [wSelf play];//注册完成后自动播放
                
            }else{
                wSelf.onVideoError? wSelf.onVideoError(@{@"errorCode":@"-1",@"errorMsg":@"播放器注册失败,请检查URL"}):nil;
            }
        }];
    }
}

- (BOOL)shouldAutorotate
{
    return YES;
}


- (void)applyModifiers
{
    //  [self setResizeMode:_resizeMode];
    //  [self setRepeat:_repeat];
    [self setPaused:_paused];
    [self setLastPosModifier:_lastPosition];
}

/*重置播放器*/
- (void) resetPlayerAndController
{
    if (_lePlayer) {
        [self stop];
        [_lePlayer unregister];
        _lePlayer.delegate = nil;
        _lePlayer = nil;
    }
    _option = nil;
    
    [_playerViewController.view removeFromSuperview];
    _playerViewController = nil;
}

/*重置状态量*/
- (void) initFieldParaStates
{
    _isSeeking = NO;
    
    _title = nil;
    _duration = 0;
    _width = 0;
    _height = 0;
    _cacheDuration = 0;
    _lastPosition = 0;
    _currentRate = @"";
    _currentLive = @"";
    _currentTime = 0;
    _serverTime = 0;
    _beginTime = 0;
    
    _ratesList = nil;
    _activityItem = nil;
    _activityConfigItem = nil;
    _activityLiveItem = nil;
    
}


#pragma mark - 播放控制
- (void)play
{
    if (_isPlaying || _lePlayer == nil ) {
        return;
    }
    __weak typeof(self) wSelf = self;
    [_lePlayer playWithCompletion:^{
        
        if( _playMode == LCPlayerVod)
            wSelf.onVideoResume?wSelf.onVideoResume(@{@"duration":[NSNumber numberWithLong:_duration],
                                                      @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
        else if(_playMode == LCPlayerActionLive)
            wSelf.onVideoResume?wSelf.onVideoResume(@{@"beginTime":[NSNumber numberWithLong:_beginTime],
                                                      @"serverTime":[NSNumber numberWithLong:_serverTime],
                                                      @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
        _paused = NO;
        _isPlaying = YES;
    }];
}

- (void)playAndSeek:(void (^)())completion;
{
    if (_isPlaying || _lePlayer == nil ) {
        return;
    }
    __weak typeof(self) wSelf = self;
    [_lePlayer playWithCompletion:^{
        
        if( _playMode == LCPlayerVod)
            wSelf.onVideoResume?wSelf.onVideoResume(@{@"duration":[NSNumber numberWithLong:_duration],
                                                      @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
        else if(_playMode == LCPlayerActionLive)
            wSelf.onVideoResume?wSelf.onVideoResume(@{@"beginTime":[NSNumber numberWithLong:_beginTime],
                                                      @"serverTime":[NSNumber numberWithLong:_serverTime],
                                                      @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
        _paused = NO;
        _isPlaying = YES;
                
        completion();
    }];
}



- (void)resume
{
    if (_isPlaying || _lePlayer == nil) {
        return;
    }
    
    [_lePlayer resume];
    
    
    if( _playMode == LCPlayerVod)
        _onVideoResume?_onVideoResume(@{@"duration":[NSNumber numberWithLong:_duration],
                                        @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
    else if(_playMode == LCPlayerActionLive)
        _onVideoResume?_onVideoResume(@{@"beginTime":[NSNumber numberWithLong:_beginTime],
                                        @"serverTime":[NSNumber numberWithLong:_serverTime],
                                        @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
    _paused = NO;
    _isPlaying = YES;
}

- (void)stop
{
    if (!_isPlaying || _lePlayer == nil){
        return;
    }
    __weak typeof(self) wSelf = self;
    [_lePlayer stopWithCompletion:^{
        _isPlaying = NO;
    }];
}

- (void)pause
{
    if (!_isPlaying || _lePlayer == nil){
        return;
    }
    
    [_lePlayer pause];
    
    if( _playMode == LCPlayerVod)
        _onVideoPause?_onVideoPause(@{@"duration":[NSNumber numberWithLong:_duration],
                                      @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
    
    else if(_playMode == LCPlayerActionLive)
        _onVideoPause?_onVideoPause(@{@"beginTime":[NSNumber numberWithLong:_beginTime],
                                      @"serverTime":[NSNumber numberWithLong:_serverTime],
                                      @"currentTime":[NSNumber numberWithLong:_currentTime],}):nil;
    _paused = YES;
    _isPlaying = NO;
}

#pragma mark - 事件处理
- (void) processPrepared:(LECPlayer *) player
             playerEvent:(LECPlayerPlayEvent) playerEvent
{
    NSLog(@"Prepared Event!");
    
    if( [player isMemberOfClass: [LECVODPlayer class]] || [player isMemberOfClass: [LECPlayer class]] ){ //点播模式
        _playMode = LCPlayerVod;
    }else if( [player isMemberOfClass: [LECActivityPlayer class]] ){ //活动模式
        _playMode = LCPlayerActionLive;
    }
    
    //当前播放模式, 当前屏幕方向
    NSMutableDictionary *event = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                  [NSNumber numberWithInt:_playMode],@"playMode",
                                  [NSNumber numberWithInt:_currentOritentation],@"orientation", nil];
    
    //视频基本信息，长/宽/方向
    _width = player.actualVideoWidth;
    _height = player.actualVideoHeight;
    [event setValue:@{@"width":[NSNumber numberWithInt:_width],
                      @"height":[NSNumber numberWithInt:_height],
                      @"videoOrientation":(_width>_height)? @"landscape" : @"portrait"}
             forKey:@"naturalSize"];
    
    [event setValue:_title forKey:@"title"];
    
    //视频码率信息
    if(_playMode == LCPlayerVod)
        _ratesList = ((LECVODPlayer*)player).streamRatesList;
    else if(_playMode == LCPlayerActionLive)
        _ratesList = ((LECActivityPlayer*)player).streamRatesList;
    
    
    if(_ratesList && [_ratesList count] > 0 ){
        NSMutableArray *ratesList = [NSMutableArray arrayWithCapacity: [_ratesList count]];
        for(LECStreamRateItem *element in _ratesList){
            if((NSNull *)element != [NSNull null] && element.isEnabled){
                [ratesList addObject: @{@"rateKey": element.code, @"rateValue": element.name }];
            }
        }
        [event setValue:ratesList forKey:@"rateList"]; //可用码率
    }
    LECStreamRateItem * lItem = player.selectedStreamRateItem;
    if( lItem ){
        _currentRate = _defaultRate = lItem.code;
        [event setValue:_defaultRate forKey:@"defaultRate"]; //默认码率
        [event setValue:_currentRate forKey:@"currentRate"]; //当前码率
    }
    
    // 视频封面信息: 加载
    if(_playMode == LCPlayerVod){
        if ( [player isMemberOfClass:[LECVODPlayer class]] && ((LECVODPlayer*)player).loadingIconUrl) {
            [event setValue:@{@"pic": ((LECVODPlayer*)player).loadingIconUrl} forKey:@"loading"];  // LOADING信息
        }
    }else if(_playMode == LCPlayerActionLive){
        if (((LECActivityPlayer*)player).loadingIconUrl) {
            [event setValue:@{@"pic": ((LECActivityPlayer*)player).loadingIconUrl} forKey:@"loading"];  // LOADING信息
        }
    }
    
    if (_playMode == LCPlayerVod) { //VOD模式下参数
        [event setValue:[NSNumber numberWithLong:(_duration==0)?player.duration:_duration] forKey:@"duration"]; //视频总长度（VOD）
        [event setValue:[NSNumber numberWithLong:_lastPosition] forKey:@"currentTime"]; //当前播放位置（VOD）
        [event setValue:[NSNumber numberWithBool:[((LECVODPlayer*)player) respondsToSelector:@selector(isPanorama)]?
                         ((LECVODPlayer*)player).isPanorama:NO] forKey:@"isPano"]; //是否全景（VOD）
        [event setValue:[NSNumber numberWithBool:[((LECVODPlayer*)player) respondsToSelector:@selector(allowDownload)]?
                         ((LECVODPlayer*)player).allowDownload:NO] forKey:@"isDownload"]; //是否可以下载（VOD）
        
    } else if(_playMode == LCPlayerActionLive ) { //ACTION模式参数
        
        NSMutableDictionary* actionLive = [NSMutableDictionary dictionaryWithCapacity:13];
        if(_activityItem){ //直播数据
            [actionLive setValue:_activityItem.activityName?_activityItem.activityName:[NSNull null] forKey:@"title"];
            [actionLive setValue:_activityItem.activityCoverImage?_activityItem.activityCoverImage:[NSNull null] forKey:@"coverImgUrl"];
            [actionLive setValue:_activityItem.activityWebUrl?_activityItem.activityWebUrl:[NSNull null] forKey:@"playerPageUrl"];
            [actionLive setValue:[NSNumber numberWithInt:_activityItem.status] forKey:@"actionState"];
            [actionLive setValue:[NSNumber numberWithInt:(int)_activityItem.beginTime?_activityItem.beginTime:0] forKey:@"beginTime"];
            [actionLive setValue:[NSNumber numberWithInt:(int)_activityItem.endTime?_activityItem.endTime:0] forKey:@"endTime"];
            [actionLive setValue:_activityItem.activityDesc?_activityItem.activityDesc:[NSNull null] forKey:@"actionDesc"];
            [actionLive setValue:[NSNumber numberWithBool:((LECActivityPlayer*)player).isPanorama] forKey:@"isPano"]; //是否全景（LIVE）
            [actionLive setValue:[NSNumber numberWithBool:((LECActivityPlayer*)player).supportSeekOperation] forKey:@"needTimeShift"]; //是否支持timeshift（LIVE）
            [actionLive setValue:[NSNumber numberWithBool:((LECActivityPlayer*)player).contentType == LECPlayerContentTypeAdv] forKey:@"isNeedAd"]; //是否广告（LIVE）
            
            NSArray* liveInfos =  _activityItem.activityLiveItemList;
            NSMutableArray* lives = [NSMutableArray arrayWithCapacity:[liveInfos count]];
            for(LECActivityLiveItem *element in liveInfos){
                if((NSNull *)element != [NSNull null]){
                    [lives addObject: @{@"liveId": element.liveId? element.liveId:[NSNull null],
                                        @"machine": [NSNumber numberWithInt:(int)element.livePositionNumber],
                                        @"previewSteamId": element.previewStreamId?element.previewStreamId:[NSNull null],
                                        @"previewSteamPlayUrl": element.previewRTMPUrl?element.previewRTMPUrl:[NSNull null],
                                        @"liveStatus": [NSNumber numberWithInt:(int)element.status]}];
                    
                    if(element.status == LCActivityLiveStatusUsing){ //默认机位
                        _activityLiveItem = element;
                        _currentLive = element.liveId;
                        [actionLive setValue:element.liveId forKey:@"currentLive"];
                    }
                }
                [actionLive setValue:lives forKey:@"lives"];
            }
            
            [event setValue:actionLive forKey:@"actionLive"];
        }
        
        if(_activityConfigItem){ //封面.logo.loading.水印
            [event setValue:@{@"pic":_activityConfigItem.logoUrl?_activityConfigItem.logoUrl:[NSNull null]} forKey:@"logo"];
            [event setValue:@{@"pic":_activityConfigItem.loadingIconUrl?_activityConfigItem.loadingIconUrl:[NSNull null]} forKey:@"loading"];
            [event setValue:@{@"pic":_activityConfigItem.waterMarkUrl?_activityConfigItem.waterMarkUrl:[NSNull null],
                              @"pos": [NSNumber numberWithInt:(int)_activityConfigItem.waterMarkPosition?
                                       _activityConfigItem.waterMarkPosition:0]} forKey:@"waterMarks"];
        }
    }
    
    // 设备信息： 音量和亮度
    //  _volume = player.volume;
    // retrieve system volume
    _originVolume = [VolumeModule getVolumeValue] * 100;
    _originBrightness = [BrightnessModule getBrightnessValue] *100 ;
    
    //    _currentBrightness = _screenBrightness;
    
    [event setValue:[NSNumber numberWithInt:_originVolume] forKey:@"volume"]; //声音百分比
    [event setValue:[NSNumber numberWithInt:_originBrightness] forKey:@"brightness"]; //屏幕亮度
    
    _onVideoLoad?_onVideoLoad(event):nil;
    
    [self applyModifiers];
    
}

/*播放完成*/
- (void) processCompleted:(LECPlayer *) player
              playerEvent:(LECPlayerPlayEvent) playerEvent
{
    _isPlaying = NO;
    _onVideoEnd?_onVideoEnd(nil):nil;
    
    _repeat?[self play] : nil;
    
}

/*缓冲事件*/
- (void) processPlayerInfo:(LECPlayer *) player
               playerEvent:(LECPlayerPlayEvent) playerEvent
{
    switch (playerEvent) {
        case LECPlayerPlayEventBufferStart:
            _isSeeking = YES;
            _onBufferStart?_onBufferStart(nil):nil;
            break;
        case LECPlayerPlayEventRenderFirstPic:
            _isSeeking = NO;
            _onVideoRendingStart?_onVideoRendingStart(nil):nil;
            break;
        case LECPlayerPlayEventBufferEnd:
            _onBufferEnd?_onBufferEnd(nil):nil;
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
    _onVideoSizeChange?_onVideoSizeChange(@{@"width": [NSNumber numberWithInt:_width],@"height": [NSNumber numberWithInt:_height],}):nil;
    
}


/*SEEK完成*/
- (void) processSeekComplete:(LECPlayer *) player
                 playerEvent:(LECPlayerPlayEvent) playerEvent
{
    _isSeeking = NO;
    _onVideoSeekComplete?_onVideoSeekComplete(nil):nil;
}

/*播放出错*/
- (void) processError:(LECPlayer *) player
          playerEvent:(LECPlayerPlayEvent) playerEvent
{
    NSString * error = [NSString stringWithFormat:@"%@:%@",player.errorCode,player.errorDescription];
    NSLog(@"播放器错误:%@",error);
    //[_playerViewController showTips:error]; //弹出提示
    _onVideoError?_onVideoError(@{@"errorCode": player.errorCode,@"errorMsg": player.errorDescription}):nil;
    
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
    //  _volume = [VolumeModule getVolumeLevel] * 100;
    
    if(_playMode == LCPlayerVod){
        _currentTime = _lastPosition = position;
        _duration = duration;
        _cacheDuration = cacheDuration;
        
        _onVideoProgress?_onVideoProgress(@{@"currentTime": [NSNumber numberWithLong:_currentTime],
                                            @"duration": [NSNumber numberWithLong:_duration],
                                            @"playableDuration": [NSNumber numberWithLong:_cacheDuration],}):nil;
        _onVideoBufferPercent?
        _onVideoBufferPercent(@{@"bufferpercent": [NSNumber numberWithInt:(int) ((((float)position + (float)cacheDuration)/(float)duration) * 100) ]}):nil;
        
    }else if(_playMode == LCPlayerActionLive){
        _currentTime = _lastPosition = ((LECActivityPlayer*)player).currentPlayTimestamp;
        _beginTime = ((LECActivityPlayer*)player).streamStartTimestamp;
        _serverTime = ((LECActivityPlayer*)player).serverRealTimestamp;
        _endTime = ((LECActivityPlayer*)player).streamEndTimestamp;
        
        _onActionTimeShift?_onActionTimeShift(@{@"currentTime": [NSNumber numberWithLong:_currentTime],
                                                @"beginTime": [NSNumber numberWithLong:_beginTime],
                                                @"serverTime": [NSNumber numberWithLong:_serverTime],
                                                @"endTime": [NSNumber numberWithLong:_endTime], }):nil;
        
    }
    //  NSLog(@"播放位置:%lld,缓冲位置:%lld,总时长:%lld",position,cacheDuration,duration);
}

/*直播事件*/
- (void) activityManager:(LECActivityInfoManager *) manager event:(LCActivityEvent) event {
    switch (event) {
        case LCActivityEventActivityConfigUpdate: {
            
            _activityItem =  manager.activityItem;
            _activityConfigItem =  manager.activityConfigItem;
            
            int actionStatus = manager.activityItem.status;
            NSString *errorMsg;
            switch (actionStatus) {
                case LECActivityStatusUnStarted:
                    errorMsg = @"活动未开始";
                    break;
                case LECActivityStatusLiving:
                    errorMsg = @"活动正在直播";
                    break;
                case LECActivityStatusSuspending:
                    errorMsg = @"活动直播中断";
                    break;
                case LECActivityStatusEnd:
                    errorMsg = @"活动直播结束";
                    break;
                default:
                    errorMsg = @"未知状态";
                    break;
            }
            
            if(_onActionStatusChange){
                _beginTime = manager.activityItem.beginTime;
                _endTime = manager.activityItem.endTime;
                _onActionStatusChange(@{@"actionState": [NSNumber numberWithInt:(int)manager.activityItem.status],
                                        @"actionId": manager.activityId,
                                        @"beginTime": [NSNumber numberWithLong:_beginTime],
                                        @"endTime": [NSNumber numberWithLong:_endTime],
                                        @"errorMsg": errorMsg});
            }
            NSLog(@"活动状态变化:%d,",(int)manager.activityItem.status);
            break;
        }
        case LCActivityEventOnlineAudiencesNumberUpdate: {
            
            _onActionOnlineNumChange?_onActionOnlineNumChange(@{@"onlineNum": [NSNumber numberWithLong:(long)manager.onlineAudiencesNumber] }):nil;
            
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
            if(_isAdPlaying){
                _isAdPlaying = NO;
                _onAdvertComplete?_onAdvertComplete(nil):nil;
            }
            break;
        case LECPlayerContentTypeAdv:
            NSLog(@"正在播放广告");
            //      [_loadIndicatorView stopAnimating];
            _isAdPlaying = YES;
            _onAdvertStart?_onAdvertStart(nil):nil;
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
        //    [self addSubview:_playerViewController.view];
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
    
    if( _lePlayer ){
        [self stop];
        
        [_lePlayer unregister];
        _lePlayer.delegate = nil;
        _lePlayer = nil;
    }
    
    _brigtnessModule = nil;
    _option = nil;
    
    [_playerViewController.view removeFromSuperview];
    _playerViewController = nil;
    
    
    if(_playMode == LCPlayerActionLive){
        [[LECActivityInfoManager sharedManager] releaseActivity];
    }
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    //  [[UIDevice currentDevice]endGeneratingDeviceOrientationNotifications];
    
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
