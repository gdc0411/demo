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
    __block int  _pushState;  //PUSH状态
    __block int  _pushTime; //推流计时
    __block BOOL _pushTimeFlag; //推流计时是否开始
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
    
    BOOL _initedValid; //参数初始化状态
    BOOL _pushFlag; //推流是否关闭
    
    int _pushType; //当前推流类型
    BOOL _isLandscape; //当前屏幕方向是否横向
    BOOL _isFrontCamera; //摄像头是否为前置
    
    BOOL _isTorch; //闪光灯设置状态
    
    NSDictionary *_pushPara; //推流参数
    NSString *_pushUrl; //推流地址
    NSString *_playUrl; //播放地址
    
    NSTimer *_timer; //计时器
    
    BOOL _flashFlag;  //是否打开闪光灯
    BOOL _switchFlag; //是否正在切换摄像头
    LCCamareOrientationState _camerOrientation; //摄像头方向
    int  _filterModel; //当前滤镜选择
    int  _volume; //音量设置
    
    BOOL _fullscreenPresented; //是否全屏
}

#pragma mark 初始化和销毁
/*实例化桥*/
- (instancetype)initWithBridge:(RCTBridge *)bridge
{
    NSLog(@"初始化桥……");
    if ((self = [super init])) {
        _bridge = bridge;
        
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


/*销毁*/
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

RCT_NOT_IMPLEMENTED(- (instancetype)init)


#pragma mark 创建viewController
- (RCTLePushViewController*)createPushViewController:(LCStreamingManager*) manager {
    
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
    NSLog(@"外部控制——— 推流参数: %@", bundle);
    if(bundle == nil || bundle.count == 0 || [RCTConvert int:[bundle objectForKey:@"type"]] == PUSH_TYPE_NONE ) return;
    
    //重置播放器
    [self resetViewAndController];
    
    //重置所有状态
    [self initFieldParaStates];
    
    //根据必要参数创建推流端
    [self pushItemForTarget:bundle];
    
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        sleep(REACT_JS_EVENT_WAIT);
        self.onPushTargetLoad? self.onPushTargetLoad(@{@"para": [[self class] returnJSONStringWithDictionary:_pushPara useSystem:YES],
                                                       @"playUrl": _playUrl?_playUrl:[NSNull null],
                                                       @"pushUrl": _pushUrl?_pushUrl:[NSNull null],
                                                       @"canTorch": [NSNumber numberWithBool:!_isFrontCamera],}):nil;});
}




/*重置播放器*/
- (void) resetViewAndController
{
    if (_initedValid) {
        
        UIView *subview = [self viewWithTag:PushViewTag];
        subview?[subview removeFromSuperview]:nil;
        
        [_manager stopStreaming];
        [_manager cleanSession];
        _manager.delegate = nil;
        
        
        if(_pushViewController){
            _pushViewController.viewControllerDelegate = nil;
            _pushViewController = nil;
        }
    }
}

/*重置状态量*/
- (void) initFieldParaStates
{
    _initedValid = NO;
    
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
    
    _pushType        = [RCTConvert int:[bundle objectForKey:@"type"]];
    _isLandscape     = [RCTConvert BOOL:[bundle objectForKey:@"landscape"]];
    _isFrontCamera   = [RCTConvert BOOL:[bundle objectForKey:@"frontCamera"]];
    _isTorch         = [RCTConvert BOOL:[bundle objectForKey:@"torch"]];
    BOOL isFocus     = [RCTConvert BOOL:[bundle objectForKey:@"focus"]];
    
    if (!_manager) {
        _manager = [[LCStreamingManager alloc] init];
        _manager.delegate = self;
        //        _manager = [LCStreamingManager sharedManager];
    }
    
    //配置推流正方
    _manager.pushOrientation = _isLandscape? UIInterfaceOrientationLandscapeRight: UIInterfaceOrientationPortrait;
    
    self.frame = LCRect_PlayerFullFrame; //全屏预览
    CGSize size = UIInterfaceOrientationLandscapeRight == _manager.pushOrientation ?
    CGSizeMake(self.bounds.size.height,self.bounds.size.width) :
    CGSizeMake(self.bounds.size.width, self.bounds.size.height);
    
    //配置推流参数
    [_manager configVCSessionWithVideoSize:size
                                 frameRate:24
                                   bitrate:1000000
                   useInterfaceOrientation:YES];
    
    //配置预览视图的frame
    [_manager configVideoViewFrame:self.bounds]; //默认占满整个屏幕
    [_manager enableManulFocus:isFocus]; //设置是否对焦
    
    _camerOrientation = _isFrontCamera? LCCamareOrientationStateFront : LCCamareOrientationStateBack;
    [_manager setCamareOrientationState:_camerOrientation]; //设置摄像头方向
    
    
    [self usePushViewController:_manager]; // 创建controller
    
    if(_pushType == PUSH_TYPE_MOBILE_URI){ //移动直播有地址
        
        _pushUrl  = [bundle objectForKey:@"url"];
        _playUrl  = _pushUrl?[_pushUrl stringByReplacingOccurrencesOfString:@"push" withString:@"pull"]:nil;
        
    }else if(_pushType == PUSH_TYPE_MOBILE){ //移动直播无地址
        
        NSString *streamName = [bundle objectForKey:@"streamName"];
        NSString *domainName = [bundle objectForKey:@"domainName"];
        NSString *appkey     = [bundle objectForKey:@"appkey"];
        
        _pushUrl =  [self rtmpAddressWithDomain:[domainName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]
                                     streamName:[streamName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]
                                         appKey:[NSString stringWithFormat:@"%@lecloud", [appkey stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]]];
        
        _playUrl  = _pushUrl?[_pushUrl stringByReplacingOccurrencesOfString:@"push" withString:@"pull"]:nil;
        
    }else if(_pushType == PUSH_TYPE_LECLOUD){ //云直播
//        NSString *activityId    = [bundle objectForKey:@"activityId"];
//        NSString *userId        = [bundle objectForKey:@"userId"];
//        NSString *secretKey     = [bundle objectForKey:@"secretKey"];
    }
    
    _initedValid = YES;
    
}


- (void)setPush:(BOOL)push
{
    if (!_initedValid || _pushFlag == push || _pushPara == nil) {
        return;
    }
    NSLog(@"外部控制——— 开始/停止推流: %@", push?@"YES":@"NO");
    
    [self setPushedModifier:push];
}

/** 设置推送开始或停止*/
- (void) setPushedModifier:(BOOL) pushed {
    
    _pushFlag = pushed;
    
    //    if (!_lePushValid) {
    //        return;
    //    }
    
    if (_pushFlag) { //启动推流
        if (PUSH_STATE_OPENED != _pushState && _initedValid ) {//初始化已完成，未推流状态

            __weak typeof(self) wSelf = self;
            [self notifyEventWithState:PUSH_STATE_CONNECTING code:0 msg:@"正在连接……" complete:^(){
                
                _pushTime = 0;
                
                if (_pushType == PUSH_TYPE_MOBILE_URI || _pushType == PUSH_TYPE_MOBILE) { //移动直播
                    
                    [wSelf.manager startStreamingWithRtmpAdress:_pushUrl];
                    
                } else if (_pushType == PUSH_TYPE_LECLOUD) { //乐视云直播
                    
                    NSString *activityId    = [_pushPara objectForKey:@"activityId"];
                    NSString *userId        = [_pushPara objectForKey:@"userId"];
                    NSString *secretKey     = [_pushPara objectForKey:@"secretKey"];
                    
                    __weak typeof(self) wSelf2 = wSelf;
                    [wSelf.manager requestVidiconInfoWithID:activityId
                                                userId:userId
                                             secretKey:secretKey
                                             completed:^(BOOL isSuccess, NSArray *items, NSString *errorCode, NSString *errorMsg) {
                                                 if (isSuccess) {
                                                     int i = 0;
                                                     for (; i < items.count; i ++) {
                                                         LCVidiconItem *vidiconItem = items[i];
                                                         if (vidiconItem.enable) {
                                                             [wSelf2.manager startStreamingWithLCVidiconItem:vidiconItem];
                                                             NSLog(@"success");
                                                             [wSelf2 notifyEventWithState:PUSH_STATE_OPENED code:0 msg:@"推流已打开" complete:^(){
                                                                 if (!_pushTimeFlag && _timer == nil) {
                                                                     _timer = [NSTimer scheduledTimerWithTimeInterval:1.0
                                                                                                               target:self
                                                                                                             selector:@selector(scrollTimer)
                                                                                                             userInfo:nil
                                                                                                              repeats:YES];
                                                                 }
                                                                 _pushTimeFlag = YES;
                                                             }];
                                                             break;
                                                         }
                                                     }
                                                     if (i == items.count) {
                                                         NSLog(@"无可用机位");
                                                         [self notifyEventWithState:PUSH_STATE_CLOSED code:-1
                                                                                msg:[NSString stringWithFormat:@"无可用机位:%d，%@",errorCode,errorMsg]
                                                                           complete:^(){
                                                                               if( _timer ){
                                                                                   [_timer invalidate];
                                                                                   _timer = nil;
                                                                               }
                                                                               _pushTimeFlag = NO;
                                                                           }];
                                                     }
                                                 } else {
                                                     NSLog(@"error");
                                                     NSLog(@"出错了！%@", errorCode);
                                                     [self notifyEventWithState:PUSH_STATE_CLOSED code:-1
                                                                            msg:[NSString stringWithFormat:@"出错:%d，%@",errorCode,errorMsg]
                                                                       complete:^(){
                                                         if( _timer ){
                                                             [_timer invalidate];
                                                             _timer = nil;
                                                         }
                                                         _pushTimeFlag = NO;
                                                     }];
                                                 }
                                             }];
                }    
            }];
            
        } else if (PUSH_STATE_OPENED == _pushState) {//正在推送，不做处理
            [self notifyEventWithState:PUSH_STATE_OPENED code:0 msg:@"无需重复推流!" complete:nil];
            
        } else if (!_initedValid) {//初始化未完成，无法推流
            [self notifyEventWithState:PUSH_STATE_ERROR code:-1 msg:@"初始化未完成，无法推流！" complete:nil];
        }
        
    } else { //关闭推流
        if (PUSH_STATE_OPENED == _pushState) { //正在推流，停止推流
            __weak typeof(self) wSelf = self;
            [self notifyEventWithState:PUSH_STATE_DISCONNECTING code:0 msg:@"正在断开……" complete:^(){
                [wSelf.manager stopStreaming];//结束推流
            }];
            
        } else { //未推送，不做处理
            [self notifyEventWithState:PUSH_STATE_CLOSED code:0 msg:@"无推流，无需关闭！" complete:nil];
        }
    }
}


- (void) notifyEventWithState:(int) pushState
                code:(int) errCode
                 msg:(NSString*) errMsg
             complete:(void (^)()) handler
{
    NSLog(@"%@", errMsg);
    _pushState = pushState;
    
    self.onPushStateUpdate? self.onPushStateUpdate(@{@"state": [NSNumber numberWithInt:pushState],
                                                     @"errorCode": [NSNumber numberWithInt:errCode],
                                                     @"errorMsg": errMsg? errMsg: [NSNull null],}):nil;
    
    //    sleep(5);
    handler? handler():nil;
}



- (void)setCamera:(int)times
{
    if (!_initedValid || times == 0) {
        return;
    }
    NSLog(@"外部控制——— 切换摄像头方向");
    
    _switchFlag = YES;
    if(_manager){
        if( _camerOrientation == LCCamareOrientationStateFront){
            _camerOrientation = LCCamareOrientationStateBack;
            _isFrontCamera = NO;
        }else if(_camerOrientation==LCCamareOrientationStateBack){
            _camerOrientation = LCCamareOrientationStateFront;
            _isFrontCamera = YES;
            
            if(_flashFlag){//切换前置摄像头会关闭闪光灯
                _flashFlag = NO;
                
                
                self.onPushFlashUpdate? self.onPushFlashUpdate(@{}):nil;
                
            }
        }
        //摄像头方向
        [_manager setCamareOrientationState:_camerOrientation];
    }
    
    _switchFlag = NO;
    self.onPushCameraUpdate?self.onPushCameraUpdate(@{@"cameraDirection": [NSNumber numberWithInt:_camerOrientation],
                                                      @"frontCamera":[NSNumber numberWithBool:_isFrontCamera],
                                                      @"cameraFlag":[NSNumber numberWithBool:_switchFlag],
                                                      @"errorCode":[NSNumber numberWithInt:0],
                                                      @"errorMsg":@"摄像头切换完毕",
                                                      @"canTorch": [NSNumber numberWithBool:!_isFrontCamera]}):nil;
}


- (void)setFlash:(BOOL)flash
{
    if (!_initedValid || _flashFlag == flash || _pushPara == nil) {
        return;
    }
    NSLog(@"外部控制——— 开始/关闭闪光灯:", flash?@"YES":@"NO");
    
    NSMutableDictionary *event = [NSMutableDictionary dictionary];
    if(!_isFrontCamera){ //后置摄像头
        _flashFlag = flash;
        [_manager setTorchOpenState:_flashFlag];
        
        [event setValue:[NSNumber numberWithInt:0] forKey:@"errorCode"];
        if(_flashFlag){
            [event setValue:@"闪光灯已打开" forKey:@"errorMsg"];
        }else{
            [event setValue:@"闪光灯已关闭" forKey:@"errorMsg"];
        }
        
    }else{
        [event setValue:[NSNumber numberWithInt:-1] forKey:@"errorCode"];
        [event setValue:@"前置摄像头无法打开闪光灯" forKey:@"errorMsg"];
    }
    
    [event setValue:[NSNumber numberWithBool:_flashFlag] forKey:@"flashFlag"];
    self.onPushFlashUpdate? self.onPushFlashUpdate(event):nil;
    
}

- (void)setFilter:(int)filterModel
{
    if (!_initedValid || _filterModel == filterModel || _pushPara == nil) {
        return;
    }
    NSLog(@"外部控制——— 设置滤镜: %d", filterModel);
    
    NSMutableDictionary *event = [NSMutableDictionary dictionary];
    if (filterModel == LCVideoFilterNone
        || filterModel == LCVideoFilterBeautyFace
        || filterModel == LCVideoFilterWarm
        || filterModel == LCVideoFilterCalm
        || filterModel == LCVideoFilterRomantic) {
        
        _filterModel = filterModel;
        
        [_manager setFilter:filterModel];
        
        [event setValue:[NSNumber numberWithInt:0] forKey:@"errorCode"];
        [event setValue:@"切换滤镜成功" forKey:@"errorMsg"];
        
    } else {
        [event setValue:[NSNumber numberWithInt:-1] forKey:@"errorCode"];
        [event setValue:@"滤镜参数错误!" forKey:@"errorMsg"];
    }
    
    [event setValue:[NSNumber numberWithInt:_filterModel] forKey:@"filter"];
    self.onPushFilterUpdate? self.onPushFilterUpdate(event):nil;
    
}


- (void)setVolume:(int)volume
{
    if (!_initedValid || _volume == volume || _pushPara == nil) {
        return;
    }
    NSLog(@"外部控制——— 设置音量: %d", volume);
    
    _volume = volume;
    
    NSMutableDictionary *event = [NSMutableDictionary dictionary];
    [_manager setMute:[NSNumber numberWithBool:_volume]]; //设置音量
    
    [event setValue:[NSNumber numberWithInt:0] forKey:@"errorCode"];
    [event setValue:@"设置音量成功" forKey:@"errorMsg"];
    [event setValue:[NSNumber numberWithInt:_volume] forKey:@"volume"];
    
    self.onPushVolumeUpdate? self.onPushVolumeUpdate(event):nil;
}


- (void)usePushViewController:(LCStreamingManager*) manager
{
    if( manager ){
        _pushViewController = [self createPushViewController: manager];
    }
}


#pragma mark - LCStreamingManagerDelegate

//推流状态变化通知
- (void)connectionStatusChanged:(LCStreamingSessionState)sessionState
{
    if( sessionState == LCStreamingSessionStateStarted ){
        [_manager setGain:1.3];
        [self notifyEventWithState:PUSH_STATE_OPENED code:0 msg:@"推流已打开" complete:^(){
            if (!_pushTimeFlag && _timer == nil) {
                _timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(scrollTimer) userInfo:nil repeats:YES];
            }
            _pushTimeFlag = YES;
        }];
        
    } else if(sessionState == LCStreamingSessionStateStarting ){
        [self notifyEventWithState:PUSH_STATE_CONNECTING code:0 msg:@"正在连接……" complete:nil];
        
    } else if(sessionState == LCStreamingSessionStatePreviewStarted || sessionState == LCStreamingSessionStateNone ){
        [self notifyEventWithState:PUSH_STATE_CLOSED code:0 msg:@"" complete:nil];
        
    } else if(sessionState == LCStreamingSessionStateEnded ){
        [self notifyEventWithState:PUSH_STATE_CLOSED code:0 msg:@"推流已关闭" complete:^(){
            if( _timer ){
                [_timer invalidate];
                _timer = nil;
            }
            _pushTimeFlag = NO;
        }];
        
    } else if(sessionState == LCStreamingSessionStateError ){
        
        [self notifyEventWithState:PUSH_STATE_CLOSED code:-1 msg:@"出现错误" complete:^(){
            if( _timer ){
                [_timer invalidate];
                _timer = nil;
            }
            _pushTimeFlag = NO;
        }];
    }
}


- (void) scrollTimer
{
    if ( _initedValid && PUSH_STATE_OPENED == _pushState) {
        _pushTime++;
        
        self.onPushTimeUpdate? self.onPushTimeUpdate(@{@"timeFlag":[NSNumber numberWithBool: _pushTimeFlag],
                                                       @"time":[NSNumber numberWithInt: _pushTime],}):nil;
    }
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
    _pushViewController.view.frame = self.bounds;
    
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
