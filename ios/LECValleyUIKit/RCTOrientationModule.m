//
//  OrientationModule.m
//  LeDemo
//
//  Created by RaoJia on 2016/12/11.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "RCTOrientationModule.h"

#import <UIKit/UIKit.h>

#define EVENT_ORIENTATION_CHANG          @"onOrientationDidChange"
#define ORIENTATION_LANDSCAPE            0
#define ORIENTATION_PORTRAIT             1
#define ORIENTATION_REVERSE_LANDSCAPE    8
#define ORIENTATION_REVERSE_PORTRAIT     9
#define ORIENTATION_UNSPECIFIED          -1

@interface RCTOrientationModule()
{
  __block BOOL _isRotating;
  BOOL _isFullScreen;
}
@end

@implementation RCTOrientationModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();


- (NSDictionary *)constantsToExport
{
    
    UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
    NSString *orientationStr = [self getOrientationStr:orientation];
    
    return @{@"initialOrientation"             : orientationStr,
             @"EVENT_ORIENTATION_CHANG"        : EVENT_ORIENTATION_CHANG,
             @"ORIENTATION_LANDSCAPE"          : @(ORIENTATION_LANDSCAPE),
             @"ORIENTATION_PORTRAIT"           : @(ORIENTATION_PORTRAIT),
             @"ORIENTATION_REVERSE_LANDSCAPE"  : @(ORIENTATION_REVERSE_LANDSCAPE),
             @"ORIENTATION_REVERSE_PORTRAIT"   : @(ORIENTATION_REVERSE_PORTRAIT),
             @"ORIENTATION_UNSPECIFIED"        : @(ORIENTATION_UNSPECIFIED)};
}

static UIInterfaceOrientationMask _orientation = UIInterfaceOrientationMaskAllButUpsideDown;

+ (void)setOrientation: (UIInterfaceOrientationMask)orientation {
  _orientation = orientation;
}
+ (UIInterfaceOrientationMask)getOrientation {
  return _orientation;
}


- (instancetype)init
{
  if ((self = [super init])) {
      
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(deviceOrientationDidChange:)
                                                 name:@"UIDeviceOrientationDidChangeNotification"
                                               object:nil];
  }
  return self;
  
}

- (void)dealloc
{
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[EVENT_ORIENTATION_CHANG];
}

- (void)deviceOrientationDidChange:(NSNotification *)notification
{
  UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
  
  if( _isRotating ||
     (orientation != UIDeviceOrientationPortrait
      && orientation != UIDeviceOrientationLandscapeLeft
      && orientation != UIDeviceOrientationLandscapeRight
      && orientation != UIDeviceOrientationPortraitUpsideDown)) return;
  
  int orientationInt = [self getOrientationInt:orientation];
  NSString* orientationStr = [self getOrientationStr:orientation];
  
  [self sendEventWithName:EVENT_ORIENTATION_CHANG
                     body:@{@"orientation": [NSNumber numberWithInt:orientationInt],
                            @"orientationStr": orientationStr}];
  
  NSLog(@"设备转屏事件——— orientation：%d 描述: %@", orientationInt, orientationStr);
}

- (NSString *)getOrientationStr: (UIDeviceOrientation)orientation {
  NSString *orientationStr;
  switch (orientation) {
    case UIDeviceOrientationPortrait:
      orientationStr = @"PORTRAIT";
      break;
      
    case UIDeviceOrientationLandscapeLeft:
      orientationStr = @"LANDSCAPE-LEFT";
      break;
      
    case UIDeviceOrientationLandscapeRight:
      orientationStr = @"LANDSCAPE-RIGHT";
      break;
      
    case UIDeviceOrientationPortraitUpsideDown:
      orientationStr = @"PORTRAITUPSIDEDOWN";
      break;
      
    default:
      orientationStr = @"UNKNOWN";
      break;
  }
  return orientationStr;
}


- (int)getOrientationInt: (UIDeviceOrientation)orientation {
  int orientationInt;
  switch (orientation) {
    case UIDeviceOrientationPortrait:
      orientationInt = 1;
      break;
      
    case UIDeviceOrientationLandscapeLeft:
      orientationInt = 0;
      break;
      
    case UIDeviceOrientationLandscapeRight:
      orientationInt = 8;
      break;
      
    case UIDeviceOrientationPortraitUpsideDown:
      orientationInt = 9;
      break;
      
    default:
      orientationInt = 1;
      
  }
  return orientationInt;
}


RCT_EXPORT_METHOD(getOrientation:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
    NSString *orientationStr = [self getOrientationStr:orientation];
    resolve(orientationStr);
}


RCT_EXPORT_METHOD(setOrientation:(int)requestedOrientation)
{
  NSLog(@"外部控制——— 设置方向 orientation: %d", requestedOrientation);
  
  if(_isRotating) return;
  
  if (requestedOrientation == 1) {
    _isRotating = YES;
    
    [RCTOrientationModule setOrientation:UIInterfaceOrientationMaskPortrait];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
      [RCTOrientationModule changeScreenOrientation:[NSNumber numberWithInt:UIInterfaceOrientationPortrait]];
      _isRotating = NO;
    }];
    
  }else if(requestedOrientation == 8){
    _isRotating = YES;
    
    [RCTOrientationModule setOrientation:UIInterfaceOrientationMaskLandscapeLeft];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
      [RCTOrientationModule changeScreenOrientation:[NSNumber numberWithInt:UIInterfaceOrientationLandscapeLeft]];
      _isRotating = NO;
    }];
    
  }else if(requestedOrientation == 0){
    _isRotating = YES;
    
    [RCTOrientationModule setOrientation:UIInterfaceOrientationMaskLandscapeRight];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
      [RCTOrientationModule changeScreenOrientation:[NSNumber numberWithInt:UIInterfaceOrientationLandscapeRight]];
      _isRotating = NO;
    }];
    
  }else if(requestedOrientation == 9){
    
    _isRotating = NO;
    
  }else if(requestedOrientation == -1){
    [RCTOrientationModule setOrientation:UIInterfaceOrientationMaskAllButUpsideDown];
    //  AppDelegate *delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    //  delegate.orientation = 3;
    _isRotating = NO;
  }
}

/*设置屏幕方向*/
+ (void)changeScreenOrientation:(NSNumber*) orientation
{
  if ([[UIDevice currentDevice] respondsToSelector:@selector(setOrientation:)]){
      
    [[UIDevice currentDevice] performSelector:@selector(setOrientation:)
                                   withObject:(id)orientation];
      
    [UIViewController attemptRotationToDeviceOrientation];
      
  }
  
  SEL selector=NSSelectorFromString(@"setOrientation:");
  NSInvocation *invocation =[NSInvocation invocationWithMethodSignature:[UIDevice instanceMethodSignatureForSelector:selector]];
  [invocation setSelector:selector];
  [invocation setTarget:[UIDevice currentDevice]];
  //  int val = _isFullScreen?UIInterfaceOrientationLandscapeRight:UIInterfaceOrientationPortrait;
  int val = [orientation intValue];
  [invocation setArgument:&val atIndex:2];
  [invocation invoke];
}



@end
