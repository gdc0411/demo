//
//  OrientationModule.m
//  DemoProject
//
//  Created by RaoJia on 2016/12/11.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "OrientationModule.h"


@implementation OrientationModule
@synthesize bridge = _bridge;

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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deviceOrientationDidChange:) name:@"UIDeviceOrientationDidChangeNotification" object:nil];
  }
  return self;
  
}

- (void)dealloc
{
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[@"onOrientationDidChange", @"specificOrientationDidChange", @"orientationDidChange" ];
}


- (void)deviceOrientationDidChange:(NSNotification *)notification
{
  UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
  
  [self sendEventWithName:@"onOrientationDidChange"
                     body:@{@"orientation": [self getSpecificOrientationStr:orientation]}];
  
  [self sendEventWithName:@"specificOrientationDidChange"
                     body:@{@"specificOrientation": [self getSpecificOrientationStr:orientation]}];
  
  [self sendEventWithName:@"orientationDidChange"
                     body:@{@"orientation": [self getOrientationStr:orientation]}];
  
}

- (NSString *)getOrientationStr: (UIDeviceOrientation)orientation {
  NSString *orientationStr;
  switch (orientation) {
    case UIDeviceOrientationPortrait:
      orientationStr = @"PORTRAIT";
      break;
    case UIDeviceOrientationLandscapeLeft:
    case UIDeviceOrientationLandscapeRight:
      
      orientationStr = @"LANDSCAPE";
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

- (NSString *)getSpecificOrientationStr: (UIDeviceOrientation)orientation {
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


RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(getOrientation:(RCTResponseSenderBlock)callback)
{
  UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
  NSString *orientationStr = [self getOrientationStr:orientation];
  callback(@[[NSNull null], orientationStr]);
}

RCT_EXPORT_METHOD(getSpecificOrientation:(RCTResponseSenderBlock)callback)
{
  UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
  NSString *orientationStr = [self getSpecificOrientationStr:orientation];
  callback(@[[NSNull null], orientationStr]);
}

RCT_EXPORT_METHOD(lockToPortrait)
{
#if DEBUG
  NSLog(@"Locked to Portrait");
#endif
  [OrientationModule setOrientation:UIInterfaceOrientationMaskPortrait];
  [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
    [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationPortrait] forKey:@"orientation"];
  }];
  
}

RCT_EXPORT_METHOD(lockToLandscape)
{
#if DEBUG
  NSLog(@"Locked to Landscape");
#endif
  UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
  NSString *orientationStr = [self getSpecificOrientationStr:orientation];
  if ([orientationStr isEqualToString:@"LANDSCAPE-LEFT"]) {
    [OrientationModule setOrientation:UIInterfaceOrientationMaskLandscape];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
      [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
    }];
  } else {
    [OrientationModule setOrientation:UIInterfaceOrientationMaskLandscape];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
      [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeLeft] forKey:@"orientation"];
    }];
  }
}

RCT_EXPORT_METHOD(lockToLandscapeRight)
{
#if DEBUG
  NSLog(@"Locked to Landscape Right");
#endif
  [OrientationModule setOrientation:UIInterfaceOrientationMaskLandscapeLeft];
  [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
    [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeLeft] forKey:@"orientation"];
  }];
  
}

RCT_EXPORT_METHOD(lockToLandscapeLeft)
{
#if DEBUG
  NSLog(@"Locked to Landscape Left");
#endif
  [OrientationModule setOrientation:UIInterfaceOrientationMaskLandscapeRight];
  [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
    // this seems counter intuitive
    [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
  }];
  
}

RCT_EXPORT_METHOD(unlockAllOrientations)
{
#if DEBUG
  NSLog(@"Unlock All Orientations");
#endif
  [OrientationModule setOrientation:UIInterfaceOrientationMaskAllButUpsideDown];
  //  AppDelegate *delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
  //  delegate.orientation = 3;
}

- (NSDictionary *)constantsToExport
{
  
  UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
  NSString *orientationStr = [self getOrientationStr:orientation];
  
  return @{@"initialOrientation": orientationStr};
}


//RCT_EXPORT_VIEW_PROPERTY(onOrientationDidChange, RCTDirectEventBlock);  // 设备方向变化事件


@end
